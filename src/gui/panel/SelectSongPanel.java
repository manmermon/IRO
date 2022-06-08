package gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jfugue.pattern.Pattern;

import gui.GameManager;
import GUI.dialog.InfoDialog;
import gui.game.screen.level.music.BackgroundMusic;
import config.ConfigApp;
import config.ConfigParameter;
import config.Player;
import config.Settings;
import config.language.Caption;
import config.language.Language;
import config.language.TranslateComponents;
import control.music.MusicPlayerControl;
import exceptions.ConfigParameterException;
import image.BasicPainter2D;
import image.icon.GeneralAppIcon;
import tools.MusicSheetTools;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

public class SelectSongPanel extends JPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4212736396179567663L;
	
	private static SelectSongPanel ssp = null;
	
	private JPanel contentPanel;
	private JPanel panelSelectControl;
	private JPanel panelMusicList;
	private JPanel panelSelectedSongs;
	private JPanel panelUpDownControl;
	private JPanel panelMoveCtr;
	private JPanel panelSessionSongsInfo;
	private JPanel panelSongInfo;
	private JPanel panelTotalSongTimeInfo;
	
	private JTable tableSongList;
	private JTable tableSelectedSongList;

	private JButton buttonSelect;
	private JButton buttonRemove;
	private JButton btnClear;
	private JButton buttonUp;
	private JButton buttonDown;
	private JButton btPlayStopSong;
	
	private JLabel lbSongInfo;
	private JLabel lbTotalSongTimeInfo;	
	
	private Pattern pattern = null;
	
	
	private long sessionTime = 0L;
	
	public static SelectSongPanel getInstance()
	{
		if( ssp == null )
		{
			ssp = new SelectSongPanel();
		}
		
		return ssp;
	}
	
	private SelectSongPanel( )
	{		
		super.setLayout( new BorderLayout() );
		
		this.add( this.getSessiongSongsInfoPanel(), BorderLayout.NORTH);
		this.add( this.getContainerPanel(), BorderLayout.CENTER);
		
		File f = new File( ConfigApp.SONG_FILE_PATH );
		
		try
		{
			JTable t = this.gettableSongList();
			DefaultTableModel tm = (DefaultTableModel)t.getModel();
			
			FilenameFilter filter = new FilenameFilter() 
			{
			    @Override
			    public boolean accept(File dir, String name) {
			        return name.endsWith(".mid");
			    }
			};
			
			File[] files = f.listFiles( filter );
			
			if( files != null && files.length > 0 )
			{
				String[] filePaths = new String[ files.length ];
				for( int i = 0; i < files.length; i++ )
				{
						filePaths[ i ] = files[ i ].getName();
				}
				
				Arrays.sort( filePaths, String.CASE_INSENSITIVE_ORDER );
				
				for( String file : filePaths )
				{											
					tm.addRow( new String[] { file } );
				}						
				
				//t.setRowSelectionInterval( 0, 0 );
				//moveSong( t, getSelectedSongTable(), false );				
				//copySong( t, getSelectedSongTable(), false );
				
				//this.updateSelectedSong();				
			}
		}
		catch( Exception e)
		{	
		}
	}
	
	public void updateSelectedSong()
	{
		JTable t = this.gettableSongList();
		JTable selectedSongTable = getSelectedSongTable();
		
		Player player = ConfigApp.getFirstPlayer();
		
		Settings cfg = null;
		
		if( player != null )
		{
			cfg = ConfigApp.getPlayerSetting( player );
		}
		
		if( cfg != null )
		{
			ConfigParameter par = cfg.getParameter( ConfigApp.SONG_LIST );
		
			Object songs = par.getSelectedValue();
			
			DefaultTableModel tm = (DefaultTableModel)selectedSongTable.getModel();
			
			if( songs != null )
			{
				String songList = songs.toString().trim().replaceAll( "\\s+", "" );
				
				if( !songList.isEmpty() )
				{	
					tm.setRowCount( 0 );
					
					String[] list = songList.split( ConfigApp.SONG_LIST_SEPARATOR );
										
					for( String s : list )
					{
						boolean isIn = false;
						
						/*
						checkValueInTable:
						for( int r = 0; r < selectedSongTable.getRowCount(); r++ )
						{
							for( int c = 0; c < selectedSongTable.getColumnCount(); c++ )
							{
								isIn = selectedSongTable.getValueAt( r, c ).equals( s );
								
								if( isIn )
								{
									break checkValueInTable;
								}
							}
						}
						*/
							
						if( !isIn )
						{
							File fs = new File( s );
							for( int i = 0; i < t.getRowCount(); i++ )
							{
								String tVal = ConfigApp.SONG_FILE_PATH + t.getValueAt( i, 0 ).toString();
								File fv = new File( tVal );
								if( fv.getAbsolutePath().equals( fs.getAbsolutePath() ) )
								{
									t.clearSelection();
									t.addRowSelectionInterval( i, i );
									//moveSong( t, getSelectedSongTable(), false );
									copySong( t, selectedSongTable );
									
									break;
								}
							}
						}
					}
				}
			}
			else
			{
				getBtnClear().doClick();
			}
		}
	}
	
	private JPanel getSessiongSongsInfoPanel()
	{
		if( this.panelSessionSongsInfo == null )
		{
			this.panelSessionSongsInfo = new JPanel( new BorderLayout() );
			
			this.panelSessionSongsInfo.add( this.getCurrentSongInfoPanel(), BorderLayout.CENTER );
			this.panelSessionSongsInfo.add( this.getTotalSongTimeInfoPanel(), BorderLayout.EAST );
		}
		
		return this.panelSessionSongsInfo;
	}
	
	private JPanel getCurrentSongInfoPanel()
	{
		if( this.panelSongInfo == null )
		{
			this.panelSongInfo = new JPanel( new BorderLayout() );
			
			this.panelSongInfo.setBorder( BorderFactory.createTitledBorder( Language.getLocalCaption( Language.SONG ) ) );
			
			TranslateComponents.add( this.panelSongInfo, Language.getAllCaptions().get( Language.SONG ) );	
			
			this.panelSongInfo.add( this.getPlayStopSongButton(), BorderLayout.WEST);
			this.panelSongInfo.add( this.getSongInfoLabel(), BorderLayout.CENTER );
		}
		
		return this.panelSongInfo;
		
	}
	
	private JPanel getTotalSongTimeInfoPanel()
	{
		if( this.panelTotalSongTimeInfo == null )
		{
			this.panelTotalSongTimeInfo = new JPanel( new FlowLayout() );
			
			this.panelTotalSongTimeInfo.setBorder( BorderFactory.createTitledBorder( Language.getLocalCaption( Language.SESSION ) ) );
			
			TranslateComponents.add( this.panelTotalSongTimeInfo, Language.getAllCaptions().get( Language.SESSION ) );	
			
			this.panelTotalSongTimeInfo.add( this.getTotalSongTimeLabel() );
		}
		
		return this.panelTotalSongTimeInfo;
	}
	
	private JLabel getTotalSongTimeLabel()
	{
		if( this.lbTotalSongTimeInfo == null )
		{
			this.lbTotalSongTimeInfo = new JLabel();
						
			this.updateSessionTime();
		}
		
		return this.lbTotalSongTimeInfo;
	}
	
	private void updateSessionTime()
	{
		String time = this.time2Text( this.sessionTime );
		this.lbTotalSongTimeInfo.setText( time );
	}
	
	private JLabel getSongInfoLabel()
	{
		if( this.lbSongInfo == null )
		{
			this.lbSongInfo = new JLabel();
		}
		
		return this.lbSongInfo;
	}
	
	private JButton getPlayStopSongButton()
	{
		if( this.btPlayStopSong == null )
		{
			this.btPlayStopSong = new JButton();
			
			this.btPlayStopSong.setIcon( new ImageIcon( BasicPainter2D.triangle( 16, 1, Color.BLACK, Color.GREEN,  BasicPainter2D.EAST ) ) );
			
			this.btPlayStopSong.setBorder( BorderFactory.createRaisedSoftBevelBorder() );
			
			this.btPlayStopSong.addActionListener( new ActionListener()
			{				
				private Pattern patternCopy = null;
				private boolean played = false;
				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					JButton b = (JButton)e.getSource();
					
					if( pattern != null )
					{
						BackgroundMusic bgm = new BackgroundMusic();
						try
						{
							bgm.setPattern( pattern );
						}
						catch (MidiUnavailableException | InvalidMidiDataException ex1)
						{
							ex1.printStackTrace();
						}
						
						boolean play = ( patternCopy == null ) 
										|| ( pattern != patternCopy );								
						patternCopy = pattern;
						
						try
						{							
							MusicPlayerControl.getInstance().stopMusic();
							b.setIcon( new ImageIcon( BasicPainter2D.triangle( 16, 1, Color.BLACK, Color.GREEN, BasicPainter2D.EAST ) ) );
							
							if( !played || play )
							{	
								b.setIcon( new ImageIcon( BasicPainter2D.rectangle( 16, 16, 1, Color.BLACK, Color.ORANGE ) ) );
								
								MusicPlayerControl.getInstance().setBackgroundMusicPatter( bgm );
								MusicPlayerControl.getInstance().startMusic();
								
								played = true;
							}
							else
							{
								played = false;
							}
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}						 
					}
				}
			});
		}
		
		return this.btPlayStopSong;
	}
		
	private JPanel getContainerPanel()
	{
		if( this.contentPanel == null )
		{
			this.contentPanel = new JPanel();
			this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			
			this.contentPanel.setLayout( new GridLayout( 0, 2 ) );
						
			this.contentPanel.add( this.getMusicListPanel() );
			this.contentPanel.add( this.getSelectControl() );
		}
		
		return this.contentPanel;
	}

	private JPanel getMusicListPanel()
	{
		if( this.panelMusicList == null )
		{
			this.panelMusicList = new JPanel( new BorderLayout() );
			
			JPanel panel = new JPanel( new BorderLayout() );
			panel.add( this.gettableSongList() , BorderLayout.CENTER );
			
			JScrollPane scroll = new JScrollPane( panel );
			scroll.setBorder( BorderFactory.createTitledBorder( Language.getLocalCaption( Language.MUSIC_LIST ) ) );
			scroll.setBackground( Color.WHITE );
			
			TranslateComponents.add( scroll, Language.getAllCaptions().get( Language.MUSIC_LIST ) );
			
			this.panelMusicList.add( scroll, BorderLayout.CENTER );
			this.panelMusicList.add( this.getPanelControl(), BorderLayout.EAST );
		}
		
		return this.panelMusicList;
	}

	private JPanel getSelectControl()
	{
		if( this.panelSelectControl == null )
		{
			this.panelSelectControl = new JPanel( new BorderLayout() );
			
			this.panelSelectControl.add( new JScrollPane( this.getSelectedSongs() ), BorderLayout.CENTER );
			this.panelSelectControl.add( this.getJPanelUpDownCtrl(), BorderLayout.EAST );
		}
		
		return this.panelSelectControl;
	}
	
	private JPanel getJPanelUpDownCtrl()
	{
		if( this.panelUpDownControl == null )
		{
			this.panelUpDownControl = new JPanel();
			
			BoxLayout ly = new BoxLayout( panelUpDownControl, BoxLayout.Y_AXIS);
			panelUpDownControl.setLayout( ly );
			
			panelUpDownControl.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5));
			
			panelUpDownControl.add( this.getBtnUp() );
			panelUpDownControl.add( Box.createRigidArea( new Dimension( 5, 5 ) ));
			panelUpDownControl.add( this.getBtnDown());
		}
		
		return this.panelUpDownControl;
	}
	
	private JButton getBtnUp()
	{
		if( this.buttonUp == null )
		{
			this.buttonUp = new JButton( );
			
			try
			{
				ImageIcon icon = new ImageIcon( BasicPainter2D.triangle( 16,  2, Color.BLACK
														, Color.LIGHT_GRAY, BasicPainter2D.NORTH ) );
				
				Dimension d = new Dimension( icon.getIconWidth(), icon.getIconHeight() );
				d.width += 6;
				d.height += 6;
				this.buttonUp.setPreferredSize( d );
				this.buttonUp.setIcon( icon );
			}
			catch( Exception ex )
			{
				Caption cap = Language.getAllCaptions().get(  Language.UP );
				this.buttonUp.setText( cap.getCaption( Language.getCurrentLanguage() ) );
				TranslateComponents.add( this.buttonUp, cap );
			}
			
			this.buttonUp.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			this.buttonUp.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					JTable t = getSelectedSongTable();
					moveSong( t, -1 );
				}
			});
		}
		
		return this.buttonUp;
	}
	
	private JButton getBtnDown()
	{
		if( this.buttonDown == null )
		{
			this.buttonDown = new JButton();
			
			try
			{
				ImageIcon icon = new ImageIcon( BasicPainter2D.triangle( 16,  2, Color.BLACK
														, Color.LIGHT_GRAY, BasicPainter2D.SOUTH ) );
				
				Dimension d = new Dimension( icon.getIconWidth(), icon.getIconHeight() );
				d.width += 6;
				d.height += 6;
				this.buttonDown.setPreferredSize( d );
				this.buttonDown.setIcon( icon );
			}
			catch( Exception ex )
			{
				Caption cap = Language.getAllCaptions().get( Language.DOWN );
				this.buttonDown.setText( cap.getCaption( Language.getCurrentLanguage() ) );
				TranslateComponents.add( this.buttonDown, cap );
			}
			
			this.buttonDown.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			this.buttonDown.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					JTable t = getSelectedSongTable();
					moveSong( t, 1 );
				}
			});
		}
		
		return this.buttonDown;
	}
	
	private JPanel getSelectedSongs()
	{
		if( this.panelSelectedSongs == null )
		{
			this.panelSelectedSongs = new JPanel( new BorderLayout() );
			this.panelSelectedSongs.setBorder(BorderFactory.createTitledBorder( Language.getLocalCaption( Language.SELECTED_SONG_LIST ) ) );
			this.panelSelectedSongs.setBackground( Color.WHITE );
			
			TranslateComponents.add( this.panelSelectedSongs, Language.getAllCaptions().get(  Language.SELECTED_SONG_LIST ) );

			
			this.panelSelectedSongs.add( this.getSelectedSongTable(), BorderLayout.CENTER );
		}
		
		return this.panelSelectedSongs;
	}
	
	private JTable getCreateJTable()
	{
		JTable table =  new JTable()
						{
							private static final long serialVersionUID = 1L;
			
							//Implement table cell tool tips.           
				            public String getToolTipText( MouseEvent e) 
				            {
				                String tip = null;
				                Point p = e.getPoint();
				                int rowIndex = rowAtPoint(p);
				                int colIndex = columnAtPoint(p);
				
				                try 
				                {
				                    tip = getValueAt(rowIndex, colIndex).toString();
				                }
				                catch ( RuntimeException e1 )
				                {
				                    //catch null pointer exception if mouse is over an empty line
				                }
				
				                return tip;
				            }				            
				        };
				        
		table.setDefaultRenderer( Object.class, new DefaultTableCellRenderer()
											{	
												public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
											    {
											        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
											        	
											        if( !table.isCellEditable( row, column ) )
											        {	
											        	cellComponent.setBackground( new Color( 255, 255, 224 ) );
											        	
											        	if( isSelected )
											        	{
											        		cellComponent.setBackground( new Color( 0, 120, 215 ) );
											        	}
											        	else
											        	{
											        		cellComponent.setBackground( Color.WHITE );
											        	}
											        }
											        
											        cellComponent.setForeground( Color.BLACK );
											        
											        return cellComponent;
											    }
											});
		
		table.getTableHeader().setReorderingAllowed( false );
		
		return table;
	}
	
	private TableModel createTablemodel( )
	{					
		TableModel tm =  new DefaultTableModel( null, new String[] { Language.getLocalCaption( Language.SONG ) } )
							{
								private static final long serialVersionUID = 1L;
								
								Class[] columnTypes = new Class[]{ String.class };								
								boolean[] columnEditables = new boolean[] { false };
								
								public Class getColumnClass(int columnIndex) 
								{
									return columnTypes[columnIndex];
								}
																								
								public boolean isCellEditable(int row, int column) 
								{
									boolean editable = columnEditables[ column ];
									
									return editable;
								}
							};
		return tm;
	}

	private JTable gettableSongList()
	{
		if( this.tableSongList == null )
		{	
			this.tableSongList = this.getCreateJTable();
			this.tableSongList.setModel( this.createTablemodel() );
			
			TranslateComponents.add( this.tableSongList.getTableHeader(), Language.getAllCaptions().get(  Language.SONG ) );
						
			this.tableSongList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			
			this.tableSongList.setPreferredScrollableViewportSize( this.tableSongList.getPreferredSize() );
			this.tableSongList.setFillsViewportHeight( true );
			
			this.tableSongList.setName( "tableSongList");
			
			this.addSelectionListenerTable( this.tableSongList );
			
		}
		
		return this.tableSongList;
	}	
	
	private JTable getSelectedSongTable()
	{
		if( this.tableSelectedSongList == null )
		{	
			this.tableSelectedSongList = this.getCreateJTable();
			this.tableSelectedSongList.setModel( this.createTablemodel() );
			
			TranslateComponents.add( this.tableSelectedSongList.getTableHeader(), Language.getAllCaptions().get(  Language.SONG ) );
			
			this.tableSelectedSongList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			
			this.tableSelectedSongList.setPreferredScrollableViewportSize( this.tableSelectedSongList.getPreferredSize() );
			this.tableSelectedSongList.setFillsViewportHeight( true );
			
			this.tableSelectedSongList.setName( "tableSelectedSongList");
			
			this.addSelectionListenerTable( this.tableSelectedSongList );
			
			this.tableSelectedSongList.getModel().addTableModelListener( new TableModelListener()
			{	
				@Override
				public void tableChanged(TableModelEvent arg0)
				{
					DefaultTableModel tm = (DefaultTableModel)arg0.getSource();
					
					String songs = "";
					
					for( int  i = 0; i < tm.getRowCount(); i++ )
					{
						songs += ConfigApp.SONG_FILE_PATH + tm.getValueAt( i, 0 ).toString() + ConfigApp.SONG_LIST_SEPARATOR; 
					}
					
					Player player = ConfigApp.getFirstPlayer();					
					Settings cfg = null;
					
					if( player != null )
					{
						cfg = ConfigApp.getPlayerSetting( player );
					}
							
					if( cfg != null )
					{
						ConfigParameter par = cfg.getParameter( ConfigApp.SONG_LIST );
												
						try
						{
							par.setSelectedValue( songs );
						}
						catch (ConfigParameterException ex)
						{
							ex.printStackTrace();
						}
					}										
				}
			});
			
		}
		
		return this.tableSelectedSongList;
	}
	
	//*
	private long getSongTime( File midiMusicSheelFile ) throws Exception
	{			
		return MusicSheetTools.getSongTime( midiMusicSheelFile );
	}
	//*/
	
	private void addSelectionListenerTable( final JTable table)
	{
		if( table != null )
		{
			table.getSelectionModel().addListSelectionListener( new ListSelectionListener()
			{	
				@Override
				public void valueChanged(ListSelectionEvent arg0)
				{	
					int sel = table.getSelectedRow();
					
					if( MusicPlayerControl.getInstance().isPlay() )
					{
						getPlayStopSongButton().doClick();
					}
					
					if( sel >= 0 && arg0.getValueIsAdjusting() )
					{
						String info = "";
						try
						{
							String song = ConfigApp.SONG_FILE_PATH + table.getValueAt( sel, 0 ).toString();
							
							File midiMusicSheelFile = new File( song );
							
							pattern = MusicSheetTools.getPatternFromMidi( midiMusicSheelFile );
							
							//long millis = MusicSheetTools.getSongTime( pattern );
							long millis = MusicSheetTools.getSongTime( midiMusicSheelFile );
							
							String time = time2Text( millis );
						
							/*
							info = midiMusicSheelFile.getName() + "; " + time + "; " + Language.getLocalCaption( Language.TRACK ) 
									+ " " + sequence.getTracks().length;
							*/
							info = midiMusicSheelFile.getName() + "; " + time + "; ";
							
						}
						catch( Exception ex )
						{	
							ex.printStackTrace();
						}
						finally
						{
							getSongInfoLabel().setText( "    " + info );
						}
					}
				}
			});
		}
	}
	
	private String time2Text( long millis )
	{	
		String time = String.format("%02d:%02d:%02d", 
								TimeUnit.MICROSECONDS.toHours(millis),
								TimeUnit.MICROSECONDS.toMinutes(millis) -  
								TimeUnit.HOURS.toMinutes(TimeUnit.MICROSECONDS.toHours(millis)), // The change is in this line
								TimeUnit.MICROSECONDS.toSeconds(millis) - 
								TimeUnit.MINUTES.toSeconds(TimeUnit.MICROSECONDS.toMinutes(millis)));
		
		return time;
	}
	
	private JPanel getPanelControl() 
	{
		if (panelMoveCtr == null) 
		{
			panelMoveCtr = new JPanel();
			
			BoxLayout ly = new BoxLayout(panelMoveCtr, BoxLayout.Y_AXIS);
			panelMoveCtr.setLayout( ly );
			
			panelMoveCtr.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5));
			
			panelMoveCtr.add(getButtonSelect());
			panelMoveCtr.add( Box.createRigidArea( new Dimension( 5, 5 ) ));
			panelMoveCtr.add(getButtonRemove());
			panelMoveCtr.add( Box.createRigidArea( new Dimension( 5, 5 ) ));
			panelMoveCtr.add(getBtnClear());
		}
		return panelMoveCtr;
	}
	
	private JButton getButtonSelect() 
	{
		if (buttonSelect == null) 
		{
			buttonSelect = new JButton( );
			
			try
			{
				ImageIcon icon = new ImageIcon( BasicPainter2D.triangle( 16,  2, Color.BLACK
						, Color.LIGHT_GRAY, BasicPainter2D.EAST ) );

				Dimension d = new Dimension( icon.getIconWidth(), icon.getIconHeight() );
				d.width += 6;
				d.height += 6;
				this.buttonSelect.setPreferredSize( d );
				
				this.buttonSelect.setIcon( icon );
			}
			catch( Exception ex )
			{
				this.buttonSelect.setText( ">>" );
			}
			
			buttonSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			this.buttonSelect.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					JTable tSelectedSong = getSelectedSongTable();
					JTable tSongList = gettableSongList();
					
					//moveSong( tSongList, tSelectedSong, false );				
					copySong( tSongList, tSelectedSong );
				}
			});
		}
		return buttonSelect;
	}
	
	private JButton getButtonRemove() 
	{
		if (buttonRemove == null) 
		{
			buttonRemove = new JButton(  );
			
			try
			{
				ImageIcon icon = new ImageIcon( BasicPainter2D.triangle( 16,  2, Color.BLACK
						, Color.LIGHT_GRAY, BasicPainter2D.WEST ) );

				Dimension d = new Dimension( icon.getIconWidth(), icon.getIconHeight() );
				d.width += 6;
				d.height += 6;
				this.buttonRemove.setPreferredSize( d );
				
				this.buttonRemove.setIcon( icon );
			}
			catch( Exception ex )
			{
				this.buttonSelect.setText( "<<" );
			}
			
			buttonRemove.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			this.buttonRemove.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					JTable tSelectedSong = getSelectedSongTable();
					
					DefaultTableModel tSongList = (DefaultTableModel)gettableSongList().getModel();
										
					if( tSelectedSong.getRowCount() > 0 )
					{
						int[] rows = tSelectedSong.getSelectedRows();
						Arrays.sort( rows );
						
						DefaultTableModel tm = (DefaultTableModel)tSelectedSong.getModel();
						
						TreeSet< String > fileErrorList = new TreeSet<  String >();
						
						for( int i = rows.length - 1; i >= 0; i-- )
						{
							String song = ConfigApp.SONG_FILE_PATH + tm.getValueAt( rows[ i ], 0 ).toString() ;
							
							try
							{	
								sessionTime -= getSongTime( new File( song ) );
							}
							catch (Exception e) 
							{
								fileErrorList.add( song );
							}
							
							tm.removeRow( rows[ i ] );							
						}			
						
						if( !fileErrorList.isEmpty() )
						{
							for( String f : fileErrorList )
							{
								for( int i = 0; i < tSongList.getRowCount(); i++ )
								{
									String v = tSongList.getValueAt( i, 0 ).toString().toLowerCase();
									
									if( v.equals( f.toLowerCase() ) )
									{
										tSongList.removeRow( i );
										break;
									}
								}
								
								for( int i = tm.getRowCount() - 1; i >= 0; i-- )
								{
									String v = tm.getValueAt( i, 0 ).toString().toLowerCase();
									
									if( v.equals( f.toLowerCase() ) )
									{
										tSongList.removeRow( i );
									}
								}
							}
								
							sessionTime = 0;
							for( int i = 0; i < tm.getRowCount(); i++ )
							{
								String song = tm.getValueAt( rows[ i ], 0 ).toString() ;
								try 
								{
									sessionTime += getSongTime( new File( song ) );
								} 
								catch (Exception e) 
								{
								}								
							}
							
							InfoDialog dg = new InfoDialog( GameManager.getInstance().getCurrentWindow()
															, Language.getLocalCaption( Language.FILE_ERROR ), true );
							Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
							d.width /= 2;
							d.height /= 2;
							dg.setSize( d );
							dg.setVisible( true );
							 
						}
						
						updateSessionTime();
					}
				}
			});
		}
		return buttonRemove;
	}
	
	private JButton getBtnClear() 
	{
		if (btnClear == null) 
		{
			btnClear = new JButton( );
			
			try
			{
				this.btnClear.setIcon( GeneralAppIcon.clear( 16, Color.BLACK ) );
			}
			catch( Exception ex )
			{	
				Caption cap = Language.getAllCaptions().get( Language.CLEAR );
			
				this.btnClear.setText( cap.getCaption( Language.getCurrentLanguage() ) );
				
				TranslateComponents.add( this.btnClear, cap );
			}
			
			btnClear.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			this.btnClear.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					JTable tsel = getSelectedSongTable();
					//JTable tlist = gettableSongList();
					
					if( tsel.getRowCount() > 0 )
					{
						tsel.addRowSelectionInterval( 0, tsel.getRowCount() - 1 );
						
						//moveSong( tsel, tlist, true );
						DefaultTableModel tm = (DefaultTableModel)tsel.getModel();
						for( int r = tm.getRowCount() - 1; r >= 0; r-- )
						{
							tm.removeRow( r );
						}
					
						sessionTime = 0;
						updateSessionTime();
					}
				}
			});
		}
		return btnClear;
	}
	
	/*
	private void moveSong( JTable source, JTable dest, boolean sortDest )
	{
		DefaultTableModel tmSource = (DefaultTableModel)source.getModel();
		DefaultTableModel tmDest = (DefaultTableModel)dest.getModel();
		
		int[] selIndex = source.getSelectedRows();
		Arrays.sort( selIndex );
		
		if( selIndex.length > 0 )
		{
			String[] songs = null;
			if( sortDest )
			{
				songs = new String[ tmDest.getRowCount() + selIndex.length ];
				for( int i = 0; i < tmDest.getRowCount(); i++ )
				{					
					songs[ i ] = tmDest.getValueAt( i, 0 ).toString();
				}				
			}
			
			for( int i = selIndex.length - 1; i >= 0; i-- )
			{
				int index = selIndex[ i ];
				
				String song = source.getValueAt( index, 0 ).toString();
			
				if( !sortDest )
				{
					tmDest.addRow( new String[] { song } );
				}
				else
				{
					songs[ dest.getRowCount() + i ] = song;
				}
				
				tmSource.removeRow( index );
			}
			
			if( songs != null )
			{
				Arrays.sort( songs, String.CASE_INSENSITIVE_ORDER );
				for( int i = 0; i < songs.length; i++ )
				{
					if( i < tmDest.getRowCount() )
					{
						tmDest.setValueAt( songs[ i ], i, 0 );
					}
					else
					{
						tmDest.addRow( new String[] { songs[ i ] } );
					}
				}
			}
		}
	}
	*/
	
	private void copySong( JTable source, JTable dest )
	{
		DefaultTableModel tmDest = (DefaultTableModel)dest.getModel();
		
		int[] selIndex = source.getSelectedRows();
		Arrays.sort( selIndex );
		
		if( selIndex.length > 0 )
		{			
			for( int i = selIndex.length - 1; i >= 0; i-- )
			{
				int index = selIndex[ i ];
				
				String song = source.getValueAt( index, 0 ).toString();
			
				try 
				{
					this.sessionTime += this.getSongTime( new File( ConfigApp.SONG_FILE_PATH +  song ) );
					
					tmDest.addRow( new String[] { song } );
				} 
				catch (Exception e) 
				{
					InfoDialog d = new InfoDialog( GameManager.getInstance().getCurrentWindow(), e.getMessage() + "\n" + e.getCause(), true );
					Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
					size.width /= 2;
					size.height /= 2;
					d.setSize( size );
					d.setVisible( true );
				}								
			}
			
			this.updateSessionTime();
		}
	}
	
	
	private void moveSong( JTable source, int shift )
	{
		DefaultTableModel tmSource = (DefaultTableModel)source.getModel();
		
		int dir = 1;
		if( shift < 0 )
		{
			dir = -1;
		}
		
		int[] selIndex = source.getSelectedRows();
		Arrays.sort( selIndex );
		
		int ref = 0;
		int from = 0; 
		int to = selIndex.length;
		
		if( dir < 0 )
		{
			for( int i = 0; i < selIndex.length; i++ )
			{
				if( ref == selIndex[ i ] )
				{
					from++;
					ref++;
				}
				else
				{
					break;
				}
			}
		}
		else
		{
			ref = source.getRowCount() -1;
			for( int i = selIndex.length - 1; i >= 0; i-- )
			{
				if( ref == selIndex[ i ] )
				{
					to--;
					ref--;
				}
				else
				{
					break;
				}
			}
		}		
		
		if( from < to )
		{		
			selIndex = Arrays.copyOfRange( selIndex, from, to );
			
			if( selIndex.length > 0 )
			{
				for( int i = selIndex.length - 1; i >= 0; i-- )
				{
					int index = selIndex[ i ];
					int row = index + dir;
						
					String song = source.getValueAt( index, 0 ).toString();
					
					if( row >= 0 && row < source.getRowCount() )
					{				
						tmSource.removeRow( index );
						tmSource.insertRow( row, new String[] { song } );
					}
					
					if( i == 0 )
					{
						source.clearSelection();
						source.addRowSelectionInterval( row, row );
					}
				}
			}
		}
	}

}
