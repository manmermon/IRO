/**
 * 
 */
package control.events;

import java.util.EventObject;


/**
 * @author manuel
 *
 */
public class InputActionEvent extends EventObject 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6931189272260263408L;
	
	public static final int RECOVER_DONE = -1;
	public static final int ACTION_NONE = 0;
	public static final int ACTION_DONE = 1;

	private int typeEvent;
	
	public InputActionEvent( Object source ) 
	{
		this( source, ACTION_NONE );
	}
	
	public InputActionEvent( Object source, int type ) 
	{
		super( source );
		
		this.typeEvent = type;
	}
	
	public int getType()
	{
		return this.typeEvent;
	}
	
	/*(non-Javadoc)
	 * @see @see java.util.EventObject#toString()
	 */
	@Override
	public String toString()
	{
		return "type = " + this.typeEvent;
	}
}
