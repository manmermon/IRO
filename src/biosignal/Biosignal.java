/**
 * 
 */
package biosignal;

import biosignal.analyzer.AnalysisResult;
import biosignal.analyzer.BiosignalAnalyzer;
import biosignal.analyzer.emotion.EmotionAnalyzer;
import biosignal.analyzer.health.HealthAnalyzer;
import general.DataQueue;
import general.StringTuple;
import lslInput.LSLUtils;
import lslInput.LSLStreamInfo.StreamType;

/**
 * @author Manuel Merino Monge
 *
 */
public class Biosignal 
{
	public enum Type { ECG, HR, EEG, EDA,  BREATH, TEMPERATURE, UNKNOW };
	
	private int winLen = 1;
	
	private int shift = 1;
	
	private Type type = Type.ECG;
	
	private DataQueue< Double > buffer;
	
	private BiosignalAnalyzer healthAnalyzer, emotionAnalyzer;
	
	public static Biosignal.Type getBiosignalType( String type )
	{
		Biosignal.Type bioType = Type.UNKNOW;
		
		String bioStr = "";
		
		if( type != null )
		{
			StringTuple strTuple = LSLUtils.splitFieldStreamContentType( type );
			
			StreamType strType = LSLUtils.getStreamType( type );
			
			if( strType == StreamType.BIOSIGNAL || strType == StreamType.CONTROLLER_BIOSIGNAL )
			{
				bioStr = strTuple.t1;
				
				if( strTuple.t2 != null )
				{
					bioStr = strTuple.t2;
				}
				
				String[] parts2 = bioStr.split( ":" );
					
				if( parts2.length == 2 )
				{
					String p22 = parts2 [ 1 ];
					boolean find = false;
	
					for( Biosignal.Type bt : Biosignal.Type.values() )
					{
						find = bt.name().equalsIgnoreCase( p22 );
	
						if( find )
						{
							bioType = bt;
							break;
						}
					}
				}
			}
		}
		
		return bioType;
	}
	
	/**
	 * 
	 * @param t
	 * @param bufferSize: window length in samples. It must be greater than 0.
	 * @param overlap: window overlap. Range: [0, 1)
	 * 
	 * @throws IllegalArgumentException: if bufferSize <= 0 or overlap is < 0 or >= 1
	 */
	public Biosignal( Type t, int bufferSize, double overlap )
	{
		if( bufferSize <= 0 || overlap < 0 || overlap >= 1 )
		{
			throw new IllegalArgumentException( "Input values are not corrects." );
		}
		
		this.winLen = bufferSize;
		
		this.buffer = new DataQueue< Double >( this.winLen );
		
		this.shift = (int)( this.winLen * ( 1 - overlap ) );		
		this.shift = this.shift <= 0 ? 1 : this.shift;
		
		this.type = t;
		
		this.healthAnalyzer = new HealthAnalyzer();
		this.emotionAnalyzer = new EmotionAnalyzer();
	}
	
	public void putData( double val )
	{
		this.buffer.put( val );
		
		if( this.buffer.isFull() )
		{
			Double[] data = this.buffer.pullAll();
			
			AnalysisResult resHealth = this.healthAnalyzer.analysis( data );
			AnalysisResult resEmotion = this.emotionAnalyzer.analysis( data );
		}
	}
	
	@Override
	public String toString() 
	{
		return "{" + this.type.name() + "," + this.winLen + "," + this.shift + "}";
	}
	
	public String toStringAll()
	{
		return this.toString() + "=" + this.buffer.toString();
	}
}
