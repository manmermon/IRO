package config;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;

import config.language.Caption;
import exceptions.ConfigParameterException;
import general.NumberRange;

public class ConfigParameter
{
	public enum ParameterType { NUMBER, STRING, BOOLEAN, COLOR, USER };
	
	private Caption _ID = null;
	private ParameterType _type = ParameterType.NUMBER;
	private Object _selectedValue = null;
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
		this._selectedValue = null;
	}
	
	public ConfigParameter( Caption id, NumberRange range) throws ConfigParameterException
	{
		if( id == null )
		{
			throw new ConfigParameterException( "Parameter ID is null." );
		}
		
		this._ID = id;
		this._type = ParameterType.NUMBER;		
		this._rng = range;
		this._values = new ArrayList< Object >();
		this._selectedValue = null;
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
				case COLOR:
				{
					if( !(value instanceof Color) )
					{
						errMsg = "Value is not a Color";
					}
					
					break;					
				}
				case USER:
				{
					if( !( value instanceof User ) )
					{
						errMsg = "Value is not a User";
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
		
		if( this._selectedValue == null )
		{
			this._selectedValue = value;
		}
	}
 	
	public void addAll( List values ) throws ConfigParameterException
	{
		if( values != null )
		{
			for( Object val : values )
			{
				this.add( val );
			}
		}
	}
	
	public void clear()
	{
		this._selectedValue = null;
		this._values.clear();		 
	}
	
	public void setSelectedValue( int index )
	{
		this._selectedValue = this._values.get( index );
	}
	
	public void setSelectedValue( Object val )
	{
		if( this._values.contains( val ) )
		{
			this._selectedValue = val;
		}
	}
	
	public Caption get_ID()
	{
		return this._ID;
	}
	
	public ParameterType get_type()
	{
		return this._type;
	}
	
	public Object getSelectedValue()
	{
		return this._selectedValue;
	}
	
	public List< Object > getAllValues()
	{
		return this._values;
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
