/*
 * Copyright 2011-2018 by Manuel Merino Monge <manmermon@dte.us.es>
 *  
 *   This file is part of CLIS.
 *
 *   CLIS is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   CLIS is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with CLIS.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */

package gui;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import stoppableThread.AbstractStoppableThread;


public class mouseTracking extends AbstractStoppableThread
{
	private Component comp = null;
	
	public mouseTracking( Component c ) 
	{
		if( c == null )
		{
			throw new IllegalArgumentException( "Component null" );
		}
		
		this.comp = c;
		
		super.setName( this.getClass().getSimpleName() );
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
		Thread.sleep( 100L );
		
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen( p, this.comp );
		
		MouseMotionListener[] mouseListeners = this.comp.getMouseMotionListeners();
		
		MouseEvent mouseEvent = new MouseEvent( this.comp, MouseEvent.MOUSE_MOVED, 
												0, 0, (int)p.getX(), (int)p.getY(),
												0, false ); 
		if( mouseListeners != null )
		{
			for( int i = 0; i < mouseListeners.length; i++ )
			{
				mouseListeners[ i ].mouseMoved( mouseEvent );
			}
		}
	}
	
	@Override
	protected void runExceptionManager(Exception e) 
	{
	}
}
