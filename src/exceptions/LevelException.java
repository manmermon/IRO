package exceptions;

public class LevelException extends Exception 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LevelException() 
	{
		super();
	}
	
	public LevelException( Throwable t )
	{
		super( t );
	}
	
	public LevelException( String msg )
	{
		super( msg );
	}
	
	public LevelException( String msg, Throwable t )
	{
		super( msg, t );
	}
	
	public LevelException( String msg, Throwable t, boolean enableSuppression, boolean writableStackTrace )
	{
		super( msg, t, enableSuppression, writableStackTrace );
	}
}
