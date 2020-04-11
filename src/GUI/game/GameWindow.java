/**
 * 
 */
package GUI.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import GUI.LevelIndicator;
import GUI.LevelProgressIndicator;

/**
 * @author manuel
 *
 */
public class GameWindow extends JFrame
{

	private JPanel contentPane;
	private JPanel gamePanel;
	private JPanel targetControllerPanel;
	
	private LevelProgressIndicator targetControllerIndicator;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					GameWindow frame = new GameWindow();
					frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GameWindow()
	{
		super.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		super.setBounds(100, 100, 450, 300);
		
		super.setContentPane( this.getContainerPanel() );		
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
	
	public LevelProgressIndicator getTargetControllerIndicator()
	{
		if( this.targetControllerIndicator == null )
		{
			this.targetControllerIndicator = new LevelProgressIndicator( 3 );
			this.targetControllerIndicator.setColorLevels( new Color[] { Color.YELLOW, Color.WHITE, Color.GREEN } );
			
			this.targetControllerIndicator.setEditable( true );
		}
		
		return this.targetControllerIndicator;
	}	
}
