package control.inputs;

import control.ScreenControl;
import stoppableThread.AbstractStoppableThread;

public class InputControl extends AbstractStoppableThread implements IInputable
{
	private static InputControl inCtr;
	
	private IInputAction controllers;
	
	private InputControl() 
	{
		super.setName( this.getClass().getSimpleName() );
	}	
	
	public static InputControl getInstance()
	{
		if( inCtr == null )
		{
			inCtr = new InputControl();
		}
		
		return inCtr;
	}
	
	@Override
	protected void preStopThread(int friendliness) throws Exception 
	{	
	}

	@Override
	protected void postStopThread(int friendliness) throws Exception 
	{	
	}

	@Override
	protected void runInLoop() throws Exception 
	{	
		synchronized( this )
		{
			this.wait();
			
			ScreenControl.getInstance().action( this.controllers );
			this.controllers = null;
		}
	}

	@Override
	public void action( IInputAction act ) 
	{
		synchronized( this )
		{
			this.controllers = act;
			this.notify();
		}
	}

	
}
