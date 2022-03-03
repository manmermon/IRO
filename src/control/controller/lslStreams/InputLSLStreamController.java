/*
 * Work based on CLIS by Manuel Merino Monge <https://github.com/manmermon/CLIS>
 * 
 * Copyright 2018-2020 by Manuel Merino Monge <manmermon@dte.us.es>
 *  
 *   This file is part of LSLRec and CLIS.
 *
 *   LSLRec is free software: you can redistribute it and/or modify
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
 */

package control.controller.lslStreams;

import exceptions.UnsupportedDataTypeException;
import general.ConvertTo;
import lslStream.LSL;
import lslStream.LSL.StreamInlet;
import lslStream.LSLStreamInfo.StreamDataType;
import lslStream.LSLStreamInfo;
import lslStream.controller.LSLMetadataController;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppableThread;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;

import control.controller.IControllerMetadata;
import control.controller.IInputController;
import control.events.IInputControllerListener;
import control.events.InputControllerEvent;

public class InputLSLStreamController extends AbstractStoppableThread implements IInputController
{
	private LSL.StreamInlet inLet = null;

	private Timer timer = null;
	
	private byte[] byteData;
	private short[] shortData;
	private int[] intData;
	private float[] floatData;
	private double[] doubleData;
	
	private StreamDataType LSLFormatData = StreamDataType.float32;
		
	private double blockTimer = 0;
		
	private EventListenerList listenerList;
	
	private IControllerMetadata metadata = null;
	
	private double dataTime;
	
	public InputLSLStreamController( LSLMetadataController meta ) throws Exception
	{
		if ( meta == null || meta.getControllerSetting() == null )
		{
			throw new IllegalArgumentException( "LSLStreamMetadata/LSL.StreamInfo is null");
		}
		
		LSL.StreamInfo info = (LSL.StreamInfo)meta.getControllerSetting();
		
		super.setName( this.getClass().getSimpleName() + "-" + super.getId() );
		
		this.metadata = meta;
		this.LSLFormatData = info.channel_format();

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
			case( LSL.ChannelFormat.int8 ):
			{
				this.byteData = new byte[ this.inLet.info().channel_count() ];			
				break;
	
			}
			case( LSL.ChannelFormat.int16 ):
			{
				nBytes = Short.BYTES;
	
				this.shortData = new short[this.inLet.info().channel_count()  ];
				break;
			}
			case( LSL.ChannelFormat.int32 ):
			{
				nBytes = Integer.BYTES;
	
				this.intData = new int[this.inLet.info().channel_count() ];
				break;
			}
			/*
			case( LSL.ChannelFormat.int64 ):
			{
				nBytes = Long.BYTES;
	
				this.longData = new long[ inLet.info().channel_count() ];
				break;
			}
			 */	
			case( LSL.ChannelFormat.float32 ):
			{
				nBytes = Float.BYTES;
	
				this.floatData = new float[ this.inLet.info().channel_count() ];
				break;
			}
			case( LSL.ChannelFormat.double64 ):
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
		
		double samplingRate = this.inLet.info().nominal_srate();
		
		this.blockTimer = 0.5D; // 0.5 s
		
		if ( samplingRate != LSL.IRREGULAR_RATE )
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
			
			this.fireInputControllerEvent( data, this.dataTime );
			
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
			case( LSL.ChannelFormat.int8 ):
			{	
				dataTime = this.inLet.pull_sample( this.byteData, this.blockTimer );
				
				if( dataTime > 0 )
				{
					out = ConvertTo.ByteArray2DoubleArray( this.byteData );
				}
	
				break;
			}
			case( LSL.ChannelFormat.int16 ):
			{
				dataTime = this.inLet.pull_sample( this.shortData, this.blockTimer );
				
				if( dataTime > 0 )
				{
					out = ConvertTo.NumberArray2DoubleArray( ConvertTo.shortArray2ShortArray( this.shortData ) );
				}
	
				break;
			}
			case( LSL.ChannelFormat.int32 ):
			{					
				dataTime = this.inLet.pull_sample( this.intData, this.blockTimer );
				
				if( dataTime > 0 )
				{
					out = ConvertTo.NumberArray2DoubleArray( ConvertTo.intArray2IntegerArray( this.intData ) );
				}
				break;
			}
			case( LSL.ChannelFormat.float32 ):
			{
				dataTime = this.inLet.pull_sample( this.floatData, this.blockTimer );
				
				if( dataTime > 0 )
				{
					out = ConvertTo.NumberArray2DoubleArray( ConvertTo.floatArray2FloatArray( this.floatData ) );
				}
	
				break;
			}
			case( LSL.ChannelFormat.double64 ):
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
				
	}
		
	protected void notifyProblem(Exception e)
	{		
		// TODO
	}

	private void timeOver( )
	{	
		this.stopThread( IStoppableThread.FORCE_STOP );
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

	
	/*(non-Javadoc)
	 * @see @see control.inputs.IInputController#addInputControllerListener(control.events.IInputControllerListener)
	 */
	@Override
	public void addInputControllerListener(IInputControllerListener listener)
	{
		this.listenerList.add( IInputControllerListener.class, listener );
	}

	/*(non-Javadoc)
	 * @see @see control.inputs.IInputController#removeInputControllerListener(control.events.IInputControllerListener)
	 */
	@Override
	public void removeInputControllerListener(IInputControllerListener listener)
	{
		this.listenerList.remove( IInputControllerListener.class, listener );
	}
	
	/**
	 * 
	 * @param typeEvent
	 */
	protected void fireInputControllerEvent( double[] values, double time )
	{
		InputControllerEvent event = new InputControllerEvent( this, values, time );

		IInputControllerListener[] listeners = this.listenerList.getListeners( IInputControllerListener.class );

		for (int i = 0; i < listeners.length; i++ ) 
		{
			listeners[ i ].InputControllerEvent( event );
		}
	}

	/*(non-Javadoc)
	 * @see @see control.inputs.IInputController#stopController()
	 */
	@Override
	public void stopController() throws Exception
	{
		super.stopThread( IStoppableThread.FORCE_STOP );
	} 
	
	/*(non-Javadoc)
	 * @see @see control.inputs.IInputController#startController()
	 */
	@Override
	public void startController() throws Exception
	{
		super.startThread();
	}


	/*(non-Javadoc)
	 * @see @see control.controller.IInputController#getMetadataController()
	 */
	@Override
	public IControllerMetadata getMetadataController()
	{
		return this.metadata;
	}

	/*(non-Javadoc)
	 * @see @see control.controller.IInputController#getListener()
	 */
	@Override
	public IInputControllerListener[] getListener()
	{
		IInputControllerListener[] listeners = this.listenerList.getListeners( IInputControllerListener.class );

		return listeners;
	}

}