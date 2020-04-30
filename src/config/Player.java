package config;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import config.language.Language;
import image.icon.GeneralAppIcon;

public class Player implements IOwner
{	
	private int id;
	private String name;
	private ImageIcon img = null;
	
	private ImageIcon defaultImg = GeneralAppIcon.getDoll( ConfigApp.playerPicSizeIcon.x , ConfigApp.playerPicSizeIcon.y
															, Color.BLACK, Color.WHITE, null );
	
	public Player()
	{
		this.id = ANONYMOUS;
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
	
	public boolean isAnonymous()
	{
		return this.id == ANONYMOUS;
	}
	
	public void setDefaultImage()
	{
		this.img = null;
	}
	
	@Override
	public int getId()
	{
		return id;
	}
	
	@Override
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
	
	/*(non-Javadoc)
	 * @see @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		
		return (new Integer( this.id )).hashCode();
	}
	
	/*(non-Javadoc)
	 * @see @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.id + ": " + this.name;
	}
	
	/*(non-Javadoc)
	 * @see @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean eq = ( obj instanceof Player );
		
		if( eq )
		{
			eq = ( this.id == ((Player)obj).getId() );
		}
		
		return eq;
	}

	/*(non-Javadoc)
	 * @see @see config.IOwner#getOwnerImage()
	 */
	@Override
	public Image getOwnerImage()
	{
		return this.getImg().getImage();
	}
}
