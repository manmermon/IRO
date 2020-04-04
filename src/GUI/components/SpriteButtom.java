package GUI.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import image.basicPainter2D;


public class SpriteButtom extends AbstractSprite
{	
	private RoundRectangle2D poly;
	
	/**
	 * 
	 */
	private String text;
		
	public SpriteButtom() 
	{
		super( );
		
		this.setButtomPolygon();
	}
			
	public SpriteButtom( String txt )
	{
		this( );
		this.text = txt;
	}
	
	public String getText( )
	{
		return this.text;
	}
	
	public void setText( String txt )
	{
		this.text = txt;
	}
	
	@Override
	public void setSize(Dimension size) 
	{
		super.setSize( size );
		
		this.setButtomPolygon();
	}
	
	private void setButtomPolygon()
	{
		this.poly = new RoundRectangle2D.Double( 0, 0, super.spriteSize.width, super.spriteSize.height, super.spriteSize.height / 4, super.spriteSize.width/4 );
	}
	
	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#getSprite()
	 */
	@Override
	public BufferedImage getSprite()
	{		
		Image img = null;
		if( super.spriteSize.width > 0 && super.spriteSize.height > 0 )
		{
			img = basicPainter2D.paintFigure( 0, 0, this.poly, Color.RED, null );
			basicPainter2D.paintOutlineFigure( 0, 0, this.poly, Color.BLACK, 2, img );
			
			basicPainter2D.text( this.text, null, Color.YELLOW, Color.ORANGE, img );
		}
		
		return (BufferedImage)img;
	}

	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#updateSprite()
	 */
	@Override
	public void updateSpecificSprite() 
	{		
	}

}
