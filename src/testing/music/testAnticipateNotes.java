package testing.music;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;

import org.jfugue.devtools.DiagnosticParserListener;
import org.jfugue.devtools.MidiDevicePrompt;
import org.jfugue.midi.MidiParser;
import org.jfugue.midi.MidiTools;
import org.jfugue.player.Player;
import org.jfugue.temporal.TemporalPLP;
import org.staccato.StaccatoParser;

import music.jfugueIRO.PlayerMod;
import music.sheet.io.IROMusicParserListener;
import testing.music.testJFuguePlayer.MyParserListener;

public class testAnticipateNotes {

	private static final String MUSIC = "Ch R R Dh Eh Fh Gh Ah B";
	public static void main(String[] args) 
	{
		try
		{
			String path = "G:\\Sync_datos\\WorkSpace\\GitHub\\IRO\\IRO\\src\\sheets\\zeldaLink2Past.mid";
			path = ".\\sheets\\zelda.mid";
			//path = "G:\\Sync_datos\\WorkSpace\\GitHub\\IRO\\IRO\\src\\sheets\\test5.mid";
			File midiMusicSheelFile = new File( path );
	
			Map<Long, List<MidiMessage>> map = MidiTools.sortMessagesByTick( MidiSystem.getSequence( midiMusicSheelFile ) );
			System.out.println("testAnticipateNotes.main() " + map );
			/*
			MidiParser parser = new MidiParser();
			TemporalPLP plp = new TemporalPLP();
		    parser.addParserListener(plp);
		    parser.parse( MidiSystem.getSequence( midiMusicSheelFile ) );
		    
			PlayerMod player = new PlayerMod();
		
	        // Part 2. Send the events from Part 1, and play the original music with a delay
	        DiagnosticParserListener dpl = new DiagnosticParserListener(); // Or your AnimationParserListener!
	        plp.addParserListener(dpl);
	        player.load( MidiSystem.getSequence( midiMusicSheelFile ) );
	        player.play();
	        System.out.println("testAnticipateNote.main()");
	        plp.parse();
	        */
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
