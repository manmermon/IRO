/**
 * 
 */
package control.controller.LSLStreams;

import config.Player;
import control.controller.ControllerMetadata;
import edu.ucsd.sccn.LSL;
import general.NumberRange;

/**
 * @author manuel
 *
 */
public class LSLStreamMetadata implements ControllerMetadata
{
	private LSL.StreamInfo info = null;
	
	private int selectedChannel = 0;
	
	private double recoverLevel;
	private NumberRange actionRange;
	private double targetTime = 0;
	
	private Player player;
	
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
	@Override
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

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getName()
	 */
	@Override
	public String getName()
	{
		return this.info.name();
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#setRecoverInputLevel(double)
	 */
	@Override
	public void setRecoverInputLevel(double value)
	{
		this.recoverLevel = value;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getRecoverInputLevel()
	 */
	@Override
	public double getRecoverInputLevel()
	{
		return this.recoverLevel;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#setActionInputLevel(general.NumberRange)
	 */
	@Override
	public void setActionInputLevel(NumberRange actionRange)
	{
		this.actionRange = new NumberRange( actionRange.getMin(), actionRange.getMax() );
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getActionInputLevel()
	 */
	@Override
	public NumberRange getActionInputLevel()
	{
		return this.actionRange;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#setTargetTimeInLevelAction(double)
	 */
	@Override
	public void setTargetTimeInLevelAction(double time)
	{
		this.targetTime = time;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getTargetTimeInLevelAction(double)
	 */
	@Override
	public double getTargetTimeInLevelAction( )
	{
		return this.targetTime;
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
	 * @see @see control.controller.ControllerMetadata#getControllerType()
	 */
	@Override
	public ControllerType getControllerType()
	{
		return ControllerType.LSLSTREAM;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getControllerSetting()
	 */
	@Override
	public Object getControllerSetting()
	{
		return this.info;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getSamplingRate()
	 */
	@Override
	public double getSamplingRate()
	{
		return this.info.nominal_srate();
	}	
}
