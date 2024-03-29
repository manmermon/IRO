/**
 * 
 */
package gui.game.component;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import GUI.progressbar.LevelProgressIndicator;
import control.events.IEnabledInputLSLDataListener;

/**
 * @author manuel
 *
 */
public class ControllerTargetBar extends JPanel implements IEnabledInputLSLDataListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9033821662920318657L;

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	
	private LevelProgressIndicator pgbar = null;
	
	private int selectedChannel = 0;
	
	private boolean enableCtr = true;
	
	private int inverted = 1;
	
	/**
	 * 
	 */
	public ControllerTargetBar( int channel )
	{
		super.setLayout( new BorderLayout() );
		
		this.selectedChannel = channel;
		
		this.pgbar = new LevelProgressIndicator( 3 );
		this.pgbar.setPaintedText( false );
		super.add( this.pgbar );
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

	/*(non-Javadoc)
	 * @see @see control.events.IInputControllerListener#enableProcessInputControllerEvent(boolean)
	 */
	/*
	@Override
	public void enableProcessInputControllerEvent(boolean enable)
	{	
	}
	//*/
}
