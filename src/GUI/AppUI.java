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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import GUI.panel.SettingPanel;
import GUI.panel.inputDevicePanel;
import config.language.Language;
import config.language.TranslateComponents;

import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class AppUI extends JFrame 
{
	/**
	 * 
	 */

	private static final long serialVersionUID = 5759856279333189057L;

	private static AppUI ui;
	
	public static final String ID_MAIN_MENU = "MAIN_MENU";
	public static final String ID_PAUSE_MENU = "PAUSE_MENU";
	
	// Panel
	//private JPanel settingPanel;
	private JPanel panelMain;
	private JPanel panelSettings;
	private JPanel panelMenu;
	private JPanel panelPlay;
	private JPanel panelFields;
	
	
	// Buttom	
	private JButton btnPlay;
	
	
		
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
		super.setBounds(100, 100, 450, 300);
		
		super.setContentPane( this.getMainPanel() );	
	}
	
	private JPanel getMainPanel()
	{
		if( this.panelMain == null )
		{
			this.panelMain = new JPanel();
			this.panelMain.setLayout( new GridLayout( 0, 2 ) );
			
			this.panelMain.add( this.getSettingPanel() );
			this.panelMain.add( inputDevicePanel.getInstance( this ) );
		}
		
		return this.panelMain;
	}
	
	private JPanel getSettingPanel()
	{
		if( this.panelSettings == null )
		{
			this.panelSettings = new JPanel( new BorderLayout() );
			
			this.panelSettings.add( this.getPanelMenu(), BorderLayout.NORTH );
			//this.contentPane.add( this.getSettingPanel(), BorderLayout.CENTER );			
			this.panelSettings.add( this.getSettingFieldPanel( ), BorderLayout.CENTER );
		}
		
		return this.panelSettings;
	}
	
	/*
	private JPanel getSettingPanel()
	{
		if( this.settingPanel == null )
		{
			this.settingPanel = new JPanel();
			
			this.settingPanel.setLayout( new GridLayout( 2, 1 ) );
			
			this.settingPanel.add( this.getSettingFieldPanel() );
			this.settingPanel.add( SelectSongPanel.getInstance() );
		}
		
		return this.settingPanel;
	}
	//*/
	
	private JPanel getSettingFieldPanel()
	{
		if( this.panelFields == null )
		{
			this.panelFields = new JPanel( new BorderLayout() );
			
			this.panelFields.add( new SettingPanel( this ) );
		}
		
		return this.panelFields;
	}
			
	private JPanel getPanelMenu() 
	{
		if ( this.panelMenu == null ) 
		{
			this.panelMenu = new JPanel();
			this.panelMenu.setLayout(new BorderLayout(0, 0));
			this.panelMenu.add( this.getPanelPlay(), BorderLayout.WEST);
			
			
			//this.panelMenu.add( this.getSelectUserPanel(), BorderLayout.EAST);			
		}
		return this.panelMenu;
	}
	
	/*
	private JPanel getSelectUserPanel()
	{
		if( this.panelUser == null )
		{
			this.panelUser = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
			this.panelUser.add( this.getSelectPlayerButtom() );
		}
		
		return this.panelUser;
	}
	//*/
	
	/*
	private JButton getSelectPlayerButtom()
	{
		if( this.btPlayer == null )
		{
			this.btPlayer = new JButton( Language.getLocalCaption( Language.PLAYER ) );
			
			TranslateComponents.add( this.btPlayer, Language.getAllCaptions().get( Language.PLAYER ) );
			
			this.btPlayer.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					AppSelectPlayer selplayerDialog = new AppSelectPlayer( ui );
					
					Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
					d.width /= 3;
					d.height /= 3;
					
					Rectangle r = ui.getBounds();
					
					Rectangle bounds = new Rectangle( d );
					bounds.setLocation( r.getLocation() );
					
					selplayerDialog.setBounds( bounds );
					selplayerDialog.setVisible( true );
					
					User user = selplayerDialog.getSelectedUser();
					
					if( user != null ) 
					{					
						ConfigParameter parUser = ConfigApp.getParameter( ConfigApp.USER );
						
						User currentUser = (User)parUser.getSelectedValue();
						
						try
						{
							if( currentUser.getId() != user.getId() )
							{
								parUser.clear();
								parUser.add( user );
													
								try
								{
									List< Tuple< String, Object > > settings = ConfigApp.getUserConfig( user.getId() );
									
									if( settings.isEmpty() )
									{
										ConfigApp.loadDefaultProperties();
										parUser = ConfigApp.getParameter( ConfigApp.USER );
										parUser.clear();
										parUser.add( user );
										
										if( user.getId() != User.ANONYMOUS_USER_ID  )
										{
											ConfigApp.insertUserConfig( user.getId() );
											
											for( ConfigParameter par : ConfigApp.getParameters() )
											{
												par.setUserID( user.getId() );
											}
										}
									}
									else
									{
										for( Tuple< String, Object > par : settings )
										{
											Object val = par.y;
											
											ConfigParameter p = ConfigApp.getParameter( par.x );
											if( p != null )
											{
												p.setUserID( user.getId() );
												
												if( p.getAllValues().size() > 1 )
												{
													if( p.get_type() == ParameterType.COLOR )
													{
														val = new Color( (Integer)val );
													}
	
													if( val != null )
													{
														p.setSelectedValue( val );
													}
												}
												else
												{
													p.clear();
													if( val != null )
													{
														p.add( val );
													}
												}
											}
										}										
									}
								}
								catch ( SQLException ex) 
								{
									JOptionPane.showMessageDialog( ui, ex.getCause() + "\n" + ex.getMessage()
																	, Language.getLocalCaption( Language.ERROR )
																	, JOptionPane.ERROR_MESSAGE );
								}
								finally 
								{
									loadSetting();
								}
							}
						} 
						catch (ConfigParameterException e1)
						{
							e1.printStackTrace();
						}
					}
				}
			});
		}
		
		return this.btPlayer;
	}
	//*/
	
	public void loadSetting()
	{
		getSettingFieldPanel().setVisible( false );
		getSettingFieldPanel().removeAll();
		getSettingFieldPanel().add( new SettingPanel( this ) );
		getSettingFieldPanel().setVisible( true );
	}
	
	private JButton getBtnPlay() 
	{
		if ( this.btnPlay == null) 
		{
			this.btnPlay = new JButton( Language.getLocalCaption( Language.PLAY ));
			
			TranslateComponents.add( this.btnPlay, Language.getAllCaptions().get( Language.PLAY ) );
			
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
	
	private JPanel getPanelPlay() 
	{
		if (panelPlay == null) 
		{
			panelPlay = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panelPlay.getLayout();
			
			flowLayout.setAlignment( FlowLayout.RIGHT );
			panelPlay.add(getBtnPlay());
		}
		
		return panelPlay;
	}
}
