/**
 * 
 */
package config;

/**
 * @author manuel
 *
 */
public class TimeSetting
{
	private int player;
	private double recoverTime;
	private double reactionTime;
	
	/**
	 * 
	 */
	public TimeSetting( int playerID, double reaction, double recover )
	{
		this.player = playerID;
		
		this.reactionTime = reaction;
		this.recoverTime = recover;
	}
	
	/**
	 * @return the player
	 */
	public int getPlayer()
	{
		return this.player;
	}
	
	/**
	 * @return the reactionTime
	 */
	public double getReactionTime()
	{
		return this.reactionTime;
	}
	
	/**
	 * @return the recoverTime
	 */
	public double getRecoverTime()
	{
		return this.recoverTime;
	}
}
