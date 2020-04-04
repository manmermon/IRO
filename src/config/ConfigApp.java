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
import java.util.List;
import java.util.Map;

import config.ConfigParameter.ParameterType;
import config.language.Caption;
import config.language.Language;
import exceptions.ConfigParameterException;
import general.Tuple;

public class ConfigApp 
{
	public static final String fullNameApp = "Interactive Rehab Orchestra";
	public static final String shortNameApp = "IRO";
	public static final Calendar buildDate = new GregorianCalendar( 2020, 03 - 1, 29 );

	public static final String version = "Version 1." + ( buildDate.get( Calendar.YEAR ) % 100 ) + "." + ( buildDate.get( Calendar.DAY_OF_YEAR ) );

	public static final String appDateRange = "2019-" + buildDate.get( Calendar.YEAR );

	public static final String DB_URL = "./user/db/data.db";
	
	public static final Tuple< Integer, Integer > playerPicSize = new Tuple<Integer, Integer>( 100, 100 );
	
	public static final Tuple< Integer, Integer > playerPicSizeIcon = new Tuple<Integer, Integer>( 32, 32 );
	
	public static final String SONG_LIST_SEPARATOR = ";";
	
	///////////
	
	public static final String LANGUAGE = "LANGUAGE";
	
	public static final String USER = "USER";
	public static final String USER_REACTION_TIME = "USER_REACTION_TIME";
	public static final String USER_RECOVER_TIME = "USER_RECOVER_TIME";
	
	public static final String SONG_LIST = "SONG_LIST";
	
	public static final String PREACTION_COLOR = "PREACTION_COLOR";
	public static final String WAITING_ACTION_COLOR = "WAITING_ACTION_COLOR";
	public static final String ACTION_COLOR = "ACTION_COLOR";
	
	///////////
	
	private static boolean test = false;
	
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
	
	private static Map< String, ConfigParameter > listUserConfig = new HashMap< String, ConfigParameter >();

	static
	{
		create_Key_Value();
	}

	private static void create_Key_Value()
	{
		listUserConfig.clear();

		loadDefaultProperties();
	}
	
	public static ConfigParameter getProperty( String propertyID )
	{
		ConfigParameter par = listUserConfig.get( propertyID );
		
		return par;
	}
		
	public static Collection< ConfigParameter > getParameters()
	{
		return listUserConfig.values();
	}
	
	public static void loadDefaultProperties()
	{
		try
		{
			listUserConfig.clear();
			
			loadDefaultLanguage(  );
			loadDefaultUser(  );
			loadDefaultUserReactionTime(  );
			loadDefaultUserRecoverTime(  );
			loadDefaultActionColors(  );
			loadDefaultSongList( );
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private static void loadDefaultUser( ) throws ConfigParameterException
	{
		Caption id = new Caption( USER, Language.defaultLanguage, Language.getCaption( Language.defaultLanguage, Language.PLAYER ) );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.USER );		
		par.add( new User() );		
		
		listUserConfig.put( USER, par );
	}
	
	private static void loadDefaultLanguage(  ) throws ConfigParameterException
	{			
		List< String > Langs = Language.getAvaibleLanguages();
		
		Caption id = new Caption( LANGUAGE, Language.defaultLanguage, Language.getCaption( Language.LANGUAGE_TXT, Language.defaultLanguage ) );
		
		for( int i = 1; i < Langs.size(); i++ )
		{	
			String lang = Langs.get( i );
			id.setCaption( lang, Language.getCaption( Language.LANGUAGE_TXT, lang ) );
		}
		
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.STRING );
		par.addAll( Langs );
		
		par.setSelectedValue( Language.getCurrentLanguage() );
		
		listUserConfig.put( LANGUAGE, par );
	}
	
	private static void loadDefaultUserReactionTime( ) throws ConfigParameterException
	{
		Caption id = new Caption( USER_REACTION_TIME, Language.defaultLanguage, "reaction time" );
		id.setCaption( "es-es", "tiempo de reacción" );
		
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.NUMBER );
		par.add( 2D );
		
		
		listUserConfig.put( USER_REACTION_TIME, par );
	}
	
	private static void loadDefaultUserRecoverTime( ) throws ConfigParameterException
	{
		Caption id = new Caption( USER_RECOVER_TIME, Language.defaultLanguage, "recover time" );
		id.setCaption( "es-es", "tiempo de recuperación" );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.NUMBER );
		par.add( 2D );
				
		listUserConfig.put( USER_RECOVER_TIME, par );
	}
	
	private static void loadDefaultActionColors( )
	{
		try
		{
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
			
			Caption id = new Caption( PREACTION_COLOR, Language.defaultLanguage, "preaction color" );
			id.setCaption( "es-es", "pre-acción" );
			ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.COLOR );
			par.addAll( colors );
			listUserConfig.put( PREACTION_COLOR, par );
			
			id = new Caption( WAITING_ACTION_COLOR, Language.defaultLanguage, "waiting-action color" );
			id.setCaption( "es-es", "esperando acción" );
			par = new ConfigParameter( id, ConfigParameter.ParameterType.COLOR );
			colors.remove( Color.blue );
			colors.add( 0, Color.blue );
			par.addAll( colors );					
			listUserConfig.put( WAITING_ACTION_COLOR, par );
			
			id = new Caption( ACTION_COLOR, Language.defaultLanguage, "action color" );
			id.setCaption( "es-es", "acción realizada" );
			par = new ConfigParameter( id, ConfigParameter.ParameterType.COLOR );
			colors.remove( Color.green );
			colors.add( 0, Color.green );
			par.addAll( colors );		
			listUserConfig.put( ACTION_COLOR, par );
		}
		catch ( ConfigParameterException e) 
		{
			e.printStackTrace();
		}
	}
	
	private static void loadDefaultSongList( )
	{
		try
		{
			Caption id = new Caption( SONG_LIST, Language.defaultLanguage, Language.getCaption( Language.MUSIC_LIST , Language.defaultLanguage ) );
			id.setCaption( "es-es", Language.getCaption( Language.MUSIC_LIST , "es-es" ) );
			
			ConfigParameter par = new ConfigParameter( id, ParameterType.STRING );
			
			listUserConfig.put( SONG_LIST, par );
		}
		catch ( ConfigParameterException e) 
		{
			e.printStackTrace();
		}
	}
}