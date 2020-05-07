/**
 * 
 */
package statistic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import general.ArrayTreeMap;
import general.NumberRange;
import general.Pair;

/**
 * @author manuel merino monge
 *
 */
public class GameSessionStatistic
{
	private long idSession;
	private Map< Integer, Pair< ControllerMetadataExtenderAdapter, Double[][] > > sessionControllerData;
	private ArrayTreeMap< Long, Pair< Integer, String > > gameEvent;
	
		
	public GameSessionStatistic( long id )
	{
		this.idSession = id;
		
		this.sessionControllerData = new HashMap<Integer, Pair<ControllerMetadataExtenderAdapter,Double[][] > >();
		this.gameEvent = new ArrayTreeMap<Long, Pair<Integer,String>>();
	}
	
	public long getStartSessionInMillis()
	{
		return this.idSession;
	}
	
	public Date getSessionDate()
	{
		return new Date( this.idSession );
	}
	
	public void addPlayer( int player )
	{
		if( !this.sessionControllerData.containsKey( player ) )
		{
			ControllerMetadataExtenderAdapter cma = new ControllerMetadataExtenderAdapter();
			Pair< ControllerMetadataExtenderAdapter, Double[][] > pair = new Pair<ControllerMetadataExtenderAdapter, Double[][]>( cma, null );
			
			this.sessionControllerData.put( player, pair );
		}
	}
	
	public Pair< ControllerMetadataExtenderAdapter, Double[][] > getControllerData( int player )
	{
		return this.sessionControllerData.get( player );
	}
	
	public void setControllerName( int player, String name )
	{
		Pair< ControllerMetadataExtenderAdapter, Double[][] > pair = this.getControllerData( player );
		
		if( pair != null )
		{
			pair.getX1().setName( name );
		}
	}
	
	public void setSamplingRate( int player, double samplingRate )
	{
		Pair< ControllerMetadataExtenderAdapter, Double[][] > pair = this.getControllerData( player );
		
		if( pair != null )
		{
			pair.getX1().setSamplingRate( samplingRate );
		}
	}
	
	public void setNumberOfChannels( int player, int n )
	{
		Pair< ControllerMetadataExtenderAdapter, Double[][] > pair = this.getControllerData( player );
		
		if( pair != null )
		{
			pair.getX1().setNumberOfChannels( n );
		}
	}
	
	public void setSelectedChannel( int player, int ch )
	{
		Pair< ControllerMetadataExtenderAdapter, Double[][] > pair = this.getControllerData( player );
		
		if( pair != null )
		{
			pair.getX1().setSelectedChannel( ch );
		}
	}
	
	public void setRecoverLevel( int player, double value )
	{
		Pair< ControllerMetadataExtenderAdapter, Double[][] > pair = this.getControllerData( player );
		
		if( pair != null )
		{
			pair.getX1().setRecoverInputLevel( value );
		}
	}
	
	public void setActionLevel( int player, NumberRange range )
	{
		Pair< ControllerMetadataExtenderAdapter, Double[][] > pair = this.getControllerData( player );
		
		if( pair != null )
		{
			pair.getX1().setActionInputLevel( range );
		}
	}
	
	public void setTargetTimeInLevelAction( int player, double time )
	{
		Pair< ControllerMetadataExtenderAdapter, Double[][] > pair = this.getControllerData( player );
		
		if( pair != null )
		{
			pair.getX1().setTargetTimeInLevelAction( time );
		}
	}
	
	public void setSessionControllerData( int player, Double[][] data )
	{
		Pair< ControllerMetadataExtenderAdapter, Double[][] > pair = this.getControllerData( player );
		
		if( pair != null )
		{
			pair.setX2( data );
		}
	}

	public void addGameEvent( long time, int player, String event )
	{
		this.gameEvent.put( time, new Pair< Integer, String>( player, event ) );
	}
	
	public ArrayTreeMap< Long, Pair< Integer, String > > getGameEvent( )
	{
		return this.gameEvent;
	}
}
