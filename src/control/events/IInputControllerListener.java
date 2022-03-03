/**
 * 
 */
package control.events;

import lslStream.event.IInputLSLDataListener;

/**
 * @author manuel
 *
 */
public interface IInputControllerListener extends IInputLSLDataListener
{
	public void setEnableInputController( boolean enable );
}
