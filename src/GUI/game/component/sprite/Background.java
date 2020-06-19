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
import java.awt.Image;
import java.awt.image.BufferedImage;

import image.basicPainter2D;

public class Background extends AbstractSprite 
{
	private BufferedImage pic = null;
	
	public Background( Dimension d, String id ) 
	{
		super( id );
		
		super.setSize( d );
	}

	public void setImage( BufferedImage bgImage )
	{
		this.pic = bgImage;
	}
	
	@Override
	protected BufferedImage createSprite() 
	{
		if( this.pic == null )
		{
			Dimension size = super.getSize();
			this.pic = (BufferedImage)basicPainter2D.createEmptyCanva( size.width, size.height, Color.WHITE );			
		}
		
		return this.pic;
	}

	@Override
	protected void updateSpecificSprite() 
	{	
	}
}
