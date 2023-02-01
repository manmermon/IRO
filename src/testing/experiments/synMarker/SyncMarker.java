package testing.experiments.synMarker;

import config.ConfigApp;
import lslInput.LSLStreamInfo;

public class SyncMarker
{
	private static SyncMarker markers = null;

	public enum Marker { NO_MARKER, START_TEST, STOP_TEST, SAM_TEST, START_MUSIC, PAUSE, SCORE_SCREEN, BEEP  } 
	
	private StreamOutlet stream;	
	
	private SyncMarker( String id ) 
	{
		try
		{
			LSLStreamInfo info = new LSLStreamInfo( id
													, "Sync Markers"
													, 1
													, 0
													, LSLStreamInfo.StreamDataType.int32.ordinal()
													, ConfigApp.shortNameApp
													);
			
			String markerDesc = "markers[";
			for( Marker m : Marker.values() )
			{
				markerDesc += m.name() + "=" + m.ordinal() + ";";
			}
			markerDesc += "]";
			
			info.desc().append_child_value( "markers", markerDesc );
					
			this.stream = new StreamOutlet( info );
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static SyncMarker getInstance( String id )
	{
		if( markers == null )
		{
			markers = new SyncMarker( id );
		}			
		
		return markers;
	}

	public void sendMarker( Marker marker )
	{
		if( marker != null )
		{
			this.stream.push_sample( new int[] { marker.ordinal() } );
		}
	}
	
}
