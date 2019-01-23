package control;

import control.events.SceneEventListener;
import control.inputs.IInputAction;
import control.inputs.IInputable;
import stoppableThread.AbstractStoppableThread;

public class ScreenControl extends AbstractStoppableThread 
							implements IScreenControl, IInputable
										, ISceneManager, SceneEventListener
{
	private static ScreenControl screenCtrl;
	
	private ISceneControl sceneCtrl;
	
	private IInputAction inAction = null;
	
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
			
			//System.out.println("\tScreenControl.runInLoop()");
			
			if( this.sceneCtrl != null )
			{
				this.sceneCtrl.updateScene( this.inAction );
			}
			
			this.inAction = null;
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
	
	public void action( IInputAction act )
	{
		synchronized( this )
		{
			this.inAction = act;
		}
	}
		
	public void setSceneControl( ISceneControl sceneCtr ) throws NullPointerException
	{
		synchronized( this )
		{
			if( sceneCtr == null )
			{
				throw new NullPointerException( "Input scene control null." );
			}
			
			this.sceneCtrl = sceneCtr;
			this.sceneCtrl.addSceneEventListener( this );
		}
	}

	@Override
	public void SceneEvent( control.events.SceneEvent ev) 
	{		
		if( ev.getType() == control.events.SceneEvent.END )
		{
			synchronized ( this )
			{
				if( this.sceneCtrl != null )
				{
					try 
					{
						this.sceneCtrl.destroyScene();
						this.sceneCtrl = null;
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}				
				}
			}
		}
	}
}
