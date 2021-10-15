/**
 * 
 */
package control.controller;

import config.Player;
import general.NumberRange;

/**
 * @author manuel
 *
 */
public interface IControllerMetadata
{
	public enum ControllerType { UNKNOWN, LSLSTREAM };
	
	public ControllerType getControllerType();
	
	public double getSamplingRate();
	
	public Object getControllerSetting();
	
	public String getControllerID();
	
	public int getNumberOfChannels();
	
	public int getSelectedChannel();
	
	public void setSelectedChannel( int selectedChannel );
	
	public String getName();
	
	public void setRecoverInputLevel( double value );
	
	public double getRecoverInputLevel();
	
	public void setActionInputLevel( NumberRange actionRange );
	
	public NumberRange getActionInputLevel( );
	
	public void setTargetTimeInLevelAction( double time );
	
	public double getTargetTimeInLevelAction( );
	
	public void setRepetitions( int rep );
	
	public int getRepetitions( );
	
	public void setPlayer( Player player );
	
	public Player getPlayer();
	
	public String getInfo();
}
