package lslInput.stream.controller;

import java.util.ArrayList;
import java.util.List;

import control.events.IEnabledInputLSLDataListener;
import control.inputStream.controller.IInputController;
import lslInput.LSLInputData;
import lslInput.LSLStreamInfo;
import lslInput.event.IInputLSLDataListener;
import stoppableThread.IStoppable;

public class LSLInputControllerStream extends LSLInputData implements IInputController
{
	private LSLMetadataController meta;
	
	public LSLInputControllerStream( LSLMetadataController meta ) throws Exception 
	{
		super( (LSLStreamInfo)meta.getInputSourseSetting() );
		
		this.meta = meta;		
	}

	/*(non-Javadoc)
	 * @see @see control.inputs.IInputController#addInputControllerListener(control.events.IInputControllerListener)
	 */
	@Override
	public void addInputControllerListener( IEnabledInputLSLDataListener listener )
	{
		super.addInputLSLDataListener( listener );
	}

	/*(non-Javadoc)
	 * @see @see control.inputs.IInputController#removeInputControllerListener(control.events.IInputControllerListener)
	 */
	@Override
	public void removeInputControllerListener( IEnabledInputLSLDataListener listener )
	{
		super.removeInputLSLDataListener( listener );
	}

	/*(non-Javadoc)
	 * @see @see control.inputs.IInputController#stopController()
	 */
	@Override
	public void stopController() throws Exception
	{	
		super.stopActing( IStoppable.FORCE_STOP );
	} 
	
	/*(non-Javadoc)
	 * @see @see control.inputs.IInputController#startController()
	 */
	@Override
	public void startController() throws Exception
	{
		super.startActing();
	}


	/*(non-Javadoc)
	 * @see @see control.controller.IInputController#getMetadataController()
	 */
	@Override
	public IControllerMetadata getMetadataController()
	{
		return this.meta;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.IInputController#getListener()
	 */
	@Override
	public IEnabledInputLSLDataListener[] getListener()
	{
		IInputLSLDataListener[] listeners = super.getListener();
		
		List< IEnabledInputLSLDataListener > lsts = new ArrayList< IEnabledInputLSLDataListener >();
		
		for( IInputLSLDataListener dl : listeners )
		{
			if( dl instanceof IInputLSLDataListener )
			{
				lsts.add( (IEnabledInputLSLDataListener)dl );
			}
		}
		
		return lsts.toArray( new IEnabledInputLSLDataListener[ 0 ] );
	}

}
