/**
 * 
 */
package control.events;

import java.util.EventObject;

/**
 * @author manuel
 *
 */
public class InputControllerEvent extends EventObject 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3231082679807242646L;
	/**
	 * @author Manuel Merino Monge
	 *
	 */
	
	public static final int RECOVER_DONE = -1;
	public static final int ACTION_DONE = 0;
	
	
	private int typeEvent;
	
	public InputControllerEvent( Object source ) 
	{
		super( source );
	}
	
	public InputControllerEvent( Object source, int type ) 
	{
		super( source );
		
		this.typeEvent = type;
	}
	
	public int getType()
	{
		return this.typeEvent;
	}
}
