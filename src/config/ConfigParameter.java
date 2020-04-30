package config;

import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.language.Caption;
import exceptions.ConfigParameterException;
import general.NumberRange;

public class ConfigParameter
{
	public enum ParameterType { USER, STRING, NUMBER, BOOLEAN, COLOR, SONG, OTHER };
	
	private Caption _ID = null;
	private ParameterType _type = ParameterType.NUMBER;
	private Object _selectedValue = null;
	private List< Object > _options = null;
	private NumberRange _rng = null;
	private Player _player = new Player();
	
	private int priority = 0;
	
	public ConfigParameter( Caption id, ParameterType type ) throws ConfigParameterException
	{
		if( id == null )
		{
			throw new ConfigParameterException( "Parameter ID is null." );
		}
		
		this._ID = id;
		this._type = type;		
		this._options = new ArrayList< Object >();
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
		this._options = new ArrayList< Object >();
		this._selectedValue = null;
	}
	
	/**
	 * @return the priority
	 */
	public int getPriority()
	{
		return this.priority;
	}
	
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
	
	public void addOption( Object value ) throws ConfigParameterException
	{
		String errMsg = this.checkValue( value );
		if( errMsg != null ) 
		{
			throw new ConfigParameterException( errMsg );
		}
		
		this._options.add( value );
	}
 	
	private String checkValue( Object value)
	{
		String errMsg = null;
		
		if( value == null )
		{
			errMsg = "Value is null.";
		}
		else
		{	
			switch ( this._type ) 
			{
				case NUMBER:
				{
					if( !(value instanceof Number) )
					{
						errMsg = "Value is not a Number.";
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
						errMsg = "Value is not a String.";
					}
					
					break;
				}
				case BOOLEAN:
				{
					if( !(value instanceof Boolean) )
					{
						errMsg = "Value is not a Boolean.";
					}
					break;
				}
				case COLOR:
				{
					if( !(value instanceof Color) )
					{
						errMsg = "Value is not a Color.";
					}
					
					break;					
				}
				case USER:
				{
					if( !( value instanceof Player ) )
					{
						errMsg = "Value is not a User.";
					}
					break;
				}
				case SONG:
				{
					if( !( value instanceof String ) )
					{
						errMsg = "Value is not the song file's path.";
					}
					
					break;
				}
				case OTHER:
				{
					break;
				}
				default:
				{
					errMsg = "Parameter type unknown.";
					break;
				}
			}
		}
		
		return errMsg;
	}
	
	public void addAllOptions( List values ) throws ConfigParameterException
	{
		if( values != null )
		{
			for( Object val : values )
			{
				this.addOption( val );
			}
		}
	}
	
	public void removeAllOptions()
	{
		this._selectedValue = null;
		this._options.clear();
	}
	
	public void setPlayer( Player player )
	{
		this._player = player;
	}
	
	public Player getUserID()
	{
		return this._player;
	}
	
	public void clearOptions()
	{
		this._selectedValue = null;
		this._options.clear();		 
	}
	
	public void setSelectedValue( Object val ) throws ConfigParameterException
	{
		String errMsg = this.checkValue( val );
		
		if( errMsg != null )
		{
			throw new ConfigParameterException( errMsg );
		}
		
		if( !this._options.isEmpty() )
		{
			if( this._options.contains( val ) )
			{
				this._selectedValue = val;
			}
		}
		else
		{
			this._selectedValue = val;
		}
		
		if( this._player != null && !this._player.isAnonymous() )
		{
			try
			{
				ConfigApp.dbUpdatePlayerSetting( this._player, this._ID.getID() );
			} 
			catch (SQLException ex)
			{
				ex.printStackTrace();
				
				throw new ConfigParameterException( ex );
			}
		}
	}
	
	public void removeSelectedValue()
	{
		this._selectedValue = null;
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
	
	public List< Object > getAllOptions()
	{
		return this._options;
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
