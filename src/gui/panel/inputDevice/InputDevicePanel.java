/**
 * 
 */
package gui.panel.inputDevice;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.BoxLayout;
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
import image.icon.GeneralAppIcon;
import lslStream.LSL;
import lslStream.LSLStreamInfo;
import lslStream.LSLStreamInfo.StreamType;
import lslStream.LSLUtils;
import lslStream.controller.LSLMetadataController;

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
	private JPanel panelControllerBiosignal;
	private JPanel panelInputBiosignalList;

	private JTable inputControllerTable;
	private JTable inputBiosignalTable;
	
	private ControllerInputValuePanel inputValues = null;
	
	private LSLStreamInfo[] lslControlStreamInfo = null;
	private LSLStreamInfo[] lslBioStreamInfo = null;
		
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
	
	public static InputDevicePanel getInstance( )
	{	
		if( setInDevPanel == null )
		{			
			setInDevPanel = new InputDevicePanel();
		}
		
		return setInDevPanel;
	}
	
	private InputDevicePanel( )
	{			
		this.setLayout(new BorderLayout());		
		this.add( this.getInputControlPanel(), BorderLayout.NORTH );
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
	
	public void refreshStreams()
	{
		this.getBtRefresh().doClick();
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
					final JButton b = (JButton)e.getSource();
					
					Thread t = new Thread()
					{
						public void run() 
						{
							b.setEnabled( false );
							
							updateInputs();
							
							b.setEnabled( true );
						};
					};
					
					t.start();
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
						
			splitInputPane.setLeftComponent( this.getControllerBiosignalPanel() );
			
			JScrollPane scroll = new JScrollPane( this.getPanelInputValues() );
			scroll.getVerticalScrollBar().setUnitIncrement( 10 );
			
			splitInputPane.setRightComponent( scroll );
		}
		return splitInputPane;
	}
	
	private JPanel getControllerBiosignalPanel()
	{
		if( this.panelControllerBiosignal == null )
		{
			this.panelControllerBiosignal = new JPanel();
			BoxLayout ly = new BoxLayout( this.panelControllerBiosignal, BoxLayout.X_AXIS );
			this.panelControllerBiosignal.setLayout( ly );
			
			
			JScrollPane scroll1 = new JScrollPane( this.getPanelInputControllerDeviceList() );
			JScrollPane scroll2 = new JScrollPane( this.getPanelInputBiosignalDeviceList() );
			
			scroll1.getVerticalScrollBar().setUnitIncrement( 5 );
			scroll2.getVerticalScrollBar().setUnitIncrement( 5 );
			
			this.panelControllerBiosignal.add( scroll1 );
			this.panelControllerBiosignal.add( scroll2 );
		}
		
		return this.panelControllerBiosignal;
	}
	
	private JPanel getPanelInputBiosignalDeviceList() 
	{
		if ( panelInputBiosignalList == null) 
		{
			panelInputBiosignalList = new JPanel();
			
			Caption caption = Language.getAllCaptions().get( Language.BIOSIGNAL );
			panelInputBiosignalList.setBorder( BorderFactory.createTitledBorder( caption.getCaption( Language.getCurrentLanguage() ) ) );
			
			TranslateComponents.add( panelInputBiosignalList, caption );
			
			panelInputBiosignalList.setLayout( new BorderLayout() );
			panelInputBiosignalList.add( this.getInputBiosignalTable().getTableHeader(), BorderLayout.NORTH );
			panelInputBiosignalList.add( this.getInputBiosignalTable(), BorderLayout.CENTER );
		}
		
		return panelInputBiosignalList;
	}
	
	private JPanel getPanelInputControllerDeviceList() 
	{
		if (panelInputDeviceList == null) 
		{
			panelInputDeviceList = new JPanel();
			
			Caption caption = Language.getAllCaptions().get( Language.CONTROLLER );
			panelInputDeviceList.setBorder( BorderFactory.createTitledBorder( caption.getCaption( Language.getCurrentLanguage() ) ) );
			
			TranslateComponents.add( panelInputDeviceList, caption );
			
			panelInputDeviceList.setLayout( new BorderLayout() );
			panelInputDeviceList.add( this.getInputControllerTable().getTableHeader(), BorderLayout.NORTH );
			panelInputDeviceList.add( this.getInputControllerTable(), BorderLayout.CENTER );
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
		this.updatePlayerAux( this.getInputControllerTable() );
		this.updatePlayerAux( this.getInputBiosignalTable() );
	}
	
	private void updatePlayerAux( JTable t )
	{
		if( t != null )
		{
			List< Player > players = new ArrayList<Player>( ConfigApp.getPlayers() );
			
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
			
 			if( cbb.getItemCount() == 2 && t.getRowCount() == 1 )
			{
				Player p = cbb.getItemAt( 1 );
				t.setValueAt( p, 0, 0);
			}
		}
	}
	
	private JTable getInputControllerTable()
	{
		if( this.inputControllerTable == null )
		{	
			this.inputControllerTable = this.getCreateJTable();
			TableModel tm = this.createControllerTablemodel();
			this.inputControllerTable.setModel( tm );		
			/*
			this.inputControllerTable.getColumnModel().getColumn( this.inputControllerTable.getColumnCount() - 1 ).setMinWidth( 0 );
			this.inputControllerTable.getColumnModel().getColumn( this.inputControllerTable.getColumnCount() - 1 ).setMaxWidth( 0 );
			this.inputControllerTable.getColumnModel().getColumn( this.inputControllerTable.getColumnCount() - 1 ).setWidth( 0 );
			*/
			
			FontMetrics fm = this.inputControllerTable.getFontMetrics( this.inputControllerTable.getFont() );
			
			JTableHeader th = this.inputControllerTable.getTableHeader();			
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
					DefaultCellEditor editor = new DefaultCellEditor( getPlayerCombobox() );	
							
					tc.setCellEditor( editor );
					s *= 2;
				}
				
				tc.setMaxWidth( s );
				tc.setPreferredWidth( s );				
			}
			
			this.inputControllerTable.getModel().addTableModelListener( new TableModelListener()
			{				
				@Override
				public void tableChanged(TableModelEvent ev )
				{
					if( ev.getType() == TableModelEvent.UPDATE )
					{
						JTable t = inputControllerTable;
					
						int row = ev.getFirstRow();		
						int col = ev.getColumn();
						
						if( row >= 0 && col == 0 )
						{
							Player player = (Player)t.getValueAt( row, 0 );
																					
							LSLStreamInfo info = null;
							info = lslControlStreamInfo[ row ];
							
							if( player.getId() != NON_SELECTED_PLAYER.getId() )
							{
								checkPlayerController( player, row );
							}

							updatePlayerControllerSetting( player, info );
						}
					}
				}
			});
			
			this.inputControllerTable.getSelectionModel().addListSelectionListener( new ListSelectionListener()
			{				
				@Override
				public void valueChanged(ListSelectionEvent e)
				{
					JTable t = getInputControllerTable();
					int sel = t.getSelectedRow();
					
					LSLStreamInfo info = null;					
					
					if( sel >= 0 )
					{
						info = lslControlStreamInfo[ sel ];
					}
					
					showInputControllerInfo( info );
				}
			});			
		}
		
		return this.inputControllerTable;
	}
		
	private JTable getInputBiosignalTable()
	{
		if( this.inputBiosignalTable == null )
		{	
			this.inputBiosignalTable = this.getCreateJTable();
			TableModel tm = this.createBiosignalTablemodel();
			this.inputBiosignalTable.setModel( tm );
			
			FontMetrics fm = this.inputBiosignalTable.getFontMetrics( this.inputBiosignalTable.getFont() );
			
			JTableHeader th = this.inputBiosignalTable.getTableHeader();			
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
			
			this.inputBiosignalTable.getModel().addTableModelListener( new TableModelListener()
			{				
				@Override
				public void tableChanged(TableModelEvent ev )
				{
					if( ev.getType() == TableModelEvent.UPDATE )
					{
						JTable t = inputBiosignalTable;
					
						int row = ev.getFirstRow();
						int col = ev.getColumn();
						
						if( row >= 0 && col == 0 )
						{
							Player player = (Player)t.getValueAt( row, 0 );
							
							LSLStreamInfo info = null;													
							info = lslBioStreamInfo[ row ];
								
							updatePlayerBiosignalSetting( player, info );
						}
					}
				}
			});
			
			/*
			this.inputBiosignalTable.getSelectionModel().addListSelectionListener( new ListSelectionListener()
			{				
				@Override
				public void valueChanged(ListSelectionEvent e)
				{
					JTable t = getInputBiosignalTable();
					int sel = t.getSelectedRow();
					LSLStreamInfo info = null;
										
					//if( sel >= 0 && sel < lslStreamInfo.length )
					//{
					//	info = lslStreamInfo[ sel ];
					//}
					
					
					info = lslBioStreamInfo[ sel ];
					
					//showInputControllerInfo( info );
				}
			});	
			//*/
		}
		
		return this.inputBiosignalTable;
	}
	
	private void updatePlayerControllerSetting( Player player, LSLStreamInfo info )
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

						String uid = info.uid();
						if( cmeta.getControllerID().equals( uid ) )
						{
							par.removeSelectedValue();
						}
					}
				}
				else
				{
					IControllerMetadata meta = new LSLMetadataController( info );
					par.setSelectedValue( meta );
				}
			}
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	}
	
	private void updatePlayerBiosignalSetting( Player player, LSLStreamInfo strInfo )
	{
		try
		{
			for( Player pl : ConfigApp.getPlayers() )
			{
				Settings setplayer = ConfigApp.getPlayerSetting( pl );			
				ConfigParameter par = setplayer.getParameter( ConfigApp.SELECTED_BIOSIGNAL );

				if( par == null )
				{
					Caption cap = Language.getAllCaptions().get( Language.BIOSIGNAL );
					cap.setID( ConfigApp.SELECTED_BIOSIGNAL );

					par = new ConfigParameter( cap, ParameterType.OTHER );											
				}

				if( !pl.equals( player ) )
				{
					Object val = par.getSelectedValue();
					
					if( val != null )
					{		
						String[] strUIDS = val.toString().split( ";" );
						
						val = "";
						for( String uid : strUIDS )
						{
							if( !strInfo.uid().equals( uid ) )
							{
								val += uid + ";";
							}						
						}
						
						par.setSelectedValue( val );
					}
				}
				else
				{
					par.setSelectedValue( strInfo.uid() );
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
				                }
				
				                return tip;
				            }				            
				        };
				      
				        /*
		table.setDefaultRenderer( Object.class, new DefaultTableCellRenderer()
											{	
												public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
											    {
											        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
											        	
											        cellComponent.setBackground( new Color( 255, 255, 200 ) );
											        
											        if( !table.isCellEditable( row, column ) )
											        {	
											        	if( hasFocus )
											        	{
											        		cellComponent.setBackground( new Color( 200, 200, 100 ) );
											        	}
											        }
											        
											        cellComponent.setForeground( Color.BLACK );
											        
											        return cellComponent;
											    }
												
											
											});
											*/
		
		return table;
	}
	
	private Class[] getControllerTableColumnTypes()
	{
		return new Class[]{ Player.class
							, Integer.class
							, String.class
							//, String.class 
							};
	}
	
	private TableModel createControllerTablemodel( )
	{					
		TableModel tm =  new DefaultTableModel( null, new String[] { Language.getLocalCaption( Language.PLAYER )
																	, Language.getLocalCaption( Language.CHANNELS )
																	, Language.getLocalCaption( Language.INPUT )
																	//,  "" 
																	} )
							{
								private static final long serialVersionUID = 1L;
								
								Class[] columnTypes = getControllerTableColumnTypes();
								boolean[] columnEditables = new boolean[] { true
																			, false
																			, false
																			//, false 
																			};
								
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
	
	private Class[] getBiosignalTableColumnTypes()
	{
		return new Class[]{ Player.class
							, String.class
							//, String.class 
							};
	}
	
	private TableModel createBiosignalTablemodel( )
	{					
		TableModel tm =  new DefaultTableModel( null, new String[] { Language.getLocalCaption( Language.PLAYER )
																	, Language.getLocalCaption( Language.INPUT )
																	//, "" 
																	} )
							{
								private static final long serialVersionUID = 1L;
								
								Class[] columnTypes = getBiosignalTableColumnTypes();
								boolean[] columnEditables = new boolean[] { true
																			, false
																			//, false 
																			};
								
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
	
	public synchronized void updateInputs( )
	{		
		//this.lslStreamInfo = LSL.resolve_streams();
		LSLStreamInfo[] streams = LSL.resolve_streams( );
		
		JTable t = this.getInputControllerTable();
		DefaultTableModel tm = (DefaultTableModel)t.getModel();
		t.clearSelection();
		
		for( int i = tm.getRowCount() -1; i >= 0; i-- )
		{
			tm.removeRow( i );
		}
		
		JTable bioT = this.getInputBiosignalTable();
		DefaultTableModel bioTm = (DefaultTableModel)bioT.getModel();
		bioT.clearSelection();
		
		for( int i = bioTm.getRowCount() -1; i >= 0; i-- )
		{
			bioTm.removeRow( i );
		}
		
		List< LSLStreamInfo > ctrStreams = new ArrayList< LSLStreamInfo >();
		List< LSLStreamInfo > bioStreams = new ArrayList< LSLStreamInfo >();			
		
		for( LSLStreamInfo info : streams )
		{
			StreamType strType = LSLUtils.getStreamType( info.content_type() );
			
			if( strType == StreamType.CONTROLLER || strType == StreamType.CONTROLLER_BIOSIGNAL )
			{				
				ctrStreams.add( info );
				
				Object selectedController = null;
				Set< Player > players = ConfigApp.getPlayers();
				
				Player selPlayer = NON_SELECTED_PLAYER;
				for( Player p : players )
				{
					Settings cfg = ConfigApp.getPlayerSetting( p );
					if( cfg != null )
					{
						ConfigParameter par = cfg.getParameter( ConfigApp.SELECTED_CONTROLLER );						
						
						if( par != null )
						{
							selectedController = par.getSelectedValue();
							
							if( selectedController != null )
							{
								IControllerMetadata meta = (IControllerMetadata)selectedController;
								
								String uid = info.uid();
								if( meta.getControllerID().equals( uid ) )
								{
									selPlayer = p;
									break;
								}
							}
						}						
						
					}
				}
				
				Object[] row = new Object[] { selPlayer, info.channel_count(), info.name(), info.uid() };
				
				tm.addRow( row );
				
				if( strType == StreamType.CONTROLLER_BIOSIGNAL )
				{
					bioStreams.add( info );
					
					Settings cfg = ConfigApp.getPlayerSetting( selPlayer );
					
					if( cfg != null )
					{
						ConfigParameter par = cfg.getParameter( ConfigApp.SELECTED_BIOSIGNAL );						
						
						if( par != null )
						{	
							List< Object > vals = par.getAllOptions();
							
							if( vals != null && !vals.isEmpty() )
							{
								boolean find = false;
								
								for( Object val : vals )
								{
									LSLStreamInfo meta = (LSLStreamInfo)val;
									
									String uid = info.uid();
									find = !meta.uid().equals( uid );
									
									if( find )
									{
										break;
									}
								}
								
								if( !find )
								{
									selPlayer = NON_SELECTED_PLAYER;
								}
							}
							else
							{
								selPlayer = NON_SELECTED_PLAYER;
							}
							
							
						}
						else
						{
							selPlayer = NON_SELECTED_PLAYER;
						}
						
					}
					else
					{
						selPlayer = NON_SELECTED_PLAYER;
					}
					
					row = new Object[] { selPlayer, info.name(), info.uid() };
					bioTm.addRow( row );
				}
			}
			else if( strType == StreamType.BIOSIGNAL )
			{
				
				bioStreams.add( info );
				
				Set< Player > players = ConfigApp.getPlayers();
				
				Player selPlayer = NON_SELECTED_PLAYER;
				
				bioPlayer:
				for( Player p : players )
				{
					Settings cfg = ConfigApp.getPlayerSetting( p );
					if( cfg != null )
					{
						ConfigParameter par = cfg.getParameter( ConfigApp.SELECTED_BIOSIGNAL );
						
						if( par != null )
						{
							/*
							selectedBiosignal = par.getSelectedValue();
							
							if( selectedBiosignal != null )
							{
								LSLStreamInfo meta = (LSLStreamInfo)selectedBiosignal;
								
								if( meta.uid().equalsIgnoreCase( info.uid() ) )
								{
									selPlayer = p;
									break;
								}
							}
							//*/
							
							Object val = par.getSelectedValue();
							
							if( val != null )
							{
								String[] uids = val.toString().split( ";" );
								
								for( String uid : uids )
								{
									if( info.uid().equals( uid ) )
									{
										selPlayer = p;										
										break bioPlayer;
									}
								}
							}
						}
					}
				}				
				
				Object[] row = new Object[] { selPlayer, info.name(), info.uid() };
				
				bioTm.addRow( row );
			}
		}
		
		this.lslControlStreamInfo = ctrStreams.toArray( new LSLStreamInfo[0] );
		this.lslBioStreamInfo = bioStreams.toArray( new LSLStreamInfo[0] );
		
		if( streams.length == 1 )
		{
			if( t.getRowCount() > 0 )
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
	}

	private void showInputControllerInfo( LSLStreamInfo info )
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
				LSLMetadataController meta = new LSLMetadataController( info );
				Player p = new Player();
				meta.setPlayer( p );				
				ctr.add( meta );
				
				ControllerManager.getInstance().startController( ctr );
				
				this.inputValues = new ControllerInputValuePanel( info.channel_count(), info.sampling_rate() );
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
		JTable t = this.getInputControllerTable();
		
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
			
			JTable t = this.getInputControllerTable();
			JTableHeader th = t.getTableHeader();
			TableColumnModel tcm = th.getColumnModel();				
			TableColumn tc = tcm.getColumn( 0 );
			DefaultCellEditor ed = (DefaultCellEditor)tc.getCellEditor();
			JComboBox cbb = (JComboBox)ed.getComponent();
			
			cbb.removeItem( player );
		}
	}
	
}

