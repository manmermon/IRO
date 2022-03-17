package testing.music;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.theory.Note;

import gui.game.screen.level.music.BackgroundMusic;
import stoppableThread.IStoppable;

public class testSemitone {

	public static void main(String[] args) 
	{
		Note n1 = new Note( "F4" );		
		Note n2 = new Note( "F#4" );
		Note n3 = new Note( "F##4" );
		Note n4 = new Note( "G4" );
		System.out.println( Note.getFrequencyForNote( n1.getValue() ) );
		System.out.println( Note.getFrequencyForNote( n2.getValue() ) );
		
		Player p = new Player();
		p.play( n1.toString() );
		p.play( n2.toString() );
		p.play( n3.toString() );
		p.play( n4.toString() );
		
		String pat = "T100 V0 I[Piano] @0.0 F5q.a71d0 @0.375 G5ia80d0 @0.5 A5qa81d0 @0.75 A5qa78d0 @1.0 G5/0.12708333333333333a73d0 @1.125 F5/0.11822916666666666a68d0 ";
		String pat2 = "T100 V0 I[Piano] :PW(5000) @0.0 F5q.a71d0 :PW(72,20) @0.375 G5ia80d0 :PW(72,20) @0.5 A5qa81d0 @0.75 A5qa78d0 @1.0 G5/0.12708333333333333a73d0 @1.125 F5/0.11822916666666666a68d0 ";
		
		 try 
	    {
	    	BackgroundMusic bgm = new BackgroundMusic();
	    	bgm.setPattern( new Pattern( pat ) );
			
	    	bgm.startActing();
	    	
	    	Thread.sleep( 5000L );
	    	bgm.stopActing( IStoppable.FORCE_STOP );
	    	
	    	System.out.print( "TEST\n" );
	    	bgm = new BackgroundMusic();
	    	bgm.setPattern( new Pattern( ":PW(72,20) " + pat + " :PW(0,64)") );
	    	bgm.startActing();
	    	Thread.sleep( 5000L );
	    	bgm.stopActing( IStoppable.FORCE_STOP );
	    	
	    	bgm = new BackgroundMusic();
	    	bgm.setPattern( new Pattern( pat2 ) );
	    	bgm.startActing();
	    	Thread.sleep( 5000L );
	    	bgm.stopActing( IStoppable.FORCE_STOP );
			
		} catch ( Exception e) 
	    {
			e.printStackTrace();
		}	    
	    finally
	    {
	    	
	    }
	}

}
