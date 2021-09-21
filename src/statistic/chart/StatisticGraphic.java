/**
 * 
 */
package statistic.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.knowm.xchart.BoxChart;
import org.knowm.xchart.BoxChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.BoxStyler.BoxplotCalCulationMethod;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import GUI.AppIcon;
import config.Player;
import config.language.Language;
import general.ArrayTreeMap;
import general.ConvertTo;
import general.Pair;
import general.Tuple;
import statistic.RegistrarStatistic;

/**
 * @author manuel
 *
 */
public class StatisticGraphic
{
	//*
	public static JPanel getSessionStatistic( GameSessionStatistic session, Player player, Dimension size )
	{   
		JPanel statPanel = new JPanel( new BorderLayout() );
		
		// Create Chart
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
		XYChart chart = new XYChartBuilder()
						.width( size.width )
						.height( size.height )
						.title( player.getName() + " - " + format.format( session.getSessionDate() ) )
						.xAxisTitle( Language.getLocalCaption( Language.TIME ) + " (s)")
						.yAxisTitle( "" )
						.build();

		// Customize Chart
		chart.getStyler().setLegendPosition( LegendPosition.OutsideE );
		chart.getStyler().setDefaultSeriesRenderStyle( XYSeriesRenderStyle.Line );
		chart.getStyler().setYAxisLabelAlignment( XYStyler.TextAlignment.Right );
		chart.getStyler().setYAxisDecimalPattern( "#,###.##" );
		chart.getStyler().setPlotMargin( 0 );
		chart.getStyler().setPlotContentSize( .95 );

		XChartPanel< XYChart > chartPanel = new XChartPanel< XYChart>( chart );

		Pair< ControllerMetadataExtenderAdapter, Double[][] > ctr = session.getControllerData( player.getId() );
		//double startSession = session.getStartSessionInMillis() / 1e3D;

		Double startSession = null;
		
		int ch = ctr.getX1().getSelectedChannel();
		Double[][] data = ctr.getX2();
		
		if( data != null )
		{
			int dataLen = data.length;
			int numChannels = data[ 0 ].length;
			double[] xAxis = new double[ dataLen ];			
			
			for( int i = 0; i < dataLen; i++ )
			{
				if( startSession == null )
				{
					startSession = data[ i ] [ numChannels - 1 ];
				}
				
				xAxis[ i ] = ( data[ i ] [ numChannels - 1 ] - startSession );
			}
	
			boolean show = true;
			List< Integer > channelList = new ArrayList<Integer>();
			
			for( int i = 0; i < numChannels - 1; i++ )
			{
				channelList.add( i );
			}
			channelList.remove( ch );
			channelList.add(  ch );
			
			for( Integer i : channelList )
			{
				double[] yAxis = new double[ dataLen ];
				for( int j = 0; j < dataLen; j++ )
				{
					yAxis[ j ] = data[ j ][ i ];
				}
	
				String lb = Language.getLocalCaption( Language.CHANNEL) + " " + i;
				Color c = Color.LIGHT_GRAY;
				
				if( i == ch )
				{
					c = Color.RED;				
				}
				
				XYSeries serie = chart.addSeries( lb, xAxis, yAxis );
				serie.setShowInLegend( false );
				
				if( i == ch )
				{
					serie.setShowInLegend( true );
				}
				else if( show )
				{
					lb = Language.getLocalCaption( Language.OTHERS );
					serie.setLabel( lb );
					serie.setShowInLegend( true );
					show = false;
				}
				
				serie.setMarker( SeriesMarkers.NONE );
				serie.setLineColor( c );
				
			}
	
			statPanel.add( chartPanel, BorderLayout.CENTER );
		}
		
		return statPanel;
	}
	//*/
	
	public static JPanel getPlot( List< Tuple< Double, Double > > data, StatisticPropieties propieties )
	{   
		JPanel statPanel = new JPanel( new BorderLayout() );
		
		Dimension size = propieties.getSize();
		
		// Create Chart
		XYChart chart = new XYChartBuilder()
						.width( size.width )
						.height( size.height )
						.title( propieties.getTitle() )
						.xAxisTitle( propieties.getXlabel() )
						.yAxisTitle( propieties.getYlabel() )
						.build();

		// Customize Chart
		chart.getStyler().setLegendPosition( LegendPosition.OutsideE );
		chart.getStyler().setDefaultSeriesRenderStyle( XYSeriesRenderStyle.Line );
		chart.getStyler().setYAxisLabelAlignment( XYStyler.TextAlignment.Right );
		chart.getStyler().setYAxisDecimalPattern( "#,###.##" );
		chart.getStyler().setPlotMargin( 0 );
		chart.getStyler().setPlotContentSize( .95 );
		chart.getStyler().setLegendVisible( propieties.isLegendOn() );

		XChartPanel< XYChart > chartPanel = new XChartPanel< XYChart>( chart );
				
		if( data != null && !data.isEmpty() )
		{
			double[ ] xData = new double[ data.size() ];
			double[ ] yData = new double[ data.size() ];
			
			int i = 0;
			for( Tuple< Double, Double > d : data )
			{	
				xData[ i ] = d.t1;
				yData[ i ] = d.t2;
				
				i++;
			}
		
			Color c = Color.BLUE;
			
			if( !propieties.getColors().isEmpty() )
			{
				c = propieties.getColors().get( 0 );
			}
			
			String lng = " ";
			
			if( !propieties.getLegend().isEmpty() )
			{
				lng = propieties.getLegend().get( 0 );
			}
			
			XYSeries serie = chart.addSeries( lng, xData, yData );
			//serie.setShowInLegend( propieties.isLegendOn() );
			

			Marker mk = SeriesMarkers.NONE;
			
			if( !propieties.getMarkes().isEmpty() )
			{
				mk = propieties.getMarkes().get( 0 );
			}

			serie.setMarker( mk );
			serie.setLineColor( c );						
	
			statPanel.add( chartPanel, BorderLayout.CENTER );
		}
		
		return statPanel;
	}
	
	public static JPanel getBoxplot( ArrayTreeMap< String,  List< Double > > data, StatisticPropieties propieties )
	{   
		JPanel statPanel = new JPanel( new BorderLayout() );
		
		Dimension size = propieties.getSize();
		
		// Create Chart
		BoxChart chart = new BoxChartBuilder()
							.width( size.width )
							.height( size.height )
							.title( propieties.getTitle() )
							.xAxisTitle( propieties.getXlabel() )
							.yAxisTitle( propieties.getYlabel() )
							.build();

		// Customize Chart
		chart.getStyler().setLegendPosition( LegendPosition.OutsideE );
		chart.getStyler().setBoxplotCalCulationMethod( BoxplotCalCulationMethod.N_LESS_1_PLUS_1 );
		chart.getStyler().setYAxisLabelAlignment( XYStyler.TextAlignment.Right );
		chart.getStyler().setYAxisDecimalPattern( "#,###.##" );
		chart.getStyler().setPlotMargin( 0 );
		chart.getStyler().setPlotContentSize( .95 );
		chart.getStyler().setLegendVisible( propieties.isLegendOn() );
				
		if( data != null )
		{
			for( String lng : data.keySet() )
			{
				List< List< Double > > values = data.get( lng );
								
				for( List< Double > d : values )
				{	
					chart.addSeries( lng, d );
				}
			}
		}
		
		XChartPanel< BoxChart > panel = new XChartPanel< BoxChart >( chart );
		
		statPanel.add( panel, BorderLayout.CENTER );
		
		return statPanel;
	}
	
	public static JPanel getScores( List< GameSessionStatistic > gss, Player player, Dimension panelSize )
	{		
		StatisticPropieties prop = new StatisticPropieties();
		
		prop.addColors( Color.BLUE );
		prop.addMarkes( SeriesMarkers.TRIANGLE_UP );
		prop.setTitle( player.getName() + ": " + Language.getLocalCaption( Language.SCORE ) );
		prop.setSize( panelSize );
		prop.setXlabel( "days" );
		prop.setLegendOn( false );
		
		List< Tuple< Double, Double > > score = new ArrayList<Tuple<Double,Double>>();
		
		List< Tuple< Long, Integer > > values = new ArrayList< Tuple< Long, Integer > >();
		
		for( GameSessionStatistic session : gss )
		{
			 values.addAll( session.getScores( player.getId() ) );			
		}
		
		Collections.sort( values
				, new Comparator< Tuple< Long, Integer > >() 
		{				
			@Override
			public int compare(Tuple<Long, Integer> o1, Tuple<Long, Integer> o2) 
			{
				return (int)( o1.t1 - o2.t1);
			}
		} );

		Calendar refDate = null;
		for( Tuple< Long, Integer > sc : values )
		{
			long date = sc.t1;
			int val = sc.t2;

			Calendar cal = GregorianCalendar.getInstance();
			cal.setTimeInMillis( date );

			if( refDate == null )
			{
				refDate = cal;
			}

			long minutes = ChronoUnit.MINUTES.between( refDate.toInstant(), cal.toInstant() );

			double time = minutes / ( 24D * 60 ); // day distance

			score.add( new Tuple<Double, Double>( time, (double)val ) );
		}
		
		JPanel scorePlot = getPlot( score, prop );
		
		return scorePlot;
	}

	
	public static JPanel getReactionTime( List< GameSessionStatistic > gss, Player player, Dimension panelSize )
	{
		StatisticPropieties prop = new StatisticPropieties();
	
		prop.addColors( Color.BLUE );
		prop.addMarkes( SeriesMarkers.TRIANGLE_UP );
		prop.setTitle( player.getName() + ": " + Language.getLocalCaption( Language.REACTION_TIME ) );
		prop.setSize( panelSize );
		prop.setXlabel( "seconds (s)" );
		prop.setLegendOn( false );
				
		ArrayTreeMap< Long, String > eventTime = new ArrayTreeMap< Long, String >();
		
		for( GameSessionStatistic session : gss )
		{
			ArrayTreeMap< Long, Pair< Integer, String > > events = session.getGameEvent();
			
			for( Long t : events.keySet() )
			{
				List< Pair< Integer, String > > evs = events.get( t );
				
				for( Pair< Integer, String > ev : evs )
				{
					String noteEvent = ev.getX2();
					int pl = ev.getX1();
					
					if( pl == player.getId() 
							&& 
							( noteEvent.equals( RegistrarStatistic.FieldType.NOTE_ENTER_FRET.name() ) 
								|| 
								noteEvent.equals( RegistrarStatistic.FieldType.CONTROLLER_MAINTAIN_LEVEL_REACH.name() )
							) 
						)
					{
						
						eventTime.put( t, noteEvent );
					}
				}
			}			
		}
		
		
		
		Calendar refDate = null;
		for( Tuple< Long, Integer > sc : values )
		{
			long date = sc.t1;
			int val = sc.t2;

			Calendar cal = GregorianCalendar.getInstance();
			cal.setTimeInMillis( date );

			if( refDate == null )
			{
				refDate = cal;
			}

			long minutes = ChronoUnit.MINUTES.between( refDate.toInstant(), cal.toInstant() );

			double time = minutes / ( 24D * 60 ); // day distance

			score.add( new Tuple<Double, Double>( time, (double)val ) );
		}
		
		JPanel scorePlot = getPlot( score, prop );
		
		return scorePlot;
	}
	
	
	public static void showSessionStatistic( Window owner, List< GameSessionStatistic > sessions, Player player, Rectangle bounds )
	{
		final List< JPanel > statPlotPanels = new ArrayList<JPanel>();
		
		JDialog dialog = new JDialog( owner );		
		dialog.setVisible( false );
		dialog.setModal( true );
		dialog.setLocation( bounds.getLocation() );
		dialog.setPreferredSize( bounds.getSize() );
		dialog.setSize( bounds.getSize() );
		dialog.setTitle( Language.getLocalCaption( Language.STATISTIC ) + ": " + player.getName() );
		dialog.setIconImage( AppIcon.appIcon( 64 ) );
		
		Container contentPane = dialog.getContentPane();
		
		contentPane.setLayout( new BorderLayout() );

		final JTable table = getCreateJTable();
		DefaultTableModel tm = (DefaultTableModel)createTablemodel();
		table.setModel( tm );
		table.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );		
		
		JPanel tablePanel = new JPanel( new BorderLayout() );
		Dimension ths = table.getTableHeader().getPreferredSize();
		FontMetrics fm = table.getFontMetrics( table.getFont() );
		ths.width = fm.stringWidth( "  9999-99-99 99:99  " );
		table.getTableHeader().setPreferredSize( ths );
		tablePanel.add( table.getTableHeader(), BorderLayout.NORTH );
		tablePanel.add( table, BorderLayout.CENTER );
		
		contentPane.add( new JScrollPane( tablePanel ), BorderLayout.WEST );	

		final JPanel plotPanel = new JPanel( new GridLayout( 1, 0 ) );
		contentPane.add( new JScrollPane( plotPanel ) , BorderLayout.CENTER );
		
		dialog.pack();
		
		Dimension panelSize = plotPanel.getSize();
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
		for( GameSessionStatistic session : sessions )
		{
			//statPlotPanels.add( getSessionStatistic( session, player, panelSize ) );
			statPlotPanels.add( getScores( sessions, player, panelSize ) );
			
			tm.addRow( new String[] {  dateFormat.format( session.getSessionDate() ) } );
		}
		
		table.getSelectionModel().addListSelectionListener( new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent ev)
			{
				int sel = table.getSelectedRow();
				
				if( sel >= 0 )
				{
					plotPanel.setVisible( false );
					plotPanel.removeAll();
					plotPanel.add( statPlotPanels.get( sel ) );
					plotPanel.setVisible( true );
				}
			}
		} );
		
		if( table.getRowCount() > 0 )
		{
			table.addRowSelectionInterval( 0, 0 );
		}
		
		//dialog.setSize( bounds.getSize() );
		dialog.setVisible( true );
	}
	
	private static JTable getCreateJTable()
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
	
	private static TableModel createTablemodel( )
	{					
		TableModel tm =  new DefaultTableModel( null, new String[] { Language.getLocalCaption( Language.SESSIONS ) } )
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

	//*************************
	//
	//
	
	private static class StatisticPropieties
	{
		private String title = "";
		private String xlabel = "";
		private String ylabel = "";
		private Dimension size = new Dimension();
		
		private List< Color > colors = new ArrayList<Color>();
		
		private List< String > legend = new ArrayList< String >();
		
		private List< Marker > markes = new ArrayList< Marker >();
		
		private boolean legendOn = false;
		
		public StatisticPropieties()
		{
		}
		
		/**
		 * @param title the title to set
		 */
		public void setTitle(String title) {
			this.title = title;
		}
		
		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}
		
		/**
		 * @param xlabel the xlabel to set
		 */
		public void setXlabel(String xlabel) {
			this.xlabel = xlabel;
		}
		
		/**
		 * @return the xlabel
		 */
		public String getXlabel() {
			return xlabel;
		}
		
		/**
		 * @param ylabel the ylabel to set
		 */
		public void setYlabel(String ylabel) {
			this.ylabel = ylabel;
		}
		
		/**
		 * @return the ylabel
		 */
		public String getYlabel() {
			return ylabel;
		}
		
		public void addColors( Color c)
		{
			this.colors.add( c );
		}
		
		/**
		 * @return the colors
		 */
		public List<Color> getColors() 
		{
			return colors;
		}
		
		/**
		 * @param size the size to set
		 */
		public void setSize(Dimension size) 
		{
			this.size = size;
		}
		
		/**
		 * @return the size
		 */
		public Dimension getSize() {
			return size;
		}
		
		/**
		 * @param legend the legend to set
		 */
		public void addLegend( String legend) 
		{
			this.legend.add( legend );
		}
		
		/**
		 * @return the legend
		 */
		public List<String> getLegend()
		{
			return legend;
		}
		
		/**
		 * @param legendOn the legendOn to set
		 */
		public void setLegendOn( boolean legendOn )
		{
			this.legendOn = legendOn;
		}
		
		/**
		 * @return the legendOn
		 */
		public boolean isLegendOn() 
		{
			return legendOn;
		}
		
		/**
		 * @param markes the markes to set
		 */
		public void addMarkes( Marker markes) 
		{
			this.markes.add( markes );
		}
		
		/**
		 * @return the markes
		 */
		public List< Marker > getMarkes() {
			return markes;
		}
	}
}
