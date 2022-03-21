package lslStream.event;

import java.util.concurrent.ConcurrentLinkedQueue;

import stoppableThread.AbstractStoppableThread;

public abstract class InputLSLDataReader extends AbstractStoppableThread implements IInputLSLDataListener
{
	protected ConcurrentLinkedQueue< InputLSLDataEvent > events = new ConcurrentLinkedQueue< InputLSLDataEvent >();
	
	private Object lock = new Object();
	
	@Override
	public void InputLSLDataEvent( InputLSLDataEvent ev) 
	{
		synchronized( this.lock )
		{
			if( !super.stopThread )
			{
				this.events.add( ev );
				
				synchronized ( this )
				{
					super.notify();
				}
			}
		}
	}
	
	@Override
	protected void runInLoop() throws Exception 
	{
		synchronized ( this ) 
		{
			super.wait();
		}
		
		while( !this.events.isEmpty() )
		{
			this.readInputData( this.events.poll() );
		}
	}
	
	@Override
	protected void runExceptionManager(Exception e) 
	{
		if( !( e instanceof InterruptedException ) )
		{
			super.runExceptionManager(e);
		}
	}
	
	@Override
	protected void cleanUp() throws Exception 
	{
		super.cleanUp();
		
		synchronized ( this.lock )
		{
			this.events.clear();
		}
	}
	
	protected abstract void readInputData( InputLSLDataEvent ev ) throws Exception;
}
