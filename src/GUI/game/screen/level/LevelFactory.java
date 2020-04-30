package GUI.game.screen.level;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
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
import config.Settings;
import general.ArrayTreeMap;
import general.NumberRange;
import image.basicPainter2D;
import io.IROMusicParserListener;
import music.MusicSheet;
import music.IROTrack;

public class LevelFactory 
{
	
	private static final double MIN_PLAYER_NOTE_TRACK = 0.5D;
	
	public static Level getLevel( File midiMusicSheelFile
									, Rectangle screenBounds
									, List< Settings > playerTimes ) throws InvalidMidiDataException, IOException
	{			
		IROMusicParserListener tool = new IROMusicParserListener();
		MidiParser parser = new MidiParser();
		parser.addParserListener( tool );
		parser.parse( MidiSystem.getSequence( midiMusicSheelFile ) );

		MusicSheet music = tool.getSheet();

		return makeLevel( music, screenBounds, playerTimes );
	}

	private static Level makeLevel( MusicSheet music
								, Rectangle screenBounds
								, List< Settings > playerSettings )
	{
		Level lv = null;
		if( music != null && music.getNumberOfTracks() > 0 )
		{
			Dimension screenSize = screenBounds.getSize();

			int tempo = music.getTempo();

			lv = new Level( screenSize );
			lv.setBPM( tempo );


			//
			//
			// IMAGES
			//
			//

			Settings cfg = ConfigApp.getSettings().iterator().next();

			ConfigParameter par = cfg.getParameter( ConfigApp.BACKGROUND_IMAGE );
			Object bg = par.getSelectedValue();
			String path = null;
			if( bg != null )
			{
				path = bg.toString();
			}

			Background back = new Background( screenSize, IScene.BACKGROUND_ID );
			back.setZIndex( -1 );
			lv.addBackgroud( back );
			if( path != null )
			{
				try
				{
					Image img = ImageIO.read( new File( path ) );

					img = img.getScaledInstance( back.getBounds().width
							, back.getBounds().height
							, Image.SCALE_SMOOTH );

					back.setImage( (BufferedImage)basicPainter2D.copyImage( img ) );
				}
				catch (IOException ex)
				{	
				}
			}		


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

			int wayWidth = ( screenSize.width - (int)fret.getScreenLocation().x );
			
			//
			//
			// NOTES
			//
			//

			final double timeQuarter = 60D / tempo;
			final double timeWhole = 4 * timeQuarter;

			Pattern backgroundPattern = new Pattern();
			backgroundPattern.setTempo( tempo );			

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
					boolean isRestNote = false; 
					while( itNotes.hasNext() )
					{
						Note note = itNotes.next();		
						
						isRestNote = note.isRest() | isRestNote;
					
						if( maxNoteDur < note.getDuration() )
						{
							maxNoteDur = note.getDuration();
						}
					}
					
					NumberRange rng = new NumberRange( noteTrackTime * timeWhole, ( noteTrackTime + maxNoteDur ) * timeWhole );
					
					if( !isRestNote )
					{
						times.add( rng );
					}

					if( rng.getMax() > musicDuration )
					{
						musicDuration = rng.getMax();
					}
				}
			}

			if( playerSettings != null && !playerSettings.isEmpty() )
			{	
				int numberOfPlayers = playerSettings.size();
				
				LevelMusicSheetSegment[] musicSheetPlayers = new LevelMusicSheetSegment[ numberOfPlayers ];
				for( int i = 0; i < numberOfPlayers; i++ )
				{
					musicSheetPlayers[ i ] = new LevelMusicSheetSegment();
				}

				int player = 0;
				NumberRange crng = null;				
				double initTrackTime = 0;
				HashMap< Integer, NumberRange > currentTimeRange = new HashMap<Integer, NumberRange>();
				int[] noteLocation = new int[ numberOfPlayers ];
				boolean added = false;
				NumberRange r = null;
				boolean getNext = true;
				List< NumberRange > timesCopy = new ArrayList<NumberRange>( times );
				boolean[] assignedNotes = new boolean[ timesCopy.size() ];
				
				setNotes:
				while( true )
				{	
					int indexNote = noteLocation[ player ];
					
					if( getNext )
					{	
						do
						{
							indexNote = noteLocation[ player ];
							noteLocation[ player ]++;
						}
						while( indexNote < assignedNotes.length 
								&& assignedNotes[ indexNote ] );
						
						if( indexNote < timesCopy.size() )
						{
							r = timesCopy.get( indexNote );
						}
						else
						{
							r = null;
						}
					}
					
					getNext = true;
					
					if( r != null )
					{
						if( r.getMin() >= initTrackTime )
						{
							if( crng == null )
							{
								crng = new NumberRange( initTrackTime, r.getMin() + MIN_PLAYER_NOTE_TRACK );
								currentTimeRange.put( player, crng );
							}
							
							if( !added )
							{
								if( crng.within( r.getMin() ) )
								{
									added = true;
									assignedNotes[ indexNote ] = true;
									musicSheetPlayers[ player  ].addNewSegments( new NumberRange( crng.getMin() / timeWhole
																								, crng.getMax() / timeWhole ) );
								}
								
								if( !added )
								{
									if( r.getMax() > crng.getMax() )
									{
										crng = new NumberRange( crng.getMax(), r.getMax() + MIN_PLAYER_NOTE_TRACK );
										currentTimeRange.put( player, crng );
									}
							 
									if( crng.within( r.getMin() ) )
									{
										added = true;
										musicSheetPlayers[ player  ].addNewSegments( new NumberRange( crng.getMin() / timeWhole
																									, crng.getMax() / timeWhole ) );
									}
								}							
	
								r = null;
							}
							else if( r.getMin() > crng.getMax() )
							{
								added = false;
								getNext = false;
								player++;
								initTrackTime = crng.getMax();
								crng = null;
									
								if( player >= numberOfPlayers )
								{
									player = 0;								
								}
									
								NumberRange ctr = currentTimeRange.get( player );
								Settings playerSetting = playerSettings.get( player );
								
								double reactionTime = ((Number)playerSetting.getParameter( ConfigApp.REACTION_TIME ).getSelectedValue()).doubleValue();
								double recoverTime = ((Number)playerSetting.getParameter( ConfigApp.RECOVER_TIME ).getSelectedValue()).doubleValue();
								if( ctr != null 
										&&
										( ctr.getMax() 
												+ reactionTime 
												+ recoverTime 
												- MIN_PLAYER_NOTE_TRACK ) > initTrackTime ) 
								{
									initTrackTime = ctr.getMax() + recoverTime + reactionTime - MIN_PLAYER_NOTE_TRACK;
								}							
							}
							else
							{
								assignedNotes[ indexNote ] = true;
							}
						}
					}
					else
					{						
						boolean end = false;
						for( int index : noteLocation )
						{
							end = end | ( index >= timesCopy.size() ) ;
						}
						
						if( end )
						{
							break setNotes;
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

								for( LevelMusicSheetSegment msplayer : musicSheetPlayers )
								{
									if( msplayer.existMusicSegmentInTime( noteTrackTime ) )
									{							
										msplayer.addNewTrack( noteTrackTime, track.getID() );
	
										msplayer.addNotes( noteTrackTime, track.getID(), Notes );
	
										Note restNote = Note.createRest( maxNoteDur );
	
										copyNotes.remove( noteTrackTime );
										copyNotes.put( noteTrackTime, restNote );
										break;
									}
								}
							}
						}
					}

					NOTES.clear();
					NOTES.putAll( copyNotes );
				}

				for( LevelMusicSheetSegment msplayer : musicSheetPlayers )
				{
					msplayer.setTracksTempo( music.getTempo() );
				}

				for( IROTrack track : music.getTracks() )
				{				
					for( LevelMusicSheetSegment msplayer : musicSheetPlayers )
					{
						msplayer.setTrackInstrument( track.getID(), track.getInstrument() );
					}
					
					backgroundPattern.add( track.getPatternTrackSheet() );
				}

				par = cfg.getParameter( ConfigApp.NOTE_IMAGE);
				Object nt = par.getSelectedValue();
				path = null;
				if( nt != null )
				{
					path = nt.toString();
				}


				//final double reactVel = fret.getFretWidth() / userReactionTime;				
				//final int wayWidth = ( screenSize.width - (int)fret.getScreenLocation().x );

				BufferedImage noteImg = null;
				Color bgc = new Color( 255, 255, 255, 140 );
				
				double maxNumNotes = Integer.MIN_VALUE;
				for( LevelMusicSheetSegment lmss : musicSheetPlayers )
				{
					if( lmss.getSegments().size() > maxNumNotes )
					{
						maxNumNotes = lmss.getSegments().size();
					}
				}
				
				Point2D.Double prevScoreLoc = null; 
				for( int i = 0; i < musicSheetPlayers.length; i++ )
				{	
					Settings playerSetting = playerSettings.get( i );
					Color actColor = (Color)playerSetting.getParameter( ConfigApp.ACTION_COLOR ).getSelectedValue();
					Color preActColor = (Color)playerSetting.getParameter( ConfigApp.PREACTION_COLOR ).getSelectedValue();
					Color waitActColor = (Color)playerSetting.getParameter( ConfigApp.WAITING_ACTION_COLOR ).getSelectedValue();
					
					LevelMusicSheetSegment msplayer = musicSheetPlayers[ i ];

					int nNotes = msplayer.getSegments().size();
					Score score = new Score( IScene.SCORE_ID, pen, (int)( 100 * maxNumNotes / nNotes ) );
					score.setZIndex( IScene.PLANE_SCORE );
					score.setOwner( playerSetting.getPlayer() );
					
					if( prevScoreLoc != null )
					{
						Point2D.Double scloc = score.getScreenLocation();
						scloc.y = prevScoreLoc.y;
						Dimension scDim = score.getSize();
						scloc.y += scDim.getHeight();
						score.setScreenLocation( scloc );
					}
					
					prevScoreLoc = score.getScreenLocation();
					
					lv.add( score, score.getZIndex() );
					
					InputGoal goal = new InputGoal( IScene.INPUT_TARGET_ID, pen );
					goal.setZIndex( IScene.PLANE_INPUT_TARGET );
					Point2D.Double goalLoc = new Point2D.Double();
					goalLoc.y = prevScoreLoc.y;
					goalLoc.x = prevScoreLoc.x + score.getSize().width + 5;
					goal.setScreenLocation( goalLoc );
					int goalW = score.getSize().width;
					goal.setSize( new Dimension( goalW, goalW ) );
					lv.add( goal, goal.getZIndex() );
					
					for( NumberRange rng : msplayer.getSegments().keySet() )
					{	
						double initTimeTrack = rng.getMin() * timeWhole;

						String trackID = "";

						List< IROTrack > Tracks = msplayer.getSegments().get( rng );

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

						double reactionTime = ((Number)playerSettings.get( i ).getParameter( ConfigApp.REACTION_TIME ).getSelectedValue()).doubleValue();
						double vel = fret.getFretWidth() / reactionTime;

						int screenPos = (int)fret.getScreenLocation().x + wayWidth + (int)( vel * initTimeTrack ) ;

						MusicNoteGroup noteSprite = new MusicNoteGroup( trackID
																		, initTimeTrack //+ startDelay
																		, Tracks
																		, IScene.NOTE_ID
																		, pen
																		, screenPos
																		, vel
																		, false
																		);
						noteSprite.setActionColor( actColor );
						noteSprite.setPreactionColor( preActColor );
						noteSprite.setWaitingActionColor( waitActColor );
						noteSprite.setOwner( playerSetting.getPlayer() );

						if( noteImg == null )
						{
							if( path != null )
							{
								try
								{
									Image img = ImageIO.read( new File( path ) );

									Dimension s = noteSprite.getBounds().getSize();
									noteImg = (BufferedImage)basicPainter2D.circle( 0, 0, s.width, bgc, null );
									noteImg = (BufferedImage)basicPainter2D.composeImage( noteImg, 0, 0
											, basicPainter2D.copyImage( 
													img.getScaledInstance( noteImg.getWidth() 
															, noteImg.getHeight()
															, Image.SCALE_SMOOTH ) ) );
								}
								catch (Exception ex) 
								{
								}
							}
						}
						noteSprite.setImage( noteImg );

						noteSprite.setZIndex( 1 );
						lv.addNote( noteSprite );
					}
				}
				
				double reactionTime = ((Number)playerSettings.get( 0 ).getParameter( ConfigApp.REACTION_TIME ).getSelectedValue()).doubleValue();
				double reactVel = fret.getFretWidth() / reactionTime;
				double startDelay = wayWidth / reactVel;

				BackgroundMusic backMusic = null;
				
				try
				{
					backMusic = new BackgroundMusic();
					backMusic.setPattern( backgroundPattern );
					backMusic.setDelay( startDelay );
				}
				catch ( Exception ex) 
				{
					ex.printStackTrace();
				}

				lv.setBackgroundPattern( backMusic );		

			}


			for( ISprite sp : lv.getAllSprites( false ) )
			{
				sp.setFrameBounds( screenBounds );
			}
		}

		return lv;
	}
	//*/
	
	
	/*
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

			
			//
			//
			// IMAGES
			//
			//
			
			Settings cfg = ConfigApp.getSettings().iterator().next();
			
			ConfigParameter par = cfg.getParameter( ConfigApp.BACKGROUND_IMAGE );
			Object bg = par.getSelectedValue();
			String path = null;
			if( bg != null )
			{
				path = bg.toString();
			}
					
			Background back = new Background( screenSize, IScene.BACKGROUND_ID );
			back.setZIndex( -1 );
			lv.addBackgroud( back );
			if( path != null )
			{
				try
				{
					Image img = ImageIO.read( new File( path ) );
						
					img = img.getScaledInstance( back.getBounds().width
												, back.getBounds().height
												, Image.SCALE_SMOOTH );
							
					back.setImage( (BufferedImage)basicPainter2D.copyImage( img ) );
				}
				catch (IOException ex)
				{	
				}
			}		
			

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
			
			//
			//
			// NOTES
			//
			//
			
			
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
			
			par = cfg.getParameter( ConfigApp.NOTE_IMAGE);
			Object nt = par.getSelectedValue();
			path = null;
			if( nt != null )
			{
				path = nt.toString();
			}
			
			BufferedImage noteImg = null;
			Color bgc = new Color( 255, 255, 255, 140 );
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
														, initTimeTrack //+ startDelay
														, Tracks
														, IScene.NOTE_ID
														, pen
														, screenPos
														, vel
														, false
														);
				
				if( noteImg == null )
				{
					if( path != null )
					{
						try
						{
							Image img = ImageIO.read( new File( path ) );
							
							Dimension s = noteSprite.getBounds().getSize();
							noteImg = (BufferedImage)basicPainter2D.circle( 0, 0, s.width, bgc, null );
							noteImg = (BufferedImage)basicPainter2D.composeImage( noteImg, 0, 0
									, basicPainter2D.copyImage( 
											img.getScaledInstance( noteImg.getWidth() 
													, noteImg.getHeight()
													, Image.SCALE_SMOOTH ) ) );
						}
						catch (Exception ex) 
						{
						}
					}
				}
				noteSprite.setImage( noteImg );

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
	
	/*
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
	//*/

	private class PlayerTimeRange
	{
		private int player;
		private NumberRange actionRange;
		private NumberRange recoverRange;
		
		/**
		 * 
		 */
		public PlayerTimeRange( int id, NumberRange actRng, NumberRange recRng )
		{
			this.player = id;
			this.actionRange = actRng;
			this.recoverRange = recRng;
		}
		
		/**
		 * @return the player
		 */
		public int getPlayer()
		{
			return this.player;
		}
		
		/**
		 * @return the NumberRange
		 */
		public NumberRange getActionTimeRange()
		{
			return this.actionRange;
		}
		
		/**
		 * @return the NumberRange
		 */
		public NumberRange getRecoverTimeRange()
		{
			return this.recoverRange;
		}
	}
}
