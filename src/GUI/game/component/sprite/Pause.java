package gui.game.component.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;

import config.language.Language;
import image.BasicPainter2D;
import tools.SceneTools;

public class Pause extends AbstractSprite 
{
	private BufferedImage img = null;
	
	public Pause( Dimension size, String id ) 
	{
		super( id );
		
		super.setSize( size );
		
		int h = super.getSize().height;
		int w = super.getSize().width;
		
		String text = Language.getLocalCaption( Language.PAUSE );
		
		FontMetrics fm = SceneTools.getFontMetricByHeight( h / 3 );
			
		if( fm.stringWidth( text ) > super.getSize().width )
		{
			fm = SceneTools.getFontMetricByWidth( w, text ); 
		}
		
		Color c = new Color( 0, 0, 0, 127 );
		
		this.img = (BufferedImage)BasicPainter2D.createEmptyCanva( w, h, c);
		
		int tw = fm.stringWidth( text );
		BasicPainter2D.text( ( w - tw ) / 2, ( h - fm.getHeight() ) / 2
							, text, fm, Color.RED
							, Color.ORANGE, this.img );
	}

	@Override
	protected void updateSpecificSprite() 
	{	
	}

	@Override
	protected BufferedImage createSprite() 
	{
		return this.img;
	}

}
