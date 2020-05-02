package control;

import javax.swing.JOptionPane;

import GUI.MainAppUI;
import GUI.GameManager;
import GUI.game.screen.IScene;
import config.ConfigApp;
import config.language.Language;
import control.events.InputActionEvent;
import control.events.InputActionListerner;
import control.events.SceneEventListener;
import control.scenes.ISceneControl;
import control.scenes.level.LevelControl;
import exceptions.SceneException;
import statistic.GameStatistic;
import statistic.GameStatistic.FieldType;
import stoppableThread.AbstractStoppableThread;

public class ScreenControl extends AbstractStoppableThread 
							implements IScreenControl
										//, IInputable
										, ISceneManager
										, SceneEventListener
										, InputActionListerner
{
	private static ScreenControl screenCtrl;
	
	private ISceneControl sceneCtrl;
	
	private InputActionEvent inAction = null;
	
	private ScreenControl( ) 
	{
		super.setName( this.getClass().getSimpleName() );
	}
	
	public static ScreenControl getInstance()
	{
		if( screenCtrl == null )
		{
			screenCtrl = new ScreenControl();
		}
		
		return screenCtrl;
	}
	
	@Override
	protected void preStopThread(int friendliness) throws Exception 
	{ }

	@Override
	protected void postStopThread(int friendliness) throws Exception 
	{ }

	@Override
	protected void runInLoop() throws Exception 
	{
		synchronized( this )
		{
			super.wait();
						
			if( this.sceneCtrl != null )
			{
				this.sceneCtrl.updateScene( this.inAction );
			}
			
			this.inAction = null;
		}
	}

	/*(non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#runExceptionManager(java.lang.Exception)
	 */
	@Override
	protected void runExceptionManager(Exception e)
	{
		if( !( e instanceof InterruptedException ) )
		{
			super.runExceptionManager( e ); 
		}
	}
	
	@Override
	public void updateScreen( ) 
	{
		synchronized( this )
		{			
			this.notify();
		}
	}
		
	/*
	 * (non-Javadoc)
	 * @see @see control.ISceneManager#startScened(GUI.game.screen.IScene)
	 */
	@Override
	public void setScene( IScene scene ) throws Exception	
	{
		synchronized( this )
		{				
			if( this.sceneCtrl != null )
			{
				this.sceneCtrl.destroyScene();
				this.sceneCtrl = null;
			}
			
			ISceneControl sceneCtr = SceneControlManager.getInstance().setSceneControl( scene );
						
			if( sceneCtr == null )
			{
				throw new SceneException( "Scene control null." );
			}
			
			this.sceneCtrl = sceneCtr;
			this.sceneCtrl.addSceneEventListener( this );
		}
	}
	
	/*(non-Javadoc)
	 * @see @see control.ISceneManager#startScene()
	 */
	@Override
	public void startScene() throws Exception
	{
		synchronized ( this )
		{
			if( this.sceneCtrl != null 
					&& this.sceneCtrl.getSceneState() == ISceneControl.SCENE_STATE_NEW )
			{
				this.sceneCtrl.startScene();
			}
		}
	}
	
	
	@Override
	public void SceneEvent( final control.events.SceneEvent ev) 
	{		
		Thread t = new Thread()
		{
			/*(non-Javadoc)
			 * @see @see stoppableThread.AbstractStoppableThread#run()
			 */
			@Override
			public synchronized void run()
			{			
				if( ev.getType() == control.events.SceneEvent.START)
				{
					GameStatistic.add( ConfigApp.getPlayers(), FieldType.GAME_START );
				}		
				else if( ev.getType() == control.events.SceneEvent.END )
				{
					if( !super.getState().equals( Thread.State.WAITING ) 
							|| !super.getState().equals( Thread.State.TIMED_WAITING ) )
					{
						super.interrupt(); 
					}

					synchronized ( this )
					{
						GameStatistic.add( ConfigApp.getPlayers(), FieldType.GAME_END );

						try
						{
							GameManager.getInstance().stopLevel( );
						}
						catch (Exception ex)
						{
							ex.printStackTrace();

							JOptionPane.showMessageDialog( MainAppUI.getInstance()
									, ex.getMessage()
									, Language.getLocalCaption( Language.ERROR )
									, JOptionPane.ERROR_MESSAGE );
						}
					}
				}
				else if( ev.getType() == control.events.SceneEvent.PAUSE )
				{
					GameStatistic.add( ConfigApp.getPlayers(), FieldType.GAME_PAUSE );
				}
				else if( ev.getType() == control.events.SceneEvent.RESUME )
				{
					GameStatistic.add( ConfigApp.getPlayers(), FieldType.GAME_RESUME );
				}
			}
		};
		
		t.setName( this.getClass().getSimpleName() + "-GameStatisticRegister" );
		t.start();
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see @see control.ISceneManager#stopScene()
	 */
	@Override
	public void stopScene() throws Exception
	{
		synchronized( this )
		{
			if( this.sceneCtrl != null )
			{
				this.sceneCtrl.destroyScene();
				this.sceneCtrl = null;
			}
		}
	}

	/*(non-Javadoc)
	 * @see @see control.events.InputActionListerner#InputAction(control.events.InputActionEvent)
	 */
	@Override
	public void InputAction(InputActionEvent ev)
	{
		if( ev != null )
		{
			this.inAction = ev;
		}
	}

	public void setUpdateLevelInputGoal( double percent )
	{
		synchronized ( this )
		{
			if( this.sceneCtrl != null && this.sceneCtrl instanceof LevelControl )
			{
				((LevelControl)this.sceneCtrl).updateInputGoal( percent );
			}
		}
	}
	
	public boolean activeInputControl()
	{
		boolean act = false;
		
		synchronized ( this )
		{
			if( this.sceneCtrl != null )
			{
				act = this.sceneCtrl.activeInputController();
			}
		}
		
		return act;
	}
}
