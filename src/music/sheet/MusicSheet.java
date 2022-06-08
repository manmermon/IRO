package music.sheet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfugue.theory.Note;

import general.ArrayTreeMap;
import general.NumberRange;
import tools.MusicSheetTools;

public class MusicSheet 
{
	private Map< String, IROTrack > tracks;
	
	private int tempo;	
			
	public MusicSheet() 
	{
		this.tracks = new HashMap< String, IROTrack >();
	}
	
	public IROTrack createNewTrack( String trackID, int voice )
	{
		IROTrack track = this.tracks.get( trackID );
		
		if( track == null )
		{
			track = new IROTrack( trackID );
			
			//track.setVoice( this.tracks.size() );
			track.setVoice( voice );
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
				track = this.createNewTrack( trackID, this.tracks.size() );
			}
			
			suf++;
			trackID = pref + suf;			
		}
				
		return track;
	}
	
	public List< IROTrack > getNotesAtIntervalTime( NumberRange times )
	{
		List< IROTrack > notes = new ArrayList< IROTrack >();
		
		if( this.tracks != null  && times != null )
		{
			for( IROTrack track : this.tracks.values() )
			{
				double wholeDuration = MusicSheetTools.getWholeTempo2Second( track.getTempo() );
				
				ArrayTreeMap< Double, Note > trackNotes = track.getTrackNotes();
				
				IROTrack ctrack = new IROTrack( track.getID() );
				ctrack.setInstrument( track.getInstrument() );
				ctrack.setTempo( track.getTempo() );
				ctrack.setVoice( track.getVoice() );
				
				for( Double t : trackNotes.keySet() )
				{
					double tt = t * wholeDuration;
					if( times.within( tt ) )
					{
						ctrack.addNotes( t, trackNotes.get( t ) );
					}
				}
				
				if( !ctrack.isEmpty() )
				{
					notes.add( ctrack );
				}
			}
		}
		
		return notes;
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
		
		/*
		int voice = 0;		
		for( IROTrack track : this.tracks.values() )
		{
			track.setID( IROTrack.TRACK_ID_DEFAULT_PREFIX + voice );
			track.setVoice( voice );
			
			voice++;
		}
		*/
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
