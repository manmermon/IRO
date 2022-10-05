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
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import image.BasicPainter2D;

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
			this.pic = (BufferedImage)BasicPainter2D.createEmptyCanva( size.width, size.height, Color.WHITE );			
		}
		
		return this.pic;
	}
	
	public float getAverageBrightness()
	{
		float brigthness = 1F;		

		if( this.pic != null )
		{
			brigthness = 0F;
			
			DataBufferInt rasterDB = (DataBufferInt)this.pic.getRaster().getDataBuffer();
			int[] imagePixelData = rasterDB.getData();
			
			for( int ip = 0; ip < imagePixelData.length; ip++ )
			{
				int px = imagePixelData[ ip ];
				int a = ( px & 0xFF000000 ) >> 24;
				int r = ( px & 0x00FF0000 ) >> 16;
				int g = ( px & 0x0000FF00 ) >> 8;
				int b = ( px & 0x000000FF );
				
				if( a != 0 )
				{				
					float[] hsb = Color.RGBtoHSB( r, g, b, null );
					float s = hsb[ 1 ];
					float br = hsb[ 2 ];
					
					brigthness += Math.sqrt( s * s*0 + br * br );// / Math.sqrt( 2 ); // sqrt(2) to normalize
				}
				else
				{
					brigthness += 1F;
				}
			}
			
			brigthness = brigthness / imagePixelData.length;
		}
		
		
		return brigthness;
	}

	@Override
	protected void updateSpecificSprite() 
	{	
	}
}
