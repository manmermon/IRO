package control;

import gui.game.screen.IScene;

public interface ISceneManager 
{
	public void setScene( IScene scene ) throws Exception;
	
	public void startScene( ) throws Exception;
	
	public void stopScene() throws Exception;	
}
