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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
import control.music.MusicPlayerControl;
import general.ArrayTreeMap;
import general.NumberRange;
import general.NumberTuple;
import general.Tuple;
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
import stoppableThread.IStoppableThread;
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
	
	public static final int PLANE_ALWAYS_IN_FRONT = Integer.MAX_VALUE;
	public static final int PLANE_ALWAYS_AT_THE_END = Integer.MIN_VALUE;

	public static final String BACKGROUND_ID = "background";
	public static final String STAVE_ID = "stave";
	public static final String NOTE_ID = "note";
	public static final String FRET_ID = "fret";
	public static final String SCORE_ID = "score";
	public static final String INPUT_TARGET_ID = "input";
	public static final String TIME_ID = "time";
	public static final String PAUSE_ID = "pause";
	public static final String LOADING_ID = "loading";

	private int BPM;

	private BackgroundMusic backgroundMusic;

	private List< Settings > playerSettings = null;

	private Boolean pause = false;

	private Boolean isMuteSession = false;

	private ArrayDeque< MusicSheet > musics = null;
	private MusicSheet currentMusic = null;
	
	private Rectangle sceneBounds;
	
	private Map< Integer, NumberTuple > playerTimes;
	
	private List< File > midiFiles = null;
	
	private boolean playGame = false;
	
	public Level( Rectangle sceneSize, List< Settings > playerSettings, List< File > midiMusicSheelFiles ) 
	{
		super( sceneSize.getSize() );

		this.sceneBounds = sceneSize;
		
		this.BPM = MidiDefaults.DEFAULT_TEMPO_BEATS_PER_MINUTE;
		
		this.musics = new ArrayDeque< MusicSheet >();
		
		if( midiMusicSheelFiles != null )
		{
			this.midiFiles = new ArrayList< File >( midiMusicSheelFiles );			
			
			for( File midiFile : midiMusicSheelFiles )
			{
				IROMusicParserListener tool = new IROMusicParserListener();
				MidiParser parser = new MidiParser();
				parser.addParserListener( tool );
				try 
				{
					parser.parse( MidiSystem.getSequence( midiFile ) );
					this.musics.add( tool.getSheet() );
				}
				catch (InvalidMidiDataException | IOException e) 
				{
				}
			}
		}
		
		this.playerSettings = new ArrayList< Settings>( playerSettings );
		this.playerTimes = new HashMap< Integer, NumberTuple >();	
		
		for( Settings set : playerSettings )
		{
			int id = set.getPlayer().getId();
			Number reactionTime = (Number)set.getParameter( ConfigApp.REACTION_TIME ).getSelectedValue();
			Number recoverTime = (Number)set.getParameter( ConfigApp.RECOVER_TIME ).getSelectedValue();
			
			this.playerTimes.put( id, new NumberTuple( reactionTime, recoverTime ) );
		}
			}
	
	public List< File > getMidiFiles() 
	{
		return this.midiFiles;
	}
		
	public List< Settings > getPlayers() 
	{
		return this.playerSettings;
	}

	public void playLevel()
	{
		if( !this.playGame )
		{
			this.playGame = true;
			this.currentMusic = this.musics.poll(); 
			this.makeLevel2( this.sceneBounds, this.playerSettings );
		}
	}
		
	public void enableLevel()
	{		
		this.playGame = false;
	}
	
	public boolean isFinished()
	{
		return this.musics.isEmpty();
	}
	
	public BackgroundMusic getBackgroundPattern() 
	{
		return this.backgroundMusic;
	}

	private void setBackgroundPattern( BackgroundMusic bgm) 
	{
		if( this.backgroundMusic != null )
		{
			this.backgroundMusic.stopThread( IStoppableThread.FORCE_STOP );
		}
		
		this.backgroundMusic = bgm;
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

		Fret f = null;
		
		if( fret != null && !fret.isEmpty() )
		{
			f = (Fret)fret.get( 0 );
		}
		
		return f;
	}

	public Stave getPentagram()
	{
		List< ISprite > pen = this.SPRITES.get( PLANE_STAVE );
		
		Stave stave = null;
		
		if( pen != null && !pen.isEmpty() )
		{
			stave = (Stave)pen.get( 0 );
		}
		
		return stave; 
	}

	public List< MusicNoteGroup > getAllNotes()
	{
		List< ISprite > sprites = this.SPRITES.get( PLANE_NOTE );
		List< MusicNoteGroup > notes = new ArrayList< MusicNoteGroup >( );
		if( sprites != null )
		{
			for( ISprite sprite : sprites )
			{
				notes.add( (MusicNoteGroup)sprite );
			}
		}

		return notes;
	}
	
	public List< MusicNoteGroup > getNotes( int idPlayer )
	{
		List< MusicNoteGroup > notes = new ArrayList< MusicNoteGroup >( this.getAllNotes() );
		
		Iterator< MusicNoteGroup > itNotes = notes.iterator();
		while( itNotes.hasNext() )
		{		
			IOwner player = itNotes.next().getOwner();
			
			if( player == null ||  player.getId() != idPlayer )
			{
				itNotes.remove();
			}
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
	public void setPause( boolean pause ) 
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
	
	public void changeSpeed( IOwner player, double percentcReactionTimeVariation, double percentRecoverTimeVariation )
	{
		if( player != null )
		{
			int idPlayer = player.getId();
			NumberTuple prevReactRecover = this.playerTimes.get( idPlayer );
			
			if( prevReactRecover != null )
			{					
				/*
				 * Set new times
				 */
				
				percentcReactionTimeVariation = ( percentcReactionTimeVariation <= 0 ) ? 1 : percentcReactionTimeVariation;
				percentRecoverTimeVariation = ( percentRecoverTimeVariation <= 0 ) ? 1 : percentRecoverTimeVariation;
								
				double reactionTime = percentcReactionTimeVariation * prevReactRecover.t1.doubleValue();
				double recoverTime = percentRecoverTimeVariation * prevReactRecover.t2.doubleValue();
				double thr = 1D;
				reactionTime = ( reactionTime < thr ) ? thr : reactionTime;
				
				if( prevReactRecover.t1.doubleValue() == reactionTime 
						&& prevReactRecover.t2.doubleValue() == recoverTime )
				{
					return;
				}
				
				double stepTime = reactionTime + recoverTime;
				
				this.playerTimes.put( idPlayer, new NumberTuple( reactionTime, recoverTime ) );
				System.out.println("Level.changeSpeed() " + this.playerTimes );
				
				/*
				 *  Get notes on screen
				 */
				
				List< MusicNoteGroup > prevNotes = this.getNotes( idPlayer );				
				List< MusicNoteGroup > noteOnScreen = new ArrayList< MusicNoteGroup >();
				double xprevNoteLoc = 0;
				
				if( prevNotes != null && !prevNotes.isEmpty() )
				{
					for( MusicNoteGroup n : prevNotes )
					{
						if( n.getOwner().getId() == player.getId() )
						{								
							if( this.sceneBounds != null 
									&& this.sceneBounds.contains( n.getNoteLocation() )
									)
							{
								if( n.getScreenLocation().x > xprevNoteLoc )
								{
									xprevNoteLoc = n.getScreenLocation().x;
								}
								
								noteOnScreen.add( n );
							}
						}
					}
				}
				
				
				/*
				 * Get note's colors, image and speed
				 */
				
				Color pre = null, wait = null, act = null;
				BufferedImage noteImg = null;
				double newVel = 1;
				
				if( !noteOnScreen.isEmpty() )
				{
					MusicNoteGroup n = noteOnScreen.get( 0 );
					newVel = n.getShiftSpeed();
					
					pre = n.getPreactionColor();
					wait = n.getWaitingActionColor();
					act = n.getActionColor();
					
					noteImg = n.getNoteImg();
				}
				
				/*
				 * Current music time
				 */
				
				double currentMusicTime = 0;
				if( this.getBackgroundPattern() != null )
				{
					currentMusicTime = this.getBackgroundPattern().getCurrentMusicSecondPosition();
				}
				
				
				/*
				 * Set new notes
				 */
				
				List< MusicNoteGroup > newNotes = new ArrayList< MusicNoteGroup >();
				
				int fretXLoc = 0;
				int fretW = 0;
				if( this.getFret() != null )
				{
					fretXLoc = (int)this.getFret().getScreenLocation().x;					
					fretW = this.getFret().getFretWidth();
					
					newVel = fretW / reactionTime;
				}
				
				// Keep notes on screen
				
				double lastNoteOnScreenXloc = 0;
				double nextMusicTime = currentMusicTime;
				if( !noteOnScreen.isEmpty() )
				{
					List< Double > noteOnScreen_Distance2Fret = new ArrayList< Double >();
											
					for( MusicNoteGroup cNote : noteOnScreen ) 
					{	
						double noteXLoc = cNote.getScreenLocation().x;
						
						double dist = noteXLoc - fretXLoc;
						noteOnScreen_Distance2Fret.add( dist );
						
						if( noteXLoc > lastNoteOnScreenXloc )
						{
							lastNoteOnScreenXloc = noteXLoc;
						}
					}
					
					double screenMusicTime = (super.getSize().width - fretXLoc) / newVel;
					double maxNoteOnScreenMusicTime = 0;
					for( int i = 0; i < noteOnScreen_Distance2Fret.size(); i++ )
					{
						double dist =  noteOnScreen_Distance2Fret.get( i );
						
						MusicNoteGroup nos = noteOnScreen.get( i );
						double xloc = nos.getScreenLocation().x;
						double yloc = nos.getScreenLocation().y;
						
						boolean isSelected = nos.isSelected();
						boolean isGhost = nos.isGhost();
						boolean isVisible = nos.isVisible();
												
						double t =  dist / newVel;
						double time = currentMusicTime + t;
						double step = Math.abs( t );
						List< MusicNoteGroup > notes = this.setNotes( time, time + step, step, 0, newVel );
						
						if( notes.isEmpty() )
						{
							notes.add( noteOnScreen.get( i ) );
						}
						else
						{
							for( MusicNoteGroup n : notes )
							{
								Point2D.Double loc = n.getScreenLocation();
								loc.x = xloc;
								loc.y = yloc;
								
								n.setScreenLocation( loc );
								
								n.setSelected( isSelected );
								n.setGhost( isGhost );
								n.setVisible( isVisible );
							}
						}
						newNotes.addAll( notes );
						
						if( maxNoteOnScreenMusicTime < t )
						{
							maxNoteOnScreenMusicTime = t;
						}
					}
					
					maxNoteOnScreenMusicTime = screenMusicTime - maxNoteOnScreenMusicTime;
					
					double timeAdjust = stepTime - maxNoteOnScreenMusicTime;
					timeAdjust = ( timeAdjust < 0 ) ? 0 : timeAdjust;
										
					nextMusicTime += screenMusicTime + timeAdjust;
				}
				
				// New notes
					
				List< MusicNoteGroup > nextNotes = this.setNotes( nextMusicTime, this.currentMusic.getDuration(), stepTime, 0, newVel );
				double spaceBetweenNotes = stepTime * newVel;	
				
				
				for( int i = 0; i < nextNotes.size(); i++ )
				{
					MusicNoteGroup ng = nextNotes.get( i );
					Point2D.Double loc = ng.getScreenLocation();
					loc.x = (int)( lastNoteOnScreenXloc + spaceBetweenNotes * (i + 1) );
					
					ng.setScreenLocation( loc );
				}
				
				newNotes.addAll( nextNotes );
				
				if( !newNotes.isEmpty() )
				{
					super.remove( new ArrayList< ISprite >( prevNotes ) );
				}
				
				// Add new notes				
				double currentGameTime = MusicPlayerControl.getInstance().getPlayTime();
				for( MusicNoteGroup note : newNotes )
				{
					note.setFrameBounds( this.sceneBounds );
					
					note.setPreactionColor( pre );
					note.setWaitingActionColor( wait );
					note.setActionColor( act );
					
					note.setOwner( player );
					
					note.setImage( noteImg );
					
					note.adjustGameTime( currentGameTime );					
					
					this.addNote( note );
				}		
			}
		}
	}

	/*
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
	//*/


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
					double vel = SceneTools.getAvatarVel( fret.getFretWidth(), reactionTime );

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

	private void makeLevel2( Rectangle sceneBounds, List< Settings > playerSettings )
	{
		if( this.currentMusic != null && this.currentMusic.getNumberOfTracks() > 0 )
		{
			int tempo = this.currentMusic.getTempo();

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

			if(  super.getSprites( Level.BACKGROUND_ID, false ).isEmpty() )
			{
				Background back = new Background( super.getSize(), Level.BACKGROUND_ID );
				back.setFrameBounds( sceneBounds );
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
			}

			if( super.getSprites( Level.PAUSE_ID, false ).isEmpty() )
			{
				Pause pause = new Pause( super.getSize(), Level.PAUSE_ID );
				pause.setFrameBounds( sceneBounds );
				pause.setZIndex( Level.PLANE_PAUSE );
				pause.setVisible( false );
				this.addPause( pause );
			}

			Stave pen = this.getPentagram();			
			if( pen == null )
			{
				pen = new Stave( super.getSize(), Level.STAVE_ID );
				pen.setFrameBounds( sceneBounds );
				pen.setZIndex( Level.PLANE_STAVE );
				this.addPentagram( pen );
			}

			Dimension sizeFret = new Dimension( pen.getPentragramWidth() / 3, pen.getPentagramHeigh() ); 
			//Fret fret = new Fret( pen, IScene.FRET_ID );
			
			Fret fret = this.getFret();
			
			if( fret == null )
			{			
				fret = new Fret( Level.FRET_ID, sizeFret );
				fret.setFrameBounds( sceneBounds );
				fret.setZIndex( Level.PLANE_FRET );
				Point2D.Double loc = new Point2D.Double();
				loc.x = this.getSize().width / 2;
				loc.y = 0;
				fret.setScreenLocation( loc );
				this.addFret( fret );
			}


			int hTS = pen.getRailHeight() / 2;
			Rectangle bounds = pen.getBounds();			
			//TimeSession time = new TimeSession( IScene.TIME_ID, pen );
			if( this.getSprites( Level.TIME_ID, false ) != null )
			{
				TimeSession time = new TimeSession( Level.TIME_ID, hTS, bounds );
				time.setZIndex( Level.PLANE_TIME );
				this.add( time, Level.PLANE_TIME );
			}

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
				
				double musicDuration = this.currentMusic.getDuration();

				Point2D.Double prevScoreLoc = null; 
				double startDelay = 0.5;
				
				if( this.getAllNotes() != null )
				{
					List< ISprite > noteSprites = new ArrayList< ISprite >( this.getAllNotes() );
					super.remove( noteSprites );
				}
				boolean addScoreAndInputGoal = super.getSprites( Level.SCORE_ID, false ).isEmpty();
				for( int indexMusicSheetPlayers = 0; indexMusicSheetPlayers < numberOfPlayers; indexMusicSheetPlayers++ )
				{	
					Settings playerSetting = playerSettings.get( indexMusicSheetPlayers );
					Color actColor = (Color)playerSetting.getParameter( ConfigApp.ACTION_COLOR ).getSelectedValue();
					Color preActColor = (Color)playerSetting.getParameter( ConfigApp.PREACTION_COLOR ).getSelectedValue();
					Color waitActColor = (Color)playerSetting.getParameter( ConfigApp.WAITING_ACTION_COLOR ).getSelectedValue();

					NumberTuple plTimes = this.playerTimes.get( playerSetting.getPlayer().getId() );
					
					System.out.println("Level.makeLevel2() "  + this.playerTimes );
					
					double reactionTime = plTimes.t1.doubleValue();
					double recoverTime = plTimes.t2.doubleValue();
					
					double vel = fret.getFretWidth() / reactionTime;
					
					int pad = super.getSize().width;
					
					List< MusicNoteGroup > playerNotes = this.setNotes( indexMusicSheetPlayers * startDelay, musicDuration, reactionTime + recoverTime, pad, vel );
					for( MusicNoteGroup note : playerNotes )
					{
						note.setFrameBounds( sceneBounds );
						note.setPreactionColor( preActColor );
						note.setWaitingActionColor( waitActColor );
						note.setActionColor( actColor );			
						note.setOwner( playerSetting.getPlayer() );			
						note.setImage( noteImg );
						
						this.addNote( note );
					}
					
					if( addScoreAndInputGoal )
					{					
						double scoreUnit = 1000D;
						
						if( !playerNotes.isEmpty() )
						{
							scoreUnit /= playerNotes.size();
						}
						scoreUnit = ( scoreUnit < 1 ) ? 1 : scoreUnit;
	
						double sc = 0;
						Score score = new Score( Level.SCORE_ID, sc,  scoreUnit, pen.getRailHeight() / 2, pen.getBounds().getLocation() );
						score.setFrameBounds( sceneBounds );
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
	
						InputGoal goal = new InputGoal( Level.INPUT_TARGET_ID, pen.getRailHeight(), pen.getBounds() );
						goal.setFrameBounds( sceneBounds );
						goal.setOwner( playerSetting.getPlayer() );
						goal.setZIndex( Level.PLANE_INPUT_TARGET );
						Point2D.Double goalLoc = new Point2D.Double();
						goalLoc.y = prevScoreLoc.y;
						goalLoc.x = prevScoreLoc.x + score.getSize().width + 5;
						goal.setScreenLocation( goalLoc );
						int goalSize = score.getSize().height;
						goal.setSize( new Dimension( goalSize, goalSize ) );
						this.add( goal, goal.getZIndex() );
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
					NumberTuple times = this.playerTimes.get( playerSetting.getPlayer().getId() );
					
					double reactionTime = times.t1.doubleValue();
					double vel = SceneTools.getAvatarVel( fret.getFretWidth(), reactionTime );

					double delay = wayWidth / vel;
					if( delay < startDealy )
					{
						refReactTime = reactionTime;
						startDealy = delay;
					}
				}

				startDealy +=  ( adjustment * refReactTime ); 
				System.out.println("Level.makeLevel2() startDelay " + startDealy);

				//
				//
				// Set player's pattern
				//
				//				

				Collection< IROTrack > tracks = this.currentMusic.getTracks();
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
			}

		}
	}
	
	private List< MusicNoteGroup > setNotes( double initMusicTime, double endMusicTime, double stepMusicTime								
											, double padding, double vel
											)
	{
		List< MusicNoteGroup > notes = new ArrayList< MusicNoteGroup >();
		
		if( this.currentMusic != null )
		{	
			for( double timeTrackOnScreen = initMusicTime
					; timeTrackOnScreen < endMusicTime
					; timeTrackOnScreen += stepMusicTime )
			{						
				String trackID = "";
	
				List< IROTrack > Tracks = this.currentMusic.getNotesAtIntervalTime( new NumberRange( timeTrackOnScreen , timeTrackOnScreen + stepMusicTime ) );
	
				if( Tracks != null && !Tracks.isEmpty() )
				{	
					double pad  = padding + vel * timeTrackOnScreen;
					//int screenPos = (int)( fret.getScreenLocation().x + pad ) ;
					int screenPos = (int)pad;
					
		
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
		
					noteSprite.setZIndex( Level.PLANE_NOTE );
					
					notes.add( noteSprite );
				}
			}
		}
		
		return notes;
	}
}

