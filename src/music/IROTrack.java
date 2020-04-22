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
	public static final String TRACK_ID_DEFAULT_PREFIX = "track-"; 
	
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
	
	private byte maxVolumeNote = 0;
	
	//public double initTrackSheetTime = 0D; 
	
	public IROTrack( ) 
	{
		this( "track-" + System.currentTimeMillis() );
	}
	
	public IROTrack( String trackID ) 
	{
		this.ID = trackID;
		
		this.instrument = "Piano";
		
		this.notes = new ArrayTreeMap< Double, Note >();
		
	}
	
	/*
	public void setStartTrackTimeInSheet( double initTrackTime )
	{
		this.initTrackSheetTime = initTrackTime; 
	}
	//*/
	
	public void setID( String id )
	{
		this.ID = id;
	}
	
	public String getID( )
	{
		return this.ID;
	}
	
	public void setVolume( byte volume ) 
	{		
		for( List< Note > Notes : this.notes.values() )
		{
			for( Note note : Notes )
			{
				note.setOnVelocity( volume );
			}
		}
		
		this.maxVolumeNote = volume;
	}
	
	public void setVolume( int percent )
	{
		if( percent >= 0 )
		{
			double adj = percent / 100.0;
			
			for( List< Note > Notes : this.notes.values() )
			{
				for( Note note : Notes )
				{
					int v = (int)( note.getOnVelocity() * adj );
					if( v > 127 )
					{
						v = 127;
					}
					
					note.setOnVelocity( (byte) v );
				}
			}
			
			int aux = (int)( this.maxVolumeNote * adj);
			
			if( aux > 127 )
			{
				aux = 127;
			}
			
			this.maxVolumeNote = (byte) aux; 
		}
	}
	
	public void setTempo( int t )
	{		
		this.tempo = t;
		
		this.adjustTrackDuration();
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
		
		if( NOTE.getOnVelocity() > this.maxVolumeNote )
		{
			this.maxVolumeNote = NOTE.getOnVelocity();
		}
		
		this.adjustTrackDuration();
	}
	
	public void addNotes( double time, List< Note > NOTES )
	{
		this.notes.put( time, NOTES );
		
		for( Note Note : NOTES )
		{
			if( Note.getOnVelocity() > this.maxVolumeNote )
			{
				this.maxVolumeNote = Note.getOnVelocity();
			}
		}
		
		this.adjustTrackDuration();
	}
	
	public byte getMaxVolumeNote() 
	{
		return this.maxVolumeNote;
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
	
	public void shiftNoteTime( double shift )
	{
		if( shift != 0 )
		{
			ArrayTreeMap< Double, Note > newNotes = new ArrayTreeMap< Double, Note >();
					
			List< Double > originTime = new ArrayList< Double >();
			originTime.addAll( this.notes.keySet() );
			Collections.sort( originTime );
			for( Double time : originTime )
			{
				newNotes.put( time + shift, this.notes.get( time ) );
			}
			
			this.notes = newNotes;
		}
	}
	
	public ArrayTreeMap< Double, Note >  getTrackNotes()
	{
		return this.notes;
	}
	
	public double getStartTimeFirstNote()
	{
		double t = -1;
		 
		if( !this.notes.isEmpty() )
		{
			Double val = this.notes.getFirstKey();
			if( val != null )
			{
				t = val.doubleValue();
			}
		}
		
		return t;
	}
	
	/*
	public double getStartTrackTimeInSheet()
	{
		return this.initTrackSheetTime;
	}
	//*/
	
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
