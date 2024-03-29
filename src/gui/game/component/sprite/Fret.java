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

package gui.game.component.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import gui.game.component.event.FretEvent;
import gui.game.component.event.FretEventListener;
import config.Player;
import image.BasicPainter2D;
import statistic.RegistrarStatistic;
import statistic.RegistrarStatistic.GameFieldType;

public class Fret extends AbstractSprite
{
	//private Pentragram pentagram;	
	private Polygon fret;
	
	private int fretWidth = 100;

	private Color fretFillColor;
	private Color fretBorderColor;
	
	private BufferedImage fretImg = null;
	
	public Fret( String id, Dimension size ) 
	{
		super( id );
		
		super.setSize( size );
		
		//this.fretWidth = pentagram.getPentragramWidth() / 3;
		this.fretWidth = size.width;
		
		this.fret = new Polygon();
		
		this.fret.addPoint( 0, 0 );
		this.fret.addPoint( this.fretWidth, 0 );
		/*
		this.fret.addPoint( this.fretWidth, this.pentagram.getPentagramHeigh() );
		this.fret.addPoint( 0, this.pentagram.getPentagramHeigh() );
		*/
		this.fret.addPoint( this.fretWidth, size.height );
		this.fret.addPoint( 0, size.height );
		
		this.setFretBrightness( 1 );		
	}
		
	public int getFretWidth()
	{
		return this.fretWidth;
	}
	
	public boolean isNoteIntoFret( MusicNoteGroup note )
	{
		Point2D.Double prevLoc = note.getPreviousNoteLocation();
		Point2D.Double currentLoc = note.getNoteLocation();
				
		boolean in = this.fret.contains( currentLoc );
		
		if( in )
		{
			if( prevLoc == null
					|| !this.fret.contains( prevLoc ) )
			{
				this.fireFretEvent( FretEvent.NOTE_ENTERED, note );
			}
		}
		else
		{
			if( this.fret.contains( prevLoc ) )
			{
				this.fireFretEvent( FretEvent.NOTE_EXITED, note );
			}
		}
			
		
		return in;
	}

	@Override
	public void setScreenLocation( Point2D.Double loc) 
	{
		int deltaX = 0;
		int deltaY = 0;
		
		Point2D.Double fretCenter = new Point2D.Double();
		
		for( int x : this.fret.xpoints )
		{
			fretCenter.x += x;
		}
				
		for( int y : this.fret.ypoints )
		{
			fretCenter.y += y;
		}
		
		Rectangle r = this.fret.getBounds();
		int h = r.height;
		int w = r.width;
				
		fretCenter.x /= this.fret.xpoints.length;
		fretCenter.y /= this.fret.ypoints.length;
		
		deltaX = (int)( loc.x - fretCenter.x - w/2 );
		deltaY = (int)( loc.y - fretCenter.y + h/2 );
		
		this.fret.translate( deltaX, deltaY );
		
		r = this.fret.getBounds();
		Point2D.Double l = new Point2D.Double();
		l.x = r.x;
		l.y = r.y;
		super.setScreenLocation( l );
	}
	
	/*
	 * (non-Javadoc)
	 * @see GUI.components.AbstractSprite#getSprite()
	 */
	@Override
	protected BufferedImage createSprite() 
	{		 
		/*
		int xs[] = new int[] { 0, this.fret.getBounds().width, this.fret.getBounds().width, 0 };
		int ys[] = new int[] { 0, 0, this.fret.getBounds().height, this.fret.getBounds().height };
		return (BufferedImage)basicPainter2D.outlinePolygon( xs, ys, 2, Color.BLACK, null );
		//*/
		
		if( this.fretImg == null )
		{
			Rectangle r = this.fret.getBounds();
			
			this.fretImg = (BufferedImage)BasicPainter2D.rectangle( r.width, r.height, 3
																	//, this.fretBorderColor
																	, this.fretBorderColor
																	, this.fretFillColor );
		}
		
		return this.fretImg; 
	}

	/*
	 * (non-Javadoc)
	 * @see GUI.components.AbstractSprite#updateSprite()
	 */
	@Override
	protected void updateSpecificSprite() 
	{		
	}
	
	public void addFretEventListener( FretEventListener listener )
	{
		super.listenerList.add( FretEventListener.class, listener );
	}
	
	public void removeFretEventListener( FretEventListener listener )
	{
		super.listenerList.remove( FretEventListener.class, listener );
	}
	
	public void setFretBrightness( float brigthness )
	{			
		this.fretFillColor = new Color( 127, 127, 127, 120 );
		this.fretBorderColor= new Color( 25, 25, 25, 180 );
		
		if( brigthness < 0.75 )
		{
			this.fretFillColor = new Color( 255, 255, 255, 160 );
			this.fretBorderColor= new Color( 255, 255, 255, 240 );			
		}
		
		this.fretImg = null;
		this.createSprite();
	}
	
	private synchronized void fireFretEvent( int typeEvent, MusicNoteGroup note )
	{
		int playerID = Player.ANONYMOUS;
		
		if( note != null && note.getOwner() != null ) 
		{
			playerID = note.getOwner().getId();
		}
				
		switch ( typeEvent )
		{
			case FretEvent.NOTE_ENTERED:
			{	
				RegistrarStatistic.addGameData( playerID, GameFieldType.NOTE_ENTER_FRET );
				break;
			}
			case FretEvent.NOTE_EXITED:
			{
				RegistrarStatistic.addGameData( playerID, GameFieldType.NOTE_EXIT_FRET );
				break;
			}
			default:
			{
				break;
			}
		}
		
		FretEvent event = new FretEvent( this, note, typeEvent );

		FretEventListener[] listeners = super.listenerList.getListeners( FretEventListener.class );

		for (int i = 0; i < listeners.length; i++ ) 
		{
			listeners[ i ].FretEvent( event );
		}
	}
}
