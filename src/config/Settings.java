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
	private Map< String, ConfigParameter > listUserConfig = new HashMap< String, ConfigParameter >();
	
	/**
	 * 
	 */
	public Settings()
	{
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
