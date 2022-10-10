/**
 * 
 */
package lslInput.stream.controller;

import lslInput.stream.InputStreamMetadataAdapter;

/**
 * @author manuel
 *
 */
public abstract class ControllerMetadataAdapter extends InputStreamMetadataAdapter implements IControllerMetadata
{		
	protected int selectedChannel = 0;
	
	protected double recoverLevel = 0;
	protected double action;
	protected double targetTime = 0;	
	protected int repetitions = 0;

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
	public void setActionInputLevel( double action )
	{
		this.action = action;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.ControllerMetadata#getActionInputLevel()
	 */
	@Override
	public double getActionInputLevel()
	{
		return this.action;
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
	
	@Override
	public void setRepetitions(int rep) 
	{
		this.repetitions = rep;
	}

	@Override
	public int getRepetitions() 
	{
		return this.repetitions;
	}
}
