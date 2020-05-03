package control.music;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import GUI.game.screen.level.BackgroundMusic;
import control.events.BackgroundMusicEventListener;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppableThread;

public class MusicPlayerControl extends AbstractStoppableThread 
{	
	private BackgroundMusic backgroundMusic = null;
	private Map< Integer, BackgroundMusic > playerMusicSheets = null;
	
	private ConcurrentHashMap< Integer, Double > mutePlayerSheet = null;
	
	private static MusicPlayerControl mpctr = null;
	
	private AtomicBoolean isPlay = new AtomicBoolean( false );
	private boolean isBackPlay = false;
	
	private long startTime = -1;
	
	private Object sync = new Object();
		
	private MusicPlayerControl( )
	{	
		super.setName( this.getClass().getSimpleName() );	
		
		this.playerMusicSheets = new HashMap<Integer, BackgroundMusic>();
		this.mutePlayerSheet = new ConcurrentHashMap< Integer, Double>();
		
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
	
	public void setPlayerMusicSheets( Map< Integer, BackgroundMusic > playerSheets )
	{
		if( !this.playerMusicSheets.isEmpty() )
		{
			for( BackgroundMusic pbgm : this.playerMusicSheets.values() )
			{
				pbgm.stopThread( IStoppableThread.FORCE_STOP );
			}
		}
		
		this.playerMusicSheets.putAll( playerSheets );
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
			this.mutePlayerSheet.clear();
			
			this.startTime = System.nanoTime();
			
			for( BackgroundMusic playerSheet : this.playerMusicSheets.values() )
			{
				playerSheet.startThread();
			}
			
			if( this.backgroundMusic != null )
			{						
				this.backgroundMusic.startThread();
			}
			
			this.isPlay.set( true );
		}
	}
	
	/**
	 * @return the isPlayBackgroundMusic
	 */
	public boolean isPlayBackgroundMusic()
	{
		return this.isBackPlay;
	}
	
	public void stopMusic( ) throws Exception 
	{
		synchronized( this.sync )
		{
			for( BackgroundMusic playerSheet : this.playerMusicSheets.values() )
			{
				playerSheet.stopThread( IStoppableThread.FORCE_STOP );
			}
			this.playerMusicSheets.clear();
			
			if( this.backgroundMusic != null )
			{	
				this.backgroundMusic.stopThread( IStoppableThread.FORCE_STOP );
			}
			
			this.isBackPlay = false;
			this.isPlay.set( false );
		}
	}
	
	public double getPlayTime()
	{
		return ( System.nanoTime() - this.startTime ) / 1e9D;
	}
	
	public double getBackgroundMusicTime()
	{
		double t = Double.NaN;
		
		if( this.backgroundMusic != null )
		{
			t = this.backgroundMusic.getCurrentMusicPosition();
		}
		
		return t;
	}
	
	public double getBackgroundMusicStartDelay()
	{
		double d = Double.NaN;
		
		if( this.backgroundMusic != null )
		{
			d = this.backgroundMusic.getDelay();
		}
		
		return d;
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
		}
		
		synchronized( this.sync )
		{
			Enumeration< Integer > players = this.mutePlayerSheet.keys();
			while( players.hasMoreElements() )
			{
				int iPlayer = players.nextElement();
				
				Double muteTime = this.mutePlayerSheet.get( iPlayer );
				
				BackgroundMusic playerSheet = this.playerMusicSheets.get( iPlayer );
				
				playerSheet.mute( muteTime );
			}
			
			this.mutePlayerSheet.clear();
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
			for( BackgroundMusic bg : this.playerMusicSheets.values() )
			{
				bg.stopThread( IStoppableThread.FORCE_STOP );
			}
			this.mutePlayerSheet.clear();
			
			if( this.backgroundMusic != null )
			{
				this.backgroundMusic.stopThread( IStoppableThread.FORCE_STOP );
				this.backgroundMusic = null;
			}
			
			this.mutePlayerSheet.clear();						
		}	
	}
	
	public void mutePlayerSheet( int player, double time )
	{
		synchronized( this )
		{
			synchronized( this.sync )
			{
				this.mutePlayerSheet.put( player, time );
				
				super.notify();
			}
		}
	}
}
