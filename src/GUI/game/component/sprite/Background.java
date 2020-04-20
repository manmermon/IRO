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
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;

import image.basicPainter2D;

public class Background extends AbstractSprite 
{
	private Dimension size;
	private BufferedImage pic = null;
	
	public Background( Dimension d, String id, String file ) 
	{
		super( id );
		
		this.size = new Dimension( d );
		
		try
		{
			if( file != null )
			{
				Image img = ImageIO.read( new File( file ) );
				
				img = img.getScaledInstance( this.size.width
											, this.size.height
											, Image.SCALE_SMOOTH );
				
				this.pic = (BufferedImage)basicPainter2D.copyImage( img );
			}
		}
		catch (IOException ex)
		{	
		}
	}

	public Background( BufferedImage bg, String id )
	{
		super( id );
		
		if( bg == null )
		{
			throw new IllegalArgumentException( "Input null." );
		}
		
		this.size = new Dimension( bg.getWidth(), bg.getHeight() );

		this.pic = bg;
	}
	
	@Override
	public BufferedImage getSprite() 
	{
		Image back = this.pic;
		
		if( back == null )
		{
			basicPainter2D.createEmptyCanva( this.size.width, this.size.height, Color.WHITE );
		}
		
		return (BufferedImage)back;
	}

	@Override
	protected void updateSpecificSprite() 
	{	
	}
}
