/*
 * Work uses part of CLIS <https://github.com/manmermon/CLIS> by Manuel Merino Monge 
 * 
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
package stoppableThread;

public interface IStoppable 
{
	public final int STOP_WITH_TASKDONE = -1;
	public final int STOP_IN_NEXT_LOOP = 0;
	public final int FORCE_STOP = 1;
	
	/**
     * Stop thread execution. 
     * 
     * @param friendliness:
     * - if friendliness < 0: stop execution when task is done.
     * - if friendliness = 0: stop execution before the next loop interaction.
     * - if friendliness > 0: interrupt immediately task and then execution is stopped.     
     */
    public void stopActing( int friendliness );
    
    /**
     * Start execution.
     */
    public void startActing() throws Exception;    
}
