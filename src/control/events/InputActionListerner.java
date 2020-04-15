/**
 * 
 */
package control.events;

import java.util.EventListener;

/**
 * @author manuel
 *
 */
public interface InputActionListerner extends EventListener
{
	public void InputAction( InputActionEvent  ev);
}
