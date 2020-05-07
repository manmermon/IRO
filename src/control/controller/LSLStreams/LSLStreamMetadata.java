/**
 * 
 */
package control.controller.LSLStreams;

import control.controller.ControllerMetadataAdapter;
import edu.ucsd.sccn.LSL;

/**
 * @author manuel
 *
 */
public class LSLStreamMetadata extends ControllerMetadataAdapter
{	
	private LSL.StreamInfo info = null; 
	/**
	 * 
	 */
	public LSLStreamMetadata( LSL.StreamInfo strInfo ) throws IllegalArgumentException
	{
		if( strInfo == null )
		{
			throw new IllegalArgumentException( "Input null." );
		}
		
		this.info = strInfo;

		super.controllerType = ControllerType.LSLSTREAM;
		
		super.id = strInfo.uid();
		super.numberOfChannels = strInfo.channel_count();
		super.info = strInfo.as_xml();
		super.name = strInfo.name();
		super.samplingRate = strInfo.nominal_srate();
	}

	
	@Override
	public void setSelectedChannel(int selectedChannel)
	{
		if( selectedChannel < 0 || selectedChannel >= super.numberOfChannels )
		{
			throw new IndexOutOfBoundsException( "Channel is out of bounds [0, " + ( super.numberOfChannels - 1 ) + "]." );
		}
		
		super.setSelectedChannel( selectedChannel );
	}	
	
	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadataAdapter#getControllerSetting()
	 */
	@Override
	public Object getControllerSetting()
	{
		return this.info;
	}	
}
