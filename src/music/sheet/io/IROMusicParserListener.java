package music.sheet.io;

import java.util.ArrayList;
import java.util.List;

import org.jfugue.midi.MidiDictionary;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import general.ArrayTreeMap;
import general.Tuple;
import music.sheet.IROTrack;
import music.sheet.MusicSheet;

public class IROMusicParserListener extends ParserListenerAdapter
{
    private MusicSheet sheet;    
    private IROTrack currentTrack;
    
    private ArrayTreeMap< String, Tuple< Double, List< Note > > > trackNotes;
    private ArrayTreeMap< String, Tuple< Double, String > > trackEffects;
    
    private final double DEFAULT_BEAT_TIME_NOTE = -1.0D;    
    
    private double trackBeatTimeNote = DEFAULT_BEAT_TIME_NOTE;
    
    private String effects = "";
        
    public IROMusicParserListener() 
    {
        super();
        this.sheet = new MusicSheet();
        
        this.trackNotes = new ArrayTreeMap< String, Tuple< Double,List< Note > > >();
        this.trackEffects = new ArrayTreeMap< String, Tuple< Double, String > >();
    }

    @Override
    public void onInstrumentParsed( byte instrument ) 
    { 
        String instrumentNames = MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get(instrument);
        
        this.currentTrack.setInstrument( instrumentNames );
    }
    
    @Override
    public void onTempoChanged(int tempoBPM) 
    {
    	super.onTempoChanged( tempoBPM );
        
    	this.sheet.setTempo( tempoBPM );
    }
    
    @Override
    public void onTrackChanged( byte track ) 
    {
    	super.onTrackChanged( track );
    	
    	String trackID = IROTrack.TRACK_ID_DEFAULT_PREFIX + track;
    	    	
    	this.sheet.createNewTrack( trackID, track );
    	
    	this.currentTrack = this.sheet.getTrack( trackID );    	
    }
       
    @Override
    public void onNoteParsed( Note note ) 
    {   
    	super.onNoteParsed( note );
    	
    	List< Note > noteList = new ArrayList< Note >();
    	
    	noteList.add( note );
    	this.trackNotes.put( this.currentTrack.getID(), new Tuple< Double, List< Note >>( this.trackBeatTimeNote, noteList ) );
    	
    	if( !this.effects.isEmpty() )
    	{
    		this.trackEffects.put( this.currentTrack.getID(), new Tuple< Double, String >( this.trackBeatTimeNote, this.effects ) );
    		
    		this.effects = "";
    	}
    	
		this.trackBeatTimeNote = DEFAULT_BEAT_TIME_NOTE;
    }
   
    //*
    @Override
    public void onNotePressed(Note note) 
    {
    	super.onNotePressed(note);
    }
    //*/
    
    //*
    @Override
    public void onNoteReleased(Note note) 
    {
    	super.onNoteReleased(note);
    }
    //*/
        
    public MusicSheet getSheet() 
    {
        return this.sheet;
    }
    
    @Override
    public void afterParsingFinished() 
    {
    	for( String trackID : this.trackNotes.keySet() )
    	{
    		List< Tuple< Double, List< Note > > > NOTES = this.trackNotes.get( trackID );
    		List< Tuple< Double, List< Note > > > NotesAndTime = new ArrayList< Tuple< Double, List< Note > > >();
    		 
    		double trackTime = 0;
    		
    		for( Tuple< Double, List< Note > > t : NOTES )
    		{	
    			double time = t.t1;
    			List< Note > Notes = t.t2;
    			
    			double maxNoteDur = 0.0;
    			
    			for( Note note : Notes )
    			{
    				double initTime = time;    				
	    			double noteDur = note.getDuration();
	    				    			
	    			if( initTime < 0 )
	    			{
	    				initTime = trackTime;
	    				if( maxNoteDur < noteDur )
	    				{
	    					maxNoteDur = noteDur;
	    				}
	    			}
	    			else if( initTime < trackTime )
	    			{
	    				double endTimeNote = initTime + noteDur;
	    				if( endTimeNote > trackTime )
	    				{
	    					if( endTimeNote - trackTime > maxNoteDur )
	    					{
	    						maxNoteDur = endTimeNote - trackTime;
	    					}
	    				}
	    				else
	    				{
	    					trackTime = initTime;
	    					maxNoteDur = noteDur;
	    				}
	    			}
	    			else
	    			{
	    				double aux = noteDur + ( initTime - trackTime );
	    				if( aux > maxNoteDur )
	    				{
	    					maxNoteDur = aux;
	    				}
	    			}
	    			
	    			NotesAndTime.add( new Tuple< Double, List< Note > >( initTime, Notes ) );
    			}
    			
    			trackTime += maxNoteDur;
    		}
    		    		
    		IROTrack track = this.sheet.getTrack( trackID );
    		for( Tuple< Double, List< Note > > t : NotesAndTime )
    		{
    			track.addNotes( t.t1, t.t2 );
    		}
    		
    		List< Tuple< Double, String > > tEffs = this.trackEffects.get( trackID );
    		
    		if( tEffs != null)
    		{
	    		for( Tuple< Double, String> effs : tEffs )
	    		{
	    			track.addEffect( effs.t1, effs.t2 );
	    		}
    		}
    	}
    	
    	this.sheet.packTracks();
    	
    	super.afterParsingFinished();
    }
    
    //*
    @Override
    public void onBarLineParsed(long id) 
    {
    	super.onBarLineParsed(id);
    }
    //*/
   
    //*
    @Override
    public void onChordParsed(Chord chord) 
    {
    	super.onChordParsed(chord);
    }
    //*/
   
    
    //*
    @Override
    public void onFunctionParsed(String id, Object message) 
    {
    	super.onFunctionParsed(id, message);
    }
    //*/
     
    //*
    @Override
    public void onLayerChanged(byte layer) 
    {
    	super.onLayerChanged(layer);
    }
    //*/
    
    //*
    @Override
    public void onLyricParsed(String lyric) 
    {
    	super.onLyricParsed(lyric);
    }
    //*/
    
    //*
    @Override
    public void onMarkerParsed(String marker) 
    {
    	super.onMarkerParsed(marker);
    }
    //*/
    
    //*
    @Override
    public void onChannelPressureParsed(byte pressure) 
    {
    	this.effects += ":CP(" + pressure + ") ";
    	
    	super.onChannelPressureParsed(pressure);
    }
    //*/
        
    //*
    @Override
    public void onControllerEventParsed(byte controller, byte value) 
    {    	
    	effects += ":CE(" + controller + "," + value + ") ";
    	
    	super.onControllerEventParsed(controller, value);
    }
    //*/
    
    //*
    @Override
    public void onPitchWheelParsed(byte lsb, byte msb) 
    {
    	this.effects += ":PW(" + lsb + "," + msb + ") ";
    	
    	super.onPitchWheelParsed(lsb, msb);
    }
    
    @Override
    public void onPolyphonicPressureParsed(byte key, byte pressure) 
    {
    	this.effects += ":PP(" + key + "," + pressure + ") ";
    			
    	super.onPolyphonicPressureParsed(key, pressure);
    }
    
    @Override
    public void onSystemExclusiveParsed(byte... bytes) 
    {
    	this.effects += ":SysEx(";
    	
    	for( byte b : bytes )
    	{
    		this.effects += b + ",";
    	}    	
    	
    	this.effects = this.effects.substring(0, this.effects.length() - 1 ) + ") ";
    	
    	super.onSystemExclusiveParsed(bytes);
    }
    
    //*
    @Override
    public void onTimeSignatureParsed(byte numerator, byte powerOfTwo) 
    {
    	super.onTimeSignatureParsed(numerator, powerOfTwo);    	
    }
    //*/
    
    @Override
    public void onTrackBeatTimeBookmarked(String timeBookmarkId) 
    {
    	super.onTrackBeatTimeBookmarked(timeBookmarkId);
    }
    
    @Override
    public void onTrackBeatTimeBookmarkRequested(String timeBookmarkId) 
    {
    	super.onTrackBeatTimeBookmarkRequested(timeBookmarkId);
    }
    //*/
    
    @Override
    public void onTrackBeatTimeRequested(double time) 
    {
    	super.onTrackBeatTimeRequested(time);
    	this.trackBeatTimeNote = time;
    }
    
    @Override
	public void beforeParsingStarts() { } 

	@Override
	public void onKeySignatureParsed(byte key, byte scale) 
	{ 
	}

}
