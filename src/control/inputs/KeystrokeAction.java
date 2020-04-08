package control.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventObject;
import java.util.concurrent.ConcurrentLinkedQueue;

public class KeystrokeAction implements IInputAction, KeyListener 
{
	private ConcurrentLinkedQueue< KeyEvent > eventList = new ConcurrentLinkedQueue< KeyEvent >();
	
	public KeystrokeAction( ) 
	{
	}
	
	@Override
	public void keyPressed(KeyEvent e) 
	{	
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		InputControl.getInstance().action( this );
	}

	@Override
	public void keyTyped(KeyEvent e) 
	{	
	}

	@Override
	public EventObject getInputEvent()
	{
		return this.eventList.poll();
	}	
}
