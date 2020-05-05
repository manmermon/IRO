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
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.DoubleBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import config.ConfigParameter.ParameterType;
import config.language.Caption;
import config.language.Language;
import control.controller.ControllerMetadata;
import exceptions.ConfigParameterException;
import general.ConvertTo;
import general.NumberRange;
import general.Tuple;
import image.basicPainter2D;
import image.icon.GeneralAppIcon;
import statistic.GameStatistic;
import statistic.GameStatistic.FieldType;

public class ConfigApp 
{
	public static final String fullNameApp = "Interactive Rehab Orchestra";
	public static final String shortNameApp = "IRO";
	public static final Calendar buildDate = new GregorianCalendar( 2020, 03 - 1, 29 );

	public static final String version = "Version 1." + ( buildDate.get( Calendar.YEAR ) % 100 ) + "." + ( buildDate.get( Calendar.DAY_OF_YEAR ) );

	public static final String appDateRange = "2019-" + buildDate.get( Calendar.YEAR );

	private static final String DB_PATH = "./user/db/data.db";
	
	public static final String SONG_FILE_PATH = "./sheets/";
	public static final String BACKGROUND_SPRITE_FILE_PATH = "./resources/background/";
	public static final String NOTE_SPRITE_FILE_PATH = "./resources/note/";
	
	public static final Tuple< Integer, Integer > playerPicSize = new Tuple<Integer, Integer>( 100, 100 );
	
	public static final Tuple< Integer, Integer > playerPicSizeIcon = new Tuple<Integer, Integer>( 48, 48 );
	
	public static final String SONG_LIST_SEPARATOR = ";";

	public static final String SELECTED_CONTROLLER = "SELECTED_CONTROLLER";
	
	public static final String LANGUAGE = "LANGUAGE";
	
	public static final String PLAYER = "PLAYER";
	
	public static final String MULTIPLAYER = "MULTIPLAYER";
	
	public static final int SINGLE_PLAYER = 0;
	public static final int LOCA_MULTIPLAYER = 1;
	public static final int REMOTE_MULTIPLAYER = 2;
	
	///////////
	//
	// Data base
	//
	///////////
	private static final String prefixComm = "jdbc:sqlite:";
	
	private static final String userTableName = "user";
	private static final String settingsTableName = "settings";
	private static final String sessionTableName = "session";
	private static final String statisticTableName = "statistic";
	
	private static final String dbURL = prefixComm + DB_PATH;
	
	private static Map< String, Map< String, Class > > tableFields;	
	
	private static Connection conn;
	
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
		dbLoadFields();
	}

	private static void create_Key_Value()
	{
		listGeneralConfig.clear();
		listPlayerConfig.clear();

		resetSettings();
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
				List< Tuple< String, Object > > settings = dbGetPlayerSetting( player.getId() );
			
				load = ( !settings.isEmpty() );
				
				if( load )
				{
					Settings cfg = getDefaultSettings();
					cfg.setPlayer( player );
					listPlayerConfig.put( player, cfg );

					for( Tuple< String, Object > par : settings )
					{
						Object val = par.y;

						ConfigParameter p = cfg.getParameter( par.x );
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
	
	public static void resetSettings()
	{
		try
		{
			listPlayerConfig.clear();
			listGeneralConfig.clear();
			
			loadDefaultLanguage( );
			
			if( isTesting() )
			{			
				loadDefaultPlayerSetting( new Player() );
			}
			
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
	
	private static Settings getDefaultSettings( ) throws ConfigParameterException
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
		
		par = loadDefaultBackgroundImage();
		cfg.setParameter( par.get_ID().getID(), par );
		
		par = loadDefaultNoteImage();
		cfg.setParameter( par.get_ID().getID(), par );
		
		for( ConfigParameter p : loadDefaultActionColors() )
		{
			cfg.setParameter( p.get_ID().getID(), p );
		}
		
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
		par.setPriority( 0 );
		
		listGeneralConfig.put( LANGUAGE, par );
	}
	
	private static ConfigParameter loadDefaultBackgroundImage() throws ConfigParameterException
	{	
		Caption id = getCaptions( Language.BACKGROUND );
		id.setID( BACKGROUND_IMAGE );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.OTHER );
		par.setPriority(  Integer.MAX_VALUE );
		
		return par;
	}
	
	private static ConfigParameter loadDefaultNoteImage() throws ConfigParameterException
	{
		Caption id = getCaptions( Language.NOTE );
		id.setID( NOTE_IMAGE );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.OTHER );
		par.setPriority(  Integer.MAX_VALUE );

		return par;		
	}
	
	private static ConfigParameter loadDefaultSelectedController() throws ConfigParameterException
	{
		Caption id = getCaptions( Language.CONTROLLER );
		id.setID( SELECTED_CONTROLLER );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.OTHER );
		par.setPriority(  Integer.MAX_VALUE );

		return par; 
	}
	
	private static ConfigParameter loadDefaultUserReactionTime( ) throws ConfigParameterException
	{		
		Caption id = getCaptions( Language.REACTION_TIME );
		id.setID( REACTION_TIME );
		NumberRange rng = new NumberRange( 0.5, Double.MAX_VALUE );
		ConfigParameter par = new ConfigParameter( id, rng );
		par.setSelectedValue( 2D );
		par.setPriority( 2 );
		
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
		NumberRange rng = new NumberRange( 0.5, Double.MAX_VALUE );
		ConfigParameter par = new ConfigParameter( id, rng );
		par.setSelectedValue( 2D );
		par.setPriority( 3 );
				
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
		par.setPriority( 7 );
		pars.add( par );

		id = getCaptions( Language.WAITING_ACTION_COLOR);
		id.setID( WAITING_ACTION_COLOR );
		par = new ConfigParameter( id, ConfigParameter.ParameterType.COLOR );
		colors.remove( Color.blue );
		colors.add( 0, Color.blue );
		par.addAllOptions( colors );
		par.setSelectedValue( Color.blue );
		par.setPriority( 8 );
		pars.add( par );

		id = getCaptions( Language.ACTION_COLOR );
		id.setID( ACTION_COLOR );
		par = new ConfigParameter( id, ConfigParameter.ParameterType.COLOR );
		colors.remove( Color.green );
		colors.add( 0, Color.green );
		par.addAllOptions( colors );
		par.setSelectedValue( Color.green );
		par.setPriority( 9 );
		pars.add( par );
		
		return pars;
	}
	
	private static ConfigParameter loadDefaultSongList( ) throws ConfigParameterException
	{	
		Caption id = getCaptions( Language.MUSIC_LIST );
		id.setID( SONG_LIST );

		ConfigParameter par = new ConfigParameter( id, ParameterType.SONG );
		par.setPriority( Integer.MAX_VALUE - 1 );

		return par;
	}
	
	private static ConfigParameter loadDefaultSelectedChannel() throws ConfigParameterException
	{			
		Caption id = getCaptions( Language.SELECTED_CHANNEL );
		id.setID( INPUT_SELECTED_CHANNEL );

		NumberRange r = new NumberRange( 1D, Double.MAX_VALUE);

		ConfigParameter par = new ConfigParameter( id, r );
		par.setSelectedValue( 1D );
		par.setPriority( 1 );

		return par;
	}
	
	private static List< ConfigParameter > loadDefaultInputRange() throws ConfigParameterException
	{	
		List< ConfigParameter > pars = new ArrayList<ConfigParameter>();
		
		Caption idMin = getCaptions( Language.MIN_INPUT_VALUE );
		idMin.setID( INPUT_MIN_VALUE );
		Caption idMax = getCaptions( Language.MAX_INPUT_VALUE );
		idMax.setID( INPUT_MAX_VALUE );

		ConfigParameter parMin = new ConfigParameter( idMin, ParameterType.NUMBER );
		parMin.setSelectedValue(  -0.7D );
		parMin.setPriority( 4 );

		ConfigParameter parMax = new ConfigParameter( idMax, ParameterType.NUMBER );
		parMax.setSelectedValue( 00D );
		parMax.setPriority( 5 );

		pars.add( parMin );
		pars.add( parMax );
		
		return pars;
	}
		
	///////////////////
	/*
	
	DATA BASE
	
	*/
	///////////////////
	
	private static void dbLoadFields()
	{
		tableFields = new HashMap<String, Map< String, Class > >();
		
		Map< String, Class > fieldType = new HashMap<String, Class>();
		tableFields.put( userTableName, fieldType );
		
		fieldType.put( "id", Integer.class );
		fieldType.put( "name", String.class );
		fieldType.put( "image", Image.class );
				
		fieldType = new HashMap<String, Class>();
		tableFields.put( settingsTableName, fieldType );
		
		fieldType.put( "userID", Integer.class );
		fieldType.put( REACTION_TIME, Double.class );
		fieldType.put( RECOVER_TIME, Double.class );
		fieldType.put( PREACTION_COLOR, Integer.class );
		fieldType.put( WAITING_ACTION_COLOR, Integer.class );
		fieldType.put( ACTION_COLOR, Integer.class );
		fieldType.put( SONG_LIST, String.class );
		fieldType.put( INPUT_MIN_VALUE, Double.class );
		fieldType.put( INPUT_MAX_VALUE, Double.class );
		fieldType.put( INPUT_SELECTED_CHANNEL, Double.class );
		fieldType.put( BACKGROUND_IMAGE, String.class );
		fieldType.put( NOTE_IMAGE, String.class );
		fieldType.put( TIME_IN_INPUT_TARGET, Double.class );

		fieldType = new HashMap<String, Class>();
		fieldType.put( "idSession", Integer.class );
		fieldType.put( "userID", Integer.class );
		//fieldType.put( "date", Integer.class );
		fieldType.put( "controllerName", String.class );
		fieldType.put( "controllerSamplingRate", Double.class );
		fieldType.put( "controllerMinValueTarget", Double.class );
		fieldType.put( "controllerMaxValueTarget", Double.class );
		fieldType.put( "controllerTimeTarget", Double.class );
		fieldType.put( "controllerData", DoubleBuffer.class );
		tableFields.put( sessionTableName, fieldType );
		
		fieldType = new HashMap<String, Class>();
		//fieldType.put( "number", Integer.class );
		//fieldType.put( "idSession", Integer.class );
		fieldType.put( "actionID", Integer.class );
		fieldType.put( "actionName", String.class );
		fieldType.put( "userID", Integer.class );
		fieldType.put( "time", Integer.class );	
		tableFields.put( statisticTableName, fieldType );
	}
	
	/**
	 * Connect to a sample database
	 * @throws SQLException 
	 */
	private static void dbConnect() throws SQLException 
	{
		if( conn == null || !conn.isClosed() )
		{
			conn = DriverManager.getConnection( dbURL );
		}
	}

	private static void dbCloseConnection() throws SQLException
	{
		if( conn != null )
		{
			conn.close();
			conn = null;
		}
	}

	public static int dbAddPlayer( String name, BufferedImage img ) throws SQLException
	{
		String vars = "name";
		String vals = "?";

		int userID = Player.ANONYMOUS;

		if( img != null )
		{
			vars += ",image";
			vals += ",?";
		}

		String sql = "INSERT INTO " + userTableName +  "(" + vars + ") VALUES(" + vals + ")";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try  
		{
			dbConnect();
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );

			pstmt.setString(1, name);

			if( img != null )
			{
				byte [] imageBytes = null;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try
				{
					ImageIO.write( img, "png", bos );
					imageBytes = bos.toByteArray();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				pstmt.setBytes( 2, imageBytes );

			}

			if( pstmt.executeUpdate() > 0 )
			{
				rs = pstmt.getGeneratedKeys();
				if(rs != null && rs.next())
				{
					userID = rs.getInt( 1 );
				}
			}
		}
		catch (SQLException e) 
		{
			throw e;
		}
		finally 
		{
			if( rs != null )
			{
				rs.close();
			}

			if( pstmt != null )
			{
				pstmt.close();
			}

			dbCloseConnection();        	
		}

		return userID;
	}

	public static void dbUpdatePlayer( Player user ) throws SQLException
	{
		if( user != null )
		{
			int id = user.getId();
			String name = user.getName();
			BufferedImage img = (BufferedImage)user.getImg().getImage();

			String sql =  "UPDATE " + userTableName + " SET ";
			sql += "name = ?, image = ? WHERE id = " + id;

			PreparedStatement pstmt = null;
			try  
			{
				dbConnect();
				pstmt = conn.prepareStatement( sql );

				pstmt.setString(1, name);

				if( img != null )
				{
					byte [] imageBytes = null;
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					try
					{
						BufferedImage scaleImg = basicPainter2D.copyImage( img.getScaledInstance( ConfigApp.playerPicSize.x, ConfigApp.playerPicSize.y, BufferedImage.SCALE_SMOOTH ) );
						ImageIO.write( scaleImg, "png", bos );
						imageBytes = bos.toByteArray();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					pstmt.setBytes( 2, imageBytes );
				}

				pstmt.executeUpdate();
			}
			catch (SQLException e) 
			{
				throw e;
			}
			finally 
			{
				if( pstmt != null )
				{
					pstmt.close();
				}

				dbCloseConnection();        	
			}	            
		}
	}

	public static void dbRemovePlayer( int id ) throws SQLException
	{	
		String sql = "DELETE FROM " + userTableName + " WHERE id = ?";
		PreparedStatement pstmt = null;

		try
		{
			dbConnect();
			pstmt= conn.prepareStatement( sql );

			pstmt.setInt(1, id);

			pstmt.executeUpdate();
		}
		catch (SQLException e) 
		{
			throw e;
		}
		finally 
		{
			if( pstmt != null )
			{
				pstmt.close();
			}

			dbCloseConnection();       
		}
	}

	private static List< Tuple< String, Object > > dbGetPlayerSetting( int userID ) throws SQLException
	{
		String sql = "SELECT * FROM " + settingsTableName + " WHERE userID = " + userID;

		Statement stmt = null;
		ResultSet rs = null;

		List< Tuple< String, Object > > pars = new ArrayList< Tuple< String, Object > >();

		try
		{ 
			dbConnect();

			stmt  = conn.createStatement();
			rs    = stmt.executeQuery( sql );

			Map< String, Class > tableFieldType = tableFields.get( settingsTableName );

			if ( rs.next() ) 
			{           	
				for( String fieldName : tableFieldType.keySet() )
				{
					Class type = tableFieldType.get( fieldName );

					Object val = null;

					if( type.isAssignableFrom( Integer.class ) )
					{
						val = rs.getInt( fieldName );
					}
					else if( type.isAssignableFrom( Double.class ) )
					{
						val = rs.getDouble( fieldName );
					}
					/*
	            		else if( type.isAssignableFrom( Image.class ) )
	            		{
	            			InputStream input = rs.getBinaryStream( "image" );

	                    	BufferedImage img = null;

	                    	if( input != null)
	                    	{
	                    		img = ImageIO.read( input );
	                    	}

	                    	val = img;
	            		}
					 */
					else if( type.isAssignableFrom( String.class ) )
					{
						val = rs.getString( fieldName );
					}

					Tuple< String, Object > par = new Tuple<String, Object>( fieldName, val );
					pars.add( par );
				}            	
			}
		}
		catch ( SQLException e)
		{
			throw e;
		}
		finally
		{
			if( rs != null )				
			{
				rs.close();
			}

			if( stmt != null )
			{
				stmt.close();
			}

			dbCloseConnection();
		}		

		return pars;
	}

	public static void dbInsertPlayerSetting( Player player ) throws SQLException
	{		
		String sql = "INSERT INTO "+ settingsTableName +  "(userID";
		String sqlValues  = "VALUES(" + player.getId();

		Map< String, Class > fieldTable = tableFields.get( settingsTableName );
		Iterator< String > itField = fieldTable.keySet().iterator();

		Settings cfg = ConfigApp.getPlayerSetting( player );
		
		if( cfg != null )
		{		
			while( itField.hasNext() )
			{			
				String fieldName = itField.next();
	
				String value = null;
				ConfigParameter par = cfg.getParameter( fieldName );
	
				if( par != null )
				{
					Object val = par.getSelectedValue();
					if( val != null )
					{
						value = val.toString();
					}
	
					if( par.get_type() == ConfigParameter.ParameterType.COLOR )
					{
						value = ((Color)val).getRGB() + "";
					}
					else if( par.get_type() == ParameterType.USER )
					{
						value = null;
					}
	
					if( value != null )
					{			
						sql += "," + fieldName;
						sqlValues += "," + value;
					}
				}
			}
	
			sql += ") " + sqlValues + ")";
	
			Statement stmt = null;
	
			try 
			{
				dbConnect();
	
				stmt  = conn.createStatement();
				stmt.executeUpdate( sql );
			}
			catch (SQLException e) 
			{
				throw e;			 
			}
			finally 
			{
				if( stmt != null )
				{
					stmt.close();
				}
	
				dbCloseConnection();
			}
		}
	}

	public static void dbUpdatePlayerSetting( Player player ) throws SQLException
	{
		String sql =  "UPDATE " + settingsTableName + " SET ";

		boolean add = false;
		Map< String, Class > fields = tableFields.get( settingsTableName );
		for( String fieldName : fields.keySet() )
		{			
			String value = dbGetFieldValue( player, fieldName );			

			if( value != null )
			{			
				add = true;
				sql += fieldName + " = " + value + " ,";				
			}
		}

		if( add )
		{		
			sql = sql.substring( 0, sql.length() - 1 );
			sql += " WHERE userID = " + player.getId();

			Statement stmt = null;

			try 
			{
				dbConnect();

				stmt  = conn.createStatement();
				stmt.executeUpdate(sql );
			}
			catch (SQLException e) 
			{
				throw e;			 
			}
			finally 
			{
				if( stmt != null )
				{
					stmt.close();
				}

				dbCloseConnection();
			}
		}
	}

	private static String dbGetFieldValue( Player player, String fieldName )
	{
		String value = null;

		Settings cfg = ConfigApp.getPlayerSetting( player );
		
		if( cfg != null )
		{
			ConfigParameter par = cfg.getParameter( fieldName );
	
			if( par != null )
			{
				Object val = par.getSelectedValue();
				if( val != null )
				{
					value = val.toString();
				}
		
				if( par.get_type() == ConfigParameter.ParameterType.COLOR )
				{
					value = ((Color)val).getRGB() + "";
				}
				else if( par.get_type() == ParameterType.USER )
				{
					value = null;
				}
				else if( par.get_type() == ParameterType.STRING && value != null )
				{
					value = "\"" + value + "\"";
				}
				else if( par.get_type() == ParameterType.SONG && value != null )
				{
					value = "\"" + value + "\"";
				}
				else if( par.get_type() == ParameterType.OTHER && value != null )
				{
					if( value instanceof String )
					{
						value = "\"" + value + "\"";
					}
				}
			}
		}

		return value;
	}

	public static void dbUpdatePlayerSetting( Player player, String fieldID ) throws SQLException
	{
		Map< String, Class > fields = tableFields.get( settingsTableName );

		boolean add = fields.containsKey( fieldID );

		String sql =  "UPDATE " + settingsTableName + " SET ";

		if( add )
		{	
			String value = dbGetFieldValue( player, fieldID );
			
			add = (value != null );

			if( add )
			{			
				add = true;
				sql += fieldID + " = " + value;				
			}			
		}

		if( add )
		{		
			sql += " WHERE userID = " + player.getId();

			Statement stmt = null;

			try 
			{
				dbConnect();

				stmt  = conn.createStatement();
				stmt.executeUpdate(sql );
			}
			catch (SQLException e) 
			{
				throw e;			 
			}
			finally 
			{
				if( stmt != null )
				{
					stmt.close();
				}

				dbCloseConnection();
			}
		}
	}

	public static Player dbGetPlayer( int ID ) throws SQLException, IOException
	{
		String sql = "SELECT * FROM " + userTableName + " WHERE id = " + ID;

		Statement stmt = null;
		ResultSet rs = null;

		Player user = null;

		try
		{ 
			dbConnect();

			stmt  = conn.createStatement();
			rs    = stmt.executeQuery( sql );

			if ( rs.next() ) 
			{	
				int id = rs.getInt( "id" );
				String name =  rs.getString( "name" );

				InputStream input = rs.getBinaryStream( "image" );

				BufferedImage img = null;

				if( input != null)
				{
					img = ImageIO.read( input );
				}

				if( img == null )
				{
					img = (BufferedImage)GeneralAppIcon.getDoll( 64, 64, Color.BLACK, Color.WHITE, null ).getImage();
				}

				user = new Player( id, name, new ImageIcon( img ) );
			}
		} 
		catch (SQLException e) 
		{
			throw e;
		} 
		finally 
		{
			if( rs != null )
			{
				rs.close();
			}

			if( stmt != null )
			{
				stmt.close();
			}

			dbCloseConnection();
		}

		return user;
	}

	public static List< Player > dbGetAllPlayers() throws SQLException, IOException
	{
		String sql = "SELECT * FROM " + userTableName;

		Statement stmt = null;
		ResultSet rs = null;

		List< Player > users = new ArrayList<Player>();

		try
		{ 
			dbConnect();

			stmt  = conn.createStatement();
			rs    = stmt.executeQuery( sql );

			while ( rs.next() ) 
			{
				int id = rs.getInt( "id" );
				String name =  rs.getString( "name" );

				InputStream input = rs.getBinaryStream( "image" );

				BufferedImage img = null;
				ImageIcon icon = null;

				if( input != null)
				{
					img = ImageIO.read( input );
				}

				if( img != null )
				{
					icon = new ImageIcon( img ); 
				}

				Player user = new Player( id, name, icon );

				users.add( user );
			}
		} 
		catch (SQLException | IOException e) 
		{
			throw e;
		} 
		finally 
		{
			if( rs != null )
			{
				rs.close();
			}

			if( stmt != null )
			{
				stmt.close();
			}

			dbCloseConnection();
		}

		return users;
	}

	public static void dbSaveStatistic() throws SQLException, IOException
	{
		LocalDateTime startTime = GameStatistic.getStartDateTime();
		
		if( startTime != null )
		{
			ZonedDateTime zdt = ZonedDateTime.of( startTime, ZoneId.systemDefault() );
			
			long sessionID = zdt.toInstant().toEpochMilli();
			
			for( int playerID : GameStatistic.getPlayerIDs() )
			{	
				List< Tuple< LocalDateTime, GameStatistic.FieldType > > register = GameStatistic.getRegister( playerID );
			
				if( playerID != Player.ANONYMOUS && !register.isEmpty() )
				{
					ControllerMetadata cmeta = GameStatistic.getControllerSetting( playerID );
					LinkedList< Double[] > cData = GameStatistic.getControllerData( playerID );
					//TODO

					String sql = "INSERT INTO "+ sessionTableName  + " ("  
							+ " idSession"
							+ ", userID"
							+ ", controllerName"
							+ ", controllerSamplingRate"
							+ ", controllerMinValueTarget"
							+ ", controllerMaxValueTarget"
							+ ", controllerTimeTarget"
							+ ", controllerData) ";

					
					sql += "VALUES(" + sessionID + "," + playerID + ",\"" + cmeta.getName() + "\""
										+ "," + cmeta.getSamplingRate() + "," + cmeta.getRecoverInputLevel()
										+ "," + cmeta.getActionInputLevel().getMin() + "," + cmeta.getTargetTimeInLevelAction()
										+ ", ?) ";

					
					PreparedStatement pstmt = null;
					Statement stmt = null;					

					try 
					{
						dbConnect();

						pstmt = conn.prepareStatement( sql );
						
						byte[] data = null;
						if( !cData.isEmpty() )
						{
							data = readControllerData( cData );
						}
						pstmt.setBytes( 1, data );

						pstmt.executeUpdate( );

						stmt = conn.createStatement();

						sql = "INSERT INTO "+ statisticTableName +  " (idSession,userID,actionID,actionName,time) VALUES ";
						
						for( Tuple< LocalDateTime, FieldType > t : register )
						{
							LocalDateTime time = t.x;
							FieldType f = t.y;

							zdt = ZonedDateTime.of( time, ZoneId.systemDefault() );							
							sql += "(" + sessionID 
											+ "," + playerID
											+ "," + f.ordinal()
											+ ",\"" + f.name() + "\""
											+ "," + zdt.toInstant().toEpochMilli()
											+ ")\n ,";							
						}

						sql = sql.substring( 0, sql.length() - 1 );
						
						stmt.executeUpdate( sql );
						
						GameStatistic.clearRegister();
					}
					catch (SQLException | IOException e) 
					{
						throw e;			 
					}
					finally 
					{
						if( pstmt != null )
						{
							pstmt.close();
						}
						
						if( stmt != null )
						{
							stmt.close();
						}

						dbCloseConnection();
					}
				}
			}
		}
	}
	
	private static byte[] readControllerData( LinkedList< Double[] > data ) throws IOException 
	{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        for( Double[] d : data ) 
        {	
        	bos.write( ConvertTo.DoubleArray2byteArray( d ) );
        }
        
        return bos.toByteArray();
    }
	
	/*
	public static void main( String[] arg )
	{
		try
		{
			dbCreateTables();
			dbAddPlayer( "Manuel M.", null );
			dbAddPlayer( "Luis V.", (BufferedImage)GeneralAppIcon.getSmileIcon( 9, 64, Color.BLACK, Color.RED ).getImage() );
			dbAddPlayer( "MM", (BufferedImage)GeneralAppIcon.getSmileIcon( 9, 64, Color.BLACK, Color.YELLOW ).getImage() );



		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//*/	
	

	private static void dbCreateTables() throws SQLException
	{	
		String sqlCreateTableUser = 
				"CREATE TABLE IF NOT EXISTS user ("
						+ " id integer PRIMARY KEY AUTOINCREMENT"
						+ ", name text NOT NULL"
						+ ", image BLOB"
						+ ");";

		String sqlCreateTableConfig = 
				"CREATE TABLE IF NOT EXISTS settings ("
				//+ " id integer PRIMARY KEY AUTOINCREMENT"
				+ " userID integer PRIMARY KEY "
				+ ", reactionTime real CHECK (reactionTime > 0)"
				+ ", recoverTime real CHECK (recoverTime > 0)"
				+ ", colorPreaction integer"
				+ ", colorWaitingAction integer"
				+ ", colorAction integer"
				+ ", songs text"
				+ ", minInputValue real"
				+ ", maxInputValue real"
				+ ", timeInInputTarget real"
				+ ", selectedChannel real"
				+ ", backgroundImage text"
				+ ", noteImage text"
				+ ", FOREIGN KEY (userID) REFERENCES user(id) ON DELETE CASCADE\n"
				+ ");";

		String sqlCreateTableSession = 
				"CREATE TABLE IF NOT EXISTS session ("
						+ " idSession integer NOT NULL" // Session date
						+ ", userID integer NOT NULL"
						+ ", controllerName text NOT NULL"
						+ ", controllerSamplingRate real NOT NULL"
						+ ", controllerMinValueTarget real NOT NULL"
						+ ", controllerMaxValueTarget real NOT NULL"
						+ ", controllerTimeTarget real NOT NULL"
						+ ", controllerData BLOB"
						//+ ", date integer NOT NULL"
						+ ", PRIMARY KEY (idSession, userID)"
						+ ", FOREIGN KEY (userID) REFERENCES user(id) ON DELETE CASCADE\n"
						+ ");";

		String sqlCreateTableStatistic = 
				"CREATE TABLE IF NOT EXISTS statistic ("
						+ " number integer PRIMARY KEY AUTOINCREMENT"
						+ ", idSession integer NOT NULL"
						+ ", userID integer NOT NULL"
						+ ", actionID integer NOT NULL"
						+ ", actionName integer NOT NULL"
						+ ", time integer NOT NULL"						
						+ ", FOREIGN KEY ( idSession ) REFERENCES statistic( idSession ) ON DELETE CASCADE"
						+ ");";
		
		try ( Connection conn = DriverManager.getConnection( dbURL );
				Statement stmt = conn.createStatement()) 
		{
			stmt.execute( sqlCreateTableUser );
			stmt.execute( sqlCreateTableConfig );
			stmt.execute( sqlCreateTableSession );
			stmt.execute( sqlCreateTableStatistic );
		} 
		catch ( SQLException e ) 
		{
			throw e;
		}
	}


}



