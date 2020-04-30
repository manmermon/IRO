package testing.other;

import exceptions.ConfigParameterException;

public class testCastType
{

	public static void main(String[] args)
	{
		Double d = new Double( 1 );
		Float f = new Float( 1 );
		Long l = new Long( 1 );
		Integer i = new Integer( 1 );
		Short s = Short.valueOf( "1" );
		Byte b = Byte.valueOf( "1" );
		
		Boolean bol = new Boolean( false );
		
		try
		{
			checkValue( d, 0 );
			checkValue( f, 0 );
			checkValue( l, 0 );
			checkValue( i, 0 );
			checkValue( s, 0 );
			checkValue( b, 0 );
			
			checkValue( d.doubleValue(), 0 );
			checkValue( f.floatValue(), 0 );
			checkValue( l.floatValue(), 0 );
			checkValue( i.intValue(), 0 );
			checkValue( s.shortValue(), 0 );
			checkValue( b.byteValue(), 0 );
			
			checkValue( "v", 1 );
			checkValue( "", 1 );
			
			checkValue( bol, 2 );
			checkValue( bol.booleanValue(), 2 );
			
			checkValue( "", 2);
		}
		catch (Exception e) 
		{
			e.printStackTrace(); 
		}
		
		System.out.println("testCastType.main() TODO OK");
	}

	public static void checkValue( Object value, int type ) throws ConfigParameterException
	{
		String errMsg = null;
		
		switch ( type ) 
		{
			case 0:
			{
				if( !(value instanceof Number) )
				{
					errMsg = "Value is not a Number";
				}
				break;
			}
			case 1:
			{
				if( !(value instanceof String) )
				{
					errMsg = "Value is not a String";
				}
				
				break;
			}
			case 2:
			{
				if( !(value instanceof Boolean) )
				{
					errMsg = "Value is not a Boolean";
				}
				break;
			}
			default:
			{
				errMsg = "Parameter type unknown.";
				break;
			}
		}
		
		if( errMsg != null )
		{
			throw new ConfigParameterException( errMsg );
		}
		
	}
}
