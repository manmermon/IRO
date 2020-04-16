package control.music;

import java.util.ArrayList;
import java.util.List;
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
	
	private List< IROTrack > patternList;
	
	private static MusicPlayerControl mpctr = null;
	
	private AtomicBoolean isPlay = new AtomicBoolean( false );
	
	private MusicPlayerControl( )
	{	
		this.patternList = new ArrayList< IROTrack >();
		
		super.setName( this.getClass().getSimpleName() );	
		
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
		synchronized( this.patternList )
		{
			if( this.iroPlayer != null )
			{
				this.iroPlayer.stop();
				this.iroPlayer = null;
			}
		}
		
		if( this.backgroundMusic != null )
		{						
			this.isPlay.set( true );
			this.backgroundMusic.startThread();						
			this.iroPlayer = new IROPlayer();
		}
	}
	
	public void stopMusic( ) throws Exception 
	{
		synchronized( this.patternList )
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
			this.patternList.clear();
		}
	}
	
	public boolean isPlay()
	{
		synchronized( this.patternList )
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
			
			synchronized( this.patternList )
			{
				if( this.iroPlayer != null )
				{
					this.iroPlayer.play( this.patternList );
				}
				
				this.patternList.clear();
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
		
		if( this.backgroundMusic != null )
		{
			this.backgroundMusic.stopThread( IStoppableThread.FORCE_STOP );
			this.backgroundMusic = null;
		}
				
		synchronized( this.patternList )
		{
			if( this.iroPlayer != null )
			{
				this.iroPlayer.stop();
			}
			
			this.iroPlayer = null;
		}	
	}
		
	public void playNotes( IROTrack track )
	{
		synchronized( this )
		{
			synchronized( this.patternList )
			{
				this.patternList.add( track );
			}
			
			super.notify();
		}
	}
	
	public void playNotes( List< IROTrack > tracks )
	{
		synchronized( this )
		{
			synchronized( this.patternList )
			{
				this.patternList.addAll( tracks );
			}
			
			super.notify();
		}
	}
}
