package control.music;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import GUI.game.screen.level.BackgroundMusic;
import control.events.BackgroundMusicEventListener;
import music.IROTrack;
import music.player.IROPlayer;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppableThread;

public class MusicPlayerControl extends AbstractStoppableThread 
{	
	private IROPlayer iroPlayer;
	
	private BackgroundMusic backgroundMusic = null;
	
	private static MusicPlayerControl mpctr = null;
	
	private AtomicBoolean isPlay = new AtomicBoolean( false );
	
	private long startTime = -1;
	
	private Object sync = new Object();
	
	private ConcurrentLinkedQueue< String > playtrackIDList;
	
	private MusicPlayerControl( )
	{	
		super.setName( this.getClass().getSimpleName() );	
		
		this.playtrackIDList = new ConcurrentLinkedQueue<String>();
		
		try
		{
			super.startThread();
		} 
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static MusicPlayerControl getInstance()
	{
		if( mpctr == null )
		{
			mpctr = new MusicPlayerControl();
		}
		
		return mpctr;
	}
	
	public void setBackgroundMusicPatter( BackgroundMusic backgroundMusicPattern )
	{
		if( this.backgroundMusic != null )
		{
			this.backgroundMusic.stopThread( IStoppableThread.FORCE_STOP );
		}
		
		this.backgroundMusic = backgroundMusicPattern;				
	}
	
	public void addBackgroundMusicEvent( BackgroundMusicEventListener listener ) 
	{	
		if( this.backgroundMusic != null )
		{
			this.backgroundMusic.addBackgroundMusicEventListener( listener );
		}
	}	
	
	@Override
	protected void preStopThread( int friendliness ) throws Exception 
	{		
	}

	@Override
	protected void postStopThread( int friendliness ) throws Exception 
	{	
	}
	
	public void startMusic( ) throws Exception 
	{
		synchronized( this.sync )
		{
			if( this.iroPlayer != null )
			{
				this.iroPlayer.stop();
				this.iroPlayer = null;
			}
			
			this.iroPlayer = new IROPlayer();
			
			if( this.backgroundMusic != null )
			{						
				this.backgroundMusic.startThread();
			}
			
			this.isPlay.set( true );
			
			this.startTime = System.nanoTime();
		}
	}
	
	public void stopMusic( ) throws Exception 
	{
		synchronized( this.sync )
		{
			if( this.iroPlayer != null )
			{
				this.iroPlayer.stop();
				this.iroPlayer = null;
			}
			
			if( this.backgroundMusic != null )
			{						
				this.backgroundMusic.stopThread( IStoppableThread.FORCE_STOP );
			}
			
			this.isPlay.set( false );
		}
	}
	
	public double getPlayTime()
	{
		return ( System.nanoTime() - this.startTime ) / 1e9D;
	}
	
	public boolean isPlay()
	{
		synchronized( this.sync )
		{
			return this.isPlay.get();
		}
	}
	
	@Override
	protected void runInLoop() throws Exception 
	{
		synchronized ( this )
		{
			super.wait();
			
			synchronized( this.sync )
			{
				if( this.iroPlayer != null )
				{
					while( !this.playtrackIDList.isEmpty() )
					{						
						String trackID = this.playtrackIDList.poll();
						this.iroPlayer.play( trackID );
					}
					
				}
			}
		}
	}
	
	@Override
	protected void runExceptionManager(Exception e) 
	{
		if( !( e instanceof InterruptedException )  )
		{
			super.runExceptionManager( e );
		}
	}
	
	@Override
	protected void cleanUp() throws Exception 
	{
		super.cleanUp();
				
		synchronized( this )
		{
			if( this.backgroundMusic != null )
			{
				this.backgroundMusic.stopThread( IStoppableThread.FORCE_STOP );
				this.backgroundMusic = null;
			}
			
			if( this.iroPlayer != null )
			{
				this.iroPlayer.stop();
			}
			
			this.playtrackIDList.clear();
			
			this.iroPlayer = null;
		}	
	}
	
	public void loadTrack( IROTrack track )
	{
		synchronized( this )
		{
			synchronized( this.sync )
			{
				this.iroPlayer.loadTrack( track );
			}
		}
	}
	
	public void loadTracks( String trackID, List< IROTrack > tracks )
	{
		synchronized( this )
		{
			synchronized( this.sync )
			{
				this.iroPlayer.loadTracks( trackID, tracks );
			}
		}
	}
	
	public void stopTrack( String trackID )
	{
		synchronized( this )
		{
			synchronized( this.sync )
			{
				try
				{
					this.iroPlayer.stopTrack( trackID );
				} 
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	public void playTrack( String trackID )
	{
		synchronized( this )
		{
			this.playtrackIDList.add( trackID );
			
			super.notify();
		}
	}
	
	public void playTracks( List< String > trackIDs )
	{
		synchronized( this )
		{
			this.playtrackIDList.addAll( trackIDs );
			
			super.notify();
		}
	}
}
