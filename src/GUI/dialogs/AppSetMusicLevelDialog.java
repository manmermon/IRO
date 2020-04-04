package GUI.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import config.ConfigApp;
import config.ConfigParameter;
import config.language.Language;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;

public class AppSetMusicLevelDialog extends JDialog
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
	
	private JTable tableSongList;
	private JTable tableSelectedSongList;

	private JButton buttonSelect;
	private JButton buttonRemove;
	private JButton btnClear;
	private JButton buttonUp;
	private JButton buttonDown;
	
	/*
	public static void main(String[] args)
	{
		try
		{
			AppSetMusicLevelDialog dialog = new AppSetMusicLevelDialog();
			dialog.setVisible(true);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	//*/

	/**
	 * Create the dialog.
	 */
	public AppSetMusicLevelDialog( Window owner, Rectangle screen )
	{
		super( owner );
		
		if( screen == null )
		{
			super.setBounds(100, 100, 450, 300);
		}
		else
		{
			super.setBounds( screen );
		}
		
		super.setTitle( Language.getLocalCaption( Language.SELECT ) 
						+ " " + Language.getLocalCaption( Language.SONG ) );
		
		super.setModal( true );
		super.getContentPane().setLayout( new BorderLayout() );
		super.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		getContentPane().add( this.getContainerPanel(), BorderLayout.CENTER);
		
		File f = new File( "./sheets/" );
		
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
			
			for( File file : files )
			{				
				tm.addRow( new String[] { file.getPath() } );
			}			
			
			ConfigParameter par = ConfigApp.getProperty( ConfigApp.SONG_LIST );
			
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
								moveSong( t, getSelectedSongTable() );
								
								break;
							}
						}
					}
				}
			}
		}
		catch( Exception e)
		{	
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
			this.panelMusicList = new JPanel( new BorderLayout());
			
			JPanel panel = new JPanel( new BorderLayout() );
			panel.add( this.gettableSongList() , BorderLayout.CENTER );
			
			JScrollPane scroll = new JScrollPane( panel );
			scroll.setBorder(BorderFactory.createTitledBorder( Language.getLocalCaption( Language.MUSIC_LIST ) ) );
			scroll.setBackground( Color.WHITE );
			
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
			
			BoxLayout ly = new BoxLayout(panelUpDownControl, BoxLayout.Y_AXIS);
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
			this.buttonUp = new JButton( "Up" );
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
			this.buttonDown = new JButton( "Down" );
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
		TableModel tm =  new DefaultTableModel( null, new String[] {  Language.getLocalCaption( Language.SONG )} )
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

			/*
			FontMetrics fm = this.tableSongList.getFontMetrics( this.tableSongList.getFont() );			
			String hCol0 = this.tableSongList.getColumnModel().getColumn( 0 ).getHeaderValue().toString();
			
			int s = fm.stringWidth( " " + hCol0 + " " ) * 2;
			this.tableSongList.getColumnModel().getColumn( 0 ).setResizable( false );
			this.tableSongList.getColumnModel().getColumn( 0 ).setPreferredWidth( s );
			this.tableSongList.getColumnModel().getColumn( 0 ).setMaxWidth( s );
			//*/
						
			this.tableSongList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			
			this.tableSongList.setPreferredScrollableViewportSize( this.tableSongList.getPreferredSize() );
			this.tableSongList.setFillsViewportHeight( true );
			
		}
		
		return this.tableSongList;
	}	
	
	private JTable getSelectedSongTable()
	{
		if( this.tableSelectedSongList == null )
		{	
			this.tableSelectedSongList = this.getCreateJTable();
			this.tableSelectedSongList.setModel( this.createTablemodel() );
			
			/*
			FontMetrics fm = this.tableSelectedSongList.getFontMetrics( this.tableSelectedSongList.getFont() );			
			String hCol0 = this.tableSelectedSongList.getColumnModel().getColumn( 0 ).getHeaderValue().toString();
			
			int s = fm.stringWidth( " " + hCol0 + " " ) * 2;
			this.tableSelectedSongList.getColumnModel().getColumn( 0 ).setResizable( false );
			this.tableSelectedSongList.getColumnModel().getColumn( 0 ).setPreferredWidth( s );
			this.tableSelectedSongList.getColumnModel().getColumn( 0 ).setMaxWidth( s );
			//*/
						
			this.tableSelectedSongList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			
			this.tableSelectedSongList.setPreferredScrollableViewportSize( this.tableSelectedSongList.getPreferredSize() );
			this.tableSelectedSongList.setFillsViewportHeight( true );
			
		}
		
		return this.tableSelectedSongList;
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
			buttonSelect = new JButton(">>");
			buttonSelect.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			this.buttonSelect.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					JTable tSelectedSong = getSelectedSongTable();
					JTable tSongList = gettableSongList();
					
					moveSong( tSongList, tSelectedSong );				
				}
			});
		}
		return buttonSelect;
	}
	
	private JButton getButtonRemove() 
	{
		if (buttonRemove == null) 
		{
			buttonRemove = new JButton("<<");
			buttonRemove.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			this.buttonRemove.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					JTable tSelectedSong = getSelectedSongTable();
					JTable tSongList = gettableSongList();
					
					moveSong( tSelectedSong, tSongList );
				}
			});
		}
		return buttonRemove;
	}
	
	private JButton getBtnClear() 
	{
		if (btnClear == null) 
		{
			btnClear = new JButton("clear");
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
						
						moveSong( tsel, tlist );					
					}
				}
			});
		}
		return btnClear;
	}
	
	private void moveSong( JTable source, JTable dest )
	{
		DefaultTableModel tmSource = (DefaultTableModel)source.getModel();
		DefaultTableModel tmDest = (DefaultTableModel)dest.getModel();
		
		int[] selIndex = source.getSelectedRows();
		Arrays.sort( selIndex );
		
		if( selIndex.length > 0 )
		{
			for( int i = selIndex.length - 1; i >= 0; i-- )
			{
				int index = selIndex[ i ];
				
				String song = source.getValueAt( index, 0 ).toString();
				
				tmDest.addRow( new String[] { song } );
				
				tmSource.removeRow( index );
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
