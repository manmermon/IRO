package exceptions;

public class ConfigParameterException extends Exception 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConfigParameterException() 
	{
		super();
	}
	
	public ConfigParameterException( Throwable t )
	{
		super( t );
	}
	
	public ConfigParameterException( String msg )
	{
		super( msg );
	}
	
	public ConfigParameterException( String msg, Throwable t )
	{
		super( msg, t );
	}
	
	public ConfigParameterException( String msg, Throwable t, boolean enableSuppression, boolean writableStackTrace )
	{
		super( msg, t, enableSuppression, writableStackTrace );
	}
}
