/**
 * 
 */
package gui.game.component.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import gui.game.component.IPossessable;
import config.IOwner;
import image.BasicPainter2D;
import image.icon.GeneralAppIcon;
import tools.SceneTools;

/**
 * @author manuel
 *
 */
public class InputGoal extends AbstractSprite implements IPossessable
{
	private Double percentageTime = 0D;
	
	private final int startAngle = 90;
	
	private final double reachedTargetShowingTime = 1.5D;
	
	private long startTime = Long.MIN_VALUE;
	private int repCounter = 0;
		
	private boolean isSetStartTime = false;
	
	private BufferedImage reachedTarget = null;
	
	private IOwner _player;
	
	/**
	 * 
	 */
	public InputGoal( String id, int size, Rectangle bounds )
	{
		super( id );
		
		super.setSize( new Dimension( size, size ) );
		
		double centerX = bounds.getCenterX();
		Point loc = bounds.getLocation();
		
		this.setScreenLocation(new Point2D.Double( centerX - super.spriteSize.width / 2
													, loc.y + size / 2 ) );		
	}
	
	/*(non-Javadoc)
	 * @see @see GUI.game.component.sprite.AbstractSprite#setSize(java.awt.Dimension)
	 */
	@Override
	public void setSize(Dimension size)
	{
		// TODO Auto-generated method stub
		super.setSize(size);
		
		this.reachedTarget = (BufferedImage)BasicPainter2D.circle( 0, 0
																, super.spriteSize.width
																, Color.WHITE, null );
		
		BasicPainter2D.composeImage( this.reachedTarget, 0, 0 
									, GeneralAppIcon.Correct()
												.getImage()
												.getScaledInstance( this.reachedTarget.getWidth()
																	, this.reachedTarget.getHeight()
																	, Image.SCALE_SMOOTH ) );
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
		BufferedImage sprite = null;
		float thinckness = super.spriteSize.width / 8;
		
		synchronized ( this.percentageTime )
		{
			if( this.percentageTime > 0 )
			{
				if( percentageTime < 100 )
				{
					sprite = (BufferedImage)BasicPainter2D.arc(0, 0
															, super.spriteSize.width
															, super.spriteSize.height
															, this.startAngle
															, (int)( -360 * this.percentageTime / 100 )
															, thinckness
															, new Color( 130 , 255, 130 )
															, null, null );
				}				
				else if( this.repCounter == 0 && !this.isSetStartTime )
				{
					this.isSetStartTime = true;
					this.startTime = System.nanoTime();
				}
				
				if( this.repCounter > 0 )
				{
					float txSize = super.spriteSize.width;
					if( sprite != null )
					{
						txSize -= 5 * thinckness;
					}
					
					String tx = "" + this.repCounter;
					sprite = (BufferedImage)BasicPainter2D.text( tx, SceneTools.getFontMetricByWidth( (int)txSize, tx ), Color.BLACK, Color.GREEN, sprite );
				}
			}
			
			if( this.isSetStartTime )
			{
				if( ( ( System.nanoTime() - this.startTime ) / 1e9D ) < this.reachedTargetShowingTime )
				{
					if( sprite != null )
					{
						sprite = (BufferedImage)BasicPainter2D.composeImage( BasicPainter2D.copyImage( this.reachedTarget ), 0, 0, sprite );
					}
					else
					{
						sprite = this.reachedTarget;
					}
				}
				else
				{
					this.percentageTime = 0D;
					this.isSetStartTime = false;
				}
			}			
		}
		
		return sprite;
	}
	
	public void setPercentage( double percentageTime, int rep )
	{
		synchronized ( this.percentageTime )
		{
			this.percentageTime = percentageTime;
			this.repCounter = rep;
			
			if( this.percentageTime >= 100 && this.isSetStartTime )
			{
				this.isSetStartTime = false;
			}
		}
	}

	/*(non-Javadoc)
	 * @see @see GUI.game.component.IPossessable#setOwner(config.IOwner)
	 */
	@Override
	public void setOwner(IOwner owner)
	{
		this._player = owner;
	}

	/*(non-Javadoc)
	 * @see @see GUI.game.component.IPossessable#getOwner()
	 */
	@Override
	public IOwner getOwner()
	{
		return this._player;
	}
	
}
