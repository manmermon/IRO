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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import GUI.panel.SettingPanel;
import GUI.tabbedpanel.ClosableTabbedPanel;
import GUI.tabbedpanel.CollectionEvent;
import GUI.tabbedpanel.CollectionListener;
import GUI.dialogs.AppSelectPlayer;
import GUI.menu.MenuScroller;
import GUI.panel.InputDevicePanel;
import GUI.panel.SelectLevelImagePanel;
import GUI.panel.SelectSongPanel;
import config.ConfigApp;
import config.Player;
import config.language.Caption;
import config.language.Language;
import config.language.TranslateComponents;
import image.icon.GeneralAppIcon;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class MainAppUI extends JFrame 
{
	/**
	 * 
	 */

	private static final long serialVersionUID = 5759856279333189057L;

	private static MainAppUI ui;
	
	public static final String ID_MAIN_MENU = "MAIN_MENU";
	public static final String ID_PAUSE_MENU = "PAUSE_MENU";
	
	// Panel
	//private JPanel settingPanel;	
	private JPanel panelSettings;
	private JPanel panelMenu;
	private JPanel panelPlay;
	private JPanel panelMultiplayer;
	
	private JSplitPane splitPanelMain;
	
	private ClosableTabbedPanel panelFields;
	private JTabbedPane scenePanel;
	
	// Buttom	
	private JButton btnPlay;
	private JButton addPlayer;
	
	
	// Radio Button
	/*
	private ButtonGroup multiplayerGroup;;	
	private JRadioButton singlePlayer;
	private JRadioButton localMultiplayer;
	private JRadioButton remoteMultiplayer;
	//*/
	
	private JMenuBar menuBar;
	
	private JMenu menuSetting;
	private JMenu jLangMenu;
	
	private MenuScroller langMenu;
	
		
	/**
	 * Create the frame.
	 */
	public static MainAppUI getInstance()
	{
		if( ui == null )
		{
			ui = new MainAppUI();
		}
		
		return ui;
	}
	
	private MainAppUI() 
	{
		super.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		super.setBounds(100, 100, 450, 300);
		
		super.setJMenuBar( this.getGameMenuBar() );
		super.setContentPane( this.getMainPanel() );
		
		/*
		this.multiplayerGroup = new ButtonGroup();
		this.multiplayerGroup.add( this.getSinglePlayerBt() );
		this.multiplayerGroup.add( this.getLocalMultiplayerPlayerBt() );
		this.multiplayerGroup.add( this.getRemoteMultiplayerPlayerBt() );
		//*/
		
		for( Player player : ConfigApp.getPlayers() )
		{
			this.addPlayerSetting( player );
		}	
	}
	
	private JMenuBar getGameMenuBar()
	{
		if( this.menuBar == null )
		{
			this.menuBar = new JMenuBar();
			
			this.menuBar.add( this.getMenuSetting() );
			//this.menuBar.add( this.getAddPlayerMenu() );
			JButton bt = this.getAddPlayerButton();
			bt.setBackground( new Color( 238, 238, 238 ) );
			bt.setPreferredSize( this.getMenuSetting().getPreferredSize() );
			
			this.menuBar.add( bt );
		}
		
		return this.menuBar;
	}
	
	private JMenu getMenuSetting()
	{
		if( this.menuSetting == null )
		{
			Caption cap = Language.getAllCaptions().get( Language.SETTING );
			
			this.menuSetting = new JMenu( cap.getCaption( Language.getCurrentLanguage() ) );
			
			this.menuSetting.add( this.setLanguageMenu() );
			
			TranslateComponents.add( this.menuSetting, cap );
		}
		
		return this.menuSetting;
	}

	private JMenu setLanguageMenu()
	{
		if( this.langMenu == null )
		{
			this.langMenu = new MenuScroller( this.getLangMenu(), 5 );
		}
		
		return this.getLangMenu();
	}
	
	private JMenu getLangMenu()
	{
		if( this.jLangMenu == null )
		{
			Caption cap = Language.getAllCaptions().get( Language.LANGUAGE_TXT );			
			this.jLangMenu = new JMenu( cap.getCaption( Language.getCurrentLanguage() ) );
			
			TranslateComponents.add( this.jLangMenu, cap );
			
			ButtonGroup menuGr = new ButtonGroup();
			
			for( String lang : Language.getAvaibleLanguages() )
			{
				JRadioButtonMenuItem langMenu = new JRadioButtonMenuItem( lang );

				langMenu.addActionListener( new ActionListener() 
				{	
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						JMenuItem m = (JMenuItem)e.getSource();

						if( !m.getText().toLowerCase().equals( Language.getCurrentLanguage().toLowerCase() ) )
						{
							TranslateComponents.translate( m.getText() );
						}
					}
				});

				if( langMenu.getText().toLowerCase().equals( Language.getCurrentLanguage().toLowerCase() ) )
				{
					langMenu.setSelected( true );
				}
				
				menuGr.add( langMenu );

				this.jLangMenu.add( langMenu );
			}
		}

		return this.jLangMenu;
	}
	
	private JSplitPane getMainPanel()
	{
		if( this.splitPanelMain == null )
		{
			this.splitPanelMain = new JSplitPane();
			
			this.splitPanelMain.setLeftComponent( this.getSettingPanel() );
			this.splitPanelMain.setRightComponent( InputDevicePanel.getInstance( this ) );
			
			this.splitPanelMain.setResizeWeight( 0.5 );
			this.splitPanelMain.setOneTouchExpandable( true );
		}
		
		return this.splitPanelMain;
	}
	
	private JPanel getSettingPanel()
	{
		if( this.panelSettings == null )
		{
			this.panelSettings = new JPanel( new BorderLayout() );
			
			this.panelSettings.add( this.getPanelMenu(), BorderLayout.NORTH );
			//this.contentPane.add( this.getSettingPanel(), BorderLayout.CENTER );			
			this.panelSettings.add( this.getSettingFieldPanel( ), BorderLayout.CENTER );
			this.panelSettings.add( this.getSongPanel(), BorderLayout.SOUTH );
		}
		
		return this.panelSettings;
	}
		
	private JTabbedPane getSettingFieldPanel()
	{
		if( this.panelFields == null )
		{
			this.panelFields = new ClosableTabbedPanel();
			
			this.panelFields.addTabbedPanelListener( new CollectionListener()
			{
				@Override
				public void collectionChange(CollectionEvent ev)
				{
					JTabbedPane t = (JTabbedPane)ev.getSource();
					
					int type = ev.getEventType();
					SettingPanel panel = (SettingPanel)ev.getElement();
					int index = ev.getElementIndex();
										
					if( type == CollectionEvent.REMOVE_ELEMENT )
					{					
						if( panel != null )
						{
							ConfigApp.removePlayerSetting( panel.getPlayer() );
						}
					}
					else if( type == CollectionEvent.CLEAR_ELEMENT )
					{
						ConfigApp.loadDefaultProperties();
						for( Player player : ConfigApp.getPlayers() )
						{
							addPlayerSetting( player );
						}
					}
					else if( type == CollectionEvent.INSERT_ELEMENT )
					{
						if( index < 1 )
						{
							Player player = ((SettingPanel)t.getComponent( index ) ).getPlayer();
							setSceneTabbedPanels( player );
						}
					}							

					InputDevicePanel.getInstance( ui ).updatePlayers();
				}
			});			
		}
		
		return this.panelFields;
	}
	
	private void addPlayerSetting( Player player )
	{
		JTabbedPane tab = this.getSettingFieldPanel();
		
		SettingPanel settings = new SettingPanel( this, player );
		tab.add( player.getName(), settings );
	}
			
	private JPanel getPanelMenu() 
	{
		if ( this.panelMenu == null ) 
		{
			this.panelMenu = new JPanel();
			this.panelMenu.setLayout(new BorderLayout(0, 0));
			this.panelMenu.add( this.getPanelPlay(), BorderLayout.WEST);
			this.panelMenu.add( this.getMultiplayerPanel(), BorderLayout.EAST );
			//this.panelMenu.add( this.getAddPlayerButton(), BorderLayout.EAST );
			
			//this.panelMenu.add( this.getSelectUserPanel(), BorderLayout.EAST);			
		}
		return this.panelMenu;
	}
	
	private JButton getAddPlayerButton()
	{
		if( this.addPlayer == null )
		{
			Caption cap = Language.getAllCaptions().get( Language.PLAYER );
			this.addPlayer = new JButton( cap.getCaption( Language.getCurrentLanguage() ) );
			
			this.addPlayer.setIcon( GeneralAppIcon.Add( 16, Color.BLACK ) );
			this.addPlayer.setFocusPainted( false );
			
			this.addPlayer.addActionListener( new ActionListener()
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
					
					List< Player > players = selplayerDialog.getSelectedPlayers();
					
					if( players != null && !players.isEmpty() ) 
					{	
						for( Player player : players )
						{
							try
							{														
								ConfigApp.loadPlayerSetting( player );
							}
							catch ( Exception ex) 
							{
								JOptionPane.showMessageDialog( ui, ex.getCause() + "\n" + ex.getMessage()
								, Language.getLocalCaption( Language.ERROR )
								, JOptionPane.ERROR_MESSAGE );
							}
							finally 
							{
								//GameManager.getInstance().updateSetting();
							}
							
							addPlayerSetting( player );
						}
						
						JTabbedPane p = getSettingFieldPanel();
						
						for( int i = p.getTabCount() - 1; i >= 0; i-- )
						{
							SettingPanel sp = (SettingPanel)p.getComponent( i );
							if( sp != null 
									&& sp.getPlayer().isAnonymous() )
							{
								p.removeTabAt( i );
								
								if( i == 0 )
								{
									setSceneTabbedPanels( players.get( 0 ) );
								}
							}
						}
						
						List< Player > anonymousPlayers = new ArrayList< Player >();
						for( Player player : ConfigApp.getPlayers() )
						{
							if( player.isAnonymous() )
							{
								anonymousPlayers.add( player );
							}
						}
						
						for( Player player : anonymousPlayers )
						{
							ConfigApp.removePlayerSetting( player );
						}
					}
				}
			});
			
			TranslateComponents.add( this.addPlayer, cap );
		}
		
		return this.addPlayer;
	}
	
	private JPanel getMultiplayerPanel()
	{
		if( this.panelMultiplayer == null )
		{
			this.panelMultiplayer = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
			
			//this.panelMultiplayer.add( this.getAddPlayerButton() );
			/*
			this.panelMultiplayer.add( this.getSinglePlayerBt() );
			this.panelMultiplayer.add( this.getLocalMultiplayerPlayerBt() );
			this.panelMultiplayer.add( this.getRemoteMultiplayerPlayerBt() );
			
			this.getSinglePlayerBt().doClick();
			//*/
		}
		
		return this.panelMultiplayer; 
	}
	
	/*
	private JRadioButton getSinglePlayerBt()
	{
		if( this.singlePlayer == null )
		{
			Caption cap = Language.getAllCaptions().get( Language.SINGLE );
			this.singlePlayer = new JRadioButton( cap.getCaption( Language.getCurrentLanguage() ) );
			
			this.singlePlayer.addActionListener( new ActionListener()
			{	
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					ConfigParameter par = ConfigApp.getPlayerConfiguration( ConfigApp.MULTIPLAYER );
					try
					{
						par.setSelectedValue( ConfigApp.SINGLE_PLAYER );
					}
					catch (ConfigParameterException ex)
					{
						ex.printStackTrace();
					}
				}
			});
			
			TranslateComponents.add( this.singlePlayer, cap );		
		}
		
		return this.singlePlayer;
	}
	
	private JRadioButton getLocalMultiplayerPlayerBt() 
	{
		if( this.localMultiplayer == null )
		{
			Caption cap = Language.getAllCaptions().get( Language.LOCAL_MULTIPLAYER );
			this.localMultiplayer = new JRadioButton( cap.getCaption( Language.getCurrentLanguage() ) );
		
			this.localMultiplayer.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					ConfigParameter par = ConfigApp.getPlayerConfiguration( ConfigApp.MULTIPLAYER );
					try
					{
						par.setSelectedValue( ConfigApp.LOCA_MULTIPLAYER );
					}
					catch (ConfigParameterException ex)
					{
						ex.printStackTrace();
					}
				}
			});
			
			TranslateComponents.add( this.localMultiplayer, cap );
		}
		
		return this.localMultiplayer;
	}
	
	private JRadioButton getRemoteMultiplayerPlayerBt()
	{
		if( this.remoteMultiplayer == null )
		{
			Caption cap = Language.getAllCaptions().get( Language.REMOTE_MULTIPLAYER );
			this.remoteMultiplayer = new JRadioButton( cap.getCaption( Language.getCurrentLanguage() ) );
			
			this.remoteMultiplayer.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					ConfigParameter par = ConfigApp.getPlayerConfiguration( ConfigApp.MULTIPLAYER );
					try
					{
						par.setSelectedValue( ConfigApp.REMOTE_MULTIPLAYER );
					}
					catch (ConfigParameterException ex)
					{
						ex.printStackTrace();
					}
				}
			});
			
			TranslateComponents.add( this.remoteMultiplayer, cap );
		}
		
		return this.remoteMultiplayer;
	}
	//*/
	
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
	
	/*
	public void loadSetting( Player player )
	{
		getSettingFieldPanel().setVisible( false );
		getSettingFieldPanel().removeAll();
		getSettingFieldPanel().add( new SettingPanel( this, player ) );
		getSettingFieldPanel().setVisible( true );
	}
	*/
	
	private JButton getBtnPlay() 
	{
		if ( this.btnPlay == null) 
		{
			this.btnPlay = new JButton( Language.getLocalCaption( Language.PLAY ));
			
			TranslateComponents.add( this.btnPlay, Language.getAllCaptions().get( Language.PLAY ) );
			
			//this.btnPlay.setFocusable( false );
			
			this.btnPlay.addActionListener( new ActionListener() 
			{				
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					try
					{
						JButton b = (JButton)e.getSource();
						b.requestFocus();
												
						GameManager.getInstance().playLevel();
					} 
					catch (Exception ex)
					{
						ex.printStackTrace();
						
						String msg = "";
						
						if( ex.getCause() != null )
						{
							msg = ex.getCause().toString();
						}

						msg += "\n" +ex.getMessage();
						
						JOptionPane.showMessageDialog( ui, msg 
														, Language.getLocalCaption( Language.ERROR )
														, JOptionPane.ERROR_MESSAGE );
					}
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
	
	private JTabbedPane getSongPanel()
	{
		if( this.scenePanel == null )
		{
			//this.songPanel = new JPanel( new BorderLayout() );
			this.scenePanel = new JTabbedPane();
			
			this.setSceneTabbedPanels( null );
			
			Dimension s = new Dimension( 300, 275 );
			this.scenePanel.setPreferredSize( s );
		}
		
		return this.scenePanel;
	}
	
	private void setSceneTabbedPanels( Player player )
	{
		if( player != null )
		{
			JTabbedPane panel = this.getSongPanel();
			panel.setVisible( false );
			panel.removeAll();
			
			Caption cap = Language.getAllCaptions().get( Language.SONGS );			
			panel.addTab( cap.getCaption( Language.getCurrentLanguage() )
									, new SelectSongPanel() );
			
			SelectLevelImagePanel selImgs = SelectLevelImagePanel.getInstance();
			selImgs.setVisible( false );
			selImgs.setPlayer( player );
			
			cap = Language.getAllCaptions().get( Language.IMAGE );
			panel.addTab( cap.getCaption( Language.getCurrentLanguage() )
									, selImgs );
			
			panel.setSelectedIndex( 0 );
			panel.setVisible( true );
		}
	}
}
