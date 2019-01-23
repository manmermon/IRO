package control;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sound.midi.MidiUnavailableException;

import GUI.screens.levels.BackgroundMusic;
import music.IROChord;
import music.player.IRORealtimePlayer;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppableThread;

public class PlayerControl extends AbstractStoppableThread
{	
	private IRORealtimePlayer realPlayer;
	
	private BackgroundMusic backgroundMusic = null;
	
	private List< IROChord > patternList;
	
	public PlayerControl( BackgroundMusic backgroundMusicPattern, int tempo ) throws MidiUnavailableException
	{	
		//this.realPlayer = new RealtimePlayer();
		
		this.backgroundMusic = backgroundMusicPattern;
		
		this.patternList = new ArrayList< IROChord >();
		
		super.setName( this.getClass().getSimpleName() );		
	}
	

	@Override
	protected void preStopThread( int friendliness ) throws Exception 
	{		
	}

	@Override
	protected void postStopThread( int friendliness ) throws Exception 
	{	
	}
	
	@Override
	protected void preStart() throws Exception 
	{
		// TODO Auto-generated method stub
		super.preStart();
	}
	
	@Override
	protected void startUp( ) throws Exception 
	{
		super.startUp();
		
		if( this.backgroundMusic != null )
		{						
			this.backgroundMusic.startThread();
		}		
		
		if( this.realPlayer != null )
		{
			this.realPlayer.stop();
		}
		
		this.realPlayer = new IRORealtimePlayer();
	}
	
	@Override
	protected void runInLoop() throws Exception 
	{
		synchronized ( this )
		{
			super.wait();
			
			synchronized( this.patternList )
			{
				if( t == 0 )
				{
					t = System.currentTimeMillis();
				}
				
				for( IROChord chord: this.patternList )
				{					
					//System.out.printf( "<%s, %.2f>", note, ( System.currentTimeMillis() - t ) / 1e3D );
					
					this.realPlayer.play( chord.getNotes(), chord.getInstrument(), chord.getVoice(), chord.getLayer(), chord.getTempo() );
				}
				
				
				//System.out.println("PlayerControl.runInLoop() " + noteList + ": tiempo = " + ( System.currentTimeMillis() - t ) / 1e3D);
				
				//t = System.currentTimeMillis();
				
				this.patternList.clear();
			}
		}
	}
	long t = 0;
	
	@Override
	protected void runExceptionManager(Exception e) 
	{
		if( !( e instanceof InterruptedException )  )
		{
			System.err.println("\nPlayerControl.runExceptionManager() " + this.getClass().getSimpleName() );
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
		
		if( this.realPlayer != null )
		{
			this.realPlayer.stop();			
		}
		this.realPlayer = null;
				
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
	}
		
	public void playNotes( IROChord chord )
	{
		synchronized( this )
		{
			synchronized( this.patternList )
			{
				this.patternList.add( chord );
			}
			
			super.notify();
		}
	}
	
	public void playNotes( List< IROChord > chords )
	{
		synchronized( this )
		{
			synchronized( this.patternList )
			{
				this.patternList.addAll( chords );
			}
			
			super.notify();
		}
	}
	
}
