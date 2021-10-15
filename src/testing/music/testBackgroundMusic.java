/**
 * 
 */
package testing.music;

import java.util.concurrent.CyclicBarrier;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import gui.game.screen.level.music.BackgroundMusic;

/**
 * @author Manuel Merino Monge
 *
 */
public class testBackgroundMusic {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try
		{
			final Player player = new Player();
			
			String pats[] = new String[] { "T100 @0.0 &V0,L0,I0,Rq. @0.375 &V0,L0,I0,Ri @0.5 &V0,L0,I0,Rq @0.75 &V0,L0,I0,Rq @1.0 &V0,L0,I0,G5/0.12708333333333333a73d0 @1.125 &V0,L0,I0,F5/0.11822916666666666a68d0 @1.2432291666666666 &V0,L0,I0,R/0.0067708333333333925 @1.25 &V0,L0,I0,G5/0.12708333333333333a81d0 @1.375 &V0,L0,I0,A5/0.11822916666666666a83d0 @1.4932291666666666 &V0,L0,I0,R/0.0067708333333333925 @1.5 &V0,L0,I0,F5qa67d0 @1.75 &V0,L0,I0,Rq @2.0 &V0,L0,I0,Rq. @2.375 &V0,L0,I0,G5ia80d0 @2.5 &V0,L0,I0,A5qa78d0 @2.75 &V0,L0,I0,Rq @3.0 &V0,L0,I0,R/0.12708333333333333 @3.125 &V0,L0,I0,R/0.11822916666666666 @3.2432291666666666 &V0,L0,I0,R/0.0067708333333333925 @3.25 &V0,L0,I0,G5/0.12708333333333333a81d0 @3.375 &V0,L0,I0,A5/0.11822916666666666a87d0 @3.4932291666666666 &V0,L0,I0,R/0.0067708333333333925 @3.5 &V0,L0,I0,Rh @4.0 &V0,L0,I0,Rq. @4.375 &V0,L0,I0,G5ia82d0 @4.5 &V0,L0,I0,A5qa87d0 @4.75 &V0,L0,I0,A5qa76d0 @5.0 &V0,L0,I0,R/0.12708333333333333 @5.125 &V0,L0,I0,R/0.11822916666666666 @5.243229166666667 &V0,L0,I0,R/0.0067708333333333925 @5.25 &V0,L0,I0,R/0.12708333333333333 @5.375 &V0,L0,I0,R/0.11822916666666666 @5.493229166666667 &V0,L0,I0,R/0.0067708333333333925 @5.5 &V0,L0,I0,Rq @5.75 &V0,L0,I0,Rq @6.0 &V0,L0,I0,F5q.a86d0 @6.375 &V0,L0,I0,G5ia80d0 @6.5 &V0,L0,I0,A5qa84d0 @6.75 &V0,L0,I0,A5qa79d0 @7.0 &V0,L0,I0,G5/0.12708333333333333a67d0 @7.125 &V0,L0,I0,R/0.11822916666666666 @7.243229166666667 &V0,L0,I0,R/0.0067708333333333925 @7.25 &V0,L0,I0,R/0.12708333333333333 @7.375 &V0,L0,I0,R/0.11822916666666666 @7.493229166666667 &V0,L0,I0,R/0.0067708333333333925 @7.5 &V0,L0,I0,Rh @8.0 &V0,L0,I0,Rw @9.0 &V0,L0,I0,C6ia91d0 @9.125 &V0,L0,I0,BB5ia74d0 @9.25 &V0,L0,I0,Ri @9.375 &V0,L0,I0,Ri @9.5 &V0,L0,I0,Ri @9.625 &V0,L0,I0,Ri @9.75 &V0,L0,I0,Rq @10.0 &V0,L0,I0,Ri @10.125 &V0,L0,I0,Ri @10.25 &V0,L0,I0,C6qa72d0 @10.5 &V0,L0,I0,Rh @11.0 &V0,L0,I0,C6ia66d0 @11.125 &V0,L0,I0,C6ia75d0 @11.25 &V0,L0,I0,C6qa72d0 @11.5 &V0,L0,I0,Ri @11.625 &V0,L0,I0,Ri @11.75 &V0,L0,I0,Rq @12.0 &V0,L0,I0,Rq. @12.375 &V0,L0,I0,Ri @12.5 &V0,L0,I0,A5qa112d0 @12.75 &V0,L0,I0,A5qa100d0 @13.0 &V0,L0,I0,G5/0.12708333333333333a93d0 @13.125 &V0,L0,I0,F5/0.11822916666666666a83d0 @13.243229166666667 &V0,L0,I0,R/0.006770833333332504 @13.25 &V0,L0,I0,G5/0.12708333333333333a106d0 @13.375 &V0,L0,I0,A5/0.11822916666666666a112d0 @13.493229166666667 &V0,L0,I0,R/0.006770833333332504 @13.5 &V0,L0,I0,F5qa91d0 @13.75 &V0,L0,I0,Rq @14.0 &V0,L0,I0,Rq. @14.375 &V0,L0,I0,Ri @14.5 &V0,L0,I0,Rq @14.75 &V0,L0,I0,A5qa103d0 @15.0 &V0,L0,I0,G5/0.12708333333333333a98d0 @15.125 &V0,L0,I0,F5/0.11822916666666666a89d0 @15.243229166666667 &V0,L0,I0,R/0.006770833333332504 @15.25 &V0,L0,I0,G5ia110d0 @15.375 &V0,L0,I0,A5ia104d0 @15.5 &V0,L0,I0,F5ha93d0 T100 @0.0 &V1,L0,I0,Rq @0.25 &V1,L0,I0,Rq @0.5 &V1,L0,I0,Rq @0.75 &V1,L0,I0,Rq @1.0 &V1,L0,I0,BB4qa103d0 @1.25 &V1,L0,I0,C5qa98d0 @1.5 &V1,L0,I0,F4qa97d0 @1.75 &V1,L0,I0,Rq @2.0 &V1,L0,I0,Rq @2.25 &V1,L0,I0,C5qa113d0 @2.5 &V1,L0,I0,F4qa90d0 @2.75 &V1,L0,I0,Rq @3.0 &V1,L0,I0,Rq @3.25 &V1,L0,I0,C5qa107d0 @3.5 &V1,L0,I0,Rq @3.75 &V1,L0,I0,Rq @4.0 &V1,L0,I0,Rq @4.25 &V1,L0,I0,C5qa99d0 @4.5 &V1,L0,I0,F4qa79d0 @4.75 &V1,L0,I0,C5qa103d0 @5.0 &V1,L0,I0,Rq @5.25 &V1,L0,I0,Rq @5.5 &V1,L0,I0,Rq @5.75 &V1,L0,I0,Rq @6.0 &V1,L0,I0,F4qa73d0 @6.25 &V1,L0,I0,C5qa94d0 @6.5 &V1,L0,I0,F4qa86d0 @6.75 &V1,L0,I0,C5qa99d0 @7.0 &V1,L0,I0,BB4qa88d0 @7.25 &V1,L0,I0,Rq @7.5 &V1,L0,I0,Rq @7.75 &V1,L0,I0,Rq @8.0 &V1,L0,I0,Ri @8.125 &V1,L0,I0,Ri @8.25 &V1,L0,I0,Ri @8.375 &V1,L0,I0,Ri @8.5 &V1,L0,I0,C5ia65d0 @8.625 &V1,L0,I0,C5ia59d0 @8.75 &V1,L0,I0,C5qd0 @9.0 &V1,L0,I0,Rwh @10.5 &V1,L0,I0,C5ia58d0 @10.625 &V1,L0,I0,C5ia63d0 @10.75 &V1,L0,I0,C5qa57d0 @11.0 &V1,L0,I0,Rw @12.0 &V1,L0,I0,Rq @12.25 &V1,L0,I0,Rq @12.5 &V1,L0,I0,F4qa96d0 @12.75 &V1,L0,I0,C5qa116d0 @13.0 &V1,L0,I0,BB4qa98d0 @13.25 &V1,L0,I0,C5qa103d0 @13.5 &V1,L0,I0,F4qa94d0 @13.75 &V1,L0,I0,Rq @14.0 &V1,L0,I0,Rq @14.25 &V1,L0,I0,Rq @14.5 &V1,L0,I0,Rq @14.75 &V1,L0,I0,C5qa115d0 @15.0 &V1,L0,I0,BB4qa98d0 @15.25 &V1,L0,I0,C5qa103d0 @15.5 &V1,L0,I0,F4qa92d0"
							, "T100 @0.0 &V0,L0,I0,F5q.a71d0 @0.375 &V0,L0,I0,Ri @0.5 &V0,L0,I0,Rq @0.75 &V0,L0,I0,Rq @1.0 &V0,L0,I0,R/0.12708333333333321 @1.125 &V0,L0,I0,R/0.11822916666666661 @1.2432291666666666 &V0,L0,I0,R/0.0067708333333333925 @1.25 &V0,L0,I0,R/0.12708333333333321 @1.375 &V0,L0,I0,R/0.11822916666666661 @1.4932291666666666 &V0,L0,I0,R/0.0067708333333333925 @1.5 &V0,L0,I0,Rq @1.75 &V0,L0,I0,C5qa69d0 @2.0 &V0,L0,I0,Rq. @2.375 &V0,L0,I0,Ri @2.5 &V0,L0,I0,Rq @2.75 &V0,L0,I0,Rq @3.0 &V0,L0,I0,R/0.12708333333333321 @3.125 &V0,L0,I0,R/0.11822916666666661 @3.2432291666666666 &V0,L0,I0,R/0.0067708333333333925 @3.25 &V0,L0,I0,R/0.12708333333333321 @3.375 &V0,L0,I0,R/0.11822916666666661 @3.4932291666666666 &V0,L0,I0,R/0.0067708333333333925 @3.5 &V0,L0,I0,F5ha67d0 @4.0 &V0,L0,I0,Rq. @4.375 &V0,L0,I0,Ri @4.5 &V0,L0,I0,Rq @4.75 &V0,L0,I0,Rq @5.0 &V0,L0,I0,R/0.12708333333333321 @5.125 &V0,L0,I0,R/0.11822916666666661 @5.243229166666667 &V0,L0,I0,R/0.0067708333333333925 @5.25 &V0,L0,I0,R/0.12708333333333321 @5.375 &V0,L0,I0,R/0.11822916666666661 @5.493229166666667 &V0,L0,I0,R/0.0067708333333333925 @5.5 &V0,L0,I0,F5qa70d0 @5.75 &V0,L0,I0,Rq @6.0 &V0,L0,I0,Rq. @6.375 &V0,L0,I0,Ri @6.5 &V0,L0,I0,Rq @6.75 &V0,L0,I0,Rq @7.0 &V0,L0,I0,R/0.12708333333333321 @7.125 &V0,L0,I0,R/0.11822916666666661 @7.243229166666667 &V0,L0,I0,R/0.0067708333333333925 @7.25 &V0,L0,I0,R/0.12708333333333321 @7.375 &V0,L0,I0,R/0.11822916666666661 @7.493229166666667 &V0,L0,I0,R/0.0067708333333333925 @7.5 &V0,L0,I0,Rh @8.0 &V0,L0,I0,Rw @9.0 &V0,L0,I0,Ri @9.125 &V0,L0,I0,Ri @9.25 &V0,L0,I0,Ri @9.375 &V0,L0,I0,Ri @9.5 &V0,L0,I0,Ri @9.625 &V0,L0,I0,Ri @9.75 &V0,L0,I0,F5qa74d0 @10.0 &V0,L0,I0,Ri @10.125 &V0,L0,I0,Ri @10.25 &V0,L0,I0,Rq @10.5 &V0,L0,I0,Rh @11.0 &V0,L0,I0,Ri @11.125 &V0,L0,I0,Ri @11.25 &V0,L0,I0,Rq @11.5 &V0,L0,I0,Ri @11.625 &V0,L0,I0,Ri @11.75 &V0,L0,I0,Rq @12.0 &V0,L0,I0,F5q.a117d0 @12.375 &V0,L0,I0,Ri @12.5 &V0,L0,I0,Rq @12.75 &V0,L0,I0,Rq @13.0 &V0,L0,I0,R/0.12708333333333321 @13.125 &V0,L0,I0,R/0.1182291666666675 @13.243229166666667 &V0,L0,I0,R/0.006770833333332504 @13.25 &V0,L0,I0,R/0.12708333333333321 @13.375 &V0,L0,I0,R/0.1182291666666675 @13.493229166666667 &V0,L0,I0,R/0.006770833333332504 @13.5 &V0,L0,I0,Rq @13.75 &V0,L0,I0,Rq @14.0 &V0,L0,I0,Rq. @14.375 &V0,L0,I0,G5ia109d0 @14.5 &V0,L0,I0,Rq @14.75 &V0,L0,I0,Rq @15.0 &V0,L0,I0,R/0.12708333333333321 @15.125 &V0,L0,I0,R/0.1182291666666675 @15.243229166666667 &V0,L0,I0,R/0.006770833333332504 @15.25 &V0,L0,I0,Ri @15.375 &V0,L0,I0,Ri @15.5 &V0,L0,I0,Rh T100 @0.0 &V1,L0,I0,F4qa92d0 @0.25 &V1,L0,I0,Rq @0.5 &V1,L0,I0,Rq @0.75 &V1,L0,I0,Rq @1.0 &V1,L0,I0,Rq @1.25 &V1,L0,I0,Rq @1.5 &V1,L0,I0,Rq @1.75 &V1,L0,I0,Rq @2.0 &V1,L0,I0,Rq @2.25 &V1,L0,I0,Rq @2.5 &V1,L0,I0,Rq @2.75 &V1,L0,I0,Rq @3.0 &V1,L0,I0,Rq @3.25 &V1,L0,I0,Rq @3.5 &V1,L0,I0,F4qa95d0 @3.75 &V1,L0,I0,Rq @4.0 &V1,L0,I0,Rq @4.25 &V1,L0,I0,Rq @4.5 &V1,L0,I0,Rq @4.75 &V1,L0,I0,Rq @5.0 &V1,L0,I0,Rq @5.25 &V1,L0,I0,Rq @5.5 &V1,L0,I0,F4qa80d0 @5.75 &V1,L0,I0,Rq @6.0 &V1,L0,I0,Rq @6.25 &V1,L0,I0,Rq @6.5 &V1,L0,I0,Rq @6.75 &V1,L0,I0,Rq @7.0 &V1,L0,I0,Rq @7.25 &V1,L0,I0,Rq @7.5 &V1,L0,I0,Rq @7.75 &V1,L0,I0,Rq @8.0 &V1,L0,I0,F4ia58d0 @8.125 &V1,L0,I0,G4ia63d0 @8.25 &V1,L0,I0,Ri @8.375 &V1,L0,I0,Ri @8.5 &V1,L0,I0,Ri @8.625 &V1,L0,I0,Ri @8.75 &V1,L0,I0,Rq @9.0 &V1,L0,I0,Rwh @10.5 &V1,L0,I0,Ri @10.625 &V1,L0,I0,Ri @10.75 &V1,L0,I0,Rq @11.0 &V1,L0,I0,Rw @12.0 &V1,L0,I0,F4qa93d0 @12.25 &V1,L0,I0,Rq @12.5 &V1,L0,I0,Rq @12.75 &V1,L0,I0,Rq @13.0 &V1,L0,I0,Rq @13.25 &V1,L0,I0,Rq @13.5 &V1,L0,I0,Rq @13.75 &V1,L0,I0,Rq @14.0 &V1,L0,I0,Rq @14.25 &V1,L0,I0,C5qa119d0 @14.5 &V1,L0,I0,Rq @14.75 &V1,L0,I0,Rq @15.0 &V1,L0,I0,Rq @15.25 &V1,L0,I0,Rq @15.5 &V1,L0,I0,Rq"
							, "T100 @0.0 &V0,L0,I0,Rq. @0.375 &V0,L0,I0,G5ia80d0 @0.5 &V0,L0,I0,Rq @0.75 &V0,L0,I0,Rq @1.0 &V0,L0,I0,R/0.12708333333333321 @1.125 &V0,L0,I0,R/0.11822916666666661 @1.2432291666666666 &V0,L0,I0,R/0.0067708333333333925 @1.25 &V0,L0,I0,R/0.12708333333333321 @1.375 &V0,L0,I0,R/0.11822916666666661 @1.4932291666666666 &V0,L0,I0,R/0.0067708333333333925 @1.5 &V0,L0,I0,Rq @1.75 &V0,L0,I0,Rq @2.0 &V0,L0,I0,F5q.a84d0 @2.375 &V0,L0,I0,Ri @2.5 &V0,L0,I0,Rq @2.75 &V0,L0,I0,Rq @3.0 &V0,L0,I0,R/0.12708333333333321 @3.125 &V0,L0,I0,R/0.11822916666666661 @3.2432291666666666 &V0,L0,I0,R/0.0067708333333333925 @3.25 &V0,L0,I0,R/0.12708333333333321 @3.375 &V0,L0,I0,R/0.11822916666666661 @3.4932291666666666 &V0,L0,I0,R/0.0067708333333333925 @3.5 &V0,L0,I0,Rh @4.0 &V0,L0,I0,F5q.a79d0 @4.375 &V0,L0,I0,Ri @4.5 &V0,L0,I0,Rq @4.75 &V0,L0,I0,Rq @5.0 &V0,L0,I0,R/0.12708333333333321 @5.125 &V0,L0,I0,R/0.11822916666666661 @5.243229166666667 &V0,L0,I0,R/0.0067708333333333925 @5.25 &V0,L0,I0,R/0.12708333333333321 @5.375 &V0,L0,I0,R/0.11822916666666661 @5.493229166666667 &V0,L0,I0,R/0.0067708333333333925 @5.5 &V0,L0,I0,Rq @5.75 &V0,L0,I0,C5qa65d0 @6.0 &V0,L0,I0,Rq. @6.375 &V0,L0,I0,Ri @6.5 &V0,L0,I0,Rq @6.75 &V0,L0,I0,Rq @7.0 &V0,L0,I0,R/0.12708333333333321 @7.125 &V0,L0,I0,R/0.11822916666666661 @7.243229166666667 &V0,L0,I0,R/0.0067708333333333925 @7.25 &V0,L0,I0,R/0.12708333333333321 @7.375 &V0,L0,I0,R/0.11822916666666661 @7.493229166666667 &V0,L0,I0,R/0.0067708333333333925 @7.5 &V0,L0,I0,Rh @8.0 &V0,L0,I0,Rw @9.0 &V0,L0,I0,Ri @9.125 &V0,L0,I0,Ri @9.25 &V0,L0,I0,Ri @9.375 &V0,L0,I0,Ri @9.5 &V0,L0,I0,Ri @9.625 &V0,L0,I0,Ri @9.75 &V0,L0,I0,Rq @10.0 &V0,L0,I0,C6ia87d0 @10.125 &V0,L0,I0,C6ia80d0 @10.25 &V0,L0,I0,Rq @10.5 &V0,L0,I0,Rh @11.0 &V0,L0,I0,Ri @11.125 &V0,L0,I0,Ri @11.25 &V0,L0,I0,Rq @11.5 &V0,L0,I0,Ri @11.625 &V0,L0,I0,Ri @11.75 &V0,L0,I0,Rq @12.0 &V0,L0,I0,Rq. @12.375 &V0,L0,I0,G5ia107d0 @12.5 &V0,L0,I0,Rq @12.75 &V0,L0,I0,Rq @13.0 &V0,L0,I0,R/0.12708333333333321 @13.125 &V0,L0,I0,R/0.1182291666666675 @13.243229166666667 &V0,L0,I0,R/0.006770833333332504 @13.25 &V0,L0,I0,R/0.12708333333333321 @13.375 &V0,L0,I0,R/0.1182291666666675 @13.493229166666667 &V0,L0,I0,R/0.006770833333332504 @13.5 &V0,L0,I0,Rq @13.75 &V0,L0,I0,Rq @14.0 &V0,L0,I0,Rq. @14.375 &V0,L0,I0,Ri @14.5 &V0,L0,I0,A5qa105d0 @14.75 &V0,L0,I0,Rq @15.0 &V0,L0,I0,R/0.12708333333333321 @15.125 &V0,L0,I0,R/0.1182291666666675 @15.243229166666667 &V0,L0,I0,R/0.006770833333332504 @15.25 &V0,L0,I0,Ri @15.375 &V0,L0,I0,Ri @15.5 &V0,L0,I0,Rh T120 @0.0 &V1,L0,I0,Rq @0.25 &V1,L0,I0,C5qa108d0 @0.5 &V1,L0,I0,Rq @0.75 &V1,L0,I0,Rq @1.0 &V1,L0,I0,Rq @1.25 &V1,L0,I0,Rq @1.5 &V1,L0,I0,Rq @1.75 &V1,L0,I0,Rq @2.0 &V1,L0,I0,F4qa88d0 @2.25 &V1,L0,I0,Rq @2.5 &V1,L0,I0,Rq @2.75 &V1,L0,I0,Rq @3.0 &V1,L0,I0,Rq @3.25 &V1,L0,I0,Rq @3.5 &V1,L0,I0,Rq @3.75 &V1,L0,I0,Rq @4.0 &V1,L0,I0,F4qa77d0 @4.25 &V1,L0,I0,Rq @4.5 &V1,L0,I0,Rq @4.75 &V1,L0,I0,Rq @5.0 &V1,L0,I0,Rq @5.25 &V1,L0,I0,Rq @5.5 &V1,L0,I0,Rq @5.75 &V1,L0,I0,Rq @6.0 &V1,L0,I0,Rq @6.25 &V1,L0,I0,Rq @6.5 &V1,L0,I0,Rq @6.75 &V1,L0,I0,Rq @7.0 &V1,L0,I0,Rq @7.25 &V1,L0,I0,Rq @7.5 &V1,L0,I0,Rq @7.75 &V1,L0,I0,Rq @8.0 &V1,L0,I0,Ri @8.125 &V1,L0,I0,Ri @8.25 &V1,L0,I0,A4ia70d0 @8.375 &V1,L0,I0,BB4ia62d0 @8.5 &V1,L0,I0,Ri @8.625 &V1,L0,I0,Ri @8.75 &V1,L0,I0,Rq @9.0 &V1,L0,I0,Rwh @10.5 &V1,L0,I0,Ri @10.625 &V1,L0,I0,Ri @10.75 &V1,L0,I0,Rq @11.0 &V1,L0,I0,Rw @12.0 &V1,L0,I0,Rq @12.25 &V1,L0,I0,C5qa117d0 @12.5 &V1,L0,I0,Rq @12.75 &V1,L0,I0,Rq @13.0 &V1,L0,I0,Rq @13.25 &V1,L0,I0,Rq @13.5 &V1,L0,I0,Rq @13.75 &V1,L0,I0,Rq @14.0 &V1,L0,I0,Rq @14.25 &V1,L0,I0,Rq @14.5 &V1,L0,I0,F4qa92d0 @14.75 &V1,L0,I0,Rq @15.0 &V1,L0,I0,Rq @15.25 &V1,L0,I0,Rq @15.5 &V1,L0,I0,Rq"
							, "T100 @0.0 &V0,L0,I0,Rq. @0.375 &V0,L0,I0,Ri @0.5 &V0,L0,I0,A5qa81d0 @0.75 &V0,L0,I0,Rq @1.0 &V0,L0,I0,R/0.12708333333333321 @1.125 &V0,L0,I0,R/0.11822916666666661 @1.2432291666666666 &V0,L0,I0,R/0.0067708333333333925 @1.25 &V0,L0,I0,R/0.12708333333333321 @1.375 &V0,L0,I0,R/0.11822916666666661 @1.4932291666666666 &V0,L0,I0,R/0.0067708333333333925 @1.5 &V0,L0,I0,Rq @1.75 &V0,L0,I0,Rq @2.0 &V0,L0,I0,Rq. @2.375 &V0,L0,I0,Ri @2.5 &V0,L0,I0,Rq @2.75 &V0,L0,I0,A5qa79d0 @3.0 &V0,L0,I0,R/0.12708333333333321 @3.125 &V0,L0,I0,R/0.11822916666666661 @3.2432291666666666 &V0,L0,I0,R/0.0067708333333333925 @3.25 &V0,L0,I0,R/0.12708333333333321 @3.375 &V0,L0,I0,R/0.11822916666666661 @3.4932291666666666 &V0,L0,I0,R/0.0067708333333333925 @3.5 &V0,L0,I0,Rh @4.0 &V0,L0,I0,Rq. @4.375 &V0,L0,I0,Ri @4.5 &V0,L0,I0,Rq @4.75 &V0,L0,I0,Rq @5.0 &V0,L0,I0,G5/0.12708333333333333a73d0 @5.125 &V0,L0,I0,F5/0.11822916666666666a68d0 @5.243229166666667 &V0,L0,I0,R/0.0067708333333333925 @5.25 &V0,L0,I0,R/0.12708333333333321 @5.375 &V0,L0,I0,R/0.11822916666666661 @5.493229166666667 &V0,L0,I0,R/0.0067708333333333925 @5.5 &V0,L0,I0,Rq @5.75 &V0,L0,I0,Rq @6.0 &V0,L0,I0,Rq. @6.375 &V0,L0,I0,Ri @6.5 &V0,L0,I0,Rq @6.75 &V0,L0,I0,Rq @7.0 &V0,L0,I0,R/0.12708333333333321 @7.125 &V0,L0,I0,F5/0.11822916666666666a58d0 @7.243229166666667 &V0,L0,I0,R/0.0067708333333333925 @7.25 &V0,L0,I0,G5/0.12708333333333333a79d0 @7.375 &V0,L0,I0,R/0.11822916666666661 @7.493229166666667 &V0,L0,I0,R/0.0067708333333333925 @7.5 &V0,L0,I0,Rh @8.0 &V0,L0,I0,Rw @9.0 &V0,L0,I0,Ri @9.125 &V0,L0,I0,Ri @9.25 &V0,L0,I0,A5ia73d0 @9.375 &V0,L0,I0,G5ia73d0 @9.5 &V0,L0,I0,Ri @9.625 &V0,L0,I0,Ri @9.75 &V0,L0,I0,Rq @10.0 &V0,L0,I0,Ri @10.125 &V0,L0,I0,Ri @10.25 &V0,L0,I0,Rq @10.5 &V0,L0,I0,Rh @11.0 &V0,L0,I0,Ri @11.125 &V0,L0,I0,Ri @11.25 &V0,L0,I0,Rq @11.5 &V0,L0,I0,C5ia51d0 @11.625 &V0,L0,I0,C5ia84d0 @11.75 &V0,L0,I0,Rq @12.0 &V0,L0,I0,Rq. @12.375 &V0,L0,I0,Ri @12.5 &V0,L0,I0,Rq @12.75 &V0,L0,I0,Rq @13.0 &V0,L0,I0,R/0.12708333333333321 @13.125 &V0,L0,I0,R/0.1182291666666675 @13.243229166666667 &V0,L0,I0,R/0.006770833333332504 @13.25 &V0,L0,I0,R/0.12708333333333321 @13.375 &V0,L0,I0,R/0.1182291666666675 @13.493229166666667 &V0,L0,I0,R/0.006770833333332504 @13.5 &V0,L0,I0,Rq @13.75 &V0,L0,I0,C5qa88d0 @14.0 &V0,L0,I0,Rq. @14.375 &V0,L0,I0,Ri @14.5 &V0,L0,I0,Rq @14.75 &V0,L0,I0,Rq @15.0 &V0,L0,I0,R/0.12708333333333321 @15.125 &V0,L0,I0,R/0.1182291666666675 @15.243229166666667 &V0,L0,I0,R/0.006770833333332504 @15.25 &V0,L0,I0,Ri @15.375 &V0,L0,I0,Ri @15.5 &V0,L0,I0,Rh T120 @0.0 &V1,L0,I0,Rq @0.25 &V1,L0,I0,Rq @0.5 &V1,L0,I0,F4qa90d0 @0.75 &V1,L0,I0,Rq @1.0 &V1,L0,I0,Rq @1.25 &V1,L0,I0,Rq @1.5 &V1,L0,I0,Rq @1.75 &V1,L0,I0,Rq @2.0 &V1,L0,I0,Rq @2.25 &V1,L0,I0,Rq @2.5 &V1,L0,I0,Rq @2.75 &V1,L0,I0,C5qa114d0 @3.0 &V1,L0,I0,Rq @3.25 &V1,L0,I0,Rq @3.5 &V1,L0,I0,Rq @3.75 &V1,L0,I0,Rq @4.0 &V1,L0,I0,Rq @4.25 &V1,L0,I0,Rq @4.5 &V1,L0,I0,Rq @4.75 &V1,L0,I0,Rq @5.0 &V1,L0,I0,BB4qa84d0 @5.25 &V1,L0,I0,Rq @5.5 &V1,L0,I0,Rq @5.75 &V1,L0,I0,Rq @6.0 &V1,L0,I0,Rq @6.25 &V1,L0,I0,Rq @6.5 &V1,L0,I0,Rq @6.75 &V1,L0,I0,Rq @7.0 &V1,L0,I0,Rq @7.25 &V1,L0,I0,C5qa92d0 @7.5 &V1,L0,I0,Rq @7.75 &V1,L0,I0,Rq @8.0 &V1,L0,I0,Ri @8.125 &V1,L0,I0,Ri @8.25 &V1,L0,I0,Ri @8.375 &V1,L0,I0,Ri @8.5 &V1,L0,I0,Ri @8.625 &V1,L0,I0,Ri @8.75 &V1,L0,I0,Rq @9.0 &V1,L0,I0,Rwh @10.5 &V1,L0,I0,Ri @10.625 &V1,L0,I0,Ri @10.75 &V1,L0,I0,Rq @11.0 &V1,L0,I0,Rw @12.0 &V1,L0,I0,Rq @12.25 &V1,L0,I0,Rq @12.5 &V1,L0,I0,Rq @12.75 &V1,L0,I0,Rq @13.0 &V1,L0,I0,Rq @13.25 &V1,L0,I0,Rq @13.5 &V1,L0,I0,Rq @13.75 &V1,L0,I0,Rq @14.0 &V1,L0,I0,Rq @14.25 &V1,L0,I0,Rq @14.5 &V1,L0,I0,Rq @14.75 &V1,L0,I0,Rq @15.0 &V1,L0,I0,Rq @15.25 &V1,L0,I0,Rq @15.5 &V1,L0,I0,Rq"
							, "T100 @0.0 &V0,L0,I0,Rq. @0.375 &V0,L0,I0,Ri @0.5 &V0,L0,I0,Rq @0.75 &V0,L0,I0,A5qa78d0 @1.0 &V0,L0,I0,R/0.12708333333333321 @1.125 &V0,L0,I0,R/0.11822916666666661 @1.2432291666666666 &V0,L0,I0,R/0.0067708333333333925 @1.25 &V0,L0,I0,R/0.12708333333333321 @1.375 &V0,L0,I0,R/0.11822916666666661 @1.4932291666666666 &V0,L0,I0,R/0.0067708333333333925 @1.5 &V0,L0,I0,Rq @1.75 &V0,L0,I0,Rq @2.0 &V0,L0,I0,Rq. @2.375 &V0,L0,I0,Ri @2.5 &V0,L0,I0,Rq @2.75 &V0,L0,I0,Rq @3.0 &V0,L0,I0,G5/0.12708333333333333a68d0 @3.125 &V0,L0,I0,F5/0.11822916666666666a61d0 @3.2432291666666666 &V0,L0,I0,R/0.0067708333333333925 @3.25 &V0,L0,I0,R/0.12708333333333321 @3.375 &V0,L0,I0,R/0.11822916666666661 @3.4932291666666666 &V0,L0,I0,R/0.0067708333333333925 @3.5 &V0,L0,I0,Rh @4.0 &V0,L0,I0,Rq. @4.375 &V0,L0,I0,Ri @4.5 &V0,L0,I0,Rq @4.75 &V0,L0,I0,Rq @5.0 &V0,L0,I0,R/0.12708333333333321 @5.125 &V0,L0,I0,R/0.11822916666666661 @5.243229166666667 &V0,L0,I0,R/0.0067708333333333925 @5.25 &V0,L0,I0,G5/0.12708333333333333a83d0 @5.375 &V0,L0,I0,A5/0.11822916666666666a90d0 @5.493229166666667 &V0,L0,I0,R/0.0067708333333333925 @5.5 &V0,L0,I0,Rq @5.75 &V0,L0,I0,Rq @6.0 &V0,L0,I0,Rq. @6.375 &V0,L0,I0,Ri @6.5 &V0,L0,I0,Rq @6.75 &V0,L0,I0,Rq @7.0 &V0,L0,I0,R/0.12708333333333321 @7.125 &V0,L0,I0,R/0.11822916666666661 @7.243229166666667 &V0,L0,I0,R/0.0067708333333333925 @7.25 &V0,L0,I0,R/0.12708333333333321 @7.375 &V0,L0,I0,A5/0.11822916666666666a87d0 @7.493229166666667 &V0,L0,I0,R/0.0067708333333333925 @7.5 &V0,L0,I0,F5ha66d0 @8.0 &V0,L0,I0,Rw @9.0 &V0,L0,I0,Ri @9.125 &V0,L0,I0,Ri @9.25 &V0,L0,I0,Ri @9.375 &V0,L0,I0,Ri @9.5 &V0,L0,I0,F5ia73d0 @9.625 &V0,L0,I0,F5ia80d0 @9.75 &V0,L0,I0,Rq @10.0 &V0,L0,I0,Ri @10.125 &V0,L0,I0,Ri @10.25 &V0,L0,I0,Rq @10.5 &V0,L0,I0,Rh @11.0 &V0,L0,I0,Ri @11.125 &V0,L0,I0,Ri @11.25 &V0,L0,I0,Rq @11.5 &V0,L0,I0,Ri @11.625 &V0,L0,I0,Ri @11.75 &V0,L0,I0,C5qa91d0 @12.0 &V0,L0,I0,Rq. @12.375 &V0,L0,I0,Ri @12.5 &V0,L0,I0,Rq @12.75 &V0,L0,I0,Rq @13.0 &V0,L0,I0,R/0.12708333333333321 @13.125 &V0,L0,I0,R/0.1182291666666675 @13.243229166666667 &V0,L0,I0,R/0.006770833333332504 @13.25 &V0,L0,I0,R/0.12708333333333321 @13.375 &V0,L0,I0,R/0.1182291666666675 @13.493229166666667 &V0,L0,I0,R/0.006770833333332504 @13.5 &V0,L0,I0,Rq @13.75 &V0,L0,I0,Rq @14.0 &V0,L0,I0,F5q.a114d0 @14.375 &V0,L0,I0,Ri @14.5 &V0,L0,I0,Rq @14.75 &V0,L0,I0,Rq @15.0 &V0,L0,I0,R/0.12708333333333321 @15.125 &V0,L0,I0,R/0.1182291666666675 @15.243229166666667 &V0,L0,I0,R/0.006770833333332504 @15.25 &V0,L0,I0,Ri @15.375 &V0,L0,I0,Ri @15.5 &V0,L0,I0,Rh T120 @0.0 &V1,L0,I0,Rq @0.25 &V1,L0,I0,Rq @0.5 &V1,L0,I0,Rq @0.75 &V1,L0,I0,C5qa112d0 @1.0 &V1,L0,I0,Rq @1.25 &V1,L0,I0,Rq @1.5 &V1,L0,I0,Rq @1.75 &V1,L0,I0,Rq @2.0 &V1,L0,I0,Rq @2.25 &V1,L0,I0,Rq @2.5 &V1,L0,I0,Rq @2.75 &V1,L0,I0,Rq @3.0 &V1,L0,I0,BB4qa98d0 @3.25 &V1,L0,I0,Rq @3.5 &V1,L0,I0,Rq @3.75 &V1,L0,I0,Rq @4.0 &V1,L0,I0,Rq @4.25 &V1,L0,I0,Rq @4.5 &V1,L0,I0,Rq @4.75 &V1,L0,I0,Rq @5.0 &V1,L0,I0,Rq @5.25 &V1,L0,I0,C5qa89d0 @5.5 &V1,L0,I0,Rq @5.75 &V1,L0,I0,Rq @6.0 &V1,L0,I0,Rq @6.25 &V1,L0,I0,Rq @6.5 &V1,L0,I0,Rq @6.75 &V1,L0,I0,Rq @7.0 &V1,L0,I0,Rq @7.25 &V1,L0,I0,Rq @7.5 &V1,L0,I0,F4qa84d0 @7.75 &V1,L0,I0,Rq @8.0 &V1,L0,I0,Ri @8.125 &V1,L0,I0,Ri @8.25 &V1,L0,I0,Ri @8.375 &V1,L0,I0,Ri @8.5 &V1,L0,I0,Ri @8.625 &V1,L0,I0,Ri @8.75 &V1,L0,I0,Rq @9.0 &V1,L0,I0,Rwh @10.5 &V1,L0,I0,Ri @10.625 &V1,L0,I0,Ri @10.75 &V1,L0,I0,Rq @11.0 &V1,L0,I0,Rw @12.0 &V1,L0,I0,Rq @12.25 &V1,L0,I0,Rq @12.5 &V1,L0,I0,Rq @12.75 &V1,L0,I0,Rq @13.0 &V1,L0,I0,Rq @13.25 &V1,L0,I0,Rq @13.5 &V1,L0,I0,Rq @13.75 &V1,L0,I0,Rq @14.0 &V1,L0,I0,F4qa87d0 @14.25 &V1,L0,I0,Rq @14.5 &V1,L0,I0,Rq @14.75 &V1,L0,I0,Rq @15.0 &V1,L0,I0,Rq @15.25 &V1,L0,I0,Rq @15.5 &V1,L0,I0,Rq"
			};
			
			CyclicBarrier b = new CyclicBarrier( pats.length );
					
			BackgroundMusic musics[] = new BackgroundMusic[ pats.length ];
			
			int i = 0;
			for( String pt : pats )
			{
				BackgroundMusic playerbgMusic = new BackgroundMusic(); 
				playerbgMusic.setPattern( new Pattern( pt ) );
				playerbgMusic.setDelay( 0.1 );
				playerbgMusic.setCoordinator( b );
				musics[ i ] = playerbgMusic;
				i++;
			}
			
			for( BackgroundMusic m : musics )
			{
				m.startThread();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
