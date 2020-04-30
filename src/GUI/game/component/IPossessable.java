/**
 * 
 */
package GUI.game.component;

import config.IOwner;

/**
 * @author manuel
 *
 */
public interface IPossessable
{
	public void setOwner( IOwner owner );
	
	public IOwner getOwner();
}
