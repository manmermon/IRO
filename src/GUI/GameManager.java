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

package GUI;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import GUI.game.GameWindow;
import GUI.game.component.Frame;
import GUI.game.screen.level.Level;
import GUI.game.screen.level.LevelFactory;
import config.ConfigApp;
import config.ConfigParameter;
import config.Player;
import config.Settings;
import control.ScreenControl;
import control.controller.ControllerActionChecker;
import control.controller.ControllerManager;
import control.controller.ControllerMetadata;
import edu.ucsd.sccn.LSL;
import exceptions.ConfigParameterException;
import general.NumberRange;
import statistic.GameStatistic;
import stoppableThread.IStoppableThread;

public class GameManager 
{
	private static GameManager manager;
	
	private GameWindow gameWindow = null;
	private mouseTracking autoHideMenu = null;
	
	private Object sync = new Object();
	
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
		
	public void setGameFrame( Frame fr )
	{
		synchronized ( this.sync )
		{
			if( this.gameWindow != null )
			{
				this.gameWindow.getGamePanel().setVisible( false );
				
				this.gameWindow.getGamePanel().add( fr );
				
				this.gameWindow.getGamePanel().setVisible( true );
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

	public void playLevel() throws Exception 
	{
		synchronized ( this.sync )
		{
			if( this.gameWindow != null )
			{
				this.gameWindow.dispose();
				this.gameWindow = null;
			}
		}		
		
		List< Player > players = new ArrayList<Player>( ConfigApp.getPlayers() );
		
		this.gameWindow = new GameWindow( players );
		this.gameWindow.setIconImage( MainAppUI.getInstance().getIconImage() );
		
		ControllerMetadata[] controllers = new ControllerMetadata[ players.size() ];
		
		for( int i = 0; i < players.size(); i++ ) 
		{
			Player player = players.get( i );
			
			Settings setting = ConfigApp.getPlayerSetting( player );
			ConfigParameter par = setting.getParameter( ConfigApp.SELECTED_CONTROLLER );
			Object ctr = par.getSelectedValue();

			if( ctr != null )
			{
				controllers[ i ] = (ControllerMetadata)ctr;				
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

		LSL.StreamInfo[] streams = LSL.resolve_streams();
		LSL.StreamInfo[] lslInfos = new LSL.StreamInfo[ controllers.length ];
		for( int i = 0; i < controllers.length; i++ )
		{			
			ControllerMetadata meta = controllers[ i ];
			
			checkLSLInfo:
			for( LSL.StreamInfo str : streams )
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
			LSL.StreamInfo info = lslInfos[ i ];
			
			if( info == null )
			{
				throw new IOException( "Player " + players.get( i ).getName() + ": controller not found." );
			}
		}

		try
		{
			List< ControllerMetadata > ctrs = new ArrayList<ControllerMetadata>();
			
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
				
				ControllerMetadata cmeta = controllers[ i ];
				
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

				cmeta.setPlayer( player );
				cmeta.setRecoverInputLevel( min );
				cmeta.setTargetTimeInLevelAction( timeTarget );
				cmeta.setActionInputLevel( new NumberRange( max, Double.POSITIVE_INFINITY ));
				
				this.gameWindow.setTargetInputValues( player, min, max);
				
				ctrs.add( cmeta );
			}
			
			ControllerManager.getInstance().startController( ctrs );
			for( ControllerMetadata cmeta : ctrs )
			{
				NumberRange rng = new NumberRange( cmeta.getRecoverInputLevel(), cmeta.getActionInputLevel().getMin() );
				ControllerActionChecker actCheck = new ControllerActionChecker( cmeta.getSelectedChannel()
																				, rng, cmeta.getTargetTimeInLevelAction( ) );
				ControllerManager.getInstance().addControllerListener( cmeta.getPlayer(), actCheck );
				actCheck.addInputActionListerner( ScreenControl.getInstance() );
			}
			
			this.gameWindow.putControllerListener();
			
			
			
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
	
			this.fullScreen( false );		
	
			Rectangle screenBounds = this.gameWindow.getSceneBounds();		
			
			File fileSong = new File( songs[ 0 ] );
			
			this.gameWindow.setTitle( MainAppUI.getInstance().getTitle() + ": " + fileSong.getName() );
			
			List< Settings > settings = new ArrayList<Settings>();
			for( Player player : ConfigApp.getPlayers() )
			{
				settings.add( ConfigApp.getPlayerSetting( player ) );
			}
			
			Level level = LevelFactory.getLevel( fileSong, screenBounds, settings );
	
			ScreenControl.getInstance().setScene( level );
	
			MainAppUI.getInstance().setVisible( false );
			
			GameStatistic.startRegister();
			
			this.gameWindow.setVisible( true );
			
			ScreenControl.getInstance().startScene();
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

	public synchronized void stopLevel( ) throws Exception
	{
		ScreenControl.getInstance().stopScene();
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
		
		ConfigApp.dbSaveStatistic();
		
		MainAppUI.getInstance().setVisible( true );
	}
}
