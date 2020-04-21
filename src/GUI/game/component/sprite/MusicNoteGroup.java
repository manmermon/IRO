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

package GUI.game.component.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jfugue.midi.MidiDictionary;
import org.jfugue.theory.Note;

import GUI.game.component.event.SpriteEvent;
import GUI.game.component.event.SpriteEventListener;
import control.music.MusicPlayerControl;
import general.NumberRange;
import image.basicPainter2D;
import image.icon.MusicInstrumentIcons;
import music.IROTrack;
import statistic.GameStatistic;
import statistic.GameStatistic.FieldType;

public class MusicNoteGroup extends AbstractSprite
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
	
	private boolean isSelected;
	private boolean isPlay;
		
	private boolean isGhost = false;
	
	private Point2D.Double previousLocation = null;
	
	private List< IROTrack > noteTracks;
	
	private BufferedImage noteImg = null;
	
	private Double SheetTime = Double.NaN;	
	private Double delay = 0D;	
	
	//
	//
	// ANIMATION SETTTINGS
	//
	//
	
	private NumberRange angleRange = new NumberRange( -45, 45 );
	private final int numAngles = 7;
	private double stepAngle = angleRange.getRangeLength() / numAngles;
	private int directionAngle = 1;	
	private double currentAngle = 0;
	
	private long time = -1;	
	private long animationTime = -1;
	private final long totalTimeAnimation = 1000; // milliseconds
	private long timeUpdate = totalTimeAnimation / numAngles; // milliseconds
	
	public MusicNoteGroup( String track
							, double sheetTime
							, List< IROTrack > Notes
							, String noteID, Pentragram pen
							, int initLoc , double shiftVel
							, boolean ghost ) 
	{
		super( noteID );
				
		this.noteTracks = new ArrayList< IROTrack >();
		this.noteTracks.addAll( Notes );	
						
		this.pentagram = pen;		
		this.shiftVelocity = shiftVel;
			
		
		int y = this.pentagram.getRailHeight();
				
		Dimension d = new Dimension();
		d.width = y;
		d.height = y;
		
		if( d.width < 1 )
		{
			d.width = 1;
		}
		
		if( d.height < 1 )
		{
			d.height = 1;
		}
		
		super.setSize( d );
		
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
						noteLine = getNoteValue( n.toString() );			
						break noteScreenLoc;
					}
				}
			}
		}
		
		super.screenLoc = new Point2D.Double( initLoc, noteLine * y + ( y - super.getSize().width ) / 2);
		
		this.color = Color.RED;		
		
		this.isPlay = false;
		
		this.isGhost = ghost;
		
		this.SheetTime = sheetTime;
		
		super.addSpriteEventListener( new SpriteEventListener()
		{	
			@Override
			public void SpriteEvent(SpriteEvent ev)
			{				
				switch ( ev.getType()  )
				{
					case SpriteEvent.ON_SCREEN:
					{
						GameStatistic.add( FieldType.NOTE_SHOW );
						
						synchronized ( delay )
						{
							delay = ( SheetTime - MusicPlayerControl.getInstance().getPlayTime() );
							//System.out.println("MusicNoteGroup.MusicNoteGroup() " + delay + ": " + noteTracks );
						}						
						
						break;
					}
					case SpriteEvent.OUTPUT_SCREEN:
					{
						GameStatistic.add( FieldType.NOTE_REMOVE );
						break;
					}
					default:
					{
						break;
					}
				}
			}
		});
	}
	
	public void setImage( BufferedImage img )
	{
		this.noteImg = img;		
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
	
	private static int getNoteValue( String n )
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
	
	/**
	 * @return the color
	 */
	public Color getColor()
	{
		return this.color;
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
		loc.x += super.getSize().width / 2 + super.getScreenLocation().x;
		loc.y += super.getSize().width / 2 + super.getScreenLocation().y;
		
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
				pic = basicPainter2D.copyImage( this.noteImg ); 
				if( pic == null )
				{				
					Set< Byte > instruments = new TreeSet< Byte >();
					for( IROTrack track : this.noteTracks )
					{
						Byte val = MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get( track.getInstrument().toUpperCase() );
						if( val == null )
						{
							val = Byte.MIN_VALUE;
						}
						
						instruments.add( val );
					}
					
					List< Byte > l = new ArrayList< Byte >();
					l.addAll( instruments );
					
					pic = (BufferedImage)MusicInstrumentIcons.getInstrument( l, super.getSize().width, this.color );
				}
				else
				{	
					if( this.currentAngle != 0 )
					{
						pic = basicPainter2D.rotate( pic, this.currentAngle );
					}					
					
					basicPainter2D.changeColorPixels( Color.BLACK, this.color, pic );
					//basicPainter2D.changeColorPixels( Color.BLACK, this.color, 0.25F, pic );
				}
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
		
		if( this.previousLocation == null )
		{
			this.previousLocation = new Point2D.Double( super.screenLoc.x, super.screenLoc.y );
		}
		
		double gradient = this.shiftVelocity * this.shiftDirection;
		double update = t * gradient;
		synchronized ( this.delay )
		{			
			update += -this.delay * gradient;				
			this.delay = 0D;
		}		
		
		this.previousLocation = this.getNoteLocation();
		
		super.screenLoc.x += update;
				
		this.time = System.nanoTime();
		
		if( this.animationTime < 0 )
		{
			this.animationTime = System.currentTimeMillis();
		}
		
		if( System.currentTimeMillis() - this.animationTime > this.timeUpdate )
		{
			double newAngle =  this.currentAngle + this.stepAngle * this.directionAngle;
			if( !this.angleRange.within( newAngle ) )
			{
				this.directionAngle *= -1;
				newAngle =  this.currentAngle + this.stepAngle * this.directionAngle;
			}
			
			this.currentAngle = newAngle;
			
			this.animationTime = System.currentTimeMillis();
		}
	}
	
	public Point2D.Double getPreviousNoteLocation()
	{
		return this.previousLocation;
	}

	public boolean isGhost() 
	{
		return this.isGhost;
	}
	
	/**
	 * @return the sheetTime
	 */
	public double getSheetTime()
	{
		return this.SheetTime;
	}
}

