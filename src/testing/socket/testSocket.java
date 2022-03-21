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
								byte[] b = new byte[1];
								s.getInputStream().read( b );
								System.out.println( "testSocket.main(...).new Thread() {...}.run() " + Arrays.toString( b ) );
								
								s.getOutputStream().write( new byte[] { (byte)( 0x07 ) });
							}
						}
						catch( Exception e)
						{
							e.printStackTrace();
						}
					};
				};
				
		t.start();
		
		SocketClient c = new SocketClient();
		
				
		
		try {
			c.startThread();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
