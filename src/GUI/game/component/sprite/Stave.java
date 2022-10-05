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

import image.BasicPainter2D;

public class Stave extends AbstractSprite
{
	public static final int NUMBER_OF_WAYS = 7; 
	private int wayHeight;
	
	private BufferedImage stave = null;
	
	private Color[] wayColors = new Color[] { Color.BLACK };
	
	public Stave( Dimension panelSize, String id ) 
	{		
		this( panelSize, id, null );
	}
	
	public Stave( Dimension panelSize, String id, Color[] wayColours ) 
	{
		super( id );
		
		super.spriteSize.width = panelSize.width;
		super.spriteSize.height = panelSize.height;
		
		this.wayHeight = this.spriteSize.height / NUMBER_OF_WAYS;
		
		if( wayColours != null && wayColours.length > 0 )
		{
			this.wayColors = wayColours;
		}
		
		this.createStaveImg();
	}
	
	public int getStaveHeigh()
	{
		return this.spriteSize.height;
	}
	
	public int getStaveWidth()
	{
		return this.spriteSize.width;
	}
	
	public int getRailHeight()
	{
		return this.spriteSize.height / NUMBER_OF_WAYS;
	}
	
	private void createStaveImg()
	{
		int railLoc = this.wayHeight / 2;
		int railThinck = railLoc / 10;
		if( railThinck < 1 )
		{
			railThinck = 1;
		}
		
		this.stave = (BufferedImage)BasicPainter2D.createEmptyCanva( this.spriteSize.width, this.spriteSize.height, null );
		
		for( int i = 0; i < NUMBER_OF_WAYS; i++ )
		{			
			Color color = this.wayColors[ i % this.wayColors.length ]; 
			
			BasicPainter2D.line( 0, i * this.wayHeight + railLoc - railThinck / 2
												, this.spriteSize.width, i * this.wayHeight + railLoc - railThinck / 2, railThinck, color, this.stave );
		}
	}
	

	@Override
	protected BufferedImage createSprite() 
	{	
		return this.stave;
	}

	@Override
	protected void updateSpecificSprite() 
	{	
	}
}
