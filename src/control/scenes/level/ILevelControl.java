package control.scenes.level;

import GUI.screens.levels.Level;
import control.scenes.ISceneControl;
import exceptions.IllegalLevelStateException;

public interface ILevelControl extends ISceneControl 
{
	public void setLevel( Level level ) throws IllegalLevelStateException;
}
