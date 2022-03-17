package lslStream.biosignal;

import lslStream.LSLStreamInfo.StreamType;
import lslStream.event.InputLSLDataReader;
import stoppableThread.IStoppable;

public class LSLInputBiosignalStreamRegistral extends InputLSLDataReader
{
	public LSLInputBiosignalStreamRegistral( StreamType strType ) 
	{		
	}
	
	@Override
	protected void readInputData(lslStream.event.InputLSLDataEvent ev) 
	{	
	}

	@Override
	protected void preStopThread(int friendliness) throws Exception 
	{	
	}

	@Override
	protected void postStopThread(int friendliness) throws Exception 
	{	
	}

	@Override
	public void close() 
	{
		super.stopActing( IStoppable.FORCE_STOP );
	}
	
}
