/**
 * 
 */
package control.inputs.LSLStreams;

import control.events.InputControllerEvent;
import edu.ucsd.sccn.LSL;
import general.NumberRange;

/**
 * @author manuel
 *
 */
public class LSLController extends InputLSLStreamTemplate
{
	private NumberRange _rng = null;

	private boolean archievedTarget = false;
	
	private int lslSelectedChannel= 0;
	
	/**
	 * @throws Exception 
	 * 
	 */
	public LSLController( LSL.StreamInfo info, int channel, NumberRange r ) throws Exception
	{
		super( info );
		
		if( channel < 0 || channel >= info.channel_count() )
		{
			throw new IndexOutOfBoundsException( "Selected channel (" + channel + ") is out of bounds [0, " + (info.channel_count()-1) + "]" );
		}
		
		if( r == null )
		{
			throw new IllegalArgumentException( "Input range null." );
		}
		
		this._rng = r;
		
		this.lslSelectedChannel = channel;
	}
	
	/*(non-Javadoc)
	 * @see @see control.inputs.LSLStreams.InputLSLStreamTemplate#managerData(double[])
	 */
	@Override
	protected void managerData(double[] inData)
	{
		if( inData != null )
		{
			double data = inData[ this.lslSelectedChannel ];
		
			if( !this.archievedTarget )
			{
				if( data >= this._rng.getMax() )
				{	
					this.archievedTarget = true;

					InputControl.getInstance().action( new IInputAction()
					{	
						@Override
						public InputControllerEvent getInputEvent()
						{
							InputControllerEvent event = new InputControllerEvent( this, InputControllerEvent.ACTION_DONE );
							return event;
						}
					} );
				}
			}
			else if( data <= this._rng.getMin() )
			{
				this.archievedTarget = false;

				InputControl.getInstance().action( new IInputAction()
				{	
					@Override
					public InputControllerEvent getInputEvent()
					{
						InputControllerEvent event = new InputControllerEvent( this, InputControllerEvent.RECOVER_DONE );
						return event;
					}
				} );
			}				
		}
	}

}
