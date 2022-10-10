/**
 * 
 */
package lslInput.stream;

import config.Player;
import lslInput.LSLStreamInfo.StreamType;

/**
 * @author manuel
 *
 */
public interface IInputStreamMetadata
{
	public enum InputSourceType { UNKNOWN, LSLSTREAM };
	
	public InputSourceType getInputSourceType();
	
	public double getSamplingRate();
	
	public Object getInputSourseSetting();
	
	public String getInputSourceID();
	
	public String getName();
	
	public int getNumberOfChannels();
		
	public void setPlayer( Player player );
	
	public Player getPlayer();
	
	public String getInfo();
	
	public StreamType getDataStreamType();
	
	public String getContentType();
}
