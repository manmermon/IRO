package gui.game.screen.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.GameManager;
import GUI.buttom.GeometricButtom;
import GUI.dialog.InfoDialog;
import gui.game.component.Frame;
import gui.game.component.sprite.Score;
import gui.game.screen.Scene;
import gui.game.screen.level.Level;
import GUI.layout.VerticalFlowLayout;
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

	private Dimension btSize;
	private double padding = 0.1;
	private List< Tuple< Player, Double > > scores = null;
	
	/**
	 * Create the panel.
	 */
	public MenuGameResults( Dimension sceneSize//, Rectangle frameBounds
							, List< Tuple< Player, Double > > results
							, boolean showNextBt, boolean showStopBt ) 
	{
		super( sceneSize );
		
		this.scores = new ArrayList<Tuple<Player,Double>>( results );
		
		int btX = ( 5 * sceneSize.width ) / 7;		 
		
		this.btSize = new Dimension( sceneSize.width / 6, sceneSize.height / 5 );
		
		int btPadding =  sceneSize.height / ( results.size() + 1 );;
		
		if( showNextBt )
		{
			//this.nextBt = new JButton( Language.getLocalCaption( Language.NEXT ) );
			this.nextBt = new GeometricButtom( GeometricButtom.ROUNDED_RECTANBLE, Language.getLocalCaption( Language.NEXT ) );
			this.nextBt.setSize( this.btSize );
			this.nextBt.setPreferredSize( this.btSize );
			this.nextBt.setBackground( Color.ORANGE );
			this.nextBt.setForeground( Color.WHITE );
			this.nextBt.setBorderThickness( 3 );
			this.nextBt.setAutoFontSize( true );
			this.nextBt.setLocation( new Point( btX, sceneSize.height / 2 - this.btSize.height - btPadding / 2 ) );
			
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
			this.stopBt.setSize( this.btSize );
			this.stopBt.setPreferredSize( this.btSize );
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
	public Frame getMenuFrame() 
	{
		Frame fr = new Frame();
		
		fr.setLayout( new BorderLayout() );
		fr.setIgnoreRepaint( true );
		fr.setDoubleBuffered( true );
		
		JPanel btPanel = new JPanel( new VerticalFlowLayout( VerticalFlowLayout.CENTER ) );
		btPanel.setBackground( Color.WHITE );
		
		Component top = Box.createRigidArea( this.btSize );
		Component mid = Box.createRigidArea( this.btSize );
		Component but = Box.createRigidArea( this.btSize );
				
		btPanel.add( top );
		if( this.nextBt != null )
		{
			btPanel.add( this.nextBt );
			btPanel.add( mid );
		}
		if( this.stopBt != null )
		{
			btPanel.add( this.stopBt );
			btPanel.add( but );
		}
		
		JPanel padPanel = new JPanel( new BorderLayout() );
		Dimension padSize = new Dimension( (int)( this.btSize.width * this.padding )
											, (int)( this.btSize.height * this.padding ) ) ;
		Component pad = Box.createRigidArea( padSize );
		padPanel.add( pad, BorderLayout.EAST );
		padPanel.setBackground( Color.WHITE );
		
		JPanel eastPanel = new JPanel( new FlowLayout() );
		eastPanel.setBackground( Color.WHITE );
		eastPanel.add( btPanel );
		eastPanel.add( padPanel );
		
		int w = super.getSize().width;
		int h = super.getSize().height;
		int wp = padSize.width;
		int wt = this.btSize.width; 
		w = w - 4 * wp - wt 
				- eastPanel.getInsets().left 
				- eastPanel.getInsets().right
				- btPanel.getInsets().left
				- btPanel.getInsets().right
				- padPanel.getInsets().left
				- padPanel.getInsets().right;
		Dimension scoreSiz = new Dimension( w, h );		
		super.size = scoreSiz;
		
		BufferedImage scImg = this.getScore( scoreSiz );	
		
		JLabel score = new JLabel( new ImageIcon( scImg ) );
		score.setBackground( Color.WHITE );
		
		fr.setBackground( Color.WHITE );
		fr.add( eastPanel, BorderLayout.EAST );
		fr.add( score, BorderLayout.CENTER );
				
		return fr;
	}
	
	private BufferedImage getScore( Dimension sceneSize )
	{
		super.removeAllSprites();
		
		int w = sceneSize.width;		
		int h =  sceneSize.height / ( this.scores.size() + 1 );
		
		BufferedImage s = null;
		
		int i = 0;
		for( Tuple< Player, Double > score : this.scores )
		{
			Score sc = null;
			int sw = w + 100 ;
			h += 1;
			while( sw > w )
			{	
				h -= 1;
				sc = new Score( Level.SCORE_ID
								, score.t2.intValue()
								, 0
								, h
								, new Point( 0 , (int)( ( i + 0.5 ) * h ) ) );
				
				sc.setOwner( score.t1 );
				
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
			
			super.add( sc, Level.PLANE_SCORE);
		}
		
		s = super.getScene();
	
		return s;
	}
	
	@Override
	public void action(InputActionEvent act) 
	{
		
	}
}
