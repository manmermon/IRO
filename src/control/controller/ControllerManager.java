/**
 * 
 */
package control.controller;

import control.controller.LSLStreams.InputLSLStreamController;
import control.events.IInputControllerListener;
import edu.ucsd.sccn.LSL;

/**
 * @author manuel
 *
 */
public class ControllerManager
{
	private static ControllerManager ctr = null;
	
	private static IInputController controller = null;
	
	private ControllerManager()
	{
		
	}
	
	public static ControllerManager getInstance()
	{
		if( ctr == null )
		{
			ctr = new ControllerManager();
		}
		
		return ctr;
	}
	
	public void startController( LSL.StreamInfo info ) throws Exception
	{		
		stopController();
		
		controller = new InputLSLStreamController( info );
				
		controller.startController();
	}
	
	public void stopController() throws Exception
	{
		if( controller != null )
		{
			controller.stopController();
		}
		
		controller = null;
	}
	
	public void addControllerListener( IInputControllerListener listener )
	{
		if( controller != null )
		{
			controller.addInputControllerListener( listener );
		}
	}
	
	public void removeControllerListener( IInputControllerListener listener )
	{
		if( controller != null )
		{
			controller.removeInputControllerListener( listener );
		}
	}
	
	/*
	public void setEnableControllerListener( boolean ena )
	{
		if( controller != null )
		{
			for( IInputControllerListener listener : controller.getListener() )
			{
				listener.enableProcessInputControllerEvent( ena );
			}
		}
	}
	//*/
		
	public IInputController getController()
	{
		return controller;
	}
}
