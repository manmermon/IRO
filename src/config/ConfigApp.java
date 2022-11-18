/*
 * Work uses part of CLIS <https://github.com/manmermon/CLIS> by Manuel Merino Monge 
 * 
 * Copyright 2019 by Manuel Merino Monge <manmermon@dte.us.es>
 *  
 *   This file is part of IRO.
 *
 *   IRO is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   LSLRec is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with LSLRec.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   Project's URL: https://github.com/manmermon/IRO
 */

package config;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import config.ConfigParameter.ParameterType;
import config.language.Caption;
import config.language.Language;
import exceptions.ConfigParameterException;
import general.NumberRange;
import general.Tuple;

public class ConfigApp 
{
	public static final String fullNameApp = "Interactive Rehab Orchestra";
	public static final String shortNameApp = "IRO";
	public static final Calendar buildDate = new GregorianCalendar( 2022, 11 - 1, 18 );

	public static final String version = "Version 1." + ( buildDate.get( Calendar.YEAR ) % 100 ) + "." + ( buildDate.get( Calendar.DAY_OF_YEAR ) );

	public static final String appDateRange = "2020-" + buildDate.get( Calendar.YEAR );

	//***********
	//
	// PATHS 
	//
	//***********
	
	public static final String SYSTEM_LIB_WIN_PATH = "systemLib/win/";
	public static final String SYSTEM_LIB_LINUX_PATH = "systemLib/linux/";
	public static final String SYSTEM_LIB_MACOS_PATH = "systemLib/macox/";
	
	public static final String RESOURCES_PATH = "./resources/";
	
	public static final String SONG_FILE_PATH = RESOURCES_PATH + "sheets/";
	public static final String BACKGROUND_SPRITE_FILE_PATH = RESOURCES_PATH + "background/";
	public static final String NOTE_SPRITE_FILE_PATH = RESOURCES_PATH + "note/";
	
	public static final int MAX_NUM_PLAYERS = 7;
	
	public static final Tuple< Integer, Integer > playerPicSize = new Tuple<Integer, Integer>( 100, 100 );
	
	public static final Tuple< Integer, Integer > playerPicSizeIcon = new Tuple<Integer, Integer>( 48, 48 );
	
	public static final String SONG_LIST_SEPARATOR = ";";

	
	//***********
	//
	//  
	//
	//***********
	
	public static final String SELECTED_CONTROLLER = "SELECTED_CONTROLLER";
	public static final String SELECTED_BIOSIGNAL = "SELECTED_BIOSIGNAL";
	
	public static final String LANGUAGE = "LANGUAGE";
	
	//public static final String PLAYER = "PLAYER";
	
	//public static final String MULTIPLAYER = "MULTIPLAYER";
		
	public static final String CONTINUOUS_SESSION = "CONTINUOUS_SESSION"; 
	
	public static final String SAM_TEST = "SAM_TEST";
			
	/*
	 * Table: settings
	 */

	public static final String REACTION_TIME = "reactionTime";
	public static final String RECOVER_TIME = "recoverTime";

	public static final String SONG_LIST = "songs";

	public static final String PREACTION_COLOR = "colorPreaction";
	public static final String WAITING_ACTION_COLOR = "colorWaitingAction";
	public static final String ACTION_COLOR = "colorAction";

	public static final String INPUT_MIN_VALUE = "minInputValue";
	public static final String INPUT_MAX_VALUE = "maxInputValue";

	public static final String INPUT_SELECTED_CHANNEL = "selectedChannel";

	public static final String TIME_IN_INPUT_TARGET = "timeInInputTarget";

	public static final String BACKGROUND_IMAGE = "backgroundImage";
	public static final String NOTE_IMAGE = "noteImage";

	public static final String MOV_REPETITIONS = "repetitions";

	public static final String LIMIT_SESSION_TIME = "sessionTime";
	
	public static final String MUTE_SESSION = "MUTE_SESSION";
	
	public static final String TASK_BLOCK_TIME = "taskBlockTime";
	
	public static final String REST_TASK_TIME = "restTaskTime";
	
	///////////
	
	private static boolean test = true;
	
	public static boolean isTesting()
	{
		//return true;
		return test;
	}
	
	public static void setTesting( boolean t )
	{
		test = t;
	}
	
	
	////////////////////////
	
	private static Map< String, ConfigParameter > listGeneralConfig = new HashMap<String, ConfigParameter>();
	
	private static LinkedHashMap< Player, Settings > listPlayerConfig = new LinkedHashMap< Player, Settings >();

	static
	{
		create_Key_Value();
		DataBaseSettings.dbLoadFields();
	}

	private static void create_Key_Value()
	{
		listGeneralConfig.clear();
		listPlayerConfig.clear();

		resetPlayerSettings();
	}
	
	public static Set< Player > getPlayers()
	{
		return listPlayerConfig.keySet();
	}
	
	public static Player getFirstPlayer()
	{
		Player firstPlayer = null;
		
		Iterator< Entry< Player, Settings >  > it=  listPlayerConfig.entrySet().iterator();
		if( it.hasNext() )
		{
			firstPlayer = it.next().getKey();
		}
		
		return firstPlayer;
	}
	
	/**
	 * 
	 * @param player
	 * @return true if setting is loaded, otherwise false
	 */
	public static boolean loadPlayerSetting( Player player )
	{	
		boolean load = false;
		
		if( player != null )
		{
			try
			{	
				List< Tuple< String, Object > > settings = DataBaseSettings.dbGetPlayerSetting( player.getId() );
			
				load = ( !settings.isEmpty() );
				
				if( load )
				{
					Settings cfg = getDefaultSettings();
					cfg.setPlayer( player );
					listPlayerConfig.put( player, cfg );

					for( Tuple< String, Object > par : settings )
					{
						Object val = par.t2;

						ConfigParameter p = cfg.getParameter( par.t1 );
						if( p != null && val != null)
						{
							if( p.get_type() == ParameterType.COLOR )
							{
								val = new Color( (Integer)val );
							}
							
							p.setSelectedValue( val );
						}
					}					
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		
		return load;
	}
	
	public static ConfigParameter getGeneralSetting( String idPar )
	{
		return listGeneralConfig.get( idPar );
	}
	
	public static void setGeneralSetting( String idPar, ConfigParameter par )
	{
		if( par != null && idPar != null )
		{
			listGeneralConfig.put( idPar, par );
		}
	}
	
	public static Settings getPlayerSetting( Player player )
	{				
		return listPlayerConfig.get( player );
	}
	
	public static void removePlayerSetting( Player player )
	{
		listPlayerConfig.remove( player );
	}
	
	public static Collection< Settings > getSettings()
	{
		return listPlayerConfig.values();
	}
	
	public static void resetPlayerSettings()
	{
		try
		{
			listPlayerConfig.clear();
			//listGeneralConfig.clear();
			
			loadDefaultLanguage( );
			
			/*
			if( isTesting() )
			{			
				loadDefaultPlayerSetting( new Player() );
			}
			//*/
			
			loadDefaultPlayerSetting( new Player() );
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void loadDefaultAllCurrentPlayerSettings() throws ConfigParameterException
	{
		for( Player player : getPlayers() )
		{
			loadDefaultPlayerSetting( player );
		}
	}	
	
	public static void loadDefaultPlayerSetting( Player player ) throws ConfigParameterException
	{
		Settings cfg = getDefaultSettings();
		cfg.setPlayer( player );
				
		listPlayerConfig.put( player, cfg );		
	}
	
	public static Settings getDefaultSettings( ) throws ConfigParameterException
	{
		Settings cfg = new Settings();
		
		ConfigParameter par = loadDefaultUserReactionTime( );
		cfg.setParameter( par.get_ID().getID(), par );
				
		par = loadDefaultUserRecoverTime(  );
		cfg.setParameter( par.get_ID().getID(), par );
		
		for( ConfigParameter p : loadDefaultActionColors(  ) )
		{
			cfg.setParameter( p.get_ID().getID(), par );
		}
		
		par = loadDefaultSongList( );
		cfg.setParameter( par.get_ID().getID(), par );
		
		for( ConfigParameter p : loadDefaultInputRange( ) )
		{
			cfg.setParameter( p.get_ID().getID(), p );
		}
				
		par = loadDefaultTimeInInputTarget();
		cfg.setParameter( par.get_ID().getID(), par );
		
		par = loadDefaultSelectedChannel();
		cfg.setParameter( par.get_ID().getID(), par );
		
		par = loadDefaultSelectedController();
		cfg.setParameter( par.get_ID().getID(), par );
		
		par = loadDefaultSelectedBiosignal();
		cfg.setParameter( par.get_ID().getID(), par );
		
		par = loadDefaultBackgroundImage();
		cfg.setParameter( par.get_ID().getID(), par );
		
		par = loadDefaultNoteImage();
		cfg.setParameter( par.get_ID().getID(), par );
		
		for( ConfigParameter p : loadDefaultActionColors() )
		{
			cfg.setParameter( p.get_ID().getID(), p );
		}
		
		par = loadDefaultRepetitions();
		cfg.setParameter( par.get_ID().getID(), par );
		
		//par = loadDefaultSessionTime();
		//cfg.setParameter( par.get_ID().getID(), par );
		
		par = loadDefaultTaskBlockTime();
		cfg.setParameter( par.get_ID().getID(), par );
		
		
		par = loadDefaultRestTaskTime();
		cfg.setParameter( par.get_ID().getID(), par );
		
		
		return cfg;
	}
	
	//
	//
	// Load default parameters
	//
	//
	
	private static Caption getCaptions(String wordID )
	{
		List< String > Langs = Language.getAvaibleLanguages();
		
		Caption cap = new Caption( wordID, Language.defaultLanguage, Language.getCaption( wordID, Language.defaultLanguage ) ); 
		
		for( int i = 1; i < Langs.size(); i++ )
		{	
			String lang = Langs.get( i );
			if( !lang.equals( Language.defaultLanguage ) )
			{
				cap.setCaption( lang, Language.getCaption( wordID, lang ) );
			}
		}
		
		return cap;
	}
		
	private static void loadDefaultLanguage(  ) throws ConfigParameterException
	{			
		List< String > Langs = Language.getAvaibleLanguages();
		
		Caption id = getCaptions( Language.LANGUAGE_TXT ); 				
		id.setID( LANGUAGE );
		
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.STRING );
		par.addAllOptions( Langs );
		
		par.setSelectedValue( Language.getCurrentLanguage() );
		par.setPriority( Integer.MAX_VALUE );
		
		listGeneralConfig.put( LANGUAGE, par );
	}
	
	private static ConfigParameter loadDefaultBackgroundImage() throws ConfigParameterException
	{	
		Caption id = getCaptions( Language.BACKGROUND );
		id.setID( BACKGROUND_IMAGE );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.OTHER );
		par.setPriority(  Integer.MIN_VALUE );
		
		return par;
	}
	
	private static ConfigParameter loadDefaultNoteImage() throws ConfigParameterException
	{
		Caption id = getCaptions( Language.NOTE );
		id.setID( NOTE_IMAGE );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.OTHER );
		par.setPriority(  Integer.MIN_VALUE );

		return par;		
	}
	
	private static ConfigParameter loadDefaultSelectedController() throws ConfigParameterException
	{
		Caption id = getCaptions( Language.CONTROLLER );
		id.setID( SELECTED_CONTROLLER );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.OTHER );
		par.setPriority(  Integer.MIN_VALUE );

		return par; 
	}
	
	private static ConfigParameter loadDefaultSelectedBiosignal() throws ConfigParameterException
	{
		Caption id = getCaptions( Language.BIOSIGNAL );
		id.setID( SELECTED_BIOSIGNAL );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.OTHER );
		par.setPriority(  Integer.MIN_VALUE );

		return par; 
	}
	
	private static ConfigParameter loadDefaultUserReactionTime( ) throws ConfigParameterException
	{		
		Caption id = getCaptions( Language.REACTION_TIME );
		id.setID( REACTION_TIME );
		NumberRange rng = new NumberRange( 0.5, Double.MAX_VALUE );
		ConfigParameter par = new ConfigParameter( id, rng );
		par.setSelectedValue( 2D );
		par.setPriority( 10 );
		
		return par;
	}
	
	private static ConfigParameter loadDefaultTimeInInputTarget( ) throws ConfigParameterException
	{
		Caption id = getCaptions( Language.TIME_INPUT_TARGET );
		id.setID( TIME_IN_INPUT_TARGET );
		NumberRange rng = new NumberRange( 0, Double.MAX_VALUE );
		ConfigParameter par = new ConfigParameter( id, rng );
		par.setSelectedValue( 0D );
		par.setPriority( 6 );
		
		return par;
	}
	
	private static ConfigParameter loadDefaultUserRecoverTime( ) throws ConfigParameterException
	{		
		Caption id = getCaptions( Language.RECOVER_TIME );
		id.setID( RECOVER_TIME );
		double minVal = 0;
		NumberRange rng = new NumberRange( minVal, Double.MAX_VALUE );
		ConfigParameter par = new ConfigParameter( id, rng );
		par.setSelectedValue( 2D );
		par.setPriority( 9 );
				
		return par;
	}
	
	private static List< ConfigParameter > loadDefaultActionColors( )  throws ConfigParameterException
	{	
		List< ConfigParameter > pars = new ArrayList<ConfigParameter>();
		
		List< Color > colors = new ArrayList<Color>();
		colors.add( Color.red );
		colors.add( Color.black );
		colors.add( Color.white );
		colors.add( Color.blue );
		colors.add( Color.cyan );
		colors.add( Color.gray );
		colors.add( Color.green );
		colors.add( Color.lightGray );
		colors.add( Color.magenta );
		colors.add( Color.orange );
		colors.add( Color.pink );			
		colors.add( Color.yellow );

		Caption id = getCaptions( Language.PREACTION_COLOR );
		id.setID( PREACTION_COLOR );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.COLOR );
		par.addAllOptions( colors );
		par.setSelectedValue( Color.red );
		par.setPriority( 2 );
		pars.add( par );

		id = getCaptions( Language.WAITING_ACTION_COLOR);
		id.setID( WAITING_ACTION_COLOR );
		par = new ConfigParameter( id, ConfigParameter.ParameterType.COLOR );
		colors.remove( Color.blue );
		colors.add( 0, Color.blue );
		par.addAllOptions( colors );
		par.setSelectedValue( Color.blue );
		par.setPriority( 1 );
		pars.add( par );

		id = getCaptions( Language.ACTION_COLOR );
		id.setID( ACTION_COLOR );
		par = new ConfigParameter( id, ConfigParameter.ParameterType.COLOR );
		colors.remove( Color.green );
		colors.add( 0, Color.green );
		par.addAllOptions( colors );
		par.setSelectedValue( Color.green );
		par.setPriority( 0 );
		pars.add( par );
		
		return pars;
	}
	
	private static ConfigParameter loadDefaultSongList( ) throws ConfigParameterException
	{	
		Caption id = getCaptions( Language.MUSIC_LIST );
		id.setID( SONG_LIST );

		ConfigParameter par = new ConfigParameter( id, ParameterType.SONG );
		par.setPriority( Integer.MIN_VALUE + 1 );

		return par;
	}
	
	private static ConfigParameter loadDefaultSelectedChannel() throws ConfigParameterException
	{			
		Caption id = getCaptions( Language.SELECTED_CHANNEL );
		id.setID( INPUT_SELECTED_CHANNEL );

		NumberRange r = new NumberRange( 1D, Double.MAX_VALUE);

		ConfigParameter par = new ConfigParameter( id, r );
		par.setSelectedValue( 1D );
		par.setPriority( 3 );

		return par;
	}
	
	private static List< ConfigParameter > loadDefaultInputRange() throws ConfigParameterException
	{	
		List< ConfigParameter > pars = new ArrayList<ConfigParameter>();
		
		Caption idMin = getCaptions( Language.RECOVERY_LEVEL );
		idMin.setID( INPUT_MIN_VALUE );
		Caption idMax = getCaptions( Language.ACTION_LEVEL );
		idMax.setID( INPUT_MAX_VALUE );

		ConfigParameter parMin = new ConfigParameter( idMin, ParameterType.NUMBER );
		parMin.setSelectedValue(  -0.7D );
		parMin.setPriority(  5 );

		ConfigParameter parMax = new ConfigParameter( idMax, ParameterType.NUMBER );
		parMax.setSelectedValue( 00D );
		parMax.setPriority( 4 );

		pars.add( parMin );
		pars.add( parMax );
		
		return pars;
	}
	
	private static ConfigParameter loadDefaultRepetitions() throws ConfigParameterException
	{	
		Caption id = getCaptions( Language.REPETITIONS );
		id.setID( MOV_REPETITIONS );

		NumberRange r = new NumberRange( 1D, Double.MAX_VALUE);

		ConfigParameter par = new ConfigParameter( id, r );
		par.setSelectedValue( 1D );
		par.setPriority( 7 );

		return par;

	}
	
	private static ConfigParameter loadDefaultTaskBlockTime() throws ConfigParameterException
	{	
		Caption id = getCaptions( Language.TASK_BLOCK_TIME );
		id.setID( TASK_BLOCK_TIME );

		NumberRange r = new NumberRange( 1D, Double.MAX_VALUE);

		ConfigParameter par = new ConfigParameter( id, r );
		par.setSelectedValue( 30D );
		par.setPriority( 12 );

		return par;

	}
	
	private static ConfigParameter loadDefaultRestTaskTime() throws ConfigParameterException
	{	
		Caption id = getCaptions( Language.REST_TASK_TIME );
		id.setID( REST_TASK_TIME );

		NumberRange r = new NumberRange( 0D, Double.MAX_VALUE);

		ConfigParameter par = new ConfigParameter( id, r );
		par.setSelectedValue( 0D );
		par.setPriority( 11  );

		return par;

	}
	
	/*
	private static ConfigParameter loadDefaultSessionTime() throws ConfigParameterException
	{	
		Caption id = getCaptions( Language.SESSION_TIME );
		id.setID( SESSION_TIME );

		NumberRange r = new NumberRange( 0D, Double.MAX_VALUE);

		ConfigParameter par = new ConfigParameter( id, r );
		par.setSelectedValue( 0D );
		par.setPriority( 10 );

		return par;

	}
	*/
		
	


}



