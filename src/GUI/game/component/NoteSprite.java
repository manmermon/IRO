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

package GUI.game.component;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.jfugue.theory.Note;

import GUI.game.component.event.SpriteEvent;
import GUI.game.component.event.SpriteEventListener;
import image.icon.MusicInstrumentIcons;
import music.IROTrack;
import statistic.GameStatistic;
import statistic.GameStatistic.FieldType;

public class NoteSprite extends AbstractSprite
{ 	
	public static final int REST = -1;
	public static final int SI = 0;
	public static final int LA = 1;
	public static final int SOL = 2;
	public static final int FA = 3;
	public static final int MI = 4;
	public static final int RE = 5;
	public static final int DO = 6;
	
	private Pentragram pentagram;
	private double shiftVelocity; // pixel by second
	private int shiftDirection = -1;
	
	private Color color ;
	
	private int noteScreenSize = 30;
		
	private long time = -1;	
	
	private boolean isSelected;
	private boolean isPlay;
		
	private boolean isGhost = false;
	
	//private IROChord chord;
	
	private List< IROTrack > noteTracks;
	
	/*
	public NoteSprite( String track, List< Note > Notes, String noteID, Pentragram pen, int initLoc ,double shiftVel, boolean ghost ) 
	{
		super();
		
		this.chord = new IROChord( track );
		this.chord.addNotes( Notes );
				
		super.ID = noteID;		
		this.pentagram = pen;		
		this.shiftVelocity = shiftVel;
			
		
		int y = this.pentagram.getRailHeight();
		
		this.noteScreenSize = y;//(2 * y ) / 3;
		if( this.noteScreenSize < 1 )
		{
			this.noteScreenSize = 1;
		}
		
		int noteLine = REST;
		
		for( Note note : Notes )
		{	
			if( !note.isRest() )
			{
				noteLine = this.getNoteValue( note.toString() );			
				break;
			}
		}
		
		super.screenLoc = new Point2D.Double( initLoc, noteLine * y + ( y - this.noteScreenSize ) / 2);
		
		this.color = Color.RED;		
		
		this.isPlay = false;
		
		this.isGhost = ghost;
	}
	*/
	
	public NoteSprite( String track, List< IROTrack > Notes, String noteID, Pentragram pen, int initLoc ,double shiftVel, boolean ghost ) 
	{
		super( noteID );
		
		this.noteTracks = new ArrayList< IROTrack >();
		this.noteTracks.addAll( Notes );	
				
		this.pentagram = pen;		
		this.shiftVelocity = shiftVel;
			
		
		int y = this.pentagram.getRailHeight();
		
		this.noteScreenSize = y;//(2 * y ) / 3;
		if( this.noteScreenSize < 1 )
		{
			this.noteScreenSize = 1;
		}
		
		int noteLine = REST;
		
		noteScreenLoc:
		for( IROTrack Track : this.noteTracks )
		{
			for( List< Note > notes : Track.getTrackNotes().values() )
			{	
				for( Note n : notes )
				{
					if( !n.isRest() )
					{
						noteLine = this.getNoteValue( n.toString() );			
						break noteScreenLoc;
					}
				}
			}
		}
		
		super.screenLoc = new Point2D.Double( initLoc, noteLine * y + ( y - this.noteScreenSize ) / 2);
		
		this.color = Color.RED;		
		
		this.isPlay = false;
		
		this.isGhost = ghost;
	}
	
	public void setTempo( int bpm )
	{
		for( IROTrack track : this.noteTracks )
		{
			track.setTempo( bpm );
		}
	}
	
	public void setGhost( boolean ghost )
	{
		this.isGhost = ghost;
	}
	
	private int getNoteValue( String n )
	{ 
		int note = DO;
		
		char noteCode = n.charAt( 0 );
		
		switch ( noteCode ) 
		{
			case 'A':
			{
				note = LA;
				break;
			}
			case 'B':
			{
				note = SI;
				break;
			}
			case 'D':
			{
				note = RE;
				break;
			}
			case 'E':
			{
				note = MI;
				break;
			}
			case 'F':
			{
				note = FA;
				break;
			}
			case 'G':
			{
				note = SOL;
				break;
			}
			case 'R':
			{
				note = REST;
				break;
			}
		}
		
		return note;
	}
		
	/*
	public void setInstrument( String instr )
	{
		String instrument = instr;
		
		Byte bt = MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get( instr );
		
		if( bt != null )
		{
			instrument = MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( MidiDefaults.META_INSTRUMENT_NAME );
		}
		
		for( IROTrack track : this.noteTracks )
		{
			track.setInstrument( instrument );
		}
	}
	*/
	
	public List< IROTrack > getNotes()
	{
		return this.noteTracks;
	}
		
	public void toggleDirection()
	{
		this.shiftDirection = -this.shiftDirection;
	}
	
	public void setColor( Color c )
	{
		this.color = c;
	}
	
	public void setPlayed( boolean played )
	{
		this.isPlay = played;
	}
	
	public boolean isPlayed()
	{
		return this.isPlay;
	}
	
	public void setSelected( boolean selected )
	{
		this.isSelected = selected;
	}
	
	public boolean isSelected()
	{
		return this.isSelected;
	}
		
	public Point2D.Double getNoteLocation()
	{
		Point2D.Double loc = new Point2D.Double( );
		loc.x += this.noteScreenSize / 2 + super.getScreenLocation().x;
		loc.y += this.noteScreenSize / 2 + super.getScreenLocation().y;
		
		return loc;
	}
		
	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#getSprite()
	 */
	@Override
	public BufferedImage getSprite() 
	{	
		BufferedImage pic = null;
		
		if( !this.isGhost )
		{	
			if( !this.noteTracks.isEmpty() )
			{	
				String instrument = "";
				for( IROTrack track : this.noteTracks )
				{
					if( instrument.isEmpty() )
					{
						instrument = track.getInstrument();
					}
					else if( !instrument.equalsIgnoreCase( track.getInstrument() ) )
					{
						instrument += track.getInstrument();
					}
				}
				
				pic = (BufferedImage)MusicInstrumentIcons.getInstrument( instrument, this.noteScreenSize, this.color );
			}			
		}
		
		return pic;
	}

	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#updateSprite()
	 */
	@Override
	protected void updateSpecificSprite() 
	{
		double t = 0;
		
		if( time >= 0 )
		{
			t = ( System.nanoTime() - this.time ) / 1e9D;
		}
		
		double update = this.shiftVelocity * t * this.shiftDirection;
		super.screenLoc.x += update;
		
		
		this.time = System.nanoTime();
	}

	public boolean isGhost() 
	{
		return this.isGhost;
	}
}
