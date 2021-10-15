/**
 * 
 */
package testing.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gui.mouseTracking;
import GUI.progressbar.TwoWayProgressBar;

/**
 * @author manuel
 *
 */
public class testTwoWayBar extends JFrame
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
					testTwoWayBar frame = new testTwoWayBar();
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
	public testTwoWayBar()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new FlowLayout() );
		this.contentPane.setLayout(new BorderLayout() );
		
		setContentPane(this.contentPane);
		
		//*
		final TwoWayProgressBar pb = new TwoWayProgressBar();
		pb.setEditable( false );
		pb.setAllowExceedExtremaValue( true );
		//*/
		
		JPanel p = new JPanel( new BorderLayout() );
		p.add( pb, BorderLayout.NORTH );
		
		JPanel p2 = new JPanel( new BorderLayout() );
		final JLabel lbmin = new JLabel( pb.getBarMinValue()  + "");
		final JLabel lbmax = new JLabel( pb.getBarMaxValue()  + "");
		p2.add( lbmin, BorderLayout.WEST );
		p2.add( lbmax, BorderLayout.EAST );
		//p.add( p2, BorderLayout.NORTH );
		
		
		pb.addMouseMotionListener( new MouseMotionListener()
		{
			
			@Override
			public void mouseMoved(MouseEvent arg0)
			{
				double middle = pb.getBarMiddleValue();
				
				int x = arg0.getX();

				pb.setLeftValue( 0 );
				pb.setRightValue( 0 );
				
				if( x < middle )
				{
					pb.setLeftValue( x );
				}
				else
				{
					pb.setRightValue( x );
				}				
			}
			
			@Override
			public void mouseDragged(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		pb.addChangeListener( new ChangeListener()
		{	
			@Override
			public void stateChanged(ChangeEvent e)
			{
				TwoWayProgressBar pb = (TwoWayProgressBar)e.getSource();
				
				double min = pb.getBarMinValue();
				double max = pb.getBarMaxValue();
				double middle = pb.getBarMiddleValue();
				
				double leftval = pb.getLeftValue();
				double rightval = pb.getRightValue();

				if( leftval < min )
				{
					pb.setExtremaBarValues(leftval, middle, max);
					lbmin.setText( leftval + "");					
				}
				
				if( rightval > max )
				{
					pb.setExtremaBarValues(min, middle, rightval);
					lbmax.setText( rightval + "" );
				}
			}
		});
		
		mouseTracking mt = new mouseTracking( pb );
		try
		{
			mt.startThread();
		}
		catch (Exception ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		this.contentPane.add( p );
	}

}
