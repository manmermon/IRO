/**
 * 
 */
package statistic;

import java.time.LocalDateTime;

import config.Player;
import general.ArrayTreeMap;

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
	
	private static int playerID;
	private static LocalDateTime startDateTime;  
	
	private static ArrayTreeMap< LocalDateTime, FieldType > register = new ArrayTreeMap<LocalDateTime, FieldType>();
	
	/**
	 * 
	 */
	public static void setPlayerID( int id )
	{
		playerID = id;
		
		startDateTime = LocalDateTime.now();
		
		register.clear();
	}
	
	public static synchronized void add( FieldType field )
	{
		register.put( LocalDateTime.now(), field );
	}
	
	/**
	 * @return the playerID
	 */
	public static int getPlayerID()
	{
		return playerID;
	}
	
	/**
	 * @return the startDateTime
	 */
	public static LocalDateTime getStartDateTime()
	{
		return startDateTime;
	}
		
	public static ArrayTreeMap< LocalDateTime, FieldType > getRegister()
	{
		return register;
	}
	
	public static void clearRegister()
	{
		playerID = Player.ANONYMOUS_USER_ID;
		startDateTime = null;
		register.clear();
	}
}
