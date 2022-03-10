package control.controller;

import java.util.List;

import control.events.InputActionEvent;

public interface IInputable 
{
	public void action( List< InputActionEvent > act );
}
