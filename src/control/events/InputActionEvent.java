/**
 * 
 */
package control.events;

import java.util.EventObject;

import config.IOwner;


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
	
	private IOwner owner = null;
	
	public InputActionEvent( Object source, IOwner actionOwner ) 
	{
		this( source, ACTION_NONE, actionOwner );
	}
	
	public InputActionEvent( Object source, int type, IOwner actionOwner ) 
	{
		super( source );
		
		this.typeEvent = type;
		
		this.owner = actionOwner;
	}
	
	public int getType()
	{
		return this.typeEvent;
	}
	
	public IOwner getActionOwner()
	{
		return this.owner;
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
