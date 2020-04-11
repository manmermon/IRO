package GUI.game.component.event;

import java.util.EventObject;

public class SpriteEvent extends EventObject 
{
	/**
	 * @author Manuel Merino Monge
	 *
	 */
	private static final long serialVersionUID = 7186425825948198038L;

	public static final int OUTPUT_SCREEN = -1;
	public static final int ON_SCREEN = 0;
	public static final int TOWARD_SCREEN = 1;
	
	private int typeEvent;
	
	public SpriteEvent( Object source ) 
	{
		super( source );
	}
	
	public SpriteEvent( Object source, int type ) 
	{
		super( source );
		
		this.typeEvent = type;
	}
	
	public int getType()
	{
		return this.typeEvent;
	}

}
