package GUI.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
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

import org.jfugue.midi.MidiParser;
import org.jfugue.midi.MidiParserListener;
import org.jfugue.pattern.Pattern;
import org.staccato.StaccatoParser;
import org.staccato.StaccatoParserListener;

import GUI.game.screen.level.BackgroundMusic;
import config.ConfigApp;
import config.ConfigParameter;
import config.language.Language;
import config.language.TranslateComponents;
import control.music.MusicPlayerControl;
import exceptions.ConfigParameterException;
import image.basicPainter2D;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
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
	private JPanel contentPanel;
	private JPanel panelSelectControl;
	private JPanel panelMusicList;
	private JPanel panelSelectedSongs;
	private JPanel panelUpDownControl;
	private JPanel panelMoveCtr;
	private JPanel panelSongInfo;
	
	private JTable tableSongList;
	private JTable tableSelectedSongList;

	private JButton buttonSelect;
	private JButton buttonRemove;
	private JButton btnClear;
	private JButton buttonUp;
	private JButton buttonDown;
	private JButton btPlayStopSong;
	
	private JLabel lbSongInfo;
	
	private Pattern pattern = null;
	
	public SelectSongPanel( )
	{
		super.setLayout( new BorderLayout() );
		
		this.add( this.getSongInfoPanel(), BorderLayout.NORTH);
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
						filePaths[ i ] = files[ i ].getPath();
				}
				
				Arrays.sort( filePaths, String.CASE_INSENSITIVE_ORDER );
				
				for( String file : filePaths )
				{				
					tm.addRow( new String[] { file } );
				}						
				
				this.updateSelectedSong();
			}
		}
		catch( Exception e)
		{	
		}
	}
	
	private void updateSelectedSong()
	{
		JTable t = this.gettableSongList();
		
		ConfigParameter par = ConfigApp.getParameter( ConfigApp.SONG_LIST );
		
		Object songs = par.getSelectedValue();
		
		if( songs != null )
		{
			String songList = songs.toString().trim().replaceAll( "\\s+", "" );
			
			if( !songList.isEmpty() )
			{
				String[] list = songList.split( ConfigApp.SONG_LIST_SEPARATOR );
				
				for( String s : list )
				{
					for( int i = 0; i < t.getRowCount(); i++ )
					{
						String tVal = t.getValueAt( i, 0 ).toString();
						if( s.equals( tVal ) )
						{
							t.addRowSelectionInterval( i, i );
							moveSong( t, getSelectedSongTable(), false );
							
							break;
						}
					}
				}
			}
		}
	}
	
	public String[] getSongList()
	{
		String[] songs;
		
		JTable t = getSelectedSongTable();
		
		int rows = t.getRowCount();
		
		songs = new String[ rows ];
		
		for( int i = 0; i < rows; i++ )
		{
			songs[ i ] = t.getValueAt( i, 0 ).toString();
		}
		
		return songs;
	}
	
	private JPanel getSongInfoPanel()
	{
		if( this.panelSongInfo == null )
		{
			this.panelSongInfo = new JPanel( new BorderLayout() );
			
			this.panelSongInfo.setBorder( BorderFactory.createTitledBorder( Language.getLocalCaption( Language.SONG ) ) );
			
			TranslateComponents.add( this.panelSongInfo, Language.getAllCaptions().get( Language.SONG ) );	
			
			this.panelSongInfo.add( this.getSongInfoLabel(), BorderLayout.CENTER );
			this.panelSongInfo.add( this.getPlayStopSongButton(), BorderLayout.WEST);
		}
		
		return this.panelSongInfo;
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
			
			this.btPlayStopSong.setIcon( new ImageIcon( basicPainter2D.triangle( 16, 1, Color.BLACK, Color.GREEN,  basicPainter2D.EAST ) ) );
			
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
						bgm.setPattern( pattern );
						
						boolean play = ( patternCopy == null ) 
										|| ( pattern != patternCopy );								
						patternCopy = pattern;
						
						try
						{							
							MusicPlayerControl.getInstance().stopMusic();
							b.setIcon( new ImageIcon( basicPainter2D.triangle( 16, 1, Color.BLACK, Color.GREEN, basicPainter2D.EAST ) ) );
							
							if( !played || play )
							{	
								b.setIcon( new ImageIcon( basicPainter2D.rectangle( 16, 16, 1, Color.BLACK, Color.ORANGE ) ) );
								
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
							// TODO Auto-generated catch block
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
			//this.contentPanel.setLayout(new BoxLayout( this.contentPanel, BoxLayout.X_AXIS));
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
			this.buttonUp = new JButton( Language.getLocalCaption( Language.UP ) );
			
			TranslateComponents.add( this.buttonUp, Language.getAllCaptions().get(  Language.UP ) );
			
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
			this.buttonDown = new JButton( Language.getLocalCaption( Language.DOWN ) );
			
			TranslateComponents.add( this.buttonDown, Language.getAllCaptions().get(  Language.DOWN ) );
			
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
						songs += tm.getValueAt( i, 0 ).toString() + ConfigApp.SONG_LIST_SEPARATOR; 
					}
					
					ConfigParameter par = ConfigApp.getParameter( ConfigApp.SONG_LIST );
											
					try
					{
						par.setSelectedValue( songs );
					}
					catch (ConfigParameterException ex)
					{
						ex.printStackTrace();
					}
				}
			});
			
		}
		
		return this.tableSelectedSongList;
	}
	
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
					
					if( sel >= 0 )
					{
						String info = "";
						try
						{
							String song = table.getValueAt( sel, 0 ).toString();
							
							File midiMusicSheelFile = new File( song );
							
							StaccatoParserListener listener = new StaccatoParserListener();
							
							MidiParser parser = new MidiParser();
							parser.addParserListener( listener );
							parser.parse( MidiSystem.getSequence( midiMusicSheelFile ) );
							
							StaccatoParser staccatoParser = new StaccatoParser();
							MidiParserListener midiParserListener = new MidiParserListener();
							staccatoParser.addParserListener( midiParserListener );
							staccatoParser.parse( listener.getPattern() );
							
							pattern = listener.getPattern();
							
							Sequence sequence = midiParserListener.getSequence();
								
							long millis = sequence.getMicrosecondLength(); //micros
							String time = String.format("%02d:%02d:%02d", 
													TimeUnit.MICROSECONDS.toHours(millis),
													TimeUnit.MICROSECONDS.toMinutes(millis) -  
													TimeUnit.HOURS.toMinutes(TimeUnit.MICROSECONDS.toHours(millis)), // The change is in this line
													TimeUnit.MICROSECONDS.toSeconds(millis) - 
													TimeUnit.MINUTES.toSeconds(TimeUnit.MICROSECONDS.toMinutes(millis)));
						
							info = midiMusicSheelFile.getName() + "; " + time + "; " + Language.getLocalCaption( Language.TRACK ) 
									+ " " + sequence.getTracks().length;
							
						}
						catch( Exception ex )
						{							
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
			buttonSelect = new JButton( ">>" );
			buttonSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			this.buttonSelect.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					JTable tSelectedSong = getSelectedSongTable();
					JTable tSongList = gettableSongList();
					
					moveSong( tSongList, tSelectedSong, false );				
				}
			});
		}
		return buttonSelect;
	}
	
	private JButton getButtonRemove() 
	{
		if (buttonRemove == null) 
		{
			buttonRemove = new JButton( "<<" );
			buttonRemove.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			this.buttonRemove.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					JTable tSelectedSong = getSelectedSongTable();
					JTable tSongList = gettableSongList();
					
					moveSong( tSelectedSong, tSongList, true );
				}
			});
		}
		return buttonRemove;
	}
	
	private JButton getBtnClear() 
	{
		if (btnClear == null) 
		{
			btnClear = new JButton( Language.getLocalCaption( Language.CLEAR ) );
			
			TranslateComponents.add( this.btnClear, Language.getAllCaptions().get( Language.CLEAR ) );
			
			btnClear.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			this.btnClear.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					JTable tsel = getSelectedSongTable();
					JTable tlist = gettableSongList();
					
					if( tsel.getRowCount() > 0 )
					{
						tsel.addRowSelectionInterval( 0, tsel.getRowCount() - 1 );
						
						moveSong( tsel, tlist, true );					
					}
				}
			});
		}
		return btnClear;
	}
	
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
