package control;

import GUI.game.screen.IScene;
import GUI.game.screen.level.Level;
import GUI.game.screen.menu.IGameMenu;
import control.scenes.ISceneControl;
import control.scenes.level.LevelControl;
import control.scenes.menu.MenuScreenControl;

public class SceneControlManager  
{
	private static SceneControlManager manager = null;
	
	/**
	 * 
	 */
	private SceneControlManager() 
	{
	}
	
	/**
	 * 
	 * @return SceneControlManager
	 */
	public static SceneControlManager getInstance()
	{
		if( manager == null )
		{
			manager = new SceneControlManager();
		}
		
		return manager;
	}
		
	/**
	 * 
	 * @param scene
	 * @return
	 * @throws Exception
	 */
	public ISceneControl setSceneControl( IScene scene ) throws Exception
	{
		ISceneControl ctr = null;
		if( scene instanceof Level )
		{
			ctr = new LevelControl();				
			ctr.setScene( scene );
		}
		else if( scene instanceof IGameMenu )
		{
			ctr = new MenuScreenControl();
			ctr.setScene( scene );
		}
		
		return ctr;			
	}
}
