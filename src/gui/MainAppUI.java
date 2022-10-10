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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gui.panel.SettingPanel;
import gui.panel.inputDevice.InputDevicePanel;
import GUI.tabbedpanel.ClosableTabbedPanel;
import GUI.tabbedpanel.CollectionEvent;
import GUI.tabbedpanel.CollectionListener;
import gui.dialogs.AppSelectPlayer;
import GUI.menu.MenuScroller;
import gui.panel.SelectLevelImagePanel;
import gui.panel.SelectSongPanel;
import config.ConfigApp;
import config.ConfigParameter;
import config.ConfigParameter.ParameterType;
import config.DataBaseSettings;
import config.Player;
import config.language.Caption;
import config.language.Language;
import config.language.TranslateComponents;
import exceptions.ConfigParameterException;
import image.icon.GeneralAppIcon;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
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
	
	private ClosableTabbedPanel playerTabPanel;
	private JTabbedPane scenePanel;
	
	// Buttom	
	private JButton btnPlay;
	private JButton addPlayer;
	
	private JCheckBox chbMuteSession;
	private JCheckBox chbContinueSession;
	private JCheckBox chbSamTest;
			
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
	
	
	private JSpinner spSessionTime;
	
	private JLabel lbSessionTime;
	
	private MenuScroller langMenu;
	
	
	private boolean addingPlayers = false;
		
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
				
		for( Player player : ConfigApp.getPlayers() )
		{
			this.addPlayerSetting( player );
		}	
		
		this.updatePreviewLevelComponents();
		
		super.addComponentListener( new ComponentAdapter() 
		{
			public void componentShown(ComponentEvent e) 
			{
				InputDevicePanel.getInstance( ui ).updateInputs();
			};
		});
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
			
			JScrollPane scroll = new JScrollPane( this.getSettingPanel() );
			scroll.getVerticalScrollBar().setUnitIncrement( 10 );
			
			this.splitPanelMain.setLeftComponent( scroll );
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
			
			JScrollPane scroll = new JScrollPane( this.getSettingFieldPanel().getTabbedPane() );
			scroll.getVerticalScrollBar().setUnitIncrement( 10 );
			
			JSplitPane splitPanel = new JSplitPane( JSplitPane.VERTICAL_SPLIT );			
			
			splitPanel.setLeftComponent( scroll );
			splitPanel.setRightComponent( this.getSongPanel() );
			
			//splitPanel.setResizeWeight( 0.5 );
			//splitPanel.setOneTouchExpandable( true );
			
			this.panelSettings.add( splitPanel, BorderLayout.CENTER );
		}
		
		return this.panelSettings;
	}
		
	private ClosableTabbedPanel getSettingFieldPanel()
	{
		if( this.playerTabPanel == null )
		{
			this.playerTabPanel = new ClosableTabbedPanel();
			
			this.playerTabPanel.addTabbedPanelListener( new CollectionListener()
			{
				@Override
				public void collectionChange(CollectionEvent ev)
				{
					//JTabbedPane t = (JTabbedPane)ev.getSource();
					ClosableTabbedPanel t = (ClosableTabbedPanel)ev.getSource();
					
					int type = ev.getEventType();
					SettingPanel panel = (SettingPanel)ev.getElement();
					int index = ev.getElementIndex();
										
					if( type == CollectionEvent.REMOVE_ELEMENT )
					{					
						if( panel != null )
						{
							ConfigApp.removePlayerSetting( panel.getPlayer() );
						}
						
						if( t.getTabCount() == 0 
								&& !addingPlayers
								//&& ConfigApp.isTesting() 
								)
						{
							ConfigApp.resetPlayerSettings();
							for( Player player : ConfigApp.getPlayers() )
							{
								addPlayerSetting( player );
							}
						}
						
						updatePreviewLevelComponents();
					}
					else if( type == CollectionEvent.INSERT_ELEMENT )
					{
						/*
						if( index < 1 )
						{
							updatePreviewLevelComponents();
						}
						//*/						
					}							

					InputDevicePanel.getInstance( ui ).updatePlayers();
				}
			});			
		}
		
		return this.playerTabPanel;
	}
	
	private void addPlayerSetting( Player player )
	{
		ClosableTabbedPanel tab = this.getSettingFieldPanel();
				
		SettingPanel settings = new SettingPanel( this, player );
		//tab.add( player.getName(), settings );
		tab.insertTab( player.getName(), settings );
		
		int c = tab.getTabCount();
		if( c < 2 && player.isAnonymous() )
		{
			tab.showTabCloseButton( false, 0 );
		}
		else
		{
			tab.showTabCloseButton( true, 0 );
		}
		
		/*
		if( c == 1 )
		{
			SelectSongPanel.getInstance().updateSelectedSong();
		}
		*/
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
						addingPlayers = true;
												
						//
						// Removing Anonymous players
						//
						
						//JTabbedPane p = getSettingFieldPanel();
						ClosableTabbedPanel p = getSettingFieldPanel();
						
						for( int i = p.getTabCount() - 1; i >= 0; i-- )
						{
							SettingPanel sp = (SettingPanel)p.getTabAt( i ); //p.getComponent( i );
							if( sp != null 
									&& sp.getPlayer().isAnonymous() )
							{
								p.removeTabAt( i );
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
						
						int playerNum = p.getTabCount();
						
						for( Player player : players )
						{
							playerNum++;
							
							if( playerNum <= ConfigApp.MAX_NUM_PLAYERS )
							{
								try
								{														
									if( !ConfigApp.loadPlayerSetting( player ) )
									{
										ConfigApp.loadDefaultPlayerSetting( player );
										DataBaseSettings.dbInsertPlayerSetting( player );
									}
								}
								catch ( Exception ex) 
								{
									JOptionPane.showMessageDialog( ui, ex.getCause() + "\n" + ex.getMessage()
									, Language.getLocalCaption( Language.ERROR )
									, JOptionPane.ERROR_MESSAGE );
								}
								finally 
								{
								}
								
								addPlayerSetting( player );
							}
							else
							{
								break;
							}
						}
						
						addingPlayers = false;
						
						updatePreviewLevelComponents();
						
						if( playerNum > ConfigApp.MAX_NUM_PLAYERS ) 
						{
							JOptionPane.showMessageDialog( ui, Language.getLocalCaption( Language.MAX_PLAYER_MSG )
															, Language.getLocalCaption( Language.ERROR )
															, JOptionPane.ERROR_MESSAGE );
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
					JButton b = (JButton)e.getSource();
					b.requestFocus();
										
					Thread t = new Thread()
					{
						public void run() 
						{
							/*
							Dimension d = new Dimension( 300, 200 );
							
							OpeningDialog dialog = new OpeningDialog( d
									, MainAppUI.getInstance().getIconImage()
									, MainAppUI.getInstance().getTitle()
									, Language.getLocalCaption( Language.LOADING )
									, Color.WHITE );
							
							//dialog.setAlwaysOnTop( true );
							dialog.setLocationRelativeTo( MainAppUI.getInstance() );
							dialog.setUndecorated( false );
							//dialog.setDefaultCloseOperation( OpeningDialog.DISPOSE_ON_CLOSE );
							dialog.setVisible( true );		
							*/
							
							try
							{												
								GameManager.getInstance().startGame();
							} 
							catch (Exception ex)
							{
								//ex.printStackTrace();
								
								MainAppUI.getInstance().setVisible( true );
								
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
							finally 
							{
								//dialog.dispose();
							}
						};
					};
						
					t.start();
				}
			});
		}
		return this.btnPlay;
	}
	
	private JPanel getPanelPlay() 
	{
		if ( this.panelPlay == null) 
		{
			this.panelPlay = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );			
			
			this.panelPlay.add( this.getBtnPlay() );
			this.panelPlay.add( this.getMuteSession() );
			this.panelPlay.add( this.getContinuousSession() );
			this.panelPlay.add( this.getSamTest() );
			this.panelPlay.add( this.getSessionTimeLabel() );
			this.panelPlay.add( this.getSessionTimeSpinner() );
		}
		
		return this.panelPlay;
	}
	
	private JTabbedPane getSongPanel()
	{
		if( this.scenePanel == null )
		{
			//this.songPanel = new JPanel( new BorderLayout() );
			this.scenePanel = new JTabbedPane();
			
			Caption cap = Language.getAllCaptions().get( Language.SONGS );			
			this.scenePanel.addTab( cap.getCaption( Language.getCurrentLanguage() )
								, SelectSongPanel.getInstance() );

			cap = Language.getAllCaptions().get( Language.IMAGE );
			this.scenePanel.addTab( cap.getCaption( Language.getCurrentLanguage() )
								, SelectLevelImagePanel.getInstance() );

			this.scenePanel.setSelectedIndex( 0 );
						
			Dimension s = new Dimension( 300, 275 );
			this.scenePanel.setPreferredSize( s );
		}
		
		return this.scenePanel;
	}
	
	private void updatePreviewLevelComponents( )
	{	
		SelectSongPanel.getInstance().updateSelectedSong();		
		SelectLevelImagePanel.getInstance().updatePreviewLevelImages();
	}
	
	private JCheckBox getMuteSession()
	{
		if( this.chbMuteSession == null )
		{
			final String ID = ConfigApp.MUTE_SESSION;
			
			this.chbMuteSession = new JCheckBox( );
			
			String txt = Language.getLocalCaption( Language.MUTE_SESSION );
					
			ImageIcon ic = GeneralAppIcon.Mute( 256,256, Color.BLACK, null );
			ic = null;
			
			if( ic != null )
			{
				BufferedImage img = (BufferedImage)ic.getImage();
				this.chbMuteSession.setIcon( new ImageIcon( img.getScaledInstance( 16, 16, BufferedImage.SCALE_SMOOTH ) ) );
			}
			else
			{
				this.chbMuteSession.setText( txt );
				TranslateComponents.add( this.chbMuteSession, Language.getAllCaptions().get(  Language.MUTE_SESSION ) );			
			}
			
			this.chbMuteSession.addItemListener( new ItemListener() 
			{				
				@Override
				public void itemStateChanged(ItemEvent arg0) 
				{
					ConfigParameter par  = ConfigApp.getGeneralSetting( ID );
					
					JCheckBox ch = (JCheckBox)arg0.getSource();
					
					try 
					{
						if( par == null )
						{
						
							par = new ConfigParameter( new Caption( Language.MUTE_SESSION, Language.defaultLanguage, Language.getLocalCaption( Language.MUTE_SESSION ) )
														, ParameterType.BOOLEAN );
							
							ConfigApp.setGeneralSetting( ID, par );
						}
					
						par.setSelectedValue( ch.isSelected() );
					}
					catch (IllegalArgumentException | ConfigParameterException e) 
					{
						e.printStackTrace();
					}					
				}
			});
		}
		
		return this.chbMuteSession;
	}
	
	private JCheckBox getContinuousSession()
	{
		if( this.chbContinueSession == null )
		{
			final String ID = ConfigApp.CONTINUOUS_SESSION;
			
			this.chbContinueSession = new JCheckBox( );
			
			String txt = Language.getLocalCaption( Language.CONTINUOUS_SESSION );
			
			this.chbContinueSession.setText( txt );
			TranslateComponents.add( this.chbContinueSession, Language.getAllCaptions().get(  Language.CONTINUOUS_SESSION ) );
			
			this.chbContinueSession.addItemListener( new ItemListener() 
			{				
				@Override
				public void itemStateChanged(ItemEvent arg0) 
				{
					ConfigParameter par  = ConfigApp.getGeneralSetting( ID );
					
					JCheckBox ch = (JCheckBox)arg0.getSource();
					
					try 
					{
						if( par == null )
						{
						
							par = new ConfigParameter( new Caption( Language.CONTINUOUS_SESSION, Language.defaultLanguage, Language.getLocalCaption( Language.CONTINUOUS_SESSION ) )
														, ParameterType.BOOLEAN );
							
							ConfigApp.setGeneralSetting( ID, par );
						}
					
						par.setSelectedValue( ch.isSelected() );
					}
					catch (IllegalArgumentException | ConfigParameterException e) 
					{
						e.printStackTrace();
					}					
				}
			});
		}
		
		return this.chbContinueSession;
	}
	
	private JCheckBox getSamTest()
	{
		if( this.chbSamTest == null )
		{
			final String ID = ConfigApp.SAM_TEST;
			
			this.chbSamTest = new JCheckBox( );
			
			final String txt = "SAM test";
			
			this.chbSamTest.setText( txt );
			//TranslateComponents.add( this.chbSamTest, Language.getAllCaptions().get(  Language.CONTINUOUS_SESSION ) );
			
			this.chbSamTest.addItemListener( new ItemListener() 
			{				
				@Override
				public void itemStateChanged(ItemEvent arg0) 
				{
					ConfigParameter par  = ConfigApp.getGeneralSetting( ID );
					
					JCheckBox ch = (JCheckBox)arg0.getSource();
					
					try 
					{
						if( par == null )
						{
							Caption cap = new Caption( ID, Language.defaultLanguage, txt );
						
							par = new ConfigParameter( cap, ParameterType.BOOLEAN );
							
							ConfigApp.setGeneralSetting( ID, par );
						}
					
						par.setSelectedValue( ch.isSelected() );
					}
					catch (IllegalArgumentException | ConfigParameterException e) 
					{
						e.printStackTrace();
					}					
				}
			});
			
			this.chbSamTest.setSelected( true );
		}
		
		return this.chbSamTest;
	}
	
	private JLabel getSessionTimeLabel()
	{
		if( this.lbSessionTime == null )
		{
			String txt = Language.getLocalCaption( Language.LIMIT_SESSION_TIME );
			
			this.lbSessionTime = new JLabel( txt );
			TranslateComponents.add( this.lbSessionTime, Language.getAllCaptions().get(  Language.LIMIT_SESSION_TIME ) );
		}
		
		return this.lbSessionTime;
	}
	
	private JSpinner getSessionTimeSpinner()
	{
		if( this.spSessionTime == null )
		{
			final String ID = ConfigApp.LIMIT_SESSION_TIME;
			
			this.spSessionTime = new JSpinner( new SpinnerNumberModel( 0, 0, null, 1 ) ); 
			
			Font f = this.spSessionTime.getFont();
			FontMetrics fm = this.spSessionTime.getFontMetrics( f );
			
			Dimension pd = this.spSessionTime.getPreferredSize();
			pd.width = fm.stringWidth( "0" ) * 8;
			this.spSessionTime.setPreferredSize( pd );
			this.spSessionTime.setSize( pd );
			
			this.spSessionTime.addChangeListener( new ChangeListener()
			{								
				@Override
				public void stateChanged(ChangeEvent e)
				{
					ConfigParameter par  = ConfigApp.getGeneralSetting( ID );
					
					JSpinner sp = (JSpinner)e.getSource();
					
					try 
					{
						if( par == null )
						{						
							par = new ConfigParameter( new Caption( Language.LIMIT_SESSION_TIME, Language.defaultLanguage, Language.getLocalCaption( Language.LIMIT_SESSION_TIME ) )
														, ParameterType.NUMBER );
							
							ConfigApp.setGeneralSetting( ID, par );
						}
					
						par.setSelectedValue( sp.getValue() );						
					} 
					catch (ConfigParameterException e1)
					{
						e1.printStackTrace();
					}
				}
			});
			
			this.spSessionTime.addMouseWheelListener( new MouseWheelListener() 
			{				
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) 
				{
					if( e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL )
					{
						try
						{	
							JSpinner sp = (JSpinner)e.getSource();
							
							int d = e.getWheelRotation();
							
							if( d > 0 )
							{
								sp.setValue( sp.getModel().getPreviousValue() );
							}
							else
							{
								sp.setValue( sp.getModel().getNextValue() );
							}	
						}
						catch( IllegalArgumentException ex )
						{												
						}
					}
				}
			});
		}
		
		return this.spSessionTime;
	}
}
