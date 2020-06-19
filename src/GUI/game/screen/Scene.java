package GUI.game.screen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import GUI.game.component.Frame;
import GUI.game.component.event.SpriteEvent;
import GUI.game.component.event.SpriteEventListener;
import GUI.game.component.sprite.ISprite;
import general.ArrayTreeMap;
import image.basicPainter2D;

public abstract class Scene implements IScene, SpriteEventListener
{
	protected ArrayTreeMap< Integer, ISprite > SPRITES;
	protected ArrayTreeMap< String, ISprite > SPRITES_By_ID;
	
	private List< ISprite > removeSprite;
	
	protected Dimension size;
	protected Rectangle frameBounds;
	//protected Frame frame;
		
	public Scene( Dimension sceneSize )//, Rectangle FrameBounds ) 
	{
		this.size = new Dimension( sceneSize );
		this.frameBounds = new Rectangle( new Point(), this.size ); //new Rectangle( FrameBounds );
		
		this.SPRITES = new ArrayTreeMap< Integer, ISprite >();
		
		this.SPRITES_By_ID = new ArrayTreeMap< String, ISprite >();
		
		this.removeSprite = new ArrayList< ISprite>();
		
		/*
		this.frame = new Frame();
		this.frame.setLayout( null );
		this.frame.setSize( sceneSize );
		*/
	}
	
	@Override
	public Dimension getSize()
	{
		return this.size;
	}
	
	@Override
	public void add( ISprite sprite, int zIndex )
	{
		sprite.addSpriteEventListener( this );
		
		synchronized ( this.SPRITES )
		{	
			this.SPRITES.put( zIndex, sprite );
			
			synchronized ( this.SPRITES_By_ID) 
			{
				this.SPRITES_By_ID.put( sprite.getID(), sprite );
			}
		}
	}
		
	@Override
	public void remove( ISprite sprite )
	{
		synchronized ( this.SPRITES )
		{
			this.SPRITES.removeValue( sprite.getZIndex(), sprite );
			synchronized ( this.SPRITES_By_ID)
			{
				this.SPRITES_By_ID.removeValue( sprite.getID(), sprite );
			}
		}
	}

	@Override
	public void removeAllSprites()
	{
		synchronized ( this.SPRITES )
		{
			this.SPRITES.clear();
			
			synchronized( this.SPRITES_By_ID )
			{
				this.SPRITES_By_ID.clear();
			}
			
			//this.frame.removeAll();
		}		
	}
	
	@Override
	public void remove( List< ISprite > sprites )
	{
		for( ISprite sp : sprites )
		{
			this.remove( sp );
		}
	}
	
	@Override
	public List< ISprite > getSprites( String idSprite, boolean onlyOnScreen ) 
	{
		synchronized ( this.SPRITES_By_ID )
		{
			List< ISprite > sprites = new ArrayList< ISprite >();
			
			Rectangle sceneLoc = this.frameBounds;
			
			List< ISprite > sprs = this.SPRITES_By_ID.get( idSprite );
			if( sprs != null )
			{
				for( ISprite sp : sprs )
				{
					if( onlyOnScreen )
					{
						if( sceneLoc.contains( sp.getScreenLocation() ) )
						{
							sprites.add( sp );
						}
					}
					else
					{
						sprites.add( sp );
					}
				}
			}
			
			return sprites; 
		}
	}
	
	/*(non-Javadoc)
	 * @see @see GUI.game.screen.IScene#getAllSprites()
	 */
	@Override		
	public List< ISprite > getAllSprites( boolean onlyOnScreen )
	{
		synchronized ( this.SPRITES_By_ID )
		{
			List< ISprite > sprites = new ArrayList< ISprite >();
						
			for( String idSprite : this.SPRITES_By_ID.keySet() )
			{
				sprites.addAll( this.getSprites( idSprite, onlyOnScreen ) );
			}
			
			return sprites; 
		}
	}
	
	@Override
	public int getNumberOfSprites(String idSprite) 
	{
		int num = 0;
				
		synchronized ( this.SPRITES_By_ID )
		{
			List< ISprite > sprs = this.SPRITES_By_ID.get( idSprite );
			if( sprs != null )
			{
				num = sprs.size();
			}
		}
		
		return num;
	}
	
	@Override
	public void updateLevel()
	{
		synchronized ( this.SPRITES )
		{
			for( List< ISprite > sprites : this.SPRITES.values() )
			{
				for( ISprite sprite : sprites )
				{
					sprite.updateSprite();
				}
			}
			
			this.remove( this.removeSprite );
			this.removeSprite.clear();
		}
	}
	
	/*
	@Override
	public Frame getScene()
	 * {
		synchronized ( this.SPRITES )
		{
			Image scene = basicPainter2D.createEmptyCanva( this.size.width, this.size.height, Color.WHITE );
			
			List< Integer > drawIndexes = new ArrayList< Integer >( this.SPRITES.keySet() );
			Collections.sort( drawIndexes );
			
			Rectangle r = this.frame.getBounds();
			
			for( Integer index : drawIndexes )
			{
				List< ISprite > PICs = this.SPRITES.get( index );
				
				for( ISprite pic : PICs )
				{	
					Point2D.Double loc = pic.getScreenLocation();
	
					if( r.contains( loc ) )
					{
						long t = System.nanoTime();
						
						BufferedImage spr = pic.getSprite();
					
						basicPainter2D.composeImage( scene, (int)loc.x, (int)loc.y, spr );
						
						System.out.println("Scene.getScene() " + pic.getID() + " " + ( System.nanoTime() - t ) / 1e6D );
					}
				}
			}
			
			this.frame.setScene( (BufferedImage)scene );
			
			return this.frame;
		}
	}
	*/
	
	@Override
	public BufferedImage getScene()
	{
		synchronized ( this.SPRITES )
		{
			BufferedImage scene = (BufferedImage)basicPainter2D.createEmptyCanva( this.size.width, this.size.height, Color.WHITE );
			
			List< Integer > drawIndexes = new ArrayList< Integer >( this.SPRITES.keySet() );
			Collections.sort( drawIndexes );
			
			Rectangle r = this.frameBounds;
			
			for( Integer index : drawIndexes )
			{
				List< ISprite > PICs = this.SPRITES.get( index );
				
				for( ISprite pic : PICs )
				{	
					if( pic.isVisible() )
					{
						Point2D.Double loc = pic.getScreenLocation();
						
						if( r.contains( loc ) )
						{						
							BufferedImage spr = pic.getSprite();
							basicPainter2D.composeImage( scene, (int)loc.x, (int)loc.y, spr );						
						}
					}
				}
			}
			
			return scene;
		}
	}
	
	public void destroyScene()
	{
		synchronized ( this.SPRITES )
		{
			this.SPRITES.clear();
			synchronized( this.SPRITES_By_ID )
			{
				this.SPRITES_By_ID.clear();			
			}
		}
	}
	
	@Override
	public void SpriteEvent( SpriteEvent ev) 
	{
		if( ev.getType() == SpriteEvent.OUTPUT_SCREEN )
		{
			ISprite sprite = (ISprite)ev.getSource();
			this.removeSprite.add( sprite );
		}
	}
}
