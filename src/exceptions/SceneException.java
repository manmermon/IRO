package exceptions;

public class SceneException extends Exception 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SceneException() 
	{
		super();
	}
	
	public SceneException( Throwable t )
	{
		super( t );
	}
	
	public SceneException( String msg )
	{
		super( msg );
	}
	
	public SceneException( String msg, Throwable t )
	{
		super( msg, t );
	}
	
	public SceneException( String msg, Throwable t, boolean enableSuppression, boolean writableStackTrace )
	{
		super( msg, t, enableSuppression, writableStackTrace );
	}
}
