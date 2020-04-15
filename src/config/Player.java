package config;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import config.language.Language;
import image.icon.GeneralAppIcon;

public class Player
{
	public static final int ANONYMOUS_USER_ID = -1;
	
	private int id;
	private String name;
	private ImageIcon img = null;
	
	private ImageIcon defaultImg = GeneralAppIcon.getDoll( ConfigApp.playerPicSizeIcon.x , ConfigApp.playerPicSizeIcon.y
															, Color.BLACK, Color.WHITE, null );
	
	public Player()
	{
		this.id = ANONYMOUS_USER_ID;
		this.name = Language.getLocalCaption( Language.ANONYMOUS );
		this.img = null;
	}
	
	public Player( int id, String name, ImageIcon img )
	{
		this();
		
		this.id = id;
		this.name = name;
		
		this.img = img;
		
	}
	
	public void setDefaultImage()
	{
		this.img = null;
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
		return this.img == null ? this.defaultImg : this.img;
	}
	
	public ImageIcon getImg( int width, int heigh )
	{
		ImageIcon ic = img;
		
		if( img != null && width > 0 && heigh > 0 )
		{
			ic = new ImageIcon( ((BufferedImage)img.getImage()).getScaledInstance( width, heigh, Image.SCALE_SMOOTH ) );
		}
		else
		{
			ic = new ImageIcon( ((BufferedImage)this.defaultImg.getImage()).getScaledInstance( width, heigh, Image.SCALE_SMOOTH ) );
		}
		
		return ic;
	}
}
