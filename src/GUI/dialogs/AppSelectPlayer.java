package GUI.dialogs;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import config.ConfigApp;
import config.Player;
import config.language.Language;

import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class AppSelectPlayer extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3214710279388548632L;

	private JPanel panelSelect = null;
	
	private JButton btnSelect = null;
	private JPanel panelUsers;
	private JTable userTable;
	
	private List< Integer > playerIDs = new ArrayList< Integer >();

	private JPanel panelSelectUser;
	private JPanel panelAddRemoveUser;
	private JButton btAddUser;
	private JButton btnRemoveUser;
	private JButton btCancel;
	
	/**
	 * Create the dialog.
	 */
	public AppSelectPlayer( Window owner )
	{
		super( owner );

		super.setTitle( Language.getLocalCaption( Language.SELECT )
						+ " " + Language.getLocalCaption( Language.PLAYER ) );
		
		super.setModal( true );
		super.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		super.setBounds( 100, 100, 450, 300 );
		super.getContentPane().setLayout(new BorderLayout(0, 0));
				
		super.getContentPane().add( this.getPanelSelectAction(), BorderLayout.SOUTH);
		
		JScrollPane scroll = new JScrollPane( this.getPanelUsers() );
		scroll.getVerticalScrollBar().setUnitIncrement( ConfigApp.playerPicSizeIcon.x / 2 );
		super.getContentPane().add( scroll, BorderLayout.CENTER);
		
		try
		{
			List< Player > allPlayers = ConfigApp.getAllPlayersDB();			
			Set< Player > selectedPlayer = ConfigApp.getPlayers();
			
			for( Player player : allPlayers )
			{
				if( !player.isAnonymous() && !selectedPlayer.contains( player ) )
				{
					this.addUserToTable( player );
				}
			}
		}
		catch (SQLException | IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addUserToTable( Player user )
	{
		if( user != null )
		{
			JTable t = getUserTable();
			DefaultTableModel tm = (DefaultTableModel)t.getModel();
			
			ImageIcon icon = user.getImg();
			if( icon != null )
			{
				Image img = icon.getImage();
				
				if( img.getWidth( null ) != ConfigApp.playerPicSizeIcon.x 
						|| img.getHeight( null ) != ConfigApp.playerPicSizeIcon.y )
				{
					icon = new ImageIcon( img.getScaledInstance( ConfigApp.playerPicSizeIcon.x
																, ConfigApp.playerPicSizeIcon.x
																, Image.SCALE_SMOOTH ) );	
				}
			}
			
			Object[] row = new Object[ ] { user.getId(), user.getName(), icon };
			tm.addRow( row );
		}
	}
	
	public List< Player > getSelectedPlayers()
	{
		List< Player > players = new ArrayList<Player>();
		try
		{
			for( Integer id : this.playerIDs )
			{
				Player player = ConfigApp.getPlayerDB( id );
				
				if( player != null && !player.isAnonymous() )
				{
					players.add( player );
				}
			}
		} 
		catch (SQLException | IOException e)
		{
			e.printStackTrace();
		}
		
		return players;
	}
	
	private JPanel getPanelSelectAction()
	{
		if( this.panelSelect == null )
		{
			this.panelSelect = new JPanel( );
			panelSelect.setLayout(new BorderLayout(0, 0));
			panelSelect.add(getPanelSelectUser(), BorderLayout.CENTER);
			panelSelect.add(getPanelRemoveUser(), BorderLayout.WEST);
		}
		
		return this.panelSelect;
	}
	
	private JButton getBtnSelect()
	{
		if( this.btnSelect == null )
		{
			this.btnSelect = new JButton( Language.getLocalCaption( Language.SELECT ) );
			
			this.btnSelect.addActionListener( new ActionListener()
			{	
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					JTable t = getUserTable();
					int[] sel = t.getSelectedRows();
					
					if( sel.length > 0 )
					{
						for( int s : sel )
						{
							playerIDs.add(  (int)t.getValueAt( s, 0 ) );
						}
					}
					dispose();
				}
			});
		}
		
		return this.btnSelect;
	}
	
	private JPanel getPanelUsers() 
	{
		if ( this.panelUsers == null) 
		{
			this.panelUsers = new JPanel();
			this.panelUsers.setLayout(new BorderLayout(0, 0));
			
			this.panelUsers.add( this.getUserTable().getTableHeader(), BorderLayout.NORTH );
			this.panelUsers.add( this.getUserTable(), BorderLayout.CENTER );
		}
		return this.panelUsers;
	}
	
	private JTable getUserTable()
	{
		if( this.userTable == null )
		{	
			this.userTable = this.getCreateJTable();
			DefaultTableModel tm = (DefaultTableModel)this.createTablemodel();
			this.userTable.setModel( tm );
						
			this.userTable.setRowHeight( ConfigApp.playerPicSizeIcon.x + 4 );
			this.userTable.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			
			TableColumnModel cmodel = this.userTable.getColumnModel();
			
			for( int i = 0; i < cmodel.getColumnCount(); i++ )
			{
				TableColumn tc = cmodel.getColumn( i );
				Class colClass = tm.getColumnClass( i );
				
				Integer s = null;
				
				if( Number.class.isAssignableFrom( colClass ) )
				{
					FontMetrics fm = this.userTable.getFontMetrics( this.userTable.getFont() );
					
					s = fm.stringWidth( "9999" ) ;
				}
				else if( colClass.isAssignableFrom( ImageIcon.class ) )
				{
					s = ConfigApp.playerPicSizeIcon.x + 4;
				}
				
				tc.setResizable( false );
				
				if( s != null )
				{	
					tc.setPreferredWidth( s );
					tc.setMaxWidth( s );
				}
			}
		}
		
		return this.userTable;
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
		DefaultTableModel tm =  new DefaultTableModel( null, new String[] {  "id", Language.getLocalCaption( Language.PLAYER ), Language.getLocalCaption( ( Language.ICON ) ) } )
							{
								private static final long serialVersionUID = 1L;
								
								Class[] columnTypes = new Class[]{ Integer.class, String.class, Icon.class };								
								boolean[] columnEditables = new boolean[] { false, false, false };
																
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
	
	private JPanel getPanelSelectUser() 
	{
		if (panelSelectUser == null) 
		{
			panelSelectUser = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panelSelectUser.getLayout();
			flowLayout.setAlignment(FlowLayout.RIGHT);
			panelSelectUser.add(getBtnSelect());
			panelSelectUser.add(getBtCancel());
		}
		return panelSelectUser;
	}
	
	private JPanel getPanelRemoveUser() 
	{
		if (panelAddRemoveUser == null) 
		{
			panelAddRemoveUser = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panelAddRemoveUser.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			
			panelAddRemoveUser.add( this.getBtNewUser() );
			panelAddRemoveUser.add( this.getBtnRemoveUser() );
		}
		return panelAddRemoveUser;
	}
	
	private JButton getBtnRemoveUser() 
	{
		if (btnRemoveUser == null) 
		{
			btnRemoveUser = new JButton( Language.getLocalCaption( Language.REMOVE ) );
			
			final JDialog dg = this;			
			this.btnRemoveUser.addActionListener( new ActionListener()
			{	
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					int action = JOptionPane.showConfirmDialog( dg, Language.getLocalCaption( Language.REMOVE_PLAYER_MSG )
													, Language.getLocalCaption( Language.WARNING )
													,	JOptionPane.WARNING_MESSAGE );
					
					if( action == JOptionPane.OK_OPTION )
					{
						JTable t = getUserTable();
						DefaultTableModel tm = (DefaultTableModel)t.getModel();
						
						int[] sel = t.getSelectedRows();
						Arrays.sort( sel );
						
						if( sel.length > 0 )
						{
							for( int i = sel.length - 1; i >= 0;  i-- )
							{
								int s = sel[ i ];
								
								int playerID = (int)t.getValueAt( s, 0 );
								
								try
								{
									if( playerID != Player.ANONYMOUS_PLAYER_ID )
									{
										ConfigApp.delPlayerDB( playerID );
										tm.removeRow( s );
									}
								} 
								catch (SQLException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
									JOptionPane.showMessageDialog( dg, e.getMessage()
																	, Language.getLocalCaption( Language.ERROR )
																	, JOptionPane.ERROR_MESSAGE );
								}
							}
						}
						
						t.clearSelection();
					}
				}
			});
		}
		return btnRemoveUser;
	}
	
	private JButton getBtCancel() 
	{
		if (btCancel == null) 
		{
			btCancel = new JButton( Language.getLocalCaption( Language.CANCEL ) );
			
			btCancel.addActionListener( new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					playerIDs.clear();
					dispose();
				}
			});
		}
		return btCancel;
	}
	
	private JButton getBtNewUser() 
	{
		if (btAddUser == null) 
		{
			btAddUser = new JButton( Language.getLocalCaption( Language.NEW ) );
			
			final JDialog dg = this;
			btAddUser.addActionListener( new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					JButton b = (JButton)arg0.getSource();
					
					String userName = JOptionPane.showInputDialog( dg
																	, Language.getLocalCaption( Language.NAME )
																	, b.getText() );
					
					try
					{
						int userId = ConfigApp.addPlayerDB( userName, null );
						Player user = new Player( userId, userName, null); 
						
						addUserToTable( user );
					} 
					catch (SQLException e)
					{
						JOptionPane.showMessageDialog( dg, e.getMessage()
														, Language.getLocalCaption( Language.ERROR )
														, JOptionPane.ERROR_MESSAGE );
					}
					
				}
			});
		}
		return btAddUser;
	}
}
