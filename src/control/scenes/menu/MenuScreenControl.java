package control.scenes.menu;

import GUI.game.screen.IScene;
import GUI.game.screen.menu.MainMenuScreen;
import control.controller.KeystrokeAction;
import control.controller.MouseStrokeAction;
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
	protected void updatedLoopAfterSetScene()
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
		MouseStrokeAction mouse = new MouseStrokeAction();
		
		scene.getScene().addMouseListener( mouse );		
		scene.getScene().addMouseMotionListener( mouse );
		
		KeystrokeAction keyboard = new KeystrokeAction();
		
		scene.getScene().addKeyListener( keyboard );	
	}

	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#getSceneClass()
	 */
	@Override
	protected Class getSceneClass()
	{
		return MainMenuScreen.class;
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

}
