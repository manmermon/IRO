package testing.socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import socket.SocketClient;

public class testSocket {

	public static void main(String[] args) 
	{
		Thread t = new Thread()
				{
					public void run() 
					{
						try
						{
							ServerSocket ss = new ServerSocket( 47808 );
							
							Socket s = null;
							while( s == null )
							{
								s = ss.accept();							
							}
							
							if( s != null )
							{
								while( true )
								{
									byte[] b = new byte[1];
									s.getInputStream().read( b );
									System.out.println( "testSocket.main(...).new Thread() {...}.run() " + Arrays.toString( b ) );
								
									byte out = 0;
									if( ( b[0] & 0x80 ) == 0b1000_0000)
									{
										out = 7;
									}
									
									if( ( b[0] & 0x40 ) == 0b0100_0000)
									{
										out = 3;
									}
									
									if( ( b[0] & 0x20 ) == 0b0010_0000)
									{
										out = 4;
									}
									
									if( ( b[0] & 0x10 ) == 0b0001_0000)
									{
										out = 2;
									}
									
									s.getOutputStream().write( new byte[] { out });
								}
							}
						}
						catch( Exception e)
						{
							e.printStackTrace();
						}
					};
				};
				
		t.start();
		
		SocketClient c = SocketClient.getInstance();
		
				
		
		try {
			c.startThread();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep( 5_000 );
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		c.reset();

	}

}
