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

package control.scenes.level;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Semaphore;

import gui.game.component.event.FretEvent;
import gui.game.component.event.FretEventListener;
import gui.game.component.sprite.Fret;
import gui.game.component.sprite.ISprite;
import gui.game.component.sprite.InputGoal;
import gui.game.component.sprite.MusicNoteGroup;
import gui.game.component.sprite.Score;
import gui.game.screen.IScene;
import gui.game.screen.level.Level;
import tools.MusicSheetTools;
import config.IOwner;
import control.events.BackgroundMusicEventListener;
import control.events.InputActionEvent;
import control.events.SceneEvent;
import control.music.MusicPlayerControl;
import control.scenes.AbstractSceneControl;
import exceptions.SceneException;
import control.events.BackgroundMusicEvent;

public class LevelControl extends AbstractSceneControl 
							implements BackgroundMusicEventListener
										, FretEventListener
{		
	private ConcurrentSkipListSet< Integer > actionOwner;
	private boolean backgroundMusicEnd = false;
	
	private boolean noteIntoFret = false;
	
	private int consecutiveErrors = 0;
	
	//private Map< Integer, Boolean > playerAchievedTarget = new HashMap<Integer, Boolean>();
	
	private Semaphore changeSpeedSemaphore = new Semaphore( 1, true );
	
	/**
	 * @throws SceneException 
	 * 
	 */
	public LevelControl( ) throws SceneException 
	{	
		super();
		
		this.actionOwner = new ConcurrentSkipListSet< Integer >();
	}
	
	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#setScene(GUI.game.screen.IScene)
	 */
	/*
	@Override
	public void setScene(IScene scene) throws SceneException
	{
		super.setScene( scene );
		
		Level lv = (Level)scene;
		
		lv.getFret().addFretEventListener( this );
	}
	//*/
				
	@Override
	public void startScene() throws Exception 
	{
		// TODO Auto-generated method stub
		super.startScene();
		
		if( super.scene != null )
		{	
			Level lv = (Level)this.scene;
			lv.enableLevel();
			lv.initiateLevel();
			
			lv.getFret().addFretEventListener( this );
			
			MusicPlayerControl.getInstance().setBackgroundMusicPatter( lv.getBackgroundPattern() );
			MusicPlayerControl.getInstance().addBackgroundMusicEvent( this );
			
			MusicPlayerControl.getInstance().setMuteSession( lv.isMuteSession() );
			
			MusicPlayerControl.getInstance().startMusic();
		
			
			
			//MusicPlayerControl.getInstance().setPlayerMusicSheets( lv.getPlayerSheets() );
		}
		else
		{
			throw new SceneException( "Level null" );
		}
	}

	/*
	 * (non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#updatedLoopAfterSetScene()
	 */
	@Override
	protected void updatedLoopAfterUpdateScene() 
	{		
		synchronized( this.scene )
		{
			Level lv = (Level)this.scene;
			Fret fret = lv.getFret();
			
			List< ISprite > Notes = this.scene.getSprites( Level.NOTE_ID, true );
	
			if( fret != null 
					&& Notes != null 
					&& !Notes.isEmpty() )
			{
				boolean noteInFret = false;
								
				for( ISprite __Note : Notes )
				{
					MusicNoteGroup note = (MusicNoteGroup) __Note;
					
					if( fret.isNoteIntoFret( note ) )
					{	
						noteInFret = true;
						
						if( note.isGhost() )
						{
							if( !note.isSelected() )
							{
								note.setSelected( true );
								note.setState(gui.game.component.sprite.MusicNoteGroup.State.ACTION );
							}
						}
						else if( !this.actionOwner.isEmpty() )
						{
							IOwner noteOwner = note.getOwner();
														
							Integer act;
							Iterator< Integer > itActOwner = this.actionOwner.iterator();
							while( itActOwner.hasNext() )
							{
								act = itActOwner.next();
								
								if( !note.isSelected() && noteOwner != null && noteOwner.getId() == act )
								{
									itActOwner.remove();
									
									note.setSelected( true );
									note.setState(gui.game.component.sprite.MusicNoteGroup.State.ACTION );
	
									for( ISprite score : this.scene.getSprites( Level.SCORE_ID, true ) )
									{
										Score sc = (Score)score;
										IOwner owner = sc.getOwner();
	
										if( owner != null && owner.getId() == act )
										{
											sc.incrementScore();
										}
									}	
									
									break;
								}
							}
						}
						else if( !note.isSelected() )
						{
							note.setState( gui.game.component.sprite.MusicNoteGroup.State.WAITING_ACTION );
						}
					}
					else
					{						
						if( !note.isSelected() )
						{
							note.setState(gui.game.component.sprite.MusicNoteGroup.State.PREACTION );						
						}
					}					
				}				
				
				this.actionOwner.clear();
				
				this.noteIntoFret = noteInFret;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#targetDone()
	 */
	@Override
	protected void targetDone() throws Exception 
	{
		synchronized( this )
		{
			if( this.scene.getNumberOfSprites( Level.NOTE_ID ) < 1 
					&& this.backgroundMusicEnd )
			{		
				super.stopThread = true;
				super.fireSceneEvent( SceneEvent.END );
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#specificUpdateScene(control.inputs.IInputAction)
	 */
	@Override
	public synchronized void specificUpdateScene( List< InputActionEvent > acts ) throws SceneException
	{			
		if( acts != null )
		{
			synchronized( this.scene )
			{
				for( InputActionEvent act : acts )
				{
					if( act.getType() == InputActionEvent.ACTION_DONE )
					{
						IOwner owner = act.getActionOwner();
						
						this.actionOwner.add( owner.getId() );
					}
				}				
			}
		}
	}

	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#setInputables(GUI.screens.IScene)
	 */
	@Override
	protected void setInputables(IScene scene)
	{	
		/*
		MouseStrokeAction mouse = new MouseStrokeAction();
		
		scene.getScene().addMouseListener( mouse );		
		scene.getScene().addMouseMotionListener( mouse );
		
		KeystrokeAction keyboard = new KeystrokeAction();
		
		scene.getScene().addKeyListener( keyboard );
		*/		
	}
		
	/*
	 * (non-Javadoc)
	 * @see control.ISceneControl#destroyScene()
	 */
	@Override
	public void specificDestroyScene()
	{
		try
		{
			MusicPlayerControl.getInstance().stopMusic();
		} 
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#specificCleanUp()
	 */
	@Override
	protected void specificCleanUp()
	{
		//this.actionDone = null;
		try
		{
			MusicPlayerControl.getInstance().stopMusic();
		} 
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see @see control.events.BackgroundMusicEventListener#BackgroundMusicEvent(control.events.BackgroundMusicEvent)
	 */
	@Override
	public void BackgroundMusicEvent( BackgroundMusicEvent event ) 
	{		
		this.backgroundMusicEnd = this.backgroundMusicEnd || event.getType() == BackgroundMusicEvent.END;
	}

	/*(non-Javadoc)
	 * @see @see control.scenes.AbstractSceneControl#getSceneClass()
	 */
	@Override
	protected Class getSceneClass()
	{
		return Level.class;
	}

	/*(non-Javadoc)
	 * @see @see GUI.game.component.event.FretEventListener#FretEvent(GUI.game.component.event.FretEvent)
	 */
	//long t = 0;
	@Override
	public synchronized void FretEvent( FretEvent ev )
	{
		MusicNoteGroup note = ev.getNote();
		
		if( note != null && !note.isGhost() )
		{
			switch( ev.getType() )
			{
				case FretEvent.NOTE_EXITED:
				{
					if( !note.isGhost() && !note.isNoteExitFret() )
					{
						note.setNoteExitFret( true );
						
						int playerID = note.getOwner().getId();
					
						int thr = 20000;			
										
						if( !note.isSelected() )
						{							
							MusicPlayerControl.getInstance().playDissonantTrack( playerID, note.getDissonantNotes() );
							
							//*
							this.consecutiveErrors++;		
							
							if( this.consecutiveErrors > thr  )
							{
								this.consecutiveErrors = 0;
								this.changeSceneSpeed( note.getOwner(), false, false );
							}
							//*/
						}
						else
						{
							//*							
							this.consecutiveErrors--;
							
							if( this.consecutiveErrors < -thr  )
							{
								this.consecutiveErrors = 0;
								this.changeSceneSpeed( note.getOwner(), true, false );
							}
							//*/
						}
					}
					
					break;
				}
				case FretEvent.NOTE_ENTERED:
				{
					//t = System.currentTimeMillis();
					
					break;
				}
			}
		}
	}
	
	public synchronized void updateInputGoal( double percentageTime, int rep, IOwner owner )
	{
		if( this.scene != null && owner != null )
		{
			List< ISprite > targets = this.scene.getSprites( Level.INPUT_TARGET_ID, true );
			
			for( ISprite tg : targets )
			{
				InputGoal goal = (InputGoal)tg;
				IOwner gOwner = goal.getOwner();
				
				if( gOwner.getId() == owner.getId() )
				{
					goal.setPercentage( percentageTime, rep );
					
					break;
				}
			}
		}
	}

	/*(non-Javadoc)
	 * @see @see control.scenes.ISceneControl#activeInputController()
	 */
	@Override
	public boolean activeInputController()
	{
		synchronized ( this )
		{
			return this.noteIntoFret;
		}
	}
	
	@Override
	public void setPauseScene(boolean pause ) 
	{
		super.setPauseScene( pause );
		MusicPlayerControl.getInstance().setPauseMusic( pause );
	}

	@Override
	public void changeSceneSpeed( final IOwner player, final boolean reduceVel, final boolean changeTempo  ) 
	{
		if( this.changeSpeedSemaphore.tryAcquire() )
		{
			Thread t = new Thread()
			{
				@Override
				public synchronized void run() 
				{
					try
					{
						MusicPlayerControl.getInstance().setPauseMusic( true );
						
						Level lv = (Level)scene;
						
						double percReaction = 0.8;
						double percRecover = 0.8;
						
						if( !reduceVel )
						{
							percReaction = 1.2;
							percRecover = 1.2;
						}
						
						lv.changeSpeed( player, percReaction, percRecover / percRecover );
						
						/*
						List< Settings > players = lv.getPlayers();		
						if( players != null )
						{
							Map< Integer, Double > velsByPlayer = lv.getSpeedForPlayers();
							for( Settings pl : players )
							{	
								Double vel = velsByPlayer.get( pl.getPlayer().getId() );
								if( vel != null )
								{
									//double reactionTime = SceneTools.getAvatarReactionTime(  lv.getFret().getFretWidth(), vel );
									//double prop = (double)tempo / newTempo;
									//reactionTime *= prop;
									//double newVel = SceneTools.getAvatarSpeed( lv.getFret().getFretWidth(), reactionTime );
									
									lv.changeSpeed( pl.getPlayer(), percReaction, percRecover );
								}
							}
						}
						//*/
						
						if( changeTempo )
						{
							int tempo = MusicPlayerControl.getInstance().getMusicTempo();
							
							double tempoTime = MusicSheetTools.getQuarterTempo2Second( tempo );
							tempoTime *= percReaction;
							int newTempo = MusicSheetTools.getQuarterSecond2Tempo( tempoTime );
							
							//double newTempoTime = MusicSheetTools.getQuarterTempo2Second( newTempo );
							
							MusicPlayerControl.getInstance().changeTempo( newTempo );
							
							try 
							{
								MusicPlayerControl.getInstance().startMusic();
							}
							catch (Exception e) 
							{
								e.printStackTrace();
							}		
						}
					}
					catch (Exception e) 
					{
					}
					finally 
					{
						if( changeSpeedSemaphore.availablePermits() < 1 ) 
						{
							changeSpeedSemaphore.release();
						}
						
						MusicPlayerControl.getInstance().setPauseMusic( false );
					}	
				}
			};
			
			t.setName( "LevelControl-ChangeSpeed" );
			t.start();
		}
	}
}
