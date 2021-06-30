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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import GUI.game.component.event.SpriteEventListener;

public interface ISprite 
{
	/**
	 * 
	 * @return Sprite's ID
	 */
	public String getID();
	
	/**
	 * 
	 * @param vis
	 * @return
	 */
	public void setVisible( boolean vis );
	
	public boolean isVisible();
	
	/**
	 * 
	 * @return drawing order. Lower value are drawn firsts.
	 */
	public int getZIndex();
	/**
	 * Drawing order. Lower value are drawn firsts.
	 * @param zIndex - drawing ord 
	 */
	public void setZIndex( int zIndex );
	
	/**
	 * 
	 * @return Point2D.Double - Screen pixels location (x, y).
	 */
	public Point2D.Double getScreenLocation();
	
	/**
	 * Set the sprite's screen location
	 * @param loc - Point2D.Double with screen location (x,y).
	 */
	public void setScreenLocation( Point2D.Double loc );
	
	/**
	 * 
	 * @return Rectangle with sprite bounds
	 */
	public Rectangle getBounds();
	
	/**
	 * 
	 * @return {@link BufferedImage} - sprite's picture.
	 */
	public BufferedImage getSprite();
	
	/**
	 * Prepare the next picture from an animation and update sprite's screen location.
	 */
	public void updateSprite( );
	
	/**
	 * Set the sprite's dimension
	 * @param size - sprite's dimension
	 */
	public void setSize( Dimension size );
	
	/**
	 * Get the sprite's dimension
	 * @return {@link Dimension} - sprite's dimension
	 */
	public Dimension getSize();
			
	
	/**
	 * 
	 * @param listener
	 */
	public void addSpriteEventListener( SpriteEventListener listener );
	
	/**
	 * 
	 * @param listener
	 */
	public void removeSpriteEventListener( SpriteEventListener listener );
	
	/*
	public void addMouseSpriteEventListener( MouseSpriteEventListener listener );
	
	public void removeMouseSpriteEventListener( MouseSpriteEventListener listener );
	*/
}
