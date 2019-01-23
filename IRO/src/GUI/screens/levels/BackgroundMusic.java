package GUI.screens.levels;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppableThread;

public class BackgroundMusic extends AbstractStoppableThread
{
	public static final double NON_DELAY = 0.0;
	
	private Player player;
	
	private Pattern pattern;
	private double delay;
	
	public BackgroundMusic() 
	{
		this.player = new Player();
		
		pattern = new Pattern();
		delay = NON_DELAY;
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
			this.player.getManagedPlayer().finish();
		}
	}

	@Override
	protected void runInLoop() throws Exception 
	{	
		synchronized( this )
		{
			this.wait( (long)( 1000L * this.delay ) );
			
			this.player.play( this.pattern );
			
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
}
