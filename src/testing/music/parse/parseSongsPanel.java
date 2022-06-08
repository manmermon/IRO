package testing.music.parse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;

import gui.game.screen.level.music.BackgroundMusic;
import gui.panel.inputDevice.DataPanel;
import config.ConfigApp;
import config.language.Language;
import config.language.TranslateComponents;
import control.music.MusicPlayerControl;
import general.Tuple;
import image.BasicPainter2D;
import music.sheet.IROTrack;
import music.sheet.io.IROMusicParserListener;
import tools.MusicSheetTools;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;


public class parseSongsPanel extends JPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4212736396179567663L;
	
	private static parseSongsPanel ssp = null;
	
	private JPanel contentPanel;
	private JPanel panelMusicList;
	private JPanel panelSessionSongsInfo;
	private JPanel panelSongInfo;
	private JPanel panelTotalSongTimeInfo;
	
	private JTable tableSongList;

	private JButton btPlayStopSong;
	
	private JLabel lbSongInfo;
	private JLabel lbTotalSongTimeInfo;	
	
	private Pattern pattern = null;
	
	
	private long sessionTime = 0L;
	private DataPanel drawMidiPanel;
	private JLabel lblCanva;
	
	public static parseSongsPanel getInstance()
	{
		if( ssp == null )
		{
			ssp = new parseSongsPanel();
		}
		
		return ssp;
	}
	
	private parseSongsPanel( )
	{		
		setLayout( new BorderLayout() );
		
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
			}
		}
		catch( Exception e)
		{	
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
			contentPanel.setBackground(Color.WHITE);
			this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPanel.setLayout(new BorderLayout(0, 0));
						
			this.contentPanel.add( this.getMusicListPanel(), BorderLayout.WEST );
			contentPanel.add(getLblCanva(), BorderLayout.CENTER);
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
		}
		
		return this.panelMusicList;
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
			
			this.tableSongList.setName( "tableSongList" );
			
			this.addSelectionListenerTable( this.tableSongList );			
		}
		
		return this.tableSongList;
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
							
							IROMusicParserListener tool = new IROMusicParserListener();
							MidiParser parser = new MidiParser();
							parser.addParserListener( tool );
							long t = 0;
							try 
							{
								parser.parse( MidiSystem.getSequence( midiMusicSheelFile ) );
								t = (long)( tool.getSheet().getDuration() * 1_000_000 ); //micros
							}
							catch (InvalidMidiDataException | IOException e) 
							{
							}
							
							double wq = 1 ;//MusicSheetTools.getWholeTempo2Second( tool.getSheet().getTempo() );
							
							List< Tuple< String, List< Double > > > times = new ArrayList< Tuple< String, List< Double > > >();
							
							for( IROTrack tracks :  tool.getSheet().getTracks() )
							{
								List< Double > ts = new ArrayList< Double >();
								for( Double v : tracks.getTrackNotes().keySet() )
								{
									ts.add( v );
								}
								
								Tuple< String, List< Double > > tt = new Tuple< String, List< Double > >( tracks.getID(), ts );
								
								times.add( tt );
							}
							
							drawTracks( times );
							
							System.out.println(
									"SelectSongPanel.addSelectionListenerTable(...).new ListSelectionListener() {...}.valueChanged()  " + table.getName() +" A " + ( System.nanoTime() - t ) / 1e9D );
							t = System.nanoTime();
							
							//long millis = MusicSheetTools.getSongTime( pattern );
							long millis = MusicSheetTools.getSongTime( midiMusicSheelFile );
							
							System.out.println(
									"SelectSongPanel.addSelectionListenerTable(...).new ListSelectionListener() {...}.valueChanged()  " + table.getName() +" B " + ( System.nanoTime() - t ) / 1e9D );
							
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
	
	
	private void drawTracks( List< Tuple< String, List< Double > > > times )
	{
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		double ct = 0;
		for( Tuple< String, List< Double > > ts : times )
		{			
			XYSeries serie = new XYSeries( ts.t1  );
		
			for( Double d : ts.t2 )
			{
				serie.add( d.doubleValue(), ct );
			}
			
			dataset.addSeries( serie );
			
			ct++;
		}
		
		JFreeChart scatterPlot = ChartFactory.createScatterPlot( "Tracks", "Time (s)", "Track", dataset );
		
		Rectangle r = getLblCanva().getBounds();
		//Insets pad = getLblCanva().getBorder().getBorderInsets(getLblCanva());
		int w = r.width; //- pad.left - pad.right;
		int h = r.height; // - pad.top - pad.bottom;
		if ((w > 0) && (h > 0))
		{
			Image img = BasicPainter2D.createEmptyCanva( w, h, null );//chart.createBufferedImage(w, h);
			
			scatterPlot.draw( (Graphics2D)img.getGraphics(), 
						new Rectangle2D.Double( 0, 0, img.getWidth( null ), img.getHeight( null ) ) );
			
			getLblCanva().setIcon(new ImageIcon(img));
		}
	}
	
	private JLabel getLblCanva() {
		if (lblCanva == null) {
			lblCanva = new JLabel("");
		}
		return lblCanva;
	}
}
