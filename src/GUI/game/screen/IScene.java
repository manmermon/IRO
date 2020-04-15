package GUI.game.screen;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;

import GUI.game.component.Frame;
import GUI.game.component.ISprite;

public interface IScene 
{
	public static final int PLANE_BRACKGROUND = -1;
	public static final int PLANE_PENTAGRAM = 0;
	public static final int PLANE_NOTE = 1; 
	public static final int PLANE_FRET = 2;
	
	public static final String BACKGROUND_ID = "background";
	public static final String PENTRAGRAM_ID = "pentagram";
	public static final String NOTE_ID = "note";
	public static final String FRET_ID = "fret";
	
	public Dimension getSize();
	
	public void add( ISprite sprite, int zIndex );
		
	public void remove( ISprite sprite );
	
	public void remove( List< ISprite > sprite );
	
	public void removeAllSprites();
		
	public List< ISprite > getSprites( String idSprite, boolean onlyOnScreen );
	
	public List< ISprite > getAllSprites( boolean onlyOnScreen );
	
	public int getNumberOfSprites( String idSprite );
		
	public void updateLevel();
	
	public Frame getScene();	
}
