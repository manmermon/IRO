package control;

import GUI.screens.IScene;
import GUI.screens.levels.Level;
import GUI.screens.menus.MainMenuScreen;
import control.scenes.level.LevelControl;
import control.scenes.menu.MenuScreenControl;
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
			lvCtr.setScene( scene );
			
			ScreenControl.getInstance().setSceneControl( lvCtr );			
		}
		else if( scene instanceof MainMenuScreen )
		{
			MenuScreenControl menuCtr = new MenuScreenControl();
			menuCtr.setScene( scene );
			
			ScreenControl.getInstance().setSceneControl( menuCtr );
		}
			
	}
}
