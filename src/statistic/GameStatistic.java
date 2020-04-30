/**
 * 
 */
package statistic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import config.Player;
import general.ArrayTreeMap;
import general.Tuple;

/**
 * @author manuel
 *
 */
public class GameStatistic
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
		// La nota ale de la zona de accion
		
		, CONTROLLER_WAIT_RECORVER_LEVEL 
		// Se espera a que el control (mando) caiga por debajo del nivel de recuperación. El funcionamiento normal está deshabilitado
		
		, CONTROLLER_RESTORED_LEVEL 
		// El control (mando) cae por debajo del nivel de recuperación habilitando el normal funcionamiento 
		
		, CONTROLER_LEVEL_REACH 
		// El control (mando) alcanza el nivel objetivo para generar una acción
		
		, CONTROLER_RECOVER_LEVEL_REACH 
		// El control (mando) cae por debajo del nivel de recuperación
		
		, CONTROLLER_MAINTAIN_LEVEL_REACH 
		// El control (mando) se mantiene en el nivel de acción el tiempo suficiente para generar la acción 
		
		, CONTROLLER_MAINTAIN_LEVEL_FINISH 
		// El control (mando) cae por debajo del nivel por debajo del nivel objetivo que generó una acción
		
		//, CONTROLER_LEVEL_REACH_WITHOUT_RECOVER_LEVEL // El control (mando) alcanza el nivel objetivo para generar una acción sin haber caído por debajo del nivel de recuperación
	};
	
	
	//**********************
	//
	// Variables
	//
	//**********************
	
	private static LocalDateTime startDateTime;  
	
	private static ArrayTreeMap< Integer, Tuple< LocalDateTime, FieldType > > register = new ArrayTreeMap< Integer, Tuple< LocalDateTime, FieldType> >();
	
	/**
	 * 
	 */
	public static void startRegister()
	{
		startDateTime = LocalDateTime.now();
		
		register.clear();
	}
	
	public static synchronized void add( Integer playerID, FieldType field )
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
	public static Set< Integer > getPlayerIDs()
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
	
	public static void clearRegister()
	{
		startDateTime = null;
		register.clear();
	}
}
