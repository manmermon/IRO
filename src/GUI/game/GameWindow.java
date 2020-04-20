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

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import GUI.AppIcon;
import GUI.GameManager;
import GUI.keyActions;
import GUI.game.component.ControllerTargetBar;
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
	private JPanel targetControllerPanel;
	
	private ControllerTargetBar targetControllerIndicator;
	
	/**
	 * Create the frame.
	 */
	public GameWindow()
	{
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); 
		Insets ins = Toolkit.getDefaultToolkit().getScreenInsets( this.getGraphicsConfiguration() );
		
		Rectangle bounds = new Rectangle( 0, 0
										, d.width - ins.left - ins.right
										, d.height - ins.bottom - ins.top );
		
		super.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		
		super.setPreferredSize( bounds.getSize() );
		//super.setLocation( bounds.getLocation() );
		
		super.setResizable( false );
		
		super.setContentPane( this.getContainerPanel() );
		
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
					GameManager.getInstance().stopLevel( );
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
	
	private JPanel getContainerPanel()
	{
		if( this.contentPane == null )
		{
			this.contentPane = new JPanel();
			
			this.contentPane = new JPanel();
			//this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			this.contentPane.setLayout(new BorderLayout(0, 0));
			
			this.contentPane.add( this.getGamePanel(), BorderLayout.CENTER );
			this.contentPane.add( this.getTargetControllerPanel(), BorderLayout.SOUTH );
		}
		
		return this.contentPane;
	}
	
	public JPanel getGamePanel()
	{
		if( this.gamePanel == null )
		{
			this.gamePanel = new JPanel( new BorderLayout() );
		}
		
		return this.gamePanel;
	}
	
	private JPanel getTargetControllerPanel()
	{
		if( this.targetControllerPanel == null )
		{
			this.targetControllerPanel = new JPanel( new BorderLayout() );
			
			this.targetControllerPanel.add( this.getTargetControllerIndicator(), BorderLayout.CENTER );
			
		}
		
		return this.targetControllerPanel;
	}
	
	private ControllerTargetBar getTargetControllerIndicator()
	{
		if( this.targetControllerIndicator == null )
		{
			this.targetControllerIndicator = new ControllerTargetBar( 0 );
			Dimension d = this.targetControllerIndicator.getPreferredSize();
			d.height *= 2;
			
			this.targetControllerIndicator.setPreferredSize( d );
		}
		
		return this.targetControllerIndicator;
	}	
	
	public void setTargetInputValues( double min, double max )
	{	
		ControllerTargetBar lpi = this.getTargetControllerIndicator();
		
		double distance = Math.abs( max - min ) / 4 ;
		
		lpi.setMinimum( min - distance );		
		lpi.setMaximum( max + distance );		
		lpi.setLevels( new double[] { min, max } );
		
		lpi.setLevelColor( new Color[] { Color.WHITE, Color.YELLOW, Color.GREEN } );
	}
	
	public void putControllerListener()
	{
		ControllerManager.getInstance().addControllerListener( this.getTargetControllerIndicator() );
	}
}
