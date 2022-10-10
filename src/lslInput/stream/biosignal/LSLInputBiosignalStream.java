package lslInput.stream.biosignal;

import lslInput.LSLInputData;
import lslInput.LSLStreamInfo;
import lslInput.stream.IInputStreamMetadata;

public class LSLInputBiosignalStream extends LSLInputData
{
	private IInputStreamMetadata meta;
	
	public LSLInputBiosignalStream( IInputStreamMetadata meta ) throws Exception 
	{
		super( (LSLStreamInfo)meta.getInputSourseSetting() );
		
		this.meta = meta;		
	}

	public IInputStreamMetadata getMetadataBiosignalStream()
	{
		return this.meta;
	}		
}
