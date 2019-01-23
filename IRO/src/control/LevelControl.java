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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.event.EventListenerList;

import org.jfugue.midi.MidiDefaults;
import org.jfugue.theory.Note;

import GUI.GuiManager;
import GUI.components.Background;
import GUI.components.Frame;
import GUI.components.Fret;
import GUI.components.ISprite;
import GUI.components.NoteSprite;
import GUI.screens.IScene;
import GUI.screens.levels.Level;
import control.events.SceneEvent;
import control.events.SceneEventListener;
import control.inputs.IInputAction;
import exceptions.IllegalLevelStateException;
import exceptions.LevelException;
import image.imagenPoligono2D;
import music.IROChord;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppableThread;

public class LevelControl extends AbstractStoppableThread implements ILevelControl
{
	private Level scene;
	private PlayerControl soundCtrl;
		
	private AtomicBoolean actionDone;
	
	private EventListenerList listenerList;

	private int BPM = MidiDefaults.DEFAULT_TEMPO_BEATS_PER_MINUTE;
	
	//private ArrayTreeMap< String, NoteSprite > selectedNotes;
	
	public LevelControl( ) 
	{	
		super.setName( this.getClass().getSimpleName() );
		
		this.actionDone = new AtomicBoolean( true );
		
		this.listenerList = new EventListenerList();
		
		//this.selectedNotes = new ArrayTreeMap< String, NoteSprite >();
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
	protected void preStart() throws Exception 
	{
		super.preStart();
		
		if( this.scene != null )
		{		
			this.soundCtrl = new PlayerControl( this.scene.getBackgroundPattern(), this.BPM );
		}
		else
		{
			throw new LevelException( "Level null" );
		}
	} 
	
	@Override
	protected void startUp() throws Exception 
	{
		super.startUp();
		
		this.fireSceneEvent( SceneEvent.START );
			
		this.soundCtrl.startThread();			
	}
	
	@Override
	protected void runInLoop() throws Exception 
	{	
		synchronized ( this ) 
		{
			super.wait();
						
			//System.out.println("\t\tLevelControl.runInLoop()");
						
			//*
			this.scene.updateLevel();
			this.setScene( this.scene.getScene() );
						
			List< ISprite > FRET = this.scene.getSprites( IScene.FRET_ID );
			List< ISprite > Notes = this.scene.getSprites( IScene.NOTE_ID );
			
			if( FRET != null 
					&& Notes != null
					&& !FRET.isEmpty() 
					&& !Notes.isEmpty() )
			{
				Fret fret = (Fret)FRET.get( 0 );
				
				//Map< String, String > pat = new HashMap< String, String >();
				
				List< IROChord > IROChords = new ArrayList< IROChord >();
								
				for( ISprite __Note : Notes )
				{
					NoteSprite note = (NoteSprite) __Note;
					
					if( fret.isNoteIntoFret( note ) )
					{	
						if( this.actionDone.get() 
								|| note.isGhost() 
								//|| true 
								)
						{
							if( !note.isSelected() )
							{
								note.setSelected( true );
								note.setColor( Color.GREEN );
								
								//this.selectedNotes.put( note.getTrackID(), note );
							}
						}
						else if( !note.isSelected() )
						{
							note.setColor( Color.BLUE );
						}
					}
					else
					{	
						if( !note.isSelected() )
						{
							note.setColor( Color.RED );
						}
						
						
						if( note.isSelected() && !note.isPlayed() )
						{
							/*
							if( !note.isGhost() )
							{
								System.out.print( "$--");
							}
							*/
							
							/*
							int v = 0;
							for( String track : this.selectedNotes.keySet() )
							{
								IRO subtrack = new IROTrack( "sub" + track );
								subtrack.setVoice( v );
								
								List< NoteSprite > NoteSprites = this.selectedNotes.get( track );
																
								for( NoteSprite noteSprite : NoteSprites )
								{
									String instr = noteSprite.getInstrument();
									subtrack.setInstrument( instr );
																		
									List< Note > notes = noteSprite.getNotes();
									subtrack.addNotes( noteSprite.getTime(), notes );
									subtrack.setTempo( noteSprite.getTempo() );
									
									noteSprite.setPlayed( true );
								}
								
								subtrackList.add( subtrack );
								
								v++;
							}
							
							this.selectedNotes.clear();
							*/
							
							note.setPlayed( true );
							
							IROChords.add( note.getNotes() );
							
						}
						
					}					
				}
								
				this.actionDone.set( false );
				
				if( !IROChords.isEmpty() )
				{											
					this.soundCtrl.playNotes( IROChords );		
				}
				
				/*
				if( Notes.isEmpty() )
				{		
					this.fireSceneEvent( SceneEvent.END );
				}
				*/
			}
			
			if( this.scene.getNumberOfSprites( IScene.NOTE_ID ) < 1 )
			{		
				this.fireSceneEvent( SceneEvent.END );
			}
			//*/
		}		
	}
	
	private String createNotePattern( double startSoundTime, Note note )
	{
		return "@" + startSoundTime + " " + note.toString() + " ";
	}
	
	@Override
	public void updateScene( IInputAction act ) throws LevelException
	{		
		this.actionDone.set( act != null );
				
		if( super.getState().equals( Thread.State.NEW ) )
		{
			try
			{
				this.startThread();
			} 
			catch (Exception e) 
			{
				throw new LevelException( e.getMessage(), e.fillInStackTrace() );
			}
		}
		else
		{
			synchronized( this )
			{
				this.notify();
			}
		}
	}

	@Override
	public void setLevel( Level level) throws IllegalLevelStateException 
	{		
		this.scene = level;
		this.BPM = level.getBPM();
		this.setScene(  this.scene.getScene() );
	}
	
	private void setScene( Frame fr )
	{
		fr.setSceneControl( this );
		
		GuiManager.getInstance().setFrame( fr );
	}

	@Override
	public synchronized void addSceneEventListener( SceneEventListener listener ) 
	{
		this.listenerList.add( SceneEventListener.class, listener );
	}

	@Override
	public synchronized void removeSceneEventListener( SceneEventListener listener ) 
	{
		this.listenerList.remove( SceneEventListener.class, listener );		
	}
	
	private synchronized void fireSceneEvent( int typeEvent )
	{
		SceneEvent event = new SceneEvent( this, typeEvent );

		SceneEventListener[] listeners = this.listenerList.getListeners( SceneEventListener.class );

		for (int i = 0; i < listeners.length; i++ ) 
		{
			listeners[ i ].SceneEvent( event );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see control.ISceneControl#destroyScene()
	 */
	@Override
	public void destroyScene() throws Exception
	{
		synchronized( this )
		{
			this.soundCtrl.stopThread( IStoppableThread.FORCE_STOP );
			super.stopThread( IStoppableThread.FORCE_STOP );
		}
	}
	
	@Override
	protected void cleanUp() throws Exception 
	{
		super.cleanUp();

		if( this.soundCtrl != null )
		{
			this.soundCtrl.stopThread( IStoppableThread.FORCE_STOP );
		}
		this.soundCtrl =  null;
		
		this.scene.removeAllSprites();
		Background fin = new Background( this.scene.getSize(), "FIN" ) 
		{				
			@Override
			public BufferedImage getSprite() 
			{
				Font f = new Font( Font.SERIF, Font.BOLD, 48 );
				FontMetrics fm = (new JButton()).getFontMetrics( f );
				return (BufferedImage)imagenPoligono2D.crearImagenTexto( 0, 0, "Fin", fm, Color.BLACK, Color.MAGENTA, null );
			}
			
			@Override
			public Point2D.Double getScreenLocation() 
			{
				Point2D.Double loc = new Point2D.Double();
				loc.x = super.screenLoc.x + super.spriteSize.width / 2;
				loc.y = super.screenLoc.y + super.spriteSize.height / 2;
				
				return loc;
			}
		};
		
		this.scene.add( fin, IScene.PLANE_BRACKGROUND );
		
		this.scene.updateLevel();
		this.setScene( this.scene.getScene() );
		this.scene = null;
		
		/*
		this.selectedNotes.clear();
		this.selectedNotes = null;
		*/
		
		this.listenerList = null;
		
		this.actionDone = null;
	}
}
