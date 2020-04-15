/**
 * 
 */
package control.events;

import java.util.EventObject;

/**
 * @author manuel
 *
 */
public class InputControllerEvent extends EventObject 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3231082679807242646L;
	/**
	 * @author Manuel Merino Monge
	 *
	 */
	
	private double[] inValues;
	
	private int typeEvent;
	
	public InputControllerEvent( Object source, double[] values ) 
	{
		super( source );
		
		this.inValues = values;
	}	
	
	public int getType()
	{
		return this.typeEvent;
	}
	
	public double[] getInputValues()
	{
		return this.inValues;
	}
}
