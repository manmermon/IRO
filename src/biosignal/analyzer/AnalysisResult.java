/**
 * 
 */
package biosignal.analyzer;

/**
 * @author Manuel Merino Monge
 *
 */
public class AnalysisResult 
{
	enum HealthRisk { NONE, LOW, MEDIUM, HIGH, EXTREME };
	
	public HealthRisk getHeathRisk()
	{
		HealthRisk risk = HealthRisk.NONE;
		
		return risk; 
	}
}
