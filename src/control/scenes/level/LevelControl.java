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

package control.scenes.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import GUI.game.component.sprite.Fret;
import GUI.game.component.sprite.ISprite;
import GUI.game.component.sprite.InputGoal;
import GUI.game.component.sprite.MusicNoteGroup;
import GUI.game.component.sprite.Score;
import GUI.game.screen.IScene;
import GUI.game.screen.level.Level;
import control.events.BackgroundMusicEventListener;
import control.events.InputActionEvent;
import control.events.SceneEvent;
import control.music.MusicPlayerControl;
import control.scenes.AbstractSceneControl;
import exceptions.SceneException;
import control.controller.KeystrokeAction;
import control.controller.MouseStrokeAction;
import control.events.BackgroundMusicEvent;

public class LevelControl extends AbstractSceneControl 
							implements BackgroundMusicEventListener
										//, FretEventListener
{		
	private AtomicBoolean actionDone;
	private boolean backgroundMusicEnd = false;
	
	private boolean noteIntoFret = false;
		
	/**
	 * @throws SceneException 
	 * 
	 */
	public LevelControl( ) throws SceneException 
	{	
		super();
		
		this.actionDone = new AtomicBoolean( true );
	}
	
	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#setScene(GUI.game.screen.IScene)
	 */
	/*
	@Override
	public void setScene(IScene scene) throws SceneException
	{
		super.setScene( scene );
		
		Level lv = (Level)scene;
		
		lv.getFret().addFretEventListener( this );
	}
	//*/
	
	/*
	 * (non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#preStart()
	 */
	@Override
	protected void preStart() throws Exception 
	{
		super.preStart();
				
		if( super.scene != null )
		{	
			MusicPlayerControl.getInstance().setBackgroundMusicPatter( (((Level)this.scene).getBackgroundPattern() ) );
			MusicPlayerControl.getInstance().addBackgroundMusicEvent( this );
		}
		else
		{
			throw new SceneException( "Level null" );
		}
	} 
	
	/*
	 * (non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#startUp()
	 */
	@Override
	protected void startUp() throws Exception 
	{
		super.startUp();

		MusicPlayerControl.getInstance().startMusic();
	}

	/*
	 * (non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#updatedLoopAfterSetScene()
	 */
	@Override
	protected void updatedLoopAfterUpdateScene() 
	{		
		List< ISprite > FRET = this.scene.getSprites( IScene.FRET_ID, true );
		List< ISprite > Notes = this.scene.getSprites( IScene.NOTE_ID, true );

		if( FRET != null 
				&& Notes != null
				&& !FRET.isEmpty() 
				&& !Notes.isEmpty() )
		{
			Fret fret = (Fret)FRET.get( 0 );

			List< String > noteTracks = new ArrayList<String>();

			boolean noteInFret = false;
			
			Map< String, Double > nt = new HashMap<String, Double>();
			for( ISprite __Note : Notes )
			{
				MusicNoteGroup note = (MusicNoteGroup) __Note;

				if( fret.isNoteIntoFret( note ) )
				{	
					noteInFret = true;
					
					if( this.actionDone.get() 
							|| note.isGhost() 
							//|| true 
							)
					{
						if( !note.isSelected() )
						{
							note.setSelected( true );
							note.setState( GUI.game.component.sprite.MusicNoteGroup.State.ACTION );
							
							for( ISprite score : this.scene.getSprites( IScene.SCORE_ID, true ) )
							{
								((Score)score).incrementScore();
							}							
						}
					}
					else if( !note.isSelected() )
					{
						note.setState( GUI.game.component.sprite.MusicNoteGroup.State.WAITING_ACTION );
					}
				}
				else
				{						
					if( !note.isSelected() )
					{
						note.setState( GUI.game.component.sprite.MusicNoteGroup.State.PREACTION );
					}

					if( note.isSelected() && !note.isPlayed() )
					{
						// TODO
						/*
						note.setPlayed( true );
						
						nt.put( note.getTrackID(), note.getSheetTime() );
						noteTracks.add( note.getTrackID() );
						*/							
					}
				}					
			}
			
			this.noteIntoFret = noteInFret;

			this.actionDone.set( false );

			if( !noteTracks.isEmpty() )
			{	
				try
				{	
					MusicPlayerControl.getInstance().playTracks( noteTracks );
					double t = MusicPlayerControl.getInstance().getBackgroundMusicTime();
					for( String s : noteTracks )
					{
						System.out.println("LevelControl.updatedLoopAfterSetScene()  backMusicTime " + t + " - note time (" + s +"): " + nt.get( s ) );
					}
				} 
				catch (Exception ex)
				{
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}					
			}				
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#targetDone()
	 */
	@Override
	protected void targetDone() throws Exception 
	{
		synchronized( this )
		{
			if( this.scene.getNumberOfSprites( IScene.NOTE_ID ) < 1 
					&& this.backgroundMusicEnd )
			{		
				super.stopThread = true;
				super.fireSceneEvent( SceneEvent.END );
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#specificUpdateScene(control.inputs.IInputAction)
	 */
	@Override
	public void specificUpdateScene( InputActionEvent act ) throws SceneException
	{			
		this.actionDone.set( act == null ? false : act.getType() == InputActionEvent.ACTION_DONE );				
	}

	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#setInputables(GUI.screens.IScene)
	 */
	@Override
	protected void setInputables(IScene scene)
	{			
		MouseStrokeAction mouse = new MouseStrokeAction();
		
		scene.getScene().addMouseListener( mouse );		
		scene.getScene().addMouseMotionListener( mouse );
		
		KeystrokeAction keyboard = new KeystrokeAction();
		
		scene.getScene().addKeyListener( keyboard );		
	}
		
	/*
	 * (non-Javadoc)
	 * @see control.ISceneControl#destroyScene()
	 */
	@Override
	public void specificDestroyScene()
	{
		try
		{
			MusicPlayerControl.getInstance().stopMusic();
		} 
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#specificCleanUp()
	 */
	@Override
	protected void specificCleanUp()
	{
		//this.actionDone = null;
	}

	/*
	 * (non-Javadoc)
	 * @see @see control.events.BackgroundMusicEventListener#BackgroundMusicEvent(control.events.BackgroundMusicEvent)
	 */
	@Override
	public void BackgroundMusicEvent( BackgroundMusicEvent event) 
	{		
		this.backgroundMusicEnd = this.backgroundMusicEnd || event.getType() == BackgroundMusicEvent.END;		
	}

	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#getSceneClass()
	 */
	@Override
	protected Class getSceneClass()
	{
		return Level.class;
	}

	/*(non-Javadoc)
	 * @see @see GUI.game.component.event.FretEventListener#FretEvent(GUI.game.component.event.FretEvent)
	 */
	/*
	@Override
	public void FretEvent(GUI.game.component.event.FretEvent ev)
	{
		MusicNoteGroup note = ev.getNote();
		
		if( note != null && !note.isGhost() )
		{
			if( ev.getType() == GUI.game.component.event.FretEvent.NOTE_ENTERED )
			{
				ControllerManager.getInstance().setEnableControllerListener( true );
			}
			else if( ev.getType() == GUI.game.component.event.FretEvent.NOTE_EXITED )
			{
				ControllerManager.getInstance().setEnableControllerListener( false );
			}
		}
	}
	//*/

	public void updateInputGoal( double percentage )
	{
		if( this.scene != null )
		{
			List< ISprite > targets = this.scene.getSprites( IScene.INPUT_TARGET_ID, true );
			
			for( ISprite tg : targets )
			{
				((InputGoal)tg).setPercentage( percentage );
			}
		}
	}

	/*(non-Javadoc)
	 * @see @see control.scenes.ISceneControl#activeInputController()
	 */
	@Override
	public boolean activeInputController()
	{
		synchronized ( this )
		{
			return this.noteIntoFret;
		}
	}
}
