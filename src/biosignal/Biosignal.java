/**
 * 
 */
package biosignal;

import biosignal.analyzer.AnalysisResult;
import biosignal.analyzer.BiosignalAnalyzer;
import biosignal.analyzer.emotion.EmotionAnalyzer;
import biosignal.analyzer.health.HealthAnalyzer;
import general.DataQueue;

/**
 * @author Manuel Merino Monge
 *
 */
public class Biosignal 
{
	public enum Type { ECG, HR, EEG, EDA,  BREATH, TEMPERATURE };
	
	private int winLen = 1;
	
	private int shift = 1;
	
	private Type type = Type.ECG;
	
	private DataQueue< Double > buffer;
	
	private BiosignalAnalyzer healthAnalyzer, emotionAnalyzer;
	
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
