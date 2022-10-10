/**
 * 
 */
package statistic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.AbstractDocument.Content;

import biosignal.Biosignal;
import config.Player;
import general.ArrayTreeMap;
import general.ConvertTo;
import general.StringTuple;
import general.Tuple;
import lslInput.LSLStreamInfo;
import lslInput.LSLStreamInfo.StreamType;
import lslInput.LSLUtils;
import lslInput.stream.IInputStreamMetadata;
import lslInput.stream.IInputStreamMetadata.InputSourceType;
import lslInput.stream.controller.IControllerMetadata;

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
	
	public enum GameFieldType 
	{
		GAME_START
		
		, LEVEL_START
		
		, LEVEL_PAUSE
		
		, LEVEL_RESUME
		
		, LEVEL_END
		
		, GAME_END
		
		, NOTE_SHOW 
		// Se muestra la nota en la escena
		
		, NOTE_REMOVE 
		// Se elimina la nota de la escena
		
		, NOTE_ENTER_FRET 
		// La nota entra en la zona de accion
		
		, NOTE_EXIT_FRET 
		// La nota sale de la zona de accion
		
		//, CONTROLLER_WAIT_RECORVER_LEVEL 
		// Se espera a que el control (mando) caiga por debajo del nivel de recuperacion. El funcionamiento normal esta deshabilitado
		
		, CONTROLLER_ENABLE_MOVEMENT 
		// El control (mando) cae por debajo del nivel de recuperacion habilitando el normal funcionamiento 
		
		, CONTROLLER_ACTION_LEVEL_REACH 
		// El control (mando) alcanza el nivel objetivo para generar una accion
		
		, CONTROLLER_RECOVERY_LEVEL_REACH 
		// El control (mando) cae por debajo del nivel de recuperacion
		
		, CONTROLLER_MAINTAIN_ACTION_LEVEL 
		// El control (mando) se mantiene en el nivel de accion el tiempo suficiente para generar la accion 
		
		, CONTROLLER_EXIT_ACTION_LEVEL 
		// El control (mando) cae por debajo del nivel objetivo que genero una accion
		
		, ERROR_CONTROLLER_DISCONNECTED
		
		//, CONTROLER_LEVEL_REACH_WITHOUT_RECOVER_LEVEL 
		// El control (mando) alcanza el nivel objetivo para generar una accion sin haber caido por debajo del nivel de recuperacion
	};
	
	//**********************
	//
	// Variables
	//
	//**********************
	
	private static LocalDateTime startDateTime;
	
	private static Map< Integer, Double > sessionScore = new HashMap<Integer, Double>();
	
	private static ArrayTreeMap< Integer, Tuple< LocalDateTime, GameFieldType > > register = new ArrayTreeMap< Integer, Tuple< LocalDateTime, GameFieldType> >();
	
	private static Map< Integer, IControllerMetadata > controllerSettings = new HashMap< Integer, IControllerMetadata >();
	
	private static Map< Integer, LinkedList< Double[] > > controllerData = new HashMap< Integer, LinkedList< Double[] > >();
	
	private static ArrayTreeMap< Integer, IInputStreamMetadata > bioLSLSettings = new ArrayTreeMap< Integer, IInputStreamMetadata >();
	
	private static Map< String, LinkedList< Double[] > > biosignalData = new HashMap< String, LinkedList< Double[] > >( );
	
	private static ArrayTreeMap< Integer, String > valArEmoRegister = new ArrayTreeMap< Integer, String >();
	
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
	
	public static synchronized void addBiosignalStreamSetting( int playerID, IInputStreamMetadata meta)
	{
		if( meta != null )
		{
			bioLSLSettings.put(  playerID, meta );
			String id = getBioLSLDataID( playerID, meta );
			if( biosignalData.get( id ) == null )
			{
				LinkedList< Double[] > cd = new LinkedList< Double[] >();
				biosignalData.put( id, cd );
			}
		}
	}
		
	private static String getBioLSLDataID( int playerID, IInputStreamMetadata meta )
	{
		String id = null;
		
		if( meta != null )
		{
			if( meta.getInputSourceType() == InputSourceType.LSLSTREAM )
			{	
				LSLStreamInfo strInfo = (LSLStreamInfo)meta.getInputSourseSetting();
				String ctype = strInfo.content_type();
				
				Biosignal.Type bioTyope = Biosignal.getBiosignalType( ctype );
				
				if( bioTyope != null )
				{
					id = playerID + bioTyope.name() + meta.getInputSourceID();
				}
			}
		}
		
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
	
	public static synchronized void addBiosignalData( int playerID, IInputStreamMetadata meta, double[] ctrData )
	{
		if( ctrData != null && ctrData.length > 0 )
		{
			String id = getBioLSLDataID( playerID, meta );
			
			LinkedList< Double[] > cd = biosignalData.get( id );
			if( cd == null )
			{
				cd = new LinkedList<Double[]>();				
			}
			
			cd.add( ConvertTo.doubleArray2DoubleArray( ctrData ) );
			biosignalData.put( id, cd );				
		}
	}
	
	public static synchronized void addGameData( int playerID, GameFieldType field )
	{
		register.put(  playerID, new Tuple< LocalDateTime, GameFieldType >( LocalDateTime.now(), field ) );
	}
	
	public static synchronized void addGameData( Set< Player > players, GameFieldType field )
	{
		for( Player player : players )
		{
			register.put(  player.getId(), new Tuple< LocalDateTime, GameFieldType >( LocalDateTime.now(), field ) );
		}
	}
	
	public static synchronized void addValenceArousalEmotionData( int playerID, String sam )
	{
		sam = ( sam == null ) ? "" : sam;
		
		valArEmoRegister.put( playerID, sam );
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
	
	public static List< Tuple< LocalDateTime, GameFieldType > > getRegister( int playerID )
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
	
	public static List< IInputStreamMetadata > getBiosignalStreamSettings( int playerID )
	{
		return bioLSLSettings.get( playerID );
	}
	
	public static LinkedList< Double[] > getBiosignalData( int playerID, IInputStreamMetadata meta )
	{
		LinkedList< Double[] > bioData = null;
		
		if( meta != null )
		{
			String idBioData = getBioLSLDataID( playerID, meta );

			bioData = biosignalData.get( idBioData );
		}
		
		return bioData;
	}
	
	public static List< String > getValenceArousalEmotionData( int playerID )
	{
		return valArEmoRegister.get( playerID );
	}
	
	public static void clearRegister()
	{
		startDateTime = null;
		
		register.clear();
		controllerSettings.clear();
		controllerData.clear();
		sessionScore.clear();
		valArEmoRegister.clear();
	}
}
