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
		
	private static Map< String, Caption > captions = new HashMap< String, Caption >();

	static 
	{
		captions.put( LANGUAGE, new Caption( LANGUAGE, defaultLanguage, defaultLanguage ) );

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
			idLng = localLang.toString();
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
				if ( lang.toLowerCase().equals( lc.toString().toLowerCase() ) )
				{
					language = lc;
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
			lng = localLang.toString();
		}

		return lng;
	}

	public static Map< String, Caption > getAllCaptions() 
	{
		return captions;
	}
}