package deprecated.control;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.event.EventListenerList;

import gui.game.component.IPossessable;
import config.IOwner;
import control.ScreenControl;
import control.events.IInputControllerListener;
import control.events.InputActionEvent;
import control.events.InputActionListerner;
import control.events.InputControllerEvent;
import general.NumberRange;
import statistic.RegistrarStatistic;
import statistic.RegistrarStatistic.FieldType;

public class ControllerActionChecker_copy implements IInputControllerListener, IPossessable 
{	
	public int selectedChannel = 0;
	
	private NumberRange _rng = null; // movement range

	private AtomicBoolean archievedTarget = new AtomicBoolean( false );
	
	private EventListenerList listeners = null;
	
	private double targetTime; // aimed time
	private int repetitions = 1; // number of movements to do
	 
	private Long refTime; // chromometer
	private int repCounter = 0; // number of movs. done
	private Object sync = new Object();			
	
	private boolean enabledController = false;
	
	private AtomicBoolean recoverLevelReach = new AtomicBoolean( false );
	
	private int statistic = 0;
	private boolean recoverLevelReported = false;
	
	private IOwner owner = null;
	private int ownerID = IOwner.ANONYMOUS;
	
	private boolean enableCheck = true;
	
 	public ControllerActionChecker_copy( int selChannel, NumberRange inputThreshold, double time, int rep ) 
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
		
		this.repetitions = rep;
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
				
				this.repCounter = 0;
			}
			
			synchronized ( this.sync )
			{
				if( data > this._rng.getMax() ) // target zone
				{
					if( this.statistic == 0 )
					{
						RegistrarStatistic.add( this.ownerID, FieldType.CONTROLLER_ACTION_LEVEL_REACH );
						
						this.statistic++;
					}
	
					//
					// Check: time in target zone
					//
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
	
					//
					// target time archieved
					//
					if( timerPercentage >= 100 )
					{	
						if( this.statistic == 1 )
						{
							RegistrarStatistic.add( this.ownerID, FieldType.CONTROLLER_MAINTAIN_ACTION_LEVEL );
							this.statistic++;
						}
	
						if( !this.archievedTarget.get( ) 
								&& enable 
								&&  this.recoverLevelReach.get() )
						{	
							this.archievedTarget.set( true );
							
							this.repCounter++;
							
							this.repCounter = ( this.repCounter >= this.repetitions ) ? 0 : this.repCounter;
							
							if( this.repCounter < 1 )
							{
								this.fireActionEvent( InputActionEvent.ACTION_DONE );
							}
						}
					}
					
					this.recoverLevelReported = false;
				}
				else if ( this._rng.within( data ) ) // intermediate are
				{
					if( this.statistic > 0 )
					{
						RegistrarStatistic.add( this.ownerID, FieldType.CONTROLLER_EXIT_ACTION_LEVEL );
						this.statistic = 0;
					}
					
					this.recoverLevelReported = false;
					
					this.refTime = null;
				}
				else // recovered area
				{	
					this.statistic = 0;
					
					if( this.archievedTarget.getAndSet( false ) && enable )
					{		
						this.fireActionEvent( InputActionEvent.RECOVER_DONE );
					}
					
					if( !this.recoverLevelReach.getAndSet( true ) )
					{
						RegistrarStatistic.add( this.ownerID, FieldType.CONTROLLER_ENABLE_MOVEMENT );
						
						this.recoverLevelReported = true;
					}
					else if( !this.recoverLevelReported )
					{
						RegistrarStatistic.add( this.ownerID, FieldType.CONTROLLER_RECOVERY_LEVEL_REACH );
					}				
				}
				
				final int rep = this.repCounter;
				if( enable )
				{
					final double tp = timerPercentage;					
					Thread t = new Thread() 
					{
						public void run() 
						{
							super.setName( "ScreenControl.getInstance().setUpdateLevelInputGoal( timerPercentage )" );
							ScreenControl.getInstance().setUpdateLevelInputGoal( tp, rep, owner );
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
							ScreenControl.getInstance().setUpdateLevelInputGoal( 0, repCounter, owner );
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
