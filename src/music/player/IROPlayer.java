package music.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.ManagedPlayerListener;
import org.jfugue.theory.Note;

import JFugueMod.org.jfugue.player.PlayerMod;
import music.IROTrack;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppableThread;
import tools.PatternTools;

public class IROPlayer 
{
	private Map< String, PlayerNote > players;
	private PlayerNote allocatedPlayer = null;
		
	public IROPlayer() 
	{		
		this.players = new HashMap< String, PlayerNote>( );
		
		try
		{
			this.allocatedPlayer = new PlayerNote();
		} 
		catch (MidiUnavailableException ex)
		{
		}
	}
	
	public void play( List< IROTrack > Tracks )
	{
		try
		{
			Pattern pat = new Pattern();
			
			for( IROTrack tr : Tracks )
			{	
				pat.add( tr.getPatternTrackSheet() );
			}
		
			PlayerNote player;
			if( this.allocatedPlayer != null )
			{
				player = allocatedPlayer;
			}
			else
			{
				player = new PlayerNote();
			}
			player.setPattern( pat );
			
			this.players.put( player.getName(), player );
			player.startThread();
			
			this.allocatedPlayer = new PlayerNote();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}		
	}
	
	public void play( List< Note > NOTES, String instrument, int voice, int layer, int bpm ) throws Exception
	{			
		if( NOTES != null && !NOTES.isEmpty() )
		{	
			List< List< Note > > noteList = new ArrayList< List< Note > >();
			noteList.add( NOTES );
			Pattern NotesPattern = PatternTools.createPattern( bpm, voice, layer, instrument, noteList );
			
			PlayerNote player;
			if( this.allocatedPlayer != null )
			{
				player = allocatedPlayer;
			}
			else
			{
				player = new PlayerNote();
			}
			player.setPattern( NotesPattern );
						
			this.players.put( player.getName(), player );
			player.startThread();			
			
			this.allocatedPlayer = new PlayerNote();
		}
	}
	
	public void stop()
	{
		synchronized( this.players )
		{
			for( PlayerNote player : this.players.values() )
			{
				player.stopThread( IStoppableThread.FORCE_STOP );
			}
			
			this.players.clear();
		}
	}
		
	private void PlayerDead( String id )
	{		
		synchronized ( players )
		{
			this.players.remove( id );			
		}
	}
		
	private class PlayerNote extends AbstractStoppableThread implements ManagedPlayerListener  
	{	
		//private Pattern musicPattern;
		
		private PlayerMod player;
		
		public PlayerNote( ) throws MidiUnavailableException 
		{
			this.player = new PlayerMod();			
			this.player.getManagedPlayer().addManagedPlayerListener( this );
		}

		public void setPattern( Pattern pattern ) throws MidiUnavailableException, InvalidMidiDataException
		{
			//this.musicPattern = pattern;
			this.player.load( pattern );
		}
		
		public PlayerMod getPlayer()
		{
			return this.player;
		}
		
		@Override
		protected void preStopThread(int friendliness) throws Exception 
		{	
		}

		@Override
		protected void postStopThread(int friendliness) throws Exception 
		{	
		}

		@Override
		protected void runInLoop() throws Exception 
		{	
			synchronized( this )			
			{	
				if( !this.player.isEmpty() )
				{
					long t = System.nanoTime();
					//Sequence seq = this.player.getSequence( this.musicPattern );
					//System.out.println("IROPlayer.PlayerNote.runInLoop() A " + ( System.nanoTime() - t ) / 1e6D );
					//t = System.nanoTime();
					this.player.play( );
					System.out.println("IROPlayer.PlayerNote.runInLoop() B " + ( System.nanoTime() - t ) / 1e6D );
					
					this.wait();
				}
			}
		}
		
		@Override
		protected void runExceptionManager(Exception e) 
		{
			if( !( e instanceof InterruptedException ) )
			{
				super.runExceptionManager(e);
			}
		}
		
		@Override
		protected void targetDone() throws Exception 
		{
			super.targetDone();
			
			super.stopThread = true;
		}
		
		
		@Override
		protected void cleanUp() throws Exception 
		{
			super.cleanUp();
			
			this.player.getManagedPlayer().finish();
			this.player = null;
						
			PlayerDead( super.getName() );
		}

		@Override
		public void onStarted(Sequence sequence) 
		{	
		}

		@Override
		public void onFinished() 
		{	
		}

		@Override
		public void onPaused() 
		{	
			synchronized ( this )
			{
				super.notify();
			}
		}

		@Override
		public void onResumed() 
		{	
		}

		@Override
		public void onSeek(long tick) 
		{	
		}

		@Override
		public void onReset() 
		{	
		}
	}
}
