package GUI.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import GUI.JColorComboBox;
import config.ConfigApp;
import config.ConfigParameter;
import config.ConfigParameter.ParameterType;
import config.User;
import config.language.Language;
import db.sqlite.DBSQLite;
import exceptions.ConfigParameterException;
import general.NumberRange;
import general.Tuple;

public class AppSettingDialog extends JDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1219995574372606332L;
	
	private JPanel containerPanel = null;
	
	private JScrollPane scroll = null;

	private JButton btnSelectPlayer = null;
	
	/**
	 * Create the dialog.
	 */
	public AppSettingDialog( JFrame owner, Rectangle screenLoc )
	{		
		super( owner );
		super.setModal( true );
		super.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		
		if( screenLoc == null )
		{
			screenLoc = new Rectangle( 450, 300 );
		}
		
		super.setBounds( screenLoc );
		super.getContentPane().setLayout( new BorderLayout() );
		
		super.getContentPane().add( this.getContainerScroll(), BorderLayout.CENTER );
		
		super.addWindowListener( new WindowAdapter()
		{	
			@Override
			public void windowClosing(WindowEvent arg0)
			{
				saveSetting();
			}
		});
	}

	private JScrollPane getContainerScroll()	
	{
		if( this.scroll == null )
		{
			this.scroll = new JScrollPane( this.getContainerPanel() );
			
			this.scroll.setVisible( true );
		}
		
		return this.scroll;
	}
	
	private JPanel getContainerPanel()
	{
		if( this.containerPanel == null )
		{
			this.containerPanel = new JPanel();
			
			this.containerPanel.setLayout( new BorderLayout() );
			
			updateUser();
		}
		
		return this.containerPanel;
	}
	
	private void updateUser()
	{
		JPanel container =  this.getContainerPanel();
		container.setVisible( false );
		container.removeAll();
		
		int cols = 2;
		
		JPanel subContainerPanel = new JPanel();
		GridLayout ly = new GridLayout( 0, cols );
		
		subContainerPanel.setLayout( ly );
		subContainerPanel.setBorder( new EmptyBorder(5, 5, 5, 5) );
		
		Collection< ConfigParameter > pars = ConfigApp.getParameters();
		
		List< JPanel > listPanels = new ArrayList<JPanel>();
		
		JPanel containerOddPanel = new JPanel( new BorderLayout() );
		JPanel containerEvenPanel = new JPanel( new BorderLayout() );
		
		subContainerPanel.add( containerEvenPanel);
		subContainerPanel.add( containerOddPanel );
		
		JPanel panelEven = new JPanel();
		JPanel panelOdd = new JPanel();
		
		panelEven.setLayout( new BoxLayout( panelEven, BoxLayout.Y_AXIS ) );
		panelOdd.setLayout( new BoxLayout( panelOdd, BoxLayout.Y_AXIS ) );
		
		containerEvenPanel.add( panelEven, BorderLayout.NORTH );
		containerOddPanel.add( panelOdd, BorderLayout.NORTH );
		
		listPanels.add( panelEven );
		listPanels.add( panelOdd );
		
		int numPars = 0;
		for( ConfigParameter p : pars )
		{
			if( p.get_type() != ParameterType.USER )
			{
				int i = numPars % listPanels.size();
				
				JPanel panel = listPanels.get( i );
				
				panel.add( this.getParamenterPanel( p ) );
				
				numPars++;
			}
		}
		
		JPanel userPanel = new JPanel( new BorderLayout() );
					
		userPanel.add( this.getUserLabel(), BorderLayout.CENTER );
		userPanel.add( this.getButtonSelect(), BorderLayout.EAST );
		
		container.add( userPanel, BorderLayout.NORTH );
		container.add( subContainerPanel, BorderLayout.CENTER );
		
		container.setVisible( true );
	}
	
	private JPanel getUserLabel()
	{
		ConfigParameter par = ConfigApp.getProperty( ConfigApp.USER );
		final User player;
		if( par != null )
		{
			player = (User)par.getSelectedValue();
		}
		else
		{
			 player = new User();
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
		
		final JDialog dialog = this;
		btImg.addActionListener( new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JButton b = (JButton)arg0.getSource();
				
				User user = (User)ConfigApp.getProperty( ConfigApp.USER ).getSelectedValue();
				if( user.getId() != User.ANONYMOUS_USER_ID )
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

					int returnVal = jfc.showDialog( dialog, null);
					
					if( returnVal == JFileChooser.APPROVE_OPTION )
					{
						File img = jfc.getSelectedFile();
						
						try
						{
							BufferedImage newImg = ImageIO.read( img );
							
							player.getImg().setImage( newImg );
							
							DBSQLite db = new DBSQLite();
							
							db.updateUser( player );
							
							b.setIcon( player.getImg( ConfigApp.playerPicSizeIcon.x, ConfigApp.playerPicSizeIcon.y ) );
						}
						catch ( SQLException | IOException e) 
						{
							JOptionPane.showMessageDialog( dialog, e.getMessage() 
															, Language.getLocalCaption( Language.ERROR )
															, JOptionPane.ERROR_MESSAGE );
						}
						
					}
				}
			}
		});
				
		panel.add( btImg );
		panel.add( userName );
		
		return panel;
	}
	
	private JButton getButtonSelect()
	{
		if( this.btnSelectPlayer == null )
		{
			this.btnSelectPlayer = new JButton( Language.getLocalCaption( Language.PLAYER ) );
			
			final JDialog dialog = this; 
			this.btnSelectPlayer.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					AppSelectPlayer selplayerDialog = new AppSelectPlayer( dialog );
					
					selplayerDialog.setBounds( dialog.getBounds() );
					selplayerDialog.setVisible( true );
					
					User user = selplayerDialog.getSelectedUser();
					
					if( user != null ) 
					{					
						ConfigParameter parUser = ConfigApp.getProperty( ConfigApp.USER );
						
						User currentUser = (User)parUser.getSelectedValue();
						
						try
						{
							if( currentUser.getId() != user.getId() )
							{
								parUser.clear();
								parUser.add( user );
							
								DBSQLite db = new DBSQLite();
															
								try
								{
									List< Tuple< String, Object > > settings = db.getUserConfig( user.getId() );
									
									if( settings.isEmpty() )
									{
										ConfigApp.loadDefaultProperties();
										parUser = ConfigApp.getProperty( ConfigApp.USER );
										parUser.clear();
										parUser.add( user );
										
										if( user.getId() != User.ANONYMOUS_USER_ID  )
										{
											db.insertUserConfig( user.getId() );
										}
									}
									else
									{
										for( Tuple< String, Object > par : settings )
										{
											Object val = par.y;
											
											ConfigParameter p = ConfigApp.getProperty( par.x );
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
								catch ( SQLException ex) 
								{
									ex.printStackTrace();
								}
								finally 
								{
									updateUser();
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
		
		return this.btnSelectPlayer;
	}
	
	private JPanel getParamenterPanel( ConfigParameter par )
	{		
		JPanel panel = new JPanel();
		/*
		Dimension d  = new Dimension( fieldSize );
		
		
		panel.setPreferredSize( d );
		panel.setBackground( Color.GREEN);
		//*/
		
		if( par != null )
		{	
			ConfigParameter lang = ConfigApp.getProperty( ConfigApp.LANGUAGE );
				
			String l = Language.getCurrentLanguage();
			
			Object languages = lang.getSelectedValue();
			if( languages != null )
			{
				l = languages.toString();
			}
			
			//panel.setLayout( new BorderLayout() );
			panel.setLayout( new GridLayout( 0, 1 ) );
			
			String title = par.get_ID().getCaption( l );			
			panel.setBorder( BorderFactory.createTitledBorder( title ));
						
			Component comp = this.getParComponent( par );
			//panel.add( comp, BorderLayout.CENTER );			
			panel.add( comp );
			
			Dimension d = super.getSize();
			Dimension dc = comp.getPreferredSize();
			Dimension size = panel.getPreferredSize();
			
			size.width = d.width / 3;
			size.height = dc.height > size.height ? dc.height : size.height;
			
			panel.setPreferredSize( size );
		}
		
		panel.setVisible( true );
		
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
				List< Object > values = par.getAllValues();
				Object selectedValue = par.getSelectedValue();
				String parId = par.get_ID().getID();

				if( values != null )
				{
					switch ( type )
					{
						case NUMBER:
						{
							if( values.size() == 1 )
							{
								NumberRange rng = par.getNumberRange();
	
								if( rng == null )
								{
									rng = new NumberRange( Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY );
								}
	
								Number value = (Number)values.get( 0 );
								if( selectedValue != null )
								{
									value = (Number)selectedValue;
								}
								
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
											par.clear();
											par.add( (Number)sp.getValue() );
										} 
										catch (ConfigParameterException e1)
										{
											e1.printStackTrace();
										}
									}
								});
	
								c = sp;
							}
							else
							{
								JComboBox< Number > combox = new JComboBox<Number>();
	
								for( Object val : values )
								{
									combox.addItem( (Number)val );
								}
								
								if( selectedValue != null )
								{
									combox.setSelectedItem( selectedValue );
								}	
								else
								{
									combox.setSelectedIndex( 0 );
								}
								
								combox.addActionListener( new ActionListener()
								{									
									@Override
									public void actionPerformed(ActionEvent arg0)
									{
										JComboBox cb = (JComboBox)arg0.getSource();
										
										int selectIndex = cb.getSelectedIndex();
										
										par.setSelectedValue( selectIndex );
									}
								});
	
								c = combox;
							}					
	
							break;
						}
						case STRING:
						{
							if( values.size() < 2 )
							{	
								JTextField txt = new JTextField( );
																
								String text = "";
								
								if( selectedValue != null )
								{
									text = selectedValue.toString();
								}
								else if( values.size() == 1 )
								{
									text = values.get( 0 ).toString();
								}
	
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
										
											par.clear();
											par.add( desc );
										}
										catch (Exception e1) 
										{
											e1.printStackTrace();
										}
									}
								});
								
								if( parId.equals( ConfigApp.SONG_LIST ) )
								{
									txt.setEditable( false );
									
									final JDialog dg = this;
									txt.addMouseListener( new MouseAdapter()
									{
										@Override
										public void mouseReleased(MouseEvent e)
										{
											JTextField tf = (JTextField)e.getSource();
											
											Rectangle bound = new Rectangle();
											
											Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
											bound.width = d.width / 2;
											bound.height = d.height / 2;
											bound.x = bound.width / 2;
											bound.y = bound.height / 2;
											
											AppSetMusicLevelDialog musicDialog = new AppSetMusicLevelDialog( dg, bound );
											
											musicDialog.setVisible( true );
											
											String[] songs = musicDialog.getSongList();

											String songlist = "";
											String toolTipText = "<html>";
											
											FontMetrics fm = tf.getFontMetrics( tf.getFont() );
											
											if( songs != null )
											{
												String ttText = "";
												for( String song : songs )
												{
													songlist += song + ConfigApp.SONG_LIST_SEPARATOR;
													ttText += song + ConfigApp.SONG_LIST_SEPARATOR;
													
													if( fm.stringWidth( ttText ) > bound.width )
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
											
											tf.setText( songlist );
											tf.setToolTipText( toolTipText );
											
											ConfigParameter par = ConfigApp.getProperty( ConfigApp.SONG_LIST );
											
											par.clear();
											try
											{
												par.add( songlist );
											} 
											catch (ConfigParameterException e1)
											{
												// TODO Auto-generated catch block
												e1.printStackTrace();
												
												JOptionPane.showMessageDialog( dg, e1.getMessage()
																				, Language.getLocalCaption( Language.ERROR )
																				, JOptionPane.ERROR_MESSAGE );
											}
										}
										
									});
								}
								
								c = txt;
							}
							else
							{
								JComboBox< String > combox = new JComboBox<String>();
	
								for( Object val : values )
								{
									combox.addItem( val.toString() );
								}
								
								if (selectedValue == null )
								{
									combox.setSelectedIndex( 0 );
								}
								else
								{
									combox.setSelectedItem( selectedValue.toString() );
								}
								
								combox.addActionListener( new ActionListener()
								{									
									@Override
									public void actionPerformed(ActionEvent arg0)
									{
										JComboBox cb = (JComboBox)arg0.getSource();
										
										int selectIndex = cb.getSelectedIndex();
										
										par.setSelectedValue( selectIndex );
									}
								});
	
								c = combox;						
							}
	
							break;
						}
						case BOOLEAN:
						{
							JCheckBox cb = new JCheckBox();
							cb.setSelected( (Boolean)values.get( 0 ) );
							if( selectedValue != null )
							{
								cb.setSelected( (Boolean)selectedValue );
							}
	
							cb.addActionListener( new ActionListener()
							{								
								@Override
								public void actionPerformed(ActionEvent arg0)
								{
									JCheckBox cb = (JCheckBox)arg0.getSource();
									
									try
									{
										par.clear();
										par.add( cb.isSelected() );
									}
									catch (ConfigParameterException e)
									{
										e.printStackTrace();
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
							for( int i = 0; i < values.size(); i++ )
							{
								Color col = (Color)values.get( i );
	
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
									JComboBox cb = (JComboBox)arg0.getSource();
									
									int selectIndex = cb.getSelectedIndex();
									
									par.setSelectedValue( selectIndex );
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

	private void saveSetting()
	{
		ConfigParameter par = ConfigApp.getProperty( ConfigApp.USER );
		User currentUser = (User)par.getSelectedValue();
		if( currentUser.getId() != User.ANONYMOUS_USER_ID )
		{	
			DBSQLite db = new DBSQLite();			
			try
			{
				db.updateUserConfig( currentUser.getId() );
			} 
			catch (SQLException e)
			{
				e.printStackTrace();
				
				JOptionPane.showMessageDialog( this, e.getCause() + "\n" + e.getMessage()
												, Language.getLocalCaption( Language.ERROR)
												, JOptionPane.ERROR_MESSAGE );
			}
		}
	}
}

