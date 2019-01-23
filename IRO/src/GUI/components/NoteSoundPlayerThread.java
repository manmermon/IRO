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

import org.jfugue.player.Player;

import stoppableThread.AbstractStoppableThread;

public class NoteSoundPlayerThread extends AbstractStoppableThread 
{
	private Player player;
	private String pattern;
	
	public NoteSoundPlayerThread( String pattern ) 
	{
		this.player = new Player();
		this.pattern = pattern;
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
		this.player.play( this.pattern );		
	}
	
	@Override
	protected void targetDone() throws Exception 
	{
		super.targetDone();
		
		super.stopThread = true;
	}
}
