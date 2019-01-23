package testing;

import java.util.ArrayList;
import java.util.List;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;

import general.ArrayTreeMap;
import tools.PatternTools;

public class testPatternTools {

	public static void main(String[] args) 
	{
		List< Note > notes = new ArrayList< Note >();
		
		System.out.println("PatternTools.createChordFromNotes( null ) " + PatternTools.createChordFromNotes( null )  );
		System.out.println("PatternTools.createChordFromNotes( null, time ) " + PatternTools.createChordFromNotes( null, 0.4 )  );
		System.out.println("PatternTools.createChordFromNotes( empty ) " + PatternTools.createChordFromNotes( notes )  );
		System.out.println("PatternTools.createChordFromNotes( empty, time ) " + PatternTools.createChordFromNotes( notes, 0.3 )  );
		
		
		for( char n = 'A'; n <= 'G'; n++ )
		{
			notes.add( new Note( n + "" ) );
		}
		
		System.out.println( "PatternTools.createChordFromNotes " + PatternTools.createChordFromNotes( notes ) );
		System.out.println( "PatternTools.createChordFromNotes " + PatternTools.createChordFromNotes( notes, 1.1 ) );
		
		System.out.println( "PatternTools.createInstrumentCommand " + PatternTools.createInstrumentCommand( (byte)0 ));
		System.out.println( "PatternTools.createInstrumentCommand " + PatternTools.createInstrumentCommand( "Flute" ) );
		
		System.out.println( "PatternTools.createLayerCommand " + PatternTools.createLayerCommand( 1 ) );
		System.out.println( "PatternTools.createTempoCommand " + PatternTools.createTempoCommand( 53 ) );
		System.out.println( "PatternTools.createVoiceCommand " + PatternTools.createVoiceCommand( 1 ) );
		
		List< List< Note > > noteList = new ArrayList< List< Note > >();
		for( int i = 0; i < 5; i++ )
		{
			noteList.add( notes );
		}
		
		ArrayTreeMap< Double, Note > noteTime = new ArrayTreeMap< Double, Note >();
		for( int i = 0; i < 5; i++ )
		{
			noteTime.put( new Double( i ), notes );
		}
		
		System.out.println( "PatternTools.createPattern( noteList ) " + PatternTools.createPattern( noteList ) );
		System.out.println( "PatternTools.createPattern( instr, noteList ) " + PatternTools.createPattern( "Flute", noteList ) );
		System.out.println( "PatternTools.createPattern( tempo, instr, noteList ) " + PatternTools.createPattern(50, "Flute", noteList ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, instr, noteList ) " + PatternTools.createPattern(50, 5, "Flute", noteList ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, layer, instr, noteList ) " + PatternTools.createPattern(50, 5,3, "Flute", noteList ) );
				
		System.out.println( "PatternTools.createPattern( noteTime ) " + PatternTools.createPattern( noteTime ) );
		System.out.println( "PatternTools.createPattern( instr, noteTime ) " + PatternTools.createPattern( "Flute", noteTime ) );
		System.out.println( "PatternTools.createPattern( tempo, instr, noteTime ) " + PatternTools.createPattern(50, "Flute", noteTime ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, instr, noteTime ) " + PatternTools.createPattern(50, 5, "Flute", noteTime ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, layer, instr, noteTime ) " + PatternTools.createPattern(50, 5,3, "Flute", noteTime ) );
		
		noteList.clear();
		System.out.println( "PatternTools.createPattern( empty ) " + PatternTools.createPattern( noteList ) );
		System.out.println( "PatternTools.createPattern( instr, empty ) " + PatternTools.createPattern( "Flute", noteList ) );
		System.out.println( "PatternTools.createPattern( tempo, instr, empty ) " + PatternTools.createPattern(50, "Flute", noteList ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, instr, empty ) " + PatternTools.createPattern(50, 5, "Flute", noteList ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, layer, instr, empty ) " + PatternTools.createPattern(50, 5,3, "Flute", noteList ) );
				
		noteTime.clear();
		System.out.println( "PatternTools.createPattern( empty ) " + PatternTools.createPattern( noteTime ) );
		System.out.println( "PatternTools.createPattern( instr, empty ) " + PatternTools.createPattern( "Flute", noteTime ) );
		System.out.println( "PatternTools.createPattern( tempo, instr, empty ) " + PatternTools.createPattern(50, "Flute", noteTime ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, instr, empty ) " + PatternTools.createPattern(50, 5, "Flute", noteTime ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, layer, instr, empty ) " + PatternTools.createPattern(50, 5,3, "Flute", noteTime ) );
		
	}

}
