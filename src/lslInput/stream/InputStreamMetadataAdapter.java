/**
 * 
 */
package lslInput.stream;

import config.Player;
import lslInput.LSLStreamInfo;
import lslInput.LSLStreamInfo.StreamType;
import lslInput.LSLUtils;

/**
 * @author manuel
 *
 */
public abstract class InputStreamMetadataAdapter implements IInputStreamMetadata
{	
	protected InputSourceType controllerType = InputSourceType.UNKNOWN;
	
	protected String id = null;
	protected String name = null;

	protected int numberOfChannels = 0;
	
	protected double samplingRate = 0;
	
	protected Object inputSourceSetting = null;
	
	protected String info = "";
	
	protected Player player;

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getControllerType()
	 */
	@Override
	public InputSourceType getInputSourceType()
	{
		return this.controllerType;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getSamplingRate()
	 */
	@Override
	public double getSamplingRate()
	{
		return this.samplingRate;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getControllerSetting()
	 */
	@Override
	public Object getInputSourseSetting()
	{
		return this.inputSourceSetting;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getControllerID()
	 */
	@Override
	public String getInputSourceID()
	{
		return this.id;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getNumberOfChannels()
	 */
	@Override
	public int getNumberOfChannels()
	{
		return this.numberOfChannels;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getName()
	 */
	@Override
	public String getName()
	{
		return this.name;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#setPlayer(config.Player)
	 */
	@Override
	public void setPlayer(Player player)
	{
		this.player = player; 
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getPlayer()
	 */
	@Override
	public Player getPlayer()
	{
		return this.player;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getInfo()
	 */
	@Override
	public String getInfo()
	{
		return this.info;
	}
	

	@Override
	public StreamType getDataStreamType() 
	{
		StreamType dataStreamType = null;
		
		if( this.inputSourceSetting != null )
		{
			String contentType = this.getContentType();
						
			dataStreamType = LSLUtils.getStreamType( contentType );
		}
		
		return dataStreamType;
	}
	
	@Override
	public String getContentType() 
	{
		String contentType = null;
		
		if( this.inputSourceSetting != null )
		{
			if( this.controllerType == InputSourceType.LSLSTREAM )
			{
				contentType = ((LSLStreamInfo)this.inputSourceSetting).content_type(); 
			}
		}
		
		return contentType;
	}
}
