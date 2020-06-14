/**
 * 
 */
package GUI.game.component.sprite;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import control.music.MusicPlayerControl;
import image.basicPainter2D;

/**
 * @author manuel
 *
 */
public class TimeSession extends AbstractSprite 
{
	private FontMetrics fontmetrics;
	
	/**
	 * @param idSprite
	 */
	public TimeSession( String idSprite, int h, Rectangle bounds )
	{
		super( idSprite );
		
		//int h = pen.getRailHeight() / 2;
		
		super.spriteSize.height = h;
		
		Canvas cv = new Canvas();
		
		Font f = new Font( Font.SANS_SERIF, Font.BOLD, 12 );
		FontMetrics fm = cv.getFontMetrics( f );
		
		while( fm.getHeight() < h )
		{
			f = new Font( f.getName(), f.getStyle(), f.getSize() + 1 );
			fm = cv.getFontMetrics( f );
		}
		
		while( fm.getHeight() > h )
		{
			f = new Font( f.getName(), f.getStyle(), f.getSize() - 1 );
			fm = cv.getFontMetrics( f );
		}
		

		//Date date = new Date();
		super.spriteSize.width = fm.stringWidth( "0:00:00" );
		super.spriteSize.height = fm.getHeight();
		
		super.screenLoc.x = bounds.getMaxX() - fm.stringWidth( "0" ) / 2 - super.spriteSize.width;
		super.screenLoc.y = bounds.getMinY() + h / 8;
		
		this.fontmetrics = fm;
	}

	/*(non-Javadoc)
	 * @see @see GUI.game.component.sprite.AbstractSprite#updateSpecificSprite()
	 */
	@Override
	protected void updateSpecificSprite()
	{
	}

	/*(non-Javadoc)
	 * @see @see GUI.game.component.sprite.AbstractSprite#getSprite()
	 */
	@Override
	public BufferedImage getSprite()
	{
		double t = MusicPlayerControl.getInstance().getPlayTime();
		
		int hh = (int)( t / 3600 );
		double mm = t / 60;
		int ss = ((int) t ) % 60;
		
		String text = "";
		if( hh > 0 )
		{
			text += hh + ":";
		}
		
		text += String.format( "%02d", (int)( mm - hh * 60 ) ) + ":" + String.format( "%02d", ss );
		
		BufferedImage img = (BufferedImage)basicPainter2D.text( text 
														, this.fontmetrics, Color.BLACK
														, Color.GREEN, null );

		
		Color bg = new Color( 255, 255, 255, 140 );
		BufferedImage bgpic = ( BufferedImage )basicPainter2D.roundRectangle( 0, 0
																			, img.getWidth()
																			, img.getHeight()
																			, img.getWidth() / 4
																			, img.getHeight()
																			, 1 , bg, bg, null );
		
		basicPainter2D.composeImage( bgpic, 0, 0, img );
		
		
		return bgpic;
	}
	
}