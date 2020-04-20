package control.controller;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.event.EventListenerList;

import control.ScreenControl;
import control.events.IInputControllerListener;
import control.events.InputActionEvent;
import control.events.InputActionListerner;
import control.events.InputControllerEvent;
import general.NumberRange;
import statistic.GameStatistic;
import statistic.GameStatistic.FieldType;

public class ControllerActionChecker implements IInputControllerListener  
{	
	public int selectedChannel = 0;
	
	private NumberRange _rng = null;

	private AtomicBoolean archievedTarget = new AtomicBoolean( false );
	
	private EventListenerList listeners = null;
	
	private double targetTime;
	
	private Long refTime;
	private Object sync = new Object();			
	
	private boolean enable = false;
	
	private AtomicBoolean recoverLevelReach = new AtomicBoolean( false );
	
	private int statistic = 0;
	private boolean recoverLevelReported = false;
	
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
							ScreenControl.getInstance().setUpdateLevelInputGoal( 0 );
						};
					};
					
					t.start();
						
				}
							
				this.enable = ena;
			}
		}
	}
	
	@Override
	public void InputControllerEvent( InputControllerEvent ev )
	{
		double[] values = ev.getInputValues();
				
		if( values != null 
				&& this.selectedChannel >= 0 
				&& this.selectedChannel < values.length
		  )
		{
			double data = values[ this.selectedChannel ];
			double timerPercentage = 0;

			synchronized ( this.sync )
			{
				if( data > this._rng.getMax() )
				{
					if( this.statistic == 0 )
					{
						GameStatistic.add( FieldType.CONTROLER_LEVEL_REACH );
						this.statistic++;
					}
	
					timerPercentage  = 100;
	
					if( this.targetTime > 0 )
					{
						double t = -this.targetTime;
						
						if( this.refTime == null )
						{
							this.refTime = System.nanoTime();
						}

						t = ( System.nanoTime() - this.refTime ) / 1e9D;
	
						timerPercentage = 100 * t  / this.targetTime;						
					}				
	
					if( timerPercentage >= 100 )
					{	
						if( this.statistic == 1 )
						{
							GameStatistic.add( FieldType.CONTROLLER_MAINTAIN_LEVEL_REACH );
							this.statistic++;
						}
	
						/*
						synchronized ( this.sync )
						{
							this.refTime = null;
						}
						//*/
	
						if( !this.archievedTarget.get( ) 
								&& this.enable 
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
						GameStatistic.add( FieldType.CONTROLLER_MAINTAIN_LEVEL_FINISH );
						this.statistic = 0;
					}
					
					this.recoverLevelReported = false;
					
					this.refTime = null;
				}
				else
				{	
					this.statistic = 0;
					
					if( this.archievedTarget.getAndSet( false ) && this.enable )
					{		
						this.fireActionEvent( InputActionEvent.RECOVER_DONE );
					}
					
					if( !this.recoverLevelReach.getAndSet( true ) )
					{
						GameStatistic.add( FieldType.CONTROLLER_RESTORED_LEVEL );
						
						this.recoverLevelReported = true;
					}
					else if( !this.recoverLevelReported )
					{
						GameStatistic.add( FieldType.CONTROLER_RECOVER_LEVEL_REACH );
					}				
				}
				
				if( this.enable )
				{
					ScreenControl.getInstance().setUpdateLevelInputGoal( timerPercentage );
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
		InputActionEvent event = new InputActionEvent( this, typeEvent );

		InputActionListerner[] listeners = this.listeners.getListeners( InputActionListerner.class );

		for (int i = 0; i < listeners.length; i++ ) 
		{
			listeners[ i ].InputAction( event );
		}
	}
}
