package control.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeystrokeAction implements IInputAction, KeyListener 
{
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
}
