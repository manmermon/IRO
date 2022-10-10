/**
 * 
 */
package control.events;

import lslInput.event.IInputLSLDataListener;

/**
 * @author manuel
 *
 */
public interface IEnabledInputLSLDataListener extends IInputLSLDataListener
{
	public void setEnableInputStream( boolean enable );	
}
