/**
 * 
 */
package testing.music;

import java.util.concurrent.CyclicBarrier;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import control.events.BackgroundMusicEvent;
import control.events.BackgroundMusicEventListener;
import gui.game.screen.level.music.BackgroundMusic;
import stoppableThread.IStoppable;
import tools.MusicSheetTools;

/**
 * @author Manuel Merino Monge
 *
 */
public class testBackgroundMusic2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try
		{
			String pats[] = new String[] { "T100 C4 C4 C4 C5 C3 T120 C4 C4 C4 C5 C3 T150 C4 C4 C4 C5 C3"
			};
						
			Pattern ptt = new Pattern();
			for( String pt : pats )
			{				 
				ptt.add( pt );
			}
			
			
			ptt.add( "T180 C4 C4 C4 C5 C3" );
			
			BackgroundMusic playerbgMusic = new BackgroundMusic();
			playerbgMusic.setPattern( ptt );
			playerbgMusic.setDelay( 0.1 );
			playerbgMusic.startActing();
		
			playerbgMusic.addBackgroundMusicEventListener( new BackgroundMusicEventListener() {
				
				@Override
				public void BackgroundMusicEvent(BackgroundMusicEvent event) 
				{
					
				}
			});
			
						
			System.out.println("testBackgroundMusic.main() END ");
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
