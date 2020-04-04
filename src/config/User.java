package config;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import GUI.GeneralAppIcon;
import config.language.Language;

public class User
{
	public static final int ANONYMOUS_USER_ID = -1;
	
	private int id;
	private String name;
	private ImageIcon img = null;
	
	public User()
	{
		this.id = ANONYMOUS_USER_ID;
		this.name = Language.getLocalCaption( Language.ANONYMOUS );
		this.img = GeneralAppIcon.getDoll( 32, 32, Color.BLACK, Color.WHITE, null );
	}
	
	public User( int id, String name, ImageIcon img )
	{
		this();
		
		this.id = id;
		this.name = name;
		
		if( img != null )
		{
			this.img = img;
		}
		else
		{
			this.setDefaultImage();
		}
	}
	
	private void setDefaultImage()
	{
		this.img = GeneralAppIcon.getDoll( 64, 64, Color.BLACK, Color.WHITE, null );
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ImageIcon getImg()
	{
		return img;
	}
	
	public ImageIcon getImg( int width, int heigh )
	{
		ImageIcon ic = img;
		
		if( img != null && width > 0 && heigh > 0 )
		{
			ic = new ImageIcon( ((BufferedImage)img.getImage()).getScaledInstance( width, heigh, Image.SCALE_SMOOTH ) );
		}
		
		return ic;
	}
}
