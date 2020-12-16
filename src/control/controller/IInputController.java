/**
 * 
 */
package control.controller;

import control.events.IInputControllerListener;

/**
 * @author manuel
 *
 */
public interface IInputController
{
	public IControllerMetadata getMetadataController();
	
	public void startController() throws Exception;
	
	public void stopController() throws Exception;
	 
	public void addInputControllerListener( IInputControllerListener listener );
	
	public void removeInputControllerListener( IInputControllerListener listener );
	
	public IInputControllerListener[] getListener();
	
	//public void clearInputControllerListener();
}
