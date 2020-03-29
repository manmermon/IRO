package config;

import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;

import config.language.Caption;
import exceptions.ConfigParameterException;
import general.NumberRange;

public class ConfigParameter
{
	public enum ParameterType { NUMBER, STRING, BOOLEAN };
	
	private Caption _ID = null;
	private ParameterType _type = ParameterType.NUMBER;
	private List< Object > _values = null;
	private NumberRange _rng = null;
	
	public ConfigParameter( Caption id, ParameterType type ) throws ConfigParameterException
	{
		if( id == null )
		{
			throw new ConfigParameterException( "Parameter ID is null." );
		}
		
		this._ID = id;
		this._type = type;		
		this._values = new ArrayList< Object >();
	}
	
	public ConfigParameter( Caption id, NumberRange range) throws ConfigurationException
	{
		if( id == null )
		{
			throw new ConfigurationException( "Parameter ID is null." );
		}
		
		this._ID = id;
		this._type = ParameterType.NUMBER;		
		this._rng = range;
		this._values = new ArrayList< Object >();
	}
	
	public void addAll( List< Object > values ) throws ConfigParameterException
	{
		if( values != null )
		{
			for( Object v : values )
			{
				this.add( v );
			}
		}
	}
	
	public void add( Object value ) throws ConfigParameterException
	{
		String errMsg = null;
		
		if( value == null )
		{
			errMsg = "Value is null";
		}
		else
		{	
			switch ( this._type ) 
			{
				case NUMBER:
				{
					if( !(value instanceof Number) )
					{
						errMsg = "Value is not a Number";
					}
					else if( this._rng != null )
					{
						if( !this._rng.within( (Number)value ) )
						{
							errMsg = "value is out of range " + this._rng.toString() + ".";
						}
					}
					
					break;
				}
				case STRING:
				{
					if( !(value instanceof String) )
					{
						errMsg = "Value is not a String";
					}
					
					break;
				}
				case BOOLEAN:
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
		}
		
		if( errMsg != null )
		{
			throw new ConfigParameterException( errMsg );
		}
		
		this._values.add( value );
	}
 
	public void replace( int index, Object value ) throws ConfigParameterException 
	{		
		Object val = this._values.remove( index );
		try
		{
			this.add( value );
		}
		catch ( ConfigParameterException e) 
		{
			this._values.add(  index, val );
			
			throw e;
		}
	}
	
	public void clear( )
	{
		this._values.clear();
	}
	
	public Caption get_ID()
	{
		return this._ID;
	}
	
	public ParameterType get_type()
	{
		return this._type;
	}
	
	public Object[] getValues()
	{
		return this._values.toArray( );
	}
	
	public NumberRange getNumberRange()
	{
		return this._rng;
	}
	
	@Override
	public int hashCode()
	{
		return this._ID.getID().hashCode();
	}
}
