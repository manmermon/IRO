/**
 * 
 */
package control.inputs;

import control.events.IInputControllerListener;

/**
 * @author manuel
 *
 */
public interface IInputController
{
	public void addInputControllerListener( IInputControllerListener listener );
	
	public void removeInputControllerListener( IInputControllerListener listener );
}
