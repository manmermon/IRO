package lslInput.event;

import java.util.EventListener;

public interface IInputLSLDataListener extends EventListener
{
	public void InputLSLDataEvent( InputLSLDataEvent  ev );

	public void close();
}
