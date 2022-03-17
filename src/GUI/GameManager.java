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
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import gui.game.GameWindow;
import gui.game.component.sprite.ISprite;
import gui.game.component.sprite.Transition;
import gui.game.screen.IScene;
import gui.game.screen.level.Level;
import gui.game.screen.menu.MenuGameResults;
import image.BasicPainter2D;
import lslStream.LSL;
import lslStream.LSLStreamInfo;
import config.ConfigApp;
import config.ConfigParameter;
import config.Player;
import config.Settings;
import config.language.Language;
import control.ScreenControl;
import control.controller.ControllerActionChecker;
import control.controller.ControllerManager;
import control.controller.IControllerMetadata;
import control.controller.IInputController;
import control.events.IInputControllerListener;
import exceptions.ConfigParameterException;
import exceptions.SceneException;
import general.NumberRange;
import general.Tuple;
import statistic.RegistrarStatistic;
import stoppableThread.IStoppable;
import thread.stoppableThread.AbstractStoppableThread;

public class GameManager 
{
	private static GameManager manager;
	
	private GameWindow gameWindow = null;
	//private mouseTracking autoHideMenu = null;
	
	private Object sync = new Object();
	
	private Level currentLevel = null;
	
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
				GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				if( device.isFullScreenSupported() && false )
				{
					device.setFullScreenWindow( this.gameWindow );					
				}
				else
				{
					/*
					if (this.autoHideMenu != null)
					{
						this.autoHideMenu.stopThread( IStoppableThread.FORCE_STOP );
						this.autoHideMenu = null;
					}
					//*/
					
					this.gameWindow.setVisible(false);
					this.gameWindow.setUndecorated( true );
					
					this.gameWindow.toFront();
					//this.gameWindow.setExtendedState( JFrame.MAXIMIZED_BOTH );
					//this.gameWindow.setLocation( 0, 0);
					Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
					this.gameWindow.setBounds( new Rectangle( new Point(), size ) );
					this.gameWindow.setPreferredSize( size );
					
					/*
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
					*/
					
					/*
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
					*/
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

		if( this.currentLevel != null )
		{
			if( this.currentLevel.getBackgroundPattern() != null )
			{
				this.currentLevel.getBackgroundPattern().stopActing( IStoppable.FORCE_STOP );
			}
		}
		
		this.currentLevel = null;

		List< Player > players = new ArrayList<Player>( ConfigApp.getPlayers() );

		IControllerMetadata[] controllers = new IControllerMetadata[ players.size() ];

		
		Image backImg = null;
		
		for( int i = 0; i < players.size(); i++ ) 
		{
			Player player = players.get( i );

			Settings setting = ConfigApp.getPlayerSetting( player );
			ConfigParameter par = setting.getParameter( ConfigApp.SELECTED_CONTROLLER );
			
			Object path = setting.getParameter( ConfigApp.BACKGROUND_IMAGE ).getSelectedValue();
			if( backImg == null )
			{
				if( path != null )
				{
					try
					{
						backImg = ImageIO.read( new File( path.toString() ) );
					}
					catch (IOException ex)
					{	
					}
				}		
			}

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

		AbstractStoppableThread loadAnimationThread = null;
		try
		{
			this.gameWindow = new GameWindow( players );
			this.gameWindow.setIconImage( MainAppUI.getInstance().getIconImage() );
			this.gameWindow.setAlwaysOnTop( true );
			this.gameWindow.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

			this.fullScreen( true );		

			loadAnimationThread = this.showTransitionScreen( Language.getLocalCaption( Language.LOADING ), backImg );
			if( loadAnimationThread != null )
			{
				loadAnimationThread.startThread();
			}
			
			MainAppUI.getInstance().setVisible( false );

			this.gameWindow.setVisible( true );

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
				cmeta.setActionInputLevel( new NumberRange( max, Double.POSITIVE_INFINITY ));
				cmeta.setTargetTimeInLevelAction( timeTarget );
				cmeta.setRepetitions( rep );
				cmeta.setSelectedChannel( ch );

				RegistrarStatistic.addControllerSetting( player.getId(), cmeta );

				//this.gameWindow.setTargetInputValues( player, min, max);

				ctrs.add( cmeta );
			}

			ControllerManager.getInstance().startController( ctrs );

			for( IControllerMetadata cmeta : ctrs )
			{
				/*
				NumberRange rng = new NumberRange( cmeta.getRecoverInputLevel(), cmeta.getActionInputLevel().getMin() );
				ControllerActionChecker actCheck = new ControllerActionChecker( cmeta.getSelectedChannel()
																				, rng, cmeta.getTargetTimeInLevelAction( )
																				, cmeta.getRepetitions() );
				//*/
				ControllerActionChecker actCheck = new ControllerActionChecker( cmeta.getSelectedChannel()
																				, cmeta.getRecoverInputLevel()
																				, cmeta.getActionInputLevel().getMin()
																				, cmeta.getTargetTimeInLevelAction( )
																				, cmeta.getRepetitions() );
				actCheck.setOwner( cmeta.getPlayer() );				
				ControllerManager.getInstance().addControllerListener( cmeta.getPlayer(), actCheck );
				actCheck.addInputActionListerner( ScreenControl.getInstance() );
				actCheck.startActing();
			}

			//this.gameWindow.putControllerListener();

			ControllerManager.getInstance().setEnableControllerListener( false );

			ConfigParameter muteSession = ConfigApp.getGeneralSetting( ConfigApp.MUTE_SESSION );			

			boolean mute = false;

			if( muteSession != null && muteSession.get_type().equals( ConfigParameter.ParameterType.BOOLEAN ) )
			{
				mute = (Boolean)muteSession.getSelectedValue();
			}

			List< Settings > settings = new ArrayList<Settings>();
			for( Player player : ConfigApp.getPlayers() )
			{
				settings.add( ConfigApp.getPlayerSetting( player ) );
			}

			List< File > fileSongs = new ArrayList< File >();
			for( String song : this.getLevelSongs() )
			{			
				fileSongs.add( new File( song ) );				
			}
			
			this.currentLevel = this.getLevel( fileSongs, mute, settings );	
			
			if( loadAnimationThread != null )
			{
				loadAnimationThread.stopThread( IStoppable.FORCE_STOP );
			}
			
			this.setLevel( this.currentLevel );			
		}
		catch( Exception ex )
		{
			ex.printStackTrace();

			if( this.gameWindow != null )
			{
				this.gameWindow.dispose();
				this.gameWindow = null;
			}

			MainAppUI.getInstance().setVisible( true );

			throw ex;
		}
		finally 
		{
			if( loadAnimationThread != null )
			{
				loadAnimationThread.stopThread( IStoppable.FORCE_STOP );
			}

			if( this.gameWindow != null )
			{	
				this.gameWindow.setWindowsKeyStrokeAction();
				this.gameWindow.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
			}
		}		
	}

	private AbstractStoppableThread showTransitionScreen( String msg, Image backImg  )
	{
		AbstractStoppableThread loadAnimationThread = null;
		
		if( this.gameWindow != null )
		{
			final Transition loading = new Transition( this.gameWindow.getSceneBounds().getSize(), Level.LOADING_ID, backImg );
			loading.setMessage( msg );
	
			loadAnimationThread = new AbstractStoppableThread() 
			{			
				@Override
				protected void runInLoop() throws Exception 
				{
					synchronized ( this )
					{
						super.wait( 500L );
					}
	
					loading.updateSprite();
					
					setGameFrame( loading.getSprite() );
				}
	
				@Override
				protected void preStopThread(int friendliness) throws Exception {}
	
				@Override
				protected void postStopThread(int friendliness) throws Exception {}
	
				@Override
				protected void cleanUp() throws Exception 
				{
					setGameFrame( null );
				}
	
				@Override
				protected void runExceptionManager(Throwable e) 
				{
					if( !( e instanceof InterruptedException ) )
					{
						e.printStackTrace();
					}
				}
			};
		}

		return loadAnimationThread;
	}
	
	private synchronized List< String > getLevelSongs() throws ConfigParameterException
	{
		List< String > levelSongs = new ArrayList< String >();
		
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
			for( String s : songs )
			{
				levelSongs.add( s );
			}
		}
		
		return levelSongs;
	}

	private Level getLevel( List< File > fileSongs, boolean isMuteSession, List< Settings > settings  ) throws Exception
	{
		Level level = null;
		
		Rectangle screenBounds = this.gameWindow.getSceneBounds();

		//Level level = LevelMusicBuilder.getLevel( fileSong, screenBounds.getSize(), settings );		

		//LevelMusicBuilder.changeLevelSpeed( level );

		level = new Level( screenBounds, settings, fileSongs );
		level.setMuteSession( isMuteSession );
		
		return level;
	}
	
	private void setLevel( Level level ) throws Exception
	{	
		if( level == null )
		{
			throw new SceneException( "Level null" );
		}
		
		if( this.gameWindow != null )
		{				
			this.gameWindow.getGamePanel().removeAll(); 
			this.gameWindow.setTitle( MainAppUI.getInstance().getTitle() );
			
			ControllerManager.getInstance().setEnableControllerListener( true );
			
			ScreenControl.getInstance().setScene( level );
		
			ScreenControl.getInstance().startScene();
		}
	}
	
	public synchronized boolean hasNextLevel()
	{
		return !this.currentLevel.isFinished();
	}
	
	public void nextLevel() throws Exception
	{
		if( !this.currentLevel.isFinished() && this.gameWindow != null )
		{			
			ScreenControl.getInstance().stopScene();
			
			this.gameWindow.getGamePanel().setVisible( false );
			
			this.gameWindow.getGamePanel().removeAll();
									
			//this.currentLevel.checkLevelSpeed();
			this.setLevel( this.currentLevel );
						
			this.gameWindow.getGamePanel().setVisible( true );
			this.gameWindow.setVisible( true );
		}
		else
		{
			this.stopLevel( false );
		}
	}

	public synchronized void stopLevel( boolean nextLevel ) throws Exception
	{
		ControllerManager.getInstance().setEnableControllerListener( false );
		
		IScene sc = ScreenControl.getInstance().getScene();
		ScreenControl.getInstance().stopScene();
		
		List< ISprite > spr = null;
		
		if( sc != null )					
		{
			spr = sc.getSprites( IScene.BACKGROUND_ID, true );
		}
		
		BufferedImage back = null;
		if( spr != null && !spr.isEmpty() )
		{
			back = spr.get( 0 ).getSprite();
		}
				
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
				this.gameWindow.getGamePanel().setVisible( false );
				
				this.gameWindow.getGamePanel().removeAll();
				
				MenuGameResults mr = new MenuGameResults( this.gameWindow.getSceneBounds().getSize()
														, scores
														, spr
														, this.hasNextLevel(), true );
				//ScreenControl.getInstance().setScene( mr );
				
				this.gameWindow.getGamePanel().add( mr.getMenuFrame(), BorderLayout.CENTER );
				
				this.gameWindow.getGamePanel().setVisible( true );
			}
		}		
		else // if( !nextLevel )
		{
			if( this.currentLevel != null )
			{	
				this.currentLevel.stopActing( IStoppable.FORCE_STOP );			
				this.currentLevel = null;
			}
			
			ControllerManager.getInstance().stopController();
			
			this.gameWindow.getGamePanel().setVisible( false );
			
			this.gameWindow.getGamePanel().removeAll();
			
			AbstractStoppableThread transitionThread = this.showTransitionScreen( Language.getLocalCaption( Language.SAVING ), back );
			if( transitionThread != null )
			{
				transitionThread.startThread();
			}
			this.gameWindow.getGamePanel().setVisible( true );			
			this.gameWindow.setVisible( true );
			
			//
			try
			{			
				ConfigApp.dbSaveStatistic();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				System.out.println("GameManager.stopLevel() - CORREGIR ERROR: java.util.ConcurrentModificationException");
			}
			
			
			if( transitionThread != null )
			{
				transitionThread.stopThread( IStoppable.FORCE_STOP );
			}

			synchronized( this.sync )
			{
				if( this.gameWindow != null )
				{
					//this.gameWindow.setVisible( false );
					for( WindowListener wl : this.gameWindow.getWindowListeners() )
					{
						this.gameWindow.removeWindowListener( wl );
					}
					this.gameWindow.dispose();
					this.gameWindow = null;
					
					System.gc();
				}
			}
			
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
