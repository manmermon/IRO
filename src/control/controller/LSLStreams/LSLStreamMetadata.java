/**
 * 
 */
package control.controller.LSLStreams;

import control.controller.ControllerMetadata;
import edu.ucsd.sccn.LSL;

/**
 * @author manuel
 *
 */
public class LSLStreamMetadata implements ControllerMetadata
{
	private LSL.StreamInfo info = null;
	
	private int selectedChannel = 0;
	
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
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getControllerID()
	 */
	@Override
	public String getControllerID()
	{
		return this.info.uid();
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getNumberOfChannels()
	 */
	@Override
	public int getNumberOfChannels()
	{
		// TODO Auto-generated method stub
		return this.info.channel_count();
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#setSelectedChannel()
	 */
	@Override
	public int getSelectedChannel()
	{
		// TODO Auto-generated method stub
		return this.selectedChannel;
	}
	
	/**
	 * @param selectedChannel the selectedChannel to set
	 * @exception IndexOutOfBoundsException if selectedChannel < 0 
	 * 		or selectedChannel >= number of channels. 
	 */
	public void setSelectedChannel(int selectedChannel)
	{
		if( selectedChannel < 0 || selectedChannel >= this.info.channel_count() )
		{
			throw new IndexOutOfBoundsException( "Channel is out of bounds [0, " + ( this.info.channel_count() - 1 ) + "]." );
		}
		
		this.selectedChannel = selectedChannel;
	}
	

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getInfo()
	 */
	@Override
	public String getInfo()
	{
		return this.info.as_xml();
	}

}
