/*
 * Work based on CLIS by Manuel Merino Monge <https://github.com/manmermon/CLIS>
 * 
 * Copyright 2018 by Manuel Merino Monge <manmermon@dte.us.es>
 *  
 *   This file is part of LSLRec.
 *
 *   LSLRec is free software: you can redistribute it and/or modify
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
 */

package config.language;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import config.ConfigApp;

public class Language 
{
	public static final String DefaultFolder = "./Lang/";

	public static final String defaultLanguage = "default";

	public static final String FILE_EXTENSION = "lang";

	private static Locale localLang = null;

	public static final String LANGUAGE = "LANGUAGE";
	public static final String LANGUAGE_TXT = "LANGUAGE_TXT";
	
	public static final String MUSIC_LIST = "MUSIC_LIST";
	public static final String SELECTED_SONG_LIST = "SELECTED_SONG_LIST";
	
	public static final String SONG = "SONG"; 
	public static final String PLAYER = "PLAYER";
	public static final String NAME = "NAME";
	public static final String ANONYMOUS = "ANONYMOUS"; 
	public static final String SELECT = "SELECT";
	public static final String ICON = "ICON";
	public static final String ERROR = "ERROR";
	public static final String WARNING = "WARNING";
	public static final String REMOVE = "REMOVE";
	public static final String DELETE = "DELETE";
	public static final String REMOVE_USER_MSG = "REMOVE_USER_MSG";
	public static final String SETTING = "SETTING";
	public static final String PLAY = "PLAY";
	public static final String CANCEL = "CANCEL";
	public static final String NEW = "NEW";	
	
	private static Map< String, Caption > captions = new HashMap< String, Caption >();

	static 
	{
		String esEs = Locale.forLanguageTag( "es-ES" ).toLanguageTag().toLowerCase();
		Caption cap = new Caption( LANGUAGE, defaultLanguage, defaultLanguage );
		cap.setCaption( esEs, esEs);
		captions.put( LANGUAGE, cap );
		
		cap = new Caption( LANGUAGE_TXT, defaultLanguage, "Language" );
		cap.setCaption( esEs, "Idioma");
		captions.put( LANGUAGE_TXT, cap );
		
		cap = new Caption( PLAY, defaultLanguage, "Play");
		cap.setCaption( esEs, "Jugar" );
		captions.put( PLAY, cap );
		
		cap = new Caption( SETTING, defaultLanguage, "Settings");
		cap.setCaption( esEs, "Opciones" );
		captions.put( SETTING, cap );
		
		cap = new Caption( MUSIC_LIST, defaultLanguage, "Song list");
		cap.setCaption( esEs, "Lista de canciones" );
		captions.put( MUSIC_LIST, cap );
		
		new Caption( SELECTED_SONG_LIST, defaultLanguage, "Selected songs");
		cap.setCaption( esEs, "Canciones seleccionadas" );
		captions.put( SELECTED_SONG_LIST, cap );
		
		cap = new Caption( SONG, defaultLanguage, "Song(s)" );
		cap.setCaption( esEs, "Canción(es)" );
		captions.put( SONG, cap );
		
		cap = new Caption( PLAYER, defaultLanguage, "Player" );
		cap.setCaption( esEs, "Jugador" );
		captions.put( PLAYER, cap );
		
		cap = new Caption( NAME, defaultLanguage, "Name" );
		cap.setCaption( esEs, "Nombre" );
		captions.put( NAME, cap );
		
		cap = new Caption( ANONYMOUS, defaultLanguage, "Anonymous" ) ;
		cap.setCaption( esEs, "Anónimo" );
		captions.put( ANONYMOUS, cap );
		
		cap = new Caption( SELECT, defaultLanguage, "Select" );
		cap.setCaption( esEs, "Seleccionar");
		captions.put( SELECT, cap );
				
		cap = new Caption( ICON, defaultLanguage, "Icon" );
		cap.setCaption( esEs, "Icono" );
		captions.put( ICON, cap );
		
		cap = new Caption( ERROR, defaultLanguage, "Error" );
		cap.setCaption( esEs, "Error" );
		captions.put( ERROR, cap );
		
		cap = new Caption( REMOVE, defaultLanguage, "Remove" );
		cap.setCaption( esEs, "Eliminar" );
		captions.put( REMOVE, cap );
		
		cap = new Caption( DELETE, defaultLanguage, "Delete" );
		cap.setCaption( esEs, "Eliminar" );
		captions.put( DELETE, cap );
		
		cap = new Caption( REMOVE_USER_MSG, defaultLanguage, "All player data will be remove. Continue?" );
		cap.setCaption( esEs, "Se eliminarán todos los datos del jugador. ¿Desea continuar?" );
		captions.put( REMOVE_USER_MSG, cap );
		
		cap = new Caption( WARNING, defaultLanguage, "Warning" );
		cap.setCaption( esEs, "Cuidado" );
		captions.put( WARNING, cap );
		
		cap = new Caption( CANCEL, defaultLanguage, "Cancel" );
		cap.setCaption( esEs, "Cancelar" );
		captions.put( CANCEL, cap );
		
		cap = new Caption( NEW, defaultLanguage, "New" );
		cap.setCaption( esEs, "Nuevo" );
		captions.put( NEW, cap );
	}

	public static void loadLanguages() 
	{
		try 
		{
			File folder = new File( DefaultFolder );
			if (folder.exists() && folder.isDirectory()) 
			{
				FileFilter filter = new FileFilter() 
				{
					@Override
					public boolean accept(File pathname) 
					{
						boolean ok = pathname.exists() && pathname.isFile()
								&& pathname.getAbsolutePath().endsWith( FILE_EXTENSION );

						return ok;
					}
				};

				File[] langFiles = folder.listFiles( filter );

				for (File lang : langFiles) 
				{
					loadLanguageFile( lang );
				}
			}
		}
		catch (Exception ex) 
		{
		}
	}

	private static void loadLanguageFile( File f ) throws Exception 
	{
		Properties prop = new Properties();
		FileInputStream propFileIn = null;

		try 
		{
			propFileIn = new FileInputStream(f);

			prop.load( new InputStreamReader( propFileIn, Charset.forName("UTF-8" ) ) );
			
			Object idLang = prop.get( Language.LANGUAGE );

			if ( idLang != null ) 
			{
				prop.remove( Language.LANGUAGE );

				Caption cap = captions.get( Language.LANGUAGE );

				cap.setCaption( idLang.toString(), idLang.toString() );

				for ( Object key : prop.keySet() ) 
				{
					Object val = prop.get(key);
					if ( val != null && !val.toString().trim().isEmpty() ) 
					{
						Caption caption = captions.get( key );
						
						if ( caption != null )
						{	
							caption.setCaption(idLang.toString(), val.toString());
						}
					}
				}
			}
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	}

	public static String getCaption( String captionID, String lang ) 
	{
		String txt = "";

		Caption cap = captions.get( captionID );

		if (cap != null) 
		{
			txt = cap.getCaption( lang.toLowerCase() );

			if (txt == null ) 
			{
				txt = cap.getCaption( defaultLanguage );
			}
		}

		return txt;
	}

	public static void setDefaultLocalLanguage() 
	{
		Locale lc = Locale.getDefault();
		changeLanguage( lc.toString() );
	}

	public static String getLocalCaption( String captionID ) 
	{
		String idLng = defaultLanguage;
		if ( localLang != null ) 
		{
			idLng = localLang.toLanguageTag().toLowerCase();
		}

		return getCaption( captionID, idLng );
	}

	public static List<String> getAvaibleLanguages() 
	{
		List<String> LANGS = new ArrayList<String>();

		Caption lang = captions.get( LANGUAGE );
		LANGS.addAll( lang.getLanguages() );
		Collections.sort( LANGS );

		return LANGS;
	}

	public static boolean changeLanguage( String lang )
	{
		Locale language = null;

		if ( getAvaibleLanguages().contains( lang.toLowerCase() ) ) 
		{
			for ( Locale lc : Locale.getAvailableLocales() )
			{
				if ( lang.toLowerCase().equals( lc.toLanguageTag().toLowerCase() ) )
				{
					language = lc;
					break;
				}
			}

			if ( language != null )
			{
				localLang = language;
			}
			else if ( lang.equals( defaultLanguage ) )
			{
				localLang = null;
			}
		}

		return language != null;
	}

	public static String getCurrentLanguage() 
	{
		String lng = defaultLanguage;

		if ( localLang != null ) 
		{
			lng = localLang.toLanguageTag().toLowerCase();
		}

		return lng;
	}

	public static Map< String, Caption > getAllCaptions() 
	{
		return captions;
	}
}