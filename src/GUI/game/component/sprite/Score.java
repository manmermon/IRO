/**
 * 
 */
package GUI.game.component.sprite;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import GUI.game.component.IPossessable;
import config.IOwner;
import image.basicPainter2D;
import statistic.RegistrarStatistic;

/**
 * @author manuel
 *
 */
public class Score extends AbstractSprite implements IPossessable 
{

	private FontMetrics fontmetrics;
	
	private int score = 0;
	
	private int scoreUnit = 100;
	
	private IOwner _player;
	
	private Image playerPic = null;
	
	private final String scoreFormat = "%06d";

	/**
	 * 
	 */
	public Score( String id, int value, int scoreUnitValue, int h, Point loc )
	{
		super( id );
		
		this.score = value;		
		
		this.scoreUnit = scoreUnitValue;
		
		//int h = pen.getRailHeight() / 2;
		
		super.spriteSize.height = h;

		this.setFontSize();

		//Point loc = pen.getBounds().getLocation();
		super.screenLoc.x = loc.x + this.fontmetrics.stringWidth( "0" ) / 2;
		super.screenLoc.y = loc.y + h / 8;
		
		super.spriteSize.width = this.fontmetrics.stringWidth( String.format( this.scoreFormat,  this.score ) );
	}
	
	private void setFontSize()
	{
		int h = super.spriteSize.height;
		
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
		
		String txt = String.format( this.scoreFormat, this.score );
		BufferedImage sprite = (BufferedImage)basicPainter2D.text( txt, this.fontmetrics
																	, border
																	, fill
																	, null );
		
		
		if( this._player != null && this.playerPic == null )
		{
			this.playerPic = this._player.getOwnerImage().getScaledInstance( sprite.getHeight(), sprite.getHeight(),  Image.SCALE_FAST );
		}
		
		Color bg = new Color( 255, 255, 255, 140 );
		BufferedImage img = ( BufferedImage )basicPainter2D.createEmptyCanva( sprite.getWidth() + this.playerPic.getWidth( null ), sprite.getHeight(), null );
		basicPainter2D.roundRectangle( this.playerPic.getWidth( null ), 0
										, sprite.getWidth() 
										, sprite.getHeight() 
										, this.fontmetrics.stringWidth( "0")
										, sprite.getHeight() 
										, 1 , bg, bg, img );
		
		//BufferedImage img = (BufferedImage)basicPainter2D.createEmptyCanva( sprite.getWidth(), sprite.getHeight(), bg );
		basicPainter2D.composeImage( img, this.playerPic.getWidth( null ), 0, sprite );
		basicPainter2D.composeImage( img, 0, 0, playerPic );
				
		return img;
	}
	
	public void incrementScore()
	{
		this.score += this.scoreUnit;
		
		if( this._player != null )
		{
			RegistrarStatistic.setPlayerScore( this._player.getId(), score );
		}
	}
	
	public void setScoreUnit( int unit )
	{
		this.scoreUnit = unit;
	}

	/*(non-Javadoc)
	 * @see @see config.IPossessable#setOwner(config.IOwner)
	 */
	@Override
	public void setOwner( IOwner owner )
	{
		if( this._player != null && this._player.getOwnerImage() != null )
		{
			super.spriteSize.width -= this._player.getOwnerImage().getWidth( null );
		}
				
		this._player = owner;
		
		if( this._player != null && this._player.getOwnerImage() != null )
		{
			super.spriteSize.width += this._player.getOwnerImage().getWidth( null );
		}
	}

	/*(non-Javadoc)
	 * @see @see config.IPossessable#getOwner()
	 */
	@Override
	public IOwner getOwner()
	{
		return this._player;
	}
}
