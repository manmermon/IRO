package GUI.game.screen.level;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;

import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;

import GUI.game.component.Background;
import GUI.game.component.Fret;
import GUI.game.component.Pentragram;
import GUI.game.component.TrackNotesSprite;
import GUI.game.screen.IScene;
import general.ArrayTreeMap;
import general.NumberRange;
import io.IROMusicParserListener;
import music.MusicSheet;
import music.IROTrack;

public class LevelFactory 
{
	public static Level getLevel( int level, Dimension screenSize ) throws NullPointerException
	{
		if( screenSize == null )
		{
			throw new NullPointerException( "Screen size null." );
		}

		Level lv = new Level( screenSize );

		switch( level ) 
		{
		case 0:
		{	
			String[] nots = new String[] { "C", "R", "R", "D", "R","R",  "E","R", "R", "R", "C", "R", "R", "R"
					/*
						, "C", "R", "R", "D", "R","R",  "E","R", "R", "R", "C", "R", "R", "R"
						, "E", "R", "R", "F", "R","R",  "G","R", "R", "R", "R"
						, "E", "R", "R", "F", "R","R",  "G","R", "R"
						, "G", "R", "R", "A", "R","R",  "G","R", "R", "R", "F", "R", "R", "E", "R", "R", "C","R", "R", "R"
						, "G", "R", "R", "A", "R","R",  "G","R", "R", "R", "F", "R", "R", "E", "R", "R", "C","R", "R", "R"
						, "D", "R", "R", "G", "R","R",  "C","R", "R"
						, "D", "R", "R", "G", "R","R",  "C","R", "R"
					 */ 
			};


			/*
				String[] nots = new String[] { "C4", "R", "C#4", "R","C#4", "R","C#4", "R","D4", "D4","D4", "D#4","D#4","D#4","D#4","D#4","D#4","E","D",  "C","G", "F", "E", "D", "C", "G", "R"
						 , "C", "R", "R", "D", "R","R",  "E","R", "R", "R", "C", "R", "R", "R"
						 , "R", "R", "R", "R", "R","R",  "R","R", "R", "R", "R"
						 , "E", "R", "R", "F", "R","R",  "G","R", "R"
						 , "E", "F", "G", "G", "F","E",  "D","C", "C", "D", "E", "E", "D", "D", "R", "R", "C","R", "R", "R"
						 , "G", "R", "R", "A", "R","R",  "G","R", "R", "R", "F", "R", "R", "E", "R", "R", "C","R", "R", "R"
						 , "D", "R", "R", "G", "R","R",  "C","R", "R"
						 , "D", "R", "R", "G", "R","R",  "C","R", "R" };
			 */
			break;
		}
		default:
		{
			lv = null;
			break;
		}
		}

		return lv;
	}

	public static Level getLevel( File midiMusicSheelFile, Dimension screenSize ) throws InvalidMidiDataException, IOException
	{			
		IROMusicParserListener tool = new IROMusicParserListener();
		MidiParser parser = new MidiParser();
		parser.addParserListener( tool );
		parser.parse( MidiSystem.getSequence( midiMusicSheelFile ) );

		MusicSheet music = tool.getSheet();

		/*
		int t = music.getTempo();
		music = new MusicSheet();
		music.setTempo( t );
		IROTrack track = music.createNewTrack( "track-0" );
		track.addNote( 0.0, new Note( "C3w" ) );
		track.addNote( 1.0, new Note( "Rw" ) );
		track.addNote( 2.0, new Note( "D3w" ) );
		track.setInstrument( "Piano" );
		 */

		return makeLevel( music, screenSize );
	}

	private static Level makeLevel( MusicSheet music, Dimension screenSize )
	{
		Level lv = null;

		if( music != null && music.getNumberOfTracks() > 0 )
		{		
			int tempo = music.getTempo();

			lv = new Level( screenSize );
			lv.setBPM( tempo );

			Background back = new Background( screenSize, IScene.BACKGROUND_ID );
			back.setZIndex( -1 );
			lv.addBackgroud( back );

			Pentragram pen = new Pentragram( screenSize, IScene.PENTRAGRAM_ID );
			pen.setZIndex( 0 );
			lv.addPentagram( pen );

			Fret fret = new Fret( pen, IScene.FRET_ID );
			fret.setZIndex( 2 );
			Point2D.Double loc = new Point2D.Double();
			loc.x = lv.getSize().width / 2;
			loc.y = 0;
			fret.setScreenLocation( loc );
			lv.addFret( fret );
			
			Pattern backgroundPattern = new Pattern();
			backgroundPattern.setTempo( tempo );			
			
			String selectedTrack = IROTrack.TRACK_ID_DEFAULT_PREFIX +1;
			
			final double timeQuarter = 60D / tempo;
			final double timeWhole = 4 * timeQuarter;
			
			final double userReactionTime = 2D; // 2 second
			final double userRecoverTime = 2D;
			final double reactVel = fret.getFretWidth() / userReactionTime;
			
			final int wayWidth = ( screenSize.width - (int)fret.getScreenLocation().x );
			//final double velQuarter = wayWidth  * timeQuarter;
						
			//final double wayWidthQuarterTime = wayWidth / velQuarter;
			
			final double startDelay = wayWidth / reactVel;
						
			Set< NumberRange > times = new TreeSet< NumberRange >();
			for( IROTrack track : music.getTracks() )
			{
				ArrayTreeMap< Double, Note > NOTES = track.getTrackNotes();
				
				for( Double noteTrackTime : NOTES.keySet() )
				{
					List< Note > Notes = NOTES.get( noteTrackTime );
					
					Iterator< Note > itNotes = Notes.iterator();
					
					double maxNoteDur = 0;
					while( itNotes.hasNext() )
					{
						Note note = itNotes.next();		
						
						if( maxNoteDur < note.getDuration() )
						{
							maxNoteDur = note.getDuration();
						}
					}
					
					times.add( new NumberRange( noteTrackTime * timeWhole, ( noteTrackTime + maxNoteDur ) * timeWhole ) );
				}
			}
			
			LevelMusicSheetSegment musicSheetPlay = new LevelMusicSheetSegment();
			
			NumberRange currentRange = null;
			NumberRange prevRange = null;
			boolean rangeCreated = false;
			for( NumberRange tRng : times )
			{
				if( currentRange == null )
				{
					if( prevRange == null )							
					{
						currentRange = tRng;
					}
					else
					{
						if( tRng.getMin() - prevRange.getMax() > userRecoverTime )
						{
							currentRange = tRng;
						}
						else if( tRng.getMin() - prevRange.getMin() > ( userReactionTime + userRecoverTime ) )
						{
							currentRange = tRng;
						}
					}
				}
				else
				{
					if( tRng.getMax() > currentRange.getMax() )
					{
						currentRange = new NumberRange( currentRange.getMin(), tRng.getMax() );
					}
					
					if( currentRange.getRangeLength() >= userReactionTime / 2 )
					{
						if( !rangeCreated )
						{
							musicSheetPlay.addNewSegments( new NumberRange( currentRange.getMin() / timeWhole, currentRange.getMax() / timeWhole ) );
							rangeCreated = true;
						}
						
						if( currentRange.getRangeLength() >= userReactionTime )
						{
							rangeCreated = false;
							prevRange = currentRange;
							currentRange = null;
						}
					}
				}
				
			}
			
			for( IROTrack track : music.getTracks() )
			{
				ArrayTreeMap< Double, Note > NOTES = track.getTrackNotes();
				ArrayTreeMap< Double, Note > copyNotes = new ArrayTreeMap< Double, Note >( );
				copyNotes.putAll( NOTES );
				
				if( NOTES != null && NOTES.size() > 0 )
				{
					for( Double noteTrackTime : NOTES.keySet() )
					{
						List< Note > Notes = NOTES.get( noteTrackTime );
												
						if( !Notes.isEmpty() )
						{
							double maxNoteDur = 0;
							
							Iterator< Note > itNotes = Notes.iterator();
							
							while( itNotes.hasNext() )
							{
								Note note = itNotes.next();		
								
								if( maxNoteDur < note.getDuration() )
								{
									maxNoteDur = note.getDuration();
								}
							}							

							if( musicSheetPlay.existMusicSegmentInTime( noteTrackTime ) )
							{							
								musicSheetPlay.addNewTrack( noteTrackTime, track.getID() );

								musicSheetPlay.addNotes( noteTrackTime, track.getID(), Notes );

								Note restNote = Note.createRest( maxNoteDur );

								copyNotes.remove( noteTrackTime );
								copyNotes.put( noteTrackTime, restNote );							
							}
						}
					}
				}
				
				NOTES.clear();
				NOTES.putAll( copyNotes );
			}
			
			musicSheetPlay.setTracksTempo( music.getTempo() );
			
			for( IROTrack track : music.getTracks() )
			{				
				musicSheetPlay.setTrackInstrument( track.getID(), track.getInstrument() );
				backgroundPattern.add( track.getPatternTrackSheet() );
			}
			
			for( NumberRange rng : musicSheetPlay.getSegments().keySet() )
			{	
				double initTimeTrack = rng.getMin() * timeWhole;
				
				String trackID = "";
								
				List< IROTrack > Tracks = musicSheetPlay.getSegments().get( rng );
							
				double shift = Double.MAX_VALUE;
				for( IROTrack track : Tracks )
				{					
					trackID += track.getID() + "_";
					
					Double auxShift = Collections.min( track.getTrackNotes().keySet() );
					if( auxShift < shift )
					{
						shift = auxShift;
					}
				}
				
				if( shift < Double.MAX_VALUE )
				{
					for( IROTrack track : Tracks )
					{	
						track.shiftNoteTime( -shift );
					}
				}
								
				double vel = reactVel;

				int screenPos = (int)fret.getScreenLocation().x + wayWidth + (int)( vel * initTimeTrack ) ;
									
				TrackNotesSprite noteSprite = new TrackNotesSprite( trackID
														, Tracks
														, IScene.NOTE_ID
														, pen
														, screenPos
														, vel
														, false
														);

				noteSprite.setZIndex( 1 );
				lv.addNote( noteSprite );
			}
			
			/*
			for( IROTrack track : music.getTracks() )
			{
				ArrayTreeMap< Double, Note > NOTES = track.getTrackNotes();
				ArrayTreeMap< Double, Note > copyNotes = new ArrayTreeMap< Double, Note >( );
				copyNotes.putAll( NOTES );
				
				if( NOTES != null && NOTES.size() > 0 )
				{	
					if( startDelay < 0 )
					{
						startDelay = wayWidthQuarterTime;
					}

					double ctrReactTime = 0;	
					double musicSegmentEnd = Double.MIN_VALUE;
					
					for( Double noteTrackTime : NOTES.keySet() )
					{
						List< Note > Notes = NOTES.get( noteTrackTime );
						
						double noteTrackTimeWhole = noteTrackTime * timeWhole;
						
						boolean rest = Notes.isEmpty();
						
						double maxNoteDur = 0;
						
						if( !rest )
						{
							Iterator< Note > itNotes = Notes.iterator();
							boolean listRestNotes = true;
							
							while( itNotes.hasNext() )
							{
								Note note = itNotes.next();		
								
								if( maxNoteDur < note.getDuration() )
								{
									maxNoteDur = note.getDuration();
								}
								
								listRestNotes = listRestNotes && note.isRest();
							}							
							
							rest = listRestNotes;
						}						
						
						boolean ghost = ( ( noteTrackTimeWhole - ctrReactTime ) > userReactionTime / 2 ) || rest;
	
						if( !ghost  )
						{
							if( !musicSheetPlay.existMusicSegmentInTime( noteTrackTime ) )
							{
								musicSheetPlay.addNewSegments( new NumberRange( noteTrackTime, noteTrackTime + userReactionTime / timeWhole ) );
							}
							
							musicSheetPlay.addNewTrack( noteTrackTime, track.getID() );
							
							musicSheetPlay.addNotes( noteTrackTime, track.getID(), Notes );
							
							Note restNote = Note.createRest( maxNoteDur );
							
							copyNotes.remove( noteTrackTime );
							copyNotes.put( noteTrackTime, restNote );
							
							if( musicSegmentEnd < ( noteTrackTime + maxNoteDur ) )
							{
								musicSegmentEnd = noteTrackTime + maxNoteDur;
							}
						}
						else
						{							
							if( ( noteTrackTimeWhole - ctrReactTime ) > userReactionTime )
							{
								ctrReactTime = musicSegmentEnd * timeWhole;
								
								if( noteTrackTimeWhole > ctrReactTime )
								{
									ctrReactTime = noteTrackTimeWhole;
								}
								
								musicSegmentEnd = Double.MIN_VALUE;
							}
						}
					}
				}
				
				NOTES.clear();
				NOTES.putAll( copyNotes );
			}
			
			musicSheetPlay.setTracksTempo( music.getTempo() );
			
			for( IROTrack track : music.getTracks() )
			{				
				musicSheetPlay.setTrackInstrument( track.getID(), track.getInstrument() );
				backgroundPattern.add( track.getPatternTrackSheet() );
			}
			
			for( NumberRange rng : musicSheetPlay.getSegments().keySet() )
			{	
				double maxTrackDuration = 0;
				double initTrack = rng.getMin() * timeWhole;
				
				String trackID = "";
				
				List< IROTrack > Tracks = musicSheetPlay.getSegments().get( rng );
				
				for( IROTrack track : Tracks )
				{
					if( maxTrackDuration < track.getTrackDuration() )
					{
						maxTrackDuration = track.getTrackDuration();
					}
					
					trackID += track.getID() + "_";
				}
								
				double vel = reactVel;

				int screenPos = (int)( vel * ( initTrack - wayWidthQuarterTime ) ) + (int)fret.getScreenLocation().x;
									
				NoteSprite noteSprite = new NoteSprite( trackID
														, Tracks
														, IScene.NOTE_ID
														, pen
														, screenPos
														, vel
														, false
														);

				//noteSprite.setInstrument( track.getInstrument() );
				//noteSprite.setTempo( tempo );

				noteSprite.setZIndex( 1 );
				lv.addNote( noteSprite );
			}
			*/
			
			/*
			for( IROTrack track : music.getTracks() )
			{	
				if( !track.getID().equals( selectedTrack ) )
				{
					backgroundPattern.add( track.getPatternTrackSheet() );
				}
				else
				{	
					ArrayTreeMap< Double, Note > NOTES = track.getTrackNotes();
						
					if( NOTES != null && NOTES.size() > 0 )
					{
						final double timeQuarter = 60D / tempo;
						final double timeWhole = 4 * timeQuarter;			
						final double velQuarter = screenSize.getWidth()  * timeQuarter;
	
						final double userReactionTime = 2D; // 2 second
						double userNoteDuration = 0.0;
						Point2D.Double fretLoc = fret.getScreenLocation();
	
						final int wayWidth = ( screenSize.width - (int)fretLoc.x );
						final double reactVel = fret.getFretWidth() / userReactionTime;			
	
						final double wayWidthQuarterTime = wayWidth / velQuarter;
						if( startDelay < 0 )
						{
							startDelay = wayWidthQuarterTime;
						}
	
						double ctrReactTime = 0;					
						List< Tuple< Double, Double > > replaceNotes = new ArrayList< Tuple< Double, Double> >();
						for( Double noteTrackTime : NOTES.keySet() )
						{
							List< Note > Notes = NOTES.get( noteTrackTime );
								
							double noteTrackTimeWhole = noteTrackTime * timeWhole;
	
							double noteDur = 0;
							Iterator< Note > itNotes = Notes.iterator();
							while( itNotes.hasNext() )
							{
								Note note = itNotes.next();		
										
								if( noteDur < note.getDuration() )
								{
									noteDur = note.getDuration();
								}
								
								if( note.isRest() )
								{
									itNotes.remove();
								}
							}
	
	
							boolean rest = Notes.isEmpty();
							
							boolean ghost = ( ( noteTrackTimeWhole - ( ctrReactTime + userNoteDuration ) ) <= userReactionTime ) || rest;
		
							if( !ghost  )
							{
								itNotes = Notes.iterator();
								while( itNotes.hasNext() )
								{
									Note note = itNotes.next();
									
//									int vol = (int)( note.getOnVelocity() * 2 );
//									if( vol > 127 )
//									{
//										vol = 127;
//									}
//									note.setOnVelocity( (byte)vol);
//									
//									note.setOnVelocity( (byte) maxVolumeNote );
									
									if( noteDur < note.getDuration() )
									{
										noteDur = note.getDuration();
									}
								}
								
								double vel = reactVel;		
								
								replaceNotes.add( new Tuple<Double, Double>( noteTrackTime, noteDur ) ); 
								
								noteDur *= timeQuarter;
								vel = reactVel;
	
								int screenPos = (int)( vel * ( noteTrackTimeWhole - noteDur + wayWidthQuarterTime ) ) + (int)fretLoc.x;
									
								ctrReactTime = noteTrackTimeWhole;
								userNoteDuration = noteDur * 3;
								
								NoteSprite noteSprite = new NoteSprite( track.getID()
																		, Notes
																		, IScene.NOTE_ID
																		, pen
																		, screenPos
																		, vel
																		, ghost
																		);
		
								noteSprite.setInstrument( track.getInstrument() );
								noteSprite.setTempo( tempo );
		
								noteSprite.setZIndex( 1 );
								lv.addNote( noteSprite );
							}	
						}
						
						for( Tuple< Double, Double > t : replaceNotes )
						{
							double time = t.x;
							double noteDuration = t.y;
							
							Note restNote = Note.createRest( noteDuration );
							
							NOTES.remove( time );
							NOTES.put( time, restNote );							
						}
						
						backgroundPattern.add( track.getPatternTrackSheet() );
					}
				}
			}			
			 */
			
			BackgroundMusic backMusic = new BackgroundMusic();
			backMusic.setPattern( backgroundPattern );
			backMusic.setDelay( startDelay );
			
			lv.setBackgroundPattern( backMusic );			
		}
		
		return lv;
	}


	/*
	private static char getNoteDuration( char dur, char auxDur )
	{
		switch( dur )
		{
		case 'w':
		{
			dur = auxDur;
			break;
		}	
		case 'h': 
		{
			if( auxDur != 'w' )
			{
				dur = auxDur;
			}

			break;
		}
		case 'q':
		{
			if( auxDur != 'h' && auxDur != 'w' )
			{
				dur = auxDur;
			}
			break;
		}
		case 'i':
		{
			if( auxDur != 'h' && auxDur != 'w' && auxDur != 'q')
			{
				dur = auxDur;								
			}
			break;
		}
		case 's':
		{
			if( auxDur == 't' ||auxDur == 'x' || auxDur != 'o')
			{
				dur = auxDur;
			}

			break;
		}
		case 't':
		{
			if( auxDur == 'x' || auxDur != 'o')
			{
				dur = auxDur;
			}

			break;
		}	
		case 'x':
		{
			if( auxDur != 'o')
			{
				dur = auxDur;
			}

			break;
		}
		}

		return dur;
	}

	private static double getScale( char dur )
	{
		double scale = 1;

		switch( dur )
		{
		case 'w':
		{
			scale *= 4;
			break;
		}	
		case 'h': 
		{
			scale *= 2;						
			break;
		}
		case 'i':
		{
			scale /= 2;
			break;
		}
		case 's':
		{
			scale /= 4;
			break;
		}
		case 't':
		{
			scale /= 8;
			break;
		}	
		case 'x':
		{
			scale /= 16;
			break;
		}
		case 'o':
		{
			scale /= 32;
			break;
		}
		}

		return scale;
	}
	//*/
}
