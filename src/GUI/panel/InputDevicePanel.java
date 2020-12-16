/**
 * 
 */
package GUI.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import config.ConfigApp;
import config.ConfigParameter;
import config.ConfigParameter.ParameterType;
import config.Player;
import config.Settings;
import config.language.Caption;
import config.language.Language;
import config.language.TranslateComponents;
import control.controller.ControllerManager;
import control.controller.IControllerMetadata;
import control.controller.LSLStreams.LSLStreamMetadata;
import edu.ucsd.sccn.LSL;
import image.icon.GeneralAppIcon;

import javax.swing.JSplitPane;
import javax.swing.JTable;

/**
 * @author manuel merino
 *
 */
public class InputDevicePanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7603576052415241514L;

	private JPanel inputCtrPanel = null;
	
	private JPanel inputDevicePanel = null;
	
	private JButton btRefresh = null;
	private JSplitPane splitInputPane;
	private JPanel panelInputDeviceList;
	private JPanel panelInputValues;

	private JTable inputDeviceTable;
	
	private ControllerInputValuePanel inputValues = null;
	
	private LSL.StreamInfo[] lslStreamInfo = null;
		
	private static InputDevicePanel setInDevPanel = null;
	
	private static Window owner;
		
	private final Player NON_SELECTED_PLAYER = new Player( Player.ANONYMOUS - 1, " ", null );
		
	public static InputDevicePanel getInstance( Window wOwner )
	{
		owner = wOwner;
		
		if( setInDevPanel == null )
		{
			setInDevPanel = new InputDevicePanel();
		}
		
		return setInDevPanel;
	}
	
	private InputDevicePanel( )
	{			
		this.setLayout(new BorderLayout());		
		this.add(this.getInputControlPanel(), BorderLayout.NORTH);
		this.add( this.getInputDevicePanel(), BorderLayout.CENTER );
		
		this.updateInputs();
	}
		
	private JPanel getInputControlPanel()
	{
		if( this.inputCtrPanel == null )
		{
			this.inputCtrPanel = new JPanel();
			
			FlowLayout fl_contentPanel = new FlowLayout();
			fl_contentPanel.setAlignment(FlowLayout.RIGHT);
			this.inputCtrPanel.setLayout(fl_contentPanel);
		
			this.inputCtrPanel.add( this.getBtRefresh() );
		}
		
		return this.inputCtrPanel;
	}
	
	private JButton getBtRefresh()
	{
		if( this.btRefresh == null )
		{
			this.btRefresh = new JButton();
			
			Caption cap = Language.getAllCaptions().get( Language.UPDATE );
			
			this.btRefresh.setText( cap.getCaption( Language.getCurrentLanguage() ) );
			
			try
			{
				this.btRefresh.setIcon( GeneralAppIcon.Refresh( 20, 20, Color.BLACK, null));
			}
			catch (Exception ex) 
			{
			}
						
			this.btRefresh.addActionListener( new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					JButton b = (JButton)e.getSource();
					b.setEnabled( false );
					
					updateInputs();
					
					b.setEnabled( true );
				}
			});
			
			TranslateComponents.add( this.btRefresh, cap );
		}
		
		return this.btRefresh;
	}
	
	private JPanel getInputDevicePanel()
	{
		if( this.inputDevicePanel == null )
		{
			this.inputDevicePanel = new JPanel( new BorderLayout());
			this.inputDevicePanel.add( this.getSplitInputPane(), BorderLayout.CENTER );	
		}
		
		return this.inputDevicePanel;
	}
	
	private JSplitPane getSplitInputPane() 
	{
		if (splitInputPane == null) 
		{
			splitInputPane = new JSplitPane();
			splitInputPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			
			splitInputPane.setResizeWeight( 0.25 );
			
			splitInputPane.setLeftComponent( new JScrollPane( this.getPanelInputDeviceList() ) );
			splitInputPane.setRightComponent( new JScrollPane( this.getPanelInputValues() ) );
		}
		return splitInputPane;
	}
	
	private JPanel getPanelInputDeviceList() 
	{
		if (panelInputDeviceList == null) 
		{
			panelInputDeviceList = new JPanel();
			
			Caption caption = Language.getAllCaptions().get( Language.CONTROLLER );
			panelInputDeviceList.setBorder( BorderFactory.createTitledBorder( caption.getCaption( Language.getCurrentLanguage() ) ) );
			
			TranslateComponents.add( panelInputDeviceList, caption );
			
			panelInputDeviceList.setLayout( new BorderLayout() );
			panelInputDeviceList.add( this.getInputDeviceTable().getTableHeader(), BorderLayout.NORTH );
			panelInputDeviceList.add( this.getInputDeviceTable(), BorderLayout.CENTER );
		}
		return panelInputDeviceList;
	}
	
	private JPanel getPanelInputValues() 
	{
		if (panelInputValues == null) 
		{
			panelInputValues = new JPanel();			
		}
		return panelInputValues;
	}
	
	private JComboBox< Player > getPlayerCombobox( )
	{
		JComboBox< Player > cbb = new JComboBox<Player>();
		
		cbb.addItem( NON_SELECTED_PLAYER );
		for( Player player : ConfigApp.getPlayers() )
		{
			cbb.addItem( player );
		}
		
		return cbb;
	}
	
	public void updatePlayers()
	{
		List< Player > players = new ArrayList<Player>( ConfigApp.getPlayers() );
		
		JTable t = this.getInputDeviceTable();
		JTableHeader th = t.getTableHeader();
		TableColumnModel tcm = th.getColumnModel();				
		TableColumn tc = tcm.getColumn( 0 );
		DefaultCellEditor ed = (DefaultCellEditor)tc.getCellEditor();
		JComboBox< Player > cbb = (JComboBox< Player >)ed.getComponent();
		
		for( int i = cbb.getItemCount() - 1; i >= 0; i-- )
		{
			Player p = cbb.getItemAt( i );
			
			if( !p.equals( NON_SELECTED_PLAYER ) )
			{
				if( !players.contains( p ) )
				{
					cbb.removeItemAt( i );
					checkPlayerController( p, -1 );
				}
				else				
				{
					players.remove( p );
				}
			}
		}
		
		for( Player p : players )
		{
			cbb.addItem( p );
		}
	}
	
	private JTable getInputDeviceTable()
	{
		if( this.inputDeviceTable == null )
		{	
			this.inputDeviceTable = this.getCreateJTable();
			TableModel tm = this.createTablemodel();
			this.inputDeviceTable.setModel( tm );
			
			FontMetrics fm = this.inputDeviceTable.getFontMetrics( this.inputDeviceTable.getFont() );
			
			JTableHeader th = this.inputDeviceTable.getTableHeader();			
			th.setReorderingAllowed( false );

			TableColumnModel tcm = th.getColumnModel();
			
			for( int i = 0; i < tcm.getColumnCount() - 1; i++ )
			{
				TableColumn tc = tcm.getColumn( i );
				tc.setResizable( false );
				
				String h = tc.getHeaderValue().toString();
				int s = fm.stringWidth( h ) + 10;
				
				if( i == 0 )
				{
					tc.setCellEditor( new DefaultCellEditor( getPlayerCombobox() ) );
					s *= 2;
				}
				
				tc.setMaxWidth( s );
				tc.setPreferredWidth( s );
			}
			
			this.inputDeviceTable.getModel().addTableModelListener( new TableModelListener()
			{				
				@Override
				public void tableChanged(TableModelEvent ev )
				{
					if( ev.getType() == TableModelEvent.UPDATE )
					{
						JTable t = inputDeviceTable;
					
						int row = ev.getFirstRow();
						int col = ev.getColumn();
						
						if( row >= 0 && col >= 0 )
						{
							if( col == 0 )
							{
								LSL.StreamInfo info = lslStreamInfo[ row ];
								
								Player player = (Player)t.getValueAt( row, col );
							
								if( player.getId() != NON_SELECTED_PLAYER.getId() )
								{
									checkPlayerController( player, row );
								}
								
								updatePlayerControllerSetting( player, info );
							}
						}
					}
				}
			});
			
			this.inputDeviceTable.getSelectionModel().addListSelectionListener( new ListSelectionListener()
			{				
				@Override
				public void valueChanged(ListSelectionEvent e)
				{
					JTable t = getInputDeviceTable();
					int sel = t.getSelectedRow();
					LSL.StreamInfo info = null;
					if( sel >= 0 && sel < lslStreamInfo.length )
					{
						info = lslStreamInfo[ sel ];
					}
					
					showInputControllerInfo( info );
				}
			});			
		}
		
		return this.inputDeviceTable;
	}
	
	private void updatePlayerControllerSetting( Player player, LSL.StreamInfo ctr )
	{
		try
		{
			for( Player pl : ConfigApp.getPlayers() )
			{
				Settings setplayer = ConfigApp.getPlayerSetting( pl );			
				ConfigParameter par = setplayer.getParameter( ConfigApp.SELECTED_CONTROLLER );


				if( par == null )
				{
					Caption cap = Language.getAllCaptions().get( Language.CONTROLLER );
					cap.setID( ConfigApp.SELECTED_CONTROLLER );

					par = new ConfigParameter( cap, ParameterType.OTHER );											
				}

				if( !pl.equals( player ) )
				{
					Object val = par.getSelectedValue();
					if( val != null )
					{											
						IControllerMetadata cmeta = (IControllerMetadata)val;

						if( cmeta.getControllerID().equals( ctr.uid() ) )
						{
							par.removeSelectedValue();
						}
					}
				}
				else
				{
					IControllerMetadata meta = new LSLStreamMetadata( ctr );
					par.setSelectedValue( meta );
				}
			}
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
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
											        }
											        
											        cellComponent.setForeground( Color.BLACK );
											        
											        return cellComponent;
											    }
											});
		
		table.getTableHeader().setReorderingAllowed( false );
		
		return table;
	}
	
	private Class[] getTableColumnTypes()
	{
		return new Class[]{ Player.class, Integer.class, String.class };
	}
	
	private TableModel createTablemodel( )
	{					
		TableModel tm =  new DefaultTableModel( null, new String[] { Language.getLocalCaption( Language.PLAYER )
																	, Language.getLocalCaption( Language.CHANNELS )
																	, Language.getLocalCaption( Language.INPUT ) } )
							{
								private static final long serialVersionUID = 1L;
								
								Class[] columnTypes = getTableColumnTypes();
								boolean[] columnEditables = new boolean[] { true, false, false };
								
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
	
	public void updateInputs( )
	{
		this.lslStreamInfo = LSL.resolve_streams();
		
		JTable t = this.getInputDeviceTable();
		DefaultTableModel tm = (DefaultTableModel)t.getModel();
		t.clearSelection();
		
		for( int i = tm.getRowCount() -1; i >= 0; i-- )
		{
			tm.removeRow( i );
		}
		
		for( LSL.StreamInfo info : this.lslStreamInfo )
		{
			Object selectedController = null;
			Set< Player > players = ConfigApp.getPlayers();
			
			Player selPlayer = NON_SELECTED_PLAYER;
			for( Player p : players )
			{
				Settings cfg = ConfigApp.getPlayerSetting( p );
				if( cfg != null )
				{
					ConfigParameter par = cfg.getParameter( ConfigApp.SELECTED_CONTROLLER );
					selectedController = par.getSelectedValue();
					
					if( selectedController != null )
					{
						IControllerMetadata meta = (IControllerMetadata)selectedController;
						
						if( meta.getControllerID().equals( info.uid() ) )
						{
							selPlayer = p;
							break;
						}
					}
				}
			}
						
			
			Object[] row = new Object[] { selPlayer, info.channel_count(), "  " + info.name() };
			
			tm.addRow( row );
		}
		
		if( this.lslStreamInfo.length == 1 )
		{
			t.addRowSelectionInterval( 0, 0 );
			Set< Player > players = ConfigApp.getPlayers();
			if( players.size() == 1 )
			{
				JTableHeader th = t.getTableHeader();
				TableColumnModel tcm = th.getColumnModel();				
				TableColumn tc = tcm.getColumn( 0 );
				DefaultCellEditor ed = (DefaultCellEditor)tc.getCellEditor();
				JComboBox cbb = (JComboBox)ed.getComponent();
				
				Player player = players.iterator().next();
				cbb.setSelectedItem( player );
				t.setValueAt( player,  0, 0 );
			}
		}
	}

	private void showInputControllerInfo( LSL.StreamInfo info )
	{	
		JPanel panel = this.getPanelInputValues();
		panel.setVisible( false );
		panel.removeAll();
		
		if( info != null )
		{
			panel.setLayout( new BorderLayout() );			
			
			JLabel lb = new JLabel();
			String txt = "<html><b>" + Language.getLocalCaption( Language.NAME )	
							+":</b> " + info.name() 
							+ "; <b>" + Language.getLocalCaption( Language.CHANNELS )
							+ ":</b> " + info.channel_count()
							+ "</html>";			
			lb.setText( txt );
			
			panel.add( lb, BorderLayout.NORTH );	
			
			try
			{
				ControllerManager.getInstance().stopController();
				
				List< IControllerMetadata > ctr = new ArrayList<IControllerMetadata>();
				LSLStreamMetadata meta = new LSLStreamMetadata( info );
				Player p = new Player();
				meta.setPlayer( p );				
				ctr.add( meta );
				
				ControllerManager.getInstance().startController( ctr );
				
				this.inputValues = new ControllerInputValuePanel( info.channel_count() );
				this.inputValues.setVisible( true );
				panel.add( this.inputValues, BorderLayout.CENTER );
				
				ControllerManager.getInstance().addControllerListener( p, this.inputValues );				
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				
				JOptionPane.showMessageDialog( owner, ex.getCause() + "\n" + ex.getMessage()
												, Language.getLocalCaption( Language.ERROR ), JOptionPane.ERROR_MESSAGE );
			}
		}
		
		panel.setVisible( true );
	}

	private void checkPlayerController( Player player, int ignoreRow )
	{
		JTable t = this.getInputDeviceTable();
		
		JTableHeader th = t.getTableHeader();
		TableColumnModel tcm = th.getColumnModel();				
		TableColumn tc = tcm.getColumn( 0 );
		DefaultCellEditor ed = (DefaultCellEditor)tc.getCellEditor();
		JComboBox cbb = (JComboBox)ed.getComponent();
		
		for( int i = 0; i < t.getRowCount(); i++ )
		{
			if( i != ignoreRow )
			{
				Player p = (Player)t.getValueAt( i, 0 );
				if( p.equals( player ) )
				{
					t.setValueAt( NON_SELECTED_PLAYER, i, 0 );
					//cbb.setSelectedItem( NON_SELECTED_PLAYER );
				}
			}
		}
	}

	public void removePlayer( Player player )
	{
		if( player != null && player.getId() != NON_SELECTED_PLAYER.getId() )
		{
			checkPlayerController( player, -1 );
			
			JTable t = this.getInputDeviceTable();
			JTableHeader th = t.getTableHeader();
			TableColumnModel tcm = th.getColumnModel();				
			TableColumn tc = tcm.getColumn( 0 );
			DefaultCellEditor ed = (DefaultCellEditor)tc.getCellEditor();
			JComboBox cbb = (JComboBox)ed.getComponent();
			
			cbb.removeItem( player );
		}
	}
	
}

