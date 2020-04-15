package control.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import control.ScreenControl;
import control.events.InputActionEvent;

public class MouseStrokeAction implements MouseListener
											, MouseMotionListener
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
		int type = InputActionEvent.ACTION_DONE;
		
		if( e.getButton() == MouseEvent.BUTTON1 )
		{
			type = InputActionEvent.RECOVER_DONE;
		}
		
		final InputActionEvent ev = new InputActionEvent( this, type );
		
		ScreenControl.getInstance().InputAction( ev );
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) 
	{	
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{		
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{	
	}
}
