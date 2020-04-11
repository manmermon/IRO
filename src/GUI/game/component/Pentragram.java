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
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import image.basicPainter2D;

public class Pentragram extends AbstractSprite
{
	private Dimension dimension;
	private int numLines = 7; 
	private int wayHeight;
	
	public Pentragram( Dimension panelSize, String id ) 
	{
		super( id );
		
		this.dimension = panelSize;
		
		this.wayHeight = this.dimension.height / this.numLines;
	}
	
	public int getPentagramHeigh()
	{
		return this.dimension.height;
	}
	
	public int getPentragramWidth()
	{
		return this.dimension.width;
	}
	
	public int getRailHeight()
	{
		return this.dimension.height / this.numLines;
	}
	

	@Override
	public BufferedImage getSprite() 
	{
		int railLoc = this.wayHeight / 2;
		int railThinck = railLoc / 10;
		if( railThinck < 1 )
		{
			railThinck = 1;
		}
		
		BufferedImage pen = (BufferedImage)basicPainter2D.createEmptyCanva( this.dimension.width, this.dimension.height, null );
		
		for( int i = 0; i < this.numLines; i++ )
		{
			basicPainter2D.line( 0, i * this.wayHeight + railLoc - railThinck / 2
												, this.dimension.width, i * this.wayHeight + railLoc - railThinck / 2, railThinck, Color.BLACK, pen );
		}
		
		return pen;
	}

	@Override
	public void updateSpecificSprite() 
	{	
	}
}
