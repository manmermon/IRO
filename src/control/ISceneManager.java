package control;

import control.scenes.ISceneControl;

public interface ISceneManager 
{
	public void setSceneControl( ISceneControl sceneCtr ) throws NullPointerException;
}
