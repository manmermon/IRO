package GUI.game.screen.level;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.swing.event.EventListenerList;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.ManagedPlayerListener;

import JFugueMod.org.jfugue.player.PlayerMod;
import control.events.BackgroundMusicEvent;
import control.events.BackgroundMusicEventListener;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppableThread;

public class BackgroundMusic extends AbstractStoppableThread implements ManagedPlayerListener
{
	public static final double NON_DELAY = 0.0;
	
	private PlayerMod player;
	
	private Pattern pattern;
	private double delay;
	
	private EventListenerList listenerList;
	
	public BackgroundMusic() 
	{
		super.setName( this.getClass().getSimpleName() );
		this.player = new PlayerMod();
		this.player.getManagedPlayer().addManagedPlayerListener( this );
		
		pattern = new Pattern();
		delay = NON_DELAY;
		
		this.listenerList = new EventListenerList();
	}
		
	public void setPattern(Pattern pattern) throws MidiUnavailableException, InvalidMidiDataException 
	{
		this.pattern = pattern;
		this.player.load( pattern );
	}
	
	public Pattern getPattern() 
	{
		return pattern;
	}
	
	public void setDelay(double delay) 
	{
		this.delay = delay;
	}
	
	public double getDelay() 
	{
		return delay;
	}

	@Override
	protected void preStopThread(int friendliness) throws Exception 
	{	
	}

	@Override
	protected void postStopThread(int friendliness) throws Exception 
	{	
		if( friendliness == IStoppableThread.FORCE_STOP )
		{
			if( this.player != null )
			{
				try
				{
					this.player.getManagedPlayer().finish();
				}
				catch (Exception ex) 
				{
				}
			}
		}
	}

	@Override
	protected void runInLoop() throws Exception 
	{	
		synchronized( this )
		{
			if( this.delay > 0 )
			{
				this.wait( (long)( 1000L * this.delay ) );
			}
			
			this.fireSceneEvent( BackgroundMusicEvent.START );						
			this.player.play( );
			
			//this.fireSceneEvent( BackgroundMusicEvent.END );
			//*/
			
			this.wait();
		}
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
		this.fireSceneEvent( BackgroundMusicEvent.END );
	}
	
	@Override
	protected void cleanUp() throws Exception 
	{
		super.cleanUp();
		
		this.player.getManagedPlayer().finish();
		this.player = null;		
	}
	
	public void addBackgroundMusicEventListener( BackgroundMusicEventListener listener) 
	{
		this.listenerList.add( BackgroundMusicEventListener.class, listener );
	}
	
	public void removeBackgroundMusicEventListener( BackgroundMusicEventListener listener) 
	{
		this.listenerList.remove( BackgroundMusicEventListener.class, listener );
	}
	
	private synchronized void fireSceneEvent( int typeEvent )
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
		// TODO Auto-generated method stub
		
	}

	/*(non-Javadoc)
	 * @see @see org.jfugue.player.ManagedPlayerListener#onResumed()
	 */
	@Override
	public void onResumed()
	{
		// TODO Auto-generated method stub
		
	}

	/*(non-Javadoc)
	 * @see @see org.jfugue.player.ManagedPlayerListener#onSeek(long)
	 */
	@Override
	public void onSeek(long tick)
	{
		// TODO Auto-generated method stub
		
	}

	/*(non-Javadoc)
	 * @see @see org.jfugue.player.ManagedPlayerListener#onReset()
	 */
	@Override
	public void onReset()
	{
		// TODO Auto-generated method stub
		
	}
}
