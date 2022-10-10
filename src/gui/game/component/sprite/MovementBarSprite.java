/**
 * 
 */
package gui.game.component.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import GUI.progressbar.LevelProgressIndicator;
import config.IOwner;
import control.events.IEnabledInputLSLDataListener;
import gui.game.component.IPossessable;

/**
 * @author manuel
 *
 */
public class MovementBarSprite extends AbstractSprite 
									implements IEnabledInputLSLDataListener, IPossessable
{

	/**
	 * 
	 */
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	
	private LevelProgressIndicator pgbar = null;
	
	private int selectedChannel = 0;
	
	private boolean enableCtr = true;
	
	private int inverted = 1;
	
	private IOwner owner = null;
	
	/**
	 * 
	 */
	public MovementBarSprite( String idSprite, int channel )
	{
		super( idSprite );
		
		this.selectedChannel = channel;
		
		this.pgbar = new LevelProgressIndicator( 3 );
		this.pgbar.setPaintedText( false );
		this.pgbar.setVisible( true );
		
		Border l1 = new LineBorder( Color.BLACK, 2 );
		Border l2 = new LineBorder( Color.WHITE, 2 );
		Border border = BorderFactory.createCompoundBorder( l1, l2 );
		this.pgbar.setBorder( border );
	}
	
	public void setOrientation( int orientation )
	{
		this.pgbar.setOrientation( orientation );
	}
	
	public void setMinimum( double min )
	{		
		this.pgbar.setMinimum( min  );
	}
	
	public void setMaximum( double max )
	{
		pgbar.setMaximum( max );
	}
	
	public void setInvetedValues( boolean inverted )
	{
		this.inverted = 1;
		
		if( inverted )
		{
			this.inverted = -1;
		}
	}
	
	@Override
	public void setSize(Dimension size) 
	{
		super.setSize( size );
		
		this.pgbar.setVisible( false );
		this.pgbar.setPreferredSize( size );
		this.pgbar.setSize( size );
		this.pgbar.setVisible( true );
	}
	
	public void setLevels( double[] levels )
	{
		pgbar.setLevels( levels );
	}
	
	public void setLevelColor( Color[] colors )
	{
		this.pgbar.setLevelColors( colors );
	}
	
	/*(non-Javadoc)
	 * @see @see control.events.IInputControllerListener#InputControllerEvent(control.events.InputControllerEvent)
	 */
	@Override
	public void InputLSLDataEvent(lslInput.event.InputLSLDataEvent ev) 
	{
		double[] data = ev.getInputValues();
		
		if( this.enableCtr 
				&& data != null 
				&& data.length > 0 
				&& this.selectedChannel >= 0 
				&& this.selectedChannel < data.length )
		{
			double value = data[ this.selectedChannel ];			
			value *= this.inverted;
			
			this.pgbar.setValue( value  );
		}
	}

	@Override
	public void setEnableInputStream( boolean enable) 
	{
		this.enableCtr = enable;
	}

	@Override
	public void close() 
	{
		this.setEnableInputStream( false );
	}

	@Override
	protected void updateSpecificSprite() 
	{	
	}

	@Override
	protected BufferedImage createSprite() 
	{		
		BufferedImage image = null;
		
		int w = this.pgbar.getWidth();
		int h = this.pgbar.getHeight();

		if( w > 0 && h > 0 )
		{
			image = new BufferedImage( w, h , BufferedImage.TYPE_INT_ARGB );				 
			// call the Component's paint method, using
			// the Graphics object of the image.				 
			this.pgbar.print( image.getGraphics() );
			//cp.repaint();			
		}
		
		return image;
	}

	@Override
	public void setOwner(IOwner owner) 
	{
		this.owner = owner;
	}

	@Override
	public IOwner getOwner() 
	{
		return this.owner;
	}

}
