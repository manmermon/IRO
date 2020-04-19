/**
 * 
 */
package GUI.game.component.sprite;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import GUI.AppIcon;
import image.basicPainter2D;
import image.icon.GeneralAppIcon;
import image.icon.MusicInstrumentIcons;

/**
 * @author manuel
 *
 */
public class Score extends AbstractSprite
{

	private FontMetrics fontmetrics;
	
	private int score = 0;
	
	private int scoreUnit = 100;
	
	/**
	 * 
	 */
	public Score( String id, Pentragram pen, int scoreUnitValue )
	{
		super( id );
		
		this.scoreUnit = scoreUnitValue;
		
		int h = pen.getRailHeight() / 2;
		
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
		

		Point loc = pen.getBounds().getLocation();
		super.screenLoc.x = loc.x + fm.stringWidth( "0" );
		super.screenLoc.y = loc.y + h / 8;
		
		
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
		Color border = Color.RED;
		Color fill = Color.ORANGE;
		
		String txt = String.format( "%06d", this.score );
		BufferedImage sprite = (BufferedImage)basicPainter2D.text( txt, this.fontmetrics
													, border
													, fill
													, null );
		
		Color bg = new Color( 255, 255, 255, 140 );
		BufferedImage img = ( BufferedImage )basicPainter2D.roundRectangle(0, 0
																	, sprite.getWidth()
																	, sprite.getHeight()
																	, this.fontmetrics.stringWidth( "0")
																	, sprite.getHeight() 
																	, 1 , bg, bg, null );
		
		//BufferedImage img = (BufferedImage)basicPainter2D.createEmptyCanva( sprite.getWidth(), sprite.getHeight(), bg );
		basicPainter2D.composeImage( img, 0, 0, sprite );
				
		return img;
	}
	
	public void incrementScore()
	{
		this.score += this.scoreUnit;
	}
	
	public void setScoreUnit( int unit )
	{
		this.scoreUnit = unit;
	}
}
