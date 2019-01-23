package testing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import GUI.AppIcons;

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
					frame.setExtendedState( frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
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
		setBounds(20, 10, 600, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.X_AXIS ) );
		setContentPane(contentPane);
		
		int[] size = new int[] { 16, 20, 32, 48, 64, 128, 256, 512 };
		for( int i = 0; i < size.length; i++ )
		{
			JButton b = new JButton();
			b.setIcon( new ImageIcon( AppIcons.Maraca( size[ i ], Color.BLACK ) ) );
			contentPane.add( b );
		}
		
	}

}
