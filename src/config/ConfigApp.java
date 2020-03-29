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

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.ConfigurationException;

import config.language.Caption;
import config.language.Language;
import exceptions.ConfigParameterException;
import general.NumberRange;

public class ConfigApp 
{
	public static final String fullNameApp = "Interactive Rehab Orchestra";
	public static final String shortNameApp = "IRO";
	public static final Calendar buildDate = new GregorianCalendar( 2020, 03 - 1, 29 );

	public static final String version = "Version 1." + ( buildDate.get( Calendar.YEAR ) % 100 ) + "." + ( buildDate.get( Calendar.DAY_OF_YEAR ) );

	public static final String appDateRange = "2019-" + buildDate.get( Calendar.YEAR );

	
	///////////
	
	public static final String LANGUAGE = "LANGUAGE";
	
	public static final String USER_REACTION_TIME = "USER_REACTION_TIME";
	public static final String USER_RECOVER_TIME = "USER_RECOVER_TIME";
	
	
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
	
	private static Map< String, ConfigParameter > listConfig = new HashMap< String, ConfigParameter >();

	static
	{
		create_Key_Value();
	}

	private static void create_Key_Value()
	{
		listConfig.clear();

		loadDefaultProperties();
	}
	
	public static ConfigParameter getProperty( String propertyID )
	{
		ConfigParameter par = listConfig.get( propertyID );
		
		return par;
	}
	
	public static Collection< ConfigParameter > getParameters()
	{
		return listConfig.values();
	}
	
	private static void loadDefaultProperties()
	{
		try
		{
			loadDefaultLanguage();
			loadDefaultUserReactionTime();
			loadDefaultUserRecoverTime();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private static void loadDefaultLanguage() throws ConfigParameterException
	{
		Caption id = new Caption( LANGUAGE, Language.defaultLanguage, Language.defaultLanguage );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.STRING );
		par.add( Language.defaultLanguage );
		
		listConfig.put( LANGUAGE, par );
	}
	
	private static void loadDefaultUserReactionTime() throws ConfigParameterException
	{
		Caption id = new Caption( USER_REACTION_TIME, Language.defaultLanguage, "user reaction time" );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.NUMBER );
		par.add( 2D );
		
		listConfig.put( USER_REACTION_TIME, par );
	}
	
	private static void loadDefaultUserRecoverTime() throws ConfigParameterException
	{
		Caption id = new Caption( USER_RECOVER_TIME, Language.defaultLanguage, "user recover time" );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.NUMBER );
		par.add( 2D );
				
		listConfig.put( USER_RECOVER_TIME, par );
	}
}
