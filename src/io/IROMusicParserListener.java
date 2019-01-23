package io;

import java.util.ArrayList;
import java.util.List;

import org.jfugue.midi.MidiDictionary;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import general.ArrayTreeMap;
import general.Tuple;
import music.MusicSheet;
import music.IROTrack;

public class IROMusicParserListener extends ParserListenerAdapter
{
    private MusicSheet sheet;    
    private IROTrack currentTrack;
    
    private ArrayTreeMap< String, Tuple< Double, List< Note > > > trackNotes;
    
    private final double DEFAULT_BEAT_TIME_NOTE = -1.0D;    
    
    private double trackBeatTimeNote = DEFAULT_BEAT_TIME_NOTE;
    
    public IROMusicParserListener() 
    {
        super();
        this.sheet = new MusicSheet();
        
        this.trackNotes = new ArrayTreeMap< String, Tuple< Double,List< Note > > >();
    }

    @Override
    public void onInstrumentParsed( byte instrument ) 
    { 
        String instrumentNames = MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get(instrument);
        
        //System.out.println("Midi2AsciiParserListener.onInstrumentParsed() " + instrumentNames + " track " + this.currentTrack.getID() );
        
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
    	
    	//this.checkReleasedNotes();
    	
    	String trackID = IROTrack.TRACK_ID_DEFAULT_PREFIX + track;
    	
    	this.sheet.createNewTrack( trackID );
    	
    	//System.out.println("Midi2AsciiParserListener.onTrackChanged() " + trackID );
    	
    	this.currentTrack = this.sheet.getTrack( trackID );    	
    }
       
    @Override
    public void onNoteParsed( Note note ) 
    {   
    	super.onNoteParsed( note );
    	
    	//System.out.println("Midi2AsciiParserListener.onNoteParsed() " + note );
    	List< Note > noteList = new ArrayList< Note >();
    	noteList.add( note );
    	this.trackNotes.put( this.currentTrack.getID(), new Tuple< Double, List< Note >>( this.trackBeatTimeNote, noteList ) );
		this.trackBeatTimeNote = DEFAULT_BEAT_TIME_NOTE;
    	
		/*
    	if( note.toStringWithoutDuration().equals( "R" ) )
    	{    	
    		this.currentTrack.addNotes( note );
    	}
    	else
    	{
    		this.releasedNotes.put( this.currentTrack.getID(), new Tuple( this.trackBeatTimeNote, note ) );
    	}
    	*/
    		
    }
   
    @Override
    public void onNotePressed(Note note) 
    {
    	super.onNotePressed(note);
    	
    	//System.out.println("Midi2AsciiParserListener.onNotePressed() " + note );
    	
    	//this.checkReleasedNotes();
    }
    
    @Override
    public void onNoteReleased(Note note) 
    {
    	super.onNoteReleased(note);
    	
    	//System.out.println("Midi2AsciiParserListener.onNoteReleased() " + note );
    }
    
    /*
    private void checkReleasedNotes()
    {
    	if( !this.releasedNotes.isEmpty() )
    	{    		
    		this.currentTrack.addNotes( this.releasedNotes.get( this.currentTrack.getID() ) );
    		
    		//System.out.println("Midi2AsciiParserListener.checkReleasedNotes() " + this.releasedNotes );
    		
    		this.releasedNotes.emptyArray( this.currentTrack.getID() );
    	}
    }
    */
    
    public MusicSheet getSheet() 
    {
        return this.sheet;
    }
    
    @Override
    public void afterParsingFinished() 
    {
    	//this.checkReleasedNotes();
    	
    	for( String trackID : this.trackNotes.keySet() )
    	{
    		List< Tuple< Double, List< Note > > > NOTES = this.trackNotes.get( trackID );
    		List< Tuple< Double, List< Note > > > NotesAndTime = new ArrayList< Tuple< Double, List< Note > > >();
    		 
    		double trackTime = 0;
    		
    		for( Tuple< Double, List< Note > > t : NOTES )
    		{	
    			double time = t.x;
    			List< Note > Notes = t.y;
    			
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
    			track.addNotes( t.x, t.y );
    		}
    	}
    	
    	this.sheet.packTracks();
    	
    	super.afterParsingFinished();
    }
    
    @Override
    public void onBarLineParsed(long id) {
    	// TODO Auto-generated method stub
    	super.onBarLineParsed(id);
    	System.out.println("Midi2AsciiParserListener.onBarLineParsed()");
    }
    
    @Override
    public void onChannelPressureParsed(byte pressure) {
    	// TODO Auto-generated method stub
    	super.onChannelPressureParsed(pressure);
    	System.out.println("Midi2AsciiParserListener.onChannelPressureParsed()");
    }
    
    @Override
    public void onChordParsed(Chord chord) {
    	// TODO Auto-generated method stub
    	super.onChordParsed(chord);
    	
    	System.out.println("Midi2AsciiParserListener.onChordParsed() " ) ;
    }
    
    @Override
    public void onControllerEventParsed(byte controller, byte value) {
    	// TODO Auto-generated method stub
    	super.onControllerEventParsed(controller, value);
    	Note n = new Note(value);
    	//System.out.println("Midi2AsciiParserListener.onControllerEventParsed() " + controller + " - "+ n.toString() );
    }
    
    @Override
    public void onFunctionParsed(String id, Object message) {
    	// TODO Auto-generated method stub
    	super.onFunctionParsed(id, message);
    	System.out.println("Midi2AsciiParserListener.onFunctionParsed()");
    }
        
    @Override
    public void onLayerChanged(byte layer) {
    	// TODO Auto-generated method stub
    	super.onLayerChanged(layer);
    	System.out.println("Midi2AsciiParserListener.onLayerChanged()");
    }
    
    @Override
    public void onLyricParsed(String lyric) {
    	// TODO Auto-generated method stub
    	super.onLyricParsed(lyric);
    	System.out.println();
    }
    
    @Override
    public void onMarkerParsed(String marker) {
    	// TODO Auto-generated method stub
    	super.onMarkerParsed(marker);
    	System.out.println("Midi2AsciiParserListener.onMarkerParsed()");
    }
    
    /*
    @Override
    public void onNoteReleased(Note note) {
    	// TODO Auto-generated method stub
    	super.onNoteReleased(note);
    	System.out.println("Midi2AsciiParserListener.onNoteReleased()");
    }
    */
    
    @Override
    public void onPitchWheelParsed(byte lsb, byte msb) {
    	// TODO Auto-generated method stub
    	super.onPitchWheelParsed(lsb, msb);
    	System.out.println("Midi2AsciiParserListener.onPitchWheelParsed()");
    }
    
    @Override
    public void onPolyphonicPressureParsed(byte key, byte pressure) {
    	// TODO Auto-generated method stub
    	super.onPolyphonicPressureParsed(key, pressure);
    	System.out.println("Midi2AsciiParserListener.onPolyphonicPressureParsed()");
    }
    
    @Override
    public void onSystemExclusiveParsed(byte... bytes) {
    	// TODO Auto-generated method stub
    	super.onSystemExclusiveParsed(bytes);
    	System.out.println("Midi2AsciiParserListener.onSystemExclusiveParsed()");
    }
    
    @Override
    public void onTimeSignatureParsed(byte numerator, byte powerOfTwo) {
    	// TODO Auto-generated method stub
    	super.onTimeSignatureParsed(numerator, powerOfTwo);
    	//System.out.println("Midi2AsciiParserListener.onTimeSignatureParsed() " + numerator + ", " + powerOfTwo );
    }
    
    @Override
    public void onTrackBeatTimeBookmarked(String timeBookmarkId) {
    	// TODO Auto-generated method stub
    	super.onTrackBeatTimeBookmarked(timeBookmarkId);
    	System.out.println("Midi2AsciiParserListener.onTrackBeatTimeBookmarked()");
    }
    
    @Override
    public void onTrackBeatTimeBookmarkRequested(String timeBookmarkId) {
    	// TODO Auto-generated method stub
    	super.onTrackBeatTimeBookmarkRequested(timeBookmarkId);
    	System.out.println("Midi2AsciiParserListener.onTrackBeatTimeBookmarkRequested()");
    }
    
    @Override
    public void onTrackBeatTimeRequested(double time) 
    {
    	// TODO Auto-generated method stub
    	super.onTrackBeatTimeRequested(time);
    	this.trackBeatTimeNote = time;
    	//System.out.println("Midi2AsciiParserListener.onTrackBeatTimeRequested() " + time);
    }
}
