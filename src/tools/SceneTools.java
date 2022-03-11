package tools;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jfugue.theory.Note;

import gui.game.screen.level.build.LevelMusicSheetSegment;
import config.ConfigApp;
import config.Settings;
import general.ArrayTreeMap;
import general.NumberRange;
import general.Tuple;
import music.sheet.IROTrack;
import music.sheet.MusicSheet;

public class SceneTools 
{
	public static FontMetrics getFontMetricByHeight( int maxHeight )
	{
		FontMetrics fm = null;
		
		if( maxHeight > 0 )
		{
			Canvas cv = new Canvas();

			Font f = new Font( Font.SANS_SERIF, Font.BOLD, 12 );
			fm = cv.getFontMetrics( f );

			while( fm.getHeight() < maxHeight )
			{
				f = new Font( f.getName(), f.getStyle(), f.getSize() + 1 );
				fm = cv.getFontMetrics( f );
			}

			while( fm.getHeight() > maxHeight )
			{
				f = new Font( f.getName(), f.getStyle(), f.getSize() - 1 );
				fm = cv.getFontMetrics( f );
			}			
		}
		
		return fm;
	}
	
	public static FontMetrics getFontMetricByWidth( int maxWidth, String text )
	{
		FontMetrics fm = null;
		
		if( maxWidth > 0 && text != null )
		{
			Canvas cv = new Canvas();

			Font f = new Font( Font.SANS_SERIF, Font.BOLD, 12 );
			fm = cv.getFontMetrics( f );

			while( fm.stringWidth( text ) < maxWidth )
			{
				f = new Font( f.getName(), f.getStyle(), f.getSize() + 1 );
				fm = cv.getFontMetrics( f );
			}

			while( fm.stringWidth( text ) > maxWidth )
			{
				f = new Font( f.getName(), f.getStyle(), f.getSize() - 1 );
				fm = cv.getFontMetrics( f );
			}			
		}
		
		return fm;
	}

	public static double getAvatarVel( int fretWidth, double reactionTime )
	{
		double vel = fretWidth / reactionTime;
		
		return vel;
	}

	public static double getAvatarSpeed( int fretWidth, double reactionTime )
	{
		double vel = fretWidth / reactionTime;
		
		return vel;
	}
	
	public static double getAvatarReactionTime( int fretWidth, double vel )
	{
		double reactionTime = fretWidth / vel;
		
		return reactionTime;
	}
	
	public static Set< NumberRange > getMusicSegmentation( MusicSheet music, double timeWhole )
	{
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
		
		return times;
	}

	public static void getPlayerSegments( MusicSheet music, List< Settings > playerSettings
															, double timeWhole, double MIN_PLAYER_NOTE_TRACK
															, LevelMusicSheetSegment[] musicPlayers
															, ArrayTreeMap< Integer, Tuple< String, NumberRange > > playerSheetRests)
	{		
		if( musicPlayers == null || playerSheetRests == null )
		{ 
			throw new IllegalArgumentException( "musicPlayers and/or playerSheetRests null" );
		}
			
		Set< NumberRange > musicSegments = getMusicSegmentation( music, timeWhole );
		
		int numberOfPlayers = playerSettings.size();
		
		
		for( int i = 0; i < musicPlayers.length; i++ )
		{
			musicPlayers[ i ] = new LevelMusicSheetSegment();
		}

		int player = 0;
		NumberRange crng = null;				
		double initTrackTime = 0;
		HashMap< Integer, NumberRange > currentTimeRange = new HashMap<Integer, NumberRange>();
		int[] noteLocation = new int[ numberOfPlayers ];
		boolean added = false;
		NumberRange r = null;
		boolean getNext = true;
		List< NumberRange > timeListCopy = new ArrayList< NumberRange >( musicSegments );
		
		boolean[] assignedNotes = new boolean[ timeListCopy.size() ];
		
		//
		//
		// Set Player-Segment
		//
		//
		
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
				
				if( indexNote < timeListCopy.size() )
				{
					r = timeListCopy.get( indexNote );
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
							musicPlayers[ player  ].addNewSegments( new NumberRange( crng.getMin() / timeWhole
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
								musicPlayers[ player  ].addNewSegments( new NumberRange( crng.getMin() / timeWhole
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
					end = end | ( index >= timeListCopy.size() ) ;
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

						boolean isRest = true;
						
						while( itNotes.hasNext() )
						{
							Note note = itNotes.next();
							
							isRest = isRest && note.isRest();

							if( maxNoteDur < note.getDuration() )
							{
								maxNoteDur = note.getDuration();
							}
						}
						
						for( int iplayer = 0; iplayer < musicPlayers.length; iplayer++ )
						{
							LevelMusicSheetSegment msplayer  = musicPlayers[ iplayer ];
							
							if( !isRest && msplayer.existMusicSegmentInTime( noteTrackTime ) )
							{							
								msplayer.addNewTrack( noteTrackTime, track.getID() );

								msplayer.addNotes( noteTrackTime, track.getID(), Notes );

								Note restNote = Note.createRest( maxNoteDur );
								
								copyNotes.remove( noteTrackTime );
								copyNotes.put( noteTrackTime, restNote );
							}
							else
							{
								playerSheetRests.put( iplayer, new Tuple< String, NumberRange>( track.getID(), new NumberRange( noteTrackTime, noteTrackTime + maxNoteDur ) ) );
							}
						}
					}
				}
			}

			NOTES.clear();
			NOTES.putAll( copyNotes );
		}
	}
}
