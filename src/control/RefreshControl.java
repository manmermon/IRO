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
		
		/*
		this.scene = new Level( this.canvas.getSize() );
		Background back = new Background( this.scene.getSize() );
		back.setZIndex( -1 );
		this.scene.addBackgroud( back );
		
		Pentragram pen = new Pentragram( this.scene.getSize() );
		pen.setZIndex( 0 );
		this.scene.addPentagram( pen );
		
		double vel = 200;
		
		*/
		
		/*
		for( int i = 0; i < 16; i++ )
		{
			int pos = (int)( pen.getPentragramWidth() - ( (i % 5 )  * 80 ));
			Note note = new Note( ( i % 7 )
								, pen
								,  pos
								, vel );
			note.setZIndex( 1 );
			this.scene.addNote( note );
		}
		*/
		/*
		String[] nots = new String[] { "C", "R", "R", "D", "R","R",  "E","R", "R", "R", "C", "R", "R", "R"
				, "C", "R", "R", "D", "R","R",  "E","R", "R", "R", "C", "R", "R", "R"
				, "E", "R", "R", "F", "R","R",  "G","R", "R", "R", "R"
				, "E", "R", "R", "F", "R","R",  "G","R", "R"
				, "G", "R", "R", "A", "R","R",  "G","R", "R", "R", "F", "R", "R", "E", "R", "R", "C","R", "R", "R"
				, "G", "R", "R", "A", "R","R",  "G","R", "R", "R", "F", "R", "R", "E", "R", "R", "C","R", "R", "R"
				, "D", "R", "R", "G", "R","R",  "C","R", "R"
				, "D", "R", "R", "G", "R","R",  "C","R", "R" 
		};
		*/
		
		/*
		String[] nots = new String[] { "C4", "R", "C#4", "R","C#4", "R","C#4", "R","D4", "D4","D4", "D#4","D#4","D#4","D#4","D#4","D#4","E","D",  "C","G", "F", "E", "D", "C", "G", "R"
				 , "C", "R", "R", "D", "R","R",  "E","R", "R", "R", "C", "R", "R", "R"
				 , "R", "R", "R", "R", "R","R",  "R","R", "R", "R", "R"
				 , "E", "R", "R", "F", "R","R",  "G","R", "R"
				 , "E", "F", "G", "G", "F","E",  "D","C", "C", "D", "E", "E", "D", "D", "R", "R", "C","R", "R", "R"
				 , "G", "R", "R", "A", "R","R",  "G","R", "R", "R", "F", "R", "R", "E", "R", "R", "C","R", "R", "R"
				 , "D", "R", "R", "G", "R","R",  "C","R", "R"
				 , "D", "R", "R", "G", "R","R",  "C","R", "R" };
		*/
		
		/*
		int initPos = pen.getPentragramWidth();
		for( String c : nots )
		{
			boolean ghost = c.charAt( 0 ) == 'E';
			
			if( !c.equals( "R" ) )
			{
				Note note = new Note( c
									, pen
									,  initPos
									, vel
									, ghost );
				note.setZIndex( 1 );
				this.scene.addNote( note );
			}
			
			initPos += 45;
		}
		
		Fret fret = new Fret( pen );
		fret.setZIndex( 2 );
		Point loc = new Point();
		loc.x = this.scene.getSize().width / 2;
		loc.y = 0;
		fret.setScreenLocation( loc );
		this.scene.addFret( fret );
		
		this.sceneCtr = new LevelControl( this.scene, this.canvas );
		this.sceneCtr.startThread();
		
		this.canvas.setSceneControl( this.sceneCtr );
		*/
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
	
	/*
	public void action( InputAction act )
	{
		synchronized( this.screenCtrl )
		{
			this.inAction = act;
		}
	}
	*/
	
	/*
	public void putSceneControl( LevelControl sceneCtrl ) throws IllegalLevelStateException
	{
		synchronized( this.screenCtrl )
		{
			this.screenCtrl.setSceneControl( sceneCtrl );
		}
	}
	*/
}
