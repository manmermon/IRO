package db.sqlite;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import GUI.GeneralAppIcon;
import config.ConfigApp;
import config.ConfigParameter;
import config.ConfigParameter.ParameterType;
import config.User;
import general.Tuple;
import image.basicPainter2D;

public class DBSQLite
{	
	/**
	 *
	 * @author Manuel Merino
	 */

	private final String prefixComm = "jdbc:sqlite:";
	
	private final String userTableName = "user";
	private final String settingsTableName = "settings";
	private final String statisticTableName = "statistic";
	
	private final String url = this.prefixComm + ConfigApp.DB_URL;
	
	private Map< String, Map< String, Class > > tableFields;
	private Map< String, String > tableFieldsMatchConfigApp;
	
	
	private Connection conn;
	
	public DBSQLite()
	{
		this.tableFields = new HashMap<String, Map< String, Class > >();
		this.tableFieldsMatchConfigApp = new HashMap<String, String>();	
		
		Map< String, Class > fieldType = new HashMap<String, Class>();
		this.tableFields.put( this.userTableName, fieldType );
		
		fieldType.put( "id", Integer.class );
		fieldType.put( "name", String.class );
		fieldType.put( "image", Image.class );
				
		fieldType = new HashMap<String, Class>();
		this.tableFields.put( this.settingsTableName, fieldType );
		
		fieldType.put( "id", Integer.class );
		fieldType.put( "userID", Integer.class );
		fieldType.put( "reactionTime", Double.class );
		fieldType.put( "recoverTime", Double.class );
		fieldType.put( "colorPreaction", Integer.class );
		fieldType.put( "colorWaitingAction", Integer.class );
		fieldType.put( "colorAction", Integer.class );
		fieldType.put( "songs", String.class );
		
		this.tableFieldsMatchConfigApp.put( "reactionTime", ConfigApp.USER_REACTION_TIME );
		this.tableFieldsMatchConfigApp.put( "recoverTime", ConfigApp.USER_RECOVER_TIME );
		this.tableFieldsMatchConfigApp.put( "colorPreaction", ConfigApp.PREACTION_COLOR );
		this.tableFieldsMatchConfigApp.put( "colorWaitingAction", ConfigApp.WAITING_ACTION_COLOR );
		this.tableFieldsMatchConfigApp.put( "colorAction", ConfigApp.ACTION_COLOR );
		this.tableFieldsMatchConfigApp.put( "songs", ConfigApp.SONG_LIST );
		
		fieldType = new HashMap<String, Class>();
		this.tableFields.put( this.statisticTableName, fieldType );
		
		fieldType.put( "id", Integer.class );
		fieldType.put( "userID", Integer.class );
	}
	
	/**
	 * Connect to a sample database
	 * @throws SQLException 
	 */
	public void connect() throws SQLException 
	{
		if( this.conn == null || !this.conn.isClosed() )
		{
			this.conn = DriverManager.getConnection( this.url );
		}
	}
	
	public void closeConnection() throws SQLException
	{
		if( this.conn != null )
    	{
    		this.conn.close();
    		this.conn = null;
    	}
	}
	
	public void SetTables() throws SQLException
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
						+ ", FOREIGN KEY (userID) REFERENCES user(id) ON DELETE CASCADE\n"
						+ ");";
		
		String sqlCreateTableStatistic = 
				"CREATE TABLE IF NOT EXISTS statistic (\n"
						+ " id integer PRIMARY KEY AUTOINCREMENT\n"
						+ ", userID integer UNIQUE\n"
						+ ", FOREIGN KEY (userID) REFERENCES user(id) ON DELETE CASCADE\n"
						+ ");";
		
		try ( Connection conn = DriverManager.getConnection( this.url );
                Statement stmt = conn.createStatement()) 
		{
            stmt.execute( sqlCreateTableUser );
            stmt.execute( sqlCreateTableConfig );
            stmt.execute( sqlCreateTableStatistic );
        } 
		catch ( SQLException e ) 
		{
            throw e;
        }
	}
	
	public int addUser( String name, BufferedImage img ) throws SQLException
	{
		String vars = "name";
		String vals = "?";
		
		int userID = User.ANONYMOUS_USER_ID;
		
		if( img != null )
		{
			vars += ",image";
			vals += ",?";
		}
		
		String sql = "INSERT INTO " + this.userTableName +  "(" + vars + ") VALUES(" + vals + ")";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
        try  
        {
        	this.connect();
            pstmt = this.conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
            		
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
        	
        	this.closeConnection();        	
		}
        
        return userID;
	}

	public void updateUser( User user ) throws SQLException
	{
		if( user != null )
		{
			int id = user.getId();
			String name = user.getName();
			BufferedImage img = (BufferedImage)user.getImg().getImage();
			
			String sql =  "UPDATE " + this.userTableName + " SET ";
			sql += "name = ?, image = ? WHERE id = " + id;
			
			PreparedStatement pstmt = null;
	        try  
	        {
	        	this.connect();
	            pstmt = this.conn.prepareStatement( sql );
	            		
	            pstmt.setString(1, name);
	            
	            if( img != null )
	            {
	            	byte [] imageBytes = null;
	                ByteArrayOutputStream bos = new ByteArrayOutputStream();
	                try
					{
	                	BufferedImage scaleImg = basicPainter2D.convertToBufferedImage( img.getScaledInstance( ConfigApp.playerPicSize.x, ConfigApp.playerPicSize.y, BufferedImage.SCALE_SMOOTH ) );
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
	        	
	        	this.closeConnection();        	
			}	            
		}
	}
	
	public void delUser( int id ) throws SQLException
	{	
        String sql = "DELETE FROM " + userTableName + " WHERE id = ?";
        PreparedStatement pstmt = null;
                
		try
		{
			this.connect();
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
        	
        	this.closeConnection();       
		}
	}
	
	
	public List< Tuple< String, Object > > getUserConfig( int userID ) throws SQLException
	{
		String sql = "SELECT * FROM " + this.settingsTableName + " WHERE userID = " + userID;
	    
		Statement stmt = null;
        ResultSet rs = null;
		        
		List< Tuple< String, Object > > pars = new ArrayList< Tuple< String, Object > >();
		
		try
        { 
        	this.connect();
        	
        	stmt  = conn.createStatement();
        	rs    = stmt.executeQuery( sql );
            
        	Map< String, Class > tableFieldType = this.tableFields.get( this.settingsTableName );
        	
            if ( rs.next() ) 
            {
            	for( String fieldName : this.tableFieldsMatchConfigApp.keySet() )
            	{
            		String setID = this.tableFieldsMatchConfigApp.get( fieldName );
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
            		
            		Tuple< String, Object > par = new Tuple<String, Object>( setID, val );
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
			
			this.closeConnection();
		}		
		
		return pars;
	}
	
	
	public void insertUserConfig( int userID ) throws SQLException
	{		
		String sql = "INSERT INTO "+ this.settingsTableName +  "(userID";
		String sqlValues  = "VALUES(" + userID;
		
		Iterator< String > itField = this.tableFieldsMatchConfigApp.keySet().iterator();
		
		while( itField.hasNext() )
		{			
			String fieldName = itField.next();
			
			String cid = this.tableFieldsMatchConfigApp.get( fieldName );
			String value = null;
			ConfigParameter par = ConfigApp.getProperty( cid );
			
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
		
		sql += ") " + sqlValues + ")";
		
		Statement stmt = null;
		
		try 
		{
			this.connect();

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
			
			this.closeConnection();
		}
	}
	
	public void updateUserConfig( int userID ) throws SQLException
	{
		String sql =  "UPDATE " + this.settingsTableName + " SET ";
		
		boolean add = false;
		for( String fieldName : this.tableFieldsMatchConfigApp.keySet() )
		{			
			String cid = this.tableFieldsMatchConfigApp.get( fieldName );
			String value = null;
			ConfigParameter par = ConfigApp.getProperty( cid );
			
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
				this.connect();
	
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
				
				this.closeConnection();
			}
		}
	}
	
	public User getUser( int ID ) throws SQLException, IOException
	{
		String sql = "SELECT * FROM " + this.userTableName + " WHERE id = " + ID;
		    
		Statement stmt = null;
        ResultSet rs = null;
		
        User user = null;
        
        try
        { 
        	this.connect();
        	
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
            	
            	user = new User( id, name, new ImageIcon( img ) );
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
			
			this.closeConnection();
		}
        
        return user;
	}
	
	public List< User > getAllUsers() throws SQLException, IOException
	{
		String sql = "SELECT * FROM " + this.userTableName;
        
		Statement stmt = null;
        ResultSet rs = null;
		
        List< User > users = new ArrayList<User>();
        
        try
        { 
        	this.connect();
        	
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
            	
            	User user = new User( id, name, icon );
            	
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
			
			this.closeConnection();
		}
        
        return users;
	}
	
	
	public static void main( String[] arg )
	{
		DBSQLite db = new DBSQLite();
		try
		{
			db.SetTables();
			db.addUser( "Manuel M.", null );
			db.addUser( "Luis V.", (BufferedImage)GeneralAppIcon.getSmileIcon( 9, 64, Color.BLACK, Color.RED ).getImage() );
			db.addUser( "MM", (BufferedImage)GeneralAppIcon.getSmileIcon( 9, 64, Color.BLACK, Color.YELLOW ).getImage() );
			
			
			
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
