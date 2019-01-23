package control.events;

import java.util.EventObject;

public class SceneEvent extends EventObject 
{
	/**
	 * @author Manuel Merino Monge
	 *
	 */
	private static final long serialVersionUID = 7186425825948198038L;

	public static final int END = -1;
	public static final int START = 0;
	
	private int typeEvent;
	
	public SceneEvent( Object source ) 
	{
		super( source );
	}
	
	public SceneEvent( Object source, int type ) 
	{
		super( source );
		
		this.typeEvent = type;
	}
	
	public int getType()
	{
		return this.typeEvent;
	}
}
