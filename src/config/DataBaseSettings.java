package config;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.DoubleBuffer;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import biosignal.Biosignal;
import config.ConfigParameter.ParameterType;
import general.ConvertTo;
import general.Tuple;
import gui.MainAppUI;
import image.BasicPainter2D;
import image.icon.GeneralAppIcon;
import lslInput.LSLStreamInfo.StreamType;
import lslInput.stream.IInputStreamMetadata;
import lslInput.stream.controller.IControllerMetadata;
import statistic.RegistrarStatistic;
import statistic.RegistrarStatistic.GameFieldType;
import statistic.chart.GameSessionStatisticPlayer;

public class DataBaseSettings 
{
	private static final String DB_FOLDER = ConfigApp.RESOURCES_PATH + "user/db/";
	private static final String DB_FILENAME = "data.db";
	private static final String DB_PATH = DB_FOLDER + DB_FILENAME;

	///////////
	//
	// Data base
	//
	///////////
	
	private static final double dbVersion = 1.0;
	
	private static final String prefixComm = "jdbc:sqlite:";

	private static final String dbInfoTableName = "databaseInfo";
	
	private static final String userTableName = "user";
	private static final String settingsTableName = "settings";
	private static final String sessionTableName = "session";
	private static final String statisticTableName = "statistic";
	private static final String sessionSettingTableName = "sessionSettings";
	private static final String inputDataStreamTableName = "inputDataStream";

	private static final String dbURL = prefixComm + DB_PATH;

	private static Map< String, Map< String, Class > > tableFields;	

	private static Connection conn;
	
	private static String DB_ID = "id";
	private static String DB_VERSION = "version";
	
	private static final String USER_ID = "id";
	private static final String USER_NAME = "name";
	private static final String USER_IMAGE = "image";
	private static final String USER_ID_FOREIGN  = "userID";
	
	private static final String SESSION_ID = "idSession";
	private static final String SESSION_SCORE = "score";	
	private static final String SESSION_MUTE = "muteSession";
	private static final String SESSION_LIMIT_SESSION_TIME = "limitSessionTime";
	private static final String SESSION_VALENCE_AROUSAL_EFFORT = "valenceArousalEmotion";
	private static final String SESSION_ID_FOREIG = "idSession";
	
	private static final String STATISTIC_NUMBER = "number";
	private static final String STATISTIC_ACTION_ID = "actionID";
	private static final String STATISTIC_ACTION_NAME = "actionName";
	private static final String STATISTIC_TIME = "time";
	
	private static final String INDATA_STREAM_ID = "number";
	private static final String INDATA_STREAM_DATA = "streamData";
	private static final String INDATA_STREAM_TYPE = "streamType";
	private static final String INDATA_STREAM_NAME = "streamName";
	private static final String INDATA_STREAM_SAMPLING_RATE = "streamSamplingRate";
	private static final String INDATA_NUMBER_CHANNELS = "streamNumberOfChannels";
	private static final String INDATA_DATETIME_FIRST_DATA = "datetimeOfFirstData";
		
	protected static void dbLoadFields()
	{
		tableFields = new HashMap<String, Map< String, Class > >();

		//**************
		//
		// DATABASE INFO TABLE
		//
		//**************

		Map< String, Class > fieldType = new HashMap<String, Class>();
		fieldType.put( DB_ID, Integer.class );
		fieldType.put( DB_VERSION, Double.class );		
		tableFields.put( dbInfoTableName, fieldType );
		
		//**************
		//
		// USER TABLE
		//
		//**************
		
		fieldType = new HashMap<String, Class>();
		fieldType.put( USER_ID, Integer.class );
		fieldType.put( USER_NAME, String.class );
		fieldType.put( USER_IMAGE, Image.class );		
		tableFields.put( userTableName, fieldType );

		//**************
		//
		// SETTING TABLE
		//
		//**************

		fieldType = new HashMap<String, Class>();
		fieldType.put( USER_ID_FOREIGN, Integer.class );
		//fieldType.put( SESSION_TIME, Integer.class );
		fieldType.put( ConfigApp.MOV_REPETITIONS, Integer.class );
		fieldType.put( ConfigApp.REACTION_TIME, Double.class );
		fieldType.put( ConfigApp.RECOVER_TIME, Double.class );		
		fieldType.put( ConfigApp.INPUT_MIN_VALUE, Double.class );
		fieldType.put( ConfigApp.INPUT_MAX_VALUE, Double.class );
		fieldType.put( ConfigApp.INPUT_SELECTED_CHANNEL, Double.class );
		fieldType.put( ConfigApp.TIME_IN_INPUT_TARGET, Double.class );
		fieldType.put( ConfigApp.PREACTION_COLOR, Integer.class );
		fieldType.put( ConfigApp.WAITING_ACTION_COLOR, Integer.class );
		fieldType.put( ConfigApp.ACTION_COLOR, Integer.class );
		fieldType.put( ConfigApp.SONG_LIST, String.class );
		fieldType.put( ConfigApp.BACKGROUND_IMAGE, String.class );
		fieldType.put( ConfigApp.NOTE_IMAGE, String.class );
		fieldType.put( ConfigApp.TASK_BLOCK_TIME, Integer.class );
		fieldType.put( ConfigApp.REST_TASK_TIME, Integer.class );		
		tableFields.put( settingsTableName, fieldType );

		//**************
		//
		// SESSION SETTING TABLE
		//
		//**************

		fieldType = new HashMap<String, Class>();				
		Map< String, Class > settingFields = tableFields.get( settingsTableName );
		fieldType.putAll( settingFields );
		fieldType.put( SESSION_ID, Integer.class );
		tableFields.put( sessionSettingTableName, fieldType );
				
		//**************
		//
		// SESSION TABLE
		//
		//**************
		
		fieldType = new HashMap<String, Class>();
		fieldType.put( SESSION_ID, Integer.class );
		fieldType.put( USER_ID_FOREIGN, Integer.class );
		fieldType.put( SESSION_SCORE, Long.class );		
		fieldType.put( SESSION_MUTE, Integer.class );
		fieldType.put( SESSION_LIMIT_SESSION_TIME, Integer.class );
		fieldType.put( SESSION_VALENCE_AROUSAL_EFFORT, String.class );
		tableFields.put( sessionTableName, fieldType );

		//**************
		//
		// STATISTICAL TABLE
		//
		//**************
		
		
		fieldType = new HashMap<String, Class>();
		fieldType.put( STATISTIC_NUMBER, Integer.class );
		fieldType.put( SESSION_ID_FOREIG, Long.class );
		fieldType.put( STATISTIC_ACTION_ID, Integer.class );
		fieldType.put( STATISTIC_ACTION_NAME, String.class );
		fieldType.put( USER_ID_FOREIGN, Integer.class );
		fieldType.put( STATISTIC_TIME, Integer.class );	
		tableFields.put( statisticTableName, fieldType );
		
		//**************
		//
		// STREAM DATA TABLE
		//
		//**************
		
		fieldType = new HashMap<String, Class>();
		fieldType.put( INDATA_STREAM_ID, Integer.class );
		fieldType.put( SESSION_ID_FOREIG, Integer.class );
		fieldType.put( USER_ID_FOREIGN, Integer.class );
		fieldType.put( INDATA_STREAM_TYPE, String.class );
		fieldType.put( INDATA_STREAM_NAME, String.class );
		fieldType.put( INDATA_STREAM_SAMPLING_RATE, Double.class );
		fieldType.put( INDATA_NUMBER_CHANNELS, Integer.class );
		fieldType.put( INDATA_DATETIME_FIRST_DATA, Integer.class );
		fieldType.put( INDATA_STREAM_DATA, DoubleBuffer.class );			
		tableFields.put( inputDataStreamTableName, fieldType );
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
			
			Statement smt = conn.createStatement();
			smt.execute( "PRAGMA foreign_keys=ON" );
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
		String vars = USER_NAME;
		String vals = "?";

		int userID = Player.ANONYMOUS;

		if( img != null )
		{
			vars += "," + USER_IMAGE;
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
			sql += USER_NAME + "  = ?, " + USER_IMAGE + " = ? WHERE " + USER_ID + " = " + id;

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
						BufferedImage scaleImg = BasicPainter2D.copyImage( img.getScaledInstance( ConfigApp.playerPicSize.t1, ConfigApp.playerPicSize.t2, BufferedImage.SCALE_SMOOTH ) );
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
		String sql = "DELETE FROM " + userTableName + " WHERE " + USER_ID + "  = ?";
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

	protected static List< Tuple< String, Object > > dbGetPlayerSetting( int userID ) throws SQLException
	{
		String sql = "SELECT * FROM " + settingsTableName + " WHERE " + USER_ID_FOREIGN + " = " + userID;

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
		String sql = "INSERT INTO "+ settingsTableName +  "(" + USER_ID_FOREIGN;
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
			sql += " WHERE " + USER_ID_FOREIGN + " = " + player.getId();

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

			/*
			add = (value != null );

			if( add )
			{			
				add = true;
				sql += fieldID + " = " + value;				
			}
			*/			
			
			sql += fieldID + " = " + value;
		}

		if( add )
		{		
			sql += " WHERE " + USER_ID_FOREIGN + " = " + player.getId();

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
		String sql = "SELECT * FROM " + userTableName + " WHERE " + USER_ID + " = " + ID;

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
				int id = rs.getInt( USER_ID );
				String name =  rs.getString( USER_NAME );

				InputStream input = rs.getBinaryStream( USER_IMAGE );

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
				int id = rs.getInt( USER_ID );
				String name =  rs.getString( USER_NAME );

				InputStream input = rs.getBinaryStream( USER_IMAGE );

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


	private static void dbSaveSessionPlayerSetting( Player player, long idSerssion ) throws SQLException
	{
		String sql1 = "INSERT INTO "+ sessionSettingTableName  +  " (" + SESSION_ID + "," + USER_ID_FOREIGN + ",";
		String sql2 = "VALUES ("+ idSerssion + "," + player.getId() + ",";

		Map< String, Class > fields = tableFields.get( sessionSettingTableName );
		for( String fieldName : fields.keySet() )
		{
			if( !fieldName.equals( SESSION_ID ) && !fieldName.equals( USER_ID_FOREIGN ) )
			{
				String value = dbGetFieldValue( player, fieldName );			

				sql1 += fieldName + ",";
				sql2 += value + ",";
			}
		}

		sql1 = sql1.substring( 0, sql1.length() - 1 ) + ")";
		sql2 = sql2.substring( 0, sql2.length() - 1 ) + ")";

		String sql = sql1 + " " + sql2;

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

	public static void dbSaveStatistic() throws SQLException, IOException
	{
		LocalDateTime startTime = RegistrarStatistic.getStartDateTime();

		if( startTime != null )
		{
			ZonedDateTime zdt = ZonedDateTime.of( startTime, ZoneId.systemDefault() );

			long sessionID = zdt.toInstant().toEpochMilli();

			boolean mute = false;

			ConfigParameter muteSession = ConfigApp.getGeneralSetting( ConfigApp.MUTE_SESSION );

			if( muteSession != null )
			{
				mute = (Boolean)muteSession.getSelectedValue();
			}

			ConfigParameter sessionTime = ConfigApp.getGeneralSetting( ConfigApp.LIMIT_SESSION_TIME );
			int limitSessiontime = 0;
			if( sessionTime != null )
			{
				limitSessiontime = ((Number)sessionTime.getSelectedValue()).intValue();
			}

			for( int playerID : RegistrarStatistic.getPlayerIDs() )
			{	
				List< Tuple< LocalDateTime, RegistrarStatistic.GameFieldType > > register = RegistrarStatistic.getRegister( playerID );

				if( playerID != Player.ANONYMOUS && !register.isEmpty() )
				{
					double score = RegistrarStatistic.getPlayerScore( playerID );					

					String surveyStatus = "" ;
					List< String > _SubjState = RegistrarStatistic.getValenceArousalEffortData( playerID );
					if( _SubjState != null )
					{
						for( String vae : _SubjState )
						{
							surveyStatus += vae + ",";
						}

						surveyStatus = surveyStatus.substring( 0, surveyStatus.length() - 1 );
					}

					//TODO					

					String sqlSession = "INSERT INTO "+ sessionTableName  + " ( "  
							+ SESSION_ID 
							+ ", " + USER_ID_FOREIGN
							+ ", " + SESSION_SCORE
							+ ", " + SESSION_MUTE
							+ ", " + SESSION_LIMIT_SESSION_TIME
							+ ", " + SESSION_VALENCE_AROUSAL_EFFORT
							+ ")";

					sqlSession += "VALUES(" + sessionID 
							+ "," + playerID
							+ "," + score
							+ "," + ( ( mute ) ? "1" : "0" )
							+ "," + limitSessiontime
							+ ", \"" + surveyStatus + "\""
							+ ")" ;


					// ESTOY GUARDANDO LOS DATOS DE SESION
					// LUEGO DATOS DE CONTROL
					// FINAL, LOS DATOS DE BIOSIGNALS
					
					IControllerMetadata cmeta = RegistrarStatistic.getControllerSetting( playerID );					
					LinkedList< Double[] > cData = RegistrarStatistic.getControllerData( playerID );
					
					
										
					String sqlDataController = "INSERT INTO "+ inputDataStreamTableName  + " ( "  
								+ SESSION_ID 
								+ ", " + USER_ID_FOREIGN
								+ ", " + INDATA_STREAM_NAME
								+ ", " + INDATA_STREAM_SAMPLING_RATE
								+ ", " + INDATA_NUMBER_CHANNELS
								+ ", " + INDATA_STREAM_TYPE
								+ ", " + INDATA_STREAM_DATA
								+ ")";
					
					sqlDataController += " VALUES (" + sessionID 
								+ "," + playerID
								+ ",\"" + cmeta.getName() + "\""
								+ "," + cmeta.getSamplingRate()
								+ "," + cmeta.getNumberOfChannels()
								+ ",\"" + StreamType.CONTROLLER.name() + "\""
								+ ", ? "
								+ ")" ;
					
					List< String > sqlDataBiosignals = new ArrayList< String >();
					List< IInputStreamMetadata > bioSettings = RegistrarStatistic.getBiosignalStreamSettings( playerID );
					if( bioSettings != null )
					{  
						for( IInputStreamMetadata bioMeta : bioSettings )
						{
							LinkedList< Double[] > bioData = RegistrarStatistic.getBiosignalData( playerID, bioMeta );
							if( bioData != null && !bioData.isEmpty() )
							{							
								String bioType = Biosignal.getBiosignalType( bioMeta.getContentType() ).name();
								
								String sqlDataBio = "INSERT INTO "+ inputDataStreamTableName  + " ( "  
											+ SESSION_ID 
											+ ", " + USER_ID_FOREIGN
											+ ", " + INDATA_STREAM_NAME
											+ ", " + INDATA_STREAM_SAMPLING_RATE
											+ ", " + INDATA_NUMBER_CHANNELS
											+ ", " + INDATA_STREAM_TYPE
											+ ", " + INDATA_STREAM_DATA
											+ ")";
							
								sqlDataBio += "VALUES(" + sessionID 
											+ "," + playerID
											+ ",\"" + bioMeta.getName() + "\""
											+ "," + bioMeta.getSamplingRate()
											+ "," + bioMeta.getNumberOfChannels()
											+ ",\"" + bioType + "\""
											+ ", ? "
											+ ")" ;
								
								sqlDataBiosignals.add( sqlDataBio );
							}
						}
					}
					
					
					PreparedStatement pstmt = null;
					Statement stmt = null;					

					try 
					{
						dbConnect();

						stmt = conn.createStatement();
						stmt.executeUpdate( sqlSession );
						stmt.close();
						
						pstmt = conn.prepareStatement( sqlDataController );

						byte[] data = null;
						if( !cData.isEmpty() )
						{
							data = readStreamData( cData );
						}
						pstmt.setBytes( 1, data );

						pstmt.executeUpdate( );

						int itBioData = 0;
						for( String sqlDataBio : sqlDataBiosignals )
						{
							pstmt = conn.prepareStatement( sqlDataBio );
	
							data = null;
							cData = RegistrarStatistic.getBiosignalData( playerID, bioSettings.get( itBioData ) );
							itBioData++;
							if( !cData.isEmpty() )
							{								
								data = readStreamData( cData );
							}
							pstmt.setBytes( 1, data );	
							pstmt.executeUpdate( );
						}							

						stmt = conn.createStatement();

						sqlSession = "INSERT INTO "+ statisticTableName +  " (idSession,userID,actionID,actionName,time) VALUES ";

						for( Tuple< LocalDateTime, GameFieldType > t : register )
						{
							LocalDateTime time = t.t1;
							GameFieldType f = t.t2;

							zdt = ZonedDateTime.of( time, ZoneId.systemDefault() );							
							sqlSession += "(" + sessionID 
									+ "," + playerID
									+ "," + f.ordinal()
									+ ",\"" + f.name() + "\""
									+ "," + zdt.toInstant().toEpochMilli()
									+ ")\n ,";							
						}

						sqlSession = sqlSession.substring( 0, sqlSession.length() - 1 );

						stmt.executeUpdate( sqlSession );
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

					for( Player p : ConfigApp.getPlayers() )
					{
						if( p.getId() == playerID )
						{
							dbSaveSessionPlayerSetting( p, sessionID );						
						}
					}
				}
			}

			RegistrarStatistic.clearRegister();
		}
	}

	private static byte[] readStreamData( LinkedList< Double[] > data ) throws IOException 
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		for( Double[] d : data ) 
		{	
			bos.write( ConvertTo.DoubleArray2byteArray( d ) );
		}

		return bos.toByteArray();
	}

	public static List< GameSessionStatisticPlayer > dbGetPlayerStatistic( int player ) throws SQLException, IOException
	{
		List< GameSessionStatisticPlayer > stat = new ArrayList< GameSessionStatisticPlayer >();

		Statement stmt = null;
		ResultSet rs = null;

		try
		{
			dbConnect();

			String sqlSession = "SELECT * FROM " + sessionTableName + " WHERE userID = " + player;
			String sqlStatistic = "SELECT * FROM " + statisticTableName + " WHERE idSession = ";
			String sqlSessionSetting = "SELECT * FROM " + sessionSettingTableName + " WHERE idSession = ";

			stmt = conn.createStatement();
			rs = stmt.executeQuery( sqlSession );

			List< Long > sessionIDS = new ArrayList< Long >();
			while( rs.next() )
			{           	
				long sessionID = rs.getLong( "idSession" );
				sessionIDS.add( sessionID );

				//
				// SESSION
				//

				/*
	GameSessionStatistic gss = new GameSessionStatistic( sessionID );
	gss.addPlayer( player );				
	gss.setControllerName( player, rs.getString( "controllerName" ) );
	gss.setSamplingRate( player, rs.getDouble( "controllerSamplingRate" ) );
	gss.setNumberOfChannels(player, nch );
	gss.setSelectedChannel(player, rs.getInt( "controllerChannel" ) );					
	gss.setActionLevel(player, new NumberRange( rs.getDouble( "controllerMaxValueTarget" ), Double.POSITIVE_INFINITY ) );
	gss.setRecoverLevel( player,rs.getDouble("controllerMinValueTarget" ) );
	gss.setTargetTimeInLevelAction(player, rs.getDouble( "controllerTimeTarget" ) );
	gss.addScore( player, sessionID , rs.getInt( "score" ) );

	InputStream input = rs.getBinaryStream( "controllerData" );
	if( input != null )
	{
		BufferedInputStream bis = new BufferedInputStream( input );

		List< Double[] > data = new ArrayList< Double[] >();					
		byte[] byteData = new byte[ ( nch + 1 ) * Double.BYTES ];

		while( bis.read( byteData ) > 0 )
		{
			data.add( ConvertTo.doubleArray2DoubleArray( ConvertTo.ByteArray2DoubleArray( byteData ) ) );
		}

		Double[][] dataMatrix = new Double[ data.size() ][ nch + 1 ];
		for( int i = 0; i < data.size(); i++ )
		{
			dataMatrix[ i ] = data.get( i );
		}
		gss.setSessionControllerData( player, dataMatrix );

	}
	//*/

				GameSessionStatisticPlayer gss = new GameSessionStatisticPlayer( sessionID, player );

				gss.setMuteSession( rs.getInt( "muteSession" ) );
				gss.setLimitSessionTime( rs.getInt( "limitSessionTime" ) );
				gss.setScore( rs.getInt( "score" ) );

				gss.setControllerName( rs.getString( "controllerName" ) );
				gss.setSamplingRate( rs.getDouble( "controllerSamplingRate" ) );

				int nch = rs.getInt( "controllerNumberOfChannel" );

				gss.setNumberOfChannels( nch );

				InputStream input = rs.getBinaryStream( "controllerData" );
				if( input != null )
				{
					BufferedInputStream bis = new BufferedInputStream( input );

					List< Double[] > data = new ArrayList< Double[] >();					
					byte[] byteData = new byte[ ( nch + 1 ) * Double.BYTES ];

					while( bis.read( byteData ) > 0 )
					{
						data.add( ConvertTo.doubleArray2DoubleArray( ConvertTo.ByteArray2DoubleArray( byteData ) ) );
					}

					Double[][] dataMatrix = new Double[ data.size() ][ nch + 1 ];
					for( int i = 0; i < data.size(); i++ )
					{
						dataMatrix[ i ] = data.get( i );
					}
					gss.setSessionControllerData( dataMatrix );
				}


				String sqlQuery = sqlSessionSetting + sessionID;
				sqlQuery += " AND userID = " + player;

				ResultSet rs2 = stmt.executeQuery( sqlQuery );

				while( rs2.next() )
				{	
					gss.setReactionTime( rs2.getDouble( "reactionTime" ) );
					gss.setRecoverTime( rs2.getDouble( "recoverTime" ) );
					gss.setRepetitions( rs2.getInt( "repetitions" ) );

					gss.setActionLevel( rs2.getDouble( "maxInputValue" ) );
					gss.setRecoverLevel( rs2.getDouble("minInputValue" ) );					
					gss.setSelectedChannel( rs2.getInt( "selectedChannel" ) );
					gss.setTargetTimeInLevelAction( rs2.getDouble( "timeInInputTarget" ) );
					
					gss.setTaskBlockTime( rs.getInt( ConfigApp.TASK_BLOCK_TIME ) );
					gss.setRestBlockTime( rs2.getInt( ConfigApp.REST_TASK_TIME ) );
				}

				sqlQuery = sqlStatistic + sessionID;
				sqlQuery += " AND userID = " + player;

				rs2 = stmt.executeQuery( sqlQuery );

				while( rs.next() )
				{
					String actName = rs.getString( "actionName" );
					long actTime = rs.getLong( "time" );

					gss.addGameEvent( actTime, actName );
				}

				stat.add( gss );
			}
		}
		catch ( SQLException | IOException ex ) 
		{
			throw ex;
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

		Collections.sort( stat, new Comparator< GameSessionStatisticPlayer >()
		{
			@Override
			public int compare(GameSessionStatisticPlayer o1, GameSessionStatisticPlayer o2)
			{
				return o1.getSessionDate().compareTo( o2.getSessionDate() );
			}
		}
				);

		return stat;
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


	public static boolean checkDBFile() throws SQLException, IOException
	{
		File dbFile = new File( DB_PATH );

		boolean created = !dbFile.exists();

		if( created )
		{
			dbFile = new File( DB_FOLDER );
			dbFile.mkdirs();

			dbCreateTables();
		}
		else
		{	
			int change = changeDBVersion();
			
			if( change != 0 )
			{
				String msg = "New ";
				if( change < 0 )
				{
					msg = "Previous ";
				}
						
				msg += " database version was detected.\nA new database has been created with the correct version and the data has been transferred.";
				
				final String m = msg;
				Thread t = new Thread()
				{
					public void run() 
					{
						JOptionPane.showMessageDialog( MainAppUI.getInstance(), m );
					}
				};
				
				t.start();
				
				transferDB2NewVersion();
			}
		}

		return created;
	}
	
	private static int changeDBVersion()
	{
		int change = 0;
		
		try 
		{
			dbConnect();
						
			Statement stmt = null;
			ResultSet rs = null;
			
			String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + dbInfoTableName + "'";
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery( sql );
						
			if( rs.next() )
			{
				sql = "SELECT * FROM " + dbInfoTableName;
				
				rs = stmt.executeQuery( sql );
				
				if( rs.next() )
				{
					double ver = rs.getDouble( DB_VERSION );
					
					if( ( dbVersion - ver ) > 0 ) 
					{
						change = 1;
					}
					else if( (dbVersion - ver) < 0 )
					{
						change = -1;
					}
				}
			}
			else
			{
				change = 1;
			}
			
			dbCloseConnection();			
		}
		catch (SQLException e) 
		{
			change = 1;
		}	
		
		return change;
	}

	private static void transferDB2NewVersion() throws SQLException, IOException
	{
		File dbFile = new File( DB_PATH );
		
		String copy = DB_PATH.substring( 0, DB_PATH.lastIndexOf( "." ) ) + "_copy" + ".db";
		
		File dbcopy = new File( copy );
		
		if( !dbFile.renameTo( dbcopy ) )
		{
			throw new IOException( "Error when transferring data to the new version: Failed to rename the database.");
		}
		
		if( dbFile.exists() )
		{
			throw new IOException( "Error previous version: original db file remaining.");
		}
		
		Connection original_conn  = null;
		try
		{		
			dbCreateTables();
			
			original_conn = DriverManager.getConnection( prefixComm + copy );
			
			Statement original_smt = original_conn.createStatement();
			original_smt.execute( "PRAGMA foreign_keys=ON" );
			
			String sql = "INSERT INTO "+ dbInfoTableName +  "(" + DB_ID  + ", " + DB_VERSION + " ) VALUES( 0, " + dbVersion + " );" ;

			dbConnect();
			Statement smt = conn.createStatement();
			smt.execute( sql );

			for( String tableName : new String[]{ userTableName, settingsTableName, sessionTableName, sessionSettingTableName, statisticTableName, inputDataStreamTableName } )
			{
				Map<String, Class> fieldType = tableFields.get( tableName );

				String original_sql = "SELECT * from " + tableName;

				ResultSet original_rs = original_smt.executeQuery( original_sql );
				ResultSetMetaData rsmd = original_rs.getMetaData();

				List< String > colNames = new ArrayList< String >();
				for( int ic = 1; ic <= rsmd.getColumnCount(); ic++ )
				{
					colNames.add( rsmd.getColumnName( ic ) );
				}
				
				while( original_rs.next() )
				{
					sql = "INSERT INTO " + tableName + "(";
					String sqlValue = " VALUES ( ";
					
					List< InputStream > blobList = new ArrayList< InputStream >();
					for( String fieldName  : colNames )
					{
						Class dataType = fieldType.get( fieldName );

						if( dataType != null )
						{
							sql += fieldName + "," ;

							if( dataType == Integer.class || dataType == Long.class )
							{
								sqlValue += original_rs.getLong( fieldName ) + ",";
							}
							else if( dataType == Double.class || dataType == Float.class )
							{
								sqlValue += original_rs.getDouble( fieldName ) + ",";
							}
							else if( dataType == String.class )
							{
								sqlValue += "'" +  original_rs.getString( fieldName ) + "',";
							}
							else
							{
								sqlValue += "?,";
								blobList.add( original_rs.getBinaryStream( fieldName ) );
							}
						}
						else
						{
							System.out.println( "Field " + fieldName + " in table " +  tableName + ", type no found." );
						}
					}
					
					sql = sql.substring( 0, sql.length() - 1 ) + " ) ";
					sqlValue = sqlValue.substring( 0, sqlValue.length() - 1 ) + " ) ";

					if( blobList.isEmpty() )
					{
						smt.executeUpdate( sql + sqlValue );
					}
					else
					{
						PreparedStatement pstmt = conn.prepareStatement( sql + sqlValue );


						for( int iBL = 0; iBL < blobList.size(); iBL++ )
						{
							InputStream inStream = blobList.get( iBL );

							byte[] data = null;
							if( inStream != null )
							{
								List< Byte > blobBytes = new ArrayList< Byte >();
	
								data = new byte[1];
								while( inStream.read( data ) > 0 )
								{
									blobBytes.add( data[ 0 ] );
								}
	
								if( !blobBytes.isEmpty() )
								{
									data = new byte[ blobBytes.size() ];
	
									for( int id = 0; id < blobBytes.size(); id++ )
									{
										data[ id ] = blobBytes.get( id );
									}									
								}
							}
							
							pstmt.setBytes( iBL+1, data );
						}

						pstmt.executeUpdate( );							
						pstmt.close();
					}
				}					
			}
			
			if( smt != null )
			{
				smt.close();
			}
						
			dbCloseConnection();
			
			if( original_smt != null )
			{
				original_smt.close();
			}			
			original_conn.close();
		}
		catch (SQLException e) 
		{
			throw e;
		}
		finally 
		{
			dbCloseConnection();
			
			if( original_conn != null )
			{ 
				original_conn.close();
			}
		}
		
	}
	
	private static void dbCreateTables() throws SQLException
	{	
		String sqlCreateTableDBInfo = 
				"CREATE TABLE IF NOT EXISTS " + dbInfoTableName + " ("
						+ DB_ID + " integer PRIMARY KEY CHECK (" + DB_ID + " = 0 )"
						+ ", " + DB_VERSION + " real CHECK (" + DB_VERSION + " >= 0 )"
						+ ");";
		
		String sqlCreateTableUser = 
				"CREATE TABLE IF NOT EXISTS " + userTableName + " ("
						+ USER_ID + " integer PRIMARY KEY AUTOINCREMENT"
						+ ", " + USER_NAME + " text NOT NULL"
						+ ", " + USER_IMAGE + " BLOB"
						+ ");";

		String sqlSettingFields =  USER_ID_FOREIGN + " integer "
							+ ", " + ConfigApp.REACTION_TIME + " real CHECK (" + ConfigApp.REACTION_TIME +"> 0)"
							+ ", " + ConfigApp.RECOVER_TIME + " real CHECK (" + ConfigApp.RECOVER_TIME + ">= 0)"
							+ ", " + ConfigApp.MOV_REPETITIONS + " integer CHECK ( " + ConfigApp.MOV_REPETITIONS + " > 0)"
							+ ", " + ConfigApp.TASK_BLOCK_TIME + " integer CHECK (" + ConfigApp.TASK_BLOCK_TIME + ">= 0)"
							+ ", " + ConfigApp.REST_TASK_TIME + " integer CHECK (" + ConfigApp.REST_TASK_TIME + ">= 0)"
							+ ", " + ConfigApp.PREACTION_COLOR + " integer"
							+ ", " + ConfigApp.WAITING_ACTION_COLOR + " integer"
							+ ", " + ConfigApp.ACTION_COLOR + " integer"
							+ ", " + ConfigApp.SONG_LIST + " text"
							+ ", " + ConfigApp.INPUT_MIN_VALUE + " real"
							+ ", " + ConfigApp.INPUT_MAX_VALUE + " real"
							+ ", " + ConfigApp.TIME_IN_INPUT_TARGET + " real"
							+ ", " + ConfigApp.INPUT_SELECTED_CHANNEL + " real"
							+ ", " + ConfigApp.BACKGROUND_IMAGE + " text"
							+ ", " + ConfigApp.NOTE_IMAGE + " text";
		
		String sqlCreateTableConfig = 
				"CREATE TABLE IF NOT EXISTS " + settingsTableName + " ("
				//+ " id integer PRIMARY KEY AUTOINCREMENT"
				+ sqlSettingFields
				+ ", PRIMARY KEY (" + USER_ID_FOREIGN + ")"
				+ ", FOREIGN KEY (" + USER_ID_FOREIGN + ") REFERENCES " + userTableName + "(" + USER_ID + ") ON DELETE CASCADE"
				+ ");";
		
		String sqlCreateTableSessionConfig = 
				"CREATE TABLE IF NOT EXISTS " + sessionSettingTableName + " ("
				//+ " id integer PRIMARY KEY AUTOINCREMENT"
				+ " " + SESSION_ID_FOREIG + " integer "
				+ ", " + sqlSettingFields				
				+ ", PRIMARY KEY (" + SESSION_ID + ", " + USER_ID_FOREIGN +")"
				+ ", FOREIGN KEY (" + SESSION_ID + "," + USER_ID_FOREIGN + ") REFERENCES " + sessionTableName + "(" + SESSION_ID + ","+USER_ID_FOREIGN + ") ON DELETE CASCADE"
				//+ ", FOREIGN KEY (" + USER_ID_FOREIGN + ") REFERENCES " + userTableName + "(" + USER_ID + ") ON DELETE CASCADE"
				+ ");";

		String sqlCreateTableSession = 
				"CREATE TABLE IF NOT EXISTS " +sessionTableName + " ("
						+ SESSION_ID + "  integer NOT NULL" // Session date
						+ ", " + USER_ID_FOREIGN + " integer NOT NULL"
						+ ", " + SESSION_MUTE + " integer NOT NULL CHECK ( " + SESSION_MUTE + " == 0 OR " + SESSION_MUTE + " == 1 ) "
						+ ", " + SESSION_LIMIT_SESSION_TIME + " integer CHECK (" + SESSION_LIMIT_SESSION_TIME + " >= 0)"
						+ ", " + SESSION_SCORE + " integer NOT NULL"						
						+ ", " + SESSION_VALENCE_AROUSAL_EFFORT + " text NOT NULL"
						+ ", PRIMARY KEY (" + SESSION_ID + "," + USER_ID_FOREIGN + ")"
						+ ", FOREIGN KEY (" + USER_ID_FOREIGN + ") REFERENCES " + userTableName +"(" + USER_ID +") ON DELETE CASCADE"
						+ ");";

		
		String sqlCreateTableInputDataStream = 
				"CREATE TABLE IF NOT EXISTS " + inputDataStreamTableName + " ("
						+ INDATA_STREAM_ID + " integer PRIMARY KEY AUTOINCREMENT"
						+ ", " + SESSION_ID_FOREIG + "  integer NOT NULL" 
						+ ", " + USER_ID_FOREIGN + " integer NOT NULL"
						+ ", " + INDATA_STREAM_NAME + "  text NOT NULL"
						+ ", " + INDATA_STREAM_SAMPLING_RATE + " real NOT NULL"
						+ ", " + INDATA_NUMBER_CHANNELS + " integer NOT NULL"
						+ ", " + INDATA_DATETIME_FIRST_DATA + " integer DEFAULT -1 NOT NULL"
						+ ", " + INDATA_STREAM_TYPE + " text NOT NULL"
						+ ", " + INDATA_STREAM_DATA + " BLOB"
						+ ", FOREIGN KEY (" + SESSION_ID + "," + USER_ID_FOREIGN + ") REFERENCES " + sessionTableName + "(" + SESSION_ID + ","+USER_ID_FOREIGN + ") ON DELETE CASCADE"
						//+ ", FOREIGN KEY (" +  + ") REFERENCES " + userTableName + "(" + USER_ID + ") ON DELETE CASCADE"
						+ ");";
		

		String sqlCreateTableStatistic = 
				"CREATE TABLE IF NOT EXISTS " + statisticTableName +" ("
						+ STATISTIC_NUMBER + " integer PRIMARY KEY AUTOINCREMENT"
						+ ", " + SESSION_ID_FOREIG + " integer NOT NULL"
						+ ", " + USER_ID_FOREIGN + " integer NOT NULL"
						+ ", " + STATISTIC_ACTION_ID + " integer NOT NULL"
						+ ", " + STATISTIC_ACTION_NAME + " text NOT NULL"
						+ ", " + STATISTIC_TIME + " integer NOT NULL"						
						+ ", FOREIGN KEY (" + SESSION_ID + "," + USER_ID_FOREIGN + ") REFERENCES " + sessionTableName + "(" + SESSION_ID + ","+USER_ID_FOREIGN + ") ON DELETE CASCADE"
						//+ ", FOREIGN KEY (" + USER_ID_FOREIGN + ") REFERENCES " + userTableName + "(" + USER_ID + ") ON DELETE CASCADE"
						+ ");";

		try ( Connection conn = DriverManager.getConnection( dbURL );
				Statement stmt = conn.createStatement()) 
		{
			stmt.execute( sqlCreateTableDBInfo );
			stmt.execute( sqlCreateTableUser );
			stmt.execute( sqlCreateTableConfig );
			stmt.execute( sqlCreateTableSession );
			stmt.execute( sqlCreateTableStatistic );
			stmt.execute( sqlCreateTableSessionConfig );
			stmt.execute( sqlCreateTableInputDataStream );
		} 
		catch ( SQLException e ) 
		{
			throw e;
		}
	}
}
