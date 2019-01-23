package control.events;

import java.util.EventObject;

public class BackgroundMusicEvent extends EventObject 
{
	/**
	 * @author Manuel Merino Monge
	 *
	 */
	
	private static final long serialVersionUID = -3626186465716241291L;
	
	public static final int END = -1;
	public static final int START = 0;
	
	private int typeEvent;
	
	public BackgroundMusicEvent( Object source ) 
	{
		super( source );
	}
	
	public BackgroundMusicEvent( Object source, int type ) 
	{
		super( source );
		
		this.typeEvent = type;
	}
	
	public int getType()
	{
		return this.typeEvent;
	}

}
