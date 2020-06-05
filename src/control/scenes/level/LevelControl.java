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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import GUI.game.component.event.FretEventListener;
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
										, FretEventListener
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
			Level lv = (Level)this.scene;
			lv.getFret().addFretEventListener( this );
			
			MusicPlayerControl.getInstance().setBackgroundMusicPatter( lv.getBackgroundPattern() );
			MusicPlayerControl.getInstance().addBackgroundMusicEvent( this );
			
			MusicPlayerControl.getInstance().setPlayerMusicSheets( lv.getPlayerSheets() );
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
		Level lv = (Level)this.scene;
		Fret fret = lv.getFret();
		
		List< ISprite > Notes = this.scene.getSprites( IScene.NOTE_ID, true );

		if( fret != null 
				&& Notes != null 
				&& !Notes.isEmpty() )
		{
			boolean noteInFret = false;
			
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
				}					
			}
			
			this.noteIntoFret = noteInFret;

			this.actionDone.set( false );
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
	public void BackgroundMusicEvent( BackgroundMusicEvent event ) 
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
	@Override
	public void FretEvent(GUI.game.component.event.FretEvent ev)
	{
		MusicNoteGroup note = ev.getNote();
		
		if( note != null && !note.isGhost() )
		{
			if( ev.getType() == GUI.game.component.event.FretEvent.NOTE_EXITED )
			{
				if( !note.isGhost() && !note.isSelected() )
				{
					int player = note.getOwner().getId();
					double time = note.getDuration();
					
					MusicPlayerControl.getInstance().mutePlayerSheet( player, time );
				}
			}
		}
	}

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
