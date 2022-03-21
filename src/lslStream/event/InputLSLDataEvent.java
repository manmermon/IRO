package lslStream.event;

import java.util.EventObject;

public class InputLSLDataEvent extends EventObject 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7232322653215504302L;

	public enum LSLDataEventType { ERROR, DATA, STOP }
	
	private double[] inValues;
	
	private double time;
	
	private LSLDataEventType typeEvent;
	
	public InputLSLDataEvent( Object source, LSLDataEventType t, double[] values, double time ) 
	{
		super( source );
		
		this.inValues = values;
		this.time = time;
		
		this.typeEvent = t;
	}	
	
	public LSLDataEventType getType()
	{
		return this.typeEvent;
	}
	
	public double[] getInputValues()
	{
		return this.inValues;
	}
	
	/**
	 * @return the time
	 */
	public double getTime()
	{
		return this.time;
	}
}
