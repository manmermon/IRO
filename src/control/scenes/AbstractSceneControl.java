package control.scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.event.EventListenerList;

import GUI.GuiManager;
import GUI.game.component.Background;
import GUI.game.component.Frame;
import GUI.game.screen.IScene;
import control.events.SceneEvent;
import control.events.SceneEventListener;
import control.inputs.IInputAction;
import control.inputs.IInputable;
import exceptions.SceneException;
import image.basicPainter2D;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppableThread;

/**
 * @author manuel
 *
 */
public abstract class AbstractSceneControl extends AbstractStoppableThread implements ISceneControl
{
	protected IScene scene;
	
	private EventListenerList listenerList;
	
	private Class sceneClass = null;
	
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
		
		this.fireSceneEvent( SceneEvent.START );				
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
												
			//*
			this.scene.updateLevel();
			this.setFrame( this.scene.getScene() );
			
			this.updatedLoopAfterSetScene();
			//*/
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
		
		if( !this.sceneClass.equals( scene.getClass() ) )
		{
			throw new SceneException( "Scene class incorrect. Scene expected " + this.sceneClass );
		}
		
		this.scene = scene;
		
		this.setFrame( this.scene.getScene() );

		this.setInputables( this.scene );		
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see @see control.scenes.ISceneControl#updateScene(control.inputs.IInputAction)
	 */
	@Override
	public void updateScene( IInputAction act ) throws SceneException
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
				this.startThread();
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
	
	/**
	 * 
	 * @param fr
	 */
	protected void setFrame( Frame fr )
	{
		fr.setSceneControl( this );
		
		fr.setRequestFocusEnabled( true );
		
		GuiManager.getInstance().setGameFrame( fr );
		
		fr.requestFocusInWindow();
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
			super.stopThread( IStoppableThread.FORCE_STOP );			
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#cleanUp()
	 */
	@Override
	protected void cleanUp() throws Exception 
	{
		super.cleanUp();

		this.specificCleanUp();
		
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
	protected abstract void updatedLoopAfterSetScene();
	
	/**
	 * 
	 * @param act
	 * @throws SceneException
	 */
	protected abstract  void specificUpdateScene( IInputAction act ) throws SceneException;
	
	/**
	 * 
	 */
	protected abstract void setInputables( IScene scene );
	
}
