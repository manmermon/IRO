/* 
 * Copyright 2019 by Manuel Merino Monge <manmermon@dte.us.es>
 *  
 *   This file is part of IRO.
 *
 *   IRO is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   LSLRec is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with LSLRec.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   Project's URL: https://github.com/manmermon/IRO
 */
package gui.game.screen.level;

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
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;

import org.jfugue.midi.MidiDefaults;
import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;

import config.ConfigApp;
import config.ConfigParameter;
import config.IOwner;
import config.Player;
import config.Settings;
import general.ArrayTreeMap;
import general.NumberRange;
import general.Tuple;
import general.Tuple3;
import gui.game.component.sprite.Background;
import gui.game.component.sprite.Fret;
import gui.game.component.sprite.ISprite;
import gui.game.component.sprite.InputGoal;
import gui.game.component.sprite.Stave;
import gui.game.component.sprite.TimeSession;
import gui.game.component.sprite.MusicNoteGroup;
import gui.game.component.sprite.Pause;
import gui.game.component.sprite.Score;
import gui.game.screen.IPausable;
import gui.game.screen.Scene;
import gui.game.screen.level.build.LevelMusicSheetSegment;
import gui.game.screen.level.music.BackgroundMusic;
import image.BasicPainter2D;
import music.sheet.IROTrack;
import music.sheet.MusicSheet;
import music.sheet.io.IROMusicParserListener;
import statistic.RegistrarStatistic;
import tools.MusicSheetTools;
import tools.SceneTools;

public class Level extends Scene implements IPausable
{
	private static final double MIN_PLAYER_NOTE_TRACK = 0.5D;
	
	public static final int PLANE_BRACKGROUND = -1;
	public static final int PLANE_STAVE = 0;	 
	public static final int PLANE_FRET = 1;
	public static final int PLANE_NOTE = 2;
	public static final int PLANE_SCORE = 3;
	public static final int PLANE_INPUT_TARGET = 4;
	public static final int PLANE_TIME = 5;
	public static final int PLANE_PAUSE = 6;


	public static final String BACKGROUND_ID = "background";
	public static final String STAVE_ID = "stave";
	public static final String NOTE_ID = "note";
	public static final String FRET_ID = "fret";
	public static final String SCORE_ID = "score";
	public static final String INPUT_TARGET_ID = "input";
	public static final String TIME_ID = "time";
	public static final String PAUSE_ID = "pause";

	private int BPM;

	private BackgroundMusic backgroundMusic;
	private Map< Integer, BackgroundMusic > playerMusics;

	private List< Settings > playerSettings = null;

	private Boolean pause = false;

	private Boolean isMuteSession = false;

	private MusicSheet music = null;
	private List< Settings > players = null;
	
	public Level( Dimension sceneSize, List< Settings > playerSettings, File midiMusicSheelFile ) 
	{
		super( sceneSize );

		this.playerMusics = new HashMap< Integer, BackgroundMusic>();
		this.BPM = MidiDefaults.DEFAULT_TEMPO_BEATS_PER_MINUTE;
		
		if( midiMusicSheelFile != null )
		{
			IROMusicParserListener tool = new IROMusicParserListener();
			MidiParser parser = new MidiParser();
			parser.addParserListener( tool );
			try 
			{
				parser.parse( MidiSystem.getSequence( midiMusicSheelFile ) );
				this.music = tool.getSheet();
			}
			catch (InvalidMidiDataException | IOException e) 
			{
			}
		}
		
		this.players = new ArrayList< Settings >( playerSettings );
		
		this.setLevel( playerSettings);
	}

	public void setBackgroundPattern( BackgroundMusic backgroundPattern ) 
	{
		this.backgroundMusic = backgroundPattern;
	}

	public void setPlayerSheetMusic( Map< Integer, BackgroundMusic > playerSheets )
	{
		this.playerMusics.putAll( playerSheets );
	}

	public void setPlayers( List< Settings > players )
	{
		this.playerSettings = players;
	}

	public List<Settings> getPlayers() 
	{
		return this.playerSettings;
	}

	public Map< Integer, BackgroundMusic > getPlayerSheets()
	{
		return this.playerMusics;
	}

	public BackgroundMusic getBackgroundPattern() 
	{
		return this.backgroundMusic;
	}

	public void addBackgroud( ISprite sprite )
	{
		sprite.setZIndex( PLANE_BRACKGROUND );
		super.add( sprite, PLANE_BRACKGROUND );
	}

	public void addNote( MusicNoteGroup sprite )
	{
		sprite.setZIndex( PLANE_NOTE );
		super.add( sprite, PLANE_NOTE );
	}

	public void addPause( Pause sprite )
	{
		sprite.setZIndex( PLANE_PAUSE );
		super.add( sprite, PLANE_PAUSE );
	}

	public void addPentagram( Stave sprite )
	{
		sprite.setZIndex( PLANE_STAVE );
		super.SPRITES.remove( PLANE_STAVE );
		super.add( sprite, PLANE_STAVE );
	}

	public void addFret( Fret sprite )
	{
		sprite.setZIndex( PLANE_FRET );
		super.SPRITES.remove( PLANE_FRET );
		super.add( sprite, PLANE_FRET );
	}

	public Fret getFret()
	{
		List< ISprite > fret = this.SPRITES.get( PLANE_FRET );

		return (Fret)fret.get( 0 );
	}

	public Stave getPentagram()
	{
		List< ISprite > pen = this.SPRITES.get( PLANE_STAVE );

		return (Stave)pen.get( 0 ); 
	}

	public List< MusicNoteGroup > getNotes()
	{
		List< ISprite > sprites = this.SPRITES.get( PLANE_NOTE );
		List< MusicNoteGroup > notes = new ArrayList< MusicNoteGroup >( );
		for( ISprite sprite : sprites )
		{
			notes.add( (MusicNoteGroup)sprite );
		}

		return notes;
	}

	public void setBPM( int tempo )
	{
		this.BPM = tempo;
	}

	public int getBPM()
	{
		return this.BPM;
	}

	@Override
	public void updateLevel() 
	{
		synchronized ( this.pause ) 
		{
			if( !this.pause )
			{
				super.updateLevel();
			}
		}
	}

	@Override
	public void setPause( boolean pause) 
	{
		synchronized ( this.pause ) 
		{
			this.pause = pause;

			for( ISprite sp : super.getSprites( Level.PAUSE_ID, false ) )
			{
				sp.setVisible( this.pause );
			}
		}
	}

	@Override
	public boolean isPaused() 
	{
		synchronized ( this.pause )
		{
			return this.pause;
		}
	}

	public void setMuteSession( boolean mute )
	{
		synchronized( this.isMuteSession )
		{
			this.isMuteSession = mute;
		}
	}

	public boolean isMuteSession()
	{
		synchronized( this.isMuteSession )
		{
			return this.isMuteSession;
		}
	}

	/*
	public void changeSpeed( double newVel, IOwner player )
	{
		synchronized( this.pause )
		{
			if( newVel > 0  && player != null )
			{
				for( MusicNoteGroup mng : this.getNotes() )
				{
					IOwner iow = mng.getOwner();
					if( iow != null && iow.getId() == player.getId() )
					{
						mng.setShiftSpeed( newVel );
					}
				}
			}
		}
	}
	*/
	
	public void changeSpeed( double newVel, IOwner player )
	{
		List< MusicNoteGroup > notes = this.getNotes();
		
		Color prev = null, wait = null, act = null;
		BufferedImage noteImg = null;
		if( notes != null && !notes.isEmpty() )
		{
			MusicNoteGroup n = notes.get( 0 );
			
			prev = n.getPreactionColor();
			wait = n.getWaitingActionColor();
			act = n.getActionColor();
			
			noteImg = n.getNoteImg();
		}
		
		Settings playerSetting = null;
					
		for( Settings st : this.players )
		{
			if( st.getPlayer().getId() == player.getId() )
			{
				playerSetting = st;
				break;
			}
		}
		
		double reactionTime = ((Number)playerSetting.getParameter( ConfigApp.REACTION_TIME ).getSelectedValue()).doubleValue();
		double recoverTime = ((Number)playerSetting.getParameter( ConfigApp.RECOVER_TIME ).getSelectedValue()).doubleValue();

		double stepTime = reactionTime + recoverTime;

		int fretXLoc = 0;
		if( this.getFret() != null )
		{
			fretXLoc = (int)this.getFret().getScreenLocation().x;
		}
		int wayWidth = ( super.getSize().width -  fretXLoc);
		
		//TODO
		// Buscar las notas que están en pantalla para ver por donde va la música
		
		notes = this.setNotes( (Player)player, new Tuple3< Color, Color, Color >( prev, wait, act )
								, 0, this.music.getDuration(), stepTime
								, wayWidth, newVel, noteImg );
	}

	public Map< Integer, Double >getSpeedForPlayers( )
	{
		Map< Integer, Double > vels = new HashMap< Integer, Double >();

		for( Settings set : this.playerSettings )
		{
			int idPlayer = set.getPlayer().getId();
			searching:
				for( MusicNoteGroup mng : this.getNotes() )
				{
					IOwner iow = mng.getOwner();
					if( iow != null && iow.getId() == idPlayer )
					{
						vels.put( idPlayer, mng.getShiftSpeed() );

						break searching;
					}
				}
		}

		return vels;
	}



















	private void setLevel( List< Settings > playerSettings ) 
	{			
			
		//this.makeLevel( music, super.getSize(), playerSettings );
		this.makeLevel2( playerSettings );
	}


	private void makeLevel( MusicSheet music, List< Settings > playerSettings )
	{
		if( music != null && music.getNumberOfTracks() > 0 )
		{
			int tempo = music.getTempo();
			
			this.setBPM( tempo );


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

			Background back = new Background( super.getSize(), Level.BACKGROUND_ID );
			back.setZIndex( Level.PLANE_BRACKGROUND );
			this.addBackgroud( back );
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

			Pause pause = new Pause( super.getSize(), Level.PAUSE_ID );
			pause.setZIndex( Level.PLANE_PAUSE );
			pause.setVisible( false );
			this.addPause( pause );

			Stave pen = new Stave( super.getSize(), Level.STAVE_ID );
			pen.setZIndex( Level.PLANE_STAVE );
			this.addPentagram( pen );

			Dimension sizeFret = new Dimension( pen.getPentragramWidth() / 3, pen.getPentagramHeigh() ); 
			//Fret fret = new Fret( pen, IScene.FRET_ID );
			Fret fret = new Fret( Level.FRET_ID, sizeFret );
			fret.setZIndex( Level.PLANE_FRET );
			Point2D.Double loc = new Point2D.Double();
			loc.x = this.getSize().width / 2;
			loc.y = 0;
			fret.setScreenLocation( loc );
			this.addFret( fret );


			int hTS = pen.getRailHeight() / 2;
			Rectangle bounds = pen.getBounds();			
			//TimeSession time = new TimeSession( IScene.TIME_ID, pen );
			TimeSession time = new TimeSession( Level.TIME_ID, hTS, bounds );
			time.setZIndex( Level.PLANE_TIME );
			this.add( time, Level.PLANE_TIME );

			int wayWidth = ( super.getSize().width - (int)fret.getScreenLocation().x );

			//
			//
			// NOTES
			//
			//

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

					this.add( score, score.getZIndex() );

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
					this.add( goal, goal.getZIndex() );

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
						this.addNote( noteSprite );
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

				this.setBackgroundPattern( backMusic );

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

				this.setPlayerSheetMusic( playerBgMusicSheets );
				this.setPlayers( playerSettings );
			}

			/*
			for( ISprite sp : this.getAllSprites( false ) )
			{
			sp.setFrameBounds( super.getSize() );
			}
			 */
		}
	}
	//*/

	private void makeLevel2( List< Settings > playerSettings )
	{
		if( this.music != null && this.music.getNumberOfTracks() > 0 )
		{
			int tempo = this.music.getTempo();

			this.setBPM( tempo );

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

			Background back = new Background( super.getSize(), Level.BACKGROUND_ID );
			back.setZIndex( Level.PLANE_BRACKGROUND );
			this.addBackgroud( back );
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

			Pause pause = new Pause( super.getSize(), Level.PAUSE_ID );
			pause.setZIndex( Level.PLANE_PAUSE );
			pause.setVisible( false );
			this.addPause( pause );

			Stave pen = new Stave( super.getSize(), Level.STAVE_ID );
			pen.setZIndex( Level.PLANE_STAVE );
			this.addPentagram( pen );

			Dimension sizeFret = new Dimension( pen.getPentragramWidth() / 3, pen.getPentagramHeigh() ); 
			//Fret fret = new Fret( pen, IScene.FRET_ID );
			Fret fret = new Fret( Level.FRET_ID, sizeFret );
			fret.setZIndex( Level.PLANE_FRET );
			Point2D.Double loc = new Point2D.Double();
			loc.x = this.getSize().width / 2;
			loc.y = 0;
			fret.setScreenLocation( loc );
			this.addFret( fret );


			int hTS = pen.getRailHeight() / 2;
			Rectangle bounds = pen.getBounds();			
			//TimeSession time = new TimeSession( IScene.TIME_ID, pen );
			TimeSession time = new TimeSession( Level.TIME_ID, hTS, bounds );
			time.setZIndex( Level.PLANE_TIME );
			this.add( time, Level.PLANE_TIME );

			int wayWidth = ( super.getSize().width - (int)fret.getScreenLocation().x );

			//
			//
			// NOTES
			//
			//

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
				
				double musicDuration = this.music.getDuration();

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

					this.add( score, score.getZIndex() );

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
					this.add( goal, goal.getZIndex() );

					double vel = fret.getFretWidth() / reactionTime;

					for( MusicNoteGroup note : this.setNotes( playerSetting.getPlayer()
															, new Tuple3<Color, Color, Color>(preActColor, waitActColor, actColor ) 
															, 0, musicDuration, reactionTime + recoverTime, wayWidth, vel, noteImg ) )
					{
						this.addNote( note );
					}
					
					/*
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
						

						noteSprite.setImage( noteImg );

						noteSprite.setZIndex( Level.PLANE_NOTE );
						this.addNote( noteSprite );
					}
					*/
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

				Collection< IROTrack > tracks = this.music.getTracks();
				List< Pattern > playerTracks = new ArrayList< Pattern >();				
				for( IROTrack tr : tracks )
				{					
					Pattern pat = new Pattern();

					Pattern ptr = tr.getPatternTrackSheet();
					pat.add( ptr );
					pat = pat.atomize();

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

				this.setBackgroundPattern( backMusic );

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

				this.setPlayerSheetMusic( playerBgMusicSheets );
				this.setPlayers( playerSettings );
			}

		}
	}
	
	private List< MusicNoteGroup > setNotes( Player player, Tuple3< Color, Color, Color > colors											
													, double initTime, double endTime, double stepTime
													, double wayWidth, double vel
													, BufferedImage noteImg )
	{
		List< MusicNoteGroup > notes = new ArrayList< MusicNoteGroup >();
		
		if( this.music != null )
		{	
			for( double timeTrackOnScreen = initTime
					; timeTrackOnScreen < endTime
					; timeTrackOnScreen += stepTime )
			{						
				String trackID = "";
	
				List< IROTrack > Tracks = music.getNotesAtIntervalTime( new NumberRange( timeTrackOnScreen , timeTrackOnScreen + stepTime ) );
	
				double pad  = wayWidth + vel * timeTrackOnScreen;
				//int screenPos = (int)( fret.getScreenLocation().x + pad ) ;
				int screenPos = (int)pad;
				
				if( this.getFret() != null )
				{
					screenPos = (int)( this.getFret().getScreenLocation().x + pad ) ;
				}
	
				int rH = 1;
				if( this.getPentagram() != null )
				{
					rH = this.getPentagram().getRailHeight();
				}
				
				MusicNoteGroup noteSprite = new MusicNoteGroup( trackID
																, timeTrackOnScreen //+ startDelay
																, Tracks
																, Level.NOTE_ID
																, rH
																, screenPos
																, vel
																, false
																);
	
				
				noteSprite.setPreactionColor( colors.t1 );
				noteSprite.setWaitingActionColor( colors.t2 );
				noteSprite.setActionColor( colors.t3 );			
				noteSprite.setOwner( player );
	
				noteSprite.setImage( noteImg );
	
				noteSprite.setZIndex( Level.PLANE_NOTE );
				
				notes.add( noteSprite );
			}
		}
		
		return notes;
	}
}
