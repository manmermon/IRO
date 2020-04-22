/**
 * 
 */
package GUI.game.component;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import GUI.LevelProgressIndicator;
import GUI.TwoWayProgressBar;
import config.ConfigApp;
import config.ConfigParameter;
import control.events.IInputControllerListener;

/**
 * @author manuel
 *
 */
public class ControllerTargetBar extends JPanel implements IInputControllerListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9033821662920318657L;

	private LevelProgressIndicator pgbar = null;
	
	private int selectedChannel = 0;
	
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
	
	public void setMinimum( double min )
	{		
		this.pgbar.setMinimum( min  );
	}
	
	public void setMaximum( double max )
	{
		pgbar.setMaximum( max );
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
	public void InputControllerEvent(control.events.InputControllerEvent ev)
	{
		double[] data = ev.getInputValues();
		
		if( data != null 
				&& data.length > 0 
				&& this.selectedChannel >= 0 
				&& this.selectedChannel < data.length )
		{
			double value = data[ this.selectedChannel ];
			
			this.pgbar.setValue( value );
		}
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
