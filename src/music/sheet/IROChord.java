package music.sheet;

import java.util.ArrayList;
import java.util.List;

import org.jfugue.midi.MidiDefaults;
import org.jfugue.midi.MidiDictionary;
import org.jfugue.theory.Note;

import tools.MusicSheetTools;

public class IROChord 
{
	private List< Note > notes;
	
	private String trackID;

	private int tempo;
	private int voice;
	private int layer;
	
	private String instrument; 
	
	public IROChord( String track ) 
	{
		this.trackID = track;
		
		this.notes = new ArrayList< Note >();
		
		this.tempo = MidiDefaults.DEFAULT_TEMPO_BEATS_PER_MINUTE;
		this.voice = 0;
		this.layer = 0;		
		
		this.instrument = MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( MidiDefaults.META_INSTRUMENT_NAME );		
	}
	
	public void addNote( Note note )
	{
		this.notes.add( note );
	}
	
	public void addNotes( List< Note > notes )
	{
		this.notes.addAll( notes );
	}
	
	public List< Note > getNotes()
	{
		return this.notes;
	}
	
	public String getStrackID()
	{
		return this.trackID;
	}
	
	public void setInstrument( String instr )
	{
		this.instrument = instr;
	}
	
	public String getInstrument() 
	{
		return instrument;
	}
	
	public void setTempo(int tempo) 
	{
		this.tempo = tempo;
	}
	
	public int getTempo() 
	{
		return tempo;
	}
	
	public void setVoice(int voice) 
	{
		this.voice = voice;
	}
	
	public int getVoice() 
	{
		return voice;
	}
	
	public void setLayer(int layer) 
	{
		this.layer = layer;
	}
	
	public int getLayer() 
	{
		return layer;
	}
	
	@Override
	public String toString() 
	{
		List< List< Note > > aux = new ArrayList< List< Note > >();
		aux.add( this.notes );
		return MusicSheetTools.createPattern( this.tempo, this.voice, this.layer, this.instrument, aux ).toString();
	}
}
