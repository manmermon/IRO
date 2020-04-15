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
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import GUI.game.GameWindow;
import GUI.game.component.Frame;
import GUI.game.screen.level.Level;
import GUI.game.screen.level.LevelFactory;
import config.ConfigApp;
import config.ConfigParameter;
import config.Player;
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
	
	public void updateSetting()
	{
		AppUI.getInstance().loadSetting();
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

		ConfigParameter par = ConfigApp.getParameter( ConfigApp.SELECTED_CONTROLLER );
		Object ctr = par.getSelectedValue();

		if( ctr == null )
		{
			throw new ConfigParameterException( "Non controller selected." );
		}

		ControllerMetadata meta = (ControllerMetadata)ctr;

		LSL.StreamInfo[] streams = LSL.resolve_streams();

		LSL.StreamInfo info = null;
		for( LSL.StreamInfo str : streams )
		{
			if( str.uid().equals( meta.getControllerID() ) )
			{
				info = str;
				break;
			}
		}

		if( info == null )
		{
			throw new IOException( "Controller not found." );
		}

		try
		{
			par = ConfigApp.getParameter( ConfigApp.PLAYER );
			Object player = par.getSelectedValue();
			int playerID = Player.ANONYMOUS_USER_ID;
			if( player != null )
			{
				playerID = ((Player)player).getId();
			}
					
			GameStatistic.setPlayerID( playerID );
			
			this.gameWindow = new GameWindow();
			this.gameWindow.setIconImage( AppUI.getInstance().getIconImage() );
				
			par = ConfigApp.getParameter( ConfigApp.INPUT_MIN_VALUE );
			double min = ((Number)par.getSelectedValue()).doubleValue();
	
			par = ConfigApp.getParameter( ConfigApp.INPUT_MAX_VALUE);
			double max = ((Number)par.getSelectedValue()).doubleValue();
			
			par = ConfigApp.getParameter( ConfigApp.TIME_IN_INPUT_TARGET);
			double timeTarget = ((Number)par.getSelectedValue()).doubleValue();
			
			par = ConfigApp.getParameter( ConfigApp.REACTION_TIME );
			double actionTime = ((Number)par.getSelectedValue()).doubleValue();
	
			par = ConfigApp.getParameter( ConfigApp.RECOVER_TIME );
			double recoverTime = ((Number)par.getSelectedValue()).doubleValue();
			
			par = ConfigApp.getParameter( ConfigApp.INPUT_SELECTED_CHANNEL );
			Object channel = par.getSelectedValue();
			
			if(  channel == null )
			{
				throw new ConfigParameterException( "Non input channel selected." );
			}
			
			int ch = ((Number)channel).intValue() - 1;
			
			if( ch < 0 || ch >= info.channel_count() )
			{
				throw new ConfigParameterException( "Selected channel out of controller's bounds [0, " + (info.channel_count() - 1 ) + "]."  );
			}
			
			par = ConfigApp.getParameter( ConfigApp.SONG_LIST );
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
			
			this.gameWindow.setTargetInputValues(min, max);
	
			this.fullScreen( false );		
	
			Rectangle screenBounds = this.gameWindow.getSceneBounds();		
			
			File fileSong = new File( songs[ 0 ] );
			
			this.gameWindow.setTitle( AppUI.getInstance().getTitle() + ": " + fileSong.getName() );
			
			Level level = LevelFactory.getLevel( fileSong, screenBounds, actionTime, recoverTime );
	
			ScreenControl.getInstance().setScene( level );
	
			ControllerManager.getInstance().startController( info );
	
			ControllerActionChecker actCheck = new ControllerActionChecker( ch, new NumberRange( min, max ), timeTarget );
			ControllerManager.getInstance().addControllerListener( actCheck );
			actCheck.addInputActionListerner( ScreenControl.getInstance() );
			
			this.gameWindow.putControllerListener();
			
			AppUI.getInstance().setVisible( false );
			
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
			
			AppUI.getInstance().setVisible( true );

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
		
		ConfigApp.saveStatistic();
		
		AppUI.getInstance().setVisible( true );
	}
}
