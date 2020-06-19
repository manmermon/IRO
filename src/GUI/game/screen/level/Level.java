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
package GUI.game.screen.level;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfugue.midi.MidiDefaults;

import GUI.game.component.sprite.Fret;
import GUI.game.component.sprite.ISprite;
import GUI.game.component.sprite.Stave;
import GUI.game.component.sprite.MusicNoteGroup;
import GUI.game.component.sprite.Pause;
import GUI.game.screen.IPausable;
import GUI.game.screen.Scene;

public class Level extends Scene implements IPausable
{
	public static final int PLANE_BRACKGROUND = -1;
	public static final int PLANE_STAVE = 0;	 
	public static final int PLANE_FRET = 1;
	public static final int PLANE_NOTE = 2;
	public static final int PLANE_SCORE = 3;
	public static final int PLANE_INPUT_TARGET = 4;
	public static final int PLANE_TIME = 5;
	public static final int PLANE_PAUSE = 6;
	
	
	public static final String BACKGROUND_ID = "background";
	public static final String STAVE_ID = "stave";
	public static final String NOTE_ID = "note";
	public static final String FRET_ID = "fret";
	public static final String SCORE_ID = "score";
	public static final String INPUT_TARGET_ID = "input";
	public static final String TIME_ID = "time";
	public static final String PAUSE_ID = "pause";
	
	private int BPM;
	
	private BackgroundMusic backgroundMusic;
	private Map< Integer, BackgroundMusic > playerMusics;
	
	private Boolean pause = false;
	
	public Level( Dimension sceneSize) //, Rectangle frameBounds ) 
	{
		//super( sceneSize, frameBounds );
		super( sceneSize );
		
		this.playerMusics = new HashMap< Integer, BackgroundMusic>();
		this.BPM = MidiDefaults.DEFAULT_TEMPO_BEATS_PER_MINUTE;
	}

	public void setBackgroundPattern( BackgroundMusic backgroundPattern ) 
	{
		this.backgroundMusic = backgroundPattern;
	}
	
	public void setPlayerSheetMusic( Map< Integer, BackgroundMusic > playerSheets )
	{
		this.playerMusics.putAll( playerSheets );
	}
	
	public Map< Integer, BackgroundMusic > getPlayerSheets()
	{
		return this.playerMusics;
	}
	
	public BackgroundMusic getBackgroundPattern() 
	{
		return this.backgroundMusic;
	}
	
	public void addBackgroud( ISprite sprite )
	{
		sprite.setZIndex( PLANE_BRACKGROUND );
		super.add( sprite, PLANE_BRACKGROUND );
	}
	
	public void addNote( MusicNoteGroup sprite )
	{
		sprite.setZIndex( PLANE_NOTE );
		super.add( sprite, PLANE_NOTE );
	}
	
	public void addPause( Pause sprite )
	{
		sprite.setZIndex( PLANE_PAUSE );
		super.add( sprite, PLANE_PAUSE );
	}
	
	public void addPentagram( Stave sprite )
	{
		sprite.setZIndex( PLANE_STAVE );
		super.SPRITES.remove( PLANE_STAVE );
		super.add( sprite, PLANE_STAVE );
	}
	
	public void addFret( Fret sprite )
	{
		sprite.setZIndex( PLANE_FRET );
		super.SPRITES.remove( PLANE_FRET );
		super.add( sprite, PLANE_FRET );
	}
	
	public Fret getFret()
	{
		List< ISprite > fret = this.SPRITES.get( PLANE_FRET );
		
		return (Fret)fret.get( 0 );
	}
	
	public Stave getPentagram()
	{
		List< ISprite > pen = this.SPRITES.get( PLANE_STAVE );
		
		return (Stave)pen.get( 0 ); 
	}
	
	public List< MusicNoteGroup > getNotes()
	{
		List< ISprite > sprites = this.SPRITES.get( PLANE_NOTE );
		List< MusicNoteGroup > notes = new ArrayList< MusicNoteGroup >( );
		for( ISprite sprite : sprites )
		{
			notes.add( (MusicNoteGroup)sprite );
		}
		
		return notes;
	}
	
	public void setBPM( int tempo )
	{
		this.BPM = tempo;
	}
	
	public int getBPM()
	{
		return this.BPM;
	}
	
	@Override
	public void updateLevel() 
	{
		synchronized ( this.pause ) 
		{
			if( !this.pause )
			{
				super.updateLevel();
			}
		}
	}
	
	@Override
	public void setPause( boolean pause) 
	{
		synchronized ( this.pause ) 
		{
			this.pause = pause;
			
			for( ISprite sp : super.getSprites( Level.PAUSE_ID, false ) )
			{
				sp.setVisible( this.pause );
			}
		}
	}
	
	@Override
	public boolean isPaused() 
	{
		synchronized ( this.pause )
		{
			return this.pause;
		}
	}
}
