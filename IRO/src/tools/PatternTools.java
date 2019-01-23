package tools;

import java.util.List;

import org.jfugue.midi.MidiDefaults;
import org.jfugue.midi.MidiDictionary;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import general.ArrayTreeMap;

public class PatternTools 
{
	public static final String NOTE_COMBINATION_SYMBOL = "+";
	public static final String TIME_SYMBOL = "@";
	public static final String VOICE_SYMBOL = "V";
	public static final String LAYER_SYMBOL = "L";
	public static final String TEMPO_SYMBOL = "T";
	public static final String INSTRUMENT_SYMBOL = "I";
	public static final String OPENED_BRACKET_SYMBOL = "[";
	public static final String CLOSED_BRACKET_SYMBOL = "]";
	public static final String BAR_NOTE_SYMBOL = "|";

	public static final String SEPARATION_SYMBOL = " ";

	public static String createVoiceCommand( int voice )
	{
		return VOICE_SYMBOL + voice;
	}
	
	public static String createInstrumentCommand( byte instr )
	{
		String instrment = MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( instr );
		
		return createInstrumentCommand( instrment );
	}
	
	public static String createInstrumentCommand( String instr )
	{
		return INSTRUMENT_SYMBOL + OPENED_BRACKET_SYMBOL + instr + CLOSED_BRACKET_SYMBOL; 
	}
	
	public static String createLayerCommand( int layer )
	{
		return LAYER_SYMBOL + layer;
	}
	
	public static String createTempoCommand( int tempo )
	{
		return TEMPO_SYMBOL + tempo;
	}
	
	public static String createChordFromNotes( List< Note > Notes )
	{
		String chord = "";

		if( Notes != null )
		{
			for( Note note : Notes )
			{
				chord += note.toString() + NOTE_COMBINATION_SYMBOL;
			}

			if( !chord.isEmpty() )
			{
				chord = chord.substring( 0, chord.length() - 1 );
			}
		}

		return chord.trim();
	}
	
	public static String createChordFromNotes( List< Note > Notes, double time )
	{
		String chord = createChordFromNotes( Notes );

		if( !chord.isEmpty() )
		{
			chord = TIME_SYMBOL + time + SEPARATION_SYMBOL + chord;
		}

		return chord;
	}
	
	public static Pattern createPattern( ArrayTreeMap< Double, Note > NotesTimes  )
	{	
		Pattern pat = new Pattern();
		for( double time : NotesTimes.keySet() )
		{
			pat.add( createChordFromNotes( NotesTimes.get( time ), time ) );
		}
		
		return pat;
	}
	
	public static Pattern createPattern( List< List< Note > > NOTES  )
	{
		Pattern pat = new Pattern();
		for( List< Note > Notes : NOTES )
		{
			pat.add( createChordFromNotes( Notes ) );
		}
		
		return pat;
	}
	
	public static Pattern createPattern( String instr, List< List< Note > > NOTES )
	{
		Pattern pat = createPattern( NOTES );
		pat.setInstrument( instr );
		
		return pat;
	}
	
	public static Pattern createPattern( int tempo, String instr, List< List< Note > > NOTES )
	{
		Pattern pat = createPattern( instr, NOTES );
		pat.setTempo( tempo );
		
		return pat;
	}
	
	public static Pattern createPattern( int tempo, int voice, String instr, List< List< Note > > NOTES )
	{
		Pattern pat = createPattern( tempo, instr, NOTES );
		pat.setVoice( voice );
		
		return pat;
	}
	
	public static Pattern createPattern( int tempo, int voice, int layer, String instr, List< List< Note > > NOTES )
	{
		Pattern pat = createPattern( tempo, voice, instr, NOTES );
		pat.setLayer( layer );
		
		return pat;
	}
	
	public static Pattern createPattern( String instr, ArrayTreeMap< Double, Note > NotesTimes )
	{
		Pattern pat = createPattern( NotesTimes );
		pat.setInstrument( instr );
		
		return pat;
	}
	
	public static Pattern createPattern( int tempo, String instr, ArrayTreeMap< Double, Note > NotesTimes )
	{
		Pattern pat = createPattern( instr, NotesTimes );
		pat.setTempo( tempo );
		
		return pat;
	}
	
	public static Pattern createPattern( int tempo, int voice, String instr, ArrayTreeMap< Double, Note > NotesTimes ) 
	{
		Pattern pat = createPattern( tempo, instr, NotesTimes );
		pat.setVoice( voice );
		
		return pat;
	}
	
	public static Pattern createPattern( int tempo, int voice, int layer, String instr, ArrayTreeMap< Double, Note > NotesTimes )
	{
		Pattern pat = createPattern( tempo, voice, instr, NotesTimes );
		pat.setLayer( layer );
		
		return pat;
	}
}
