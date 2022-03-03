/* 
 * Copyright 2019 by Manuel Merino Monge <manmermon@dte.us.es>
 *  
 *   This file is part of IRO.
 *
 *   IRO is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   LSLRec is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with LSLRec.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   Project's URL: https://github.com/manmermon/IRO
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import gui.game.GameWindow;
import gui.game.screen.level.Level;
import gui.game.screen.level.build.LevelMusicBuilder;
import gui.game.screen.menu.MenuGameResults;
import lslStream.LSL;
import lslStream.LSLStreamInfo;
import config.ConfigApp;
import config.ConfigParameter;
import config.Player;
import config.Settings;
import control.ScreenControl;
import control.controller.ControllerActionChecker;
import control.controller.ControllerManager;
import control.controller.IControllerMetadata;
import exceptions.ConfigParameterException;
import general.NumberRange;
import general.Tuple;
import statistic.RegistrarStatistic;
import stoppableThread.IStoppableThread;

public class GameManager 
{
	private static GameManager manager;
	
	private GameWindow gameWindow = null;
	private mouseTracking autoHideMenu = null;
	
	private Object sync = new Object();
	
	private LinkedList< String > leveSongs = new LinkedList<String>();
	
	private GameManager() 
	{ }
	
	public static GameManager getInstance()
	{
		if( manager == null )
		{
			manager = new GameManager();		
		}
		
		return manager;
	}
	
	/*
	public void setGameFrame( Frame fr )
	{
		synchronized ( this.sync )
		{
			if( this.gameWindow != null )
			{
				this.gameWindow.getGamePanel().setVisible( false );
				
				this.gameWindow.getGamePanel().removeAll();
				this.gameWindow.getGamePanel().add( fr );
				
				this.gameWindow.getGamePanel().setVisible( true );
			}
		}		
	}
	*/
	
	public void setGameFrame( BufferedImage fr)
	{
		synchronized ( this.sync )
		{
			if( this.gameWindow != null )
			{
				this.gameWindow.getGamePanel().setScene( fr );					
			}
		}		
	}
		
	private void fullScreen( boolean full )
	{		
		if( this.gameWindow != null )
		{
			if( full  )
			{
				if (this.autoHideMenu != null)
				{
					this.autoHideMenu.stopThread( IStoppableThread.FORCE_STOP );
					this.autoHideMenu = null;
				}
				
				this.gameWindow.setVisible(false);
				this.gameWindow.setUndecorated( true );
				
				this.gameWindow.toFront();
				
				
				this.gameWindow.addMouseMotionListener(new MouseAdapter()
				{

					public void mouseMoved(MouseEvent e)
					{
						JFrame jf = (JFrame)e.getSource();
						JMenuBar menuBar = jf.getJMenuBar();
						
						if (menuBar != null)
						{
							System.out.println("GameManager.fullScreen(...).new MouseAdapter() {...}.mouseMoved() " + e.getY());
							menuBar.setVisible(e.getY() < 15);
						}

					}
				});

				this.autoHideMenu = new mouseTracking( this.gameWindow );
	
				try
				{
					this.autoHideMenu.startThread();
				}
				catch (Exception e1)
				{
					JMenuBar menuBar = this.gameWindow.getJMenuBar();
					if (menuBar != null)
					{
						menuBar.setVisible(true);
					}

					this.autoHideMenu.stopThread( IStoppableThread.FORCE_STOP );
					this.autoHideMenu = null;
				}
			}
		}
	}
	
	public JFrame getCurrentWindow()
	{
		JFrame w = this.gameWindow;
		if( w == null )
		{
			w = MainAppUI.getInstance();
		}
		
		return w;
	}

	public void startGame( ) throws Exception 
	{
		synchronized ( this.sync )
		{
			if( this.gameWindow != null )
			{
				this.gameWindow.dispose();
				this.gameWindow = null;
			}
		}		
		
		RegistrarStatistic.startRegister();
		
		this.setLevelSongs();
				
		List< Player > players = new ArrayList<Player>( ConfigApp.getPlayers() );
		
		this.gameWindow = new GameWindow( players );
		this.gameWindow.setIconImage( MainAppUI.getInstance().getIconImage() );
		
		IControllerMetadata[] controllers = new IControllerMetadata[ players.size() ];
		
		for( int i = 0; i < players.size(); i++ ) 
		{
			Player player = players.get( i );
			
			Settings setting = ConfigApp.getPlayerSetting( player );
			ConfigParameter par = setting.getParameter( ConfigApp.SELECTED_CONTROLLER );
			
			Object ctr = par.getSelectedValue();

			if( ctr != null )
			{
				controllers[ i ] = (IControllerMetadata)ctr;				
			}
		}
		
		for( int i = 0; i < players.size(); i++ ) 
		{
			if( controllers[ i ] == null )
			{
				throw new ConfigParameterException( "Player " + players.get( i ).getName() 
													+ ": controller non found." );
			}
		}

		LSLStreamInfo[] streams = LSL.resolve_streams();
		LSLStreamInfo[] lslInfos = new LSLStreamInfo[ controllers.length ];
		for( int i = 0; i < controllers.length; i++ )
		{			
			IControllerMetadata meta = controllers[ i ];
			
			checkLSLInfo:
			for( LSLStreamInfo str : streams )
			{
				if( str.uid().equals( meta.getControllerID() ) )
				{
					lslInfos[ i ] = str;
					break checkLSLInfo;
				}
			}
		}

		for( int i = 0; i < lslInfos.length; i++ )
		{
			LSLStreamInfo info = lslInfos[ i ];
			
			if( info == null )
			{
				throw new IOException( "Player " + players.get( i ).getName() + ": controller not found." );
			}
		}

		try
		{
			List< IControllerMetadata > ctrs = new ArrayList<IControllerMetadata>();
			
			for( int i = 0; i < players.size(); i++ )
			{
				//
				// REACTION TIMES
				//
				
				Player player = players.get( i );
				
				Settings setting = ConfigApp.getPlayerSetting( player);
				
				//
				// CONTROLLER
				//
				
				IControllerMetadata cmeta = controllers[ i ];
				
				ConfigParameter par = setting.getParameter( ConfigApp.INPUT_MIN_VALUE );
				double min = ((Number)par.getSelectedValue()).doubleValue();
		
				par = setting.getParameter( ConfigApp.INPUT_MAX_VALUE);
				double max = ((Number)par.getSelectedValue()).doubleValue();
				
				par = setting.getParameter( ConfigApp.TIME_IN_INPUT_TARGET);
				double timeTarget = ((Number)par.getSelectedValue()).doubleValue();
				
				par = setting.getParameter( ConfigApp.INPUT_SELECTED_CHANNEL );
				int ch = ((Number)par.getSelectedValue()).intValue() - 1;
				
				if( ch < 0 || ch >= cmeta.getNumberOfChannels() )
				{
					throw new ConfigParameterException( "Selected channel out of controller's bounds [0, " + (cmeta.getNumberOfChannels() - 1 ) + "]."  );
				}

				par = setting.getParameter( ConfigApp.MOV_REPETITIONS );
				int rep = ((Number)par.getSelectedValue()).intValue();
				
				cmeta.setPlayer( player );
				cmeta.setRecoverInputLevel( min );
				cmeta.setTargetTimeInLevelAction( timeTarget );
				cmeta.setActionInputLevel( new NumberRange( max, Double.POSITIVE_INFINITY ));
				cmeta.setRepetitions( rep );
				
				RegistrarStatistic.addControllerSetting( player.getId(), cmeta );
				
				this.gameWindow.setTargetInputValues( player, min, max);
				
				ctrs.add( cmeta );
			}
			
			ControllerManager.getInstance().startController( ctrs );
			
			for( IControllerMetadata cmeta : ctrs )
			{
				NumberRange rng = new NumberRange( cmeta.getRecoverInputLevel(), cmeta.getActionInputLevel().getMin() );
				ControllerActionChecker actCheck = new ControllerActionChecker( cmeta.getSelectedChannel()
																				, rng, cmeta.getTargetTimeInLevelAction( )
																				, cmeta.getRepetitions() );
				actCheck.setOwner( cmeta.getPlayer() );				
				ControllerManager.getInstance().addControllerListener( cmeta.getPlayer(), actCheck );
				actCheck.addInputActionListerner( ScreenControl.getInstance() );
				actCheck.startThread();
			}
						
			this.gameWindow.putControllerListener();
			
			ControllerManager.getInstance().setEnableControllerListener( false );
			
			this.fullScreen( false );		
			
			File fileSong = new File( this.leveSongs.poll() );
			
			MainAppUI.getInstance().setVisible( false );

			ConfigParameter muteSession = ConfigApp.getGeneralSetting( ConfigApp.MUTE_SESSION );			
						
			boolean mute = false;
			
			if( muteSession != null && muteSession.get_type().equals( ConfigParameter.ParameterType.BOOLEAN ) )
			{
				mute = (Boolean)muteSession.getSelectedValue();
			}
			
			this.setLevel( fileSong, mute );			

			this.gameWindow.setVisible( true );			
		}
		catch( Exception ex )
		{
			if( this.gameWindow != null )
			{
				this.gameWindow.dispose();
				this.gameWindow = null;
			}
			
			MainAppUI.getInstance().setVisible( true );

			throw ex;
		}
		
	}

	private synchronized void setLevelSongs() throws ConfigParameterException
	{
		this.leveSongs.clear();
		
		Player firstPlayer = ConfigApp.getFirstPlayer();
		Settings setplayer = ConfigApp.getPlayerSetting( firstPlayer );
		ConfigParameter par = setplayer.getParameter( ConfigApp.SONG_LIST );

		Object songList = par.getSelectedValue();			
		if( songList == null )
		{
			throw new ConfigParameterException( "Non songs selected." );
		}

		String[] songs = songList.toString().split( ConfigApp.SONG_LIST_SEPARATOR );

		if( songs.length == 0 )
		{
			throw new ConfigParameterException( "Non songs selected." );
		}			
		else
		{
			this.leveSongs.clear();

			for( String s : songs )
			{
				this.leveSongs.add( s );
			}
		}
	}

	private void setLevel( File fileSong, boolean isMuteSession ) throws Exception
	{
		Rectangle screenBounds = this.gameWindow.getSceneBounds();
		
		List< Settings > settings = new ArrayList<Settings>();
		for( Player player : ConfigApp.getPlayers() )
		{
			settings.add( ConfigApp.getPlayerSetting( player ) );
		}
		
		Level level = LevelMusicBuilder.getLevel( fileSong, screenBounds.getSize(), settings );
	
		//LevelMusicBuilder.changeLevelSpeed( level );
		
		level.setMuteSession( isMuteSession );
	
		this.gameWindow.setTitle( MainAppUI.getInstance().getTitle() + ": " + fileSong.getName() );
		
		ControllerManager.getInstance().setEnableControllerListener( true );
		
		ScreenControl.getInstance().setScene( level );
	
		ScreenControl.getInstance().startScene();
	}
	
	public synchronized boolean hasNextLevel()
	{
		return !this.leveSongs.isEmpty();
	}
	
	public void nextLevel() throws Exception
	{
		if( !this.leveSongs.isEmpty() && this.gameWindow != null )
		{
			ScreenControl.getInstance().stopScene();
			
			this.gameWindow.getGamePanel().setVisible( false );
			
			this.gameWindow.getGamePanel().removeAll();
			
			ConfigParameter muteSession = ConfigApp.getGeneralSetting( ConfigApp.MUTE_SESSION );			
			
			boolean mute = false;
			
			if( muteSession != null && muteSession.get_type().equals( ConfigParameter.ParameterType.BOOLEAN ) )
			{
				mute = (Boolean)muteSession.getSelectedValue();
			}
						
			this.setLevel( new File( this.leveSongs.poll() ), mute );
			
			this.gameWindow.getGamePanel().setVisible( true );
		}
		else
		{
			this.stopLevel( false );
		}
	}
	
	
	public synchronized void stopLevel( boolean nextLevel ) throws Exception
	{
		ScreenControl.getInstance().stopScene();
		//ControllerManager.getInstance().stopController();
		ControllerManager.getInstance().setEnableControllerListener( false );
		
		if( nextLevel )
		{
			List< Tuple< Player, Double > > scores = new ArrayList< Tuple< Player, Double > >();
			
			for( Player p : ConfigApp.getPlayers() )
			{
				scores.add( new Tuple< Player, Double>( p, RegistrarStatistic.getPlayerScore( p.getId() ) ) );
			}
			
			Collections.sort( scores, new Comparator< Tuple< Player, Double > >() 
										{							
											@Override
											public int compare( Tuple< Player, Double > t0, Tuple< Player, Double > t1 ) 
											{
												int eq = (int)( Math.signum( t0.t2 - t1.t2 ) );
												return eq;
											}
										});
			
			this.gameWindow.getGamePanel().setVisible( false );
			
			this.gameWindow.getGamePanel().removeAll();
			
			ConfigParameter contSession = ConfigApp.getGeneralSetting( ConfigApp.CONTINUOUS_SESSION );			
			
			boolean continuous = false;
			
			if( contSession != null && contSession.get_type().equals( ConfigParameter.ParameterType.BOOLEAN ) )
			{
				continuous = (Boolean)contSession.getSelectedValue();
			}
			
			if( continuous && this.hasNextLevel() )
			{
				this.nextLevel();
			}			
			else
			{
				MenuGameResults mr = new MenuGameResults( this.gameWindow.getSceneBounds().getSize()
														//, this.gameWindow.getSceneBounds()
														, scores, this.hasNextLevel(), true );
				//ScreenControl.getInstance().setScene( mr );
				
				this.gameWindow.getGamePanel().add( mr.getMenuFrame(), BorderLayout.CENTER );
				
				this.gameWindow.getGamePanel().setVisible( true );
			}
		}
		
		if( !nextLevel )
		{
			ControllerManager.getInstance().stopController();
			
			synchronized( this.sync )
			{
				if( this.gameWindow != null )
				{
					this.gameWindow.setVisible( false );
					this.gameWindow = null;
					
					System.gc();
				}
			}		
			
			//
			System.out.println("GameManager.stopLevel() - CORREGIR ERROR: java.util.ConcurrentModificationException");
			ConfigApp.dbSaveStatistic();
			
			MainAppUI.getInstance().setVisible( true );
		}
	}
	
	public void togglePause()
	{
		if( this.gameWindow != null )
		{
			boolean pause = !ScreenControl.getInstance().isPausedScene();
			
			ScreenControl.getInstance().setPauseScene( pause );
			ControllerManager.getInstance().setEnableControllerListener( !pause );
		}
	}
}
