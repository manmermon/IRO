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
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import config.ConfigParameter.ParameterType;
import config.language.Caption;
import config.language.Language;
import exceptions.ConfigParameterException;
import general.ArrayTreeMap;
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
		loadDBFields();
	}

	private static void create_Key_Value()
	{
		listUserConfig.clear();

		loadDefaultProperties();
	}
	
	public static ConfigParameter getParameter( String propertyID )
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
			loadDefaultInputRange( );
			
			loadDefaultTimeInInputTarget();
			
			loadDefaultSelectedChannel();
			
			loadDefaultSelectedController();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private static void loadDefaultSelectedController()
	{
		try
		{
			Caption id = Language.getAllCaptions().get( Language.CONTROLLER );
			id.setID( SELECTED_CONTROLLER );
			ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.OTHER );
			par.setPriority(  Integer.MAX_VALUE );
			
			listUserConfig.put( SELECTED_CONTROLLER, par ); 
		} 
		catch (ConfigParameterException ex)
		{
			ex.printStackTrace();
		}
	}
	
	private static void loadDefaultUser( ) throws ConfigParameterException
	{
		Caption id = new Caption( PLAYER, Language.defaultLanguage, Language.getCaption( Language.defaultLanguage, Language.PLAYER ) );
		ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.USER );		
		par.setSelectedValue( new Player() );		
		par.setPriority( Integer.MAX_VALUE );
		
		listUserConfig.put( PLAYER, par );
	}
	
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
		
		listUserConfig.put( LANGUAGE, par );
	}
	
	private static void loadDefaultUserReactionTime( ) throws ConfigParameterException
	{
		Caption id = getCaptions( Language.REACTION_TIME );
		id.setID( REACTION_TIME );
		NumberRange rng = new NumberRange( 0.5, Double.MAX_VALUE );
		ConfigParameter par = new ConfigParameter( id, rng );
		par.setSelectedValue( 2D );
		par.setPriority( 2 );
		
		
		listUserConfig.put( REACTION_TIME, par );
	}
	
	private static void loadDefaultTimeInInputTarget( ) throws ConfigParameterException
	{
		Caption id = getCaptions( Language.TIME_INPUT_TARGET );
		id.setID( TIME_IN_INPUT_TARGET );
		NumberRange rng = new NumberRange( 0, Double.MAX_VALUE );
		ConfigParameter par = new ConfigParameter( id, rng );
		par.setSelectedValue( 0D );
		par.setPriority( 6 );
		
		listUserConfig.put( TIME_IN_INPUT_TARGET, par );
	}
	
	private static void loadDefaultUserRecoverTime( ) throws ConfigParameterException
	{
		Caption id = getCaptions( Language.RECOVER_TIME );
		id.setID( RECOVER_TIME );
		NumberRange rng = new NumberRange( 0.5, Double.MAX_VALUE );
		ConfigParameter par = new ConfigParameter( id, rng );
		par.setSelectedValue( 2D );
		par.setPriority( 3 );
				
		listUserConfig.put( RECOVER_TIME, par );
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
			
			Caption id = getCaptions( Language.PREACTION_COLOR );
			id.setID( PREACTION_COLOR );
			ConfigParameter par = new ConfigParameter( id, ConfigParameter.ParameterType.COLOR );
			par.addAllOptions( colors );
			par.setSelectedValue( Color.red );
			par.setPriority( 7 );
			listUserConfig.put( PREACTION_COLOR, par );
			
			id = getCaptions( Language.WAITING_ACTION_COLOR);
			id.setID( WAITING_ACTION_COLOR );
			par = new ConfigParameter( id, ConfigParameter.ParameterType.COLOR );
			colors.remove( Color.blue );
			colors.add( 0, Color.blue );
			par.addAllOptions( colors );
			par.setSelectedValue( Color.blue );
			par.setPriority( 8 );
			listUserConfig.put( WAITING_ACTION_COLOR, par );
			
			id = getCaptions( Language.ACTION_COLOR );
			id.setID( ACTION_COLOR );
			par = new ConfigParameter( id, ConfigParameter.ParameterType.COLOR );
			colors.remove( Color.green );
			colors.add( 0, Color.green );
			par.addAllOptions( colors );
			par.setSelectedValue( Color.green );
			par.setPriority( 9 );
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
			Caption id = getCaptions( Language.MUSIC_LIST );
			id.setID( SONG_LIST );
			
			ConfigParameter par = new ConfigParameter( id, ParameterType.SONG );
			par.setPriority( Integer.MAX_VALUE - 1 );
			
			listUserConfig.put( SONG_LIST, par );
		}
		catch ( ConfigParameterException e) 
		{
			e.printStackTrace();
		}
	}
	
	private static void loadDefaultSelectedChannel()
	{
		try
		{	
			Caption id = getCaptions( Language.SELECTED_CHANNEL );
			id.setID( INPUT_SELECTED_CHANNEL );
						
			NumberRange r = new NumberRange( 1D, Double.MAX_VALUE);
			
			ConfigParameter par = new ConfigParameter( id, r );
			par.setSelectedValue( 1D );
			par.setPriority( 1 );
			
			listUserConfig.put( INPUT_SELECTED_CHANNEL, par );
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	}
	
	private static void loadDefaultInputRange()
	{
		try
		{	
			Caption idMin = getCaptions( Language.MIN_INPUT_VALUE );
			idMin.setID( INPUT_MIN_VALUE );
			Caption idMax = getCaptions( Language.MAX_INPUT_VALUE );
			idMax.setID( INPUT_MAX_VALUE );
			
			ConfigParameter parMin = new ConfigParameter( idMin, ParameterType.NUMBER );
			parMin.setSelectedValue(  0D );
			parMin.setPriority( 4 );
			
			ConfigParameter parMax = new ConfigParameter( idMax, ParameterType.NUMBER );
			parMax.setSelectedValue( 100D );
			parMax.setPriority( 5 );
			
			listUserConfig.put( INPUT_MIN_VALUE, parMin );
			listUserConfig.put( INPUT_MAX_VALUE, parMax );
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	}
	
	/*
	private static void loadDefaultSelectedInputChannel()
	{
		try
		{
			Caption id = Language.getAllCaptions().get( Language.SELECTED_CHANNEL );
			id.setID( SELECTED_INPUT_CHANNEL );
			NumberRange rng = new NumberRange( 1, Integer.MAX_VALUE );
			ConfigParameter par = new ConfigParameter( id, rng );
			par.add( 1D );
			
			listUserConfig.put( SELECTED_INPUT_CHANNEL, par );
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	}
	//*/
	
	///////////////////
	/*
	
	DATA BASE
	
	*/
	///////////////////
	
	private static void loadDBFields()
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

		fieldType = new HashMap<String, Class>();
		fieldType.put( "idSession", Integer.class );
		fieldType.put( "userID", Integer.class );
		fieldType.put( "date", Integer.class );
		tableFields.put( sessionTableName, fieldType );
		
		fieldType = new HashMap<String, Class>();
		//fieldType.put( "number", Integer.class );
		//fieldType.put( "idSession", Integer.class );
		fieldType.put( "actionID", Integer.class );
		fieldType.put( "actionName", String.class );
		fieldType.put( "time", Integer.class );	
		tableFields.put( statisticTableName, fieldType );
	}
	

	/**
	 * Connect to a sample database
	 * @throws SQLException 
	 */
	private static void connectDB() throws SQLException 
	{
		if( conn == null || !conn.isClosed() )
		{
			conn = DriverManager.getConnection( dbURL );
		}
	}

	private static void closeDBConnection() throws SQLException
	{
		if( conn != null )
		{
			conn.close();
			conn = null;
		}
	}

	private static void SetTables() throws SQLException
	{	
		String sqlCreateTableUser = 
				"CREATE TABLE IF NOT EXISTS user (\n"
						+ " id integer PRIMARY KEY AUTOINCREMENT\n"
						+ ", name text NOT NULL\n"
						+ ", image BLOB\n"
						+ ");";

		String sqlCreateTableConfig = 
				"CREATE TABLE IF NOT EXISTS settings (\n"
				//+ " id integer PRIMARY KEY AUTOINCREMENT\n"
				+ " userID integer PRIMARY KEY \n"
				+ ", reactionTime real CHECK (reactionTime > 0)\n"
				+ ", recoverTime real CHECK (recoverTime > 0)\n"
				+ ", colorPreaction integer\n"
				+ ", colorWaitingAction integer\n"
				+ ", colorAction integer\n"
				+ ", songs text\n"
				+ ", minInputValue real\n"
				+ ", maxInputValue real\n"
				+ ", selectedChannel real"
				+ ", FOREIGN KEY (userID) REFERENCES user(id) ON DELETE CASCADE\n"
				+ ");";

		String sqlCreateTableSession = 
				"CREATE TABLE IF NOT EXISTS session (\n"
						+ " idSession integer PRIMARY KEY AUTOINCREMENT\n"
						+ ", userID integer"
						+ ", date integer NOT NULL\n"
						+ ", FOREIGN KEY (userID) REFERENCES user(id) ON DELETE CASCADE\n"
						+ ");";

		String sqlCreateTableStatistic = 
				"CREATE TABLE IF NOT EXISTS statistic (\n"
						+ " number integer PRIMARY KEY AUTOINCREMENT\n"
						+ ", idSession integer\n"
						+ ", actionID integer NOT NULL\n"
						+ ", actionName integer NOT NULL\n"
						+ ", time integer NOT NULL\n"						
						+ ", FOREIGN KEY ( idSession ) REFERENCES statistic( idSession ) ON DELETE CASCADE\n"
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

	public static int addPlayerDB( String name, BufferedImage img ) throws SQLException
	{
		String vars = "name";
		String vals = "?";

		int userID = Player.ANONYMOUS_USER_ID;

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
			connectDB();
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

			closeDBConnection();        	
		}

		return userID;
	}

	public static void updatePlayerDB( Player user ) throws SQLException
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
				connectDB();
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

				closeDBConnection();        	
			}	            
		}
	}

	public static void delPlayerDB( int id ) throws SQLException
	{	
		String sql = "DELETE FROM " + userTableName + " WHERE id = ?";
		PreparedStatement pstmt = null;

		try
		{
			connectDB();
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

			closeDBConnection();       
		}
	}

	public static List< Tuple< String, Object > > getPlayerConfigDB( int userID ) throws SQLException
	{
		String sql = "SELECT * FROM " + settingsTableName + " WHERE userID = " + userID;

		Statement stmt = null;
		ResultSet rs = null;

		List< Tuple< String, Object > > pars = new ArrayList< Tuple< String, Object > >();

		try
		{ 
			connectDB();

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

			closeDBConnection();
		}		

		return pars;
	}

	public static void insertPlayerConfigDB( int userID ) throws SQLException
	{		
		String sql = "INSERT INTO "+ settingsTableName +  "(userID";
		String sqlValues  = "VALUES(" + userID;

		Map< String, Class > fieldTable = tableFields.get( settingsTableName );
		Iterator< String > itField = fieldTable.keySet().iterator();

		while( itField.hasNext() )
		{			
			String fieldName = itField.next();

			String value = null;
			ConfigParameter par = ConfigApp.getParameter( fieldName );

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
			connectDB();

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

			closeDBConnection();
		}
	}

	public static void updatePlayerConfigDB( int userID ) throws SQLException
	{
		String sql =  "UPDATE " + settingsTableName + " SET ";

		boolean add = false;
		Map< String, Class > fields = tableFields.get( settingsTableName );
		for( String fieldName : fields.keySet() )
		{			
			String value = getFieldValue( fieldName );			

			if( value != null )
			{			
				add = true;
				sql += fieldName + " = " + value + " ,";				
			}
		}

		if( add )
		{		
			sql = sql.substring( 0, sql.length() - 1 );
			sql += " WHERE userID = " + userID;

			Statement stmt = null;

			try 
			{
				connectDB();

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

				closeDBConnection();
			}
		}
	}

	private static String getFieldValue( String fieldName )
	{
		String value = null;

		ConfigParameter par = ConfigApp.getParameter( fieldName );

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

		return value;
	}

	public static void updatePlayerConfigDB( int userID, String fieldID ) throws SQLException
	{
		Map< String, Class > fields = tableFields.get( settingsTableName );

		boolean add = fields.containsKey( fieldID );

		String sql =  "UPDATE " + settingsTableName + " SET ";

		if( add )
		{	
			String value = getFieldValue( fieldID );
			
			add = (value != null );

			if( add )
			{			
				add = true;
				sql += fieldID + " = " + value;				
			}			
		}

		if( add )
		{		
			sql += " WHERE userID = " + userID;

			Statement stmt = null;

			try 
			{
				connectDB();

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

				closeDBConnection();
			}
		}
	}

	public static Player getPlayerDB( int ID ) throws SQLException, IOException
	{
		String sql = "SELECT * FROM " + userTableName + " WHERE id = " + ID;

		Statement stmt = null;
		ResultSet rs = null;

		Player user = null;

		try
		{ 
			connectDB();

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

			closeDBConnection();
		}

		return user;
	}

	public static List< Player > getAllPlayersDB() throws SQLException, IOException
	{
		String sql = "SELECT * FROM " + userTableName;

		Statement stmt = null;
		ResultSet rs = null;

		List< Player > users = new ArrayList<Player>();

		try
		{ 
			connectDB();

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

			closeDBConnection();
		}

		return users;
	}

	public static void saveStatistic() throws SQLException
	{
		int playerID = GameStatistic.getPlayerID();
		ArrayTreeMap< LocalDateTime, GameStatistic.FieldType > register = GameStatistic.getRegister();
		
		if( playerID != Player.ANONYMOUS_USER_ID && !register.isEmpty() )
		{
			LocalDateTime startTime = GameStatistic.getStartDateTime();
			
			ZonedDateTime zdt = ZonedDateTime.of( startTime, ZoneId.systemDefault() );
			
			String sql = "INSERT INTO "+ sessionTableName +  "(userID, date) ";
			String sqlValues  = "VALUES(" + playerID + "," + zdt.toInstant().toEpochMilli() + ")";

			Statement stmt = null;

			ResultSet rs = null;
			
			try 
			{
				connectDB();

				stmt  = conn.createStatement();
			
				Integer sessionID = null;
				
				if( stmt.executeUpdate( sql + sqlValues ) > 0 )
				{
					rs = stmt.getGeneratedKeys();
					if(rs != null && rs.next())
					{
						sessionID = rs.getInt( 1 );
					}
				}
				
				if( sessionID != null )
				{	
					for( LocalDateTime t : register.keySet() )
					{
						List< FieldType > fields = register.get( t );
						
						zdt = ZonedDateTime.of( t, ZoneId.systemDefault() );
						
						for( FieldType f : fields )
						{
							sql = "INSERT INTO "+ statisticTableName +  "(idSession,actionID,actionName,time)";
							sqlValues = " VALUES(" + sessionID 
										+ "," + f.ordinal()
										+ ",\"" + f.name() + "\""
										+ "," + zdt.toInstant().toEpochMilli()
										+ ")";
							
							stmt.executeUpdate( sql + sqlValues );
						}
					}
					
					GameStatistic.clearRegister();
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

				closeDBConnection();
			}
		}
	}
	/*
	public static void main( String[] arg )
	{
		try
		{
			SetTables();
			addPlayerDB( "Manuel M.", null );
			addPlayerDB( "Luis V.", (BufferedImage)GeneralAppIcon.getSmileIcon( 9, 64, Color.BLACK, Color.RED ).getImage() );
			addPlayerDB( "MM", (BufferedImage)GeneralAppIcon.getSmileIcon( 9, 64, Color.BLACK, Color.YELLOW ).getImage() );



		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//*/	
}



