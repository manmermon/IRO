package GUI.screens;

import java.awt.Dimension;
import java.util.List;

import GUI.components.Frame;
import GUI.components.ISprite;

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
		
	public List< ISprite > getSprites( String idSprite );
	
	public int getNumberOfSprites( String idSprite );
	
	public void updateLevel();
	
	public Frame getScene();	
}
