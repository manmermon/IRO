/**
 * 
 */
package lslInput.stream.controller;

import lslInput.stream.IInputStreamMetadata;

/**
 * @author manuel
 *
 */
public interface IControllerMetadata extends IInputStreamMetadata
{	
	public void setSelectedChannel( int selectedChannel );
	
	public int getSelectedChannel();
		
	public void setRecoverInputLevel( double value );
	
	public double getRecoverInputLevel();
	
	public void setActionInputLevel( double actionRange );
	
	public double getActionInputLevel( );
	
	public void setTargetTimeInLevelAction( double time );
	
	public double getTargetTimeInLevelAction( );
		
	public void setRepetitions( int rep );
	
	public int getRepetitions( );		
}
