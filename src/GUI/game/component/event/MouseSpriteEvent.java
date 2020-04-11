package GUI.game.component.event;

import java.awt.event.MouseEvent;
import java.util.EventObject;

public class MouseSpriteEvent extends EventObject 
{
	/**
	 * @author Manuel Merino Monge
	 *
	 */
	private static final long serialVersionUID = -7219649449185290212L;
		
	private MouseEvent event;
	
	public MouseSpriteEvent( Object source, MouseEvent ev ) 
	{
		super( source );
		
		this.event = ev;
	}
	
	public MouseEvent getMouseEvent()
	{
		return this.event;
	}

}