package GUI.game.screen.level;

import javax.swing.event.EventListenerList;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import control.events.BackgroundMusicEvent;
import control.events.BackgroundMusicEventListener;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppableThread;

public class BackgroundMusic extends AbstractStoppableThread
{
	public static final double NON_DELAY = 0.0;
	
	private Player player;
	
	private Pattern pattern;
	private double delay;
	
	private EventListenerList listenerList;
	
	public BackgroundMusic() 
	{
		this.player = new Player();
		
		pattern = new Pattern();
		delay = NON_DELAY;
		
		this.listenerList = new EventListenerList();
	}
		
	public void setPattern(Pattern pattern) 
	{
		this.pattern = pattern;
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
				this.player.getManagedPlayer().finish();
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
			
			this.player.play( this.pattern );
			
			this.fireSceneEvent( BackgroundMusicEvent.END );
			
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
}
