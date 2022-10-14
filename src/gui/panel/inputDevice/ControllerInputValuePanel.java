/**
 * 
 */
package gui.panel.inputDevice;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import GUI.progressbar.TwoWayProgressBar;
import config.language.Language;
import control.events.IEnabledInputLSLDataListener;

/**
 * @author manuel
 *
 */
public class ControllerInputValuePanel extends JPanel implements IEnabledInputLSLDataListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2944922658889049973L;
	
	private JPanel containerInputValuePanel;
	private JPanel inputValuePanel;
	
	private double[] mins;
	private double[] maxs;
	
	private JLabel[] lbMax;
	private JLabel[] lbMin;
	
	private TwoWayProgressBar[] _2progBar;
	
	private DataPanel[] plotPanel;
	
	private String format = "%.2f";
	
	private boolean enaCtr = true;
	
	public ControllerInputValuePanel( int channels, double frq ) throws Exception
	{
		this.containerInputValuePanel = new JPanel();
		this.containerInputValuePanel.setLayout( new BorderLayout() );
		
		this.inputValuePanel = new JPanel();
		this.inputValuePanel.setLayout( new BoxLayout( this.inputValuePanel, BoxLayout.Y_AXIS ) );
		
		this.containerInputValuePanel.add( this.inputValuePanel, BorderLayout.NORTH );
		
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension plotDim = new Dimension( 0, (int)( screenDim.height * 0.05 ) );
		
		int xLen = ( frq > 0 ) ? (int)( frq * 5 ) : 500;
		
		int nCh = channels;
		
		this.lbMax = new JLabel[ nCh ];
		this.lbMin = new JLabel[ nCh ];
		
		this.maxs = new double[ nCh ];
		this.mins = new double[ nCh ];
		
		this._2progBar = new TwoWayProgressBar[ nCh ];
		
		this.plotPanel = new DataPanel[ nCh ];
		
		for( int i = 0; i < nCh; i++ )		
		{
			JPanel panel1 = new JPanel( new BorderLayout() );
			JPanel panel2 = new JPanel( new BorderLayout() );
			
			TwoWayProgressBar twpb = new TwoWayProgressBar();
			JLabel lbmin = new JLabel();
			JLabel lbmax = new JLabel();
			
			twpb.setBackground( Color.WHITE );
			twpb.setBarColor( Color.CYAN );
			
			this._2progBar[ i ] = twpb;
			
			DataPanel plot = new DataPanel( xLen );
			plot.setPreferredSize( plotDim );
			
			this.plotPanel[ i ] = plot;
			
			this.lbMin[ i ] = lbmin;
			this.lbMax[ i ] = lbmax;
			
			this.maxs[ i ] = Double.NEGATIVE_INFINITY;
			this.mins[ i ] = Double.POSITIVE_INFINITY;
			
			panel2.add( lbmin, BorderLayout.WEST );
			panel2.add( lbmax, BorderLayout.EAST );
			panel2.add( twpb, BorderLayout.CENTER );
			panel2.add( Box.createRigidArea( new Dimension( 0, 5 ) ), BorderLayout.SOUTH );
			
			panel1.add( panel2, BorderLayout.NORTH );
			//panel1.add( twpb, BorderLayout.CENTER );
			panel1.add( this.plotPanel[ i ], BorderLayout.CENTER );
			
			panel1.setBorder( BorderFactory.createTitledBorder( Language.getLocalCaption( Language.CHANNEL ) + " " + ( i + 1) ) );
			
			this.inputValuePanel.add( panel1 );
		}
		
		super.setLayout( new BorderLayout() );
		super.add( this.containerInputValuePanel, BorderLayout.CENTER );
	}
	
	/*(non-Javadoc)
	 * @see @see control.events.IInputControllerListener#InputControllerEvent(control.events.InputControllerEvent)
	 */
	@Override
	public void InputLSLDataEvent( lslInput.event.InputLSLDataEvent ev ) 
	{
		double[] data = ev.getInputValues();
				
		if( this.enaCtr && data != null )
		{
			for( int i = 0; i < data.length; i++ )
			{
				double val = data[ i ];
							
				boolean updateMinMaxBar = false;
				
				if( val > this.maxs[ i ] )
				{
					this.maxs[ i ] = val;
					updateMinMaxBar = true;
				}
				
				if( val < this.mins[ i ] )
				{
					this.mins[ i ] = val;
					
					updateMinMaxBar = true;
				}
				
				if( updateMinMaxBar )
				{	
					double min = this.mins[ i ];
					double max = this.maxs[ i ];
					double middle = ( min + max ) / 2;
					
					if( min <= 0 && max >= 0 )
					{
						middle = 0;
					}
					
					this.lbMax[ i ].setText( " " + String.format( format, this.maxs[ i ] ) );
					this.lbMin[ i ].setText( String.format( format, this.mins[ i ] ) + " " );
					
					this._2progBar[ i ].setExtremaBarValues( min, middle, max );
				}
				
				double middleValue = this._2progBar[ i ].getBarMiddleValue();
				
				if( val <= middleValue )
				{
					this._2progBar[ i ].setRightValue( middleValue );
					this._2progBar[ i ].setLeftValue( val );
				}
				
				if( val >= middleValue )
				{
					this._2progBar[ i ].setRightValue( val );
					this._2progBar[ i ].setLeftValue( middleValue );
				}
				
				this.plotPanel[ i ].drawData( new double[][] { new double[] { val } } );
			}
		}
	}

	@Override
	public void setEnableInputStream(boolean enable) 
	{
		this.enaCtr = enable;
	}

	@Override
	public void close() 
	{
		this.setEnableInputStream( false );
	}	
}
