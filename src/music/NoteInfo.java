package music;

public class NoteInfo 
{	
	private char note;
	private int octave;
	private String type;
	private int alter;
	
	public NoteInfo() 
	{
		this.note = IROTrack.NOTE_REST;
	}
	
	public void setNote( char n )
	{
		this.note = n;
	}
	
	public char getNote() 
	{
		return this.note;
	}
	
	public void setOctave( int oct )
	{
		this.octave = oct;
	}
	
	public int getOctave()
	{
		return this.octave;
	}
	
	public void setAlter( int alter )
	{
		this.alter = alter;
	}
	
	public int getAlter()
	{
		return this.alter;
	}
	
	public void setType( String t )
	{
		this.type = t;
	}
	
	public String getType()
	{
		return this.type;
	}
}
