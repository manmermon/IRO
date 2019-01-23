package testing;

import java.util.ArrayList;
import java.util.List;

import org.jfugue.player.Player;
import org.jfugue.theory.Note;

import control.PlayerControl;
import music.IROTrack;

public class testPlayerControl 
{
	public static void main(String[] args) 
	{
		PlayerControl ctrPlay;
		try 
		{
			/*
			ctrPlay = new PlayerControl( null, 73 );
			ctrPlay.startThread();
			 */
			List< IROTrack > tracks = new ArrayList< IROTrack >();

			IROTrack track1 = new IROTrack( "track-test-1" );
			track1.setTempo( 73 );

			track1.addNote( 0.0, new Note( "Rs" ) );
			track1.addNote( 0.0, new Note( "F3h.a100d0" ) );
			track1.addNote( 0.2054794520547945, new Note("F4ta100d0") );
			track1.addNote( 0.3082191780821918, new Note( "Rs" ) );
			track1.addNote( 0.3082191780821918, new Note( "A4/0.65625a100d0" ) );	
			track1.addNote( 0.410958904109589, new Note( "D5/0.625a100d0" ) );
			track1.addNote( 0.5136986301369862, new Note( "E5/0.59375a100d0" ) );
			track1.setInstrument( "Piano" );
						

			Player pl = new Player();
			long t = System.currentTimeMillis();
			pl.play( track1.toString() );
			System.out.println("testPlayerControl.main() " + ( ( System.currentTimeMillis() - t ) / 1e3D ) );
			
			IROTrack track2 = new IROTrack( "track-test-2" );

			track2.addNote( 0.410958904109589, new Note( "D5/0.625a100d0" ) );
			track2.addNote( 0.5136986301369862, new Note( "E5/0.59375a100d0" ) );
			track2.setTempo( 73 );
			track2.setInstrument( "Piano" );
			
			/*
			tracks.add( track1 );		
			ctrPlay.playNotes( tracks );

			tracks.clear();
			tracks.add( track2 );
			ctrPlay.playNotes( tracks );
			*/
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
