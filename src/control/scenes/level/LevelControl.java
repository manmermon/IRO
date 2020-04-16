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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import GUI.game.component.Fret;
import GUI.game.component.ISprite;
import GUI.game.component.TrackNotesSprite;
import GUI.game.component.event.FretEventListener;
import GUI.game.screen.IScene;
import GUI.game.screen.level.Level;
import config.ConfigApp;
import control.events.BackgroundMusicEventListener;
import control.events.InputActionEvent;
import control.events.SceneEvent;
import control.music.MusicPlayerControl;
import control.scenes.AbstractSceneControl;
import exceptions.SceneException;
import music.IROTrack;
import stoppableThread.IStoppableThread;
import control.controller.ControllerManager;
import control.controller.KeystrokeAction;
import control.controller.MouseStrokeAction;
import control.events.BackgroundMusicEvent;

public class LevelControl extends AbstractSceneControl 
							implements BackgroundMusicEventListener
										, FretEventListener
{		
	private AtomicBoolean actionDone;
	private boolean backgroundMusicEnd = false;
	private Color preactionColor = Color.RED;
	private Color waitActionColor = Color.BLUE;
	private Color actionColor = Color.GREEN;
	
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
	@Override
	public void setScene(IScene scene) throws SceneException
	{
		super.setScene( scene );
		
		Level lv = (Level)scene;
		
		lv.getFret().addFretEventListener( this );
	}
	
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
			this.preactionColor = (Color)ConfigApp.getParameter( ConfigApp.PREACTION_COLOR).getSelectedValue();
			this.waitActionColor = (Color)ConfigApp.getParameter( ConfigApp.WAITING_ACTION_COLOR).getSelectedValue();
			this.actionColor = (Color)ConfigApp.getParameter( ConfigApp.ACTION_COLOR).getSelectedValue();
			
			MusicPlayerControl.getInstance().setBackgroundMusicPatter( ( ((Level)this.scene).getBackgroundPattern() ) );
			MusicPlayerControl.getInstance().addBackgroundMusicEvent( this );
		}
		else
		{
			throw new SceneException( "Level null" );
		}
	} 
		
	/*
	 * (non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#updatedLoopAfterSetScene()
	 */
	@Override
	protected void updatedLoopAfterSetScene() 
	{		
		List< ISprite > FRET = this.scene.getSprites( IScene.FRET_ID, true );
		List< ISprite > Notes = this.scene.getSprites( IScene.NOTE_ID, true );

		if( FRET != null 
				&& Notes != null
				&& !FRET.isEmpty() 
				&& !Notes.isEmpty() )
		{
			Fret fret = (Fret)FRET.get( 0 );

			List< IROTrack > noteTracks = new ArrayList< IROTrack >();

			for( ISprite __Note : Notes )
			{
				TrackNotesSprite note = (TrackNotesSprite) __Note;

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
							note.setColor( this.actionColor );
						}
					}
					else if( !note.isSelected() )
					{
						note.setColor( this.waitActionColor );
					}
				}
				else
				{						
					if( !note.isSelected() )
					{
						note.setColor( this.preactionColor );
					}

					if( note.isSelected() && !note.isPlayed() )
					{
						note.setPlayed( true );

						noteTracks.addAll( note.getNotes() );							
					}

				}					
			}

			this.actionDone.set( false );

			if( !noteTracks.isEmpty() )
			{	
				try
				{
					MusicPlayerControl.getInstance().playNotes( noteTracks );
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
		this.actionDone = null;
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
	@Override
	public void FretEvent(GUI.game.component.event.FretEvent ev)
	{
		TrackNotesSprite note = ev.getNote();
		
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

}
