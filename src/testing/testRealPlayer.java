package testing;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.realtime.RealtimePlayer;

import JFugueMod.org.jfugue.player.PlayerMod;

public class testRealPlayer {

	public static void main(String[] args) 
	{
		RealtimePlayer real;
		try 
		{
			final String p1 = "V0 I0 "
					+ "Rs F4ta100d0 Rs E5/0.59375a100d0 "
					+ "@0.125 D5/0.625a100d0 "
					+ "@0.0 F3h.a100d0 "
					+ "@0.09375 A4/0.65625a100d0 R/0.8354166666666667 E4ta100d0 Rs. E5/0.5625a100d0 "
					//*
					+ "@1.6791666666666667 D5/0.59375a100d0 "
					+ "@1.5229166666666667 C3h.a100d0 "
					+ "@1.6479166666666667 B4/0.625a100d0 "
					+ "@1.6166666666666667 G4/0.65625a100d0 R/1.1437499999999998 D5/0.08333333333333333a100d0 "
					+ "@3.125 C4ha100d0 @3.25 F4/0.5833333333333334a100d0 "
					+ "@3.3333333333333335 A4/0.5833333333333334a100d0 "
					+ "@3.0 F3wha100d0 "
					+ "@3.5 E5wa100d0 R/0.41666666666666696 D5/0.08333333333333333a100d0 "
					+ "@4.625 G3ha100d0 " 
					+ "@4.75 E4/0.5833333333333334a100d0 "
					+ "@4.84375 G4/0.5833333333333334a100d0 "
					+ "@5.0 E5wa100d0 "
					+ "@4.5 C3wha100d0 R/0.41666666666666696 D5/0.08333333333333333a100d0"
					//*/
					;
			
			final String p2 = "V1 I48 "
					+ "Rs C4ta100d0 Rs D5/0.59375a100d0 "
					+ "@0.125 A5/0.625a100d0 "
					+ "@0.0 B3h.a100d0 "
					+ "@0.09375 D4/0.65625a100d0 R/0.8354166666666667 F4ta100d0 Rs. A5/0.5625a100d0 "
					//*
					+ "@1.6791666666666667 D5/0.59375a100d0 "
					+ "@1.5229166666666667 C3h.a100d0 "
					+ "@1.6479166666666667 B4/0.625a100d0 "
					+ "@1.6166666666666667 G4/0.65625a100d0 R/1.1437499999999998 D5/0.08333333333333333a100d0 "
					+ "@3.125 C4ha100d0 @3.25 F4/0.5833333333333334a100d0 "
					+ "@3.3333333333333335 A4/0.5833333333333334a100d0 "
					+ "@3.0 F3wha100d0 "
					+ "@3.5 E5wa100d0 R/0.41666666666666696 D5/0.08333333333333333a100d0 "
					+ "@4.625 G3ha100d0 " 
					+ "@4.75 E4/0.5833333333333334a100d0 "
					+ "@4.84375 G4/0.5833333333333334a100d0 "
					+ "@5.0 E5wa100d0 "
					+ "@4.5 C3wha100d0 R/0.41666666666666696 D5/0.08333333333333333a100d0"
					//*/
					;
			
			double t = 60.D / 100;
			String p3 = "T100 V0 I[Piano] @0.0 F5q.a24d0 @0.375 G5ia28d0 @0.5 A5qa28d0 @0.75 A5qa27d0 T100 V1 I[Piano] @0.0 F4qa32d0 @0.25 C5qa37d0 @0.5 F4qa31d0 @0.75 C5qa39d0";
			String p4 = "T100 V0 I[Piano] @1.125 F5/0.11822916666666666a23d0 @1.25 G5/0.12708333333333333a28d0 @1.375 A5/0.11822916666666666a29d0 @1.5 F5qa23d0 @1.75 C5qa24d0 T100 V1 I[Piano] @1.25 C5qa34d0 @1.5 F4qa33d0+A4qa33d0";
			String p5 = "T100 V0 I[Piano] @2.0 F5q.a29d0 @2.5 A5qa27d0 @2.75 A5qa27d0 T100 V1 I[Piano] @2.0 F4qa30d0 @2.25 C5qa39d0 @2.5 F4qa31d0 @2.75 C5qa39d0";
			String p6 = "T100 V0 I[Piano] @3.0 G5/0.12708333333333333a23d0 @3.125 F5/0.11822916666666666a21d0 @3.25 G5/0.12708333333333333a28d0 @3.375 A5/0.11822916666666666a30d0 @3.5 F5ha23d0 T100 V1 I[Piano] @3.25 C5qa37d0 @3.5 F4qa33d0+A4qa33d0";
			String p7 = "T100 V0 I[Piano] @4.0 F5q.a27d0 @4.5 A5qa30d0 @4.75 A5qa26d0 T100 V1 I[Piano] @4.0 F4qa26d0 @4.25 C5qa34d0 @4.5 F4qa27d0 @4.75 C5qa36d0";

			p3 = "T100 V0 I[Piano] @0.0 F5q.a71d0 @0.375 G5ia80d0 @0.5 A5qa81d0 @0.75 A5qa78d0 @1.0 G5/0.12708333333333333a73d0 T100 V1 I[Piano] @0.0 F4qa92d0 @0.25 C5qa108d0 @0.5 F4qa90d0 @0.75 C5qa112d0 @1.0 Bb4qa103d0"; 
			p4 = "T100 V0 I[Piano] @1.125 F5/0.11822916666666666a68d0 @1.2432291666666666 R/0.0067708333333333925 @1.25 G5/0.12708333333333333a81d0 @1.375 A5/0.11822916666666666a83d0 @1.4932291666666666 R/0.0067708333333333925 @1.5 F5qa67d0 @1.75 C5qa69d0 T100 V1 I[Piano] @1.25 C5qa98d0 @1.5 F4qa97d0+A4qa97d0 @1.75 Rq"; 
			p5 = "T100 V0 I[Piano] @2.0 F5q.a84d0 @2.375 G5ia80d0 @2.5 A5qa78d0 @2.75 A5qa79d0 T100 V1 I[Piano] @2.0 F4qa88d0 @2.25 C5qa113d0 @2.5 F4qa90d0 @2.75 C5qa114d0";
			p6 = "T100 V0 I[Piano] @3.0 G5/0.12708333333333333a68d0 @3.125 F5/0.11822916666666666a61d0 @3.2432291666666666 R/0.0067708333333333925 @3.25 G5/0.12708333333333333a81d0 @3.375 A5/0.11822916666666666a87d0 @3.4932291666666666 R/0.0067708333333333925 @3.5 F5ha67d0"; 
			p7 = "T100 V0 I[Piano] @4.0 F5q.a79d0 @4.375 G5ia82d0 @4.5 A5qa87d0 @4.75 A5qa76d0 T100 V1 I[Piano] @4.0 F4qa77d0 @4.25 C5qa99d0 @4.5 F4qa79d0 @4.75 C5qa103d0";
			
			PlayerMod play = new PlayerMod();
			play.load( p7 );
			play.play( );
			Thread.sleep( 30_000L );
			
			
			final String pz = "T73 V0 I[Piano] @0.0 F3h.a100d0 @0.0625 F4ta100d0 @0.09375 Rs+A4/0.65625a100d0 @0.125 D5/0.625a100d0 @0.15625 E5/0.59375a100d0 @0.75 R/0.8354166666666667 V1 I[String_Ensemble_1] @0.0 Rh @0.5 F4wa100d0+A4wa100d0";
			final String pz2 = "T100 V0 I[Piano] @0.0 F5q.a71d0 @0.375 G5ia80d0 @0.5 A5qa81d0 @0.75 A5qa78d0 @1.0 G5/0.12708333333333333a73d0 T100 V1 I[Piano] @0.0 F4qa92d0 @0.25 C5qa108d0 @0.5 F4qa90d0 @0.75 C5qa112d0 @1.0 Bb4qa103d0";
			
			new Player();
			Thread t1 = new Thread()
			{
				@Override
				public void run() {
					PlayerMod p = new PlayerMod();
					System.out.println("testRealPlayer.main(...).new Thread() {...}.run() A " + p.getManagedPlayer().isPlaying());
					try
					{
						p.load( p1 );
					} catch (MidiUnavailableException | InvalidMidiDataException ex)
					{
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					p.play( );
					//p.getManagedPlayer().finish();
				}
			};
			Thread t2 = new Thread()
			{
				@Override
				public void run() {
					PlayerMod p = new PlayerMod();
					System.out.println("testRealPlayer.main(...).new Thread() {...}.run() B " + p.getManagedPlayer().isPlaying());
					try
					{
						p.load( p2 );
					} catch (MidiUnavailableException | InvalidMidiDataException ex)
					{
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					p.play( );
					//p.getManagedPlayer().finish();
				}
			};
			
			t1.start();
			Thread.sleep( 1500L );
			t2.start();

			Thread.sleep( 10_000L );
			
			real = new RealtimePlayer();
									
			real.play( p3 );		
						
			double d = 1.125;
			long tSleep = (long)( 1000 * d * t * 4 );
			tSleep = 16438;
			Thread.sleep( tSleep );
			
			real.close();
			
			
			real.play( p4 );		
			
			d = 2.0 - 1.125;
			tSleep = (long)( 1000 * d * t * 4 );
			Thread.sleep( tSleep );
			
			real.play( p5 );		
			
			d = 3.0 - 2.0;
			tSleep = (long)( 1000 * d * t * 4 );
			Thread.sleep( tSleep );
			
			real.play( p6 );		
			
			d = 4.0 - 3.0;
			tSleep = (long)( 1000 * d * t * 4 );
			Thread.sleep( tSleep );
			
			real.play( p7 );		
			
			d = 7.0 - 4.0;
			tSleep = (long)( 1000 * d * t * 4 );
			Thread.sleep( tSleep );
			
			real.close();
			//*/
			
		}
		catch ( Exception e) 
		{
			e.printStackTrace();
		}
		
	}

}
