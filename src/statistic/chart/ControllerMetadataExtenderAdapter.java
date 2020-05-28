/**
 * 
 */
package statistic.chart;

import control.controller.ControllerMetadataAdapter;

/**
 * @author manuel
 *
 */
public class ControllerMetadataExtenderAdapter extends ControllerMetadataAdapter
{
	public void setSamplingRate( double rate )
	{
		super.samplingRate = rate;
	}

	public void setControllerID( String id )
	{
		this.id = id;
	}

	public void setNumberOfChannels( int nch )
	{
		this.numberOfChannels = nch;
	}

	public void setName( String name )
	{
		super.name = name;
	}
	public void setInfo( String desc)
	{
		this.info = desc;
	}
}
