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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.event.EventListenerList;

import gui.game.component.event.SpriteEvent;
import gui.game.component.event.SpriteEventListener;

public abstract class AbstractSprite implements ISprite
{
	protected Point2D.Double screenLoc;
	protected int zIndex;
	
	protected String ID;
	
	protected Dimension spriteSize;
	
	protected EventListenerList listenerList;

	protected Rectangle frameBounds;
	
	private boolean onScreen = false;
	
	private boolean visible = true;
	
	public AbstractSprite( String idSprite )
	{		
		if( idSprite == null || idSprite.isEmpty() )
		{
			throw new IllegalArgumentException( "Input string null or empty." );
		}
		
		this.screenLoc = new Point2D.Double( );
		this.zIndex = 0;
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
		return new Point2D.Double( this.screenLoc.x, this.screenLoc.y );
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
		this.spriteSize = new Dimension( size );
	}
	
	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#getSize()
	 */
	public Dimension getSize()
	{
		return new Dimension( this.spriteSize );
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
	
	//*
	@Override
	public void setFrameBounds(Rectangle bounds) 
	{
		this.frameBounds = new Rectangle( bounds );
	}
	//*/
	
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
	
	@Override
	public void setVisible(boolean vis) 
	{
		this.visible = vis;
	} 
	
	@Override
	public boolean isVisible() 
	{	
		return this.visible;
	}
	
	protected abstract void updateSpecificSprite();
	
	
	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#getSprite()
	 */
	@Override
	public BufferedImage getSprite()
	{
		BufferedImage sprite = null;
		
		if( this.visible )
		{
			sprite = this.createSprite();
		}
		
		return sprite;
	}
	
	protected abstract BufferedImage createSprite();		
}
