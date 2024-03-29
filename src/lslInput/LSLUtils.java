/* 
 * Copyright 2018-2020 by Manuel Merino Monge <manmermon@dte.us.es>
 *  
 *   This file is part of LSLRec.  https://github.com/manmermon/LSLRecorder
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
 *   
 */
package lslInput;

import java.nio.charset.Charset;
import java.util.List;

import biosignal.Biosignal;
import general.StringTuple;
import lslInput.LSLStreamInfo.StreamDataType;
import lslInput.LSLStreamInfo.StreamType;

public class LSLUtils 
{	
	public static String getAdditionalInformationLabelInXml()
	{
		return "desc";
	}

	public static StringTuple splitFieldStreamContentType( String content_type )
	{	
		String p1 = "";
		String p2 = "";
		
		String[] parts = content_type.split( "/" );

		if( parts.length == 1 )
		{
			p1 = parts[ 0 ];
		}
		else if( parts.length == 2 )
		{
			p1 = parts[ 0 ];
			p2 = parts[ 1 ];
		}
		
		return new StringTuple( p1, p2 );
	}
	
	public static StreamType getStreamType( String type )
	{
		StreamType strType = StreamType.UNKNOW;
		
		if( type != null )
		{		
			StringTuple stparts = splitFieldStreamContentType( type );
			
			String[] parts = new String[] { stparts.t1, stparts.t2 };
			
			if( parts.length == 1 )
			{
				if( parts[ 0 ].equalsIgnoreCase( StreamType.CONTROLLER.name() ) )
				{
					strType = StreamType.CONTROLLER;
				}
				else 
				{
					String[] parts2 = parts[ 0 ].split( ":" );
					
					if( parts2.length == 2 )
					{
						String p21 = parts2 [ 0 ];
						String p22 = parts2 [ 1 ];
						
						if( p21.equalsIgnoreCase( StreamType.BIOSIGNAL.name() ) )
						{
							boolean find = false;
							
							for( Biosignal.Type bt : Biosignal.Type.values() )
							{
								find = bt.name().equalsIgnoreCase( p22 );
								
								if( find )
								{
									break;
								}
							}
								
							if( find )
							{
								strType = StreamType.BIOSIGNAL;
							}
						}
					
						strType = StreamType.BIOSIGNAL;
					}
				}
			}
			else if( parts.length == 2 )
			{
				String p1 = parts[ 0 ];
				String p2 = parts[ 1 ];
				
				if( p1.equalsIgnoreCase( StreamType.CONTROLLER.name() ) )
				{
					strType = StreamType.CONTROLLER;
				}
				
				String[] parts2 = p2.split( ":" );
				
				if( parts2.length == 2 )
				{
					String p21 = parts2 [ 0 ];
					String p22 = parts2 [ 1 ];
					
					if( p21.equalsIgnoreCase( StreamType.BIOSIGNAL.name() ) )
					{
						boolean find = false;
						
						for( Biosignal.Type bt : Biosignal.Type.values() )
						{
							find = bt.name().equalsIgnoreCase( p22 );
							
							if( find )
							{
								break;
							}
						}
							
						if( find )
						{
							if( strType == StreamType.CONTROLLER )
							{
								strType = StreamType.CONTROLLER_BIOSIGNAL;
							}
							else
							{
								strType = StreamType.BIOSIGNAL;
							}
						}
					}
				}
			}
		}
		
		return strType;
	}
	
	public static StreamDataType getDataTypeByClass( Object classType )
	{
		StreamDataType dataType = StreamDataType.undefined;
		
		if( classType instanceof Double )
		{
			dataType = StreamDataType.double64;			
		}
		else if( classType instanceof Float )
		{
			dataType = StreamDataType.float32;
		}
		else if( classType instanceof Long )
		{
			dataType = StreamDataType.int64;
		}
		else if( classType instanceof Integer )
		{
			dataType = StreamDataType.int32;
		}
		else if( classType instanceof Short )
		{
			dataType = StreamDataType.int16;
		}
		else if( classType instanceof Byte )
		{
			dataType = StreamDataType.int8;
		}
		else if( classType instanceof String )
		{
			dataType = StreamDataType.string;
		}
		else if ( classType instanceof Character )
		{
			dataType = StreamDataType.string;
		}
		
		return dataType;
	}
	
	public static int getDataTypeBytes( StreamDataType type )
	{
		int len = -1;
		
		switch ( type ) 
		{
			case double64:
			{
				len = Double.BYTES;
				break;
			}
			case float32:
			{
				len = Float.BYTES;
				break;
			}
			case string:
			{
				//len = Character.BYTES;
				
				Charset c = Charset.forName( "UTF-8" );
				
				len = ( "A" ).getBytes( c ).length;
				break;
			}
			case int8:
			{
				len = Byte.BYTES;
				break;
			}
			case int16:
			{
				len = Short.BYTES;
				break;
			}
			case int32:
			{
				len = Integer.BYTES;
				break;
			}			
			case int64:
			{
				len = Long.BYTES;
				break;
			}
			default: //undefined
			{
				len = -1;
				
				break;
			}
		}
		
		return len;
	}
	
	public static int getTimeMarkBytes( )
	{
		return getDataTypeBytes( getTimeMarkType() );
	}
	
	public static StreamDataType getTimeMarkType( )
	{
		return StreamDataType.double64;
	}
	
	public static StreamDataType getStringLengthDataType( )
	{
		return StreamDataType.double64;
	}
	
	public static int numberOfRepeatedtNodoName( XMLElement child , String label )
	{		
		int countEq = 0;
		while( child != null && !child.name().isEmpty() )
		{
			String name = child.name().toLowerCase();
			if( name.equals( label ) )
			{
				countEq++;
			}
			
			child = child.next_sibling();
		}					
		
		return countEq;
	}
	
	public static XMLElement findFirstXMLNode( XMLElement desc, String NodeName )
	{		
		XMLElement child = desc.first_child();
		if( child != null )
		{
			String name = child.name().toLowerCase();
			
			if( !name.isEmpty() )
			{
				if( !name.equals( NodeName.toLowerCase() ) )
				{
					child = findFirstXMLNode( child.next_sibling(), NodeName );
				}
			}
		}
		
		return child;
	}
	
	public static void removeNode( LSLStreamInfo stream, String childNode )
	{
		if( stream != null )
		{
			XMLElement parent = stream.desc();
										
			parent.remove_child( childNode );
		}
	}
	
	public static void addNode( LSLStreamInfo stream, StringTuple child )
	{
		if( child != null && stream != null )
		{
			XMLElement parent = stream.desc();

			parent.append_child_value( child.t1, child.t2 );
		}
	}
	
	public static void addNodes( LSLStreamInfo stream, List< StringTuple > childNodes )
	{
		if( childNodes != null && stream != null )
		{
			for( StringTuple child : childNodes )
			{
				addNode( stream, child );
			}
		}
	}
}
