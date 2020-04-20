
package GUI.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import GUI.GameManager;
import GUI.JColorComboBox;
import GUI.dialogs.AppSelectPlayer;
import config.ConfigApp;
import config.ConfigParameter;
import config.ConfigParameter.ParameterType;
import config.Player;
import config.language.Caption;
import config.language.Language;
import config.language.TranslateComponents;
import exceptions.ConfigParameterException;
import general.ArrayTreeMap;
import general.NumberRange;
import general.Tuple;

public class SettingPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1219995574372606332L;
	
	private JPanel containerPanel = null;
	private JTabbedPane scenePanel = null;
	
	private JScrollPane scrollFields = null;
	
	private JPopupMenu playerImgPopMenu;
		
	private Window owner;
	/**
	 * Create the dialog.
	 */
	public SettingPanel( Window owner )
	{		
		this.owner = owner;
		
		this.setLayout( new GridLayout( 2, 0 ) );
		
		this.add( this.getContainerScroll()  );
		
		this.add( this.getSongPanel() );
	}

	private JScrollPane getContainerScroll()	
	{
		if( this.scrollFields == null )
		{
			this.scrollFields = new JScrollPane( this.getContainerPanel() );
			
			this.scrollFields.setVisible( true );
		}
		
		return this.scrollFields;
	}
	
	private JTabbedPane getSongPanel()
	{
		if( this.scenePanel == null )
		{
			//this.songPanel = new JPanel( new BorderLayout() );
			this.scenePanel = new JTabbedPane();
			
			this.setSceneTabbedPanels();
			
		}
		
		return this.scenePanel;
	}
	
	private void setSceneTabbedPanels()
	{
		JTabbedPane panel = this.getSongPanel();
		panel.removeAll();
		
		Caption cap = Language.getAllCaptions().get( Language.SONGS );			
		panel.addTab( cap.getCaption( Language.getCurrentLanguage() )
								, new SelectSongPanel() );
		
		cap = Language.getAllCaptions().get( Language.IMAGE );
		panel.addTab( cap.getCaption( Language.getCurrentLanguage() )
								, new SelectLevelImagePanel() );
		//TranslateComponents.add( this.scenePanel, cap );
	}
	
	private JPanel getContainerPanel()
	{
		if( this.containerPanel == null )
		{
			this.containerPanel = new JPanel();
			
			this.containerPanel.setLayout( new BorderLayout() );
			
			this.updateSetting( );
		}
		
		return this.containerPanel;
	}
	
	private void updateSetting( )
	{
		JPanel container = this.getContainerPanel();
		container.setVisible( false );
		container.removeAll();
		
		int cols = 2;
		
		container.setLayout( new BorderLayout() );
		
		JPanel subContainer = new JPanel( new GridLayout( 0, cols, 0, 0) );
		container.add( subContainer, BorderLayout.CENTER );
		
		Collection< ConfigParameter > parameters = ConfigApp.getParameters();
		
		ArrayTreeMap< Integer, ConfigParameter > orderPars = new ArrayTreeMap< Integer, ConfigParameter>();
		for( ConfigParameter p : parameters )
		{
			orderPars.put( p.getPriority(), p );
		}
		
		List< ConfigParameter > pars = new ArrayList<ConfigParameter>();
		for( Integer t : orderPars.keySet() )			
		{
			for( ConfigParameter p : orderPars.get( t ) )
			{
				pars.add( p );
			}
		}
		
		List< JPanel > listPanels = new ArrayList<JPanel>();
		
		JPanel containerEvenPanel = new JPanel( new BorderLayout() );
		subContainer.add(containerEvenPanel);
		
		JPanel panelEven = new JPanel();
		
		panelEven.setLayout( new BoxLayout( panelEven, BoxLayout.Y_AXIS ) );		
		containerEvenPanel.add( panelEven, BorderLayout.NORTH );
		
		listPanels.add( panelEven );
		
		JPanel containerOddPanel = new JPanel( new BorderLayout() );
		subContainer.add(containerOddPanel);
		
		JPanel panelOdd = new JPanel();
		panelOdd.setLayout( new BoxLayout( panelOdd, BoxLayout.Y_AXIS ) );
		containerOddPanel.add( panelOdd, BorderLayout.NORTH );
		listPanels.add( panelOdd );
		
		if( ! listPanels.isEmpty() )
		{
			int numPars = 0;
			for( ConfigParameter p : pars )
			{
				if( p.get_type() != ParameterType.USER && p.get_type() != ParameterType.SONG )
				{
					int i = numPars % listPanels.size();
					
					JPanel panel = listPanels.get( i );
					
					JPanel parPanel = this.getParamenterPanel( p );
					
					if( parPanel != null )
					{
						panel.add( parPanel );
						
						numPars++;
					}
				}
			}
		}
		
		JPanel userPanel = new JPanel( new BorderLayout() );
					
		userPanel.add( this.getUserLabel(), BorderLayout.CENTER );
		
		container.add( userPanel, BorderLayout.NORTH );		
				
		container.setVisible( true );
		
		this.setSceneTabbedPanels();
	}
	
	private JPanel getUserLabel()
	{
		ConfigParameter par = ConfigApp.getParameter( ConfigApp.PLAYER );
		final Player player;
		if( par != null )
		{
			player = (Player)par.getSelectedValue();
		}
		else
		{
			 player = new Player();
		}

		JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		
		JLabel userName = new JLabel( player.getName() );
		JButton btImg = new JButton( );
		btImg.setBorder( BorderFactory.createEtchedBorder() );
		btImg.setIcon( player.getImg( ConfigApp.playerPicSizeIcon.x, ConfigApp.playerPicSizeIcon.y ) );		
		btImg.setBackground( Color.WHITE );
		
		btImg.addMouseListener( new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				JButton b = (JButton)e.getSource();
				b.setBorder(BorderFactory.createLineBorder( Color.BLUE, 2 ) );
			}
			
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				JButton b = (JButton)e.getSource();
				b.setBorder(BorderFactory.createEtchedBorder() );
			} 
		});
		
		btImg.addActionListener( new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//if( player.getId() != User.ANONYMOUS_USER_ID )
				{
					JButton b = (JButton)e.getSource();
					
					getPlayerImgPopMenu().show( b, b.getLocation().x, b.getLocation().y );
				}
			}
				
		});
				
		panel.add( btImg );
		panel.add( userName );
		
		return panel;
	}
	
	private JPopupMenu getPlayerImgPopMenu()
	{
		if( this.playerImgPopMenu == null )
		{
			this.playerImgPopMenu = new JPopupMenu();
						
			final JMenuItem changeImageMenu = new JMenuItem( Language.getLocalCaption( Language.CHANGE_IMAGE ) );
			TranslateComponents.add( changeImageMenu, Language.getAllCaptions().get( Language.CHANGE_IMAGE ) );
			changeImageMenu.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{			
					selectNewUserImage( (JButton)playerImgPopMenu.getInvoker() );
				}
			});
			
			final JMenuItem removeImageMenu = new JMenuItem( Language.getLocalCaption( Language.REMOVE_IMAGE ) );
			TranslateComponents.add( removeImageMenu, Language.getAllCaptions().get( Language.REMOVE_IMAGE ) );
			removeImageMenu.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					int act = JOptionPane.showConfirmDialog( owner, Language.getLocalCaption( Language.REMOVE_PLAYER_IMAGE_MSG ) );
					
					if( act == JOptionPane.OK_OPTION )
					{
						removeUserImage( (JButton)playerImgPopMenu.getInvoker() );
					}
				}
			});
			
			JMenuItem changePlayerMenu = new JMenuItem( Language.getLocalCaption( Language.CHANGE_PLAYER ) );
			TranslateComponents.add( changePlayerMenu, Language.getAllCaptions().get( Language.CHANGE_PLAYER ) );
			changePlayerMenu.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					AppSelectPlayer selplayerDialog = new AppSelectPlayer( owner );
					
					Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
					d.width /= 3;
					d.height /= 3;
					
					Rectangle r = owner.getBounds();
					
					Rectangle bounds = new Rectangle( d );
					bounds.setLocation( r.getLocation() );
					
					selplayerDialog.setBounds( bounds );
					selplayerDialog.setVisible( true );
					
					Player user = selplayerDialog.getSelectedUser();
					
					if( user != null ) 
					{					
						ConfigParameter parUser = ConfigApp.getParameter( ConfigApp.PLAYER );
						
						Player currentUser = (Player)parUser.getSelectedValue();
						
						try
						{
							if( currentUser.getId() != user.getId() )
							{
								parUser.setSelectedValue( user );
													
								try
								{
									List< Tuple< String, Object > > settings = ConfigApp.getPlayerConfigDB( user.getId() );
									
									if( settings.isEmpty() )
									{
										ConfigApp.loadDefaultProperties();
										parUser = ConfigApp.getParameter( ConfigApp.PLAYER );
										
										parUser.setSelectedValue( user );
										
										if( user.getId() != Player.ANONYMOUS_USER_ID  )
										{
											ConfigApp.insertPlayerConfigDB( user.getId() );
											
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
												
												if( p.get_type() == ParameterType.COLOR )
												{
														val = new Color( (Integer)val );
												}
	
												if( val != null )
												{
													p.setSelectedValue( val );
												}
											}
										}										
									}
								}
								catch ( Exception ex) 
								{
									JOptionPane.showMessageDialog( owner, ex.getCause() + "\n" + ex.getMessage()
																	, Language.getLocalCaption( Language.ERROR )
																	, JOptionPane.ERROR_MESSAGE );
								}
								finally 
								{
									GameManager.getInstance().updateSetting();
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
			
			this.playerImgPopMenu.addPopupMenuListener( new PopupMenuListener()
			{	
				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent arg0)
				{
					changeImageMenu.setVisible( true );
					changeImageMenu.setEnabled( true );
					
					removeImageMenu.setVisible( true );
					removeImageMenu.setEnabled( true );
					
					ConfigParameter par = ConfigApp.getParameter( ConfigApp.PLAYER );
					Player user = (Player)par.getSelectedValue();
					
					if( user.getId() == Player.ANONYMOUS_USER_ID )
					{
						changeImageMenu.setVisible( false );
						changeImageMenu.setEnabled( false );
						
						removeImageMenu.setVisible( false );
						removeImageMenu.setEnabled( false );
					}
				}
				
				@Override
				public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void popupMenuCanceled(PopupMenuEvent arg0)
				{
					// TODO Auto-generated method stub
					
				}
			});
			
			this.playerImgPopMenu.add( changeImageMenu );
			this.playerImgPopMenu.add( removeImageMenu );
			this.playerImgPopMenu.add( new JSeparator() );
			this.playerImgPopMenu.add( changePlayerMenu );
			
		}
			
		return this.playerImgPopMenu;
	}
	
	private void removeUserImage( JButton b )
	{
		Player user = (Player)ConfigApp.getParameter( ConfigApp.PLAYER ).getSelectedValue();
		if( user.getId() != Player.ANONYMOUS_USER_ID )
		{
			user.setDefaultImage();
			
			updatePlayerImage( b );
			
			try
			{
				ConfigApp.updatePlayerDB( user );
			} 
			catch (SQLException ex)
			{
				JOptionPane.showMessageDialog( owner, ex.getMessage() 
						, Language.getLocalCaption( Language.ERROR )
						, JOptionPane.ERROR_MESSAGE );
			}
		}
	}
	
	private void selectNewUserImage( JButton b )
	{	
		Player user = (Player)ConfigApp.getParameter( ConfigApp.PLAYER ).getSelectedValue();
		if( user.getId() != Player.ANONYMOUS_USER_ID )
		{
			JFileChooser jfc = new JFileChooser( "./" );

			jfc.setMultiSelectionEnabled( false );

			jfc.setDialogTitle( "");
			jfc.setDialogType( JFileChooser.OPEN_DIALOG );
			jfc.setFileSelectionMode( JFileChooser.FILES_ONLY );
			
			String exts[] = ImageIO.getReaderFileSuffixes();
			
			for( int i = exts.length - 1; i >= 0; i-- )
			{
				FileNameExtensionFilter filter = new FileNameExtensionFilter( exts[ i ], exts[ i ] );
							
				jfc.addChoosableFileFilter( filter );
				
				if( i == 0 )
				{
					jfc.setFileFilter( filter );
				}
			}

			int returnVal = jfc.showDialog( owner, null);
			
			if( returnVal == JFileChooser.APPROVE_OPTION )
			{
				File img = jfc.getSelectedFile();
				
				try
				{
					BufferedImage newImg = ImageIO.read( img );
					
					user.getImg().setImage( newImg );
					
					ConfigApp.updatePlayerDB( user );
					
					updatePlayerImage( b );
				}
				catch ( SQLException | IOException e) 
				{
					JOptionPane.showMessageDialog( owner, e.getMessage() 
													, Language.getLocalCaption( Language.ERROR )
													, JOptionPane.ERROR_MESSAGE );
				}
				
			}
		}
	}
	
	private void updatePlayerImage( JButton b )
	{
		if( b != null )
		{
			Player user = (Player)ConfigApp.getParameter( ConfigApp.PLAYER ).getSelectedValue();
			
			b.setIcon( user.getImg( ConfigApp.playerPicSizeIcon.x, ConfigApp.playerPicSizeIcon.y ) );
		}
	}
	
	private JPanel getParamenterPanel( ConfigParameter par )
	{		
		JPanel panel = null;
		
		if( par != null )
		{	
			ConfigParameter lang = ConfigApp.getParameter( ConfigApp.LANGUAGE );
				
			String l = Language.getCurrentLanguage();
			
			Object languages = lang.getSelectedValue();
			if( languages != null )
			{
				l = languages.toString();
			}
						
			Component comp = this.getParComponent( par );
			
			if( comp != null )
			{
				panel = new JPanel();
				panel.setLayout( new GridLayout( 0, 1 ) );
				
				String title = par.get_ID().getCaption( l );			
				panel.setBorder( BorderFactory.createTitledBorder( title ));
				
				TranslateComponents.add( panel, par.get_ID() );
				
				panel.add( comp );
				
				Dimension d = super.getSize();
				Dimension dc = comp.getPreferredSize();
				Dimension size = panel.getPreferredSize();
				
				size.width = d.width / 3;
				size.height = dc.height > size.height ? dc.height : size.height;
				
				panel.setPreferredSize( size );
			}
		}
		
		return panel;
	}
	
	private Component getParComponent( final ConfigParameter par )
	{
		Component c = null;
		
		if( par != null )
		{
			try
			{
				ParameterType type = par.get_type();
				List< Object > options = par.getAllOptions();
				Object selectedValue = par.getSelectedValue();
				final String parId = par.get_ID().getID();

				if( selectedValue != null )
				{
					switch ( type )
					{
						case NUMBER:
						{
							if( options.isEmpty() )
							{
								NumberRange rng = par.getNumberRange();
	
								if( rng == null )
								{
									rng = new NumberRange( Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY );
								}
	
								Number value = (Number)selectedValue;
								
								JSpinner sp = new JSpinner();
								SpinnerNumberModel model = new SpinnerNumberModel( value, rng.getMin(), rng.getMax(), 1 );
								sp.setModel( model );		
	
								sp.addChangeListener( new ChangeListener()
								{								
									@Override
									public void stateChanged(ChangeEvent e)
									{
										JSpinner sp = (JSpinner)e.getSource();
	
										try
										{
											par.setSelectedValue( (Number)sp.getValue() );
										} 
										catch (ConfigParameterException e1)
										{
											e1.printStackTrace();
										}
									}
								});
								
								sp.addMouseWheelListener( new MouseWheelListener() 
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
	
								c = sp;
							}
							else
							{
								JComboBox< Number > combox = new JComboBox<Number>();
	
								for( Object val : options )
								{
									combox.addItem( (Number)val );
								}
								
								combox.setSelectedItem( selectedValue );
																
								combox.addActionListener( new ActionListener()
								{									
									@Override
									public void actionPerformed(ActionEvent arg0)
									{
										JComboBox cb = (JComboBox)arg0.getSource();
										
										Object select = cb.getSelectedItem();
										
										try
										{
											par.setSelectedValue( select );
										} 
										catch (ConfigParameterException ex)
										{
											ex.printStackTrace();
											
											JOptionPane.showMessageDialog( owner, ex.getCause() + "\n" + ex.getMessage()
																			, Language.getLocalCaption( Language.ERROR)
																			, JOptionPane.ERROR_MESSAGE );
										}
									}
								});
	
								c = combox;
							}					
	
							break;
						}
						case STRING:
						{
							if( options.isEmpty() )
							{	
								JTextField txt = new JTextField( );
																
								String text = selectedValue.toString();
									
								txt.setText( text );
								
								String toolTipText = "<html>";
								
								FontMetrics fm = txt.getFontMetrics( txt.getFont() );
								
								String[ ] songs = text.split( ConfigApp.SONG_LIST_SEPARATOR );
								
								if( songs != null )
								{
									Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
									
									String ttText = "";
									for( String song : songs )
									{
										ttText += song + ConfigApp.SONG_LIST_SEPARATOR;
										
										if( fm.stringWidth( ttText ) > d.width / 2 )
										{
											toolTipText += "<p>" + ttText + "</p>";
											
											ttText = "";
										}
									}
								
									if( !ttText.isEmpty() )
									{
										toolTipText += "<p>" + ttText + "</p>";
									}
								}
								
								toolTipText += "</html>";
								
								txt.setToolTipText( toolTipText );
								
								txt.getDocument().addDocumentListener( new DocumentListener()
								{									
									@Override
									public void removeUpdate(DocumentEvent e)
									{
										updateVal( e );
									}
									
									@Override
									public void insertUpdate(DocumentEvent e)
									{
										updateVal( e );
									}
									
									@Override
									public void changedUpdate(DocumentEvent e)
									{
										updateVal( e );
									}
									
									private void updateVal( DocumentEvent e )
									{
										try 
										{
											String desc = e.getDocument().getText( 0, e.getDocument().getLength() );
										
											par.setSelectedValue( desc );;
										}
										catch (Exception e1) 
										{
											e1.printStackTrace();
										}
									}
								});								
								
								c = txt;
							}
							else
							{
								JComboBox< String > combox = new JComboBox<String>();
	
								for( Object val : options )
								{
									combox.addItem( val.toString() );
								}
								
								combox.setSelectedItem( selectedValue.toString() );
																
								combox.addActionListener( new ActionListener()
								{									
									@Override
									public void actionPerformed(ActionEvent arg0)
									{
										JComboBox< String > cb = (JComboBox< String >)arg0.getSource();
										
										Object select = cb.getSelectedItem();
										
										try
										{
											par.setSelectedValue( select );
										} 
										catch (ConfigParameterException ex)
										{
											ex.printStackTrace();
											
											JOptionPane.showMessageDialog( owner, ex.getCause() + "\n" + ex.getMessage()
											, Language.getLocalCaption( Language.ERROR)
											, JOptionPane.ERROR_MESSAGE );
										}
										
										if( parId.equals( ConfigApp.LANGUAGE ) )
										{
											Object lang = cb.getSelectedItem();
											
											if( lang != null )
											{											
												if( !Language.getCurrentLanguage().equals( lang.toString() ) )
												{
													TranslateComponents.translate( lang.toString() );
												}
											}
											 
										}
									}
								});
	
								c = combox;						
							}
	
							break;
						}
						case BOOLEAN:
						{
							JCheckBox cb = new JCheckBox();
							cb.setSelected( (Boolean)selectedValue );
							
							cb.addActionListener( new ActionListener()
							{								
								@Override
								public void actionPerformed(ActionEvent arg0)
								{
									JCheckBox cb = (JCheckBox)arg0.getSource();
									
									try
									{
										par.setSelectedValue( cb.isSelected() );
									}
									catch (ConfigParameterException ex)
									{
										ex.printStackTrace();
										
										JOptionPane.showMessageDialog( owner, ex.getCause() + "\n" + ex.getMessage()
																	, Language.getLocalCaption( Language.ERROR)
																	, JOptionPane.ERROR_MESSAGE );
									}
								}
							});
							
							c = cb;
	
							break;
						}
						case COLOR:
						{	
							LinkedHashMap< Color, String > defaultColor = JColorComboBox.getColorNameTable();						
							LinkedHashMap< String, Color > colors = new LinkedHashMap<String, Color>();
	
							int selectedIndex = 0;
							for( int i = 0; i < options.size(); i++ )
							{
								Color col = (Color)options.get( i );
	
								String colorName = defaultColor.get( col );
	
								if( colorName == null )
								{
									colorName = "color " + i;								
								}
	
								colors.put( colorName, col );
								if( selectedValue != null && col.equals( selectedValue ) )
								{
									selectedIndex = i;
								}
							}
	
							JColorComboBox combox = new JColorComboBox( colors );
							combox.setSelectedIndex( selectedIndex );
							
							combox.addActionListener( new ActionListener()
							{									
								@Override
								public void actionPerformed(ActionEvent arg0)
								{
									JColorComboBox cb = (JColorComboBox)arg0.getSource();
									
									String selectColorName = cb.getSelectedItem().toString();
									
									Color select = cb.getColorTable().get( selectColorName );
									
									try
									{
										par.setSelectedValue( select );
									} 
									catch (ConfigParameterException ex)
									{
										ex.printStackTrace();
										
										JOptionPane.showMessageDialog( owner, ex.getCause() + "\n" + ex.getMessage()
																		, Language.getLocalCaption( Language.ERROR)
																		, JOptionPane.ERROR_MESSAGE );
									}
								}
							});
							
							c = combox;
	
							break;						
						}
						default:
						{
							break;
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return c;
	}
}

