/**
 * 
 */
package control.events;

import java.util.EventListener;

/**
 * @author manuel
 *
 */
public interface IInputControllerListener extends EventListener 
{
	public void InputControllerEvent( InputControllerEvent  ev );
	
	public void setEnableInputController( boolean enable );
	
	//public void enableProcessInputControllerEvent( boolean enable );
}
