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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.event.EventListenerList;

import GUI.game.component.event.SpriteEvent;
import GUI.game.component.event.SpriteEventListener;
import statistic.GameStatistic;
import statistic.GameStatistic.FieldType;

public abstract class AbstractSprite implements ISprite 
{
	protected Point2D.Double screenLoc;
	protected int zIndex;
	
	protected String ID;
	
	protected Dimension spriteSize;
	
	protected EventListenerList listenerList;

	private Rectangle frameBounds;
	
	private boolean onScreen = false;
	
	public AbstractSprite( String idSprite )
	{		
		if( idSprite == null || idSprite.isEmpty() )
		{
			throw new IllegalArgumentException( "Input string null or empty." );
		}
		
		this.screenLoc = new Point2D.Double( );
		this.zIndex = 0;
		this.ID = getClass().getSimpleName();
		this.spriteSize = new Dimension( 50, 50 );
		
		this.listenerList = new EventListenerList();
		
		this.ID = idSprite;
	}
	
	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#getZIndex()
	 */
	@Override
	public int getZIndex() 
	{
		return this.zIndex;
	}

	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#setZIndex(int)
	 */
	@Override
	public void setZIndex(int zIndex) 
	{
		this.zIndex = zIndex;
	}

	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#getScreenLocation()
	 */
	@Override
	public Point2D.Double getScreenLocation() 
	{
		return this.screenLoc;
	}

	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#setScreenLocation(java.awt.Point)
	 */
	@Override
	public void setScreenLocation( Point2D.Double loc) 
	{
		this.screenLoc.x = loc.x;
		this.screenLoc.y = loc.y;
	}

	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#getID()
	 */
	@Override
	public String getID() 
	{		
		return this.ID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#setSize(java.awt.Dimension)
	 */
	public void setSize( Dimension size )
	{
		this.spriteSize = size;
	}
	
	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#getSize()
	 */
	public Dimension getSize()
	{
		return this.spriteSize;
	}
	
	@Override
	public void addSpriteEventListener( SpriteEventListener listener) 
	{
		this.listenerList.add( SpriteEventListener.class, listener );
	}
	
	@Override
	public void removeSpriteEventListener( SpriteEventListener listener) 
	{
		this.listenerList.remove( SpriteEventListener.class, listener );
	}
		
	private synchronized void fireSceneEvent( int typeEvent )
	{		
		SpriteEvent event = new SpriteEvent( this, typeEvent );

		SpriteEventListener[] listeners = this.listenerList.getListeners( SpriteEventListener.class );

		for (int i = 0; i < listeners.length; i++ ) 
		{
			listeners[ i ].SpriteEvent( event );
		}
	}
	
	@Override
	public void setFrameBounds(Rectangle bounds) 
	{
		this.frameBounds = bounds;
	}
	
	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#updateSprite()
	 */
	@Override
	final public void updateSprite()
	{
		this.updateSpecificSprite();
				
		if( this.screenLoc.x < 0 )
		{
			this.fireSceneEvent( SpriteEvent.OUTPUT_SCREEN );
		}
		else
		{
			if( this.frameBounds != null )
			{				
				if( this.frameBounds.getBounds().contains( this.screenLoc ) && !this.onScreen )
				{
					this.onScreen = true;
					this.fireSceneEvent( SpriteEvent.ON_SCREEN );
				}
			}
		}
	}
		
	@Override
	public Rectangle getBounds()
	{
		
		Rectangle r = new Rectangle( (int)this.screenLoc.x
									, (int)this.screenLoc.y
									, (int)this.spriteSize.width
									, (int)this.spriteSize.height );
		
		return r;
	}
	
	protected abstract void updateSpecificSprite();
	
	
	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#getSprite()
	 */
	@Override
	public abstract BufferedImage getSprite();
	
}
