package GUI;

import java.awt.Color;
import java.awt.Image;

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


		Color holeColor = new Color( ~c.getRGB() );
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

	public static Image Xilofono( int size, Color c )
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
		Color nc = new Color( ~c.getRGB() );
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
		Image img = imagenPoligono2D.crearLienzoVacio( size, size/2, null );

		Image tr = imagenPoligono2D.crearImagenTriangulo( size / 3, 1, c, c,
												imagenPoligono2D.WEST );

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
		
		int w = size - thicknessBorder - tr.getWidth( null );
		int h = ( 2 * tr.getHeight( null ) ) / 3;
		imagenPoligono2D.crearImagenRectanguloRedondo( ( int )( 1.5 * thicknessBorder ) 
													, y
													, w
													, h
													, w / 2
													, (int)( 0.9 * h ) 
													, thicknessBorder
													, c, null, img );

		int x = ( int )( 1.5 * thicknessBorder ) + w / 2;  
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
		
		
		return imagenPoligono2D.componerImagen( imagenPoligono2D.crearLienzoVacio( size, size, null )
												, 0, ( size - img.getHeight( null ) ) / 2
												, img );
	}
	
	public static Image Drum( int size, Color c )
	{
		Image img = imagenPoligono2D.crearLienzoVacio( size, size, null );
		
		Color borderColor = new Color( ~c.getRGB() );
		
		int divStep = size / 10;
		
		int thicknessBorder = size / 32;

		if( thicknessBorder < 1 )
		{
			thicknessBorder = 1;
		}
		
		int ovalHeigh = divStep * 4;
		
		
		imagenPoligono2D.crearImagenOvalo( 0, size - ovalHeigh, size, ovalHeigh, thicknessBorder, c, c, img );
		imagenPoligono2D.crearImagenPoligonoRelleno( new int[] { size, 0, 0, size }
													, new int[] { size - ovalHeigh / 2, size - ovalHeigh / 2, size - ( 3 * ovalHeigh ) / 2, size - ( 3 * ovalHeigh ) / 2}
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
}
