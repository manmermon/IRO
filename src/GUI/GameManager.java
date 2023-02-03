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
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
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
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import gui.game.GameWindow;
import gui.game.component.sprite.ISprite;
import gui.game.component.sprite.Transition;
import gui.game.screen.IScene;
import gui.game.screen.level.Level;
import gui.game.screen.menu.MenuGameResults;
import gui.panel.statusSurvey.PlayerStatusSurvey;
import gui.panel.statusSurvey.PlayerStatusSurvey.StatusSurvey;
import lslInput.LSL;
import lslInput.LSLStreamInfo;
import lslInput.stream.IInputStreamMetadata;
import lslInput.stream.controller.IControllerMetadata;
import config.ConfigApp;
import config.ConfigParameter;
import config.DataBaseSettings;
import config.Player;
import config.Settings;
import config.language.Language;
import control.ScreenControl;
import control.inputStream.biosignal.InputBiosignalStreamManager;
import control.inputStream.biosignal.LSLInputBiosignalStreamReader;
import control.inputStream.controller.ControllerActionChecker;
import control.inputStream.controller.ControllerManager;
import exceptions.ConfigParameterException;
import exceptions.SceneException;
import general.ArrayTreeMap;
import general.Tuple;
import statistic.RegistrarStatistic;
import statistic.RegistrarStatistic.GameFieldType;
import stoppableThread.IStoppable;
import testing.experiments.synMarker.SyncMarker;
import testing.experiments.synMarker.SyncMarker.Marker;
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
			
			this.currentLevel.stopActing( IStoppable.FORCE_STOP );
		}
		
		this.currentLevel = null;
		
		List< Player > players = new ArrayList<Player>( ConfigApp.getPlayers() );
		
		IControllerMetadata[] controllers = new IControllerMetadata[ players.size() ];
		ArrayTreeMap< Integer, IInputStreamMetadata > biosignals = new ArrayTreeMap< Integer, IInputStreamMetadata >(); 		
		
		Image backImg = null;
		
		for( int i = 0; i < players.size(); i++ ) 
		{
			Player player = players.get( i );

			Settings setting = ConfigApp.getPlayerSetting( player );
			
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

			ConfigParameter par = setting.getParameter( ConfigApp.SELECTED_CONTROLLER );
			Object ctr = par.getSelectedValue();

			if( ctr != null )
			{
				controllers[ i ] = (IControllerMetadata)ctr;				
			}
			
			par = setting.getParameter( ConfigApp.SELECTED_BIOSIGNAL );
			Object bios = par.getSelectedValue();
			if( bios != null )
			{				
				List< IInputStreamMetadata > biosgs = (List< IInputStreamMetadata >)bios;
				
				for( IInputStreamMetadata bs : biosgs )
				{
					bs.setPlayer( player );
				}
				
				biosignals.put( player.getId(), biosgs );
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

		//this.checkController( controllers );
		//TODO

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

			SyncMarker.getInstance( ConfigApp.shortNameApp ).sendMarker( Marker.START_TEST );
			
			//
			//
			//			
			ConfigParameter statusSurvey = ConfigApp.getGeneralSetting( ConfigApp.STATUS_SURVEY_TEST );
			
			boolean showStatusSurvey = true;
			
			if( statusSurvey != null && statusSurvey.get_type().equals( ConfigParameter.ParameterType.BOOLEAN ) )
			{
				showStatusSurvey = (Boolean)statusSurvey.getSelectedValue();
			}
			
			if( showStatusSurvey )
			{
				this.showStatusSurveyDialog( players );
			}
			//
			//
			//

			//
			// Por si desconecta durante el test SAM
			//
			this.checkInputStream( controllers );
			for( Integer playerID : biosignals.keySet() )
			{
				List< IInputStreamMetadata > bs = biosignals.get( playerID );
				this.checkInputStream( bs.toArray( new IInputStreamMetadata[0] ) );
			}
						
			this.gameWindow.setVisible( true );

			List< IControllerMetadata > ctrs = new ArrayList< IControllerMetadata >();

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
				//cmeta.setActionInputLevel( new NumberRange( max, Double.POSITIVE_INFINITY ));
				cmeta.setActionInputLevel( max );
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
				ControllerActionChecker actCheck = new ControllerActionChecker( cmeta.getSelectedChannel()
																				, cmeta.getRecoverInputLevel()
																				, cmeta.getActionInputLevel()
																				, cmeta.getTargetTimeInLevelAction( )
																				, cmeta.getRepetitions() );
				//*/
				ControllerActionChecker actCheck = new ControllerActionChecker( cmeta );
				actCheck.setOwner( cmeta.getPlayer() );				
				actCheck.setEnableInputStream( false );
				ControllerManager.getInstance().addControllerListener( cmeta.getPlayer(), actCheck );
				actCheck.addInputActionListerner( ScreenControl.getInstance() );
				actCheck.startActing();
			}
			
			//ControllerManager.getInstance().setEnableControllerListener( false );
			
			for( Integer playerID : biosignals.keySet() )
			{				
				List< IInputStreamMetadata > biometa = biosignals.get( playerID );
				InputBiosignalStreamManager.getInstance().startInputBioStream( biometa );
			}
			
			for( Integer playerID : biosignals.keySet() )
			{				
				List< IInputStreamMetadata > biometa = biosignals.get( playerID );
				
				for( IInputStreamMetadata meta : biometa )
				{
					RegistrarStatistic.addBiosignalStreamSetting( playerID, meta );
					
					LSLInputBiosignalStreamReader bioReader = new LSLInputBiosignalStreamReader( meta );
					bioReader.setOwner( meta.getPlayer() );
					bioReader.setEnableInputStream( false );
					InputBiosignalStreamManager.getInstance().addInputBiosignalStreamListener( meta.getPlayer(), bioReader );
					bioReader.startActing();
				}
			}

			//this.gameWindow.putControllerListener();

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
			
			
			ConfigParameter sessionTime = ConfigApp.getGeneralSetting( ConfigApp.LIMIT_SESSION_TIME );
			double sstime = 0;
			
			if( sessionTime != null )
			{
				sstime = ((Number)sessionTime.getSelectedValue()).doubleValue();
			}
			
			this.currentLevel = this.getLevel( fileSongs, mute, settings, sstime );	
			
			if( loadAnimationThread != null )
			{
				loadAnimationThread.stopThread( IStoppable.FORCE_STOP );
			}
						
			RegistrarStatistic.addGameData( ConfigApp.getPlayers(), GameFieldType.GAME_START );
			
			this.setLevel( this.currentLevel );			
		}
		catch( Exception ex )
		{
			ex.printStackTrace();

			synchronized ( sync )
			{
				if( this.gameWindow != null )
				{
					this.gameWindow.dispose();
					this.gameWindow = null;
				}
			}			
			
			RegistrarStatistic.clearRegister();

			MainAppUI.getInstance().setVisible( true );

			throw ex;
		}
		finally 
		{
			if( loadAnimationThread != null )
			{
				loadAnimationThread.stopThread( IStoppable.FORCE_STOP );
			}

			synchronized ( sync )
			{
				if( this.gameWindow != null )
				{	
					this.gameWindow.setWindowsKeyStrokeAction();
					this.gameWindow.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				}
			}
		}		
	}
	
	/*
	private void checkController( List< Player > players, IControllerMetadata[] controllers ) throws IOException
	{
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
	}
	//*/
	
	private void checkInputStream( IInputStreamMetadata[] controllers ) throws IOException
	{
		LSLStreamInfo[] streams = LSL.resolve_streams();
		LSLStreamInfo[] lslInfos = new LSLStreamInfo[ controllers.length ];
		for( int i = 0; i < controllers.length; i++ )
		{			
			IInputStreamMetadata meta = controllers[ i ];

			if( meta != null )
			{
				checkLSLInfo:
					for( LSLStreamInfo str : streams )
					{
						if( str.uid().equals( meta.getInputSourceID() ) )
						{
							lslInfos[ i ] = str;
							break checkLSLInfo;
						}
					}
			}
		}

		for( int i = 0; i < lslInfos.length; i++ )
		{
			LSLStreamInfo info = lslInfos[ i ];

			if( info == null )
			{	
				String pname = "" + i;
				
				IInputStreamMetadata meta = controllers[ i ];
								
				if( meta != null )
				{
					pname = meta.getPlayer().getName();
				}
				throw new IOException( "Player " + pname + ": controller not found." );
			}
		}
	}

	private void showStatusSurveyDialog( List< Player > players )
	{
		if( players != null )
		{
			JDialog statusSurveyDialog = new JDialog( MainAppUI.getInstance() );
			statusSurveyDialog.setVisible( false );
	
			statusSurveyDialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
			statusSurveyDialog.setModal( true );
	
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
	
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Insets pads = Toolkit.getDefaultToolkit().getScreenInsets( gc );
			screenSize.width += -( pads.left + pads.right );
			screenSize.height += -( pads.top + pads.bottom );
	
			statusSurveyDialog.setLocation( pads.left, pads.top );
			statusSurveyDialog.setSize( screenSize );
			JPanel container = new JPanel( new BorderLayout() );
			statusSurveyDialog.setContentPane( container );
	
			SyncMarker.getInstance( ConfigApp.shortNameApp ).sendMarker( Marker.SAM_TEST );
			
			for( Player p : players )
			{
				if( !p.isAnonymous() )
				{
					statusSurveyDialog.setVisible( false );
		
					PlayerStatusSurvey sae = new PlayerStatusSurvey( p, screenSize, new StatusSurvey[] { StatusSurvey.VALENCE, StatusSurvey.AROUSAL, StatusSurvey.PHYSICAL_EFFORT }, statusSurveyDialog );
	
					container.removeAll();
					container.add( sae, BorderLayout.CENTER );
	
					statusSurveyDialog.setVisible( true );
					
					String playerState = sae.getPlayerState();
					RegistrarStatistic.addValenceArousalEffortData( p.getId(), playerState );
				}
			}
	
			statusSurveyDialog.dispose();
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
		if( songList != null )
		{
			String[] songs = songList.toString().split( ConfigApp.SONG_LIST_SEPARATOR );
					
			if( songs.length > 0  )
			{
				for( String s : songs )
				{
					if( !s.trim().isEmpty() )
					{
						levelSongs.add( s );
					}
				}
				
			}
		}
		
		if( levelSongs.isEmpty() )
		{
			throw new ConfigParameterException( "Non songs selected." );
		}
		
		return levelSongs;
	}

	private Level getLevel( List< File > fileSongs, boolean isMuteSession, List< Settings > settings, double sessionTime  ) throws Exception
	{
		Level level = null;
		
		Rectangle screenBounds = this.gameWindow.getSceneBounds();

		//Level level = LevelMusicBuilder.getLevel( fileSong, screenBounds.getSize(), settings );		

		//LevelMusicBuilder.changeLevelSpeed( level );

		level = new Level( screenBounds, settings, fileSongs, sessionTime );
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
			SyncMarker.getInstance( ConfigApp.shortNameApp ).sendMarker( Marker.START_MUSIC );
			
			this.gameWindow.getGamePanel().removeAll(); 
			this.gameWindow.setTitle( MainAppUI.getInstance().getTitle() );
			
			ControllerManager.getInstance().setEnableControllerListener( true );
			InputBiosignalStreamManager.getInstance().setEnableInputBiosignalStreamListener( true );
			
			ScreenControl.getInstance().setScene( level );
			
			ScreenControl.getInstance().startScene();
		}
	}
	
	public synchronized boolean hasNextLevel()
	{
		boolean next = false;
		
		if( this.currentLevel != null )
		{
			next = !this.currentLevel.isFinished();
		}
		
		return next;
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
		InputBiosignalStreamManager.getInstance().setEnableInputBiosignalStreamListener( false );
		
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
												int eq = (int)( Math.signum( t1.t2 - t0.t2 ) );
												
												if( eq == 0 )
												{
													eq = t1.t1.getId() - t0.t1.getId();
												}
												
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
				if( this.gameWindow != null )
				{
					SyncMarker.getInstance( ConfigApp.shortNameApp ).sendMarker( Marker.SCORE_SCREEN );
					
					this.gameWindow.getGamePanel().setVisible( false );
					
					this.gameWindow.getGamePanel().removeAll();
				
					
					MenuGameResults mr = new MenuGameResults( this.gameWindow.getSceneBounds().getSize()
															, scores
															, spr
															, this.hasNextLevel()
															, true );
					//ScreenControl.getInstance().setScene( mr );
					
					this.gameWindow.getGamePanel().add( mr.getMenuFrame(), BorderLayout.CENTER );
					
					this.gameWindow.getGamePanel().setVisible( true );
				}
			}
		}		
		else // if( !nextLevel )
		{
			if( this.currentLevel != null )
			{	
				this.currentLevel.stopActing( IStoppable.FORCE_STOP );			
				this.currentLevel = null;
			}
		
			RegistrarStatistic.addGameData( ConfigApp.getPlayers(), GameFieldType.GAME_END );		
			
			ControllerManager.getInstance().stopController();
			
			AbstractStoppableThread transitionThread = null;
			
			synchronized ( sync )
			{
				if( this.gameWindow != null )
				{
					this.gameWindow.getGamePanel().setVisible( false );
					
					this.gameWindow.getGamePanel().removeAll();
					
					transitionThread = this.showTransitionScreen( Language.getLocalCaption( Language.SAVING ), back );
					if( transitionThread != null )
					{
						transitionThread.startThread();
					}
					this.gameWindow.getGamePanel().setVisible( true );			
		
					//
					//
					//
					this.gameWindow.setVisible( false );
				}
			}
			ConfigParameter samTest = ConfigApp.getGeneralSetting( ConfigApp.STATUS_SURVEY_TEST );
			
			boolean showSamTest = true;
			if( samTest != null && samTest.get_type().equals( ConfigParameter.ParameterType.BOOLEAN ) )
			{
				showSamTest = (Boolean)samTest.getSelectedValue();
			}
			
			if( showSamTest )
			{				
				this.showStatusSurveyDialog( new ArrayList< Player >( ConfigApp.getPlayers() ) );
			}
			
			SyncMarker.getInstance( ConfigApp.shortNameApp ).sendMarker( Marker.STOP_TEST );
			
			//
			//
			//

			synchronized( this.sync )
			{
				if( this.gameWindow != null )
				{
					this.gameWindow.setAlwaysOnTop( false );
					this.gameWindow.setVisible( true );
				}
			}
						
			//
			try
			{			
				DataBaseSettings.dbSaveStatistic();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				//System.out.println("GameManager.stopLevel() - CORREGIR ERROR: java.util.ConcurrentModificationException");
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
			InputBiosignalStreamManager.getInstance().setEnableInputBiosignalStreamListener( !pause );
		}
	}
}
