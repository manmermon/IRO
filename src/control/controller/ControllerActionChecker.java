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
			}
			
			this.archievedTarget.set( false );
			
			this.recoverLevelReach.set( false );
			
			GameStatistic.add( FieldType.CONTROLLER_WAIT_RECORVER_LEVEL );
		}
		
		this.enable = ena;
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

			if( data > this._rng.getMax() )
			{
				if( this.statistic == 0 )
				{
					GameStatistic.add( FieldType.CONTROLER_LEVEL_REACH );
					this.statistic++;
				}

				double timerPercentage = 100;

				if( this.targetTime > 0 )
				{
					double t = -this.targetTime;
					
					synchronized ( this.sync )
					{
						if( this.refTime == null )
						{
							this.refTime = System.nanoTime();
						}
								
						t = ( System.nanoTime() - this.refTime ) / 1e9D;
					}

					timerPercentage = 100 * t  / this.targetTime;						
				}				

				if( timerPercentage >= 100 )
				{	
					if( this.statistic == 1 )
					{
						GameStatistic.add( FieldType.CONTROLLER_MAINTAIN_LEVEL_REACH );
						this.statistic++;
					}

					this.refTime = null;

					if( !this.archievedTarget.get( ) 
							&& this.enable 
							&&  this.recoverLevelReach.get() )
					{	
						this.archievedTarget.set( true );
						
						ScreenControl.getInstance().setUpdateLevelInputGoal( 100 );
						
						this.fireActionEvent( InputActionEvent.ACTION_DONE );						
					}
				}
				else
				{
					ScreenControl.getInstance().setUpdateLevelInputGoal( timerPercentage );
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
					ScreenControl.getInstance().setUpdateLevelInputGoal( 0 );
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
