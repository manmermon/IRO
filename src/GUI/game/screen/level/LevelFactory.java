package GUI.game.screen.level;

import java.awt.Dimension;
import java.awt.Rectangle;
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

import GUI.game.component.sprite.Background;
import GUI.game.component.sprite.Fret;
import GUI.game.component.sprite.ISprite;
import GUI.game.component.sprite.InputGoal;
import GUI.game.component.sprite.Pentragram;
import GUI.game.component.sprite.Score;
import GUI.game.component.sprite.MusicNoteGroup;
import GUI.game.screen.IScene;
import config.ConfigApp;
import config.ConfigParameter;
import general.ArrayTreeMap;
import general.NumberRange;
import io.IROMusicParserListener;
import music.MusicSheet;
import music.IROTrack;

public class LevelFactory 
{
	public static Level getLevel( File midiMusicSheelFile, Rectangle screenBounds
									, double actionTime, double recoverTime ) throws InvalidMidiDataException, IOException
	{			
		IROMusicParserListener tool = new IROMusicParserListener();
		MidiParser parser = new MidiParser();
		parser.addParserListener( tool );
		parser.parse( MidiSystem.getSequence( midiMusicSheelFile ) );

		MusicSheet music = tool.getSheet();

		return makeLevel( music, screenBounds, actionTime, recoverTime );
	}

	//*
	private static Level makeLevel( MusicSheet music
									, Rectangle screenBounds
									, final double userReactionTime
									, final double userRecoverTime )
	{
		Level lv = null;
		
		Dimension screenSize = screenBounds.getSize();

		if( music != null && music.getNumberOfTracks() > 0 )
		{		
			int tempo = music.getTempo();

			lv = new Level( screenSize );
			lv.setBPM( tempo );

			ConfigParameter par = ConfigApp.getParameter( ConfigApp.BACKGROUND_IMAGE );
			Object bg = par.getSelectedValue();
			String path = null;
			if( bg != null )
			{
				path = bg.toString();
			}
					
			Background back = new Background( screenSize, IScene.BACKGROUND_ID, path );
			back.setZIndex( -1 );
			lv.addBackgroud( back );

			Pentragram pen = new Pentragram( screenSize, IScene.PENTRAGRAM_ID );
			pen.setZIndex( 0 );
			lv.addPentagram( pen );

			Score score = new Score( IScene.SCORE_ID, pen, 100 );
			score.setZIndex( IScene.PLANE_SCORE );
			lv.add( score, score.getZIndex() );
			
			InputGoal goal = new InputGoal( IScene.INPUT_TARGET_ID, pen );
			goal.setZIndex( IScene.PLANE_INPUT_TARGET );
			lv.add( goal, goal.getZIndex() );
			
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
			
			final double reactVel = fret.getFretWidth() / userReactionTime;
			
			final int wayWidth = ( screenSize.width - (int)fret.getScreenLocation().x );
			
			final double startDelay = wayWidth / reactVel;
						
			Set< NumberRange > times = new TreeSet< NumberRange >();
			double musicDuration = 0;
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
					
					NumberRange rng = new NumberRange( noteTrackTime * timeWhole, ( noteTrackTime + maxNoteDur ) * timeWhole );
					times.add( rng );
					
					if( rng.getMax() > musicDuration )
					{
						musicDuration = rng.getMax();
					}
				}
			}
			
			Set< NumberRange > actionTimes = new TreeSet< NumberRange >();
			double time = 0;
			do
			{
				NumberRange segment = new NumberRange( time, time + userReactionTime );
				actionTimes.add( segment );
				time += userReactionTime + userRecoverTime;
			}
			while( time < musicDuration );
			
			LevelMusicSheetSegment musicSheetPlay = new LevelMusicSheetSegment();
			
			Iterator< NumberRange > itMusicTimes = times.iterator();			
			Iterator< NumberRange > itActionTimes = actionTimes.iterator();
			
			while( itActionTimes.hasNext() )
			{
				NumberRange actionTimeRng = itActionTimes.next();
				NumberRange newActionTimeRng = new NumberRange( actionTimeRng.getMin() 
																, actionTimeRng.getMax() + userRecoverTime );
				
				boolean added = false;
				
				musicTimes:
				while( itMusicTimes.hasNext() )
				{
					NumberRange musicTimeRng = itMusicTimes.next();
					
					if( !added )
					{
						if( actionTimeRng.within( musicTimeRng.getMin() ) )
						{
							added = true;
							
							musicSheetPlay.addNewSegments( new NumberRange( actionTimeRng.getMin() / timeWhole
																			, actionTimeRng.getMax() / timeWhole ) );
						}
						else if( newActionTimeRng.within( musicTimeRng.getMin() ) )
						{
							added = true;
							
							itActionTimes.remove();
							musicSheetPlay.addNewSegments( new NumberRange( newActionTimeRng.getMin() / timeWhole
																			, musicTimeRng.getMin() / timeWhole ) );
						}
						else if( musicTimeRng.getMin() > newActionTimeRng.getMax() )
						{
							break musicTimes;
						}
					}
					else if( musicTimeRng.getMin() > actionTimeRng.getMax() )
					{
						break musicTimes;
					}
				}	
			}
			
			
			//musicSheetPlay.addNewSegments( new NumberRange( currentRange.getMin() / timeWhole, currentRange.getMax() / timeWhole ) );
			
			/*
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
						
//						if( tRng.getMin() - prevRange.getMin() > ( userReactionTime + userRecoverTime ) )
//						{
//							currentRange = tRng;
//						}
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
			//*/
			
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
			
			par = ConfigApp.getParameter( ConfigApp.NOTE_IMAGE);
			Object nt = par.getSelectedValue();
			path = null;
			if( nt != null )
			{
				path = nt.toString();
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
									
				MusicNoteGroup noteSprite = new MusicNoteGroup( trackID
														, Tracks
														, IScene.NOTE_ID
														, pen
														, screenPos
														, vel
														, false
														, path 
														);

				noteSprite.setZIndex( 1 );
				lv.addNote( noteSprite );
			}
			
			
			BackgroundMusic backMusic = new BackgroundMusic();
			backMusic.setPattern( backgroundPattern );
			backMusic.setDelay( startDelay );
			
			lv.setBackgroundPattern( backMusic );		
			
		}
		
		for( ISprite sp : lv.getAllSprites( false ) )
		{
			sp.setFrameBounds( screenBounds );
		}
		
		return lv;
	}
	//*/
	
	private static Level makeLevel2( MusicSheet music, Rectangle screenBounds
			, final double userReactionTime, final double userRecoverTime )
	{
		Level lv = null;

		Dimension screenSize = screenBounds.getSize();

		if( music != null && music.getNumberOfTracks() > 0 )
		{		
			int tempo = music.getTempo();
			double musicDuration = music.getDuration(); 

			lv = new Level( screenSize );
			lv.setBPM( tempo );

			Background back = new Background( screenSize, IScene.BACKGROUND_ID, ConfigApp.BACKGROUND_SPRITE_FILE_PATH + "006.jpeg" );
			back.setZIndex( -1 );
			lv.addBackgroud( back );

			Pentragram pen = new Pentragram( screenSize, IScene.PENTRAGRAM_ID );
			pen.setZIndex( 0 );
			lv.addPentagram( pen );

			Score score = new Score( IScene.SCORE_ID, pen, 100 );
			score.setZIndex( IScene.PLANE_SCORE );
			lv.add( score, score.getZIndex() );

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

			final double reactVel = fret.getFretWidth() / userReactionTime;

			final int wayWidth = ( screenSize.width - (int)fret.getScreenLocation().x );

			final double startDelay = wayWidth / reactVel;

			LevelMusicSheetSegment musicSheetAction = new LevelMusicSheetSegment();			
			LevelMusicSheetSegment musicSheetRecover = new LevelMusicSheetSegment();
			
			double time = 0;
			do
			{
				NumberRange segment = new NumberRange( time, time + userReactionTime );
				musicSheetAction.addNewSegments( segment );
				double auxtime = time + userReactionTime * 1.01;
				
				segment = new NumberRange( auxtime, auxtime + userRecoverTime * 0.98 );
				musicSheetRecover.addNewSegments( segment );
				time += userReactionTime + userRecoverTime;
				
			}
			while( time < musicDuration );

			for( IROTrack track : music.getTracks() )
			{
				ArrayTreeMap< Double, Note > NOTES = track.getTrackNotes();
				ArrayTreeMap< Double, Note > copyNotes = new ArrayTreeMap< Double, Note >( );
				copyNotes.putAll( NOTES );

				if( NOTES != null && NOTES.size() > 0 )
				{
					for( Double noteTime : NOTES.keySet() )
					{
						List< Note > Notes = NOTES.get( noteTime );
						
						if( !Notes.isEmpty() )
						{
							double maxNoteDur = 0;
							
							Iterator< Note > itNotes = Notes.iterator();

							boolean isRest  = true;
							while( itNotes.hasNext() )
							{
								Note note = itNotes.next();		
																
								if( !note.isRest() )
								{
									isRest = false;
									if( maxNoteDur < note.getDuration() )
									{
										maxNoteDur = note.getDuration();
									}
								}
							}	
							
							if( isRest )								 
							{
								Notes.clear();
							}
							
							if( !Notes.isEmpty() )
							{
								double noteTrackTime = noteTime * timeWhole;
								
								if( musicSheetAction.existMusicSegmentInTime( noteTrackTime ) )
								{							
									musicSheetAction.addNewTrack( noteTrackTime, track.getID() );
	
									musicSheetAction.addNotes( noteTrackTime, track.getID(), Notes );
	
									Note restNote = Note.createRest( maxNoteDur );
	
									copyNotes.remove( noteTime );
									copyNotes.put( noteTime, restNote );							
								}
								
								if( musicSheetRecover.existMusicSegmentInTime( noteTrackTime ) )
								{							
									musicSheetRecover.addNewTrack( noteTrackTime, track.getID() );
	
									musicSheetRecover.addNotes( noteTrackTime, track.getID(), Notes );
	
									Note restNote = Note.createRest( maxNoteDur );
	
									copyNotes.remove( noteTime );
									copyNotes.put( noteTime, restNote );							
								}
							}
						}
					}
				}

				NOTES.clear();
				NOTES.putAll( copyNotes );
			}
			
			musicSheetAction.setTracksTempo( music.getTempo() );
			musicSheetRecover.setTracksTempo( music.getTempo() );
			
			for( IROTrack track : music.getTracks() )
			{				
				musicSheetAction.setTrackInstrument( track.getID(), track.getInstrument() );
				musicSheetRecover.setTrackInstrument( track.getID(), track.getInstrument() );				
			}

			Iterator< NumberRange > itSheetAction = musicSheetAction.getSegments().keySet().iterator();
			Iterator< NumberRange > itSheetRecover = musicSheetRecover.getSegments().keySet().iterator();
			ArrayTreeMap< NumberRange, IROTrack > adjustSheetAction = new ArrayTreeMap<NumberRange, IROTrack>();
			
			while( itSheetAction.hasNext() )
			{
				NumberRange rngAction = itSheetAction.next();
				List< IROTrack > trackAction = musicSheetAction.getSegments().get( rngAction );
				
				if( trackAction.isEmpty() )
				{
					itSheetAction.remove();
					
					boolean find = false;
					while( !find && itSheetRecover.hasNext() )
					{
						NumberRange rngRecover = itSheetRecover.next();
						
						if( rngRecover.getMin() > rngAction.getMax() )
						{
							List< IROTrack > trackRecover = musicSheetRecover.getSegments().get( rngAction );
							
							if( trackRecover != null && !trackRecover.isEmpty() )
							{
								adjustSheetAction.put( rngRecover, trackRecover );								
							}
							
							itSheetRecover.remove();
						}
					}
				}
			}
						
			for( NumberRange rng : adjustSheetAction.keySet() )				
			{				
				List< IROTrack > track = adjustSheetAction.get( rng );
				musicSheetAction.getSegments().put( rng, track );
			}
			adjustSheetAction.clear();

			for( NumberRange rng : musicSheetAction.getSegments().keySet() )
			{	
				double initTimeTrack = rng.getMin() * timeWhole;

				String trackID = "";

				List< IROTrack > Tracks = musicSheetAction.getSegments().get( rng );

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

				MusicNoteGroup noteSprite = new MusicNoteGroup( trackID
						, Tracks
						, IScene.NOTE_ID
						, pen
						, screenPos
						, vel
						, false
						, ConfigApp.NOTE_SPRITE_FILE_PATH + "001.png");

				noteSprite.setZIndex( 1 );
				lv.addNote( noteSprite );
			}
			
			for( IROTrack track : music.getTracks() )
			{
				backgroundPattern.add( track.getPatternTrackSheet() );
			}
			
			for( NumberRange rng : musicSheetRecover.getSegments().keySet() )
			{
				List< IROTrack > tracks = musicSheetRecover.getSegments().get( rng );
				for( IROTrack track : tracks )
				{
					backgroundPattern.add( track.getPatternTrackSheet() );
				}
			}

			BackgroundMusic backMusic = new BackgroundMusic();
			backMusic.setPattern( backgroundPattern );
			backMusic.setDelay( startDelay );

			lv.setBackgroundPattern( backMusic );		

		}

		for( ISprite sp : lv.getAllSprites( false ) )
		{
			sp.setFrameBounds( screenBounds );
		}

		return lv;
	}

}
