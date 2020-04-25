/**
 * 
 */
package testing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import GUI.progressbar.LevelIndicator;
import GUI.progressbar.LevelProgressIndicator;

/**
 * @author manuel
 *
 */
public class testLevelIndicator extends JFrame
{

	private JPanel contentPane;

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
					testLevelIndicator frame = new testLevelIndicator();
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
	public testLevelIndicator()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
				
		/*
		this.contentPane.add( level1, BorderLayout.WEST );
		this.contentPane.add( level2, BorderLayout.EAST );
		this.contentPane.add( level3, BorderLayout.NORTH );
		this.contentPane.add( level4, BorderLayout.SOUTH );
		//*/
		
		//*
		double min = -.9, max = -.85, l1 = -.89, l2 = -.87;
		LevelProgressIndicator progLevel = new LevelProgressIndicator(  );
		
		progLevel.setInset( new Insets( 0, 20, 0, 20 ));
		progLevel.setEditable( true );
		
		LevelProgressIndicator progLevel2 = new LevelProgressIndicator( min, max, new double[] { l1, l2 } );
		progLevel2.setEditable( true );
		progLevel2.setOrientation( LevelProgressIndicator.VERTICAL );
		
		LevelProgressIndicator progLevel3 = new LevelProgressIndicator( min, max, new double[] { l1, l2 } );
		progLevel3.setEditable( true );
		progLevel3.setInverted( true );
		
		LevelProgressIndicator progLevel4 = new LevelProgressIndicator( min, max, new double[] { l1, l2 } );
		progLevel4.setEditable( true );
		progLevel4.setOrientation( LevelProgressIndicator.VERTICAL );
		progLevel4.setInverted( true );
		
		
		this.contentPane.add( progLevel, BorderLayout.SOUTH );
		this.contentPane.add( progLevel2, BorderLayout.EAST );		
		this.contentPane.add( progLevel3, BorderLayout.NORTH );
		this.contentPane.add( progLevel4, BorderLayout.WEST );		
		//*/
		
		/*
		LevelIndicator progLevel = new LevelIndicator( 3 );
		progLevel.setEditable( true );
		
		LevelIndicator progLevel2 = new LevelIndicator( 3 );
		progLevel2.setEditable( true );
		progLevel2.setOrientation( LevelIndicator.VERTICAL );
		
		LevelIndicator progLevel3 = new LevelIndicator( 3 );
		progLevel3.setEditable( true );
		progLevel3.setInverted( true );
		
		LevelIndicator progLevel4 = new LevelIndicator( 3  );
		progLevel4.setEditable( true );
		progLevel4.setOrientation( LevelIndicator.VERTICAL );
		progLevel4.setInverted( true );
		
		
		this.contentPane.add( progLevel, BorderLayout.SOUTH );
		this.contentPane.add( progLevel2, BorderLayout.EAST );		
		this.contentPane.add( progLevel3, BorderLayout.NORTH );
		this.contentPane.add( progLevel4, BorderLayout.WEST );
		//*/
	}

}
