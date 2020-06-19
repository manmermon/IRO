package GUI.game.screen;

public interface IPausable 
{
	/**
	 * 
	 * @param pause
	 */
	public void setPause( boolean pause );
	
	/**
	 * 
	 * @return
	 */
	public boolean isPaused( );
}
