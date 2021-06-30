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

import music.jfugueIRO.PlayerMod;
import music.sheet.IROTrack;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppableThread;
import tools.MusicSheetTools;

public class IROPlayer 
{
	private Map< String, PlayerNote > players;
		
	public IROPlayer() 
	{		
		this.players = new HashMap< String, PlayerNote>( );		
	}
	
	public void loadTracks( String trackID, List< IROTrack > Tracks )
	{
		try
		{
			Pattern pat = new Pattern();
			
			for( IROTrack tr : Tracks )
			{	
				pat.add( tr.getPatternTrackSheet() );
			}
		
			PlayerNote player = new PlayerNote();
			player.setPattern( pat );
			player.setName( trackID );
			
			this.players.put( player.getName(), player );
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}		
	}
	
	public void loadTrack( IROTrack Track )
	{
		try
		{
			Pattern pat = new Pattern();
			
			pat.add( Track.getPatternTrackSheet() );
		
			PlayerNote player = new PlayerNote();
			player.setPattern( pat );
			player.setName( Track.getID() );
			
			this.players.put( player.getName(), player );
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}		
	}
	
	public String loadNotes( List< Note > NOTES, String instrument, int voice, int layer, int bpm ) throws Exception
	{		
		String trackID = null;
		
		if( NOTES != null && !NOTES.isEmpty() )
		{	
			List< List< Note > > noteList = new ArrayList< List< Note > >();
			noteList.add( NOTES );
			Pattern NotesPattern = MusicSheetTools.createPattern( bpm, voice, layer, instrument, noteList );
			
			PlayerNote player = new PlayerNote();
			player.setPattern( NotesPattern );
						
			this.players.put( player.getName(), player );
		
			trackID = player.getName();
		}
		
		return trackID;
	}
	
	public void play( String trackID ) throws Exception
	{
		synchronized( this.players )
		{
			PlayerNote player = this.players.get( trackID );
			if( player != null )
			{
				player.startThread();
			}
		}
	}
	
	public void stopTrack( String trackID ) throws Exception
	{
		synchronized( this.players )
		{
			PlayerNote player = this.players.get( trackID );
			if( player != null )
			{	
				player.stopThread( IStoppableThread.FORCE_STOP );
			}
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
		
	//
	//
	//
	//
	//
	
	private class PlayerNote extends AbstractStoppableThread implements ManagedPlayerListener  
	{	
		private PlayerMod player;
		private Object lock = new Object();
		
		public PlayerNote( ) throws MidiUnavailableException 
		{
			super.setName( this.getClass().getSimpleName() + "-" + super.getId() );
			this.player = new PlayerMod();			
			this.player.getManagedPlayer().addManagedPlayerListener( this );
		}

		public void setPattern( Pattern pattern ) throws MidiUnavailableException, InvalidMidiDataException
		{
			this.player.load( pattern );
		}
		
		public PlayerMod getPlayer()
		{
			return this.player;
		}
		
		@Override
		protected void preStopThread(int friendliness) throws Exception 
		{	
			synchronized ( this )
			{
				if( this.player != null && !this.player.getManagedPlayer().isFinished() )
				{
					Thread t = new Thread()
					{
						@Override
						public void run()
						{
							player.getManagedPlayer().finish();
						}
					};
					
					t.setName( "player.getManagedPlayer.finish" );
					t.start();
				}
			}
		}

		@Override
		protected void postStopThread(int friendliness) throws Exception 
		{	
		}

		@Override
		protected void runInLoop() throws Exception 
		{	
			synchronized ( this )
			{
				if( !this.player.isEmpty() )
				{
					this.player.play( );

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
			
			synchronized ( this )
			{
				if( !this.player.getManagedPlayer().isFinished() )
				{
					this.player.getManagedPlayer().removeManagedPlayerListener( this );
					this.player.getManagedPlayer().finish();
				}
			}
			
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
			synchronized ( this )
			{
				super.notify();
			}
		}

		@Override
		public void onPaused() 
		{	
			/*
			synchronized ( this )
			{
				super.notify();
			}
			*/
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
