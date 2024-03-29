package music.jfugueIRO.event;

import java.util.EventObject;

public class MusicPlayerEvent extends EventObject 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -412370914758652517L;
	/**
	 * @author Manuel Merino Monge
	 *
	 */	
	
	public static final int FINISH = -1;
	public static final int START = 0;
	public static final int PAUSE = 1;
	public static final int RESUME = 1;
	
	private int typeEvent;
	
	public MusicPlayerEvent( Object source ) 
	{
		super( source );
	}
	
	public MusicPlayerEvent( Object source, int type ) 
	{
		super( source );
		
		this.typeEvent = type;
	}
	
	public int getType()
	{
		return this.typeEvent;
	}
}
