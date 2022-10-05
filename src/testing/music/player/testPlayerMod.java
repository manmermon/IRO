package testing.music.player;

import javax.sound.midi.Sequence;

import org.jfugue.player.ManagedPlayerListener;

import music.jfugueIRO.PlayerMod;

public class testPlayerMod 
{
	public static void main(String[] args) 
	{
		Thread t1 = new Thread()
		{
			@Override
			public void run() 
			{
				try
				{
					String p = "TIME:6/4 KEY:Cmaj T73 V0 I0 V1 I48 V2 I79 V0 Rs F4ta100d0 Rs E5/0.59375a100d0 @0.125 D5/0.625a100d0 @0.0 F3h.a100d0 @0.09375 A4/0.65625a100d0 R/0.8354166666666667 E4ta100d0 Rs. E5/0.5625a100d0 @1.6791666666666667 D5/0.59375a100d0 @1.5229166666666667 C3h.a100d0 @1.6479166666666667 B4/0.625a100d0 @1.6166666666666667 G4/0.65625a100d0 "
							+ "R/1.1437499999999998 D5/0.08333333333333333a100d0 @3.125 C4ha100d0 @3.25 F4/0.5833333333333334a100d0 @3.3333333333333335 A4/0.5833333333333334a100d0 @3.0 F3wha100d0 @3.5 E5wa100d0 R/0.41666666666666696 D5/0.08333333333333333a100d0 @4.625 G3ha100d0 "
							+ " @4.75 E4/0.5833333333333334a100d0 @4.84375 G4/0.5833333333333334a100d0 @5.0 E5wa100d0 @4.5 C3wha100d0 R/0.41666666666666696 D5/0.08333333333333333a100d0";
	
					System.out.println("testPlayerMod.main(...).new Thread() {...}.run() A ");
					PlayerMod player = new PlayerMod();					
					player.load( p );
	
					player.getManagedPlayer().addManagedPlayerListener( new ManagedPlayerListener() {
						
						@Override
						public void onStarted(Sequence arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onSeek(long arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onResumed() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onReset() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onPaused() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onFinished() {
							// TODO Auto-generated method stub
							System.out.println("testPlayerMod.main() A END");
						}
					});
					
					player.play();
					System.out.println("testPlayerMod.main(...).new Thread() {...}.run() A END");
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		};
		
		Thread t2 = new Thread()
		{
			@Override
			public void run() 
			{
				try
				{
					String p = "TIME:6/4 KEY:Cmaj T180 V0 I0 V1 I48 V2 I79 V0 Rs F4ta100d0 Rs E5/0.59375a100d0 @0.125 D5/0.625a100d0 @0.0 F3h.a100d0 @0.09375 A4/0.65625a100d0 R/0.8354166666666667 E4ta100d0 Rs. E5/0.5625a100d0 @1.6791666666666667 D5/0.59375a100d0 @1.5229166666666667 C3h.a100d0 @1.6479166666666667 B4/0.625a100d0 @1.6166666666666667 G4/0.65625a100d0 "
							+ "R/1.1437499999999998 D5/0.08333333333333333a100d0 @3.125 C4ha100d0 @3.25 F4/0.5833333333333334a100d0 @3.3333333333333335 A4/0.5833333333333334a100d0 @3.0 F3wha100d0 @3.5 E5wa100d0 R/0.41666666666666696 D5/0.08333333333333333a100d0 @4.625 G3ha100d0 "
							+ " @4.75 E4/0.5833333333333334a100d0 @4.84375 G4/0.5833333333333334a100d0 @5.0 E5wa100d0 @4.5 C3wha100d0 R/0.41666666666666696 D5/0.08333333333333333a100d0";
	
					System.out.println("testPlayerMod.main(...).new Thread() {...}.run() B ");
					PlayerMod player = new PlayerMod();										
					player.load( p );
					
player.getManagedPlayer().addManagedPlayerListener( new ManagedPlayerListener() {
						
						@Override
						public void onStarted(Sequence arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onSeek(long arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onResumed() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onReset() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onPaused() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onFinished() {
							// TODO Auto-generated method stub
							System.out.println("testPlayerMod.main() B END");
						}
					});
	
					player.play();
					
					System.out.println("testPlayerMod.main(...).new Thread() {...}.run() B END ");
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		};
		
		t1.start();
		
		try {
			Thread.sleep( 100L );
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		t2.start();
	}
}
