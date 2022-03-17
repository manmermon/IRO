package gui.game.component.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import config.language.Language;
import image.BasicPainter2D;
import tools.SceneTools;

public class Transition extends AbstractSprite 
{		
	private int stepAnim = 0;
	
	private BufferedImage loadTextImage = null;
	
	private BufferedImage circleImage = null;
	
	private BufferedImage back = null;
	
	private String msg = null;
	
	private Color border = Color.RED;
	private Color fill = Color.ORANGE;
	
	public Transition( Dimension size, String idSprite, Image bg )
	{
		super( idSprite );		
		super.setSize( size );
		
		//String msg = Language.getLocalCaption( Language.LOADING );
		
		if( bg != null )
		{
			this.back = BasicPainter2D.copyImage( bg.getScaledInstance( size.width, size.height, Image.SCALE_SMOOTH ) );
		}
		
		int w = size.width;
		int h = size.height;
		
		int s = Math.min( w, h ) / 10;
		
		if( s < 5 )
		{
			s = 5;
		}
		
		BufferedImage c1 = (BufferedImage)BasicPainter2D.circle( 0, 0, s, fill, null );
		this.circleImage = (BufferedImage)BasicPainter2D.circumference( 0, 0, s, 2, border, c1 );		
	}
	
	public void setMessage( String msg )
	{
		this.msg = msg;
		
		int w = super.getSize().width;
		int h = super.getSize().height;
		
		FontMetrics fontmetrics = SceneTools.getFontMetricByHeight(  Math.min( w, h ) /4  );

		this.loadTextImage = (BufferedImage)BasicPainter2D.text( msg, fontmetrics, border, fill, null );
	}

	@Override
	protected void updateSpecificSprite() 
	{
		this.stepAnim = ( this.stepAnim + 1 ) % 3;
	}

	@Override
	protected BufferedImage createSprite() 
	{	
		int pad = this.circleImage.getWidth();
		
		/*
		BufferedImage compose = ( BufferedImage )BasicPainter2D.createEmptyCanva( this.loadTextImage.getWidth() + this.circleImage.getWidth() + pad * 4
																				, Math.max( this.loadTextImage.getHeight(),  this.circleImage.getHeight() )
																				, new Color( 255, 255, 255, 127 ) );
		//*/
		
		int wT = 0, hT = 0;
		
		if( this.loadTextImage != null )
		{
			wT = this.loadTextImage.getWidth();
			hT = this.loadTextImage.getHeight();
		}
		
		int wc = wT + this.circleImage.getWidth() + pad * 4;
		int hc = Math.max( hT,  this.circleImage.getHeight() );
		BufferedImage compose = ( BufferedImage )BasicPainter2D.roundRectangle( 0, 0
																				, wc, hc
																				, wc / 10, hc / 2  
																				, 1F
																				, new Color( 255, 255, 255, 150 )
																				, new Color( 255, 255, 255, 150 )
																				, null );
				
		int y =  (compose.getHeight() - hT ) / 2;
		y = ( y < 0 ) ? 0 : y;
		BasicPainter2D.composeImage( compose, 0, y, this.loadTextImage );
		
		y = (compose.getHeight() - this.circleImage.getHeight() ) / 2;		
		BasicPainter2D.composeImage( compose, wT + pad + this.stepAnim * pad, y, this.circleImage );
		
		Dimension size = super.getSize();
		
		BufferedImage sprite = null;
		
		if( this.back == null )
		{
			sprite = (BufferedImage)BasicPainter2D.createEmptyCanva( size.width, size.height, null );
		}
		else
		{
			sprite = BasicPainter2D.copyImage( this.back );
		}
		
		BasicPainter2D.composeImage( sprite
									, ( size.width - compose.getWidth()) /2
									, (size.height - compose.getHeight() ) / 2
									, compose );
		
		return sprite;
	}

}
