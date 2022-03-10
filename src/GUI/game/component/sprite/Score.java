/**
 * 
 */
package gui.game.component.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import gui.game.component.IPossessable;
import config.IOwner;
import image.BasicPainter2D;
import statistic.RegistrarStatistic;
import tools.SceneTools;

/**
 * @author manuel
 *
 */
public class Score extends AbstractSprite implements IPossessable 
{

	private FontMetrics fontmetrics;
	
	private double score = 0;
	
	private double scoreUnit = 100D;
	
	private IOwner _player;
	
	private Image playerPic = null;
	
	private final String scoreFormat = "%06.0f";

	/**
	 * 
	 */
	public Score( String id, double value, double scoreUnitValue, int h, Point loc )
	{
		super( id );
		
		this.score = value;		
		
		this.scoreUnit = scoreUnitValue;
		
		//int h = pen.getRailHeight() / 2;
		
		super.spriteSize.height = h;

		this.fontmetrics = SceneTools.getFontMetricByHeight( super.spriteSize.height );

		//Point loc = pen.getBounds().getLocation();
		super.screenLoc.x = loc.x + this.fontmetrics.stringWidth( "0" ) / 2;
		super.screenLoc.y = loc.y + h / 8;
		
		super.spriteSize.width = this.fontmetrics.stringWidth( String.format( this.scoreFormat,  this.score ) );
	}
	
	@Override
	public Dimension getSize() 
	{
		Dimension d = super.getSize();
		
		if( this._player != null && this.playerPic == null )
		{
			d.width += d.height;
		}
		
		return d;
	}
	
	public void setScore(double score) 
	{
		this.score = score;
	}
	
	public double getScore() 
	{
		return score;
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
	protected BufferedImage createSprite()
	{	  
		Color border = Color.RED;
		Color fill = Color.ORANGE;
		
		String txt = String.format( this.scoreFormat, this.score );
		BufferedImage sprite = (BufferedImage)BasicPainter2D.text( txt, this.fontmetrics
																	, border
																	, fill
																	, null );
		
		
		if( this._player != null && this.playerPic == null )
		{
			this.playerPic = this._player.getOwnerImage().getScaledInstance( sprite.getHeight(), sprite.getHeight(),  Image.SCALE_FAST );
		}
		
		Color bg = new Color( 255, 255, 255, 140 );
		BufferedImage img = ( BufferedImage )BasicPainter2D.createEmptyCanva( sprite.getWidth() + this.playerPic.getWidth( null ), sprite.getHeight(), null );
		BasicPainter2D.roundRectangle( this.playerPic.getWidth( null ), 0
										, sprite.getWidth() 
										, sprite.getHeight() 
										, this.fontmetrics.stringWidth( "0")
										, sprite.getHeight() 
										, 1 , bg, bg, img );
		
		//BufferedImage img = (BufferedImage)basicPainter2D.createEmptyCanva( sprite.getWidth(), sprite.getHeight(), bg );
		BasicPainter2D.composeImage( img, this.playerPic.getWidth( null ), 0, sprite );
		BasicPainter2D.composeImage( img, 0, 0, playerPic );
				
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
	
	public void setScoreUnit( double unit )
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
