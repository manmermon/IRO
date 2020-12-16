package control.controller;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.event.EventListenerList;

import GUI.game.component.IPossessable;
import config.IOwner;
import control.ScreenControl;
import control.events.IInputControllerListener;
import control.events.InputActionEvent;
import control.events.InputActionListerner;
import control.events.InputControllerEvent;
import general.NumberRange;
import statistic.RegistrarStatistic;
import statistic.RegistrarStatistic.FieldType;

public class ControllerActionChecker implements IInputControllerListener, IPossessable 
{	
	public int selectedChannel = 0;
	
	private NumberRange _rng = null;

	private AtomicBoolean archievedTarget = new AtomicBoolean( false );
	
	private EventListenerList listeners = null;
	
	private double targetTime;
	
	private Long refTime;
	private Object sync = new Object();			
	
	private boolean enabledController = false;
	
	private AtomicBoolean recoverLevelReach = new AtomicBoolean( false );
	
	private int statistic = 0;
	private boolean recoverLevelReported = false;
	
	private IOwner owner = null;
	private int ownerID = IOwner.ANONYMOUS;
	
	private boolean enableCheck = true;
	
 	public ControllerActionChecker( int selChannel, NumberRange inputThreshold, double time ) 
	{
		//super.setName( this.getClass().getSimpleName() );
		
		if( inputThreshold == null )
		{
			throw new IllegalArgumentException( "Input range null." );
		}
		
		this.listeners = new EventListenerList();
		
		this.selectedChannel = selChannel;
		
		this._rng = inputThreshold;
		
		this.targetTime = time;
	}	
	
 	/*
	public void enableProcessInputControllerEvent( boolean ena )
	{
		if( this.enable != ena )
		{	
			synchronized ( this.sync )
			{
				this.refTime = null;
				
				this.archievedTarget.set( false );
				
				this.recoverLevelReach.set( false );
				
				GameStatistic.add( FieldType.CONTROLLER_WAIT_RECORVER_LEVEL );
				
				if( !ena )
				{
					Thread t = new Thread()
					{
						public void run() 
						{
							super.setName( "ScreenControl.getInstance().setUpdateLevelInputGoal( 0 )");
							ScreenControl.getInstance().setUpdateLevelInputGoal( 0 );
						};
					};
					
					t.start();
						
				}
							
				this.enable = ena;
			}
		}
	}
	*/
	
	@Override
	public void InputControllerEvent( InputControllerEvent ev )
	{
		double[] values = ev.getInputValues();
		double time = ev.getTime();
		
		if( this.enableCheck 
				&& values != null 
				&& this.selectedChannel >= 0 
				&& this.selectedChannel < values.length
		  )
		{
			double[] vt = new double[ values.length + 1 ];
			System.arraycopy( values, 0, vt, 0, values.length );
			vt[ vt.length -1 ] = time;
			RegistrarStatistic.addControllerData( ownerID, vt );
			
			double data = values[ this.selectedChannel ];
			double timerPercentage = 0;

			boolean enable = ScreenControl.getInstance().activeInputControl();
			
			if( enable && !this.enabledController)
			{
				refTime = null;
				
				this.enabledController = true;
			}
			
			synchronized ( this.sync )
			{
				if( data > this._rng.getMax() )
				{
					if( this.statistic == 0 )
					{
						RegistrarStatistic.add( this.ownerID, FieldType.CONTROLER_LEVEL_REACH );
						
						this.statistic++;
					}
	
					timerPercentage  = 100;
	
					if( this.targetTime > 0 )
					{	
						if( this.refTime == null )
						{
							this.refTime = System.nanoTime();
						}

						double t = ( System.nanoTime() - this.refTime ) / 1e9D;
	
						timerPercentage = 100 * t  / this.targetTime;						
					}				
	
					if( timerPercentage >= 100 )
					{	
						if( this.statistic == 1 )
						{
							RegistrarStatistic.add( this.ownerID, FieldType.CONTROLLER_MAINTAIN_LEVEL_REACH );
							this.statistic++;
						}
	
						if( !this.archievedTarget.get( ) 
								&& enable 
								&&  this.recoverLevelReach.get() )
						{	
							this.archievedTarget.set( true );
							
							this.fireActionEvent( InputActionEvent.ACTION_DONE );						
						}
					}
					
					this.recoverLevelReported = false;
				}
				else if ( this._rng.within( data ) )
				{
					if( this.statistic > 0 )
					{
						RegistrarStatistic.add( this.ownerID, FieldType.CONTROLLER_MAINTAIN_LEVEL_FINISH );
						this.statistic = 0;
					}
					
					this.recoverLevelReported = false;
					
					this.refTime = null;
				}
				else
				{	
					this.statistic = 0;
					
					if( this.archievedTarget.getAndSet( false ) && enable )
					{		
						this.fireActionEvent( InputActionEvent.RECOVER_DONE );
					}
					
					if( !this.recoverLevelReach.getAndSet( true ) )
					{
						RegistrarStatistic.add( this.ownerID, FieldType.CONTROLLER_RESTORED_LEVEL );
						
						this.recoverLevelReported = true;
					}
					else if( !this.recoverLevelReported )
					{
						RegistrarStatistic.add( this.ownerID, FieldType.CONTROLER_RECOVER_LEVEL_REACH );
					}				
				}
				
				if( enable )
				{
					final double tp = timerPercentage;
					Thread t = new Thread() 
					{
						public void run() 
						{
							super.setName( "ScreenControl.getInstance().setUpdateLevelInputGoal( timerPercentage )" );
							ScreenControl.getInstance().setUpdateLevelInputGoal( tp, owner );
						};
					};
					t.setName( this.getClass().getSimpleName() + "-setUpdateLevelInputGoal");
					t.start();
				}
				else if( this.enabledController )
				{
					this.enabledController = false;
					//this.refTime = null;
					
					Thread t = new Thread() 
					{
						public void run() 
						{
							super.setName( "ScreenControl.getInstance().setUpdateLevelInputGoal( 0 )" );
							ScreenControl.getInstance().setUpdateLevelInputGoal( 0, owner );
						};
					};
					t.setName( this.getClass().getSimpleName() + "-setUpdateLevelInputGoal" );
					t.start();
				}					
			}
		}
	}
	
	public synchronized void addInputActionListerner( InputActionListerner listener ) 
	{
		this.listeners.add( InputActionListerner.class, listener );
	}

	public synchronized void removeInputActionListerner( InputActionListerner listener ) 
	{
		this.listeners.remove( InputActionListerner.class, listener );		
	}
	
	/**
	 * 
	 * @param typeEvent
	 */
	private synchronized void fireActionEvent( int typeEvent )
	{
		InputActionEvent event = new InputActionEvent( this, typeEvent, this.owner );

		InputActionListerner[] listeners = this.listeners.getListeners( InputActionListerner.class );

		for (int i = 0; i < listeners.length; i++ ) 
		{
			listeners[ i ].InputAction( event );
		}
	}

	/*(non-Javadoc)
	 * @see @see GUI.game.component.IPossessable#setOwner(config.IOwner)
	 */
	@Override
	public void setOwner(IOwner owner)
	{
		this.owner = owner;
		if( owner != null )
		{
			this.ownerID = this.owner.getId();
		}
	}

	/*(non-Javadoc)
	 * @see @see GUI.game.component.IPossessable#getOwner()
	 */
	@Override
	public IOwner getOwner()
	{
		return this.owner;
	}

	@Override
	public void setEnableInputController(boolean enable)
	{
		this.enableCheck = enable;
	}
}
