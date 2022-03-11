package lslStream.controller;

import java.util.ArrayList;
import java.util.List;

import control.controller.IControllerMetadata;
import control.controller.IInputController;
import control.events.IInputControllerListener;
import stoppableThread.IStoppableThread;
import lslStream.LSLInputStream;
import lslStream.LSLStreamInfo;
import lslStream.event.IInputLSLDataListener;

public class LSLInputControllerStream extends LSLInputStream implements IInputController
{
	private LSLMetadataController meta;
	
	public LSLInputControllerStream( LSLMetadataController meta) throws Exception 
	{
		super( (LSLStreamInfo)meta.getControllerSetting() );
		
		this.meta = meta;		
	}

	/*(non-Javadoc)
	 * @see @see control.inputs.IInputController#addInputControllerListener(control.events.IInputControllerListener)
	 */
	@Override
	public void addInputControllerListener( IInputControllerListener listener )
	{
		super.addInputLSLDataListener( listener );
	}

	/*(non-Javadoc)
	 * @see @see control.inputs.IInputController#removeInputControllerListener(control.events.IInputControllerListener)
	 */
	@Override
	public void removeInputControllerListener( IInputControllerListener listener )
	{
		super.removeInputLSLDataListener( listener );
	}

	/*(non-Javadoc)
	 * @see @see control.inputs.IInputController#stopController()
	 */
	@Override
	public void stopController() throws Exception
	{	
		super.stopThread( IStoppableThread.FORCE_STOP );
	} 
	
	/*(non-Javadoc)
	 * @see @see control.inputs.IInputController#startController()
	 */
	@Override
	public void startController() throws Exception
	{
		super.startThread();
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
	public IInputControllerListener[] getListener()
	{
		IInputLSLDataListener[] listeners = super.getListener();
		
		List< IInputControllerListener > lsts = new ArrayList< IInputControllerListener >();
		
		for( IInputLSLDataListener dl : listeners )
		{
			if( dl instanceof IInputLSLDataListener )
			{
				lsts.add( (IInputControllerListener)dl );
			}
		}
		
		return lsts.toArray( new IInputControllerListener[ 0 ] );
	}

}
