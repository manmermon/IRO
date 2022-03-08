package control.music;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.jfugue.pattern.Pattern;

import gui.game.screen.level.music.BackgroundMusic;
import music.sheet.IROTrack;
import control.events.BackgroundMusicEventListener;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppableThread;
import tools.MusicSheetTools;

public class MusicPlayerControl extends AbstractStoppableThread 
{	
	private BackgroundMusic backgroundMusic = null;
	private Map< Integer, BackgroundMusic > playerDissonantMusicSheets = null;
	
	private ConcurrentHashMap< Integer, Double > mutePlayerSheet = null;
	
	private static MusicPlayerControl mpctr = null;
	
	private AtomicBoolean isPlay = new AtomicBoolean( false );
	private boolean isBackPlay = false;
	
	private long startTime = -1;
	private Long pauseTime = null;
	private double pauseAdjust = 0;
	
	private Object sync = new Object();
	
	private boolean isMuteSession = false;
	
	private Stack< Integer > tempoStack = new Stack<Integer>();
	
	private MusicPlayerControl( )
	{	
		super.setName( this.getClass().getSimpleName() );	
		
		this.playerDissonantMusicSheets = new HashMap<Integer, BackgroundMusic>();
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
	
	public void setMuteSession( boolean mute )
	{
		synchronized ( this.sync )
		{
			this.isMuteSession = mute;
		}
	}
	
	public void setBackgroundMusicPatter( BackgroundMusic backgroundMusicPattern )
	{
		if( this.backgroundMusic != null )
		{
			this.backgroundMusic.stopThread( IStoppableThread.FORCE_STOP );
		}
		
		this.backgroundMusic = backgroundMusicPattern;				
	}
	
	/*
	public void setPlayerMusicSheets( Map< Integer, BackgroundMusic > playerSheets )
	{
		if( !this.playerDissonantMusicSheets.isEmpty() )
		{
			for( BackgroundMusic pbgm : this.playerDissonantMusicSheets.values() )
			{
				pbgm.stopThread( IStoppableThread.FORCE_STOP );
			}
		}
		
		this.playerDissonantMusicSheets.putAll( playerSheets );
	}
	//*/
	
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
						
			for( BackgroundMusic playerSheet : this.playerDissonantMusicSheets.values() )
			{
				playerSheet.setMuteSession( this.isMuteSession );
				playerSheet.startThread();
			}
			
			if( this.backgroundMusic != null )
			{						
				this.backgroundMusic.setMuteSession( this.isMuteSession );
				this.backgroundMusic.startThread();
			}
			
			this.isPlay.set( true );
			this.pauseTime = null;
			this.pauseAdjust = 0;
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
			for( BackgroundMusic playerSheet : this.playerDissonantMusicSheets.values() )
			{
				playerSheet.stopThread( IStoppableThread.FORCE_STOP );
			}
			this.playerDissonantMusicSheets.clear();
			
			if( this.backgroundMusic != null )
			{	
				this.backgroundMusic.stopThread( IStoppableThread.FORCE_STOP );
			}
			
			this.isMuteSession = false;			
			this.isBackPlay = false;
			this.isPlay.set( false );
		}
	}
	
	public void setPauseMusic( boolean pause )
	{
		synchronized ( this.sync )
		{
			if( pause &&  this.pauseTime == null )
			{
				this.pauseTime = System.nanoTime();
			}
			else if( this.pauseTime != null ) 
			{	
				this.pauseAdjust += ( System.nanoTime() - this.pauseTime ) / 1e9D;
				
				this.pauseTime = null;
			}
			
			if( this.backgroundMusic != null )
			{
				this.backgroundMusic.setPause( pause );
			}
				
			for( BackgroundMusic playerSheet : this.playerDissonantMusicSheets.values() )
			{
				playerSheet.setPause( pause );
			}
		}
	}
	
	public double getPlayTime()
	{		
		synchronized ( this.sync ) 
		{
			double t = System.nanoTime() - this.startTime;
			
			if( this.pauseTime != null )
			{
				t = this.pauseTime - this.startTime;
			}
			
			t = t / 1e9D - this.pauseAdjust;
				
			return t;
		}		
	}
	
	public double getBackgroundMusicTime()
	{
		double t = Double.NaN;
		
		if( this.backgroundMusic != null )
		{
			t = this.backgroundMusic.getCurrentMusicSecondPosition();
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
			Double muteTime = -1D;
			
			while( players.hasMoreElements() )
			{
				int iPlayer = players.nextElement();
				
				double mt = this.mutePlayerSheet.get( iPlayer );
				if( mt > muteTime )
				{
					mt = muteTime;
				}
				
				//System.out.println("MusicPlayerControl.runInLoop() " + iPlayer);
				BackgroundMusic playerSheet = this.playerDissonantMusicSheets.get( iPlayer );
			
				if( playerSheet != null )
				{
					playerSheet.startThread();
				}
			}
			
			if( muteTime > 0 && this.backgroundMusic != null )
			{
				this.backgroundMusic.mute( muteTime );
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
			for( BackgroundMusic bg : this.playerDissonantMusicSheets.values() )
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
	
	public void playDissonantTrack( int player, List< IROTrack > tracks )
	{
		if( !this.isMuteSession && tracks != null && !tracks.isEmpty() )
		{
			double muteTime = Double.MIN_VALUE;
			Pattern pat = new Pattern();
			
			for( IROTrack t : tracks )
			{
				pat.add( t.getPatternTrackSheet() );
				double ti = t.getTrackDuration();
				if( ti > muteTime )
				{
					muteTime = ti;
				}
			}			
			
			try 
			{
				BackgroundMusic pm = new BackgroundMusic();
				pm.setPattern( pat );

				this.playerDissonantMusicSheets.put( player, pm );
			}
			catch (MidiUnavailableException | InvalidMidiDataException e) 
			{
			}
			
			this.mutePlayerSheet( player, muteTime );
		}
	}
		
	public void changeTempo( int newTempo )
	{
		synchronized( this.sync )
		{
			if( this.isPlay() ) 
			{
				this.setPauseMusic( true );
				
				int tempo = this.backgroundMusic.getTempo();
				
				this.tempoStack.add( tempo );
				System.out.println("MusicPlayerControl.changeTempo() new tempo = " + newTempo );
			
				try
				{
					long tickPos = this.backgroundMusic.getCurrentMusicTickPosition();
					String pat = this.backgroundMusic.getPattern().toString();
								
					BackgroundMusicEventListener[] listerns = this.backgroundMusic.getBackgroundMusicEventListeners();				
					for( BackgroundMusicEventListener bml : listerns )
					{
						this.backgroundMusic.removeBackgroundMusicEventListener( bml );
					}				
					this.backgroundMusic.stopThread( IStoppableThread.FORCE_STOP );
					
					BackgroundMusic bgMusic = new BackgroundMusic();
					int delay = this.backgroundMusic.getRemainingDelay();
					double prop = (double)tempo / newTempo;
					bgMusic.setDelay( prop*delay / 1000D );
										
					this.backgroundMusic = bgMusic;
					
					for( BackgroundMusicEventListener bml : listerns )
					{
						bgMusic.addBackgroundMusicEventListener( bml );
					}	
										
					pat = MusicSheetTools.changeMusicTempo( pat, newTempo );					
					bgMusic.setPattern( new Pattern( pat ) );
					bgMusic.setMusicTickPosition( tickPos );
					
					CyclicBarrier b = new CyclicBarrier( 1 + this.playerDissonantMusicSheets.size() );
					
					bgMusic.setCoordinator( b );
					
					for( Integer id : this.playerDissonantMusicSheets.keySet() )
					{
						BackgroundMusic pms = this.playerDissonantMusicSheets.get( id );
						pat = pms.getPattern().toString();
						tickPos = pms.getCurrentMusicTickPosition();
						
						BackgroundMusic bgm = new BackgroundMusic();
						listerns = pms.getBackgroundMusicEventListeners();				
						for( BackgroundMusicEventListener bml : listerns )
						{
							pms.removeBackgroundMusicEventListener( bml );
						}				
						pms.stopThread( IStoppableThread.FORCE_STOP );
						
						for( BackgroundMusicEventListener bml : listerns )
						{
							bgm.addBackgroundMusicEventListener( bml );
						}											
						pat = MusicSheetTools.changeMusicTempo( pat, newTempo );				
						bgm.setPattern( new Pattern( pat ) );
						bgm.setMusicTickPosition( tickPos );						
						bgm.setCoordinator( b );
						
						this.playerDissonantMusicSheets.put( id, bgm );
					}
					
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
				
				//this.setPauseMusic( false );
			}
		}
		
		/*
		if( this.isPlay() )
		{
			try 
			{
				this.startMusic();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		*/
	}
	
	public void restoreTempo()
	{
		if( !this.tempoStack.isEmpty() )
		{
			this.changeTempo( this.tempoStack.pop() );
			//this.tempoStack.pop();
		}
	}
	
	public int getMusicTempo()
	{
		int tempo = 120;
		
		synchronized ( this.sync )
		{
			if( this.backgroundMusic != null )
			{
				tempo = this.backgroundMusic.getTempo();
			}
		}
		
		return tempo;
	}
}
