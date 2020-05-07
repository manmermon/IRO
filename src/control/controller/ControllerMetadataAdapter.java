/**
 * 
 */
package control.controller;

import config.Player;
import general.NumberRange;

/**
 * @author manuel
 *
 */
public class ControllerMetadataAdapter implements ControllerMetadata
{	
	protected ControllerType controllerType = ControllerType.UNKNOWN;
	
	protected String id = null;
	protected String name = null;

	protected int numberOfChannels = 0;
	protected int selectedChannel = 0;
	
	protected double recoverLevel = 0;
	protected NumberRange actionRange;
	protected double targetTime = 0;
	protected double samplingRate = 0;
	
	protected Object controllerSetting = null;
	
	protected String info = "";
	
	protected Player player;

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getControllerType()
	 */
	@Override
	public ControllerType getControllerType()
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
	public Object getControllerSetting()
	{
		return this.controllerSetting;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getControllerID()
	 */
	@Override
	public String getControllerID()
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
	 * @see @see control.controller.ControllerMetadata#getSelectedChannel()
	 */
	@Override
	public int getSelectedChannel()
	{
		return this.selectedChannel;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#setSelectedChannel(int)
	 */
	@Override
	public void setSelectedChannel(int selectedChannel)
	{
		this.selectedChannel = selectedChannel;
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
	 * @see @see control.controller.ControllerMetadata#getTargetTimeInLevelAction()
	 */
	@Override
	public double getTargetTimeInLevelAction()
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
	 * @see @see control.controller.ControllerMetadata#getInfo()
	 */
	@Override
	public String getInfo()
	{
		return this.info;
	}
}
