package testing;

import java.io.File;
import java.util.Arrays;

import javax.sound.midi.MidiSystem;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.theory.Note;
import org.jfugue.tools.ComputeDurationForEachTrackTool;
import org.jfugue.tools.GetInstrumentsUsedTool;
import org.jfugue.tools.GetPatternStats;

import io.IROMusicParserListener;
import music.MusicSheet;
import music.IROTrack;

public class testJFuguePlayer 
{
	public static void main(String[] args) 
	{
		try
		{	
			String path = "G:\\Sync_datos\\WorkSpace\\GitHub\\IRO\\IRO\\src\\sheets\\zeldaLink2Past.mid";
			path = "G:\\Sync_datos\\WorkSpace\\GitHub\\IRO\\IRO\\src\\sheets\\zelda.mid";
			//path = "G:\\Sync_datos\\WorkSpace\\GitHub\\IRO\\IRO\\src\\sheets\\test5.mid";
			File midiMusicSheelFile = new File( path );

			IROMusicParserListener listener = new IROMusicParserListener();

			MidiParser parser = new MidiParser();
			parser.addParserListener(listener);
			parser.parse( MidiSystem.getSequence( midiMusicSheelFile ) );

			Player player = new Player();

			MusicSheet sheet = listener.getSheet();

			String pat = "";
			for( IROTrack t : sheet.getTracks() )
			{
				if( t.getInstrument().equals( "Piano" ) ) 
				{
					pat += t.toString();
				}
			}
			
			long t = System.currentTimeMillis();
			for( int i = 0; i < 5; i++ )
			{
				player.play( "T73 E4/0.5833333333333334a100d0" );
			}
			
			player.play( pat );
			System.out.println("testJFuguePlayer.main() " + ( System.currentTimeMillis()-t)/1e3D);

			Pattern patron = MidiFileManager.loadPatternFromMidi( midiMusicSheelFile );
			player.play( patron );

		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}






	}
}
