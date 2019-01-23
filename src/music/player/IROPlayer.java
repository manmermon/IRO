package music.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	//private List< Integer > realTimeTrackAvaible = null;
	
	public IROPlayer() 
	{
		/*
		try 
		{
			PlayerNote pl = new PlayerNote( new Pattern(), 10L, 0 ); 
			PlayerMod rp = pl.getPlayer();
			this.realTimeTrackAvaible = new LinkedList< Integer >();
			
			for( int i = rp.getEnableTrackSize()-1; i >= 0; i-- )
			{
				rp.changeTrack( i );
				if( !rp.isNullCurrentTrack() )
				{
					this.realTimeTrackAvaible.add( i );
				}
			}			
		}
		catch (MidiUnavailableException e) 
		{
			e.printStackTrace();
		}
		//*/
		
		this.players = new HashMap< String, PlayerNote>( );
	}
	
	public void play( List< IROTrack > Tracks )
	{
		try
		{
			/*
			int track = 0;
			
			long duration = 0;
			*/
			Pattern pat = new Pattern();
			
			for( IROTrack tr : Tracks )
			{
				/*
				if( tr.getTrackDuration() > duration )
				{
					duration = (long)( 1000 * tr.getTrackDuration() );
				}
				*/
				
				pat.add( tr.getPatternTrackSheet() );
			}
			
			//System.out.println("IRORealtimePlayer.play() " + pat );
					
			/*
			synchronized( this.realTimeTrackAvaible )
			{
				if( this.realTimeTrackAvaible.size() >  0 )
				{
					track = this.realTimeTrackAvaible.get( 0 );
					this.realTimeTrackAvaible.remove( 0 );
				}
			}
			*/
			
			//PlayerNote player = new PlayerNote( pat, duration, track );
			PlayerNote player = new PlayerNote( pat );
			
			this.players.put( player.getName(), player );
			player.startThread();
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
			
			/*
			double secondByQuarter = 60.0D / bpm;
			long noteDuration = (long)( 4 * secondByQuarter * 1000L ); // Whole note time in milliseconds
			
			double auxDur = -1D;
			
			for( Note n : NOTES )
			{
				if( !n.isRest() )
				{
					double durScale = n.getDuration() * 4; // multiply by 4 scale with respect to quarter scale ( quarter = 1 )

					if( auxDur <  durScale * secondByQuarter )
					{
						auxDur = durScale * secondByQuarter;
					}
				}
			}
			
			if( auxDur > 0 )
			{
				noteDuration = (long)( auxDur * 1e3D ) + 100L; // 100 millisecond of margin
			}
			*/

			/*
			int track = 0;
			
			synchronized( this.realTimeTrackAvaible )
			{
				if( this.realTimeTrackAvaible.size() >  0 )
				{
					track = this.realTimeTrackAvaible.get( 0 );
					this.realTimeTrackAvaible.remove( 0 );
				}
			}
			*/
						
			//PlayerNote player = new PlayerNote( NotesPattern, noteDuration, track );
			PlayerNote player = new PlayerNote( NotesPattern );
						
			this.players.put( player.getName(), player );
			player.startThread();
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
	
	/*
	private void PlayerDead( String id, int track )
	{
		synchronized ( this.realTimeTrackAvaible ) 
		{
			this.realTimeTrackAvaible.add( 0, track );			
		}
		
		synchronized ( players )
		{
			this.players.remove( id );			
		}
	}
	*/
	
	private void PlayerDead( String id )
	{		
		synchronized ( players )
		{
			this.players.remove( id );			
		}
	}
	
	/**
	 * 
	 * @author Manuel Merino Monge
	 *
	 */
	/*
	private class PlayerNote extends AbstractStoppableThread  
	{	
		private long noteDuration;
		private Pattern musicPattern;
		
		private RealtimePlayer player;
		//private Player player;
		
		private int track = 0;
				
		public PlayerNote( Pattern pattern, long duration, int track ) throws MidiUnavailableException 
		{
			this.noteDuration = duration;
			
			this.musicPattern = pattern;
						
			this.player = new RealtimePlayer();
			this.player.changeTrack( track );
			//this.player = new  Player();
			
			this.track = track;
		}

		public RealtimePlayer getRealTimePlayer()
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
				this.player.play( this.musicPattern );
				
				System.out.println("IRORealtimePlayer.PlayerNote.runInLoop() " + noteDuration);
				super.wait( noteDuration );
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
			
			this.player.close();
			this.player = null;
						
			PlayerDead( super.getName(), this.track );
		}
	}
	*/
	
	private class PlayerNote extends AbstractStoppableThread implements ManagedPlayerListener  
	{	
		private Pattern musicPattern;
		
		private PlayerMod player;
		
		public PlayerNote( Pattern pattern ) throws MidiUnavailableException 
		{
			this.musicPattern = pattern;
						
			this.player = new PlayerMod();
			this.player.getManagedPlayer().addManagedPlayerListener( this );
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
				this.player.play( this.musicPattern );
				
				this.wait();
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
