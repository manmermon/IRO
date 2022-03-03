/**
 * 
 */
package lslStream.controller;

import control.controller.ControllerMetadataAdapter;
import lslStream.LSLStreamInfo;

/**
 * @author manuel
 *
 */
public class LSLMetadataController extends ControllerMetadataAdapter
{	
	private LSLStreamInfo info = null; 
	/**
	 * 
	 */
	public LSLMetadataController( LSLStreamInfo strInfo ) throws IllegalArgumentException
	{
		if( strInfo == null )
		{
			throw new IllegalArgumentException( "Input null." );
		}
		
		this.info = strInfo;

		super.controllerType = ControllerType.LSLSTREAM;
		
		super.id = strInfo.uid();
		super.numberOfChannels = strInfo.channel_count();
		super.info = strInfo.description();
		super.name = strInfo.name();
		super.samplingRate = strInfo.sampling_rate();
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
