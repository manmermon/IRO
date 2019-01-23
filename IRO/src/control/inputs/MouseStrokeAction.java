package control.inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseStrokeAction implements IInputAction, MouseListener, MouseMotionListener
{
	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) 
	{	
	}

	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) 
	{	
	}

	@Override
	public void mouseExited(java.awt.event.MouseEvent e) 
	{	
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) 
	{	
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) 
	{
		InputControl.getInstance().action( this );
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{		
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
		InputControl.getInstance().action( this );		
	}

}
