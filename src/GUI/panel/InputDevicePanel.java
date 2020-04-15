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

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import config.language.Caption;
import config.language.Language;
import config.language.TranslateComponents;
import control.controller.ControllerManager;
import control.controller.ControllerMetadata;
import control.controller.LSLStreams.LSLStreamMetadata;
import edu.ucsd.sccn.LSL;
import exceptions.ConfigParameterException;

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
	
	//private JSpinner selectChannelSpinner;

	private JTable inputDeviceTable;
	
	private ControllerInputValuePanel inputValues = null;
	
	//private LSL.StreamInfo selectedLSLStream = null;
	//private int selectedChannel = 0;
	private LSL.StreamInfo[] lslStreamInfo = null;
		
	private static InputDevicePanel setInDevPanel = null;
	
	private static Window owner;
	
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
		
			/*
			Caption cap = Language.getAllCaptions().get( Language.SELECTED_CHANNEL );
			JLabel lb = new JLabel( cap.getCaption( Language.getCurrentLanguage() ) );
			TranslateComponents.add( lb, cap );
			
			this.inputCtrPanel.add( lb );
			this.inputCtrPanel.add( this.getSelectedChannelSpinner() );
			//*/
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

	/*
	private JSpinner getSelectedChannelSpinner()
	{
		if( this.selectChannelSpinner == null )
		{
			this.selectChannelSpinner  = new JSpinner();
			this.selectChannelSpinner.setModel( new SpinnerNumberModel( 1, 1, null, 1) );
			this.selectChannelSpinner.setEnabled( false );
			
			Dimension s = this.selectChannelSpinner.getPreferredSize();
			s.width *= 2;
			this.selectChannelSpinner.setPreferredSize( s );
			
			this.selectChannelSpinner.addChangeListener( new  ChangeListener()
			{	
				@Override
				public void stateChanged(ChangeEvent arg0)
				{
					JSpinner sp = (JSpinner)arg0.getSource();
					
					Object val = sp.getValue();
					
					selectedChannel = (Integer)val;
				}
			});
		}
		
		return this.selectChannelSpinner;
	}
	//*/
	
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
				String h = tc.getHeaderValue().toString();
				
				tc.setResizable( false );	
				int s = fm.stringWidth( h ) + 10;
				tc.setMaxWidth( s );
				tc.setPreferredWidth( s );
			}
			
			tm.addTableModelListener( new TableModelListener()
			{				
				@Override
				public void tableChanged(TableModelEvent arg0)
				{
					//JTable t = (JTable)arg0.getSource();
					JTable t = getInputDeviceTable(); 
					
					int selRow = arg0.getFirstRow();
					int selCol = arg0.getColumn();
					
					if( selRow >= 0 && selRow < lslStreamInfo.length )
					{
						if( selCol == 0 )
						{
							Boolean sel = (Boolean)t.getValueAt( selRow, selCol );
							
							//selectedLSLStream = null;
							
							//JSpinner sp = getSelectedChannelSpinner();
							
							ConfigParameter par = ConfigApp.getParameter( ConfigApp.SELECTED_CONTROLLER );
							
							try
							{
								if( sel )						
								{
									//selectedLSLStream = lslStreamInfo[ selRow ];
									
									
									par.setSelectedValue( new LSLStreamMetadata( lslStreamInfo[ selRow ] ) );
									/*
									sp.setEnabled( true );
									SpinnerNumberModel spm = (SpinnerNumberModel)sp.getModel();
									spm.setMaximum( selectedLSLStream.channel_count() );
									*/
								}
								else
								{	
									//selectedChannel = 0;
									//sp.setEnabled( false );
									
									par.removeSelectedValue();
								}
							}
							catch ( ConfigParameterException ex) 
							{
								ex.printStackTrace();
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
		return new Class[]{ Boolean.class, Integer.class, String.class };
	}
	
	private TableModel createTablemodel( )
	{					
		TableModel tm =  new DefaultTableModel( null, new String[] { Language.getLocalCaption( Language.SELECT )
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
	
	private void updateInputs()
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
			ConfigParameter par = ConfigApp.getParameter( ConfigApp.SELECTED_CONTROLLER );
			
			Object selectedController = par.getSelectedValue();
			
			boolean sel = (selectedController != null );
			
			if( sel )
			{
				ControllerMetadata meta = (ControllerMetadata)selectedController;
				
				sel = meta.getControllerID().equals( info.uid() );
			}
			
			Object[] row = new Object[] { sel, info.channel_count(), "  " + info.name() };
			
			tm.addRow( row );
		}
		
		if( this.lslStreamInfo.length == 1 )
		{
			t.addRowSelectionInterval( 0, 0 );
			t.setValueAt( true, 0, 0);
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
				
				ControllerManager.getInstance().startController( info );
				
				this.inputValues = new ControllerInputValuePanel( info.channel_count() );
				this.inputValues.setVisible( true );
				panel.add( this.inputValues, BorderLayout.CENTER );
				
				ControllerManager.getInstance().addControllerListener( this.inputValues );				
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

	
}
