package gui.game.component.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;

import config.language.Language;
import image.BasicPainter2D;
import tools.SceneTools;

public class Loading extends AbstractSprite 
{		
	private int stepAnim = 0;
	
	private BufferedImage loadTextImage = null;
	
	private BufferedImage circleImage = null;
	
	public Loading( Dimension size, String idSprite )
	{
		super( idSprite );		
		super.setSize( size );
		
		String msg = Language.getLocalCaption( Language.LOADING );
		
		int w = size.width;
		int h = size.height;

		Color border = Color.RED;
		Color fill = Color.ORANGE;
		
		int s = Math.min( w, h ) / 10;
		
		if( s < 5 )
		{
			s = 5;
		}
		
		BufferedImage c1 = (BufferedImage)BasicPainter2D.circle( 0, 0, s, fill, null );
		this.circleImage = (BufferedImage)BasicPainter2D.circumference( 0, 0, s, 2, border, c1 );
		
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
						
		BufferedImage compose = ( BufferedImage )BasicPainter2D.createEmptyCanva( this.loadTextImage.getWidth() + this.circleImage.getWidth() + pad * 4
																				, Math.max( this.loadTextImage.getHeight(),  this.circleImage.getHeight() )
																				, null );
		
		int y =  (compose.getHeight() - this.loadTextImage.getHeight() ) / 2;
		y = ( y < 0 ) ? 0 : y;
		BasicPainter2D.composeImage( compose, 0, y, this.loadTextImage );
		
		y = (compose.getHeight() - this.circleImage.getHeight() ) / 2;		
		BasicPainter2D.composeImage( compose, this.loadTextImage.getWidth() + pad + this.stepAnim * pad, y, this.circleImage );
		
		Dimension size = super.getSize();
		BufferedImage sprite = ( BufferedImage )BasicPainter2D.createEmptyCanva( size.width, size.height, null );
		
		BasicPainter2D.composeImage( sprite
									, ( size.width - compose.getWidth()) /2
									, (size.height - compose.getHeight() ) / 2
									, compose );
		
		return sprite;
	}

}
