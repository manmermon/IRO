package tools;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;

public class SceneTools 
{
	public static FontMetrics getFontMetricByHeight( int maxHeight )
	{
		FontMetrics fm = null;
		
		if( maxHeight > 0 )
		{
			Canvas cv = new Canvas();

			Font f = new Font( Font.SANS_SERIF, Font.BOLD, 12 );
			fm = cv.getFontMetrics( f );

			while( fm.getHeight() < maxHeight )
			{
				f = new Font( f.getName(), f.getStyle(), f.getSize() + 1 );
				fm = cv.getFontMetrics( f );
			}

			while( fm.getHeight() > maxHeight )
			{
				f = new Font( f.getName(), f.getStyle(), f.getSize() - 1 );
				fm = cv.getFontMetrics( f );
			}			
		}
		
		return fm;
	}
	
	public static FontMetrics getFontMetricByWidth( int maxWidth, String text )
	{
		FontMetrics fm = null;
		
		if( maxWidth > 0 && text != null )
		{
			Canvas cv = new Canvas();

			Font f = new Font( Font.SANS_SERIF, Font.BOLD, 12 );
			fm = cv.getFontMetrics( f );

			while( fm.stringWidth( text ) < maxWidth )
			{
				f = new Font( f.getName(), f.getStyle(), f.getSize() + 1 );
				fm = cv.getFontMetrics( f );
			}

			while( fm.stringWidth( text ) > maxWidth )
			{
				f = new Font( f.getName(), f.getStyle(), f.getSize() - 1 );
				fm = cv.getFontMetrics( f );
			}			
		}
		
		return fm;
	}
}
