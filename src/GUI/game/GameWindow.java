/**
 * 
 */
package GUI.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import GUI.GameManager;
import GUI.action.keyActions;
import GUI.game.component.ControllerTargetBar;
import config.Player;
import control.controller.ControllerManager;

/**
 * @author manuel
 *
 */
public class GameWindow extends JFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4360081921411943013L;
	private JPanel contentPane;
	private JPanel gamePanel;
	
	private Map< Player,  ControllerTargetBar > targetControllerIndicators;
	
	/**
	 * Create the frame.
	 */
	public GameWindow( List< Player > players )
	{
		if( players == null || players.isEmpty() )
		{
			throw new IllegalArgumentException( "Player list is null or empty." );
		}
		
		this.targetControllerIndicators = new HashMap<Player, ControllerTargetBar>();
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); 
		Insets ins = Toolkit.getDefaultToolkit().getScreenInsets( this.getGraphicsConfiguration() );
		
		Rectangle bounds = new Rectangle( 0, 0
										, d.width - ins.left - ins.right
										, d.height - ins.bottom - ins.top );
		
		super.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		
		super.setPreferredSize( bounds.getSize() );
		//super.setLocation( bounds.getLocation() );
		
		super.setResizable( false );
		
		super.setContentPane( this.getContainerPanel( players ) );
		
		super.setBounds( bounds );
		
		super.addWindowListener( new WindowAdapter()
		{
			/*(non-Javadoc)
			 * @see @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosed(WindowEvent e)
			{
				try
				{
					GameManager.getInstance().stopLevel( false );
				} 
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		
		super.getRootPane().registerKeyboardAction( keyActions.getEscapeCloseWindows( "EscapeCloseWindow"), 
													KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0), 
													JComponent.WHEN_IN_FOCUSED_WINDOW );
	}

	public Dimension getSceneSize()
	{
		super.pack();
		
		Dimension s = super.getSize();
		
		Dimension s2 = this.getTargetControllerIndicator().getPreferredSize();
		
		Insets inset = super.getInsets();
		
		s.height =  s.height - s2.height - inset.bottom - inset.top;
		s.width = s.width - inset.left - inset.right;
				
		return s;
	}
	
	public Rectangle getSceneBounds()
	{
		Dimension size = this.getSceneSize();
		Point loc = super.getLocation();
		
		Rectangle r = new Rectangle( loc , size );
				
		return r;
	}
	
	private JPanel getContainerPanel( List< Player > players )
	{
		if( this.contentPane == null )
		{
			this.contentPane = new JPanel();
			
			this.contentPane = new JPanel();
			
			this.contentPane.setLayout( new BorderLayout( 0, 0 ) );
			
			String[] distributions = new String[] { BorderLayout.SOUTH
													, BorderLayout.NORTH
													, BorderLayout.WEST
													, BorderLayout.EAST };
			
			this.contentPane.add( this.getGamePanel(), BorderLayout.CENTER );
			
			Map< String, JPanel > panelDistribution = new HashMap< String, JPanel >();
			for( int i = 0; i < players.size(); i++ )
			{
				Player player = players.get( i );
				
				int index = i % distributions.length;
				String dist = distributions[ index ];

				ControllerTargetBar ctgb = getTargetControllerIndicator();
				Dimension preSize = ctgb.getPreferredSize();			
				int s = preSize.height;
				
				int axis = BoxLayout.X_AXIS;
				
				if( dist.equals( BorderLayout.WEST ) 
						|| dist.equals( BorderLayout.EAST ) )
				{
					axis = BoxLayout.Y_AXIS;
					
					int aux = preSize.height;
					preSize.height = preSize.width;
					preSize.width = aux;
				}

				JLabel playerIco = new JLabel();
				playerIco.setIcon( player.getImg( s, s ) );
				
				JPanel controllerPanel = panelDistribution.get( dist );
				if( controllerPanel == null )
				{
					controllerPanel = new JPanel();
					
					controllerPanel.setLayout( new BoxLayout( controllerPanel, axis ) );
					panelDistribution.put( dist, controllerPanel );
					
					this.contentPane.add( controllerPanel, dist );
				}
				
				controllerPanel.add( playerIco );
				controllerPanel.add( ctgb );
				
				this.targetControllerIndicators.put( player, ctgb );
			}
		}
		
		return this.contentPane;
	}
	
	public JPanel getGamePanel()
	{
		if( this.gamePanel == null )
		{
			this.gamePanel = new JPanel( new BorderLayout() );
			this.gamePanel.setDoubleBuffered( true );
		}
		
		return this.gamePanel;
	}
		
	private ControllerTargetBar getTargetControllerIndicator()
	{		
		ControllerTargetBar targetControllerIndicator = new ControllerTargetBar( 0 );
		Dimension d = targetControllerIndicator.getPreferredSize();
		d.height *= 2;
			
		targetControllerIndicator.setPreferredSize( d );		
		
		return targetControllerIndicator;
	}	
	
	public void setTargetInputValues( Player player, double min, double max )
	{
		ControllerTargetBar lpi = this.targetControllerIndicators.get( player );
		
		double distance = Math.abs( max - min ) / 4 ;
		
		lpi.setMinimum( min - distance );		
		lpi.setMaximum( max + distance );		
		lpi.setLevels( new double[] { min, max } );
		
		lpi.setLevelColor( new Color[] { Color.WHITE, Color.YELLOW, Color.GREEN } );
	}
	
	public void putControllerListener()
	{
		for( Player player : this.targetControllerIndicators.keySet() )
		{
			ControllerTargetBar ctb = this.targetControllerIndicators.get( player );
			
			ControllerManager.getInstance().addControllerListener( player, ctb );
		}
	}
}
