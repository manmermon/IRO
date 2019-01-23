package control;

import control.events.SceneEventListener;
import control.inputs.IInputAction;
import exceptions.LevelException;

public interface ISceneControl
{
	public void updateScene( IInputAction act ) throws LevelException;
	
	public void destroyScene() throws Exception;
		
	public void addSceneEventListener( SceneEventListener listener );
	
	public void removeSceneEventListener( SceneEventListener listener );
}
