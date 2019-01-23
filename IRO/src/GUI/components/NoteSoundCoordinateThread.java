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

package GUI.components;

import java.util.ArrayList;
import java.util.List;

import org.jfugue.player.Player;

import stoppableThread.AbstractStoppableThread;


public class NoteSoundCoordinateThread extends AbstractStoppableThread 
{
	private List< NoteSoundPlayerThread > players;
		
	public NoteSoundCoordinateThread( ) 
	{
		super.setName( this.getClass().getSimpleName() );
		
		new Player();
		
		this.players = new ArrayList< NoteSoundPlayerThread >();
	}
	
	public void addPattern( String pat ) 
	{
		this.players.add( new NoteSoundPlayerThread( pat ) );
	}
	
	/*
	 * (non-Javadoc)
	 * @see StoppableThread.AbstractStoppableThread#preStopThread()
	 */
	@Override
	protected synchronized void preStopThread(int friendliness) 
	{		
		//Tone is off.
	}
	
	/*
	 * (non-Javadoc)
	 * @see StoppableThread.AbstractStoppableThread#postStopThread()
	 */
	@Override
	protected void postStopThread(int friendliness) 
	{
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see StoppableThread.AbstractStoppableThread#runInLoop()
	 */
	@Override
	protected synchronized void runInLoop() throws Exception 
	{	
		//Tone is on. 
		for( NoteSoundPlayerThread player : this.players )
		{
			player.startThread();
		}

		this.players.clear();
	}
	
	@Override
	protected void targetDone() throws Exception 
	{
		synchronized( this )
		{
			super.wait();
		}
	}
	
	@Override
	protected void runExceptionManager(Exception e) 
	{
		if( !( e instanceof InterruptedException ) )
		{
			super.runExceptionManager( e );
		}
	}
	
}
