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

package gui.game.component.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jfugue.midi.MidiDictionary;
import org.jfugue.theory.Note;

import gui.game.component.IPossessable;
import gui.game.component.event.SpriteEvent;
import gui.game.component.event.SpriteEventListener;
import config.IOwner;
import config.Player;
import control.music.MusicPlayerControl;
import general.ArrayTreeMap;
import general.NumberRange;
import image.BasicPainter2D;
import image.icon.MusicInstrumentIcons;
import music.sheet.IROTrack;
import statistic.RegistrarStatistic;
import statistic.RegistrarStatistic.GameFieldType;
import tools.MusicSheetTools;

public class MusicNoteGroup extends AbstractSprite implements IPossessable
{ 	
	public static final int REST = -1;
	public static final int SI = 0;
	public static final int LA = 1;
	public static final int SOL = 2;
	public static final int FA = 3;
	public static final int MI = 4;
	public static final int RE = 5;
	public static final int DO = 6;
	
	public static enum State { PREACTION, WAITING_ACTION, ACTION };
	
	//private Pentragram pentagram;
	private double shiftVelocity; // pixel by second
	private int shiftDirection = -1;
		
	private boolean isSelected;
		
	private boolean isGhost = false;
	
	private Point2D.Double previousLocation = null;
	
	private List< IROTrack > noteTracks;
	private List< IROTrack > dissonantNoteTracks;
	
	private BufferedImage noteImg = null;
	
	private Double timeOnScreen = Double.NaN;	
	
	private Double prevGameTime = 0.0;
	private Double delay = 0D;
	
	private String trackID = "";
	
	private IOwner _player = new Player();
		
	//
	//
	// ANIMATION SETTTINGS
	//
	//
	
	private boolean animationActive = false;
	
	private final NumberRange angleRange = new NumberRange( -45, 45 );
	private final int numAngles = 7;
	private double stepAngle = angleRange.getRangeLength() / numAngles;
	private int directionAngle = 1;	
	private double currentAngle = 0;
	
	//private long time = -1;	
	private long animationTime = -1;
	private final long totalTimeAnimation = 800; // milliseconds
	private long timeUpdate = totalTimeAnimation / numAngles; // milliseconds
	
	private Color preactionColor = Color.RED;
	private Color waitingActionColor = Color.BLUE;
	private Color actionColor = Color.GREEN;
	
	private State state = State.PREACTION;
	
	private BufferedImage circPreAction = null;
	private BufferedImage circAction = null;
	private BufferedImage circWaitAction = null;
	
	private int notePasteX = 0;
	private int notePasteY = 0;
	
	private boolean noteExitFret = false;
	private boolean noteEnterFret = false;
	
	public MusicNoteGroup( String track
							, double timeScreen
							, List< IROTrack > Notes
							, String noteID
							, int railHeight
							, int initLoc 
							, double shiftVel
							, boolean ghost ) 
	{
		super( noteID );
		
		this.trackID = "track-" + System.nanoTime();
		
		this.noteTracks = new ArrayList< IROTrack >();
		this.dissonantNoteTracks = new ArrayList< IROTrack >();
		
		if( Notes != null )
		{
			this.noteTracks.addAll( Notes );	
		
			boolean restTrack = true;
			
			String instrument = null;
			double firstNoteTime = 0D;
			double duration = 0;
			int tempo = 120;
			
			if( !Notes.isEmpty() )
			{			
				IROTrack trackID = Notes.get( 0 );
				
				instrument = Notes.get( 0 ).getInstrument();
				firstNoteTime = Notes.get( 0 ).getStartTimeFirstNote();
				tempo = Notes.get( 0 ).getTempo();
				
				for( IROTrack tr : Notes )
				{
					if( duration < tr.getTrackDuration() )
					{
						duration = tr.getTrackDuration();
					}
					
					restTrack = restTrack && tr.isRestTrack();
				}
			}
			
			if( restTrack )
			{
				IROTrack tr = new IROTrack( trackID );
				if( instrument != null )
				{
					tr.setInstrument( instrument );
				}
				
				duration = ( duration <= 0 ) ? 1 : duration;
			
				Note n = new Note();
				double wt = MusicSheetTools.getWholeTempo2Second( tempo );
				n.setDuration( duration / wt );
				
				tr.addNote( firstNoteTime, n );
				
				Notes = new ArrayList<IROTrack>();
				Notes.add( tr );
			}
			
			for( IROTrack t : Notes )
			{
				IROTrack disT = new IROTrack( t.getID() );
				disT.setInstrument( t.getInstrument() );
				disT.setTempo( t.getTempo() );
				disT.setVoice( t.getVoice() );
				
				ArrayTreeMap< Double,  Note > tN = t.getTrackNotes(); 
				
				for( Double time : tN.keySet() )
				{
					List< Note > nots = new ArrayList<Note>();
					List< Note > nL = tN.get( time );
					for( Note nt : nL )
					{					
						Note n = new Note( nt );
						byte v = n.getValue();						
						n.changeValue( ( v == 127) ? -1 : 1 );
						nots.add( n );
					}
					
					disT.addNotes( nots );
				}
				
				if( !disT.isEmpty() )
				{
					this.dissonantNoteTracks.add( disT );
				}
			}
		}
						
		//this.pentagram = pen;		
		this.shiftVelocity = shiftVel;
			
		//int y = this.pentagram.getRailHeight();
		int y = railHeight;
				
		Dimension d = new Dimension();
		d.width = y;
		d.height = y;
		
		if( d.width < 1 )
		{
			d.width = 1;
		}
		
		if( d.height < 1 )
		{
			d.height = 1;
		}
		
		super.setSize( d );
		
		int noteLine = REST;
		
		noteScreenLoc:
		for( IROTrack Track : this.noteTracks )
		{
			this.trackID += Track.getID();
			
			for( List< Note > notes : Track.getTrackNotes().values() )
			{	
				for( Note n : notes )
				{
					if( !n.isRest() )
					{
						noteLine = getNoteValue( n.toString() );			
						break noteScreenLoc;
					}
				}
			}
		}
		
		super.screenLoc = new Point2D.Double( initLoc, noteLine * y + ( y - super.getSize().width ) / 2);
		
		this.isGhost = ghost;
		
		this.timeOnScreen = timeScreen;		
		
		super.addSpriteEventListener( new SpriteEventListener()
		{	
			@Override
			public void SpriteEvent(SpriteEvent ev)
			{	
				switch ( ev.getType()  )
				{
					case SpriteEvent.ON_SCREEN:
					{
						int playerID = Player.ANONYMOUS;
						if( _player != null )
						{
							playerID = _player.getId();
						}
						
						RegistrarStatistic.addGameData( playerID, GameFieldType.NOTE_SHOW );
						
						synchronized ( delay )
						{
							//prevGameTime = MusicPlayerControl.getInstance().getBackgroundMusicTime();
							
							//time = System.nanoTime();
							//delay += ( timeOnScreen - MusicPlayerControl.getInstance().getPlayTime());
							//System.out.println("MusicNoteGroup.MusicNoteGroup() " + delay);
						}						
						
						break;
					}
					case SpriteEvent.OUTPUT_SCREEN:
					{
						int playerID = Player.ANONYMOUS;
						if( _player != null )
						{
							playerID = _player.getId();
						}
						
						RegistrarStatistic.addGameData( playerID, GameFieldType.NOTE_REMOVE );
						break;
					}
					default:
					{
						break;
					}
				}
			}
		});
		
		this.previousLocation = this.getNoteLocation();
	}
	
	public void copyScreenProperties( MusicNoteGroup note )
	{
		if( note != null )
		{
			this.setScreenLocation( note.getScreenLocation() );
			
			this.setSelected( note.isSelected() );
			this.setGhost( note.isGhost() );
			this.setVisible( note.isVisible() );
			
			super.setFrameBounds( note.frameBounds );
			
			this.setPreactionColor( note.getPreactionColor() );
			this.setWaitingActionColor( note.getWaitingActionColor() );
			this.setActionColor( note.getActionColor() );
			
			this.setOwner( note.getOwner() );
			
			this.setImage( note.getNoteImg() );
			
			this.currentAngle = note.currentAngle;
			
			this.state = note.state;
			
			this.directionAngle = note.directionAngle;
		}
	}
	
	public Double getMusicTime() 
	{
		return this.timeOnScreen;
	}
	
	public void setPreactionColor( Color c )
	{
		if( c != null )
		{
			this.preactionColor = c;
		}
	}
	
	public Color getPreactionColor()
	{
		return preactionColor;
	}
	
	public void setWaitingActionColor( Color c )
	{
		if( c != null )
		{
			this.waitingActionColor = c;
		}
	}
	
	public Color getWaitingActionColor() 
	{
		return waitingActionColor;
	}
	
	public void setActionColor( Color c )
	{
		if( c != null )
		{
			this.actionColor = c;
		}
	}
	
	public Color getActionColor() 
	{
		return actionColor;
	}
	
	public void setState( State state )
	{
		this.state = state;
	}
	
	public String getTrackID()
	{
		return this.trackID;
	}	
	
	public void setImage( BufferedImage img )
	{
		//this.noteImg = img;
		
		if( img != null )
		{
			/*
			Dimension size = super.getBounds().getSize();
			
			int l = (int)Math.max( size.getWidth(), size.getHeight() );
			int s = (int)Math.sqrt( l * l / 2 );  
			
			if( s <= 0 )
			{
				s = 1;
			}			
			
			this.noteImg = (BufferedImage)BasicPainter2D.copyImage( img.getScaledInstance( s 
																	, s
																	, BufferedImage.SCALE_SMOOTH ) );
			//*/
			
			this.noteImg = img;
		}
		
		this.animationActive = ( this.noteImg != null );
				
	}
	
	public BufferedImage getNoteImg() 
	{
		return noteImg;
	}
	
	public void setTempo( int bpm )
	{
		for( IROTrack track : this.noteTracks )
		{
			track.setTempo( bpm );
		}
	}
	
	public void setGhost( boolean ghost )
	{
		this.isGhost = ghost;
	}
	
	private static int getNoteValue( String n )
	{ 
		int note = DO;
		
		char noteCode = n.charAt( 0 );
		
		switch ( noteCode ) 
		{
			case 'A':
			{
				note = LA;
				break;
			}
			case 'B':
			{
				note = SI;
				break;
			}
			case 'D':
			{
				note = RE;
				break;
			}
			case 'E':
			{
				note = MI;
				break;
			}
			case 'F':
			{
				note = FA;
				break;
			}
			case 'G':
			{
				note = SOL;
				break;
			}
			case 'R':
			{
				note = REST;
				break;
			}
		}
		
		return note;
	}
	
	public List< IROTrack > getNotes()
	{
		return this.noteTracks;
	}
	
	public List< IROTrack > getDissonantNotes()
	{
		return this.dissonantNoteTracks;
	}
		
	public double getDuration()
	{
		double dur = 0;
		
		for( IROTrack tr : this.noteTracks )
		{
			if( dur < tr.getTrackDuration() )
			{
				dur = tr.getTrackDuration();
			}
		}
		
		return dur;
	}
		
	public void toggleDirection()
	{
		this.shiftDirection = -this.shiftDirection;
	}
		
	public void setSelected( boolean selected )
	{
		this.isSelected = selected;
	}
	
	public boolean isSelected()
	{
		return this.isSelected;
	}
		
	public Point2D.Double getNoteLocation()
	{
		Point2D.Double loc = new Point2D.Double( );
		loc.x += super.getSize().width / 2 + super.getScreenLocation().x;
		loc.y += super.getSize().height / 2 + super.getScreenLocation().y;
		
		return loc;
	}
		
	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#getSprite()
	 */
	@Override
	protected BufferedImage createSprite() 
	{	
		BufferedImage pic = null;
		
		if( !this.isGhost )
		{	
			if( !this.noteTracks.isEmpty() )
			{	
				/*
				Color c = preactionColor;
				
				switch ( this.state )
				{
					case WAITING_ACTION:
					{
						c = this.waitingActionColor;
						break;
					} 
					case ACTION:
					{
						c = this.actionColor;
						break;
					}
					default:
					{
						break;
					}
				}
				*/
				
				pic = BasicPainter2D.copyImage( this.noteImg ); 
				if( pic == null )
				{				
					Set< Byte > instruments = new TreeSet< Byte >();
					for( IROTrack track : this.noteTracks )
					{
						Byte val = MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get( track.getInstrument().toUpperCase() );
						if( val == null )
						{
							val = Byte.MIN_VALUE;
						}
						
						instruments.add( val );
					}
					
					List< Byte > l = new ArrayList< Byte >();
					l.addAll( instruments );
					
					int s = (int)( Math.max(  super.getSize().width, super.getSize().height ) ) + 1;
					s = (int)Math.abs( Math.sqrt( s * s / 2 ) );
					if( s <= 0 )
					{
						s = 1;
					}
					
					pic = (BufferedImage)MusicInstrumentIcons.getInstrument( l, s, Color.BLACK );
				}
				
				BufferedImage bg = this.circPreAction;
				
				if( bg == null )
				{
					
					int alpha = 170;
					Color c = new Color( this.preactionColor.getRed(), this.preactionColor.getGreen(), this.preactionColor.getBlue(), alpha );
					
					int r = (int)( Math.max(  super.getSize().width, super.getSize().height ) ) + 1;
					
					this.circPreAction = (BufferedImage)BasicPainter2D.circle( 0
																				, 0
																				, r 
																				, c
																				, null );
					
					c = new Color( this.actionColor.getRed(), this.actionColor.getGreen(), this.actionColor.getBlue(), alpha );
					this.circAction = (BufferedImage)BasicPainter2D.circle( 0
																			, 0
																			, r
																			, c
																			, null );
					
					c = new Color( this.waitingActionColor.getRed(), this.waitingActionColor.getGreen(), this.waitingActionColor.getBlue(), alpha );
					this.circWaitAction = (BufferedImage)BasicPainter2D.circle( 0
																				, 0
																				, r
																				, c
																				, null );
					
					bg = this.circPreAction;
					
					this.notePasteX = Math.abs(  bg.getWidth() - pic.getWidth() ) / 2;
					this.notePasteY = Math.abs( bg.getHeight() - pic.getHeight() ) / 2;
				}
				
				switch ( this.state )
				{
					case WAITING_ACTION:
					{
						bg = this.circWaitAction;
						break;
					} 
					case ACTION:
					{
						bg = this.circAction;
						break;
					}
					default:
					{
						break;
					}
				}
				
				pic = (BufferedImage)BasicPainter2D.composeImage( BasicPainter2D.copyImage( bg ), this.notePasteX, this.notePasteY, pic );
				//pic = bg;
				if( this.currentAngle != 0 && this.animationActive )
				{
						pic = BasicPainter2D.rotate( pic, this.currentAngle );
				}					
					
				//basicPainter2D.changeColorPixels( Color.BLACK, c, pic );
				//basicPainter2D.changeColorPixels( Color.BLACK, this.color, 0.25F, pic );				
			}			
		}
		
		return pic;
	}	

	//*
	public void adjustGameTime( double adjustTime )
	{
		this.prevGameTime = adjustTime;
	}
	//*/
	
	/*
	 * (non-Javadoc)
	 * @see GUI.components.ISprite#updateSprite()
	 */
	@Override
	protected void updateSpecificSprite() 
	{
		synchronized ( this.delay )
		{
			double t = 0;

			/*
			if( time >= 0 )
			{
				t = ( System.nanoTime() - this.time ) / 1e9D;
			}

			this.time = System.nanoTime();
			*/

			//double currentMusicTime = MusicPlayerControl.getInstance().getBackgroundMusicTime();
			double currentTime = MusicPlayerControl.getInstance().getPlayTime();
			
			double ctt = ( currentTime - prevGameTime ); 

			if( ctt > 0D )
			{
				t = ctt;
			}
			prevGameTime = currentTime;

			double gradient = this.shiftVelocity * this.shiftDirection;
			double update = t * gradient;

			update += -this.delay * gradient;				
						
			this.delay = 0D;
			
			this.previousLocation = this.getNoteLocation();
			
			super.screenLoc.x += update;
		}			
						
		if( this.animationTime < 0 )
		{
			this.animationTime = System.currentTimeMillis();
		}
		
		if( System.currentTimeMillis() - this.animationTime > this.timeUpdate )
		{
			double newAngle =  this.currentAngle + this.stepAngle * this.directionAngle;
			if( !this.angleRange.within( newAngle ) )
			{
				this.directionAngle *= -1;
				newAngle =  this.currentAngle + this.stepAngle * this.directionAngle;
			}
			
			this.currentAngle = newAngle;
			
			this.animationTime = System.currentTimeMillis();
		}
	}
	
	public void setShiftSpeed( double vel )
	{
		this.shiftVelocity = vel;
	}
	
	public double getShiftSpeed()
	{
		return this.shiftVelocity;
	}
	
	public Point2D.Double getPreviousNoteLocation()
	{
		return this.previousLocation;
	}

	public boolean isGhost() 
	{
		return this.isGhost;
	}
	

	/*(non-Javadoc)
	 * @see @see config.IPossessable#setOwner(config.IOwner)
	 */
	@Override
	public void setOwner(IOwner owner)
	{		
		this._player = owner;
		
		if( this._player == null )
		{
			this._player = new Player();
		}
	}

	/*(non-Javadoc)
	 * @see @see config.IPossessable#getOwner()
	 */
	@Override
	public IOwner getOwner()
	{
		return this._player;
	}
	
	public int getNotesTempo()
	{
		int tempo = 120;
		
		for( IROTrack t : this.noteTracks )
		{
			tempo = t.getTempo();
			break;
		}
		
		return tempo;
	}
	
	public void setNoteExitFret( boolean exit )
	{
		this.noteExitFret = exit;
	}
	
	public boolean isNoteExitFret() 
	{
		return this.noteExitFret;
	}
	
	public void setNoteEnterFret(boolean noteEnterFret) 
	{
		this.noteEnterFret = noteEnterFret;
	}
	
	public boolean isNoteEnterFret() 
	{
		return this.noteEnterFret;
	}
	
	@Override
	public String toString() 
	{
		return "loc=" + super.getScreenLocation() + ", size=" + super.getSize() +", Tracks = " +  this.noteTracks.toString();
	}
}

