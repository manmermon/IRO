package control.scenes;

import GUI.screens.IScene;
import control.events.SceneEventListener;
import control.inputs.IInputAction;
import exceptions.IllegalLevelStateException;
import exceptions.SceneException;

public interface ISceneControl
{
	public void updateScene( IInputAction act ) throws SceneException;
	
	public void setScene( IScene scene ) throws SceneException;
	
	public void destroyScene() throws Exception;
		
	public void addSceneEventListener( SceneEventListener listener );
	
	public void removeSceneEventListener( SceneEventListener listener );
}
