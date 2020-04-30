/**
 * 
 */
package config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author manuel
 *
 */
public class Settings
{
	private Player player;
	private Map< String, ConfigParameter > listUserConfig = new HashMap< String, ConfigParameter >();
	
	/**
	 * 
	 */
	public Settings()
	{
	}
	
	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
		
		for( ConfigParameter par : listUserConfig.values() )
		{
			par.setPlayer( player );
		}
	}
	
	/**
	 * @return the player
	 */
	public Player getPlayer()
	{
		return this.player;
	}
	
	public ConfigParameter getParameter( String propertyID )
	{	
		ConfigParameter par = listUserConfig.get( propertyID );
		
		return par;
	}
	
	public void setParameter( String propertyID, ConfigParameter par )
	{
		if( propertyID != null )
		{
			if( par != null )
			{
				par.setPlayer( this.player );
				
				this.listUserConfig.put( propertyID, par );
			}
			else
			{
				this.listUserConfig.remove( propertyID );
			}
		}
	}

	public Collection< ConfigParameter > getParameters()
	{
		return this.listUserConfig.values();
	}
}
