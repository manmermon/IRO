package gui.panel.samSurvey;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import image.BasicPainter2D;
import image.icon.GeneralAppIcon;

import org.jgrasstools.gears.utils.math.interpolation.splines.Bspline;

import com.vividsolutions.jts.geom.Coordinate;

public class SamIcon 
{
	public static ImageIcon getSAMValence( int level, int side, Color borderColor, Color fillColor )
	{
		int thicknessBorder = 2;

		Image img = BasicPainter2D.rectangle( side, side, thicknessBorder, borderColor, fillColor );

		Image eye = BasicPainter2D.circle( 0, 0, side/6, borderColor, null );

		Image mouth = getSmile( level, side, thicknessBorder, borderColor, fillColor );

		int w = mouth.getWidth( null );
		int hg = mouth.getHeight( null );

		img = BasicPainter2D.composeImage( img, side/6, side/6 , eye );
		img = BasicPainter2D.composeImage( img, side - side/3, side/6 , eye );
		//img = BasicPainter2D.composeImage( img, ( 5 * side ) / 12, ( 5 * side) / 12, nose );

		img = BasicPainter2D.composeImage( img, ( side - w ) / 2 , side - hg*2, mouth );

		return new ImageIcon( img );
		//return new ImageIcon( mouth );
	}

	private static Image getSmile( int level, int imgWidth, int thickness, Color borderColor, Color fillColor )
	{
		double x1 = 1;
		double x2 = ( 8 * imgWidth ) / 24.0 + x1, x3 = ( 8 * imgWidth ) / 12.0 + x1;
		double y1 = imgWidth / 6.0;
		double y2 = y1, y3 = y1;

		double h = ( imgWidth / 3.0 ) / 8.0; 

		int val = level - 5;

		y1 += -val * h;
		y3 = y1;
		y2 += val * h;

		if( y1 < 0 )
		{
			y1 = 0;
			y3 = y1;
			y2 = h * 8;
		}
		else if( y1 > h * 8 )
		{
			y2 = 0;
			y1 = h * 8;
			y3 = y1;
		}

		Bspline bspline = new Bspline();

		bspline.addPoint( -1, y1 );
		bspline.addPoint( 0, y1 );
		bspline.addPoint( x1, y1 );
		bspline.addPoint( x2, y2 );
		bspline.addPoint( x3, y3 );
		bspline.addPoint( x3, y3 );
		bspline.addPoint( x2, y2 );
		bspline.addPoint( x1, y1 );
		bspline.addPoint( 0, y1 );
		bspline.addPoint( -1, y1 );

		List< Coordinate > coords = bspline.getInterpolated();

		int n = coords.size();
		int[] xs = new int[ n ];
		int[] ys = new int[ xs.length ];

		int i = 0;
		int w = 0;
		int hg = 0;
		for( Coordinate c : coords )
		{
			xs[ i ] = (int)c.x;
			ys[ i ] = (int)c.y;

			if( w < xs[ i ] )
			{
				w = xs[ i ];
			}

			if( hg < ys[ i ] )
			{
				hg = ys[ i ];
			}

			i += 1;			
		}

		Image mouth = BasicPainter2D.createEmptyCanva( w + 1, hg + 1, fillColor );

		for( i = 1; i < xs.length; i++ )
		{
			BasicPainter2D.line( xs[i-1], ys[i-1]
					, xs[i], ys[i]
							, thickness
							, borderColor, mouth );
		}

		return mouth;
	}

	public static ImageIcon getSAMValence( float level, int side, Color borderColor, Color fillColor )
	{
		int thicknessBorder = 2;

		Image img = BasicPainter2D.rectangle( side, side, thicknessBorder, borderColor, fillColor );

		Image eye = BasicPainter2D.circle( 0, 0, side/6, borderColor, null );

		Image mouth = getSmile( level, side, thicknessBorder, borderColor, fillColor );

		int w = mouth.getWidth( null );
		int hg = mouth.getHeight( null );

		img = BasicPainter2D.composeImage( img, side/6, side/6 , eye );
		img = BasicPainter2D.composeImage( img, side - side/3, side/6 , eye );
		//img = BasicPainter2D.composeImage( img, ( 5 * side ) / 12, ( 5 * side) / 12, nose );

		img = BasicPainter2D.composeImage( img, ( side - w ) / 2 , side - hg*2, mouth );

		return new ImageIcon( img );
		//return new ImageIcon( mouth );
	}
	
	private static Image getSmile( float level, int imgWidth, int thickness, Color borderColor, Color fillColor )
	{
		double x1 = 1;
		double x2 = ( 8 * imgWidth ) / 24.0 + x1, x3 = ( 8 * imgWidth ) / 12.0 + x1;
		double y1 = imgWidth / 6.0;
		double y2 = y1, y3 = y1;

		double h = ( imgWidth / 3.0 ) / 8.0; 

		if( level < 0 || level > 1 )
		{
			level = 1F;
		}
		
		int val = Math.round( level * 9 ) - 5;

		y1 += -val * h;
		y3 = y1;
		y2 += val * h;

		if( y1 < 0 )
		{
			y1 = 0;
			y3 = y1;
			y2 = h * 8;
		}
		else if( y1 > h * 8 )
		{
			y2 = 0;
			y1 = h * 8;
			y3 = y1;
		}

		Bspline bspline = new Bspline();

		bspline.addPoint( -1, y1 );
		bspline.addPoint( 0, y1 );
		bspline.addPoint( x1, y1 );
		bspline.addPoint( x2, y2 );
		bspline.addPoint( x3, y3 );
		bspline.addPoint( x3, y3 );
		bspline.addPoint( x2, y2 );
		bspline.addPoint( x1, y1 );
		bspline.addPoint( 0, y1 );
		bspline.addPoint( -1, y1 );

		List< Coordinate > coords = bspline.getInterpolated();

		int n = coords.size();
		int[] xs = new int[ n ];
		int[] ys = new int[ xs.length ];

		int i = 0;
		int w = 0;
		int hg = 0;
		for( Coordinate c : coords )
		{
			xs[ i ] = (int)c.x;
			ys[ i ] = (int)c.y;

			if( w < xs[ i ] )
			{
				w = xs[ i ];
			}

			if( hg < ys[ i ] )
			{
				hg = ys[ i ];
			}

			i += 1;			
		}

		Image mouth = BasicPainter2D.createEmptyCanva( w + 1, hg + 1, fillColor );

		for( i = 1; i < xs.length; i++ )
		{
			BasicPainter2D.line( xs[i-1], ys[i-1]
					, xs[i], ys[i]
							, thickness
							, borderColor, mouth );
		}

		return mouth;
	}
	
	public static ImageIcon getSAMArousal( int level, int side, Color borderColor, Color fillColor )
	{
		int l = level;
		if( l < 0 )
		{
			l = 1;
		}
		else if( l > 9 )
		{
			l = 9;
		}


		int[] xs = { 0, 3, 4, 5, 8, 5, 8, 5, 7, 4, 3, 3, 1, 3, 0 };
		int[] ys = { 0, 2, 0, 2, 0, 3, 5, 5, 8, 6, 8, 5, 7, 4, 0 };

		for( int i = 0; i < xs.length; i++ )
		{
			xs[ i ] = (int)( ( l / 9.0 ) * (2.0 / 3 ) * side * xs[ i ] / 8.0 );
			ys[ i ] = (int)( ( l / 9.0 ) * (2.0 / 3 ) *  side * ys[ i ] / 8.0 );
		}

		Image doll = GeneralAppIcon.getDoll( side, side, borderColor, fillColor, fillColor ).getImage();
		Image cloud = BasicPainter2D.fillPolygon( xs, ys, fillColor, null ); 
		BasicPainter2D.outlinePolygon( xs, ys, 2, borderColor, cloud );
		Image mov = BasicPainter2D.arc( 0, 0, (int)( side * .25 / 2)
				, (int)( side * .25 / 2), 180, -90
				, 2, borderColor, null, null );

		Image mov2 = BasicPainter2D.arc( 0, 0, (int)( side * .25 / 2)
				, (int)( side * .25 / 2), 90, -90
				, 2, borderColor, null, null );

		Image mov3 = BasicPainter2D.arc( 0, 0, (int)( side * .25 / 2)
				, (int)( side * .25 / 2), 0, -90
				, 2, borderColor, null, null );

		Image mov4 = BasicPainter2D.arc( 0, 0, (int)( side * .25 / 2)
				, (int)( side * .25 / 2), 270, -90
				, 2, borderColor, null, null );												

		int w = cloud.getWidth( null ) ;
		int h = cloud.getHeight( null );

		BasicPainter2D.composeImage( doll, (side - w) / 2, (side - h ) / 2, cloud );

		if( l > 6 )
		{
			int gap = 5;
			w = mov.getWidth( null );
			h = mov.getHeight( null )/2;

			BasicPainter2D.composeImage( doll, w / 2 + gap, h + gap, mov );
			if( l > 7 )
			{

				BasicPainter2D.composeImage( doll, gap, gap, mov );
			}

			w = mov2.getWidth( null );
			h = mov2.getHeight( null )/2;

			BasicPainter2D.composeImage( doll, side - w - w / 2 - gap, h + gap, mov2 );
			if( l > 7 )
			{
				BasicPainter2D.composeImage( doll, side - w - gap, gap , mov2 );
			}

			if( l > 8 )
			{
				w = mov4.getWidth( null );
				h = mov4.getHeight( null )/2;

				BasicPainter2D.composeImage( doll, gap, side - 2 * h - gap, mov4 );
				BasicPainter2D.composeImage( doll, w  / 2 + gap, side - 2 * h - h/2 - gap, mov4 );

				w = mov3.getWidth( null );
				h = mov3.getHeight( null )/2;

				BasicPainter2D.composeImage( doll, side - w - gap, side - 2 * h - gap, mov3 );
				BasicPainter2D.composeImage( doll, side - w - w / 2 - gap, side - 2 * h - h/2 - gap, mov3 );
			}
		}	

		return new ImageIcon( doll );
	}

	public static ImageIcon getSAMArousal( double level, int side, Color borderColor, Color fillColor )
	{
		double l = level;
		if( l < 0 )
		{
			l = 1;
		}
		else if( l > 1 )
		{
			l = 1;
		}


		int[] xs = { 0, 3, 4, 5, 8, 5, 8, 5, 7, 4, 3, 3, 1, 3, 0 };
		int[] ys = { 0, 2, 0, 2, 0, 3, 5, 5, 8, 6, 8, 5, 7, 4, 0 };

		for( int i = 0; i < xs.length; i++ )
		{
			xs[ i ] = (int)( l * (2.0 / 3 ) * side * xs[ i ] / 8.0 );
			ys[ i ] = (int)( l * (2.0 / 3 ) *  side * ys[ i ] / 8.0 );
		}

		Image doll = GeneralAppIcon.getDoll( side, side, borderColor, fillColor, fillColor ).getImage();
		try
		{
			Image cloud = BasicPainter2D.fillPolygon( xs, ys, fillColor, null );		
			BasicPainter2D.outlinePolygon( xs, ys, 2, borderColor, cloud );
		
			Image mov = BasicPainter2D.arc( 0, 0, (int)( side * .25 / 2)
					, (int)( side * .25 / 2), 180, -90
					, 2, borderColor, null, null );
	
			Image mov2 = BasicPainter2D.arc( 0, 0, (int)( side * .25 / 2)
					, (int)( side * .25 / 2), 90, -90
					, 2, borderColor, null, null );
	
			Image mov3 = BasicPainter2D.arc( 0, 0, (int)( side * .25 / 2)
					, (int)( side * .25 / 2), 0, -90
					, 2, borderColor, null, null );
	
			Image mov4 = BasicPainter2D.arc( 0, 0, (int)( side * .25 / 2)
					, (int)( side * .25 / 2), 270, -90
					, 2, borderColor, null, null );												
	
			int w = cloud.getWidth( null ) ;
			int h = cloud.getHeight( null );
	
			BasicPainter2D.composeImage( doll, (side - w) / 2, (side - h ) / 2, cloud );
		
			if( l > 6 / 9.0D )
			{
				int gap = 5;
				w = mov.getWidth( null );
				h = mov.getHeight( null )/2;
	
				BasicPainter2D.composeImage( doll, w / 2 + gap, h + gap, mov );
				if( l > 7 )
				{
	
					BasicPainter2D.composeImage( doll, gap, gap, mov );
				}
	
				w = mov2.getWidth( null );
				h = mov2.getHeight( null )/2;
	
				BasicPainter2D.composeImage( doll, side - w - w / 2 - gap, h + gap, mov2 );
				if( l > 7 / 9.0D )
				{
					BasicPainter2D.composeImage( doll, side - w - gap, gap , mov2 );
				}
	
				if( l > 8 / 9.0D )
				{
					w = mov4.getWidth( null );
					h = mov4.getHeight( null )/2;
	
					BasicPainter2D.composeImage( doll, gap, side - 2 * h - gap, mov4 );
					BasicPainter2D.composeImage( doll, w  / 2 + gap, side - 2 * h - h/2 - gap, mov4 );
	
					w = mov3.getWidth( null );
					h = mov3.getHeight( null )/2;
	
					BasicPainter2D.composeImage( doll, side - w - gap, side - 2 * h - gap, mov3 );
					BasicPainter2D.composeImage( doll, side - w - w / 2 - gap, side - 2 * h - h/2 - gap, mov3 );
				}
			}	
		}
		catch (Exception e) 
		{
		}

		return new ImageIcon( doll );
	}
	
	public static ImageIcon getSAMDominance( double level, int side, Color borderColor, Color fillColor )
	{
		double l = level;
		if( l < 0 )
		{
			l = 1;
		}
		else if( l > 1 )
		{
			l = 1;
		}


		double w = 0.2 + l * 0.8 ; 
		w *= side;

		return GeneralAppIcon.getDoll((int)w , (int)w, borderColor, fillColor, fillColor ); //getSAMValence( 5, (int)w, borderColor, fillColor );
	}
	
	
	public static ImageIcon getSAMDominance( int level, int side, Color borderColor, Color fillColor )
	{
		int l = level;
		if( l < 0 )
		{
			l = 1;
		}
		else if( l > 9 )
		{
			l = 9;
		}


		double w = 0.2 + l * 0.8 / 9 ; 
		w *= side;

		return GeneralAppIcon.getDoll((int)w , (int)w, borderColor, fillColor, fillColor ); //getSAMValence( 5, (int)w, borderColor, fillColor );
	}

	public static ImageIcon getBasicEmotion(int level, int side, Color borderColor, Color fillColor, String text, FontMetrics fm)
	{
		int l = level;
		if (l < 1)
		{
			l = 1;
		}
		else if (l > 7)
		{
			l = 7;
		}

		int thicknessBorder = 3;

		int h = side;
		int htext = 0;
		Image imgtext = null;

		if ((text != null) && (!text.isEmpty()))
		{
			imgtext = BasicPainter2D.text(0, 0, text, fm, fillColor, borderColor, null);

			htext = imgtext.getHeight(null);
			h -= htext;
		}

		if (h > 0)
		{
			side = h;
		}
		else if( h <= 0 )
		{
			h = 1;
		}

		Image img = BasicPainter2D.rectangle(side, h, thicknessBorder, borderColor, fillColor);
		Image eye = BasicPainter2D.circle(0, 0, side / 6, borderColor, null);

		if (l == 1)
		{
			// Sadness

			img = getSAMValence( 1, side, borderColor, fillColor).getImage();	
			if( h > 0)
			{
				img = img.getScaledInstance( img.getWidth( null ), h,  BufferedImage.SCALE_SMOOTH );
			}
		}
		else if (l == 2)
		{
			// Surprise	

			Image eyebrow = BasicPainter2D.createEmptyCanva( eye.getWidth( null ), thicknessBorder, borderColor );
			Image mouth = BasicPainter2D.circle( 0, 0, side/3, borderColor, null );

			img = BasicPainter2D.composeImage( img, side/6, side/6 , eye );
			img = BasicPainter2D.composeImage( img, side/6, side/6 - eye.getHeight( null )/4, eyebrow );

			img = BasicPainter2D.composeImage( img, side - side/3, side/6 , eye );
			img = BasicPainter2D.composeImage( img, side - side/3, side/6 - eye.getHeight( null )/4, eyebrow );

			img = BasicPainter2D.composeImage( img, img.getWidth( null ) / 2 - mouth.getWidth( null ) /2
					, img.getHeight( null ) - (int)( 1.25 * mouth.getHeight( null ) ), mouth );
		}
		else if (l == 3)
		{
			// Anger

			img = getSAMValence( 4, side, borderColor, fillColor).getImage();

			//Image mouth = getSmile( 5, side, thicknessBorder, borderColor, fillColor );					
			Image rightEyebrow = BasicPainter2D.line( 0, 0
					, eye.getWidth(null ), side/10
					, thicknessBorder, borderColor, null );

			Image leftEyebrow = BasicPainter2D.line( 0, side/10
					, eye.getWidth(null ), 0 
					, thicknessBorder, borderColor, null );

			/*
			img = BasicPainter2D.composeImage( img, side/6, side/5 , eye );
			img = BasicPainter2D.composeImage( img, side/6, side/5 - rightEyebrow.getHeight( null ), rightEyebrow );

			img = BasicPainter2D.composeImage( img, side - side/3, side/5 , eye );
			img = BasicPainter2D.composeImage( img, side - side/3, side/5 - leftEyebrow.getHeight( null ), leftEyebrow );

			img = BasicPainter2D.composeImage( img, side/2 - mouth.getWidth( null ) / 2 , side - side / 3 , mouth );
			 */		

			img = BasicPainter2D.composeImage( img, side/6, side/5 - (int)( 1.05 * rightEyebrow.getHeight( null )), rightEyebrow );			
			img = BasicPainter2D.composeImage( img, side - leftEyebrow.getWidth( null ) - side/6, side/5 - (int)(1.05 * leftEyebrow.getHeight( null )), leftEyebrow );

			if( h > 0)
			{
				img = img.getScaledInstance( side, h,  BufferedImage.SCALE_SMOOTH );
			}
		}
		else if (l == 4)
		{
			// Disgust

			Image rigthEye = BasicPainter2D.createEmptyCanva(eye.getWidth(null), eye.getHeight(null), fillColor);
			BasicPainter2D.line(0, 0, eye.getWidth(null), eye.getHeight(null) / 2, thicknessBorder, borderColor, rigthEye);
			BasicPainter2D.line(eye.getWidth(null), eye.getHeight(null) / 2, 0, eye.getHeight(null), thicknessBorder, borderColor, rigthEye);

			Image leftEye = BasicPainter2D.createEmptyCanva(eye.getWidth(null), eye.getHeight(null), fillColor);
			BasicPainter2D.line(eye.getWidth(null), 0, 0, eye.getHeight(null) / 2, thicknessBorder, borderColor, leftEye);
			BasicPainter2D.line(eye.getWidth(null), eye.getHeight(null), 0, eye.getHeight(null) / 2, thicknessBorder, borderColor, leftEye);

			Image mouth = BasicPainter2D.arc(0, 0, (int)( side / 2.5 ), (int)( side / 2.5 ), 0, 180, thicknessBorder, borderColor, borderColor, null);

			int tongueW = mouth.getWidth(null) / 2;

			int x = thicknessBorder / 2;
			int y = thicknessBorder / 2;

			int w = tongueW - 2 * thicknessBorder;
			if (w < 1)
			{
				w = tongueW;
			}

			int tongueH = tongueW - thicknessBorder;
			if( tongueH <= 0 )
			{
				tongueH = 1;
			}
			
			Image tongueTip = BasicPainter2D.arc(0, 0, tongueW, tongueH, 0, -180, thicknessBorder, borderColor, fillColor, null);

			Image tongueBody = BasicPainter2D.createEmptyCanva(tongueTip.getWidth(null), tongueTip.getHeight(null), fillColor);

			BasicPainter2D.line(x, y, tongueBody.getWidth(null), y, thicknessBorder, borderColor, tongueBody);
			BasicPainter2D.line(x, y, x, tongueBody.getHeight(null), thicknessBorder, borderColor, tongueBody);
			BasicPainter2D.line(tongueBody.getWidth(null) / 2 - x, y, tongueBody.getWidth(null) / 2 - x, tongueBody.getHeight(null), thicknessBorder, borderColor, tongueBody);
			BasicPainter2D.line(tongueBody.getWidth(null) - x - 1, y, tongueBody.getWidth(null) - x - 1, tongueBody.getHeight(null), thicknessBorder, borderColor, tongueBody);

			Image tongue = BasicPainter2D.createEmptyCanva(tongueTip.getWidth(null), tongueTip.getHeight(null) / 2 + tongueBody.getHeight(null), fillColor);

			tongue = BasicPainter2D.composeImage(tongue, 0, 0, tongueBody);
			tongue = BasicPainter2D.composeImage(tongue, 0, tongueBody.getHeight(null) / 2, tongueTip);


			Image mouthTongue = BasicPainter2D.createEmptyCanva(mouth.getWidth(null), mouth.getHeight(null) / 2 + tongue.getHeight(null) - mouth.getHeight(null) / 6, null);
			mouthTongue = BasicPainter2D.composeImage(mouthTongue, 0, 0, mouth);
			mouthTongue = BasicPainter2D.composeImage(mouthTongue, 
					mouthTongue.getWidth(null) / 2 - tongue.getWidth(null) / 2, 
					mouth.getHeight(null) / 3, 
					tongue);


			img = BasicPainter2D.composeImage(img, side - side / 3 - leftEye.getWidth(null) / 4, side / 6, leftEye);


			img = BasicPainter2D.composeImage(img, side / 6 + rigthEye.getWidth(null) / 4, side / 6, rigthEye);


			img = BasicPainter2D.composeImage(img, (img.getWidth(null) - mouthTongue.getWidth(null)) / 2, 
					img.getHeight(null) - (int)(1.05D * mouthTongue.getHeight(null)), 
					mouthTongue);
		}
		else if (l == 5)
		{
			// Fear

			int smileW = side - side / 3;
			int smileH = side / 9;

			Image mouth = BasicPainter2D.createEmptyCanva(smileW, smileH + 2 * thicknessBorder, fillColor);

			double stepSin = (4  * Math.PI ) / smileW;

			List<Integer> yVal = new ArrayList< Integer >();
			for (double xVal = 0.0D; xVal <= 4 * Math.PI; xVal += stepSin)
			{
				yVal.add( ( int )( smileH * ( Math.sin( xVal ) + 1 ) / 2 ) + thicknessBorder );
			}

			for (int i = 1; i < yVal.size(); i++)
			{
				mouth = BasicPainter2D.line(i - 1, ((Integer)yVal.get(i - 1)).intValue(), i, ((Integer)yVal.get(i)).intValue(), thicknessBorder, borderColor, mouth);
			}

			Image leftEyebrow = BasicPainter2D.line( 0, 0
					, eye.getWidth(null ), side/6
					, thicknessBorder, borderColor, null );

			Image rigthEyebrow = BasicPainter2D.line( 0, side/6
					, eye.getWidth(null ), 0 
					, thicknessBorder, borderColor, null );

			img = BasicPainter2D.composeImage( img, side/6, side/6 , eye );
			img = BasicPainter2D.composeImage( img, side/6 - eye.getWidth( null ) / 2, side/6 - rigthEyebrow.getHeight( null ) / 2, rigthEyebrow );

			img = BasicPainter2D.composeImage( img, side - side/3, side/6 , eye );
			img = BasicPainter2D.composeImage( img, side - side/3 + eye.getWidth( null ) / 2, side/6 - leftEyebrow.getHeight( null ) / 2, leftEyebrow );

			img = BasicPainter2D.composeImage( img, side/2 - mouth.getWidth( null ) / 2 , img.getHeight( null ) - img.getHeight( null ) / 3  , mouth );
		}
		else if (l == 6)
		{
			// Happiness

			img = getSAMValence(9, side, borderColor, fillColor).getImage();
			if (h > 0)
			{
				img = img.getScaledInstance(img.getWidth(null), h,  BufferedImage.SCALE_SMOOTH );
			}

		}
		else // Neutral
		{
			img = getSAMValence(5, side, borderColor, fillColor).getImage();
			if (h > 0)
			{
				img = img.getScaledInstance(img.getWidth(null), h,  BufferedImage.SCALE_SMOOTH );
			}
		}

		Image out = BasicPainter2D.createEmptyCanva(img.getWidth(null), img.getHeight(null) + htext, fillColor);
		BasicPainter2D.composeImage(out, 0, 0, img);

		if (imgtext != null)
		{
			BasicPainter2D.composeImage(out, (img.getWidth(null) - imgtext.getWidth(null)) / 2, img.getHeight(null), imgtext);
		}

		return new ImageIcon(out);
	}

}
