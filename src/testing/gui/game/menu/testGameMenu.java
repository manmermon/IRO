package testing.gui.game.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import gui.game.component.Frame;
import gui.game.component.sprite.Pause;
import gui.game.screen.IScene;
import gui.game.screen.level.Level;
import gui.game.screen.menu.MenuGameResults;
import config.Player;
import general.Tuple;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class testGameMenu extends JFrame {

	private JPanel contentPane;
	
	private Frame frame;
	private JPanel panel;
	private JLabel lblMenu;
	private JLabel lblNum;
	private JSpinner spinner;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					testGameMenu frame = new testGameMenu();					
					frame.pack();
					frame.setBounds( new Rectangle(100, 100, 450, 300) );
					frame.setVisible(true);					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public testGameMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPane = new JPanel();
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		frame = new Frame();
		frame.setLayout( new BorderLayout() );
		frame.setBackground( Color.white );
		contentPane.add( this.frame, BorderLayout.CENTER );
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		lblMenu = new JLabel("Menu:");
		panel.add(lblMenu);
		
		JComboBox< String > comboBox = new JComboBox< String >();
		panel.add(comboBox);
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "---", "MenuGameResults", "PauseScreen"}));
		
		lblNum = new JLabel("Num. ");
		panel.add(lblNum);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		panel.add(spinner);
		
		comboBox.addItemListener( new ItemListener() 
		{			
			@Override
			public void itemStateChanged(ItemEvent ev) 
			{
				showMenu( ev.getItem().toString() );
			}
		});
	}	
	
	private void showMenu( String menuID )
	{		
		this.frame.setVisible( false );
		this.frame.removeAll();
		
		switch ( menuID )
		{
			case "MenuGameResults":
			{
				List< Tuple< Player, Double > > res = new ArrayList<Tuple<Player,Double>>();
				
				for( int i = 0; i < ((Integer)spinner.getValue()); i++ )
				{
					Player p = new  Player( i, ( 'A' + i ) + "", null );
					double v = (int)( Math.random() * 10000 );
					
					res.add( new Tuple<Player, Double>( p, v ) );
				}
				
				MenuGameResults mgr = new MenuGameResults( this.frame.getSize(), res, true, true );
			
				this.frame.add( mgr.getMenuFrame(), BorderLayout.CENTER );  
				
				break;
			}
			case "PauseScreen":
			{
				Pause p = new Pause( this.frame.getSize(), "Pause" );
				Level lv = new Level( this.frame.getSize() );
				lv.add( p, IScene.PLANE_PAUSE );
				
				Frame f = new Frame();
				BufferedImage s = lv.getScene();
				f.setScene( s );
				
				this.frame.add( f, BorderLayout.CENTER );
				
				break;
			}
			default:
			{
				break;				
			}
		}
		
		this.frame.setVisible( true );
	}
}
