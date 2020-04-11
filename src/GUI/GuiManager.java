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

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import GUI.game.GameWindow;
import GUI.game.component.Frame;
import GUI.game.screen.level.Level;
import GUI.game.screen.level.LevelFactory;
import config.ConfigApp;
import config.ConfigParameter;
import control.LaunchControl;
import control.inputs.LSLStreams.LSLController;
import edu.ucsd.sccn.LSL;
import exceptions.IllegalLevelStateException;
import stoppableThread.IStoppableThread;

public class GuiManager 
{
	private static GuiManager manager;
	
	private GameWindow gameWindow = null;
	private JFrame gameWindowFullScreen = null;
	private mouseTracking autoHideMenu = null;

	private Point prevGUILocation = null;
		
	private GuiManager() 
	{ }
	
	public static GuiManager getInstance()
	{
		if( manager == null )
		{
			manager = new GuiManager();
		}
		
		return manager;
	}
	
	public void updateSetting()
	{
		AppUI.getInstance().loadSetting();
	}
	
	public void setGameFrame( Frame fr )
	{
		if( this.gameWindow != null )
		{
			this.gameWindow.getGamePanel().setVisible( false );
			
			this.gameWindow.getGamePanel().add( fr );
			
			this.gameWindow.getGamePanel().setVisible( true );
		}
	}
		
	public void fullScreen( boolean full )
	{		
		if( this.gameWindow != null )
		{
			if( !full  )
			{
				if (this.gameWindowFullScreen != null)
				{
					if (this.autoHideMenu != null)
					{
						this.autoHideMenu.stopThread( IStoppableThread.FORCE_STOP );
						this.autoHideMenu = null;
					}
		
					this.gameWindowFullScreen.setVisible(false);			
					
					this.gameWindow.setVisible(false);
					this.gameWindow.setContentPane( this.gameWindowFullScreen.getContentPane() );
					//ui.setJMenuBar( this.appGUIFullScreen.getJMenuBar() );
		
					this.gameWindowFullScreen.dispose();
					this.gameWindowFullScreen = null;
		
					//ui.getJMenuBar().setVisible(true);
					this.gameWindow.setVisible(true);
				}
				else if (this.prevGUILocation != null)
				{
					this.gameWindow.setLocation( this.prevGUILocation );
				}
			}
			else
			{
					this.gameWindow.setVisible(false);
	
					this.gameWindowFullScreen = new JFrame();
					this.gameWindowFullScreen.setUndecorated(true);
					
					this.gameWindowFullScreen.setContentPane( this.gameWindow.getContentPane());
					this.gameWindowFullScreen.setResizable(false);
					this.gameWindowFullScreen.setAlwaysOnTop(true);
					this.gameWindowFullScreen.setLocation(0, 0);
					this.gameWindowFullScreen.setIconImage( this.gameWindow.getIconImage());
					this.gameWindowFullScreen.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	
					this.gameWindowFullScreen.addFocusListener(new FocusAdapter()
					{
	
						public void focusGained(FocusEvent e)
						{
							JFrame jf = (JFrame)e.getSource();
							jf.toFront();
						}
					});
	
					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice[] gs = ge.getScreenDevices();
					GraphicsDevice gd = null;
					
					if (gs.length == 0)
					{
						throw new RuntimeException("No screens found.");
					}
					else
					{
						gd = gs[ 0 ];
					}
					
					if (gd.isFullScreenSupported())
					{
						gd.setFullScreenWindow(this.gameWindowFullScreen);
					}
					else
					{
						Rectangle bounds = gd.getDefaultConfiguration().getBounds();
	
						this.gameWindowFullScreen.setSize(bounds.width, bounds.height);
					}
	
					this.gameWindowFullScreen.setVisible(true);
					this.gameWindowFullScreen.toFront();
	
					this.gameWindowFullScreen.addMouseMotionListener(new MouseAdapter()
					{
	
						public void mouseMoved(MouseEvent e)
						{
							JFrame jf = (JFrame)e.getSource();
							JMenuBar menuBar = jf.getJMenuBar();
	
							if (menuBar != null)
							{
								menuBar.setVisible(e.getY() < 15);
							}
	
						}
					});
	
					this.autoHideMenu = new mouseTracking(this.gameWindowFullScreen);
	
					try
					{
						this.autoHideMenu.startThread();
					}
					catch (Exception e1)
					{
						JMenuBar menuBar = this.gameWindowFullScreen.getJMenuBar();
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

	/*
	public void mainMenu()
	{
		GuiManager.getInstance().removeCurrentFrame();
		
		GuiManager.getInstance().fullScreen( false );		
		
		Dimension screenSize = GuiManager.getInstance().getScreenSize();
		
		MainMenuScreen mainMenu = new MainMenuScreen( screenSize );
		
		try 
		{	
			LaunchControl.getInstance().launchScene( mainMenu );
		} 
		catch (IllegalLevelStateException e1) 
		{
			e1.printStackTrace();
		} 
		catch (Exception e1) 
		{
			e1.printStackTrace();
		}
	}
	*/
	
	public void updateInputControllerValue( double inVal )
	{
		if( this.gameWindow != null )
		{
			LevelProgressIndicator lpi = this.gameWindow.getTargetControllerIndicator();
			lpi.setValue( inVal );
		}
	}
	
	public void playLevel()
	{
		if( this.gameWindow != null )
		{
			this.gameWindow.dispose();
		}
		
		this.gameWindow = new GameWindow();
		
		LevelProgressIndicator lpi = this.gameWindow.getTargetControllerIndicator();
		
		ConfigParameter par = ConfigApp.getParameter( ConfigApp.INPUT_MIN_VALUE );
		
		
		lpi.setMinimum( ((Number)par.getSelectedValue()).doubleValue() );

		par = ConfigApp.getParameter( ConfigApp.INPUT_MAX_VALUE);
		
		lpi.setMaximum( ((Number)par.getSelectedValue()).doubleValue() );
		
		this.gameWindow.setVisible( true );
		
		GuiManager.getInstance().fullScreen( false );		
		
		Dimension screenSize = this.gameWindow.getSize();
		
		Level lv0 = LevelFactory.getLevel( 0, screenSize );			
		
		LSL.StreamInfo info = LSL.resolve_streams()[ 0 ];
		
		LSLController in = new LSLController( info, 0, )
		
		try 
		{
			//lv0 = LevelFactory.getLevel( new File( ".\\src\\sheets\\zeldaLink2Past.mid" ), screenSize );		
			//lv0 = LevelFactory.getLevel( new File( ".\\src\\sheets\\zelda.mid" ), screenSize );
			//lv0 = LevelFactory.getLevel( new File( ".\\src\\sheets\\Pokemon.mid" ), screenSize );
			//lv0 = LevelFactory.getLevel( new File( ".\\src\\sheets\\test5.mid" ), screenSize );
			lv0 = LevelFactory.getLevel( new File( "./sheets/simpsons.mid" ), screenSize );
			//lv0 = LevelFactory.getLevel( new File( ".\\src\\sheets\\alouette.mid" ), screenSize );
			//lv0 = LevelFactory.getLevel( new File( ".\\src\\sheets\\espana.mid" ), screenSize );
			
			LaunchControl.getInstance().launchScene( lv0 );
		} 
		catch (IllegalLevelStateException e1) 
		{
			e1.printStackTrace();
		} 
		catch (Exception e1) 
		{
			e1.printStackTrace();
		}
	}
}
