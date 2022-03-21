/**
 * 
 */
package statistic;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import config.Player;
import control.controller.IControllerMetadata;
import general.ArrayTreeMap;
import general.ConvertTo;
import general.Tuple;
import lslStream.LSLStreamInfo;

/**
 * @author manuel
 *
 */
public class RegistrarStatistic
{
	//**********************
	//
	// Field IDs
	//
	//**********************
	
	public enum FieldType 
	{
		GAME_START
		
		, GAME_PAUSE
		
		, GAME_RESUME
		
		, GAME_END
		
		, NOTE_SHOW 
		// Se muestra la nota en la escena
		
		, NOTE_REMOVE 
		// Se elimina la nota de la escena
		
		, NOTE_ENTER_FRET 
		// La nota entra en la zona de accion
		
		, NOTE_EXIT_FRET 
		// La nota sale de la zona de accion
		
		, CONTROLLER_WAIT_RECORVER_LEVEL 
		// Se espera a que el control (mando) caiga por debajo del nivel de recuperación. El funcionamiento normal está deshabilitado
		
		, CONTROLLER_RESTORED_LEVEL 
		// El control (mando) cae por debajo del nivel de recuperación habilitando el normal funcionamiento 
		
		, CONTROLLER_LEVEL_REACH 
		// El control (mando) alcanza el nivel objetivo para generar una acción
		
		, CONTROLER_RECOVER_LEVEL_REACH 
		// El control (mando) cae por debajo del nivel de recuperación
		
		, CONTROLLER_MAINTAIN_LEVEL_REACH 
		// El control (mando) se mantiene en el nivel de acción el tiempo suficiente para generar la acción 
		
		, CONTROLLER_MAINTAIN_LEVEL_FINISH 
		// El control (mando) cae por debajo del nivel objetivo que generó una acción
		
		//, CONTROLER_LEVEL_REACH_WITHOUT_RECOVER_LEVEL // El control (mando) alcanza el nivel objetivo para generar una acción sin haber caído por debajo del nivel de recuperación
	};
	
	//**********************
	//
	// Variables
	//
	//**********************
	
	private static LocalDateTime startDateTime;
	
	private static Map< Integer, Double > sessionScore = new HashMap<Integer, Double>();
	
	private static ArrayTreeMap< Integer, Tuple< LocalDateTime, FieldType > > register = new ArrayTreeMap< Integer, Tuple< LocalDateTime, FieldType> >();
	
	private static Map< Integer, IControllerMetadata > controllerSettings = new HashMap< Integer, IControllerMetadata >();
	
	private static Map< Integer, LinkedList< Double[] > > controllerData = new HashMap< Integer, LinkedList< Double[] > >();
	
	private static Map< Integer, LSLStreamInfo > bioLSLSettings = new HashMap< Integer, LSLStreamInfo >();
	
	private static Map< String, LinkedList< Double[] > > biosignalData = new HashMap< String, LinkedList< Double[] > >( );
	
	/**
	 * 
	 */
	public static void startRegister()
	{
		clearRegister();
		
		startDateTime = LocalDateTime.now();
	}
	
	public static void setPlayerScore( int player, double score )
	{
		sessionScore.put( player, score );
	}
	
	public static Double getPlayerScore( int player )
	{
		Double score = sessionScore.get( player );
		
		if( score == null )
		{
			score = 0D;
		}
		
		return score;
	}
	
	public static synchronized void addControllerSetting( int playerID, IControllerMetadata meta )
	{
		if( meta != null )
		{
			controllerSettings.put(  playerID, meta );
			if( controllerData.get( playerID ) == null )
			{
				LinkedList< Double[] > cd = new LinkedList<Double[]>();
				controllerData.put( playerID, cd );
			}
		}
	}
	
	public static synchronized void addBiosignalStreamSetting( int playerID, LSLStreamInfo info)
	{
		if( info != null )
		{
			bioLSLSettings.put(  playerID, info );
			String id = getBioLSLDataID( playerID, info.content_type() );
			if( biosignalData.get( id ) == null )
			{
				LinkedList< Double[] > cd = new LinkedList<Double[]>();
				biosignalData.put( id, cd );
			}
		}
	}
	
	private static String getBioLSLDataID( int playerID, String type )
	{
		String id = playerID + type;
		
		return id;
	}
	
	public static synchronized void addControllerData( int playerID, double[] ctrData )
	{
		if( ctrData != null && ctrData.length > 0 )
		{
			LinkedList< Double[] > cd = controllerData.get( playerID );
			if( cd == null )
			{
				cd = new LinkedList<Double[]>();				
			}
			
			cd.add( ConvertTo.doubleArray2DoubleArray( ctrData ) );
			controllerData.put( playerID, cd );				
		}
	}
	
	public static synchronized void addBiosignalData( int playerID, String bioType, double[] ctrData )
	{
		if( ctrData != null && ctrData.length > 0 )
		{
			String id = getBioLSLDataID( playerID, bioType );
			
			LinkedList< Double[] > cd = biosignalData.get( id );
			if( cd == null )
			{
				cd = new LinkedList<Double[]>();				
			}
			
			cd.add( ConvertTo.doubleArray2DoubleArray( ctrData ) );
			biosignalData.put( id, cd );				
		}
	}
	
	public static synchronized void add( int playerID, FieldType field )
	{
		register.put(  playerID, new Tuple< LocalDateTime, FieldType >( LocalDateTime.now(), field ) );
	}
	
	public static synchronized void add( Set< Player > players, FieldType field )
	{
		for( Player player : players )
		{
			register.put(  player.getId(), new Tuple< LocalDateTime, FieldType >( LocalDateTime.now(), field ) );
		}
	}
	
	/**
	 * @return the playerID
	 */
	public synchronized static Set< Integer > getPlayerIDs()
	{
		return register.keySet();
	}
	
	/**
	 * @return the startDateTime
	 */
	public static LocalDateTime getStartDateTime()
	{
		return startDateTime;
	}
		
	public Set< Integer > getRegisteredPlayerIDs()
	{
		return register.keySet();
	}
	
	public static List< Tuple< LocalDateTime, FieldType > > getRegister( int playerID )
	{
		return register.get( playerID );
	}
	
	public static IControllerMetadata getControllerSetting( int playerID )
	{
		return controllerSettings.get( playerID );
	}
	
	public static LinkedList< Double[] > getControllerData( int playerID )
	{
		return controllerData.get( playerID );
	}
	
	public static void clearRegister()
	{
		startDateTime = null;
		
		register.clear();
		controllerSettings.clear();
		controllerData.clear();
		sessionScore.clear();
	}
}
