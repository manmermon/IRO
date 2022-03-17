package lslStream;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.swing.Timer;
import javax.swing.event.EventListenerList;

import exceptions.UnsupportedDataTypeException;
import general.ConvertTo;
import lslStream.LSL.StreamInlet;
import lslStream.LSLStreamInfo.StreamDataType;
import lslStream.event.IInputLSLDataListener;
import lslStream.event.InputLSLDataEvent;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppable;

public class LSLInputStream extends AbstractStoppableThread 
{
	private LSL.StreamInlet inLet = null;

	private Timer timer = null;
	
	private byte[] byteData;
	private short[] shortData;
	private int[] intData;
	private long[] longData;
	private float[] floatData;
	private double[] doubleData;
	
	private StreamDataType LSLFormatData = StreamDataType.float32;
		
	private double blockTimer = 0;
		
	private EventListenerList listenerList;
	
	private double dataTime;
	
	public LSLInputStream( LSLStreamInfo info ) throws Exception
	{
		if ( info == null )
		{
			throw new IllegalArgumentException( "StreamInfo null");
		}
		
		super.setName( this.getClass().getSimpleName() + "-" + super.getId() );
		
		this.LSLFormatData = info.data_type();

		this.inLet = new StreamInlet( info, 360,  0, false );
					
		this.createArrays();
		
		// Avoid unnecessary buffering data, waste unnecessary system, and network resources.
		this.inLet.close_stream();
		
		this.listenerList = new EventListenerList();		
	}

	protected int createArrayData() throws Exception
	{
		int nBytes = 1;
		switch (this.LSLFormatData)
		{
			case int8 :
			{
				this.byteData = new byte[ this.inLet.info().channel_count() ];			
				break;
	
			}
			case int16:
			{
				nBytes = Short.BYTES;
	
				this.shortData = new short[this.inLet.info().channel_count()  ];
				break;
			}
			case int32:
			{
				nBytes = Integer.BYTES;
	
				this.intData = new int[this.inLet.info().channel_count() ];
				break;
			}
			case int64:
			{
				nBytes = Long.BYTES;
	
				this.longData = new long[ inLet.info().channel_count() ];
				break;
			}
			case float32:
			{
				nBytes = Float.BYTES;
	
				this.floatData = new float[ this.inLet.info().channel_count() ];
				break;
			}
			case double64:
			{
				nBytes = Double.BYTES;
	
				this.doubleData = new double[ this.inLet.info().channel_count() ];
				break;
			}
			default:
			{
				String msg = "Data type (" + this.LSLFormatData + ") of stream input unsupported.";
				throw new UnsupportedDataTypeException( msg );
			}
		}
		return nBytes;
	}
			
	private void createArrays() throws Exception 
	{
		this.createArrayData();
		
		double samplingRate = this.inLet.info().sampling_rate();
		
		this.blockTimer = 0.5D; // 0.5 s
		
		if ( samplingRate != LSLStreamInfo.IRREGULAR_RATE )
		{
			this.blockTimer = 1.5 / samplingRate; // 1.5 times the period time  
			
			int time = (int)(3*1000.0D / samplingRate);
			if (time < 3000)
			{
				time = 3000; // 3 seconds
			}
						
			this.timer = new Timer( time, new ActionListener() 
				{				
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						timeOver();
					}
			});					
		}
	}

	protected void startUp() throws Exception
	{
		super.startUp();

		if( this.timer != null )
		{
			this.timer.start();
		}
		
		this.inLet.open_stream();
	}

	/*
	 * (non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#runInLoop()
	 */
	@Override
	protected void runInLoop() throws Exception
	{	
		double[] data = this.readData();
		
		if( data != null )
		{	
			if (this.timer != null)
			{
				this.timer.stop();
			}
			
			this.fireInputLSLDataEvent( data, this.dataTime );
			
			if (this.timer != null)
			{
				this.timer.restart();
			}			
		}
	}
	
	/*(non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#runExceptionManager(java.lang.Exception)
	 */
	@Override
	protected void runExceptionManager(Exception e)
	{
		if( !( e instanceof InterruptedException ) )
		{
			super.stopThread = true;
		}
	}
	
	private double[] readData() throws Exception
	{
		double[] out = null;
				
		switch (this.LSLFormatData)
		{
			case int8:
			{	
				dataTime = this.inLet.pull_sample( this.byteData, this.blockTimer );
				
				if( dataTime > 0 )
				{
					out = ConvertTo.ByteArray2DoubleArray( this.byteData );
				}
	
				break;
			}
			case int16:
			{
				dataTime = this.inLet.pull_sample( this.shortData, this.blockTimer );
				
				if( dataTime > 0 )
				{
					out = ConvertTo.NumberArray2DoubleArray( ConvertTo.shortArray2ShortArray( this.shortData ) );
				}
	
				break;
			}
			case int32:
			{					
				dataTime = this.inLet.pull_sample( this.intData, this.blockTimer );
				
				if( dataTime > 0 )
				{
					out = ConvertTo.NumberArray2DoubleArray( ConvertTo.intArray2IntegerArray( this.intData ) );
				}
				break;
			}
			case int64:
			{					
				dataTime = this.inLet.pull_sample( this.longData, this.blockTimer );
				
				if( dataTime > 0 )
				{
					out = ConvertTo.NumberArray2DoubleArray( ConvertTo.longArray2LongArray( this.longData ) );
				}
				break;
			}
			case float32:
			{
				dataTime = this.inLet.pull_sample( this.floatData, this.blockTimer );
				
				if( dataTime > 0 )
				{
					out = ConvertTo.NumberArray2DoubleArray( ConvertTo.floatArray2FloatArray( this.floatData ) );
				}
	
				break;
			}
			case double64:
			{
				dataTime = this.inLet.pull_sample( this.doubleData, this.blockTimer );
				
				if( dataTime > 0 )
				{
					out = this.doubleData;
				}
	
				break;
			}
			default:
			{
				throw new UnsupportedDataTypeException( );
			}
		}
		
		return out;
	}
				
	protected void runExceptionManager( Throwable e )
	{
		if ( !(e instanceof InterruptedException) 
				|| ( e instanceof Error ) )
		{
			if( this.timer != null )
			{
				this.timer.stop(); 
			}
			
			this.stopThread = true;
			
			String msg = e.getMessage();
			
			if( msg != null )
			{
				msg += " <" + super.getName() + ">";
			}
			
			Exception ex = new Exception( msg, e );
			
			String errMsg = "Timer is over. ";
			
			if( e instanceof Error ) 
			{
				errMsg = "Fatal Error. "; 
			}
			
			errMsg += "The stream " + super.getName() + " does not respond." ;
			
			ex.addSuppressed( new IOException( errMsg ) );
			
			this.notifyProblem( ex );
		}
	}

	protected void cleanUp() throws Exception
	{
		super.cleanUp();
		
		this.inLet.close();
		
		if (this.timer != null)
		{
			this.timer.stop();
		}		
		this.timer = null;
		
		for( IInputLSLDataListener list : this.getListener() )
		{
			list.close();
		}		
	}
		
	protected void notifyProblem(Exception e)
	{		
		// TODO
	}

	private void timeOver( )
	{	
		this.stopActing( IStoppable.FORCE_STOP );
		this.notifyProblem( new TimeoutException( "Waiting time for input data from device was exceeded." ) );		
	}

	/*(non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#preStopThread(int)
	 */
	@Override
	protected void preStopThread(int friendliness) throws Exception
	{	
	}

	/*(non-Javadoc)
	 * @see @see stoppableThread.AbstractStoppableThread#postStopThread(int)
	 */
	@Override
	protected void postStopThread(int friendliness) throws Exception
	{
	}

	public void addInputLSLDataListener( IInputLSLDataListener listener)
	{
		this.listenerList.add( IInputLSLDataListener.class, listener );
	}

	public void removeInputLSLDataListener( IInputLSLDataListener listener)
	{
		this.listenerList.remove( IInputLSLDataListener.class, listener );
	}
	
	public IInputLSLDataListener[] getListener()
	{
		return this.listenerList.getListeners( IInputLSLDataListener.class );
	}
	
	/**
	 * 
	 * @param typeEvent
	 */
	protected void fireInputLSLDataEvent( double[] values, double time )
	{
		InputLSLDataEvent event = new InputLSLDataEvent( this, values, time );

		IInputLSLDataListener[] listeners = this.listenerList.getListeners( IInputLSLDataListener.class );

		for (int i = 0; i < listeners.length; i++ ) 
		{
			listeners[ i ].InputLSLDataEvent( event );
		}
	}

}
