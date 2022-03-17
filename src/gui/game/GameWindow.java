/**
 * 
 */
package gui.game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import gui.GameManager;
import GUI.action.keyActions;
import gui.game.component.ControllerTargetBar;
import gui.game.component.Frame;
import config.Player;

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
	private Frame gamePanel;
	
	//private Map< Player,  ControllerTargetBar > targetControllerIndicators;
	
	/**
	 * Create the frame.
	 */
	public GameWindow( List< Player > players )
	{
		if( players == null || players.isEmpty() )
		{
			throw new IllegalArgumentException( "Player list is null or empty." );
		}		
		
		//this.targetControllerIndicators = new HashMap<Player, ControllerTargetBar>();
		
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
		
		super.setIgnoreRepaint( true  );
				
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
	}

	public void setWindowsKeyStrokeAction()
	{
		super.getRootPane().registerKeyboardAction( keyActions.getEscapeCloseWindows( "EscapeCloseWindow"), 
													KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0), 
													JComponent.WHEN_IN_FOCUSED_WINDOW );
	}
	
	public Dimension getSceneSize()
	{
		super.pack();
		
		Dimension s = super.getSize();
		
		Dimension s2 = this.getTargetControllerIndicator( 0 ).getPreferredSize();
		
		Insets inset = super.getInsets();
		
		s.height =  s.height - s2.height - inset.bottom - inset.top;
		s.width = s.width - inset.left - inset.right;
				
		return s;
	}
	
	public Rectangle getSceneBounds()
	{
		Dimension size = this.getSceneSize();
		//Point loc = super.getLocation();
		Point loc = new Point();
		
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
						
			this.contentPane.add( this.getGamePanel(), BorderLayout.CENTER );
		}
		
		return this.contentPane;
	}
	
	public Frame getGamePanel()
	{
		if( this.gamePanel == null )
		{
			this.gamePanel = new Frame();
			
			this.gamePanel.setLayout( new BorderLayout() );
			this.gamePanel.setDoubleBuffered( true );
			this.gamePanel.setIgnoreRepaint( true );
		}
		
		return this.gamePanel;
	}
		
	private ControllerTargetBar getTargetControllerIndicator( int channel )
	{		
		ControllerTargetBar targetControllerIndicator = new ControllerTargetBar( channel );
		Dimension d = targetControllerIndicator.getPreferredSize();
		d.height *= 2;
			
		targetControllerIndicator.setPreferredSize( d );		
		
		return targetControllerIndicator;
	}		
}
