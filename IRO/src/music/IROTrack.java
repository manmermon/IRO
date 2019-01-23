package music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jfugue.midi.MidiDefaults;
import org.jfugue.midi.MidiDictionary;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;

import general.ArrayTreeMap;
import tools.PatternTools;

public class IROTrack 
{	
	public static final char NOTE_REST = 'R';
	public static final char NOTE_DO = 'C';
	public static final char NOTE_RE = 'D';
	public static final char NOTE_MI = 'E';
	public static final char NOTE_FA = 'F';
	public static final char NOTE_SOL = 'G';
	public static final char NOTE_LA = 'A';
	public static final char NOTE_SI = 'B';
	
	
	private String ID;
	private String instrument;
	private ArrayTreeMap< Double, Note > notes;	 // Pressed time of notes	
	private int tempo = MidiDefaults.DEFAULT_TEMPO_BEATS_PER_MINUTE;
	private int voiceID = 0;
	
	private double trackDuration = 0.0D;

	public IROTrack() 
	{
		this( "track-" + System.currentTimeMillis() );
	}
	
	public IROTrack( String trackID ) 
	{
		this.ID = trackID;
		
		this.instrument = "Piano";
		
		this.notes = new ArrayTreeMap< Double, Note >();
	}
	
	public void setID( String id )
	{
		this.ID = id;
	}
	
	public String getID( )
	{
		return this.ID;
	}
	
	public void setTempo( int t )
	{		
		this.tempo = t;
	}
	
	public int getTempo( )
	{
		return this.tempo;
	}
	
	public boolean setInstrument( String instr )
	{
		boolean ok = instr != null;
		
		if( ok )
		{
			ok = MidiDictionary.INSTRUMENT_STRING_TO_BYTE.containsKey( instr.toUpperCase() );
			
			if( !ok )
			{
				try
				{
					byte num = new Byte( instr.replaceAll( "\\D", "" ) );
				
					instr = MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( num );
					
					if( instr == null )
					{
						instr = "PIANO";
					}
				}
				catch (Exception e) 
				{
				}
			}
			
			this.instrument = instr;
		}
		
		return ok;
	}
	
	public String getInstrument()
	{
		return this.instrument;
	}
	
	public void addNotes( Note NOTE )
	{
		Double time = this.getMaxNoteStartTime();
		
		this.addNote( time, NOTE );
	}
	
	public void addNotes( List< Note > NOTES )
	{	
		Double time = this.getMaxNoteStartTime();
		
		this.addNotes( time, NOTES );		
	}
	
	public void addNote( double time, Note NOTE )
	{
		this.notes.put( time, NOTE );
		
		this.adjustTrackDuration();
	}
	
	public void addNotes( double time, List< Note > NOTES )
	{
		this.notes.put( time, NOTES );
		
		this.adjustTrackDuration();
	}
	
	private void adjustTrackDuration()
	{
		double startTime = Double.MAX_VALUE;
		double endTime = Double.MIN_VALUE;
		
		for( Double time : this.notes.keySet() )
		{
			if( time < startTime )
			{
				startTime = time;
			}
			
			List< Note > Notes = this.notes.get( time );
			
			double finTime = time;
			for( Note note : Notes )
			{
				double noteDuration = note.getDuration() * 4; // To do quarter duration equal to 1
				noteDuration *= ( 60.0D / this.tempo ); 
				finTime = ( finTime >= time + noteDuration ) ? ( finTime ) : ( time + noteDuration );
			}
			
			if( endTime < finTime )
			{
				endTime = finTime;
			}
		}
		
		this.trackDuration = endTime - startTime;
	}
	
	public double getTrackDuration()
	{
		return this.trackDuration;
	}
	
	private double getMaxNoteStartTime()
	{
		Double time = 0.0D;
		
		Set< Double > startTime = this.notes.keySet();
		if( !startTime.isEmpty() )
		{
			Double maxTime = Collections.max( startTime );
			time = maxTime;		

			for( Note note : this.notes.get( maxTime ) )
			{
				if( maxTime + note.getDuration() > time )
				{
					time = maxTime + note.getDuration();
				}
			}
		}
		
		return time;
	}
	
	public void setVoice( int voice )
	{
		this.voiceID = voice;
	}
	
	public int getVoice( )
	{
		return this.voiceID;
	}
	
	/*
	public void removeNote( int pos )
	{
		this.notes.remove( pos );		
	}
	*/
		
	public ArrayTreeMap< Double, Note >  getTrackNotes()
	{
		return this.notes;
	}
	
	public Pattern getPatternTrackSheet()
	{	
		return PatternTools.createPattern( this.tempo, this.voiceID, this.instrument, this.notes );
	}
	
	public String toString()
	{
		String trackText = "T" + this.tempo + " V" + this.voiceID + " I[" + this.instrument + "] ";
		
		List< Double > TIMES = new ArrayList< Double >( this.notes.keySet() );
		Collections.sort( TIMES );
		
		for( Double time : TIMES )
		{
			trackText += "@" + time + " ";
			
			List< Note > Notes = this.notes.get( time );
			for( int i = 0; i < Notes.size() - 1; i++ )
			{
				trackText += Notes.get( i ).toString() + "+";
			}

			trackText += Notes.get( Notes.size() - 1 ).toString() ;
			trackText += " ";
		}
		
		return trackText.replace( "  ", " " );
	}
	
	public boolean isEmpty()
	{
		return this.notes.isEmpty();
	}
}
