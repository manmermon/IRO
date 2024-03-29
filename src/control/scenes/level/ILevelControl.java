package control.scenes.level;

import gui.game.screen.level.Level;
import control.scenes.ISceneControl;
import exceptions.IllegalLevelStateException;

public interface ILevelControl extends ISceneControl 
{
	public void setLevel( Level level ) throws IllegalLevelStateException;
}
