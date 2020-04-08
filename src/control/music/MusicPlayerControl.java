package control.music;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiUnavailableException;

import GUI.screens.levels.BackgroundMusic;
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
	
	public MusicPlayerControl( BackgroundMusic backgroundMusicPattern ) throws MidiUnavailableException
	{	
		//this.realPlayer = new RealtimePlayer();
		
		this.backgroundMusic = backgroundMusicPattern;
		
		this.patternList = new ArrayList< IROTrack >();
		
		super.setName( this.getClass().getSimpleName() );		
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
	
	/*
	@Override
	protected void preStart() throws Exception 
	{
		super.preStart();
	}
	*/
	
	@Override
	protected void startUp( ) throws Exception 
	{
		super.startUp();
		
		if( this.backgroundMusic != null )
		{						
			this.backgroundMusic.startThread();
		}		
		
		if( this.iroPlayer != null )
		{
			this.iroPlayer.stop();
		}
		
		this.iroPlayer = new IROPlayer();
	}
	
	@Override
	protected void runInLoop() throws Exception 
	{
		synchronized ( this )
		{
			super.wait();
			
			synchronized( this.patternList )
			{
				this.iroPlayer.play( this.patternList );
				
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
		
		if( this.iroPlayer != null )
		{
			this.iroPlayer.stop();			
		}
		this.iroPlayer = null;
		
		/*
		Set< Thread > threads = Thread.getAllStackTraces().keySet();
		
		for( Thread thread : threads )
		{				
			boolean del = false;
			
			for( StackTraceElement el : thread.getStackTrace() )
			{
				if( el.getFileName() != null && el.getFileName().equals( "RealtimeMidiParserListener.java" ) )
				{
					del = true;
					break;
				}
			}
			
			if( del )
			{
				thread.stop();
			}
		}
		*/
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
