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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import GUI.components.Frame;
import GUI.screens.levels.Level;
import GUI.screens.levels.LevelFactory;
import control.LaunchControl;
import exceptions.IllegalLevelStateException;
import stoppableThread.IStoppableThread;

public class GuiManager 
{
	private static GuiManager manager;
	
	private Frame currentFrame;
	
	private JFrame appGUIFullScreen = null;
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
	
	public void setFrame( Frame fr )
	{
		if( this.currentFrame == null )
		{
			AppUI ui = AppUI.getInstance();

			JPanel scenePanel = ui.getScreenPanel();

			scenePanel.setVisible( false );
			scenePanel.removeAll();
			scenePanel.add( fr, BorderLayout.CENTER );
			scenePanel.setVisible( true );
			
			this.currentFrame = fr;
		}
	}
	
	public void removeCurrentFrame()
	{
		AppUI ui = AppUI.getInstance();

		JPanel scenePanel = ui.getScreenPanel();
		
		scenePanel.setVisible( false );
		scenePanel.removeAll();
		scenePanel.setVisible( true );
		
		this.currentFrame = null;
	}
	
	public Dimension getScreenSize()
	{
		AppUI ui = AppUI.getInstance();
		return ui.getSceneSize();
	}
	
	public void fullScreen( boolean full )
	{
		AppUI ui = AppUI.getInstance();
		
		if( !full )
		{
			if (this.appGUIFullScreen != null)
			{
				if (this.autoHideMenu != null)
				{
					this.autoHideMenu.stopThread( IStoppableThread.FORCE_STOP );
					this.autoHideMenu = null;
				}
	
				this.appGUIFullScreen.setVisible(false);			
				
				ui.setVisible(false);
				ui.setContentPane( this.appGUIFullScreen.getContentPane() );
				//ui.setJMenuBar( this.appGUIFullScreen.getJMenuBar() );
	
				this.appGUIFullScreen.dispose();
				this.appGUIFullScreen = null;
	
				//ui.getJMenuBar().setVisible(true);
				ui.setVisible(true);
			}
			else if (this.prevGUILocation != null)
			{
				ui.setLocation( this.prevGUILocation );
			}
		}
		else
		{
				ui.setVisible(false);

				this.appGUIFullScreen = new JFrame();
				this.appGUIFullScreen.setUndecorated(true);
				//this.appGUIFullScreen.setJMenuBar(ui.getJMenuBar());
				//this.appGUIFullScreen.getJMenuBar().setVisible(false);
				this.appGUIFullScreen.setContentPane(ui.getContentPane());
				this.appGUIFullScreen.setResizable(false);
				this.appGUIFullScreen.setAlwaysOnTop(true);
				this.appGUIFullScreen.setLocation(0, 0);
				this.appGUIFullScreen.setIconImage(ui.getIconImage());
				this.appGUIFullScreen.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

				this.appGUIFullScreen.addFocusListener(new FocusAdapter()
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
					gd.setFullScreenWindow(this.appGUIFullScreen);
				}
				else
				{
					Rectangle bounds = gd.getDefaultConfiguration().getBounds();

					this.appGUIFullScreen.setSize(bounds.width, bounds.height);
				}

				this.appGUIFullScreen.setVisible(true);
				this.appGUIFullScreen.toFront();

				this.appGUIFullScreen.addMouseMotionListener(new MouseAdapter()
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

				this.autoHideMenu = new mouseTracking(this.appGUIFullScreen);

				try
				{
					this.autoHideMenu.startThread();
				}
				catch (Exception e1)
				{
					JMenuBar menuBar = this.appGUIFullScreen.getJMenuBar();
					if (menuBar != null)
					{
						menuBar.setVisible(true);
					}

					this.autoHideMenu.stopThread( IStoppableThread.FORCE_STOP );
					this.autoHideMenu = null;
				}
		}
	}

	public void playLevel()
	{
		GuiManager.getInstance().removeCurrentFrame();
		
		GuiManager.getInstance().fullScreen( false );		
		
		Dimension screenSize = GuiManager.getInstance().getScreenSize();
		
		Level lv0 = LevelFactory.getLevel( 0, screenSize );				
		
		try 
		{
			//lv0 = LevelFactory.getLevel( new File( ".\\src\\sheets\\zeldaLink2Past.mid" ), screenSize );		
			//lv0 = LevelFactory.getLevel( new File( ".\\src\\sheets\\zelda.mid" ), screenSize );
			//lv0 = LevelFactory.getLevel( new File( ".\\src\\sheets\\Pokemon.mid" ), screenSize );
			//lv0 = LevelFactory.getLevel( new File( ".\\src\\sheets\\test5.mid" ), screenSize );
			lv0 = LevelFactory.getLevel( new File( ".\\src\\sheets\\simpsons.mid" ), screenSize );
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
