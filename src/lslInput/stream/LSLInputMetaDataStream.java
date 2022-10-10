package lslInput.stream;

import lslInput.LSLStreamInfo;

public class LSLInputMetaDataStream extends InputStreamMetadataAdapter
{
	public LSLInputMetaDataStream( LSLStreamInfo strInfo ) throws IllegalArgumentException
	{
		if( strInfo == null )
		{
			throw new IllegalArgumentException( "Input null." );
		}

		super.controllerType = InputSourceType.LSLSTREAM;
				
		super.id = strInfo.uid();
		super.name = strInfo.name();
		
		super.numberOfChannels = strInfo.channel_count();
		
		super.samplingRate = strInfo.sampling_rate();
		
		super.inputSourceSetting = strInfo;
		
		super.info = strInfo.description();		
	}
}
