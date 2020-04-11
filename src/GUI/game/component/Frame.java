/* 
 * Copyright 2019 by Manuel Merino Monge <manmermon@dte.us.es>
 *  
 *   This file is part of IRO.
 *
 *   IRO is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   LSLRec is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with LSLRec.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   Project's URL: https://github.com/manmermon/IRO
 */

package GUI.game.component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import control.scenes.ISceneControl;

public class Frame extends JPanel
{
	private static final long serialVersionUID = 1L;
	private BufferedImage scene = null;
	private ISceneControl ctrl = null;
		
	public Frame( ) 
	{
		super.setDoubleBuffered( true );	
		
		super.setFocusable( true );
	}
		
	public void setSceneControl( ISceneControl ctr )
	{
		synchronized( this )
		{
			this.ctrl = ctr;
		}
	}
	
	public void setScene( BufferedImage img )
	{
		this.scene = img;
		
		this.repaint();		
	}
	
	@Override
	public void paintComponent( Graphics g )
	{
		//super.setVisible( false );
		
		super.paintComponent( g );

		if( this.scene != null )
		{
			Dimension size = super.getSize();
			
			Image img = new BufferedImage( size.width, size.height,  BufferedImage.TYPE_INT_ARGB );

			Graphics2D gr = (Graphics2D)img.getGraphics();
			gr.drawImage( this.scene, 0, 0, null);
			gr.dispose();
			
			g.drawImage( img, 0, 0, null );
		}
		
		getToolkit().sync();		
		g.dispose();		

		super.revalidate();
		//super.setVisible( true );
	}
}
