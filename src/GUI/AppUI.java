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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import GUI.screens.levels.Level;
import GUI.screens.levels.LevelFactory;
import control.LaunchControl;
import exceptions.IllegalLevelStateException;

import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.Image;

public class AppUI extends JFrame 
{
	/**
	 * 
	 */
	/*
	public static void main(String[] args)
	{
		getInstance().setVisible( true );
	}
	*/
	
	private static final long serialVersionUID = 5759856279333189057L;

	private static AppUI ui;
	
	// Panel
	private JPanel scenePane;
	private JPanel contentPane;
	private JPanel panelMenu;
	
	// Buttom	
	private JButton btnPlay;
	private JButton btnSettings;
	
	/**
	 * Create the frame.
	 */
	public static AppUI getInstance()
	{
		if( ui == null )
		{
			ui = new AppUI();
		}
		
		return ui;
	}
	
	private AppUI() 
	{
		super.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setBounds(100, 100, 450, 300);
		
		setContentPane( this.getMainPanel() );	
	}
	
	protected JPanel getMainPanel()
	{
		if( this.contentPane == null )
		{
			this.contentPane = new JPanel( new BorderLayout() );
			
			this.contentPane.add( this.getPanelMenu(), BorderLayout.NORTH );
			this.contentPane.add( this.getScreenPanel(), BorderLayout.CENTER );
		}
		
		return this.contentPane;
	}
	
	protected JPanel getScreenPanel()
	{
		if( this.scenePane == null )
		{
			this.scenePane = new JPanel();
			//this.contentPane.setBorder( new EmptyBorder(5, 5, 5, 5) );
			this.scenePane.setLayout( new BorderLayout(0, 0) );
			
			//this.contentPane.setFocusable( true );
			
			//this.contentPane.addKeyListener( new KeystrokeAction() );
			//this.contentPane.add( new Frame() );
			
			//this.contentPane.add( new JButton( new ImageIcon( appIcons.appIcon( 32 ) ) ), BorderLayout.CENTER );
		}
		
		return this.scenePane;
	}
		
	protected Dimension getSceneSize()
	{
		return this.getScreenPanel().getSize();
	}
	
	private JPanel getPanelMenu() 
	{
		if ( this.panelMenu == null ) 
		{
			this.panelMenu = new JPanel();
			FlowLayout flowLayout = (FlowLayout) this.panelMenu.getLayout();
			flowLayout.setAlignment( FlowLayout.LEFT );
			
			this.panelMenu.add( this.getBtnPlay() );
			this.panelMenu.add( this.getBtnSettings() );
		}
		return this.panelMenu;
	}
	
	private JButton getBtnPlay() 
	{
		if ( this.btnPlay == null) 
		{
			this.btnPlay = new JButton("Play");
			
			this.btnPlay.setFocusable( false );
			
			this.btnPlay.addActionListener( new ActionListener() 
			{				
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					GuiManager.getInstance().playLevel();
				}
			});
		}
		return this.btnPlay;
	}
	
	private JButton getBtnSettings() 
	{
		if ( this.btnSettings == null ) 
		{ 
			this.btnSettings = new JButton( "Settings" );
						
			this.btnSettings.setIcon( new ImageIcon( AppIcons.Config2( Color.BLACK ).getScaledInstance( 16, 16, BufferedImage.SCALE_SMOOTH ) ) );			
			//this.btnSettings.setIcon( new ImageIcon( AppIcons.Contrabass( 512, Color.BLACK ) ) );
			
			this.btnSettings.setFocusable( false );
		}
		return this.btnSettings;
	}
}
