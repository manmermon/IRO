package GUI;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jfugue.midi.MidiDictionary;

import image.imagenPoligono2D;

public class AppIcons extends GeneralAppIcon 
{
	public static Image appIcon( int size )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );

		int thick = size/8;

		imagenPoligono2D.crearImagenOvalo( -size + thick, -1, 2*size - (3*thick)/2, size, thick, Color.ORANGE, Color.WHITE, img );
		imagenPoligono2D.crearImagenLinea( thick/2 - 1, 0, thick/2 - 1, size, thick, Color.ORANGE, img );

		Image quaver = Quaver( size - (int)(4.25 * thick), size - (int)(4.25 * thick), Color.BLACK, null );

		imagenPoligono2D.componerImagen( img, (int)( 1.1 * thick ), ( size - quaver.getHeight( null ) ) /2, quaver );

		return img;
	}

	public static Image getInstrument( List< Byte > idInstruments, int size, Color c )
	{
		Set< String > instr = new TreeSet< String >();
		
		for( Byte val : idInstruments )
		{
			if( MidiDictionary.INSTRUMENT_BYTE_TO_STRING.containsKey( val ) )
			{
				if( val < 8 ) // Piano 
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)0 ) );
				}
				else if( ( val > 7 && val < 16 ) || ( val == 108 ) ) // Chromatic Percussion
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)13 ) );
				}
				else if( val > 15 && val < 24 ) // Organ
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)19 ) );
				}
				else if( ( val > 24 && val < 32 ) || ( val > 103 && val < 108 ) ) // Guitar
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)24 ) );
				}
				else if( val > 31 && val < 40 ) // Bass
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)43 ) );
				}
				else if( ( val > 39 && val < 48 ) || ( val == 110 ) ) // Strings
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)40 ) );
				}
				else if( val > 47 && val < 56 ) // Ensemble
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)48 ) );
				}
				else if( val == 56 ) // Trumpet
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)val ) );
				}
				else if( val > 56 && val < 64 ) // Brass
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)57 ) );
				}
				else if( val > 63 && val < 68 ) // Sax 
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)57 ) );
				}
				else if( ( val > 67 && val < 78 ) || ( val == 109 ) || ( val == 111 ) ) // Pipe
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)73 ) );
				}
				else if( val == 78 ) // Whistle
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)val ) );
				}
				else if( val == 79 ) // Ocarina
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)val ) );
				}
				else if( val == 112 ) // Bell 
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)val ) );
				}
				else if( val == 113 ) // Agogo
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)val ) );
				}
				else if( val > 113 && val < 120 ) // Percussive
				{
					instr.add(  MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get( (byte)114 ) );
				}
				else
				{
					instr.add( "undefined" );
				}
			}
		}
		
		return getInstrument( instr , size, c );
	}
	
	public static Image getInstrument( Set< String > idInstruments, int size, Color c )
	{
		Image img =  imagenPoligono2D.crearImagenCirculo( 0, 0, size, c, null );
		
		if( idInstruments != null && !idInstruments.isEmpty() )
		{
			List< String > knowInstrs = new ArrayList< String >();
			for( String inst : idInstruments )
			{
				if( MidiDictionary.INSTRUMENT_STRING_TO_BYTE.containsKey( inst.toUpperCase() ) )
				{
					knowInstrs.add( inst );
				}
			}
			
			if( !knowInstrs.isEmpty() )
			{
				img = imagenPoligono2D.crearLienzoVacio( size, size, null );
				List< Point > locs = new ArrayList<Point>();
				
				int sizeInst = size / 2 - 1;
				if( knowInstrs.size() == 1 )
				{
					sizeInst = size;
				}
				else if( knowInstrs.size() > 4 )
				{
					sizeInst = size / 3  -1;
				}
				
				switch ( knowInstrs.size() ) 
				{
					case 1:
					{
						locs.add( new Point() );
						break;
					}
					case 2:
					{
						locs.add( new Point( 0, 0 ) );
						locs.add( new Point( size - sizeInst, size - sizeInst) );
						
						break;
					}
					case 3:
					{	
						locs.add( new Point( ( size - sizeInst ) / 2, 0 ) );
						locs.add( new Point( 0, size - sizeInst ) );
						locs.add( new Point( size - sizeInst, size - sizeInst ) );
						
						break;
					}
					case 4:
					{
						locs.add( new Point( 0, 0 ) );
						locs.add( new Point( size - sizeInst, 0 ) );
						locs.add( new Point( 0, size - sizeInst ) );
						locs.add( new Point( size - sizeInst, size - sizeInst ) );
						
						break;
					}
					case 5:
					{
						locs.add( new Point( 0, 0 ) );
						locs.add( new Point( size - sizeInst, 0) );
						locs.add( new Point( ( size - sizeInst ) / 2, ( size - sizeInst ) / 2 ) );
						locs.add( new Point( 0, size - sizeInst) );
						locs.add( new Point( size - sizeInst, size - sizeInst ) );
						
						break;
					}
					case 6:
					{
						locs.add( new Point( 0, 0 ) );
						locs.add( new Point( size - sizeInst, 0) );
						locs.add( new Point( sizeInst / 2, ( size - sizeInst ) / 2 ) );
						locs.add( new Point( size - sizeInst - sizeInst / 2, ( size - sizeInst ) / 2 ) );
						locs.add( new Point( 0, size - sizeInst) );
						locs.add( new Point( size - sizeInst, size - sizeInst ) );
						
						break;
					}
					case 7:
					{
						locs.add( new Point( 0, 0 ) );
						locs.add( new Point( (size - sizeInst) / 2, 0) );
						locs.add( new Point( size - sizeInst, 0) );
						locs.add( new Point( 0, (size - sizeInst) / 2 ) );
						locs.add( new Point( (size - sizeInst) / 2, (size - sizeInst) / 2) );
						locs.add( new Point( size - sizeInst, (size - sizeInst) / 2) );
						locs.add( new Point( (size - sizeInst) / 2, size - sizeInst) );
						
						break;
					}
					case 8:
					{
						locs.add( new Point( 0, 0 ) );
						locs.add( new Point( (size - sizeInst) / 2, 0) );
						locs.add( new Point( size - sizeInst, 0) );
						locs.add( new Point( 0, (size - sizeInst) / 2 ) );
						locs.add( new Point( (size - sizeInst) / 2, (size - sizeInst) / 2) );
						locs.add( new Point( size - sizeInst, (size - sizeInst) / 2) );
						locs.add( new Point( 0, size - sizeInst) );
						locs.add( new Point( size - sizeInst, size - sizeInst ) );
						
						break;
					}
					default:
					{
						locs.add( new Point( 0, 0 ) );
						locs.add( new Point( (size - sizeInst) / 2, 0) );
						locs.add( new Point( size - sizeInst, 0) );
						locs.add( new Point( 0, (size - sizeInst) / 2 ) );
						locs.add( new Point( (size - sizeInst) / 2, (size - sizeInst) / 2) );
						locs.add( new Point( size - sizeInst, (size - sizeInst) / 2) );
						locs.add( new Point( 0, size - sizeInst) );
						locs.add( new Point( (size - sizeInst) / 2, size - sizeInst ) );
						locs.add( new Point( size - sizeInst, size - sizeInst ) );
						
						break;
					}
				}
				
				int count = 0;
				for( String inst : knowInstrs )
				{
					Image imgInst = getInstrument( inst.toLowerCase(), sizeInst, c );
					
					Point loc = locs.get( count );
					
					imagenPoligono2D.componerImagen( img, loc.x, loc.y, imgInst );
					
					count++;
					if( count > 8 )
					{
						break;
					}
				}
			}
		}
		
		return img;
	}
	
	public static Image getInstrument( String idInstrument, int size, Color c )
	{
		Image img = null;

		switch( idInstrument.toLowerCase() )
		{
			case "piano":
			{
				img = Piano( size, c );
				break;
			}
			case "church_organ":
			{
				img = Organ( size, c );
				break;
			}
			case "guitar":
			{
				img = Guitar(size, c);
				break;
			}
			case "whistle":
			{
				img = Whistle(size, c);
				break;
			}
			case "tinkle_bell":
			{
				img = Bell(size, c);
				break;
			}
			case "agogo":
			{
				img = Maraca(size, c);
				break;
			}
			case "contrabass":
			{
				img = Contrabass(size, c);
				break;
			}
			case "soprano_sax":
			{
				img = Sax( size, c);
				break;
			}
			case "violin":
			{
				img = Violin(size, c);
				break;
			}
			case "string_ensemble_1":
			{
				img = StringEnsemble( size, c);
				break;
			}
			case "ocarina":
			{
				img = Ocarina(size, c);
				break;
			}
			case "flute":
			{
				img = Flute(size, c);				
				break;
			}
			case "trombone":
			{
				img = Trombone(size, c);
				break;
			}
			case "xylophone":
			{
				img = Xylophone(size, c);
				break;
			}
			case "trumpet":
			{
				img = Trumpet(size, c);
				break;
			}
			case "steel_drums":
			{
				img = Drum(size, c);
				break;
			}			
			default:
			{
				img = imagenPoligono2D.crearImagenCirculo( 0, 0, size, c, null );// Quaver( size, size, c, c );
				break;
			}
		}
		

		return img;
	}

	public static Image Piano( int size, Color c )
	{	
		int thicknessBorder = size / 16;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}

		Image img = imagenPoligono2D.crearImagenRectangulo( size, size, thicknessBorder, c, Color.WHITE );

		double w = img.getWidth( null ) / 4.0 - thicknessBorder/2;

		double t4 = thicknessBorder / 4;

		double p = 0;
		for( int i = 0; i < 4; i++ )
		{				
			int x = (int)( p + thicknessBorder / 2);
			imagenPoligono2D.crearImagenLinea( x, 0, x, img.getHeight( null ), thicknessBorder, c, img );

			p += w + t4;
		}

		Image black = imagenPoligono2D.crearImagenRectangulo( img.getWidth( null ) / 6, img.getHeight( null ) / 2, 1, c, c );
		p = p - ( w + t4);
		for( int i = 0; i < 3; i++ )
		{
			imagenPoligono2D.componerImagen( img, (int)(p - black.getWidth( null ) / 2 + thicknessBorder / 2), thicknessBorder, black );

			p -= (w + t4);
		}

		return img;
	}

	public static Image Flute( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );

		int divSize = size / 10;

		if( divSize < 1 )
		{
			divSize = 1;
		}

		int[] xs = new int[] { 0, size - 2 * divSize, size - divSize / 2, size
				,size - divSize / 2, divSize + divSize / 2 };
		int[] ys = new int[] { size - divSize - divSize / 2, divSize /2, 0
				, divSize / 2, divSize * 2, size };

		imagenPoligono2D.crearImagenPoligonoRelleno( xs, ys, c, img );


		//Color holeColor = new Color( ~c.getRGB() );
		Color holeColor = Color.WHITE;
		float[] hsb = new float[ 3 ];
		Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), hsb );
		
		if( hsb[ 1 ] < 0.25 )
		{
			holeColor = Color.BLACK;
		}
		
		
		int x = divSize;
		int y = size - 2 * divSize;

		for( int i = 0; i < 6; i++ )
		{
			imagenPoligono2D.crearImagenCirculo( x, y, divSize, holeColor, img );
			x += divSize;
			y -= divSize;
		}

		xs = new int[] { size - 2 * divSize - divSize / 2
				, size - divSize - divSize / 2
				, size - divSize
				, size - divSize*2
				//, size - (int)( 0.2 * divSize ) - divSize
				//, size - (int)( 0.5 * divSize ) - divSize
		};
		ys = new int[] { divSize * 2
				, divSize
				, divSize + divSize / 2
				, 2 * divSize + divSize/2
				//, (int)( 1.5 * divSize )
				//, (int)( 1.2 * divSize )
		};

		imagenPoligono2D.crearImagenPoligonoRelleno(xs, ys, holeColor, img);

		return img;
	}

	public static Image Xylophone( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );

		int thicknessBorder = size / 32;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}

		int numPiece = 4;

		int pieceWitdh = size / numPiece - thicknessBorder;
		int divStepY = size / ( 3 * numPiece ) - thicknessBorder / 2;

		if( pieceWitdh < 1 )
		{
			pieceWitdh = 1;
		}

		if( divStepY < 1 )
		{
			divStepY = 1;
		}

		int x1 = 0;
		int x2 = x1 + pieceWitdh;
		int y1 = 0;
		int y2 = size - y1;
		int pH = size / 3 ;
		int r = pieceWitdh / 3;
		//Color nc = new Color( ~c.getRGB() );
		
		Color nc = Color.WHITE;
		float[] hsb = new float[ 3 ];
		Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), hsb );
		
		if( hsb[ 1 ] < 0.25 )
		{
			nc = Color.BLACK;
		}
		
		
		for( int i = 0; i < numPiece; i++ )
		{
			imagenPoligono2D.crearImagenPoligonoRelleno( new int[] { x1, x2, x2, x1}
			, new int[] { y1, y1, y2, y2}
			, c, img );

			imagenPoligono2D.crearImagenCirculo( (x1 + x2)/2 - r / 2, pH - r / 2,
					r, nc, img);
			imagenPoligono2D.crearImagenCirculo( (x1 + x2)/2 - r / 2, size - pH - r
					/ 2, r, nc, img);

			x1 = x2 + thicknessBorder;
			x2 = x1 + pieceWitdh;
			y1 += divStepY;
			y2 = size - y1;
		}

		thicknessBorder = 2 * thicknessBorder;
		imagenPoligono2D.crearImagenLinea( size / 2, size / 2, size -
				thicknessBorder, size - thicknessBorder, thicknessBorder , c, img );

		r = 3 * thicknessBorder;
		imagenPoligono2D.crearImagenCirculo( ( size - r) /2, ( size - r) / 2, r,
				c, img );

		thicknessBorder = (int)( 0.9 * thicknessBorder );
		imagenPoligono2D.crearImagenLinea( size / 2, size / 2, size -
				(int)(thicknessBorder * 1.2 ), size - (int)(thicknessBorder * 1.2 ),
				thicknessBorder , nc, img );
		r = 3 * thicknessBorder;
		imagenPoligono2D.crearImagenCirculo( ( size - r) /2, ( size - r) / 2, r,
				nc, img );

		return img;
	}

	public static Image Trumpet( int size, Color c)
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );

		Image tr = imagenPoligono2D.crearImagenPoligonoRelleno( new int[] { 0, size/4, size/4 }
																, new int[] { size / 4, 0, size / 2 }
																, c, null );

		int thicknessBorder = tr.getHeight( null ) / 4;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}

		int y = tr.getHeight( null ) / 2;
		imagenPoligono2D.crearImagenLinea( 0, y, size, y, thicknessBorder, c,
				img );
		imagenPoligono2D.componerImagen( img, size - tr.getWidth( null ), 0, tr );

		imagenPoligono2D.crearImagenLinea( thicknessBorder / 4 - 1, y - thicknessBorder / 2
										, thicknessBorder / 4 - 1, y + thicknessBorder / 2, thicknessBorder / 2
										, c , img );
		
		int w = size - tr.getWidth( null ) - thicknessBorder;
		int h = size - tr.getHeight( null ) /2 - thicknessBorder;
		imagenPoligono2D.crearImagenRectanguloRedondo( ( int )( thicknessBorder ) 
													, y
													, w
													, h
													, w / 2
													, (int)( 0.9 * h ) 
													, thicknessBorder
													, c, null, img );

		int x = ( int )( 1 * thicknessBorder ) + w / 2;  
		imagenPoligono2D.crearImagenLinea( x, y - thicknessBorder / 2, x, y + h
				+ thicknessBorder / 2,  (int)( 0.65 * thicknessBorder ), c, img );
		imagenPoligono2D.crearImagenLinea( x, y - thicknessBorder, x, y - thicknessBorder / 2
											, thicknessBorder / 4, c, img );
		imagenPoligono2D.crearImagenLinea( x - thicknessBorder / 3, y - thicknessBorder - thicknessBorder / 4
											, x + thicknessBorder / 3, y - thicknessBorder - thicknessBorder / 4
											,  thicknessBorder / 4, c, img );
		
		
		x += (int)( 1.25 * thicknessBorder );
		imagenPoligono2D.crearImagenLinea( x, y - thicknessBorder / 2, x, y + h
				+ thicknessBorder / 2,  (int)( 0.65 * thicknessBorder ), c, img );
		imagenPoligono2D.crearImagenLinea( x, y - thicknessBorder, x, y - thicknessBorder / 2
											, thicknessBorder / 4, c, img );
		imagenPoligono2D.crearImagenLinea( x - thicknessBorder / 3, y - thicknessBorder - thicknessBorder / 4
											, x + thicknessBorder / 3, y - thicknessBorder - thicknessBorder / 4
											,  thicknessBorder / 4, c, img );
		
		x -=  (int)(  2.5 * thicknessBorder );
		imagenPoligono2D.crearImagenLinea( x, y - thicknessBorder / 2, x, y + h
				+ thicknessBorder / 2, (int)( 0.65 * thicknessBorder ), c, img );
		imagenPoligono2D.crearImagenLinea( x, y - thicknessBorder, x, y - thicknessBorder / 2
											, thicknessBorder / 4, c, img );
		imagenPoligono2D.crearImagenLinea( x - thicknessBorder / 3, y - thicknessBorder - thicknessBorder / 4
											, x + thicknessBorder / 3, y - thicknessBorder - thicknessBorder / 4
											,  thicknessBorder / 4, c, img );
		
		
		imagenPoligono2D.componerImagen( imagenPoligono2D.crearLienzoVacio( size, size, null )
												, 0, ( size - img.getHeight( null ) ) / 2
												, img );
		
				
		return img;
	}
	
	public static Image Drum( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );
		
		//Color borderColor = new Color( ~c.getRGB() );
				
		Color borderColor = Color.WHITE;
		float[] hsb = new float[ 3 ];
		Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), hsb );
		
		if( hsb[ 1 ] < 0.25 )
		{
			borderColor = Color.BLACK;
		}
		
		int divStep = size / 10;
		
		int thicknessBorder = size / 32;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}
		
		int ovalHeigh = divStep * 4;
		
		
		imagenPoligono2D.crearImagenOvalo( 0, size - ovalHeigh, size, ovalHeigh, thicknessBorder, c, c, img );
		imagenPoligono2D.crearImagenPoligonoRelleno( new int[] { size, 0, 0, size }
													, new int[] { size - ovalHeigh / 2, size - ovalHeigh / 2
													, size - ( 3 * ovalHeigh ) / 2, size - ( 3 * ovalHeigh ) / 2}
													, c, img);
		
		imagenPoligono2D.crearImagenOvalo( 0, size - 2 * ovalHeigh, size, ovalHeigh, thicknessBorder, borderColor, borderColor, img );
		imagenPoligono2D.crearImagenOvalo( 0, size - (int)( 2.05 * ovalHeigh ), size, ovalHeigh, thicknessBorder, c, borderColor, img );
		
		int y = size - (int)( 2.05 * ovalHeigh );
		imagenPoligono2D.crearImagenPunto( ( 2 * size ) / 3, y - 4 * thicknessBorder, thicknessBorder * 3, c, true, img );
		imagenPoligono2D.crearImagenLinea( ( 2 * size ) / 3 + thicknessBorder, y - 4 * thicknessBorder + thicknessBorder
											, size - thicknessBorder, thicknessBorder, thicknessBorder
											, c, img );
		
		imagenPoligono2D.crearImagenPunto( size / 3, y - 4 * thicknessBorder, thicknessBorder * 3, c, true, img );
		imagenPoligono2D.crearImagenLinea( size / 3 + thicknessBorder, y - 4 * thicknessBorder + thicknessBorder
											, thicknessBorder, thicknessBorder, thicknessBorder
											, c, img );
		
		return img;
	}

	public static Image Trombone( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );
		
		int thicknessBorder = size / 10;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}
						
		double gridSize = size / 8;
		int startAngle = 90;
		int arcAngle = 180;
		double arcSize = 3.25  *gridSize;
		
		Image tr = imagenPoligono2D.crearImagenTriangulo( (int)( gridSize * 3), 1, c, c, imagenPoligono2D.WEST );

		Image curv = imagenPoligono2D.crearImagenArco( 0, 0, (int)arcSize, (int)arcSize, startAngle, arcAngle, thicknessBorder, c, null, null );
		Image curv2 = imagenPoligono2D.crearImagenArco( 0, 0, (int)arcSize, (int)arcSize, -startAngle, arcAngle, thicknessBorder, c, null, null );
		
		imagenPoligono2D.componerImagen( img, size - tr.getWidth( null ), 0, tr );
		
		imagenPoligono2D.crearImagenLinea( (int)( arcSize / 2 ) + thicknessBorder, tr.getHeight( null ) / 2
											, size - tr.getWidth( null ) / 2, tr.getHeight( null ) / 2
											, thicknessBorder, c, img );
		
		imagenPoligono2D.componerImagen( img, 0, tr.getHeight( null ) / 2 - thicknessBorder / 2, curv );
		
		imagenPoligono2D.crearImagenLinea( curv.getWidth( null ) /2 + thicknessBorder
											, tr.getHeight( null ) / 2 + curv.getHeight( null ) - thicknessBorder
											, size - tr.getWidth( null ) 
											, tr.getHeight( null ) / 2 + curv.getHeight( null ) - thicknessBorder 
											, thicknessBorder, c, img );
		
		imagenPoligono2D.componerImagen( img, size - tr.getWidth( null ) - curv.getWidth( null ) / 2 + thicknessBorder/ 2
										, tr.getHeight( null ) / 2 + curv.getHeight( null ) - thicknessBorder - thicknessBorder / 2, curv2 );
		
		imagenPoligono2D.crearImagenLinea( curv.getWidth( null ) /4 
											, tr.getHeight( null ) / 2 + curv.getHeight( null ) - 2*thicknessBorder + curv2.getHeight( null )
											, size - tr.getWidth( null )
											, tr.getHeight( null ) / 2 + curv.getHeight( null ) - 2*thicknessBorder  + curv2.getHeight( null )
											, thicknessBorder, c, img );
		
		/****/
		imagenPoligono2D.crearImagenLinea( curv.getWidth( null ) /4
											, tr.getHeight( null ) / 2 + curv.getHeight( null ) - 2*thicknessBorder + curv2.getHeight( null ) - thicknessBorder / 2
											, curv.getWidth( null ) /4
											, tr.getHeight( null ) / 2 + curv.getHeight( null ) - 2*thicknessBorder  + curv2.getHeight( null ) + thicknessBorder / 2
											, thicknessBorder, c, img );
		
		
		imagenPoligono2D.crearImagenLinea( size /2 - thicknessBorder / 2
											, tr.getHeight( null ) / 2 + curv.getHeight( null ) - thicknessBorder
											, size / 2 - thicknessBorder / 2
											, tr.getHeight( null ) / 2 + curv.getHeight( null ) - 2*thicknessBorder + curv2.getHeight( null )
											, thicknessBorder, c, img );
	
		imagenPoligono2D.crearImagenLinea( curv.getWidth( null ) / 2 + thicknessBorder / 2
											, tr.getHeight( null ) / 2
											, curv.getWidth( null ) / 2 + thicknessBorder / 2
											, tr.getHeight( null ) / 2 + curv.getHeight( null ) - thicknessBorder
											, thicknessBorder, c, img );
		
		imagenPoligono2D.crearImagenLinea( size - tr.getWidth( null ) - thicknessBorder
											, tr.getHeight( null ) / 2
											, size - tr.getWidth( null ) - thicknessBorder
											, tr.getHeight( null ) / 2 + curv.getHeight( null ) - thicknessBorder
											, thicknessBorder, c, img );
		
		return img;
	}
	
	public static Image Organ( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );
		
		int thicknessBorder = size / 10;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}
		
		Color holeColor = Color.WHITE;
		float[] hsb = new float[ 3 ];
		Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), hsb );
		
		if( hsb[ 1 ] < 0.25 )
		{
			holeColor = Color.BLACK;
		}		
		
		int tubeNum = 5;
		
		int pad = thicknessBorder;
		
		int tubePad = 1;
		int tubeWidth = size / tubeNum - tubePad;
		int tubeHeight = size  / 2;		
		int tubeArc = tubeWidth / 3;		
		
		if( tubeWidth < 1 )
		{
			tubeWidth = 1;
		}
		
		if( tubeHeight < 1 )
		{
			tubeHeight = 1;
		}		
		
		for( int i = 0; i < tubeNum; i++ )
		{ 
			Image tubeTop = imagenPoligono2D.crearImagenOvalo( 0, 0, tubeWidth, tubeWidth / 2, 1, c, c, null );
			int holeWidth =  (2 * tubeWidth ) / 3;
			int holeHeight = tubeWidth / 3;
			if( holeWidth < 1 )
			{
				holeWidth = 1;
			}
			
			if( holeHeight < 1 )
			{
				holeHeight = 1;
			}
			Image tubeTopHole = imagenPoligono2D.crearImagenOvalo( 0, 0, holeWidth, holeHeight, 1, holeColor, holeColor, null );
			
			Image tube = imagenPoligono2D.crearImagenRectanguloRedondo( 0, 0, tubeWidth, tubeHeight - tubeTop.getHeight( null ) / 2 + tubeArc / 2, tubeArc, tubeArc, 1, c, c, null );
			
			
			imagenPoligono2D.crearImagenOvalo( tube.getWidth( null ) / 3, tube.getHeight( null ) - (int)( 1.5 * tubeArc )
															, tube.getWidth( null ) / 3, tubeArc / 2
															, 1, holeColor, holeColor, tube );
			
			imagenPoligono2D.componerImagen( tubeTop, ( tubeTop.getWidth( null ) - tubeTopHole.getWidth( null ) ) / 2
													, ( tubeTop.getHeight( null ) - tubeTopHole.getHeight( null ) ) / 2
													, tubeTopHole );
			
			imagenPoligono2D.componerImagen( img, i * ( tubeWidth + tubePad) , tubeTop.getHeight( null ) / 2 - tubeArc / 2, tube );
			imagenPoligono2D.componerImagen( img, i * ( tubePad + tubeWidth ) , 0, tubeTop );
		}
		
		Image keyboard = imagenPoligono2D.crearImagenRectangulo( size, size - tubeHeight - tubePad, thicknessBorder, c, holeColor );
		
		int keyWidth = keyboard.getWidth( null ) / 8;
		Image blackKeys = imagenPoligono2D.crearImagenRectangulo( keyWidth - pad / 2, ( 2 * keyboard.getHeight( null ) ) / 3 - pad / 4
																	, thicknessBorder, c, c );
		
		imagenPoligono2D.componerImagen( keyboard, keyWidth, 0, blackKeys );
		imagenPoligono2D.componerImagen( keyboard, 2 * keyWidth, 0, blackKeys );
		imagenPoligono2D.componerImagen( keyboard, 3 * keyWidth, 0, blackKeys );
		imagenPoligono2D.componerImagen( keyboard, 5 * keyWidth, 0, blackKeys );
		imagenPoligono2D.componerImagen( keyboard, 6 * keyWidth, 0, blackKeys );
		
		imagenPoligono2D.componerImagen( img, 0, size - keyboard.getHeight( null ), keyboard );
		
		return img;
	}

	public static Image Guitar( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );
		
		int thicknessBorder = size / 16;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}
		
		Color c2 = Color.WHITE;
		float[] hsb = new float[ 3 ];
		Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), hsb );
		
		if( hsb[ 1 ] > 0.75 )
		{
			c2 = Color.BLACK;
		}		
		
		
		int mast = thicknessBorder * 2;
		int mastHead = thicknessBorder * 3; 
		imagenPoligono2D.crearImagenLinea( size - mast, mast, size / 2, size / 2, mast, c, img );
		imagenPoligono2D.crearImagenLinea(  size - mast, mast,  size - mast - mastHead / 10, mast + mastHead / 10, mastHead, c, img );
		
		
		int r = (int)( 0.6 *  size );
		int r2 = ( 3 * r ) / 4;
		int r3 = ( 4 * r2 ) / 10;
		
		Image circ1 = imagenPoligono2D.crearImagenCirculo( 0, 0, r, c, null );
		Image circ2 = imagenPoligono2D.crearImagenCirculo( 0, 0, r2, c, null );
		
		imagenPoligono2D.componerImagen( img, 0, size - circ1.getHeight( null ), circ1 );
		imagenPoligono2D.componerImagen( img, circ1.getWidth( null ) / 2 - circ2.getWidth( null ) / 8
											, size - circ1.getHeight( null ) 
												- (int)( Math.cos( Math.PI / 4) *  circ2.getHeight( null ) / 2 ) 
												+ circ2.getHeight( null ) / 8
											, circ2 );
		
		imagenPoligono2D.crearImagenCirculo( circ1.getWidth( null ) / 2 - circ2.getWidth( null ) / 8 + circ2.getWidth( null ) / 2 - r3 / 2
												, size - circ1.getHeight( null ) 
													- (int)( Math.cos( Math.PI / 4) *  circ2.getHeight( null ) / 2 ) 
													+ circ2.getHeight( null ) / 8 
													+ circ2.getHeight( null ) / 2
													- r3 / 2
												, r3, c2, img );
		
		imagenPoligono2D.crearImagenLinea( 3 * thicknessBorder, size - r + 3*thicknessBorder
											, r - 3 * thicknessBorder, size - 3*thicknessBorder, thicknessBorder, c2, img );
				
				
		return img;
	}

	public static Image Contrabass( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );
		
		int thicknessBorder = size / 10;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}
		
		Color c2 = Color.WHITE;
		float[] hsb = new float[ 3 ];
		Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), hsb );
		
		if( hsb[ 1 ] < 0.25 )
		{
			c2 = Color.BLACK;
		}		
		
		int w = (int)( 0.60 * size );
		int w2 = (int)( 0.75 * size );
		
		if( w < 1 )
		{
			w = 1;
		}
		
		if( w2 < 1 )
		{
			w2 = 1;
		}
		
		int pad = (int)( 1.05 * thicknessBorder );
		
		Image topCirc = imagenPoligono2D.crearImagenArco( 0, 0, w, w, 0, 180, 1, c, c, null );
		Image butCirc = imagenPoligono2D.crearImagenArco( 0, 0, w2, w2, 180, 180, 1, c, c, null );
		Image fillPad = imagenPoligono2D.crearImagenRectangulo( w, pad, 1, c, c );
		
		Image leftCurve = imagenPoligono2D.crearImagenArco( 0, 0
															, ( butCirc.getWidth( null ) - topCirc.getWidth( null ) ), pad * 2 + thicknessBorder /2
															, 270, 90, thicknessBorder / 2, c, null, null);
		
		int r = (int)( 0.75 * thicknessBorder );
		if( r < 1 )
		{
			r = 1;
		}
		Image circ = imagenPoligono2D.crearImagenCirculo( 0, 0, r, c2, null );
		
		Image rigthCurve = imagenPoligono2D.crearImagenArco( 0, 0
														, ( butCirc.getWidth( null ) - topCirc.getWidth( null ) ), pad * 2 + thicknessBorder /2
														, 180, 90, thicknessBorder / 2, c, null, null);
				
		imagenPoligono2D.componerImagen( img, 0, size - butCirc.getHeight( null), butCirc );
		
		imagenPoligono2D.componerImagen( img, (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2
											,  size - butCirc.getHeight( null) / 2 - topCirc.getHeight( null ) / 2 - pad
											, topCirc );
		
		imagenPoligono2D.componerImagen( img, (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2
											,  size - butCirc.getHeight( null) / 2 - fillPad.getHeight( null ) 
											, fillPad );
		
		imagenPoligono2D.componerImagen( img, (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 - leftCurve.getWidth( null ) + thicknessBorder / 2
										, size - butCirc.getHeight( null)/2 - leftCurve.getHeight( null ) + thicknessBorder / 2, leftCurve );
		
		imagenPoligono2D.componerImagen( img, butCirc.getWidth( null) - rigthCurve.getWidth( null ) + thicknessBorder / 2
										, size - butCirc.getHeight( null)/2 - rigthCurve.getHeight( null ) + thicknessBorder / 2, rigthCurve );
		
				
		imagenPoligono2D.crearImagenLinea( (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + topCirc.getWidth( null ) / 2
											, 0
											, (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + topCirc.getWidth( null ) / 2
											, size / 2, (int)( 0.75 * thicknessBorder ), c, img );		
		
		imagenPoligono2D.crearImagenLinea( (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + topCirc.getWidth( null ) / 2
												, 0
												, (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + topCirc.getWidth( null ) / 2
												, thicknessBorder, thicknessBorder, c, img );
		
		imagenPoligono2D.crearImagenLinea(  size - thicknessBorder
											, 4 * thicknessBorder
											, size - thicknessBorder
											, size - thicknessBorder, thicknessBorder / 2, c, img );
		
		
		
		
		
		imagenPoligono2D.componerImagen( img, butCirc.getWidth( null )/3  - circ.getWidth( null ) / 2
											, size - butCirc.getHeight( null) / 2 - fillPad.getHeight( null )
											, circ );
		
		imagenPoligono2D.componerImagen( img, butCirc.getWidth( null ) / 3 - circ.getWidth( null )   
												, size - butCirc.getHeight( null) / 3
												, circ );
		
		imagenPoligono2D.crearImagenLinea( butCirc.getWidth( null ) / 3 + r/4 - circ.getHeight( null ) / 2
											, size - butCirc.getHeight( null) / 2 - fillPad.getHeight( null ) + circ.getHeight( null ) / 2 + r/ 4
											, butCirc.getWidth( null ) / 3 + r/4 - circ.getHeight( null ) /2
											, size - butCirc.getHeight( null) / 3 + circ.getHeight( null ) / 2 - r/4
											, r/ 2, c2, img );
		
		
		
		
		imagenPoligono2D.componerImagen( img, ( 2 * butCirc.getWidth( null ) ) / 3 - circ.getWidth( null ) / 2
											, size - butCirc.getHeight( null) / 2 - fillPad.getHeight( null )
											, circ );

		imagenPoligono2D.componerImagen( img, ( 2 * butCirc.getWidth( null ) ) / 3    
											, size - butCirc.getHeight( null) / 3
											, circ );

		imagenPoligono2D.crearImagenLinea( ( 2 * butCirc.getWidth( null ) ) / 3 + r /4
												, size - butCirc.getHeight( null) / 2 - fillPad.getHeight( null ) + circ.getHeight( null ) / 2 + r/ 4
												, ( 2 * butCirc.getWidth( null ) ) / 3 + r/4 
												, size - butCirc.getHeight( null) / 3 + circ.getHeight( null ) / 2 - r/4
												, r/ 2, c2, img );
		
		
		return img;
	}
	
	public static Image Violin( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );
		
		int thicknessBorder = size / 10;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}
		
		Color c2 = Color.WHITE;
		float[] hsb = new float[ 3 ];
		Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), hsb );
		
		if( hsb[ 1 ] < 0.25 )
		{
			c2 = Color.BLACK;
		}		
		
		int w = (int)( 0.5 * size );
		int w2 = (int)( 0.5 * size );
		
		int pad = (int)( 2 * thicknessBorder );
		
		if( w < 1 )
		{
			w = 1;
		}
		
		if( w2 < 1 )
		{
			w2 = 1;
		}
					
		
		Image topCirc = imagenPoligono2D.crearImagenArco( 0, 0, w, w, 0, 180, 1, c, c, null );
		Image butCirc = imagenPoligono2D.crearImagenArco( 0, 0, w2, w2, 180, 180, 1, c, c, null );
		Image fillPad = imagenPoligono2D.crearImagenRectangulo( w - thicknessBorder, pad, 1, c, c );
		
		int xShift = ( size - topCirc.getWidth( null ) ) / 4;
		
		int curvBotH = pad;
		if( curvBotH < 1 )
		{
			curvBotH = 1;
		}
		
		int curvW = ( butCirc.getWidth( null ) - topCirc.getWidth( null ) );
		if( curvW < 1 )
		{
			curvW = topCirc.getWidth( null ) / 4; 
		}
			
		
		Image leftCurve = imagenPoligono2D.crearImagenArco( 0, 0
															, curvW
															, curvBotH
															, 270, 180, thicknessBorder / 2, c, null, null);
		
		Image rigthCurve = imagenPoligono2D.crearImagenArco( 0, 0
															, curvW
															, curvBotH
															, 90, 180, thicknessBorder / 2, c, null, null);
		
		int r = (int)( 0.75 * thicknessBorder );
		if( r < 1 )
		{
			r = 1;
		}
		Image circ = imagenPoligono2D.crearImagenCirculo( 0, 0, r, c2, null );
				
				
		imagenPoligono2D.componerImagen( img, xShift + 0, size - butCirc.getHeight( null), butCirc );
		
		imagenPoligono2D.componerImagen( img, xShift + (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2
											,  size - butCirc.getHeight( null) / 2 - topCirc.getHeight( null ) / 2 - pad
											, topCirc );
						
		imagenPoligono2D.componerImagen( img,  xShift + (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + ( topCirc.getWidth( null ) - fillPad.getWidth( null ) ) / 2 
												,  size - butCirc.getHeight( null) / 2 - fillPad.getHeight( null ) 
												, fillPad );
		
		
		imagenPoligono2D.componerImagen( img
										, xShift -leftCurve.getWidth( null ) / 2 + thicknessBorder / 4
										, size - butCirc.getHeight( null)/2 - leftCurve.getHeight( null ) + thicknessBorder / 8
										, leftCurve );

		
		imagenPoligono2D.componerImagen( img, xShift + (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + topCirc.getWidth( null ) - rigthCurve.getWidth( null ) / 2 - thicknessBorder / 4 
										, size - butCirc.getHeight( null)/2 - rigthCurve.getHeight( null ) + thicknessBorder / 8
										, rigthCurve );
		
		
		
		
				
		imagenPoligono2D.crearImagenLinea( xShift +  (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + topCirc.getWidth( null ) / 2
											, 0
											, xShift +  (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + topCirc.getWidth( null ) / 2
											, size / 2, (int)( 0.75 * thicknessBorder ), c, img );		
		
		imagenPoligono2D.crearImagenLinea( xShift +  (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + topCirc.getWidth( null ) / 2
												, 0
												, xShift +  (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + topCirc.getWidth( null ) / 2
												, thicknessBorder, thicknessBorder, c, img );
		
		imagenPoligono2D.crearImagenLinea( xShift +  (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + topCirc.getWidth( null ) / 2 - ( 3 * thicknessBorder ) / 4 
												, thicknessBorder / 4
												, xShift +  (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + topCirc.getWidth( null ) / 2 + ( 3 * thicknessBorder ) / 4 
												, thicknessBorder / 4
												, thicknessBorder / 4, c, img );
		
		imagenPoligono2D.crearImagenLinea( xShift +  (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + topCirc.getWidth( null ) / 2 - ( 3 * thicknessBorder ) / 4
											, thicknessBorder / 4 + thicknessBorder / 2
											, xShift +  (butCirc.getWidth( null ) - topCirc.getWidth( null ) ) / 2 + topCirc.getWidth( null ) / 2 + ( 3 * thicknessBorder ) / 4 
											, thicknessBorder / 4 + thicknessBorder / 2
											, thicknessBorder / 4, c, img );
		
		
		
		imagenPoligono2D.crearImagenLinea( xShift + topCirc.getWidth( null ) + thicknessBorder
											, 3 * thicknessBorder
											, xShift + topCirc.getWidth( null ) + thicknessBorder 
											, size - thicknessBorder / 2
											, thicknessBorder / 2, c, img );
		
		imagenPoligono2D.crearImagenLinea( xShift + topCirc.getWidth( null ) + thicknessBorder
											, (int)( 3.25 * thicknessBorder )
											, xShift + topCirc.getWidth( null ) + thicknessBorder + thicknessBorder / 2 
											, (int)( 3.25  * thicknessBorder )
											, thicknessBorder / 2, c, img );
		
		imagenPoligono2D.crearImagenLinea( xShift +  topCirc.getWidth( null ) + thicknessBorder
										, (int)( size - thicknessBorder * 1.25 )
										, xShift + topCirc.getWidth( null ) + thicknessBorder + thicknessBorder / 2 
										, (int)( size - thicknessBorder * 1.25 )
										, thicknessBorder / 2, c, img );
		
		imagenPoligono2D.crearImagenLinea( xShift + topCirc.getWidth( null ) + thicknessBorder + thicknessBorder / 2
											, (int)( 3.25 * thicknessBorder )
											, xShift + topCirc.getWidth( null ) + thicknessBorder + thicknessBorder / 2 
											, (int)( size - thicknessBorder * 1.25 )
											, thicknessBorder / 4, c, img );
		
		
		imagenPoligono2D.componerImagen( img, xShift + butCirc.getWidth( null )/3  - circ.getWidth( null ) / 2
											, size - butCirc.getHeight( null) / 2 - fillPad.getHeight( null ) - topCirc.getHeight( null ) / 4
											, circ );
		
		imagenPoligono2D.componerImagen( img, xShift + butCirc.getWidth( null ) / 3 - circ.getWidth( null )   
												, size - butCirc.getHeight( null) / 3 - topCirc.getHeight( null ) / 4
												, circ );
		
		imagenPoligono2D.crearImagenLinea( xShift + butCirc.getWidth( null ) / 3 + r/4 - circ.getHeight( null ) / 2
											, size - butCirc.getHeight( null) / 2 - fillPad.getHeight( null ) + circ.getHeight( null ) / 2 + r/ 4 - topCirc.getHeight( null ) / 4
											, xShift + butCirc.getWidth( null ) / 3 + r/4 - circ.getHeight( null ) /2
											, size - butCirc.getHeight( null) / 3 + circ.getHeight( null ) / 2 - r/4 - topCirc.getHeight( null ) / 4
											, r/ 2, c2, img );
		
		
		
		
		imagenPoligono2D.componerImagen( img, xShift + ( 2 * butCirc.getWidth( null ) ) / 3 - circ.getWidth( null ) / 2
											, size - butCirc.getHeight( null) / 2 - fillPad.getHeight( null ) - topCirc.getHeight( null ) / 4
											, circ );

		imagenPoligono2D.componerImagen( img, xShift + ( 2 * butCirc.getWidth( null ) ) / 3    
											, size - butCirc.getHeight( null) / 3 - topCirc.getHeight( null ) / 4
											, circ );

		imagenPoligono2D.crearImagenLinea( xShift + ( 2 * butCirc.getWidth( null ) ) / 3 + r /4
												, size - butCirc.getHeight( null) / 2 - fillPad.getHeight( null ) + circ.getHeight( null ) / 2 + r/ 4 - topCirc.getHeight( null ) / 4
												, xShift +  ( 2 * butCirc.getWidth( null ) ) / 3 + r/4 
												, size - butCirc.getHeight( null) / 3 + circ.getHeight( null ) / 2 - r/4 - topCirc.getHeight( null ) / 4
												, r/ 2, c2, img );
		
		imagenPoligono2D.crearImagenOvalo( xShift + thicknessBorder
											, (int)( size - thicknessBorder * 1.25 )
											, (int)( 1.75 * thicknessBorder)
											, thicknessBorder, 1, c2, c2, img );
		
		return img;
	}
		
	public static Image StringEnsemble( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );
		
		int thicknessBorder = size / 10;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}
		
		Color c2 = Color.WHITE;
		float[] hsb = new float[ 3 ];
		Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), hsb );
		
		if( hsb[ 1 ] < 0.25 )
		{
			c2 = Color.BLACK;
		}
		
		int w = size / 2;
		int h = ( 2 * size ) / 3;
		
		if( w < 1 )
		{
			w = 1;
		}
		
		if( h < 1 )
		{
			h = 1;
		}
		
		Image head = imagenPoligono2D.crearImagenRectangulo( w, h, 1, c, c );
		
		head = imagenPoligono2D.crearImagenPoligonoRelleno( new int[] { 0, w, w, w - w/6, w / 6, 0}
															, new int[] { 0, 0, h - h / 6, h, h, h - h / 6 }
															, c, null );
		
		int r = w / 5;
		if( r < 1 )
		{
			r = 1;
		}
		Image circ = imagenPoligono2D.crearImagenCirculo( 0, 0, r, c2, null );
						
		imagenPoligono2D.componerImagen( head, circ.getWidth( null ) / 2, circ.getWidth( null ), circ );
		imagenPoligono2D.componerImagen( head, circ.getWidth( null ) / 2, (int)( 2.5 * circ.getWidth( null ) ), circ );
		imagenPoligono2D.componerImagen( head, circ.getWidth( null ) / 2, 4 * circ.getWidth( null ), circ );
		
		imagenPoligono2D.componerImagen( head, head.getWidth( null ) - circ.getWidth( null ) - circ.getWidth( null ) / 2, circ.getWidth( null ), circ );
		imagenPoligono2D.componerImagen( head, head.getWidth( null ) - circ.getWidth( null ) - circ.getWidth( null ) / 2, (int)( 2.5 * circ.getWidth( null ) ), circ );
		imagenPoligono2D.componerImagen( head, head.getWidth( null ) - circ.getWidth( null ) - circ.getWidth( null ) / 2, 4 * circ.getWidth( null ), circ );
		
		
		
		
		int fretPad = r / 4;
		
		int wFret = ( 2 * w ) / 3;
		int hFret = ( size - head.getHeight( null ) ) / 2 - fretPad;
		if( wFret < 1 )
		{
			wFret = 1;
		}
		
		if( hFret < 1 )
		{
			hFret = 1;
		}
		 
		Image fret = imagenPoligono2D.crearImagenRectangulo( wFret, hFret, 1, c, c );
		
		
		int ovalW = (int)( 0.75 * r );
		int ovalH = (int)( 1.25 * r );
		if( ovalW < 1 )
		{
			ovalW = 1;
		}
		
		if( ovalH < 1 )
		{
			ovalH = 1;
		}
		
		Image oval = imagenPoligono2D.crearImagenOvalo( 0, 0, ovalW, ovalH, 1, c, c, null );
		
		
		imagenPoligono2D.componerImagen( img
											, ( size - head.getWidth( null ) ) / 2
											, 0
											, head );
		
		
		
		imagenPoligono2D.componerImagen( img
										, ( size - head.getWidth( null ) ) / 2 - (int)( 1.5 * oval.getWidth( null ) )
										, circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2
										, oval );
		
		imagenPoligono2D.crearImagenLinea( ( size - head.getWidth( null ) ) / 2 - (int)( 1.5 * oval.getWidth( null ) ) + ovalW / 2
											, circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2 + ovalH / 2
											, ( size - head.getWidth( null ) ) / 2
											, circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2 + ovalH / 2
											, r / 2, c, img );
		
		imagenPoligono2D.componerImagen( img
										, ( size - head.getWidth( null ) ) / 2 - (int)( 1.5 * oval.getWidth( null ) )
										, (int)( 2.5 * circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2)
										, oval );
		
		imagenPoligono2D.crearImagenLinea( ( size - head.getWidth( null ) ) / 2 - (int)( 1.5 * oval.getWidth( null ) ) + ovalW / 2
											, (int)( 2.5 * circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2) + ovalH / 2
											, ( size - head.getWidth( null ) ) / 2
											, (int)( 2.5 * circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2) + ovalH / 2
											, r / 2, c, img );
		
		imagenPoligono2D.componerImagen( img
										, ( size - head.getWidth( null ) ) / 2 - (int)( 1.5 * oval.getWidth( null ) )
										, (int)( 4 * circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2)
										, oval );
		
		imagenPoligono2D.crearImagenLinea( ( size - head.getWidth( null ) ) / 2 - (int)( 1.5 * oval.getWidth( null ) ) + ovalW / 2
											, (int)( 4 * circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2) + ovalH / 2
											, ( size - head.getWidth( null ) ) / 2
											, (int)( 4 * circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2) + ovalH / 2
											, r / 2, c, img );
		
		
		imagenPoligono2D.componerImagen( img
										, ( size - head.getWidth( null ) ) / 2 + head.getWidth( null ) + (int)( 0.5 * oval.getWidth( null ) )
										, circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2
										, oval );

		imagenPoligono2D.crearImagenLinea( ( size - head.getWidth( null ) ) / 2 + head.getWidth( null )
											, circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2 + ovalH / 2
											, ( size - head.getWidth( null ) ) / 2 + head.getWidth( null ) + oval.getWidth( null )
											, circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2 + ovalH / 2
											, r / 2, c, img );		
		
		imagenPoligono2D.componerImagen( img
										, ( size - head.getWidth( null ) ) / 2  + head.getWidth( null ) + (int)( 0.5 * oval.getWidth( null ) )
										, (int)( 2.5 * circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2)
										, oval );
		
		imagenPoligono2D.crearImagenLinea( ( size - head.getWidth( null ) ) / 2 + head.getWidth( null )
										, (int)( 2.5 * circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2) + ovalH / 2
										, ( size - head.getWidth( null ) ) / 2 + head.getWidth( null ) + oval.getWidth( null )
										, (int)( 2.5 * circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2) + ovalH / 2
										, r / 2, c, img );

		imagenPoligono2D.componerImagen( img
										, ( size - head.getWidth( null ) ) / 2  + head.getWidth( null ) + (int)( 0.5 * oval.getWidth( null ) )
										, (int)( 4 * circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2)
										, oval );
		
		imagenPoligono2D.crearImagenLinea( ( size - head.getWidth( null ) ) / 2 + head.getWidth( null )
										, (int)( 4 * circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2) + ovalH / 2
										, ( size - head.getWidth( null ) ) / 2 + head.getWidth( null ) + oval.getWidth( null )
										, (int)( 4 * circ.getHeight( null ) - ( oval.getHeight( null ) - circ.getHeight( null ) ) / 2) + ovalH / 2
										, r / 2, c, img );
		
		
		imagenPoligono2D.componerImagen( img
											, ( size - fret.getWidth( null ) ) / 2
											, size - fret.getHeight( null )
											, fret);
		
		imagenPoligono2D.componerImagen( img
											, ( size - fret.getWidth( null ) ) / 2
											, size - 2 * fret.getHeight( null ) - fretPad
											, fret);
		
		return img;
	}

	public static Image Sax( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );
		
		int thicknessBorder = size / 10;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}
		
		Color c2 = Color.WHITE;
		float[] hsb = new float[ 3 ];
		Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), hsb );
		
		if( hsb[ 1 ] < 0.25 )
		{
			c2 = Color.BLACK;
		}
		
		int holeW = size / 3;
		int holeH = holeW / 2;
		
		if( holeW < 1 )
		{
			holeW = 1;
		}
		
		if( holeH < 1 )
		{
			holeH = 1;
		}
		
		Image hole = imagenPoligono2D.crearImagenArco( 0, 0, holeW, holeH
														, 0, 360, 1, c, c, null );
	
		int holeW2 = holeW - 10;
		int holeH2 = holeH - 10; 
		if( holeW2 < 1 )
		{
			holeW2 = 1;
		}
		
		if( holeH2 < 1 )
		{
			holeH2 = 1;
		}
		
		Image hole2 = imagenPoligono2D.crearImagenArco( 0, 0, holeW2, holeH2
														, 0, 360, 1, c2, c2, null );
		
		imagenPoligono2D.componerImagen( hole
										, ( hole.getWidth( null ) - hole2.getWidth( null ) ) / 2
										, ( hole.getHeight( null ) - hole2.getHeight( null ) ) / 2
										, hole2 );
		
		Image tr = imagenPoligono2D.crearImagenTriangulo( hole.getWidth( null ), 1, c, c, imagenPoligono2D.SOUTH );
		
		Image butCurve = imagenPoligono2D.crearImagenArco( 0, 0
															, (int)( 1.5 * tr.getWidth( null ) )
															, size / 2
															, 180, 180
															, (int)( thicknessBorder * 2 )
															, c, null, null );
				
		Image vertLin = imagenPoligono2D.crearImagenLinea( thicknessBorder * 2
															, 0
															, thicknessBorder * 2
															, size - butCurve.getHeight( null ) / 2 - thicknessBorder * 3
															, thicknessBorder * 4
															, c, null );
		
		Image circTop = imagenPoligono2D.crearImagenCirculo( 0, 0, vertLin.getWidth( null ), c, null );
		
		int rck = vertLin.getWidth( null ) / 2;
		if( rck < 1 )
		{
			rck = 1;
		}
		
		Image circKey = imagenPoligono2D.crearImagenCirculo( 0, 0, rck, c2, null );
		
		imagenPoligono2D.componerImagen( vertLin
											, vertLin.getWidth( null ) - circKey.getWidth( null )
											, vertLin.getHeight( null ) / 2
											, circKey );
		
		imagenPoligono2D.componerImagen( vertLin
										, vertLin.getWidth( null ) - circKey.getWidth( null )
										, vertLin.getHeight( null ) / 2 - circKey.getHeight( null ) - rck / 4
										, circKey );
		
		imagenPoligono2D.componerImagen( vertLin
										, vertLin.getWidth( null ) - circKey.getWidth( null )
										, vertLin.getHeight( null ) / 2 + circKey.getHeight( null ) + rck / 4
										, circKey );
		
		Image blowCurve = imagenPoligono2D.crearImagenArco( 0, 0
															, circTop.getWidth( null ) * 3
															, circTop.getHeight( null ) * 2
															, 0, -80
															, thicknessBorder / 2
															, c, null, null );
		
		imagenPoligono2D.componerImagen( img
										, size - hole.getWidth( null ) - butCurve.getWidth( null ) / 2 - thicknessBorder / 2
										, size - butCurve.getHeight( null )
										, butCurve );
		
		imagenPoligono2D.componerImagen( img
											, size - hole.getWidth( null ) - butCurve.getWidth( null ) / 2 - thicknessBorder / 2
											, size - butCurve.getHeight( null ) / 2 - vertLin.getHeight( null )
											, vertLin );
		
		imagenPoligono2D.componerImagen( img
											, size - hole.getWidth( null ) - butCurve.getWidth( null ) / 2 - thicknessBorder / 2
											, size - butCurve.getHeight( null ) / 2 - vertLin.getHeight( null ) - circTop.getHeight( null ) / 2
											, circTop );
		
		imagenPoligono2D.componerImagen( img
										, size - hole.getWidth( null ) - butCurve.getWidth( null ) / 2 - thicknessBorder / 2 - blowCurve.getWidth( null ) + thicknessBorder /2
										, size - butCurve.getHeight( null ) / 2 - vertLin.getHeight( null ) - circTop.getHeight( null ) / 2 - blowCurve.getHeight( null ) / 2 + thicknessBorder * 2
										, blowCurve );
				
				
		imagenPoligono2D.componerImagen( img
										, size - tr.getWidth( null ) 
										, size - butCurve.getHeight( null ) / 2 - tr.getHeight( null ) / 2 
										, tr );
		
		imagenPoligono2D.componerImagen( img
											, size - hole.getWidth( null ) 
											,size - butCurve.getHeight( null ) / 2 - tr.getHeight( null ) / 2 - hole.getHeight( null )/ 2 
											, hole );
				
		
				
		
		return img;
	}

	public static Image Ocarina( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );
		
		int thicknessBorder = size / 10;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}
		
		Color c2 = Color.WHITE;
		float[] hsb = new float[ 3 ];
		Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), hsb );
		
		if( hsb[ 1 ] < 0.25 )
		{
			c2 = Color.BLACK;
		}
		
		int w = size / 2;
		if( w < 1 )
		{
			w = 1;
		}
		Image tr = imagenPoligono2D.crearImagenTriangulo( w, 1, c, c, imagenPoligono2D.NORTH );
		
		int r = w / 4;
		if( r < 1 )
		{
			r = 1;
		}
		Image circ = imagenPoligono2D.crearImagenCirculo( 0, 0, r, c2, null );
		
		Image body = imagenPoligono2D.crearImagenOvalo( 0, 0, size, size - tr.getHeight( null ) / 2, 1, c, c, null  );
		
		imagenPoligono2D.componerImagen( body, circ.getWidth( null ) / 2, body.getHeight( null ) / 2, circ );
		imagenPoligono2D.componerImagen( body, circ.getWidth( null ) / 2 + circ.getWidth( null ), body.getHeight( null ) / 2 + circ.getHeight( null ), circ );
		imagenPoligono2D.componerImagen( body, circ.getWidth( null ) / 2 + circ.getWidth( null ) * 2, body.getHeight( null ) / 2, circ );
		
		imagenPoligono2D.componerImagen( body, body.getWidth( null ) / 2, circ.getHeight( null ) / 2 - circ.getHeight( null ) / 3, circ );
		imagenPoligono2D.componerImagen( body, body.getWidth( null ) / 2 + (int)( 1 * circ.getWidth( null ) ), circ.getHeight( null ) / 2 - circ.getHeight( null ) / 3 + circ.getHeight( null ) / 3, circ );
		imagenPoligono2D.componerImagen( body, body.getWidth( null ) / 2 + 2 * circ.getWidth( null ), circ.getHeight( null ) / 2 - circ.getHeight( null ) / 3 + (int)( 2.5 * circ.getHeight( null ) ) / 3, circ );
		imagenPoligono2D.componerImagen( body, body.getWidth( null ) / 2 + (int)( 3 * circ.getWidth( null ) ) - circ.getWidth( null ) / 4, circ.getHeight( null ) / 2 - circ.getHeight( null ) / 3 + (int)( 5 * circ.getHeight( null ) ) / 3, circ );
		
		imagenPoligono2D.componerImagen( img, tr.getWidth( null ) / 8, 0, tr );
		
		imagenPoligono2D.componerImagen( img, 0, size - body.getHeight( null ), body );
				
				
		return img;
	}
	
	public static Image Whistle( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );
		
		int thicknessBorder = size / 2;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}
		
		int radio = ( 3 * size ) / 4;
		
		if( radio < 1)
		{
			radio = 1;
		}
		
		imagenPoligono2D.crearImagenCircunferencia( 0, size - radio, radio, thicknessBorder, c, img ); 
		
		double angle = Math.PI/11;
		
		
		double x1 = ( Math.cos( Math.PI / 2 + angle ) + 1 ); 
		double y1 = ( Math.sin( -( Math.PI / 2 + angle ) ) + 1 );
		
		double m = -Math.tan( angle );
		double n = y1 - m * x1; 				
		
		double x2 = -n / m;
		double y2 = m * x2 + n;
		x1 = x1 / 2;
		y1 = y1 / 2;
				
		int[] xs = new int[] { (int)( radio * x1 ), (int)( size * x2 ) , size, radio - thicknessBorder/2};
		int[] ys = new int[] { (int)( y1 * radio +  size - radio ), (int)( size * y2 ) , (size-radio)/2, size/2};
		
		imagenPoligono2D.crearImagenPoligonoRelleno(xs, ys, c, img );
		
		int thickness = size /18;
		if(thickness < 1 )
		{
			thickness = 1;
		}
		
		imagenPoligono2D.crearImagenLinea( (int)( 2 * thickness ), thickness, xs[ 0 ] - (int)( 1 * thickness ), ys[ 0 ] - (int)( 1.5 * thickness ), thickness, c, img );
		imagenPoligono2D.crearImagenLinea( thickness, thickness * 4, xs[ 0 ] - thickness * 2, ys[ 0 ], thickness, c, img );
		imagenPoligono2D.crearImagenLinea( xs[0] + thickness, ys[ 0 ] - (int)(thickness * 1.5 ), xs[0] + (int)( thickness * 1.5 ), ys[ 0 ] - thickness * 4, thickness, c, img );
		
		
		return img;
	}
	
	public static Image Bell( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );
		
		int bellBallSize = size / 5;
		if( bellBallSize < 1 )
		{
			bellBallSize = 1;
		}
				
		Image bellBall = imagenPoligono2D.crearImagenArco( 0, 0, bellBallSize, bellBallSize, 180, 180, 1.5F, c, c, null );
		Image bellBottom = imagenPoligono2D.crearImagenArco( 0, 0, size, 4 * bellBallSize, 0, 180, 1.5F, c, c, null );
		Image bellBody = imagenPoligono2D.crearImagenRectangulo( 3 * bellBallSize + 1, 3 * bellBallSize, 2, c, c );
		Image bellTop = imagenPoligono2D.crearImagenArco( 0, 0, 3 * bellBallSize, 2 * bellBallSize, 0, 180, 1.5F, c, c, null );
		
		imagenPoligono2D.componerImagen( img, bellBallSize, 0, bellTop );
		imagenPoligono2D.componerImagen( img, bellBallSize, bellBallSize, bellBody );
		imagenPoligono2D.componerImagen( img, 0, size - bellBottom.getHeight( null )/2 - bellBall.getHeight( null ) / 2, bellBottom );
		imagenPoligono2D.componerImagen( img, ( size - bellBall.getWidth( null ) ) /2, size - bellBall.getHeight( null ), bellBall );
		
		
		return img;
	}
	
	public static Image Maraca( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );
		
		int thicknessBorder = size / 32;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}
		
		Color c2 = Color.WHITE;
		float[] hsb = new float[ 3 ];
		Color.RGBtoHSB( c.getRed(), c.getGreen(), c.getBlue(), hsb );
		
		if( hsb[ 1 ] < 0.25 )
		{
			c2 = Color.BLACK;
		}
		
		int w = size / 2 - thicknessBorder;
		int h = (size * 1 ) / 2;
		
		Image oval = imagenPoligono2D.crearImagenOvalo( 0, 0, w, h, 2, c, c, null );
		
		int yL =  oval.getHeight( null ) / 2 - thicknessBorder /2;
		
		for( int pos = 0; pos < oval.getWidth( null ); pos += thicknessBorder )
		{
			imagenPoligono2D.crearImagenCirculo( pos, yL, thicknessBorder, c2, oval );
		}
				
		int wStick = oval.getWidth( null ) / 3;
		if( wStick < 1 )
		{
			wStick = 1;
		}
		
		Image stick = imagenPoligono2D.crearImagenOvalo( 0, 0, wStick, ( h * 4 ) / 3 , 2, c, c, null );
				
		
		imagenPoligono2D.componerImagen( img, 0, 0, oval );
		imagenPoligono2D.componerImagen( img, ( oval.getWidth( null ) - stick.getWidth( null ) ) / 2, size - stick.getHeight( null ), stick );
		
		imagenPoligono2D.componerImagen( img, size - oval.getWidth( null ), 0, oval );
		imagenPoligono2D.componerImagen( img, size - oval.getWidth( null ) + ( oval.getWidth( null ) - stick.getWidth( null ) ) / 2, size - stick.getHeight( null ), stick );
		
		return img;
	}
}

