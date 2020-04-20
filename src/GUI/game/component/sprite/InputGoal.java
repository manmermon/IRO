/**
 * 
 */
package GUI.game.component.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import image.basicPainter2D;
import image.icon.GeneralAppIcon;

/**
 * @author manuel
 *
 */
public class InputGoal extends AbstractSprite
{

	private Double percentage = 0D;
	
	private final int startAngle = 90;
	
	private final double reachedTargetShowingTime = 1.5D;
	
	private long startTime = Long.MIN_VALUE;
	
	private boolean isSetStartTime = false;
	
	private BufferedImage reachedTarget = null;
	
	
	/**
	 * 
	 */
	public InputGoal( String id, Pentragram pen  )
	{
		super( id );
		
		super.spriteSize = new Dimension( pen.getRailHeight(), pen.getRailHeight() ); 
		double centerX = pen.getScreenLocation().x + pen.getPentragramWidth() / 2;
		
		super.screenLoc.x = centerX - super.spriteSize.width / 2;
		super.screenLoc.y = pen.getScreenLocation().y + pen.getRailHeight() / 2;
		
		this.reachedTarget = (BufferedImage)basicPainter2D.circle( 0, 0
																	, super.spriteSize.width
																	, Color.WHITE, null );

		basicPainter2D.composeImage( this.reachedTarget, 0, 0 
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
	public BufferedImage getSprite()
	{
		BufferedImage sprite = null;
		
		synchronized ( this.percentage )
		{
			if( this.percentage > 0 )
			{
				if( percentage < 100 )
				{
					sprite = (BufferedImage)basicPainter2D.arc(0, 0
															, super.spriteSize.width
															, super.spriteSize.height
															, this.startAngle
															, (int)( -360 * this.percentage / 100 )
															, super.spriteSize.width / 8
															, new Color( 130 , 255, 130 )
															, null, null );
				}				
				else if( !this.isSetStartTime )
				{
					this.isSetStartTime = true;
					this.startTime = System.nanoTime();
				}
			}
			
			if( this.isSetStartTime )
			{
				if( ( ( System.nanoTime() - this.startTime ) / 1e9D ) < this.reachedTargetShowingTime )
				{
					if( sprite != null )
					{
						sprite = (BufferedImage)basicPainter2D.composeImage( basicPainter2D.copyImage( this.reachedTarget ), 0, 0, sprite );
					}
					else
					{
						sprite = this.reachedTarget;
					}
				}
				else
				{
					this.percentage = 0D;
					this.isSetStartTime = false;
				}
			}			
		}
		
		return sprite;
	}

	public void setPercentage( double percentage )
	{
		synchronized ( this.percentage )
		{
			this.percentage = percentage;
			
			if( this.percentage >= 100 && this.isSetStartTime )
			{
				this.isSetStartTime = false;
			}
		}
	}
	
}
