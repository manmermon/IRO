package testing.music.player;

import java.util.ArrayList;
import java.util.List;

import org.jfugue.theory.Note;

import music.player.IROPlayer;

public class testIROPlayer 
{
	public static void main(String[] args) 
	{	
		IROPlayer player = new IROPlayer();
		
		String[] pat = new String[] { "C3h.a70d0", "F3wha70d0", "E4/0.5833333333333334a70d0", "F3wha70d0" };
		double wtime = ( 63.0 / 73 ) * 4;
		long st[] = new long[] { (long)( 1000 * wtime * .75 ), (long)( wtime * 1.5 * 1000 ), (long)( 1000 * wtime *0.5833333333333334 ), (long)( wtime * 1.5 * 1000 ) };

		for( int i = 0; i < pat.length; i++ )
		{	
			List< Note > notes = new ArrayList< Note >();
		
			notes.add( new Note( pat[ i ]) );
		
			try {
				String id = player.loadNotes( notes, "piano", 0, 0, 73 );
				player.play( id );
				Thread.sleep( st[ i ] );
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try
		{
			player.stop();
			Thread.sleep( 10 * 1_000 );
		} catch (InterruptedException ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
}
