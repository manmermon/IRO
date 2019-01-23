package control;

import GUI.screens.levels.Level;
import exceptions.IllegalLevelStateException;

public interface ILevelControl extends ISceneControl 
{
	public void setLevel( Level level ) throws IllegalLevelStateException;
}
