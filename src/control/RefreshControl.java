/* 
 * Copyright 2019 by Manuel Merino Monge <manmermon@dte.us.es>
 *  
 *   This file is part of IRO.
 *
 *   IRO is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   LSLRec is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with LSLRec.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   Project's URL: https://github.com/manmermon/IRO
 */

package control;

import stoppableThread.AbstractStoppableThread;

public class RefreshControl extends AbstractStoppableThread
{
	private static RefreshControl drawCtr = null;
	
	private double FPS = 30;
	private long waitTime = 30;
	private double delay = 0;
	
	private IScreenControl screenCtrl;
	
	//private InputAction inAction;
	
	private RefreshControl()
	{
		super.setName( this.getClass().getSimpleName() );
		
		this.FPS = 30;
		this.waitTime = (long)( 1000 / this.FPS );		
	}
	
	public double getFPS()
	{
		return this.FPS;
	}
	
	public static RefreshControl getInstance()
	{
		if( drawCtr == null )
		{
			drawCtr = new RefreshControl();
		}
		
		return drawCtr;
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
	protected void startUp() throws Exception 
	{
		this.waitTime = (long)( 1000 / this.FPS );
		if( this.waitTime < 2 )
		{
			this.waitTime = 2;
		}
		
		this.delay = System.nanoTime();
	}
	
	@Override
	protected void preStart() throws Exception 
	{
		this.screenCtrl = ScreenControl.getInstance();
	}
	
	@Override
	protected void runInLoop() throws Exception 
	{			
		synchronized( this.screenCtrl )
		{
			//this.screenCtrl.updateScene( this.inAction );
			//this.inAction = null;
			
			//System.out.println("RefreshControl.runInLoop()");
			this.screenCtrl.updateScreen();
		}
	}
		
	@Override
	protected void targetDone() throws Exception 
	{
		synchronized ( this ) 
		{
			this.delay = (System.nanoTime() - this.delay)  / 1e6; 
			long t = (long)( this.waitTime - this.delay );
			
			if( t > 2 )
			{
				super.wait( t );
			}
			
			this.delay = System.nanoTime();
		}
	}	
}
