/**
 * 
 */
package control.inputStream.biosignal;

import java.util.ArrayList;
import java.util.List;

import config.Player;
import control.events.IEnabledInputLSLDataListener;
import lslInput.event.IInputLSLDataListener;
import lslInput.stream.IInputStreamMetadata;
import lslInput.stream.biosignal.LSLInputBiosignalStream;

/**
 * @author manuel
 *
 */
public class InputBiosignalStreamManager
{
	private static InputBiosignalStreamManager ctr = null;
	
	private List< LSLInputBiosignalStream > inBioStreams = null;
	
	private InputBiosignalStreamManager()
	{
		inBioStreams = new ArrayList< LSLInputBiosignalStream >();
	}
	
	public static InputBiosignalStreamManager getInstance()
	{
		if( ctr == null )
		{
			ctr = new InputBiosignalStreamManager();			
		}
		
		return ctr;
	}
	
	public void startInputBioStream( List< IInputStreamMetadata > controllers ) throws Exception
	{		
		stopInputBiosignalStream();
		
		for( IInputStreamMetadata meta : controllers )
		{
			switch ( meta.getInputSourceType() )
			{
				case LSLSTREAM:
				{
					LSLInputBiosignalStream controller = new LSLInputBiosignalStream( meta );
					this.inBioStreams.add( controller );
					break;
				}
				default:
				{
					break;
				}
			}		
		}
		
		for( LSLInputBiosignalStream bio : this.inBioStreams )
		{
			bio.startActing();
		}
	}
	
	public void stopInputBiosignalStream() throws Exception
	{
		for( LSLInputBiosignalStream bio : this.inBioStreams )
		{
			bio.stopActing( LSLInputBiosignalStream.FORCE_STOP );
		}
		
		this.inBioStreams.clear();
	}
	
	public void addInputBiosignalStreamListener( Player player, IEnabledInputLSLDataListener listener )
	{
		for( LSLInputBiosignalStream bio : this.inBioStreams )
		{
			if( bio.getMetadataBiosignalStream().getPlayer().equals( player ) )
			{
				bio.addInputLSLDataListener( listener );
			}
		}
	}
	
	public void removeInputBiosignalStreamListener( Player player, IEnabledInputLSLDataListener listener )
	{
		for( LSLInputBiosignalStream bio : this.inBioStreams )
		{
			if( bio.getMetadataBiosignalStream().getPlayer().equals( player ) )
			{
				bio.removeInputLSLDataListener( listener );
			}
		}
	}
		
	public void setEnableInputBiosignalStreamListener( boolean ena )
	{
		for( LSLInputBiosignalStream bio : this.inBioStreams )
		{
			for( IInputLSLDataListener listener : bio.getListener() )
			{
				if( listener instanceof IEnabledInputLSLDataListener )
				{
					((IEnabledInputLSLDataListener)listener).setEnableInputStream( ena );
				}
			}
		}
	}
	
	public List< LSLInputBiosignalStream > getInputBiosignalStreams()
	{
		return this.inBioStreams;
	}
}
