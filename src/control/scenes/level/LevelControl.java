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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.event.EventListenerList;


import GUI.GuiManager;
import GUI.game.component.Background;
import GUI.game.component.Frame;
import GUI.game.component.Fret;
import GUI.game.component.ISprite;
import GUI.game.component.TrackNotesSprite;
import GUI.game.screen.IScene;
import GUI.game.screen.Scene;
import GUI.game.screen.level.Level;
import config.ConfigApp;
import control.events.BackgroundMusicEventListener;
import control.events.SceneEvent;
import control.events.SceneEventListener;
import control.inputs.IInputAction;
import control.inputs.KeystrokeAction;
import control.inputs.MouseStrokeAction;
import control.music.MusicPlayerControl;
import control.scenes.AbstractSceneControl;
import exceptions.IllegalLevelStateException;
import exceptions.SceneException;
import image.basicPainter2D;
import music.IROTrack;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppableThread;
import control.events.BackgroundMusicEvent;

public class LevelControl extends AbstractSceneControl implements BackgroundMusicEventListener
{
	private MusicPlayerControl soundCtrl;
		
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
			
			this.soundCtrl = new MusicPlayerControl( ((Level)this.scene).getBackgroundPattern() );
			this.soundCtrl.addBackgroundMusicEvent( this );
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
			
		this.soundCtrl.startThread();			
	}
	
	/*
	 * (non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#updatedLoopAfterSetScene()
	 */
	@Override
	protected void updatedLoopAfterSetScene() 
	{		
		List< ISprite > FRET = this.scene.getSprites( IScene.FRET_ID );
		List< ISprite > Notes = this.scene.getSprites( IScene.NOTE_ID );

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
				this.soundCtrl.playNotes( noteTracks );					
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
	public void specificUpdateScene( IInputAction act ) throws SceneException
	{	
		this.actionDone.set( act != null );				
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
		this.soundCtrl.stopThread( IStoppableThread.FORCE_STOP );
	}
	
	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#specificCleanUp()
	 */
	@Override
	protected void specificCleanUp()
	{
		
		if( this.soundCtrl != null )
		{
			this.soundCtrl.stopThread( IStoppableThread.FORCE_STOP );
		}
		
		this.soundCtrl =  null;
				
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

}
