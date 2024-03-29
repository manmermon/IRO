package gui.game.screen.level.music;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Synthesizer;
import javax.swing.event.EventListenerList;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.ManagedPlayerListener;
import org.jfugue.player.SynthesizerManager;

import gui.game.screen.IPausable;
import control.events.BackgroundMusicEvent;
import control.events.BackgroundMusicEventListener;
import music.jfugueIRO.PlayerMod;
import music.sheet.IROTrack;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppable;
import thread.timer.PausableTimer;
import tools.MusicSheetTools;

public class BackgroundMusic extends AbstractStoppableThread 
								implements ManagedPlayerListener, IPausable
{
	public static final double NON_DELAY = 0.0;
	
	private PlayerMod player;
	
	private Pattern pattern;
	private double delay;
	
	private EventListenerList listenerList;
	
	private Object lock = new Object();
	
	private MuteThread muteThread = null;
	
	private PausableTimer delayTimer = null;
	
	private CyclicBarrier barrier = null;
	
	private boolean mute = false;
	
	private int tempo = 120;

	//private ManagedPlayerMod managedPlayerMod;
	
 	public BackgroundMusic() 
	{
		super.setName( this.getClass().getSimpleName() );
		this.player = new PlayerMod();
		this.player.getManagedPlayer().addManagedPlayerListener( this );
		
		this.muteThread = new MuteThread( this.player );
		
		pattern = new Pattern();
		delay = NON_DELAY;
		
		this.listenerList = new EventListenerList();		
	}
 	
	public void setPattern( Pattern pattern ) throws MidiUnavailableException, InvalidMidiDataException 
	{
		this.pattern = pattern;
		this.player.load( pattern );
		
		this.tempo =  MusicSheetTools.getTempo( pattern.toString() ) ;	
	}
		
	public Pattern getPattern() 
	{
		return pattern;
	}
	
	/**
	 * @param mute the mute to set
	 */
	public void setMuteSession( boolean mute ) 
	{
		this.mute = mute;
	}
	
	public boolean isMute() 
	{
		return this.mute;
	}
	
	/*
	public void setVolume( double gain )
	{
		gain = gain < 0 ? 0 : ( gain > 1 ? 1 : gain );
		
		if( this.player != null )
		{
			Synthesizer syn = ((Synthesizer) this.player.getSequence( this.pattern ));
			
			for( MidiChannel cs : syn.getChannels() )
			{
				cs.controlChange( 7, (int)( gain * 127.0 ) );
			}			
		}
	}
	*/
	
	public void setDelay( double delay ) 
	{
		this.delay = delay;
	}
	
	public double getDelay() 
	{
		return delay;
	}
	
	public int getRemainingDelay()
	{
		int t = 0;
		
		if( this.delayTimer != null )
		{
			t = this.delayTimer.getRemainingTime();
		}
		
		return t;
	}
	
	public void setCoordinator( CyclicBarrier barrier )
	{
		this.barrier = barrier;
	}
		
	public int getTempo()
	{
		return this.tempo;
	}
	
	@Override
	protected void preStopThread(int friendliness) throws Exception 
	{	
	}

	@Override
	protected void postStopThread(int friendliness) throws Exception 
	{	
		if( friendliness == IStoppable.FORCE_STOP )
		{
			if( this.player != null )
			{
				synchronized ( this.lock )
				{
					final PlayerMod playerCopy = player;
					
					Thread t = new Thread()
					{
						/*(non-Javadoc)
						 * @see @see java.lang.Thread#run()
						 */
						@Override
						public void run()
						{
							try
							{
								playerCopy.getManagedPlayer().finish();
							}
							catch (Exception ex)
							{
							}
						}
					};
					t.setName( "backgroundMusic.postStopThread.player.getManagedPlayer.finish" );
					t.start();
				}
			}
		}
	}
	
	/*(non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#startUp()
	 */
	@Override
	protected void startUp() throws Exception
	{
		super.startUp();
		
		this.muteThread.startActing();
		
		if( this.delay > 0 )
		{
			this.delayTimer = new PausableTimer( (int)( this.delay * 1000 )
					, new ActionListener() 
						{
							@Override
							public void actionPerformed(ActionEvent arg0) 
							{								
								delayTimerFinish();
							}
						} 
			); 
		}
	}
	
	private void delayTimerFinish()
	{
		synchronized ( this )
		{
			synchronized ( this.lock )
			{
				this.delayTimer = null;
			}
			
			this.notify();
		}
	}
	
	@Override
	protected void runInLoop() throws Exception 
	{	
		synchronized( this )
		{
			/*
			if( this.delay > 0 )
			{
				this.wait( (long)( 1000L * this.delay ) );
			}
			*/
			
			if( this.delayTimer != null )
			{
				this.delayTimer.startThread();
				this.wait();
			}
			
			if( this.barrier != null )
			{
				this.barrier.await();
			}
			
			if( this.mute )
			{
				final double t = this.player.getManagedPlayer().getMicrosecondLength() / 1e6D;				
				
				/*
				this.player.getManagedPlayer().addManagedPlayerListener( new ManagedPlayerListener() 
				{					
					@Override
					public void onStarted(Sequence sequence) 
					{	
						mute( t );
					}
					
					@Override
					public void onSeek(long tick) 
					{}
					
					@Override
					public void onResumed() 
					{}
					
					@Override
					public void onReset() 
					{}
					
					@Override
					public void onPaused() 
					{}
					
					@Override
					public void onFinished() 
					{}
				});
				*/
				//this.mute( t );
				
				this.player.getManagedPlayer().muteTrack( -1,  true );
			}
			
			this.player.play( );
						
			this.fireBackgroundMusicEvent( BackgroundMusicEvent.START );
			
			//this.fireSceneEvent( BackgroundMusicEvent.END );
			//*/
			
			this.wait();
		}
	}
		
	public void mute( double time )
	{
		synchronized ( this.lock )
		{
			if( this.muteThread != null )
			{
				this.muteThread.mutePlayer( time );
			}
		}
	}
	
	public void setMusicTickPosition( long tick )
	{
		synchronized ( this.lock )
		{
			this.player.getManagedPlayer().seek( tick );
		}
	}
	
	public double getCurrentMusicSecondPosition()
	{
		long microsec  = 0;
		synchronized ( this.lock )
		{
			try
			{
				microsec = this.player.getManagedPlayer().getMicrosecondPosition();
			}
			catch (NullPointerException ex)
			{}
		}		
		
		return microsec / 1e6D;
	}
	
	public long getCurrentMusicTickPosition()
	{
		long tickPos  = 0;
		synchronized ( this.lock )
		{
			try
			{
				tickPos = this.player.getManagedPlayer().getTickPosition();
			}
			catch (NullPointerException ex)
			{}
		}		
		
		return tickPos;
	}
		
	@Override
	protected void runExceptionManager(Exception e) 
	{
		if( !( e instanceof InterruptedException ) )
		{
			super.runExceptionManager( e );
		}	
	}
	
	/*(non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#finallyManager()
	 */
	@Override
	protected void finallyManager()
	{
		super.stopThread = true;
		this.fireBackgroundMusicEvent( BackgroundMusicEvent.END );
	}
	
	@Override
	protected void cleanUp() throws Exception 
	{
		super.cleanUp();
		
		synchronized ( this.lock )
		{
			this.muteThread.stopActing( IStoppable.FORCE_STOP );
			this.muteThread = null;
			
			final PlayerMod playercopy = this.player;
			this.player = null;
			
			Thread t = new Thread()
			{
				/*(non-Javadoc)
				 * @see @see stoppableThread.AbstractStoppableThread#run()
				 */
				@Override
				public synchronized void run()
				{
					playercopy.getManagedPlayer().finish();
				}
			};
			
			t.setName( "Background.player.getManagedPlayer.finish" );
			
			t.start();
		}		
	}
	
	public void addBackgroundMusicEventListener( BackgroundMusicEventListener listener) 
	{
		this.listenerList.add( BackgroundMusicEventListener.class, listener );
	}
	
	public void removeBackgroundMusicEventListener( BackgroundMusicEventListener listener) 
	{
		this.listenerList.remove( BackgroundMusicEventListener.class, listener );
	}
	
	public BackgroundMusicEventListener[] getBackgroundMusicEventListeners()
	{
		return this.listenerList.getListeners( BackgroundMusicEventListener.class );
	}
	
	private synchronized void fireBackgroundMusicEvent( int typeEvent )
	{
		BackgroundMusicEvent event = new BackgroundMusicEvent( this, typeEvent );

		BackgroundMusicEventListener[] listeners = this.listenerList.getListeners( BackgroundMusicEventListener.class );

		for (int i = 0; i < listeners.length; i++ ) 
		{
			listeners[ i ].BackgroundMusicEvent( event );
		}
	}

	/*(non-Javadoc)
	 * @see @see org.jfugue.player.ManagedPlayerListener#onStarted(javax.sound.midi.Sequence)
	 */
	@Override
	public void onStarted(Sequence sequence)
	{
		// TODO Auto-generated method stub
		
	}

	/*(non-Javadoc)
	 * @see @see org.jfugue.player.ManagedPlayerListener#onFinished()
	 */
	@Override
	public void onFinished()
	{
		synchronized ( this )
		{
			super.notify();
		}
	}

	/*(non-Javadoc)
	 * @see @see org.jfugue.player.ManagedPlayerListener#onPaused()
	 */
	@Override
	public void onPaused()
	{
	}

	/*(non-Javadoc)
	 * @see @see org.jfugue.player.ManagedPlayerListener#onResumed()
	 */
	@Override
	public void onResumed()
	{	
	}

	/*(non-Javadoc)
	 * @see @see org.jfugue.player.ManagedPlayerListener#onSeek(long)
	 */
	@Override
	public void onSeek(long tick)
	{
	}

	/*(non-Javadoc)
	 * @see @see org.jfugue.player.ManagedPlayerListener#onReset()
	 */
	@Override
	public void onReset()
	{
	}

	@Override
	public void setPause( boolean pause ) 
	{
		synchronized ( this.lock )
		{
			if( this.player != null )
			{
				if( this.delayTimer == null  )
				{
					if( pause )
					{
						try
						{
							this.player.getManagedPlayer().pause();					
						}
						catch (Exception e)
						{
							//e.printStackTrace();
						}
					}
					else if( this.player.getManagedPlayer().isPaused() )
					{
						try
						{
							this.player.getManagedPlayer().resume();
						}
						catch (Exception e) 
						{
						}
					}
				}
				else
				{
					if( pause )
					{
						this.delayTimer.pauseTimer();
					}
					else
					{
						this.delayTimer.resumenTimer();
					}
				}
			}
		}
	}
	
	@Override
	public boolean isPaused() 
	{
		synchronized ( this.lock )
		{
			boolean pause = this.player.getManagedPlayer().isPaused();

			if( !pause && this.delayTimer != null )
			{
				pause = !this.delayTimer.isRunning();
			}
			
			return  pause;
		}		
	}
	
	//////////////////////////
	//
	//
	// Mute player thread
	//
	//
	private class MuteThread extends AbstractStoppableThread
	{
		private PlayerMod player;
		
		private double muteTime = 0D;
		private boolean isMute = false;

		public MuteThread( PlayerMod player )
		{
			this.player = player;
			
			super.setName( this.getClass().getName() );
		}

		/*(non-Javadoc)
		 * @see @see stoppableThread.AbstractStoppableThread#preStopThread(int)
		 */
		@Override
		protected void preStopThread(int friendliness) throws Exception
		{	
		}

		/*(non-Javadoc)
		 * @see @see stoppableThread.AbstractStoppableThread#postStopThread(int)
		 */
		@Override
		protected void postStopThread(int friendliness) throws Exception
		{	
		}

		/*(non-Javadoc)
		 * @see @see stoppableThread.AbstractStoppableThread#startUp()
		 */
		@Override
		protected void startUp() throws Exception
		{
			super.startUp();
			
			super.stopThread = super.stopThread || ( this.player == null );
		}
		
		/*(non-Javadoc)
		 * @see @see stoppableThread.AbstractStoppableThread#runInLoop()
		 */
		@Override
		protected void runInLoop() throws Exception
		{
			synchronized ( this )
			{
				if( this.muteTime <= 0D )
				{					
					if( this.isMute )
					{
						this.player.getManagedPlayer().muteTrack( -1,  false );
						this.isMute = false;
					}
					
					super.wait();
				}
			}
			
			double time = 0;
			synchronized ( this )
			{ 
				time = this.muteTime; 
				this.muteTime = 0D;
				this.isMute = true;
			}	
			
			this.player.getManagedPlayer().muteTrack( -1,  true );
						
			synchronized ( this )
			{
				super.wait( (long)( time  * 1000 ) );
			}			
		}
		
		/*(non-Javadoc)
		 * @see @see stoppableThread.AbstractStoppableThread#cleanUp()
		 */
		@Override
		protected void cleanUp() throws Exception
		{
			this.player = null;
			
			super.cleanUp();
		}
		
		/*(non-Javadoc)
		 * @see @see stoppableThread.AbstractStoppableThread#runExceptionManager(java.lang.Exception)
		 */
		@Override
		protected void runExceptionManager(Exception e)
		{
			if( !( e instanceof InterruptedException ) )
			{
				super.runExceptionManager(e);
			}
		}
		
		public void mutePlayer( double time )
		{
			synchronized ( this )
			{
				if( time > 0 )
				{
					this.muteTime = time;
					
					if( !this.isMute )
					{
						super.notify();
					}
					else
					{
						super.interrupt();
					}
				}
			}
		}
	}

}
