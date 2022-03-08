package gui.game.screen.level.build;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiSystem;

import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;

import gui.game.component.sprite.Background;
import gui.game.component.sprite.Fret;
import gui.game.component.sprite.InputGoal;
import gui.game.component.sprite.Stave;
import gui.game.component.sprite.Score;
import gui.game.component.sprite.TimeSession;
import gui.game.screen.level.Level;
import gui.game.screen.level.music.BackgroundMusic;
import gui.game.component.sprite.MusicNoteGroup;
import gui.game.component.sprite.Pause;
import config.ConfigApp;
import config.ConfigParameter;
import config.Settings;
import general.ArrayTreeMap;
import general.NumberRange;
import general.Tuple;
import image.BasicPainter2D;
import music.sheet.IROTrack;
import music.sheet.MusicSheet;
import music.sheet.io.IROMusicParserListener;
import statistic.RegistrarStatistic;
import tools.MusicSheetTools;
import tools.SceneTools;

public class LevelMusicBuilder 
{	
	private static final double MIN_PLAYER_NOTE_TRACK = 0.5D;
	
	public static Level getLevel( File midiMusicSheelFile
									//, Rectangle screenBounds
									, Dimension screenSize
									, List< Settings > playerSettings ) 
								throws Exception
	{		
		IROMusicParserListener tool = new IROMusicParserListener();
		MidiParser parser = new MidiParser();
		parser.addParserListener( tool );
		parser.parse( MidiSystem.getSequence( midiMusicSheelFile ) );

		MusicSheet music = tool.getSheet();
		
		//return makeLevel( music, screenBounds, playerTimes );
		//Level lv = makeLevel( music, screenSize, playerSettings );
		Level lv = makeLevel2( music, screenSize, playerSettings );

		return lv;
	}


	private static Level makeLevel( MusicSheet music
								//, Rectangle screenBounds
								, Dimension screenSize
								, List< Settings > playerSettings )
	{
		Level lv = null;
		if( music != null && music.getNumberOfTracks() > 0 )
		{
			int tempo = music.getTempo();

			//lv = new Level( screenSize, screenBounds );
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

			Background back = new Background( screenSize, Level.BACKGROUND_ID );
			back.setZIndex( Level.PLANE_BRACKGROUND );
			lv.addBackgroud( back );
			if( path != null )
			{
				try
				{
					Image img = ImageIO.read( new File( path ) );

					img = img.getScaledInstance( back.getBounds().width
							, back.getBounds().height
							, Image.SCALE_SMOOTH );

					back.setImage( (BufferedImage)BasicPainter2D.copyImage( img ) );
				}
				catch (IOException ex)
				{	
				}
			}		

			Pause pause = new Pause( screenSize, Level.PAUSE_ID );
			pause.setZIndex( Level.PLANE_PAUSE );
			pause.setVisible( false );
			lv.addPause( pause );

			Stave pen = new Stave( screenSize, Level.STAVE_ID );
			pen.setZIndex( Level.PLANE_STAVE );
			lv.addPentagram( pen );

			Dimension sizeFret = new Dimension( pen.getPentragramWidth() / 3, pen.getPentagramHeigh() ); 
			//Fret fret = new Fret( pen, IScene.FRET_ID );
			Fret fret = new Fret( Level.FRET_ID, sizeFret );
			fret.setZIndex( Level.PLANE_FRET );
			Point2D.Double loc = new Point2D.Double();
			loc.x = lv.getSize().width / 2;
			loc.y = 0;
			fret.setScreenLocation( loc );
			lv.addFret( fret );
			
			
			int hTS = pen.getRailHeight() / 2;
			Rectangle bounds = pen.getBounds();			
			//TimeSession time = new TimeSession( IScene.TIME_ID, pen );
			TimeSession time = new TimeSession( Level.TIME_ID, hTS, bounds );
			time.setZIndex( Level.PLANE_TIME );
			lv.add( time, Level.PLANE_TIME );

			int wayWidth = ( screenSize.width - (int)fret.getScreenLocation().x );
			
			//
			//
			// NOTES
			//
			//

			//final double timeQuarter = 60D / tempo;
			//final double timeWhole = 4 * timeQuarter;
			final double timeWhole = MusicSheetTools.getWholeTempo2Second( tempo );

			Pattern backgroundPattern = new Pattern();
			backgroundPattern.setTempo( tempo );			

			//
			//
			// Music sheet segmentation
			// Player-Segment
			//
			//
			
			if( playerSettings != null && !playerSettings.isEmpty() )
			{
				int numberOfPlayers = playerSettings.size();
				LevelMusicSheetSegment[] musicPlayers = new LevelMusicSheetSegment[ numberOfPlayers ];
				ArrayTreeMap< Integer, Tuple< String, NumberRange > > playerSheetRests = new ArrayTreeMap<Integer, Tuple< String, NumberRange> >();
			
				SceneTools.getPlayerSegments( music, playerSettings, timeWhole, MIN_PLAYER_NOTE_TRACK
											, musicPlayers, playerSheetRests );
				
				for( LevelMusicSheetSegment msplayer : musicPlayers )
				{
					msplayer.setTracksTempo( music.getTempo() );
				}

				for( IROTrack track : music.getTracks() )
				{				
					for( LevelMusicSheetSegment msplayer : musicPlayers )
					{
						msplayer.setTrackInstrument( track.getID(), track.getInstrument() );
					}
					
					backgroundPattern.add( track.getPatternTrackSheet() );
				}
				
				backgroundPattern = backgroundPattern.atomize();
					
				//
				//
				// Note sprites
				//
				//
				
				par = cfg.getParameter( ConfigApp.NOTE_IMAGE);
				Object nt = par.getSelectedValue();
				path = null;
				if( nt != null )
				{
					path = nt.toString();
				}

				BufferedImage noteImg = null;
				//Color bgc = new Color( 255, 255, 255, 140 );
				
				double maxNumNotes = Integer.MIN_VALUE;
				for( LevelMusicSheetSegment lmss : musicPlayers )
				{
					if( lmss.getSegments().size() > maxNumNotes )
					{
						maxNumNotes = lmss.getSegments().size();
					}
				}
								
				Point2D.Double prevScoreLoc = null; 
				for( int indexMusicSheetPlayers = 0; indexMusicSheetPlayers < musicPlayers.length; indexMusicSheetPlayers++ )
				{	
					Settings playerSetting = playerSettings.get( indexMusicSheetPlayers );
					Color actColor = (Color)playerSetting.getParameter( ConfigApp.ACTION_COLOR ).getSelectedValue();
					Color preActColor = (Color)playerSetting.getParameter( ConfigApp.PREACTION_COLOR ).getSelectedValue();
					Color waitActColor = (Color)playerSetting.getParameter( ConfigApp.WAITING_ACTION_COLOR ).getSelectedValue();
										
					LevelMusicSheetSegment msplayer = musicPlayers[ indexMusicSheetPlayers ];

					int nNotes = msplayer.getSegments().size();
					
					Double sc = RegistrarStatistic.getPlayerScore( playerSetting.getPlayer().getId() );
					
					if( sc == null )
					{
						sc = 0D;
					}
					
					Score score = new Score( Level.SCORE_ID, sc.intValue(), (int)( 100 * maxNumNotes / nNotes ), pen.getRailHeight() / 2, pen.getBounds().getLocation() );
					score.setZIndex( Level.PLANE_SCORE );
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
					
					//InputGoal goal = new InputGoal( IScene.INPUT_TARGET_ID, pen );
					InputGoal goal = new InputGoal( Level.INPUT_TARGET_ID, pen.getRailHeight(), pen.getBounds() );
					goal.setOwner( playerSetting.getPlayer() );
					goal.setZIndex( Level.PLANE_INPUT_TARGET );
					Point2D.Double goalLoc = new Point2D.Double();
					goalLoc.y = prevScoreLoc.y;
					goalLoc.x = prevScoreLoc.x + score.getSize().width + 5;
					goal.setScreenLocation( goalLoc );
					int goalSize = score.getSize().height;
					goal.setSize( new Dimension( goalSize, goalSize ) );
					lv.add( goal, goal.getZIndex() );
					
					double reactionTime = ((Number)playerSettings.get( indexMusicSheetPlayers ).getParameter( ConfigApp.REACTION_TIME ).getSelectedValue()).doubleValue();
					//reactionTime += ((Number)playerSettings.get( indexMusicSheetPlayers ).getParameter( ConfigApp.TIME_IN_INPUT_TARGET ).getSelectedValue()).doubleValue();
					
					double vel = fret.getFretWidth() / reactionTime;
					
					for( NumberRange rng : msplayer.getSegments().keySet() )
					{						
						double timeTrackOnScreen = rng.getMin() * timeWhole;
						
						String trackID = "";

						List< IROTrack > Tracks = msplayer.getSegments().get( rng );

						for( IROTrack track : Tracks )
						{
							trackID += track.getID() + "_";
						}
						
						/*
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

						if( shift < Double.MAX_VALUE && shift != 0D )
						{
							for( IROTrack track : Tracks )
							{	
								track.shiftNoteTime( -shift );
							}
						}
						*/
						
						double pad  = wayWidth + vel * timeTrackOnScreen;
						int screenPos = (int)( fret.getScreenLocation().x + pad ) ;

						MusicNoteGroup noteSprite = new MusicNoteGroup( trackID
																		, timeTrackOnScreen //+ startDelay
																		, Tracks
																		, Level.NOTE_ID
																		//, pen
																		, pen.getRailHeight()
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

									/*
									Dimension s = noteSprite.getBounds().getSize();
									
									noteImg = (BufferedImage)basicPainter2D.circle( 0, 0, s.width, bgc, null );
									noteImg = (BufferedImage)basicPainter2D.composeImage( noteImg, 0, 0
											, basicPainter2D.copyImage( 
													img.getScaledInstance( noteImg.getWidth() 
															, noteImg.getHeight()
															, Image.SCALE_SMOOTH ) ) );
									 
									noteImg = (BufferedImage)img.getScaledInstance( s.width 
																					, s.width
																					, Image.SCALE_SMOOTH );
									 */
									noteImg = (BufferedImage)img;
									
								}
								catch (Exception ex) 
								{
								}
							}
						}
						
						noteSprite.setImage( noteImg );

						noteSprite.setZIndex( Level.PLANE_NOTE );
						lv.addNote( noteSprite );
					}
				}
				
				
				//
				//
				// Set rests in player's music sheet
				//
				//
				
				for( Integer iplayer : playerSheetRests.keySet() )
				{
					LevelMusicSheetSegment msplayer = musicPlayers[ iplayer ];
					
					for( Tuple< String, NumberRange > tRest : playerSheetRests.get( iplayer ) )
					{
						String trackID = tRest.t1;						
						NumberRange restRange = tRest.t2;
						double noteTrackTime = restRange.getMin();
						List< Note > restList = new ArrayList<Note>();
						restList.add( Note.createRest( restRange.getRangeLength() ) );
						
						if(  !msplayer.existMusicSegmentInTime( restRange.getMin() ) )
						{			
							msplayer.addNewSegments( new NumberRange( restRange.getMin(), restRange.getMax() ) );
						}
						
						msplayer.addNewTrack( noteTrackTime, trackID );
						msplayer.addNotes( noteTrackTime, trackID, restList );
					}
				}
				
				//
				//
				// Start delay
				//
				//
				
				double startDealy = Double.POSITIVE_INFINITY;
				double refReactTime = 1D;
				double adjustment = 0.079687D;
				for( Settings playerSetting : playerSettings )
				{
					double reactionTime = ((Number)playerSetting.getParameter( ConfigApp.REACTION_TIME ).getSelectedValue()).doubleValue();
					double vel = SceneTools.getAvatarVel( fret.getFretWidth(), playerSetting);
					
					double delay = wayWidth / vel;
					if( delay < startDealy )
					{
						refReactTime = reactionTime;
						startDealy = delay;
					}
				}
				
				startDealy +=  ( adjustment * refReactTime ); 
				//startDealy /= timeWhole;
				
				//
				//
				// Set delay
				//
				//
				
				/*
				for( LevelMusicSheetSegment msplayer : musicPlayers  )
				{
					for( NumberRange rng : msplayer.getSegments().keySet() )
					{	
						List< IROTrack > Tracks = msplayer.getSegments().get( rng );

						for( IROTrack track : Tracks )
						{	
							track.shiftNoteTime( startDealy );
						}
					}
					
					List< String > firstTracks = msplayer.getFirstTracks();
					
					List< Note > trackRestNote = new ArrayList< Note >();
					trackRestNote.add( Note.createRest( startDealy ) );
					
					if( !msplayer.existMusicSegmentInTime( 0 ) )
					{		
						msplayer.addNewSegments( new NumberRange( 0, startDealy / timeWhole ) );
					}
					
					for( String track : firstTracks )
					{
						msplayer.addNewTrack( 0, track );									
						msplayer.addNotes( 0, track, trackRestNote );
					} 
				}
				//*/
				
				//
				//
				// Set player's pattern
				//
				//				
				
				Pattern[] playerPatterns = new Pattern[ numberOfPlayers ];
				for( int iplayer = 0; iplayer < musicPlayers.length; iplayer++ )
				{
					LevelMusicSheetSegment msplayer = musicPlayers[ iplayer ];
					Pattern pat = new Pattern();
					
					Map< String, IROTrack > auxTracks = new HashMap<String, IROTrack >();
					for( NumberRange kr : msplayer.getSegments().keySet() )
					{
						List< IROTrack > tracks = msplayer.getSegments().get( kr );
												
						for( IROTrack tr : tracks )
						{
							IROTrack aux = auxTracks.get( tr.getID() );
							if( aux == null )
							{
								auxTracks.put( tr.getID(), tr );
							}
							else
							{
								for( Double t : tr.getTrackNotes().keySet() )
								{
									List< Note > notes = tr.getTrackNotes().get( t );
									aux.addNotes( t,notes );
								}
							}
						}
					}
					
					for( IROTrack tr : auxTracks.values() )
					{
						pat.add( tr.getPatternTrackSheet() );
					}
					pat = pat.atomize();
					//pat.setTempo( tempo );
					playerPatterns[ iplayer ] = pat;
				}				
				
				
				BackgroundMusic backMusic = null;
				
				CyclicBarrier musicCoordinator = new CyclicBarrier( playerPatterns.length + 1 );
				
				try
				{
					backMusic = new BackgroundMusic();
					backMusic.setPattern( backgroundPattern );
					backMusic.setDelay( startDealy );
					backMusic.setCoordinator( musicCoordinator );
				}
				catch ( Exception ex) 
				{
					ex.printStackTrace();
				}
				
				lv.setBackgroundPattern( backMusic );
				
				Map< Integer, BackgroundMusic > playerBgMusicSheets = new HashMap< Integer, BackgroundMusic >();
				
				try
				{	
					for( int i = 0; i < playerPatterns.length; i++ )
					{
						Settings setPl = playerSettings.get( i );
						Pattern pt = playerPatterns[ i ];
						BackgroundMusic playerbgMusic = new BackgroundMusic();
						playerbgMusic.setPattern( pt );
						playerbgMusic.setDelay( startDealy );
						playerbgMusic.setCoordinator( musicCoordinator );
						
						playerBgMusicSheets.put( setPl.getPlayer().getId(), playerbgMusic );
					}					
				}
				catch ( Exception ex) 
				{
					ex.printStackTrace();
				}
				
				lv.setPlayerSheetMusic( playerBgMusicSheets );
				lv.setPlayers( playerSettings );
			}

			/*
			for( ISprite sp : lv.getAllSprites( false ) )
			{
				sp.setFrameBounds( screenSize );
			}
			*/
		}

		return lv;
	}
	//*/

	private static Level makeLevel2( MusicSheet music
									, Dimension screenSize
									, List< Settings > playerSettings )
	{
		Level lv = null;
		if( music != null && music.getNumberOfTracks() > 0 )
		{
			int tempo = music.getTempo();
			
			//lv = new Level( screenSize, screenBounds );
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

			Background back = new Background( screenSize, Level.BACKGROUND_ID );
			back.setZIndex( Level.PLANE_BRACKGROUND );
			lv.addBackgroud( back );
			if( path != null )
			{
				try
				{
					Image img = ImageIO.read( new File( path ) );

					img = img.getScaledInstance( back.getBounds().width
							, back.getBounds().height
							, Image.SCALE_SMOOTH );

					back.setImage( (BufferedImage)BasicPainter2D.copyImage( img ) );
				}
				catch (IOException ex)
				{	
				}
			}		

			Pause pause = new Pause( screenSize, Level.PAUSE_ID );
			pause.setZIndex( Level.PLANE_PAUSE );
			pause.setVisible( false );
			lv.addPause( pause );

			Stave pen = new Stave( screenSize, Level.STAVE_ID );
			pen.setZIndex( Level.PLANE_STAVE );
			lv.addPentagram( pen );

			Dimension sizeFret = new Dimension( pen.getPentragramWidth() / 3, pen.getPentagramHeigh() ); 
			//Fret fret = new Fret( pen, IScene.FRET_ID );
			Fret fret = new Fret( Level.FRET_ID, sizeFret );
			fret.setZIndex( Level.PLANE_FRET );
			Point2D.Double loc = new Point2D.Double();
			loc.x = lv.getSize().width / 2;
			loc.y = 0;
			fret.setScreenLocation( loc );
			lv.addFret( fret );


			int hTS = pen.getRailHeight() / 2;
			Rectangle bounds = pen.getBounds();			
			//TimeSession time = new TimeSession( IScene.TIME_ID, pen );
			TimeSession time = new TimeSession( Level.TIME_ID, hTS, bounds );
			time.setZIndex( Level.PLANE_TIME );
			lv.add( time, Level.PLANE_TIME );

			int wayWidth = ( screenSize.width - (int)fret.getScreenLocation().x );

			//
			//
			// NOTES
			//
			//

			//final double timeQuarter = 60D / tempo;
			//final double timeWhole = 4 * timeQuarter;
			//final double timeWhole = MusicSheetTools.getWholeTempo2Second( tempo );

			Pattern backgroundPattern = new Pattern();
			backgroundPattern.setTempo( tempo );			

			//
			//
			// Music sheet segmentation
			// Player-Segment
			//
			//

			if( playerSettings != null && !playerSettings.isEmpty() )
			{
				int numberOfPlayers = playerSettings.size();
				
				//
				//
				// Note sprites
				//
				//

				par = cfg.getParameter( ConfigApp.NOTE_IMAGE);
				Object nt = par.getSelectedValue();
				path = null;
				if( nt != null )
				{
					path = nt.toString();
				}

				BufferedImage noteImg = null;
				//Color bgc = new Color( 255, 255, 255, 140 );

				double musicDuration = music.getDuration();
				
				Point2D.Double prevScoreLoc = null; 
				for( int indexMusicSheetPlayers = 0; indexMusicSheetPlayers < numberOfPlayers; indexMusicSheetPlayers++ )
				{	
					Settings playerSetting = playerSettings.get( indexMusicSheetPlayers );
					Color actColor = (Color)playerSetting.getParameter( ConfigApp.ACTION_COLOR ).getSelectedValue();
					Color preActColor = (Color)playerSetting.getParameter( ConfigApp.PREACTION_COLOR ).getSelectedValue();
					Color waitActColor = (Color)playerSetting.getParameter( ConfigApp.WAITING_ACTION_COLOR ).getSelectedValue();

					double reactionTime = ((Number)playerSettings.get( indexMusicSheetPlayers ).getParameter( ConfigApp.REACTION_TIME ).getSelectedValue()).doubleValue();
					double recoverTime = ((Number)playerSettings.get( indexMusicSheetPlayers ).getParameter( ConfigApp.RECOVER_TIME ).getSelectedValue()).doubleValue();
					
					int scoreUnit = (int)( 1000 * ( reactionTime + recoverTime ) / musicDuration );
					scoreUnit = ( scoreUnit == 0 ) ? 1 : scoreUnit;

					Double sc = RegistrarStatistic.getPlayerScore( playerSetting.getPlayer().getId() );

					if( sc == null )
					{
						sc = 0D;
					}

					Score score = new Score( Level.SCORE_ID, sc.intValue(),  scoreUnit, pen.getRailHeight() / 2, pen.getBounds().getLocation() );
					score.setZIndex( Level.PLANE_SCORE );
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

					//InputGoal goal = new InputGoal( IScene.INPUT_TARGET_ID, pen );
					InputGoal goal = new InputGoal( Level.INPUT_TARGET_ID, pen.getRailHeight(), pen.getBounds() );
					goal.setOwner( playerSetting.getPlayer() );
					goal.setZIndex( Level.PLANE_INPUT_TARGET );
					Point2D.Double goalLoc = new Point2D.Double();
					goalLoc.y = prevScoreLoc.y;
					goalLoc.x = prevScoreLoc.x + score.getSize().width + 5;
					goal.setScreenLocation( goalLoc );
					int goalSize = score.getSize().height;
					goal.setSize( new Dimension( goalSize, goalSize ) );
					lv.add( goal, goal.getZIndex() );

					double vel = fret.getFretWidth() / reactionTime;
					
					//double quarterDuration = MusicSheetTools.getQuarterTempo2Second( music.getTempo() );
					//double wholeDuration = MusicSheetTools.getWholeTempo2Second( music.getTempo() );

					for( double timeTrackOnScreen = 0D
							; timeTrackOnScreen < musicDuration
							; timeTrackOnScreen += reactionTime + recoverTime )
					{						
						String trackID = "";

						List< IROTrack > Tracks = music.getNotesAtIntervalTime( new NumberRange( timeTrackOnScreen , timeTrackOnScreen + reactionTime + recoverTime ) );
						
						double pad  = wayWidth + vel * timeTrackOnScreen;
						int screenPos = (int)( fret.getScreenLocation().x + pad ) ;

						MusicNoteGroup noteSprite = new MusicNoteGroup( trackID
																	, timeTrackOnScreen //+ startDelay
																	, Tracks
																	, Level.NOTE_ID
																	, pen.getRailHeight()
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

									noteImg = (BufferedImage)img;

								}
								catch (Exception ex) 
								{
								}
							}
						}

						noteSprite.setImage( noteImg );

						noteSprite.setZIndex( Level.PLANE_NOTE );
						lv.addNote( noteSprite );
					}
				}
				
				//
				//
				// Start delay
				//
				//

				double startDealy = Double.POSITIVE_INFINITY;
				double refReactTime = 1D;
				double adjustment = 0.079687D;
				for( Settings playerSetting : playerSettings )
				{
					double reactionTime = ((Number)playerSetting.getParameter( ConfigApp.REACTION_TIME ).getSelectedValue()).doubleValue();
					double vel = SceneTools.getAvatarVel( fret.getFretWidth(), playerSetting);

					double delay = wayWidth / vel;
					if( delay < startDealy )
					{
						refReactTime = reactionTime;
						startDealy = delay;
					}
				}

				startDealy +=  ( adjustment * refReactTime ); 


				//
				//
				// Set player's pattern
				//
				//				

				Collection< IROTrack > tracks = music.getTracks();
				List< Pattern > playerTracks = new ArrayList< Pattern >();
				int iplayer = 0;
				for( IROTrack tr : tracks )
				{					
					Pattern pat = new Pattern();
					
					Pattern ptr = tr.getPatternTrackSheet();
					pat.add( ptr );
					pat = pat.atomize();
					//pat.setTempo( tempo );
					
					/*
					if( iplayer < numberOfPlayers )
					{
						playerTracks.add( pat );
						
						iplayer++;
					}
					else
					{
						backgroundPattern.add( pat );
					}
					//*/
					
					backgroundPattern.add( pat );
				}				


				BackgroundMusic backMusic = null;

				CyclicBarrier musicCoordinator = new CyclicBarrier( playerTracks.size()+ 1 );

				try
				{
					backMusic = new BackgroundMusic();
					backMusic.setPattern( backgroundPattern );
					backMusic.setDelay( startDealy );
					backMusic.setCoordinator( musicCoordinator );
				}
				catch ( Exception ex) 
				{
					ex.printStackTrace();
				}

				lv.setBackgroundPattern( backMusic );

				Map< Integer, BackgroundMusic > playerBgMusicSheets = new HashMap< Integer, BackgroundMusic >();

				try
				{	
					for( int i = 0; i < playerTracks.size(); i++ )
					{
						Settings setPl = playerSettings.get( i );
						Pattern pt = playerTracks.get( i );
						BackgroundMusic playerbgMusic = new BackgroundMusic();
						playerbgMusic.setPattern( pt );
						playerbgMusic.setDelay( startDealy );
						playerbgMusic.setCoordinator( musicCoordinator );

						playerBgMusicSheets.put( setPl.getPlayer().getId(), playerbgMusic );
					}					
				}
				catch ( Exception ex) 
				{
					ex.printStackTrace();
				}

				lv.setPlayerSheetMusic( playerBgMusicSheets );
				lv.setPlayers( playerSettings );
			}

		}

		return lv;
	}

	/*
	public static void changeLevelSpeed( Level lv )
	{
		Map< Integer, BackgroundMusic > bm = lv.getPlayerSheets();
		for( Integer ip : bm.keySet() )
		{
			BackgroundMusic m = bm.get( ip );
			Pattern pt = m.getPattern();
			String ptTx = pt.toString();
			ptTx = ptTx.replaceAll( "T[0-9]+\\s", "T120 ");
			System.out.println( "Pattern A: " + pt );
			pt = new Pattern( ptTx );
			System.out.println( "Pattern B: " + ptTx );
		}
	}
	*/
}
