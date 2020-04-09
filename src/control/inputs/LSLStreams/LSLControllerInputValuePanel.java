/**
 * 
 */
package control.inputs.LSLStreams;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import GUI.TwoWayProgressBar;
import config.language.Language;
import edu.ucsd.sccn.LSL;

/**
 * @author manuel
 *
 */
public class LSLControllerInputValuePanel extends InputLSLStreamTemplate
{
	private JPanel containerInputValuePanel;
	private JPanel inputValuePanel;
	
	private double[] mins;
	private double[] maxs;
	
	private JLabel[] lbMax;
	private JLabel[] lbMin;
	
	private TwoWayProgressBar[] _2progBar;
	
	private String format = "%.2f";
	
	public LSLControllerInputValuePanel( LSL.StreamInfo info ) throws Exception
	{
		super( info );
		
		this.containerInputValuePanel = new JPanel();
		this.containerInputValuePanel.setLayout( new BorderLayout() );
		
		this.inputValuePanel = new JPanel();
		this.inputValuePanel.setLayout( new BoxLayout( this.inputValuePanel, BoxLayout.Y_AXIS ) );
		
		this.containerInputValuePanel.add( this.inputValuePanel, BorderLayout.NORTH );
		
		int nCh = info.channel_count();
		
		this.lbMax = new JLabel[ nCh ];
		this.lbMin = new JLabel[ nCh ];
		
		this.maxs = new double[ nCh ];
		this.mins = new double[ nCh ];
		
		this._2progBar = new TwoWayProgressBar[ nCh ];
		
		for( int i = 0; i < nCh; i++ )		
		{
			JPanel panel1 = new JPanel( new BorderLayout() );
			JPanel panel2 = new JPanel( new BorderLayout() );
			
			TwoWayProgressBar twpb = new TwoWayProgressBar();
			JLabel lbmin = new JLabel();
			JLabel lbmax = new JLabel();
			
			this._2progBar[ i ] = twpb;
			
			this.lbMin[ i ] = lbmin;
			this.lbMax[ i ] = lbmax;
			
			this.maxs[ i ] = Double.NEGATIVE_INFINITY;
			this.mins[ i ] = Double.POSITIVE_INFINITY;
			
			panel2.add( lbmin, BorderLayout.WEST );
			panel2.add( lbmax, BorderLayout.EAST );
			
			panel1.add( panel2, BorderLayout.NORTH );
			panel1.add( twpb, BorderLayout.CENTER );
			
			panel1.setBorder( BorderFactory.createTitledBorder( Language.getLocalCaption( Language.CHANNEL ) + " " + ( i + 1) ) );
			
			this.inputValuePanel.add( panel1 );
		}		
	}
	
	public JPanel getInputValuePanel()
	{
		return this.containerInputValuePanel;
	}
	
	@Override
	protected void managerData(double[] data)
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
				
				this.lbMax[ i ].setText( String.format( format, this.maxs[ i ] ) );
				this.lbMin[ i ].setText( String.format( format, this.mins[ i ] ) );
				
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
		}
	}

}
