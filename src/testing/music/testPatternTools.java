package testing.music;

import java.util.ArrayList;
import java.util.List;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;

import general.ArrayTreeMap;
import tools.MusicSheetTools;

public class testPatternTools {

	public static void main(String[] args) 
	{
		List< Note > notes = new ArrayList< Note >();
		
		System.out.println("PatternTools.createChordFromNotes( null ) " + MusicSheetTools.createChordFromNotes( null )  );
		System.out.println("PatternTools.createChordFromNotes( null, time ) " + MusicSheetTools.createChordFromNotes( null, 0.4 )  );
		System.out.println("PatternTools.createChordFromNotes( empty ) " + MusicSheetTools.createChordFromNotes( notes )  );
		System.out.println("PatternTools.createChordFromNotes( empty, time ) " + MusicSheetTools.createChordFromNotes( notes, 0.3 )  );
		
		
		for( char n = 'A'; n <= 'G'; n++ )
		{
			notes.add( new Note( n + "" ) );
		}
		
		System.out.println( "PatternTools.createChordFromNotes " + MusicSheetTools.createChordFromNotes( notes ) );
		System.out.println( "PatternTools.createChordFromNotes " + MusicSheetTools.createChordFromNotes( notes, 1.1 ) );
		
		System.out.println( "PatternTools.createInstrumentCommand " + MusicSheetTools.createInstrumentCommand( (byte)0 ));
		System.out.println( "PatternTools.createInstrumentCommand " + MusicSheetTools.createInstrumentCommand( "Flute" ) );
		
		System.out.println( "PatternTools.createLayerCommand " + MusicSheetTools.createLayerCommand( 1 ) );
		System.out.println( "PatternTools.createTempoCommand " + MusicSheetTools.createTempoCommand( 53 ) );
		System.out.println( "PatternTools.createVoiceCommand " + MusicSheetTools.createVoiceCommand( 1 ) );
		
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
		
		System.out.println( "PatternTools.createPattern( noteList ) " + MusicSheetTools.createPattern( noteList ) );
		System.out.println( "PatternTools.createPattern( instr, noteList ) " + MusicSheetTools.createPattern( "Flute", noteList ) );
		System.out.println( "PatternTools.createPattern( tempo, instr, noteList ) " + MusicSheetTools.createPattern(50, "Flute", noteList ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, instr, noteList ) " + MusicSheetTools.createPattern(50, 5, "Flute", noteList ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, layer, instr, noteList ) " + MusicSheetTools.createPattern(50, 5,3, "Flute", noteList ) );
				
		System.out.println( "PatternTools.createPattern( noteTime ) " + MusicSheetTools.createPattern( noteTime ) );
		System.out.println( "PatternTools.createPattern( instr, noteTime ) " + MusicSheetTools.createPattern( "Flute", noteTime ) );
		System.out.println( "PatternTools.createPattern( tempo, instr, noteTime ) " + MusicSheetTools.createPattern(50, "Flute", noteTime ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, instr, noteTime ) " + MusicSheetTools.createPattern(50, 5, "Flute", noteTime ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, layer, instr, noteTime ) " + MusicSheetTools.createPattern(50, 5,3, "Flute", noteTime ) );
		
		noteList.clear();
		System.out.println( "PatternTools.createPattern( empty ) " + MusicSheetTools.createPattern( noteList ) );
		System.out.println( "PatternTools.createPattern( instr, empty ) " + MusicSheetTools.createPattern( "Flute", noteList ) );
		System.out.println( "PatternTools.createPattern( tempo, instr, empty ) " + MusicSheetTools.createPattern(50, "Flute", noteList ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, instr, empty ) " + MusicSheetTools.createPattern(50, 5, "Flute", noteList ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, layer, instr, empty ) " + MusicSheetTools.createPattern(50, 5,3, "Flute", noteList ) );
				
		noteTime.clear();
		System.out.println( "PatternTools.createPattern( empty ) " + MusicSheetTools.createPattern( noteTime ) );
		System.out.println( "PatternTools.createPattern( instr, empty ) " + MusicSheetTools.createPattern( "Flute", noteTime ) );
		System.out.println( "PatternTools.createPattern( tempo, instr, empty ) " + MusicSheetTools.createPattern(50, "Flute", noteTime ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, instr, empty ) " + MusicSheetTools.createPattern(50, 5, "Flute", noteTime ) );
		System.out.println( "PatternTools.createPattern( tempo, voice, layer, instr, empty ) " + MusicSheetTools.createPattern(50, 5,3, "Flute", noteTime ) );
		
	}

}
