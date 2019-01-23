package control;

import GUI.screens.IScene;
import GUI.screens.levels.Level;
import exceptions.IllegalLevelStateException;
import stoppableThread.AbstractStoppableThread;

public class LaunchControl extends AbstractStoppableThread 
{
	private static LaunchControl launchCtr = null;
	
	private LaunchControl() 
	{
		super.setName( this.getClass().getSimpleName() );
	}
	
	public static LaunchControl getInstance()
	{
		if( launchCtr == null )
		{
			launchCtr = new LaunchControl();
		}
		
		return launchCtr;
	}
	
	@Override
	protected void preStopThread(int friendliness) throws Exception 
	{	
	}

	@Override
	protected void postStopThread(int friendliness) throws Exception 
	{	
	}

	@Override
	protected void runInLoop() throws Exception 
	{	
	}
	
	public void launchScene( IScene scene ) throws IllegalLevelStateException, Exception
	{	
		if( scene instanceof Level )
		{
			LevelControl lvCtr = new LevelControl();				
			lvCtr.setLevel( (Level)scene );
			
			ScreenControl.getInstance().setSceneControl( lvCtr );			
		}
	}
}
