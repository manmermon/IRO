/**
 * 
 */
package config;

import java.awt.Image;

/**
 * @author manuel
 *
 */
public interface IOwner
{
	public static final int ANONYMOUS = -1;
	
	public int getId();
	
	public String getName();
	
	public Image getOwnerImage();
}
