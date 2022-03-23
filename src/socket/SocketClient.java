package socket;

import java.io.IOException;
import java.net.Socket;

import thread.stoppableThread.AbstractStoppableThread;

public class SocketClient extends AbstractStoppableThread 
{	
	public static final short SEARCH  = 0b1000_0000;
	public static final short CONECT  = 0b0100_0000;
	public static final short CLOSE   = 0b0010_0000;
	public static final short ERROR   = 0b0001_0000;
	public static final short NONE    = 0b0000_0000;
	
	//private final String IP = "192.168.0.132";
	private final String IP = "127.0.0.1";
	private final int port = 47808;
	
	private Socket socket;
	
	private long sleepTime = 1000L;
	
	private short state = SEARCH;
	
	private Object lock = new Object();
		
	public SocketClient( ) 
	{		
		
	}
	
	@Override
	protected void preStopThread(int friendliness) throws Exception 
	{	
	}

	@Override
	protected void postStopThread(int friendliness) throws Exception 
	{	
	}

	private void createSocket() throws IOException
	{
		if( this.socket == null )
		{
			this.socket = new Socket( this.IP, port );
			this.socket.setSoTimeout( 5_000 );
		}
	}
	
	@Override
	protected void startUp() throws Exception 
	{
		this.createSocket();
	}
	
	@Override
	protected void runInLoop() throws Exception 
	{
		synchronized( this )
		{
			if( this.state != NONE )
			{
				super.wait( this.sleepTime );
			}
			else
			{
				super.wait();
			}
		}
		
		System.out.println("SocketClient.runInLoop() WAKE UP");
		if( this.socket == null )
		{
			this.createSocket();
		}
		
		if( this.socket != null )
		{			
			synchronized( this.lock )
			{
				switch( this.state )
				{
					case SEARCH:
					{
						short msg = SEARCH ; //| CONECT ;
						short res = this.sendMessage( msg );
						
						if( ( res & ERROR ) == 0 
								&& ( res & 0x07 ) > 0 )
						{
							this.state = CONECT;
						}
						
						break;
					}
					case CONECT:
					{
						short msg =  CONECT ; //| (short)0b0000_0111;
						short res = this.sendMessage( msg );
						
						if( ( res & ERROR ) == 0 
								&& ( res & 0x07 ) > 0 )
						{
							this.state = NONE;
						}
						
						break;
					}				
					default:
					{
						break;
					}
				}
			}
		}
	}
	
	@Override
	protected void cleanUp() throws Exception 
	{
		super.cleanUp();
		
		if( this.socket != null )
		{
			short msg = CLOSE ;// | 0b0000_0111;
			
			try
			{
				boolean again = true;
				do
				{
					short res = this.sendMessage( msg );
					again = ( res & ERROR ) != 0 ;
				}
				while( again );
			}
			catch (Exception e) 
			{
			}
			finally 
			{
				this.socket.close();
			}
		}
	}
	
	private short sendMessage( short msg ) throws IOException
	{
		short response = NONE;
		
		if( this.socket != null )
		{
			this.socket.getOutputStream().write( new byte[] { (byte)msg } );
			
			byte[] buf = new byte[ 1 ];
			int r = this.socket.getInputStream().read( buf );
			if( r > 0 )
			{
				response = buf[ 0 ];
			}			
		}
		
		return response;
	}
	
	public synchronized void reset()
	{
		synchronized( this.lock )
		{
			this.state = SEARCH;
			
			synchronized( this )
			{
				super.notify();
			}
		}
	}
}
