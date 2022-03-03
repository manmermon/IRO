/**
 * 
 */
package biosignal.analyzer.health;

import biosignal.analyzer.AnalysisResult;
import biosignal.analyzer.BiosignalAnalyzer;

/**
 * @author Manuel Merino Monge
 *
 */
public class HealthAnalyzer implements BiosignalAnalyzer
{
	@Override
	public AnalysisResult analysis(Double[] data) 
	{
		AnalysisResult res = new AnalysisResult();
		
		return res;
	}
}
