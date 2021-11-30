/**
 * 
 */
package gui;

import java.awt.Color;
import java.awt.Image;

import image.BasicPainter2D;
import image.icon.MusicInstrumentIcons;

/**
 * @author manuel
 *
 */
public class AppIcon 
{
	public static Image appIcon( int size )
	{
		Image img = BasicPainter2D.createEmptyCanva( size, size, null );

		int thick = size/8;

		BasicPainter2D.oval( -size + thick, -1, 2*size - (3*thick)/2, size, thick, Color.ORANGE, Color.WHITE, img );
		BasicPainter2D.line( thick/2 - 1, 0, thick/2 - 1, size, thick, Color.ORANGE, img );

		Image quaver = MusicInstrumentIcons.Quaver( size - (int)(4.25 * thick), size - (int)(4.25 * thick), Color.BLACK, null );

		BasicPainter2D.composeImage( img, (int)( 1.1 * thick ), ( size - quaver.getHeight( null ) ) /2, quaver );

		return img;
	}
}
