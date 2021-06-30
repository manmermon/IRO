/**
 * 
 */
package testing.music.player;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Track;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;

import music.jfugueIRO.PlayerMod;
import music.sheet.IROTrack;
import music.sheet.MusicSheet;
import music.sheet.io.IROMusicParserListener;
import tools.MusicSheetTools;

/**
 * @author manuel
 *
 */
public class testPlayerMute
{
	public static void main(String[] args) 
	{
		try
		{
			File midiMusicSheetFile = new File( "./sheets/simpsons.mid");
			
			IROMusicParserListener tool = new IROMusicParserListener();
			MidiParser parser = new MidiParser();
			parser.addParserListener( tool );
			parser.parse( MidiSystem.getSequence( midiMusicSheetFile ) );

			MusicSheet music = tool.getSheet();
						
			System.out.println( "testPlayerMute.main() " + music.getNumberOfTracks() );
			
			final PlayerMod player = new PlayerMod();
			Pattern pat = new Pattern();
			for( IROTrack track : music.getTracks() )
			{
				pat.add( track.getPatternTrackSheet() );
			}
			
			System.out.println("testPlayerMute.main() " + pat );
			
			player.load( pat.toString() );
			
			Thread t = new Thread()
			{
				public void run() 
				{
					player.play();
				}
			};
			
			t.start();
			
			Thread.sleep( 4000L );
			player.getManagedPlayer().muteTrack( -1, true);
			Thread.sleep( 4000L );
			player.getManagedPlayer().muteTrack( -1, false);
			
		}
		catch ( InterruptedException | IOException 
				| InvalidMidiDataException | MidiUnavailableException  ex)
		{
			ex.printStackTrace();
		
		}

		
	}
}
