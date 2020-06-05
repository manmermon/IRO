package GUI.game.screen.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.List;

import GUI.GameManager;
import GUI.buttom.GeometricButtom;
import GUI.dialog.InfoDialog;
import GUI.game.component.Frame;
import GUI.game.component.sprite.Score;
import GUI.game.screen.IScene;
import GUI.game.screen.Scene;
import config.Player;
import config.language.Language;
import control.controller.IInputable;
import control.events.InputActionEvent;
import general.Tuple;

public class MenuGameResults extends Scene implements IInputable, IGameMenu 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9062959498200684001L;
	
	private GeometricButtom nextBt;
	private GeometricButtom stopBt;

	/**
	 * Create the panel.
	 */
	public MenuGameResults( Dimension sceneSize, List< Tuple< Player, Double > > results
							, boolean showNextBt, boolean showStopBt ) 
	{
		super( sceneSize );
		
		int btX = ( 5 * sceneSize.width ) / 7;
		
		int w = (int)( btX * 0.8 );		
		int h = sceneSize.height / ( results.size() + 1 );
				
		int i = 0;
		for( Tuple< Player, Double > score : results )
		{
			Score sc = null;
			int sw = w + 100 ;
			h += 1;
			while( sw > w )
			{	
				h -= 1;
				sc = new Score( IScene.SCORE_ID
								, score.y.intValue()
								, 0
								, h
								, new Point( 0 , (int)( ( i + 0.5 ) * h ) ) );
				
				Dimension sizeSC = sc.getSize();
				sw = sizeSC.width;
			}
						
			Point2D.Double loc = sc.getScreenLocation();
			
			loc.x = ( w - sw ) / 2 ;
			if( loc.x < 0 )
			{
				loc.x = 0;
			}
			
			sc.setScreenLocation( loc );
			
			i++;
			
			sc.setOwner( score.x );
			
			super.add( sc, PLANE_SCORE);
		}
		
		Dimension btSize = new Dimension( sceneSize.width / 6, sceneSize.height / 5 );		
		int btPadding = h;
		
		if( showNextBt )
		{
			//this.nextBt = new JButton( Language.getLocalCaption( Language.NEXT ) );
			this.nextBt = new GeometricButtom( GeometricButtom.ROUNDED_RECTANBLE, Language.getLocalCaption( Language.NEXT ) );
			this.nextBt.setSize( btSize );
			this.nextBt.setBackground( Color.ORANGE );
			this.nextBt.setForeground( Color.WHITE );
			this.nextBt.setBorderThickness( 3 );
			this.nextBt.setAutoFontSize( true );
			this.nextBt.setLocation( new Point( btX, sceneSize.height / 2 - btSize.height - btPadding / 2 ) );
			
			this.nextBt.addActionListener( new ActionListener() 
			{
				
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					try
					{
						GameManager.getInstance().nextLevel();
					}
					catch (Exception e) 
					{
						e.printStackTrace();
						String msg = "";
						if( e != null )
						{
							msg += e.getCause();
							if( !msg.isEmpty() )
							{
								msg += "\n";
							}
							
							msg += e.getMessage();
						}
						
						if( msg.isEmpty() )
						{
							msg += e.getClass().getCanonicalName();
						}
						
						InfoDialog dialog = new InfoDialog( msg );
						Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
						dialog.setSize( d );						
						dialog.setVisible( true );
						dialog.toFront();
					}
				}
			});
		}
		
		if( showStopBt )
		{
			this.stopBt =  new GeometricButtom( GeometricButtom.ROUNDED_RECTANBLE, Language.getLocalCaption( Language.END ) );
			this.stopBt.setSize( btSize );
			this.stopBt.setBackground( Color.RED );
			this.stopBt.setForeground( Color.WHITE );
			this.stopBt.setBorderThickness( 3 );
			this.stopBt.setAutoFontSize( true );
			this.stopBt.setLocation( new Point( btX, sceneSize.height / 2 + btPadding / 2 ) );
			
			this.stopBt.addActionListener( new ActionListener() 
			{				
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					try 
					{
						GameManager.getInstance().stopLevel( false );
					}
					catch (Exception e) 
					{
						e.printStackTrace();
						String msg = "";
						if( e != null )
						{
							msg += e.getCause();
							if( !msg.isEmpty() )
							{
								msg += "\n";
							}
							
							msg += e.getMessage();
						}
						
						if( msg.isEmpty() )
						{
							msg += e.getClass().getCanonicalName();
						}
						
						InfoDialog dialog = new InfoDialog( msg );
						Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
						dialog.setSize( d );						
						dialog.setVisible( true );
						dialog.toFront();
					}
				}
			});
		}
	}

	@Override
	public Frame getScene() 
	{
		Frame fr = super.getScene();
		
		if( this.nextBt != null )
		{
			fr.add( this.nextBt );
		}
		
		if( this.stopBt != null )
		{
			fr.add( this.stopBt );
		}
		
		return fr;
	}
	
	@Override
	public void action(InputActionEvent act) 
	{
		
	}
}
