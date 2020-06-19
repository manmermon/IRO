package GUI.game.component.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import control.controller.IInputable;
import control.events.InputActionEvent;
import image.basicPainter2D;

public class Buttom extends AbstractSprite implements IInputable
{	
	private RoundRectangle2D poly;
	private RoundRectangle2D screenPoly;
	private FontMetrics fontmetric;
	
	private final Color defaultBgColor = Color.ORANGE;
	private final Color defaultFgColor = Color.BLACK;
	
	private Color bgColor;
	private Color fgColor;
	
	private Color currentBgColor;
	private Color currentFgColor;
	
	/**
	 * 
	 */
	private String text;
	
	public Buttom( String id )
	{
		super( id );
		
		this.setButtomPolygon();
				
		this.bgColor = this.defaultBgColor;		
		this.fgColor = this.defaultFgColor;
		
		this.currentBgColor = this.bgColor;
		this.currentFgColor = this.fgColor;
	}
	
	
	
	public void setBackgroundColor( Color c )
	{
		this.bgColor = c;
		
		if( c == null )
		{
			this.bgColor = this.defaultBgColor;
			this.currentBgColor = this.bgColor;
		}
	}
	
	public Color getBackgroundColor()
	{
		return this.bgColor;
	}
	
	public void setForegroundColor( Color c )
	{
		this.fgColor = c;
		
		if( c == null )
		{
			this.fgColor = this.defaultFgColor;
			this.currentFgColor = this.fgColor;
		}
	}
	
	public Color getForegroundColor()
	{
		return this.fgColor;
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
		this.setButtomScreenPolygon();
	}
	
	@Override
	public void setScreenLocation(java.awt.geom.Point2D.Double loc)
	{
		super.setScreenLocation(loc);
		
		this.setButtomScreenPolygon();
	}
	
	private void setButtomPolygon()
	{		
		this.poly = new RoundRectangle2D.Double( 0, 0
												, super.spriteSize.width
												, super.spriteSize.height
												, super.spriteSize.height
												, super.spriteSize.width  );		
	}
	
	private void setButtomScreenPolygon()
	{
		Point2D.Double loc = super.getScreenLocation();
		
		this.screenPoly = new RoundRectangle2D.Double( loc.x, loc.y
												, super.spriteSize.width
												, super.spriteSize.height
												, super.spriteSize.height
												, super.spriteSize.width ); 
				
	}
	
	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#getSprite()
	 */
	@Override
	protected BufferedImage createSprite()
	{		
		Image img = null;
		if( super.spriteSize.width > 0 && super.spriteSize.height > 0 )
		{
			img = basicPainter2D.paintFigure( 0, 0, this.poly, this.currentBgColor, null );
			basicPainter2D.paintOutlineFigure( 0, 0, this.poly, this.currentBgColor.darker(), 2, img );
			
			if( this.text != null )
			{
				if( this.fontmetric == null )
				{
					this.setFontMetric( (BufferedImage)img );
				}
				
				basicPainter2D.text( this.text, this.fontmetric, this.currentFgColor.darker(), this.fgColor, img );
			}			
		}
		
		return (BufferedImage)img;
	}
	
	private void setFontMetric( BufferedImage img )
	{
		if( img != null )
		{
			
			Font f = img.getGraphics().getFont();
					
			boolean rep = true;
			do
			{	
				f = new Font( f.getFontName(), f.getStyle(), f.getSize() + 5 );
				Shape shapeText = basicPainter2D.generateShapeFromText( (int)this.poly.getX(), (int)this.poly.getY()
																, (int)this.poly.getWidth(), (int)this.poly.getHeight()
																, f, this.text );
				
				Rectangle2D shapeBounds = shapeText.getBounds2D();
				Double xcenter = this.poly.getCenterX();
				Double ycenter = this.poly.getCenterY();
				Rectangle2D.Double textBounds = new Rectangle2D.Double( xcenter - shapeBounds.getWidth() / 2
															, ycenter - shapeBounds.getHeight() / 2
															, shapeBounds.getWidth()
															, shapeBounds.getHeight() );
				rep = this.poly.contains( textBounds );
			}
			while( rep );
			
			f = new Font( f.getFontName(), f.getStyle(), f.getSize() - 8 );
			this.fontmetric = img.getGraphics().getFontMetrics( f );			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#updateSprite()
	 */
	@Override
	public void updateSpecificSprite() 
	{		
	}



	/*(non-Javadoc)
	 * @see @see control.controller.IInputable#action(control.controller.IInputAction)
	 */
	@Override
	public void action( InputActionEvent act)
	{
		// TODO Auto-generated method stub
		
	}	
}
