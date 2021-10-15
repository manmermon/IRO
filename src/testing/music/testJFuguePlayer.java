package testing.music;

import java.io.File;
import java.util.Arrays;

import javax.sound.midi.MidiSystem;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.midi.MidiParser;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.realtime.RealtimePlayer;
import org.jfugue.theory.Note;
import org.jfugue.tools.ComputeDurationForEachTrackTool;
import org.jfugue.tools.GetInstrumentsUsedTool;
import org.jfugue.tools.GetPatternStats;

import gui.game.screen.level.music.BackgroundMusic;
import music.jfugueIRO.PlayerMod;
import music.player.IROPlayer;
import music.sheet.IROTrack;
import music.sheet.MusicSheet;
import music.sheet.io.IROMusicParserListener;

public class testJFuguePlayer 
{
	public static void main(String[] args) 
	{
		try
		{	
			String path = "G:\\Sync_datos\\WorkSpace\\GitHub\\IRO\\IRO\\src\\sheets\\zeldaLink2Past.mid";
			path = ".\\sheets\\zelda.mid";
			//path = "G:\\Sync_datos\\WorkSpace\\GitHub\\IRO\\IRO\\src\\sheets\\test5.mid";
			File midiMusicSheelFile = new File( path );

			IROMusicParserListener listener = new IROMusicParserListener();
			MyParserListener list = new MyParserListener();

			MidiParser parser = new MidiParser();
			parser.addParserListener(listener);
			parser.addParserListener( list );
			parser.parse( MidiSystem.getSequence( midiMusicSheelFile ) );

			PlayerMod player = new PlayerMod();
			
			MusicSheet sheet = listener.getSheet();

			String pat = "";
			for( IROTrack t : sheet.getTracks() )
			{
				t.setTempo( 120 );
				pat += t.toString();
			}						
			
			Pattern p = new Pattern(  pat );
			System.out.println("testJFuguePlayer.main() " + p);
			player.load( p );
			
			long t = System.currentTimeMillis();
			
			//(new Player()).play( pat );
			
			player.play();
			System.out.println("testJFuguePlayer.main() " + ( System.currentTimeMillis()-t)/1e3D);
			Thread.sleep( 2000L );			
			System.out.println("testJFuguePlayer.main() " + ( System.currentTimeMillis()-t)/1e3D);
			player.getManagedPlayer().finish();
			player.getManagedPlayer().reset();
			
			pat = "";
			for( IROTrack tr : sheet.getTracks() )
			{
				tr.setTempo( 73 );
				pat += tr.toString();
			}						
			
			p = new Pattern(  pat );

			player = new PlayerMod();
			player.load( pat );
			player.play();
			
			Thread.sleep( 5000L );
			
			player.getManagedPlayer().finish();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static class MyParserListener extends ParserListenerAdapter 
	{    
	    @Override
	    public void onNoteParsed(Note note) 
	    {
	        //A "C" note is in the 0th position of an octave
	        System.out.println("Note pushed at position " + note.getPositionInOctave());
	    }
	}
}
