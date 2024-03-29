package control.scenes;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.event.EventListenerList;

import gui.GameManager;
import gui.game.screen.IPausable;
import gui.game.screen.IScene;
import control.events.InputActionEvent;
import control.events.SceneEvent;
import control.events.SceneEventListener;
import control.inputStream.controller.IInputable;
import exceptions.SceneException;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppable;

/**
 * @author manuel merino monge
 *
 */
public abstract class AbstractSceneControl extends AbstractStoppableThread 
										implements ISceneControl
{
	protected IScene scene;
	
	private EventListenerList listenerList;
	
	private Class sceneClass = null;
	
	private AtomicInteger sceneState = new AtomicInteger( ISceneControl.SCENE_STATE_NEW );
	
	/**
	 * @throws SceneException 
	 * 
	 */
	public AbstractSceneControl( ) 
	{	
		super.setName( this.getClass().getSimpleName() );
				
		this.listenerList = new EventListenerList();
		
		Class sclass = this.getSceneClass();
		
		if( sclass == null )
		{
			sclass = IScene.class;
		}
		else if( !IScene.class.isAssignableFrom( sclass ) ) 
		{
			throw new ClassCastException( "Class scene incorrect." );
		}
		
		this.sceneClass = sclass;
	}
	
	/*(non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#preStart()
	 */
	@Override
	protected void preStart() throws Exception
	{
		synchronized ( this.sceneState )
		{
			this.sceneState.set( ISceneControl.SCENE_STATE_RUNNING );
		}

		super.preStart();
	}
	
	/*
	 * (non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#preStopThread(int)
	 */
	@Override
	protected void preStopThread(int friendliness) throws Exception 
	{	
	}

	/*
	 * (non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#postStopThread(int)
	 */
	@Override
	protected void postStopThread(int friendliness) throws Exception 
	{	
	}
	
	/*
	 * (non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#startUp()
	 */
	@Override
	protected void startUp() throws Exception 
	{
		super.startUp();				
	}
	
	/*
	 * (non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#runInLoop()
	 */
	@Override
	protected void runInLoop() throws Exception 
	{	
		synchronized ( this ) 
		{
			super.wait();
			
			this.scene.updateLevel();
			
			BufferedImage scene = this.scene.getScene();
			
			this.setFrame( scene );
			
			this.updatedLoopAfterUpdateScene();
		}		
	}
	
	/*
	 * (non-Javadoc)
	 * @see @see control.scenes.ISceneControl#setScene(GUI.screens.IScene)
	 */
	@Override
	public void setScene(IScene scene) throws SceneException
	{	
		if( scene == null )
		{
			throw new SceneException( "Scene null" );
		}
		
		//if( !this.sceneClass.equals( scene.getClass() ) )
		if( !IScene.class.isAssignableFrom( this.sceneClass ) ) 
		{
			throw new SceneException( "Scene class incorrect. Scene expected " + this.sceneClass );
		}
		
		this.scene = scene;
		
		this.setFrame( this.scene.getScene() );

		this.setInputables( this.scene );		
	}
	
	@Override
	public IScene getScene() 
	{
		return this.scene;
	}
	
	/*
	 * (non-Javadoc)
	 * @see @see control.scenes.ISceneControl#updateScene(control.inputs.IInputAction)
	 */
	@Override
	public synchronized void updateScene( List< InputActionEvent > act ) throws SceneException
	{	
		this.specificUpdateScene( act );
		
		if( this.scene instanceof IInputable )
		{
			((IInputable)this.scene).action( act );
		}
		
		if( super.getState().equals( Thread.State.NEW ) )
		{
			try
			{
				this.startActing();
			} 
			catch (Exception e) 
			{
				throw new SceneException( e.getMessage(), e.fillInStackTrace() );
			}
		}
		else
		{
			synchronized( this )
			{
				this.notify();
			}
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
			super.runExceptionManager(e);
		}
	}
	
	/**
	 * 
	 * @param fr
	 */
	/*
	protected void setFrame( Frame fr )
	{
		//fr.setSceneControl( this );
		
		fr.setRequestFocusEnabled( true );
		
		GameManager.getInstance().setGameFrame( fr );
		
		fr.requestFocusInWindow();
	}
	*/
	protected void setFrame( BufferedImage frame )
	{
		GameManager.getInstance().setGameFrame( frame );		
	}

	/*
	 * (non-Javadoc)
	 * @see @see control.scenes.ISceneControl#addSceneEventListener(control.events.SceneEventListener)
	 */
	@Override
	public synchronized void addSceneEventListener( SceneEventListener listener ) 
	{
		this.listenerList.add( SceneEventListener.class, listener );
	}

	/*
	 * (non-Javadoc)
	 * @see @see control.scenes.ISceneControl#removeSceneEventListener(control.events.SceneEventListener)
	 */
	@Override
	public synchronized void removeSceneEventListener( SceneEventListener listener ) 
	{
		this.listenerList.remove( SceneEventListener.class, listener );		
	}
	
	/**
	 * 
	 * @param typeEvent
	 */
	protected synchronized void fireSceneEvent( int typeEvent )
	{
		SceneEvent event = new SceneEvent( this, typeEvent );

		SceneEventListener[] listeners = this.listenerList.getListeners( SceneEventListener.class );

		for (int i = 0; i < listeners.length; i++ ) 
		{
			listeners[ i ].SceneEvent( event );
		}
	}
	
	/*(non-Javadoc)
	 * @see @see control.scenes.ISceneControl#startScene()
	 */
	@Override
	public void startScene() throws Exception
	{
		super.startActing();
	}
	
	/*
	 * (non-Javadoc)
	 * @see control.ISceneControl#destroyScene()
	 */
	@Override
	public void destroyScene() throws Exception
	{
		synchronized( this )
		{
			this.specificDestroyScene();
			super.stopActing( IStoppable.FORCE_STOP );			
		}
	}
	
	/*(non-Javadoc)
	 * @see @see control.scenes.ISceneControl#getSceneState()
	 */
	@Override
	public int getSceneState()
	{
		synchronized ( this.sceneState )
		{
			return this.sceneState.get();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#cleanUp()
	 */
	@Override
	protected void cleanUp() throws Exception 
	{
		synchronized ( this.sceneState )
		{
			this.sceneState.set( ISceneControl.SCENE_STATE_END );
		}
		
		super.cleanUp();

		this.specificCleanUp();
		
		/*
		this.scene.removeAllSprites();
		Background fin = new Background( this.scene.getSize(), "FIN" ) 
		{				
			@Override
			public BufferedImage getSprite() 
			{
				Font f = new Font( Font.SERIF, Font.BOLD, 48 );
				FontMetrics fm = (new JButton()).getFontMetrics( f );
				return (BufferedImage)basicPainter2D.text( 0, 0, "Fin", fm, Color.BLACK, Color.MAGENTA, null );
			}
			
			@Override
			public Point2D.Double getScreenLocation() 
			{
				Point2D.Double loc = new Point2D.Double();
				loc.x = super.screenLoc.x + super.spriteSize.width / 2;
				loc.y = super.screenLoc.y + super.spriteSize.height / 2;
				
				return loc;
			}
		};
		
		this.scene.add( fin, IScene.PLANE_BRACKGROUND );
		
		this.scene.updateLevel();
		this.setFrame( this.scene.getScene() );
		this.scene = null;
		
		this.listenerList = null;		
		//*/
	}

	/**
	 * 
	 * @return
	 */
	protected abstract Class getSceneClass();
	
	/**
	 * 
	 */
	protected abstract void specificCleanUp();
	
	/**
	 * 
	 */
	protected abstract void specificDestroyScene();
	
	/**
	 * 
	 */
	protected abstract void updatedLoopAfterUpdateScene();
	
	/**
	 * 
	 * @param act
	 * @throws SceneException
	 */
	protected abstract  void specificUpdateScene( List< InputActionEvent > act ) throws SceneException;
	
	/**
	 * 
	 */
	protected abstract void setInputables( IScene scene );

	@Override
	public void setPauseScene( boolean pause ) 
	{
		if( this.scene != null )
		{
			if( this.scene instanceof IPausable )
			{
				((IPausable)this.scene).setPause( pause );
			}
		}
	}
	
	@Override
	public boolean isPausedScene() 
	{
		boolean pause = false;
		
		if( this.scene != null )
		{
			if( this.scene instanceof IPausable )
			{
				pause = ((IPausable)this.scene).isPaused();
			}
		}
				
		return pause;
	}
	
}
