package testing.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import image.basicPainter2D;
import image.icon.MusicInstrumentIcons;

public class testAppIcons extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					testAppIcons frame = new testAppIcons();
					frame.setVisible(true);
					//frame.setExtendedState( frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public testAppIcons() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(20, 10, 200, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout( new GridLayout( 0, 10 ) );
		setContentPane( new JScrollPane( contentPane ) );
		
		/*
		int[] size = new int[] { 16, 20, 32, 48, 64, 128, 256, 512 };
		for( int i = 0; i < size.length; i++ )
		{
			JButton b = new JButton();
			b.setIcon( new ImageIcon( MusicInstrumentIcons.Maraca( size[ i ], Color.BLACK ) ) );
			contentPane.add( b );
		}
		//*/
		
		/*
		int[] size = new int[] { 128 };
		for( int i = 0; i < 114; i++ )
		{
			for( int s = 0; s < size.length; s++ )
			{
				JButton b = new JButton();
				b.setIcon( new ImageIcon( MusicInstrumentIcons.getInstrument( (byte)i, size[ s ], Color.RED ) ) );
				contentPane.add( b );
			}
		}
		//*/
		
		final int size = 128;
		
		final JButton b = new JButton();		
		contentPane.add( b );
		
		Thread t = new Thread()
		{
			public void run() 
			{
				float p = 0;
				float inc = 1;
				
				float h = 0; 
				while( p < 100 )
				{	
					System.out.println("testAppIcons p = " + p );
					
					float arc = 359 * p / 100;
					//b.setVisible( false );
					
					BufferedImage img = (BufferedImage)basicPainter2D.arc( 0, 0
															, size, size 
															, 90, -(int)arc
															, size / 8, Color.BLACK
															, null, null ); 
							
					basicPainter2D.arc( 0, 0
										, size, size 
										, 90, -(int)arc
										, size / 10, Color.GREEN
										, null, img );						
					b.setIcon( new ImageIcon( img ) );
					//b.setVisible( true );
					try
					{
						Thread.sleep( 100 );
					} catch (InterruptedException ex)
					{
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					p += inc;
				}
				System.out.println("testAppIcons END");
				
				try
				{
					Thread.sleep( 2000  );
				} catch (InterruptedException ex)
				{
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				
				System.exit( 0 );
			};
		};
		t.start();
		
		
		//*/
	}

}
