package music;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.midi.Track;

public class MusicSheet 
{
	private Map< String, IROTrack > tracks;
	private int tempo;	
	
	public MusicSheet() 
	{
		this.tracks = new HashMap< String, IROTrack >();
	}
	
	public IROTrack createNewTrack( String trackID )
	{
		IROTrack track = this.tracks.get( trackID );
		
		if( track == null )
		{
			track = new IROTrack( trackID );
			
			track.setVoice( this.tracks.size() );
			track.setTempo( this.tempo );
			
			this.tracks.put( trackID, track );
		}
		
		return track;
	}
	
	public IROTrack createNewTrack( )
	{
		String pref = "track-";
		int suf = this.tracks.size();
		
		String trackID = pref + suf;
		
		IROTrack track = null;
		
		while( track == null )
		{
			if( !this.tracks.containsKey( trackID ) )
			{
				track = this.createNewTrack( trackID );
			}
			
			suf++;
			trackID = pref + suf;			
		}
				
		return track;
	}
	
	public void setTempo( int tempo )
	{
		this.tempo = tempo;
		
		for( IROTrack track : this.tracks.values() )
		{
			track.setTempo( this.tempo );
		}
	}
	
	public int getTempo( )
	{
		return this.tempo;
	}
	
	public int getNumberOfTracks()
	{
		return this.tracks.size();
	}
	
	public IROTrack getTrack( String trackID )
	{
		return this.tracks.get( trackID );
	}
	
	public Collection< IROTrack > getTracks()
	{
		return this.tracks.values();
	}
	
	public void packTracks()
	{
		Iterator< Entry< String, IROTrack > > itTracks = this.tracks.entrySet().iterator();
		
		while( itTracks.hasNext() )
		{
			Entry< String, IROTrack > entryTrack = itTracks.next();
			
			IROTrack track = entryTrack.getValue();
			if( track != null && track.isEmpty() )
			{
				itTracks.remove();
			}			
		}
		
		int voice = 0;		
		for( IROTrack track : this.tracks.values() )
		{
			track.setID( IROTrack.TRACK_ID_DEFAULT_PREFIX + voice );
			track.setVoice( voice );
			
			voice++;
		}
	}

	public double getDuration()
	{
		double t = 0;
		for( IROTrack track : this.tracks.values() )
		{
			if( t < ( track.getStartTimeFirstNote() + track.getTrackDuration() ) )
			{
				t = track.getTrackDuration() + track.getStartTimeFirstNote();
			}
		}
		
		return t;
	}
}
