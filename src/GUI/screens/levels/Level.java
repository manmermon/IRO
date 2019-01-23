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
package GUI.screens.levels;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.jfugue.midi.MidiDefaults;

import GUI.components.Fret;
import GUI.components.ISprite;
import GUI.components.TrackNotesSprite;
import GUI.components.Pentragram;
import GUI.screens.Scene;

public class Level extends Scene 
{
	private int BPM;
	
	private BackgroundMusic backgroundMusic;
	
	public Level( Dimension sceneSize ) 
	{
		super( sceneSize );
		
		this.BPM = MidiDefaults.DEFAULT_TEMPO_BEATS_PER_MINUTE;
	}

	public void setBackgroundPattern( BackgroundMusic backgroundPattern ) 
	{
		this.backgroundMusic = backgroundPattern;
	}
	
	public BackgroundMusic getBackgroundPattern() 
	{
		return backgroundMusic;
	}
	
	public void addBackgroud( ISprite sprite )
	{
		sprite.setZIndex( PLANE_BRACKGROUND );
		super.add( sprite, PLANE_BRACKGROUND );
	}
	
	public void addNote( TrackNotesSprite sprite )
	{
		sprite.setZIndex( PLANE_NOTE );
		super.add( sprite, PLANE_NOTE );
	}
	
	public void addPentagram( Pentragram sprite )
	{
		sprite.setZIndex( PLANE_PENTAGRAM );
		super.SPRITES.remove( PLANE_PENTAGRAM );
		super.add( sprite, PLANE_PENTAGRAM );
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
	
	public Pentragram getPentagram()
	{
		List< ISprite > pen = this.SPRITES.get( PLANE_PENTAGRAM );
		
		return (Pentragram)pen.get( 0 ); 
	}
	
	public List< TrackNotesSprite > getNotes()
	{
		List< ISprite > sprites = this.SPRITES.get( PLANE_NOTE );
		List< TrackNotesSprite > notes = new ArrayList< TrackNotesSprite >( );
		for( ISprite sprite : sprites )
		{
			notes.add( (TrackNotesSprite)sprite );
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
}
