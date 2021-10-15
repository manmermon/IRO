package control.scenes.menu;

import gui.game.screen.IScene;
import gui.game.screen.menu.IGameMenu;
import control.events.InputActionEvent;
import control.scenes.AbstractSceneControl;
import exceptions.SceneException;

public class MenuScreenControl extends AbstractSceneControl
{		
	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#specificCleanUp()
	 */
	@Override
	protected void specificCleanUp()
	{	
	}

	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#specificDestroyScene()
	 */
	@Override
	protected void specificDestroyScene()
	{		
	}

	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#updatedLoopAfterSetScene()
	 */
	@Override
	protected void updatedLoopAfterUpdateScene()
	{	
	}

	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#specificUpdateScene(control.inputs.IInputAction)
	 */
	@Override
	protected void specificUpdateScene( InputActionEvent act ) throws SceneException
	{	
	}

	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#setInputables(GUI.screens.IScene)
	 */
	@Override
	protected void setInputables( IScene scene )
	{
	}

	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#getSceneClass()
	 */
	@Override
	protected Class getSceneClass()
	{
		return IGameMenu.class;
	}

	/*(non-Javadoc)
	 * @see @see control.scenes.ISceneControl#activeInputController()
	 */
	@Override
	public boolean activeInputController()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void changeSceneSpeed(boolean reduce) 
	{	
	}
}
