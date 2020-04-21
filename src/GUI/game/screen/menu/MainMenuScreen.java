package GUI.game.screen.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;

import GUI.game.component.sprite.Background;
import GUI.game.component.sprite.ISprite;
import GUI.game.component.sprite.Buttom;
import GUI.game.screen.Scene;
import config.language.Language;
import control.controller.IInputable;
import control.events.InputActionEvent;
import image.basicPainter2D;

public class MainMenuScreen extends Scene implements IInputable
{
	private final String BUTTON_SPRITE_ID = "BUTTON_ID";
	
	public MainMenuScreen( Dimension sceneSize)
	{
		super(sceneSize);
		
		Dimension d = super.getSize();
		Dimension btSize = new Dimension( d ); 
		btSize.width /= 4;
		btSize.height /= 10;
		
		Background bg = new Background( d, MainMenuScreen.BACKGROUND_ID );
		bg.setImage( (BufferedImage)basicPainter2D.createEmptyCanva( d.width, d.height, Color.GREEN.brighter() ) );
		
		
		Buttom btOption = new Buttom( BUTTON_SPRITE_ID );
		btOption.setSize( btSize );
		btOption.setZIndex( MainMenuScreen.PLANE_NOTE );
		Point2D.Double loc = new Point2D.Double( d.width / 4, d.height / 3 );
		btOption.setScreenLocation( loc );
		btOption.setText( Language.getLocalCaption( Language.PLAY ) );
		
		
		super.add( bg, MainMenuScreen.PLANE_BRACKGROUND );
		super.add( btOption, MainMenuScreen.PLANE_NOTE);
		
		/*
		super.getScene().addMouseMotionListener( new MouseMotionAdapter()
		{	
			@Override
			public void mouseMoved( MouseEvent e)
			{
				Point mouseLoc = e.getLocationOnScreen();
				
				for( ISprite sprite : getSprites( BUTTON_SPRITE_ID ) )
				{
					SpriteButtom sbt = (SpriteButtom)sprite;
					
					if( sbt.getBounds().contains( mouseLoc ) )
					{
						sbt.setBackgroundColor( c );
					}
					else
					{
						sbt.setBackgroundColor( null );
					}
				}
			}			
		});
		*/
	}

	@Override
	public void action( InputActionEvent act)
	{
		for( List< ISprite > spriteList : super.SPRITES.values() )
		{
			for( ISprite sprite : spriteList )
			{
				if( sprite instanceof IInputable )
				{
					((IInputable)sprite).action( act );
				}
			}
		}
	}
}
