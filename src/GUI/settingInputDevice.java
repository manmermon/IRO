/**
 * 
 */
package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import config.language.Caption;
import config.language.Language;
import config.language.TranslateComponents;
import edu.ucsd.sccn.LSL;

import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

/**
 * @author manuel
 *
 */
public class settingInputDevice extends JDialog
{
	private JPanel inputCtrPanel = null;
	
	private JPanel inputDevicePanel = null;
	
	private JButton btRefresh = null;
	private JSplitPane splitInputPane;
	private JPanel panelInputDeviceList;
	private JPanel panelInputValues;

	private JTable inputDeviceTable;
	
	private LSL.StreamInfo selectedLSLStream = null;
	private LSL.StreamInfo[] lslStreamInfo = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			settingInputDevice dialog = new settingInputDevice();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public settingInputDevice()
	{		
		setBounds(100, 100, 450, 300);
		
		super.getContentPane().setLayout(new BorderLayout());		
		super.getContentPane().add(this.getInputControlPanel(), BorderLayout.NORTH);
		super.getContentPane().add( this.getInputDevicePanel(), BorderLayout.CENTER );
	}

	private JPanel getInputControlPanel()
	{
		if( this.inputCtrPanel == null )
		{
			this.inputCtrPanel = new JPanel();
			
			FlowLayout fl_contentPanel = new FlowLayout();
			fl_contentPanel.setAlignment(FlowLayout.LEFT);
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
			
			this.btRefresh.setText( cap.getCaption( Language.defaultLanguage ) );
			
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
			panelInputDeviceList.add( this.getInputDeviceTable() );
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
		
			this.inputDeviceTable.getTableHeader().setReorderingAllowed( false );
			this.inputDeviceTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
			
			TableColumn tc =  this.inputDeviceTable.getColumnModel().getColumn( 1 );
			tc.setResizable( false );
			tc.setMaxWidth( 20 );

			tm.addTableModelListener( new TableModelListener()
			{				
				@Override
				public void tableChanged(TableModelEvent arg0)
				{
					JTable t = (JTable)arg0.getSource();
					
					int selRow = arg0.getFirstRow();
					int selCol = arg0.getColumn();
					
					if( selCol == 1 
							&& selRow >= 0 
							&& selRow < lslStreamInfo.length )
					{
						Boolean sel = (Boolean)t.getValueAt( selRow, selCol );
						
						selectedLSLStream = null;
						
						if( sel )						
						{
							selectedLSLStream = lslStreamInfo[ selCol ];
						}
					}
				}
			});
			
			this.inputDeviceTable.getSelectionModel().addListSelectionListener( new ListSelectionListener()
			{				
				@Override
				public void valueChanged(ListSelectionEvent e)
				{
					int sel = e.getFirstIndex();
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
	
	private Class[] getTableColumnTypes()
	{
		return new Class[]{ String.class, Boolean.class };
	}
	
	private TableModel createTablemodel( )
	{					
		TableModel tm =  new DefaultTableModel( null, new String[] { Language.getLocalCaption( Language.INPUT ), Language.getLocalCaption( Language.SELECT ) } )
							{
								private static final long serialVersionUID = 1L;
								
								Class[] columnTypes = getTableColumnTypes();
								boolean[] columnEditables = new boolean[] { false, true };
								
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
		
		for( int i = tm.getColumnCount() -1; i >= 0; i-- )
		{
			tm.removeRow( i );
		}
		
		for( LSL.StreamInfo info : this.lslStreamInfo )
		{
			boolean sel = ( this.selectedLSLStream != null)
							&& ( this.selectedLSLStream.uid().equals( info.uid() ) );
			
			Object[] row = new Object[] { info.name(), sel };
			
			tm.addRow( row );
		}
	}

	private void showInputControllerInfo( LSL.StreamInfo info )
	{
		JPanel panel = this.getPanelInputValues();
		panel.removeAll();
		
		if( info != null )
		{
			panel.setLayout( new BorderLayout() );			
			
			JLabel lb = new JLabel();
			String txt = "<html><b>" + Language.getLocalCaption( Language.NAME )	
							+":</b> " + info.name() 
							+ "; <b>" + Language.getLocalCaption( Language.CHANNEL )
							+ "Channels"
							+ ":</b> " + info.channel_count()
							+ "</html>";			
			lb.setText( txt );
			
			panel.add( lb, BorderLayout.NORTH );			
		}
	}
}
