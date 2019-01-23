package GUI.screens.levels;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;

import GUI.components.Background;
import GUI.components.Fret;
import GUI.components.ISprite;
import GUI.components.NoteSprite;
import GUI.components.Pentragram;
import GUI.screens.IScene;
import general.ArrayTreeMap;
import general.Tuple;
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
			
			double startDelay = -1; 
			
			String selectedTrack = "track-0";
			
			
			for( IROTrack track : music.getTracks() )
			{
				System.out.println("LevelFactory.makeLevel() " + track.getID() + " " + track);
				
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
	
						final double userReactionTime = 2.0D; // 2 second
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
	
							noteTrackTime *= timeWhole;
	
							boolean rest = true;
								
							double noteDur = 0;
							for( int n = 0; n < Notes.size(); n++ )
							{
								Note note = Notes.get( n );
	
								rest = rest && note.isRest();		
								if( noteDur < note.getDuration() )
								{
									noteDur = note.getDuration();
								}
							}
	
	
							boolean ghost = ( ( noteTrackTime - ( ctrReactTime + userNoteDuration ) ) <= userReactionTime ) || rest;
		
							if( !ghost  )
							{
								double vel = reactVel;		
								
								replaceNotes.add( new Tuple<Double, Double>( noteTrackTime / timeWhole, noteDur ) ); 
								
								noteDur *= timeQuarter;
								vel = reactVel;
	
								int screenPos = (int)( vel * ( noteTrackTime - noteDur + wayWidthQuarterTime ) ) + (int)fretLoc.x;
									
								ctrReactTime = noteTrackTime;
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
			
			BackgroundMusic backMusic = new BackgroundMusic();
			backMusic.setPattern( backgroundPattern);
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
