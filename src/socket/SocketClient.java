package socket;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import gui.MainAppUI;
import gui.panel.inputDevice.InputDevicePanel;
import thread.stoppableThread.AbstractStoppableThread;

public class SocketClient extends AbstractStoppableThread 
{	
	private static SocketClient sc = null;
	
	public static final short SEARCH  = 0b1000_0000; // 128
	public static final short CONECT  = 0b0100_0000; // 64
	public static final short CLOSE   = 0b0010_0000; // 32
	public static final short ERROR   = 0b0001_0000; // 16
	public static final short ALIVE   = 0b0000_0000; // 16
	public static final short NONE    = 0b0000_0000; // 0
	
	//private final String IP = "192.168.0.132";
	private final String IP = "150.214.141.159";
	private final int port = 0xBEBA; // 0xBAC0;
	
	private Socket socket;
	
	private long sleepTime = 1000L;
	private long sleepToCheckConnection = 10_000L;
	
	private short state = SEARCH;
	
	private Object lock = new Object();
		
	private SocketClient( ) 
	{				
	}
	
	public static SocketClient getInstance()
	{
		if( sc == null )
		{
			sc = new SocketClient();
		}
		
		return sc;
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
			this.socket = new Socket( this.IP, this.port );
			this.socket.setSoTimeout( 5_000 );
			
			this.state = SEARCH;
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
				super.wait( this.sleepToCheckConnection );
				
				this.checkSocketConnection();
			}
		}
		
		//System.out.println("SocketClient.runInLoop() WAKE UP");
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
						short msg = SEARCH ; 
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
						short msg =  CONECT ; 
						short res = this.sendMessage( msg );
						
						if( ( res & ERROR ) == 0 
								&& ( res & 0x07 ) > 0 )
						{
							this.state = NONE;
						}
						else if( ( res & ERROR ) != 0 )
						{
							this.state = SEARCH;
							
							InputDevicePanel.getInstance().refreshStreams();
						}
						
						break;
					}
					case CLOSE:
					{
						short msg =  CLOSE ;
						short res = this.sendMessage( msg );
						
						if( ( res & ERROR ) == 0 
								&& ( res & 0x07 ) > 0 )
						{
							this.state = SEARCH;
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
		
		//System.out.println("SocketClient.runInLoop() " + this.state);
	}
	
	@Override
	protected void runExceptionManager(Throwable e) 
	{
		if( !( e instanceof ConnectException ) 
				&& !( e instanceof InterruptedException ) )
		{
			super.runExceptionManager(e);
		}
	}
				
	private void checkSocketConnection()
	{
		if( this.socket != null )
		{
			try 
			{
				short res = this.sendMessage( ALIVE );
				if( res == ERROR )
				{
					this.state = SEARCH;
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
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
			try
			{
				this.socket.getOutputStream().write( new byte[] { (byte)msg } );
				
				byte[] buf = new byte[ 1 ];
				int r = this.socket.getInputStream().read( buf );
				if( r > 0 )
				{
					response = buf[ 0 ];
				}			
			}
			catch ( Exception e ) 
			{
				try
				{			
					this.socket.getOutputStream().write( new byte[] { (byte)CLOSE } );
					
					byte[] buf = new byte[ 1 ];
					this.socket.getInputStream().read( buf );					
				}
				catch (Exception e1) 
				{
				}
				
				this.socket.close();
				this.socket = null;
				
				response = ERROR;				
			}
		}
		
		return response;
	}
	
	public synchronized void reset()
	{
		synchronized( this.lock )
		{
			this.state = CLOSE;
			
			synchronized( this )
			{
				super.notify();
			}
		}
	}
}
