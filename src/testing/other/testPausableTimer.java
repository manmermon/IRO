package testing.other;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import thread.timer.PausableTimer;

public class testPausableTimer 
{
	public static void main(String[] args)
	{
		final Date date = new Date();
		final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSSS");
		
		PausableTimer t = new PausableTimer( 5000, new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{	
				Date d2 = new Date();
				System.out.println("Start time: " + formatter.format( date ) );
				System.out.println("End time: " + formatter.format( d2 ) );
			}
		} );
		
		try
		{
			t.startThread();
			Thread.sleep( 4000L );
			t.pauseTimer();
			Thread.sleep( 4000L );
			t.resumenTimer();
		}
		catch (Exception e) 
		{
		}
	}
}
