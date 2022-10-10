/**
 * 
 */
package control.inputStream.controller;

import java.util.ArrayList;
import java.util.List;

import config.Player;
import control.events.IEnabledInputLSLDataListener;
import lslInput.stream.controller.IControllerMetadata;
import lslInput.stream.controller.LSLInputControllerStream;
import lslInput.stream.controller.LSLMetadataController;

/**
 * @author manuel
 *
 */
public class ControllerManager
{
	private static ControllerManager ctr = null;
	
	private List< IInputController > controllers = null;
	
	private ControllerManager()
	{
		controllers = new ArrayList<IInputController>();
	}
	
	public static ControllerManager getInstance()
	{
		if( ctr == null )
		{
			ctr = new ControllerManager();			
		}
		
		return ctr;
	}
	
	public void startController( List< IControllerMetadata > controllers ) throws Exception
	{		
		stopController();
		
		for( IControllerMetadata meta : controllers )
		{
			switch ( meta.getInputSourceType() )
			{
				case LSLSTREAM:
				{
					IInputController controller = new LSLInputControllerStream( (LSLMetadataController)meta );
					this.controllers.add( controller );
					break;
				}
				default:
				{
					break;
				}
			}		
		}
		
		for( IInputController controller : this.controllers )
		{
			controller.startController();
		}
	}
	
	public void stopController() throws Exception
	{
		for( IInputController controller : this.controllers )
		{
			controller.stopController();
		}
		
		this.controllers.clear();
	}
	
	public void addControllerListener( Player player, IEnabledInputLSLDataListener listener )
	{
		for( IInputController controller : this.controllers )
		{
			if( controller.getMetadataController().getPlayer().equals( player ) )
			{
				controller.addInputControllerListener( listener );
			}
		}
	}
	
	public void removeControllerListener( Player player, IEnabledInputLSLDataListener listener )
	{
		for( IInputController controller : this.controllers )
		{
			if( controller.getMetadataController().getPlayer().equals( player ) )
			{
				controller.removeInputControllerListener( listener );
			}
		}
	}
		
	public void setEnableControllerListener( boolean ena )
	{
		for( IInputController controller : this.controllers )
		{
			for( IEnabledInputLSLDataListener listener : controller.getListener() )
			{
				listener.setEnableInputStream( ena );
			}
		}
	}
	
	public List< IInputController > getControllers()
	{
		return this.controllers;
	}
}
