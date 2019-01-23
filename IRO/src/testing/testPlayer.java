package testing;

import javax.sound.midi.MidiUnavailableException;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.realtime.RealtimePlayer;
import org.jfugue.theory.Note;
import org.jfugue.tools.GetInstrumentsUsedTool;

public class testPlayer {

	public static void main(String[] args) 
	{
		final Player player = new Player();
		final Player player2 = new Player();

		System.out.println("testPlayer.main() =player? " + player.getManagedPlayer().equals( player2.getManagedPlayer() ) );

		/*
		player.play("TIME:6/4 KEY:Cmaj T73 V0 I0 V1 I48 V2 I79 V0 Rs F4ta100d0 Rs E5/0.59375a100d0 @0.125 D5/0.625a100d0 @0.0 F3h.a100d0 @0.09375 A4/0.65625a100d0 R/0.8354166666666667 E4ta100d0 Rs. E5/0.5625a100d0 @1.6791666666666667 D5/0.59375a100d0 @1.5229166666666667 C3h.a100d0 @1.6479166666666667 B4/0.625a100d0 @1.6166666666666667 G4/0.65625a100d0 "
				+ "R/1.1437499999999998 D5/0.08333333333333333a100d0 @3.125 C4ha100d0 @3.25 F4/0.5833333333333334a100d0 @3.3333333333333335 A4/0.5833333333333334a100d0 @3.0 F3wha100d0 @3.5 E5wa100d0 R/0.41666666666666696 D5/0.08333333333333333a100d0 @4.625 G3ha100d0 "
				+ " @4.75 E4/0.5833333333333334a100d0 @4.84375 G4/0.5833333333333334a100d0 @5.0 E5wa100d0 @4.5 C3wha100d0 R/0.41666666666666696 D5/0.08333333333333333a100d0" );
		 */

		Pattern p = new Pattern();
		//p.add(  "Rs F4ta100d0 Rs E5/0.59375a100d0 @0.0 F3h.a100d0 @0.09375 A4/0.65625a100d0 @0.125 D5/0.625a100d0 "  );

		/*
		p.add(  "@0.0 F3h.a100d0 @0.0625 F4ta100d0 @0.09375 A4/0.65625a100d0 @0.125 D5/0.625a100d0 @0.15625 E5/0.59375a100d0"
				+ " @0.75 R/0.8354166666666667 @1.5854166667 E4ta100d0 @1.6166667 Rs. @1.71041666666 E5/0.5625a100d0 @1.6791666666666667 D5/0.59375a100d0 @1.5229166666666667 C3h.a100d0 @1.6479166666666667 B4/0.625a100d0 @1.6166666666666667 G4/0.65625a100d0"  );
		for( Token tok : p.getTokens() )
		{
			if( tok.getType() == Token.TokenType.NOTE )
			{				
				Note n = new Note( tok.toString() );
				System.out.println("testPlayer.main() " + n.toString() + " dur " + n.getDuration()  );
			}
		}
		//*/

		p.add(     );
		long t = System.currentTimeMillis();		
		//player.play( p );
		System.out.println("testPlayer.main() " + ( System.currentTimeMillis() - t ) / 1e3D);

		final String pat = "T73 V0 I[Piano] @0.0 F3wha100d0";
		for( int i = 0; i < 50; i++ )
		{
			RealtimePlayer r;
			try 
			{
				System.out.println("testPlayer.main() ITER " + i);
				
				r = new RealtimePlayer();	
				r.play( pat );
				
				Thread.sleep( 5000L );				
				r.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

		System.out.println("testPlayer.main() " + ( System.currentTimeMillis() - t ) / 1e3D);
		player.getManagedPlayer().finish();
		player2.getManagedPlayer().finish();

	}

}
