/**
 * 
 */
package control.inputStream.controller;

import control.events.IEnabledInputLSLDataListener;
import lslInput.stream.controller.IControllerMetadata;

/**
 * @author manuel
 *
 */
public interface IInputController
{
	public IControllerMetadata getMetadataController();
	
	public void startController() throws Exception;
	
	public void stopController() throws Exception;
	 
	public void addInputControllerListener( IEnabledInputLSLDataListener listener );
	
	public void removeInputControllerListener( IEnabledInputLSLDataListener listener );
	
	public IEnabledInputLSLDataListener[] getListener();
	
	//public void clearInputControllerListener();
}
