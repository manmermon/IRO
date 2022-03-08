package control.scenes;

import gui.game.screen.IScene;
import control.events.InputActionEvent;
import control.events.SceneEventListener;
import exceptions.SceneException;

public interface ISceneControl
{
	public final int SCENE_STATE_END = -1;
	public final int SCENE_STATE_NEW = 0;
	public final int SCENE_STATE_RUNNING = 1;
	public final int SCENE_STATE_PAUSE = 2;
	
	/**
	 * 
	 * @return
	 */
	public boolean activeInputController();
	
	/**
	 * 
	 * @param act
	 * @throws SceneException
	 */
	public void updateScene( InputActionEvent act ) throws SceneException;
	
	/**
	 * 
	 * @param scene
	 * @throws SceneException
	 */
	public void setScene( IScene scene ) throws SceneException;
	
	/**
	 * 
	 * @return
	 */
	public IScene getScene();
	
	/**
	 * @param pause
	 */
	public void setPauseScene( boolean pause );
	
	/**
	 * 
	 * @return
	 */
	public boolean isPausedScene();	
	
	/**
	 * 
	 * @throws SceneException
	 */
	public void startScene() throws Exception;
	
	/**
	 * 
	 * @throws Exception
	 */
	public void destroyScene() throws Exception;
	
	/**
	 * 
	 * @return
	 */
	public int getSceneState();
		
	/**
	 * 
	 * @param listener
	 */
	public void addSceneEventListener( SceneEventListener listener );
	
	/**
	 * 
	 * @param listener
	 */
	public void removeSceneEventListener( SceneEventListener listener );
	
	/**
	 * 
	 * @param reduce
	 */
	public void changeSceneSpeed( boolean reduce );
}
