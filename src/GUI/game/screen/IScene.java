package gui.game.screen;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;

import gui.game.component.sprite.ISprite;

public interface IScene 
{
	public static final int PLANE_BRACKGROUND = -1;	
	
	public static final String BACKGROUND_ID = "background";	
	
	public Dimension getSize();
	
	public void add( ISprite sprite, int zIndex );
		
	public void remove( ISprite sprite );
	
	public void remove( List< ISprite > sprite );
	
	public void removeAllSprites();
		
	public List< ISprite > getSprites( String idSprite, boolean onlyOnScreen );
	
	public List< ISprite > getAllSprites( boolean onlyOnScreen );
	
	public int getNumberOfSprites( String idSprite );
		
	public void updateLevel();
	
	//public Frame getScene();	
	public BufferedImage getScene();
}
