/**
 * 
 */
package GUI.game.component.event;

import java.util.EventObject;

import GUI.game.component.TrackNotesSprite;

/**
 * @author Manuel Merino Monge
 *
 */
public class FretEvent extends EventObject 
{
	private static final long serialVersionUID = 8850347687323924648L;
	
	public static final int NOTE_EXITED = -1;
	public static final int NOTE_OUT_BOUNDS = 0;
	public static final int NOTE_ENTERED = 1;
	
	private int typeEvent;
	
	private TrackNotesSprite trackNote;
	
	public FretEvent( Object source, TrackNotesSprite note ) 
	{
		this( source, note, NOTE_OUT_BOUNDS );
	}
	
	public FretEvent( Object source, TrackNotesSprite note, int type ) 
	{
		super( source );
		
		this.trackNote = note;
		this.typeEvent = type;
	}
	
	public int getType()
	{
		return this.typeEvent;
	}

	public TrackNotesSprite getNote()
	{
		return this.trackNote;
	}
}