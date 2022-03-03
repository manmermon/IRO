package lslStream.event;

import java.util.EventListener;

public interface IInputLSLDataListener extends EventListener
{
	public void InputLSLDataEvent( InputLSLDataEvent  ev );
}
