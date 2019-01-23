package GUI.screens.levels;

import java.util.ArrayList;
import java.util.List;

import org.jfugue.theory.Note;

import general.ArrayTreeMap;
import general.NumberRange;
import music.IROTrack;

public class LevelMusicSheetSegment 
{
	private ArrayTreeMap< NumberRange, IROTrack > segments;

	public LevelMusicSheetSegment() 
	{
		this.segments = new ArrayTreeMap< NumberRange, IROTrack >();
	}
	
	public void setTracksTempo( int tempo )
	{
		for( List< IROTrack > Tracks : this.segments.values() )
		{
			for( IROTrack track : Tracks )
			{
				track.setTempo( tempo );
			}
		}
	}
	
	public void setTrackInstrument( String trackID, String instrument )
	{
		for( List< IROTrack > Tracks : this.segments.values() )
		{
			for( IROTrack track : Tracks )
			{
				if( track.getID().equals( trackID ) )
				{
					track.setInstrument( instrument );
				}
			}
		}
	}
	
	/**
	 * Create a new music sheet segment. 
	 * 
	 * @param timeRange - Time interval
	 * @return True if new segment is created. False if it exists
	 * 			overlap with an time interval (segment is not created). 
	 */
	public boolean addNewSegments( NumberRange timeRange )
	{
		boolean add = false;
				
		if( timeRange != null )
		{
			NumberRange timeSegment = this.findSubTimeSegment( timeRange );
			
			add = ( timeSegment == null );
			if( add )
			{
				this.segments.put( timeRange, new ArrayList< IROTrack >() );
			}			
		}		
		
		return add;
	}
	
	public boolean existMusicSegmentInTime( double time )
	{
		boolean exist = false;
		
		for( NumberRange rng : this.segments.keySet() )
		{			
			exist = rng.within( time );
			if( exist )
			{
				break;
			}
		}
		
		return exist;
	}
	
	public boolean addNewTrack( double time, String trackID )
	{
		boolean add = false;

		IROTrack track = this.getTrack( time, trackID );
		
		add = ( track == null );
		if( add )
		{
			NumberRange timeRng = this.getTimeSegment( time );
			track = new IROTrack( trackID );
			
			track.setVoice( this.segments.get( timeRng ).size() );
			
			this.segments.put( timeRng, track );
		}
		
		return add;
	}
	
	public boolean addNotes( double time, String trackID, List< Note > notes )
	{
		boolean add = false;
		
		IROTrack track = this.getTrack( time, trackID );
		add = track != null;
		if( add )
		{
			track.addNotes( time, notes );
		}
		
		return add;
	}

	public boolean isFirstNote( double time, String trackID )
	{
		boolean first = false;
		
		IROTrack track = this.getTrack( time, trackID );
		if( track != null )
		{
			first = track.isEmpty();
		}
		
		return first;
	}
	
	public ArrayTreeMap<NumberRange, IROTrack> getSegments() 
	{		
		return this.segments;
	}
	
	private IROTrack getTrack( double time, String trackID )
	{
		IROTrack track = null;
		
		NumberRange rng = this.getTimeSegment( time );
		
		if( rng != null )
		{
			List< IROTrack > tracks = this.segments.get( rng );

			for( IROTrack t : tracks )
			{
				if( t.getID().equals( trackID ) )
				{
					track = t;						
					break;
				}
			}
		}
		
		return track;
	}
		
	private NumberRange findSubTimeSegment( NumberRange timeRange )
	{		
		NumberRange time = null;
		
		if( timeRange != null )
		{
			for( NumberRange rng : this.segments.keySet() )
			{
				if( rng.contain( timeRange ) )
				{
					time = rng;
					break;
				}
			}
		}
		
		return time;
	}
	
	private NumberRange getTimeSegment( double time )
	{
		NumberRange timeSeg = null;
		
		for( NumberRange rng : this.segments.keySet() )
		{
			if( rng.within( time ) )
			{
				timeSeg = rng;
				
				break;
			}
		}
		
		return timeSeg;
	}
}
