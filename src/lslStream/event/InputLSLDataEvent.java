package lslStream.event;

import java.util.EventObject;

public class InputLSLDataEvent extends EventObject 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7232322653215504302L;

	private double[] inValues;
	
	private double time;
	
	private int typeEvent;
	
	public InputLSLDataEvent( Object source, double[] values, double time ) 
	{
		super( source );
		
		this.inValues = values;
		this.time = time;
	}	
	
	public int getType()
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
