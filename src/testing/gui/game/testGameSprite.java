package testing.gui.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import gui.game.component.Frame;
import gui.game.component.sprite.ISprite;
import gui.game.component.sprite.Loading;
import gui.game.component.sprite.Pause;
import gui.game.screen.IScene;
import gui.game.screen.level.Level;
import gui.game.screen.menu.MenuGameResults;
import stoppableThread.IStoppableThread;
import thread.stoppableThread.AbstractStoppableThread;
import config.ConfigApp;
import config.Player;
import config.Settings;
import exceptions.ConfigParameterException;
import general.Tuple;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class testGameSprite extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	
	private Frame frame;
	private JPanel panel;
	private JLabel lblMenu;
	private JLabel lblNum;
	private JSpinner spinner;

	private enum OPTS { None, MenuGameResults, PauseScreen, LoadingScreen};
	
	private AbstractStoppableThread animationThread = null;
	
	private Level lv = null;
	
	private Object lock = new Object();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					testGameSprite frame = new testGameSprite();					
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
	 * @throws Exception 
	 */
	public testGameSprite() throws Exception 
	{
		
		if( animationThread != null )
		{
			animationThread.stopThread( IStoppableThread.FORCE_STOP );
		}
		
		animationThread = new AbstractStoppableThread() {
			
			@Override
			protected void runInLoop() throws Exception 
			{
				synchronized ( this )
				{
					super.wait( 500L );
				}
				
				synchronized( lock )
				{
					if( lv != null )
					{
						lv.updateLevel();
						frame.setVisible( false );
						frame.removeAll();
						frame.setScene( lv.getScene() );
						frame.setVisible( true );
					}					
				}
			}
			
			@Override
			protected void preStopThread(int friendliness) throws Exception 
			{	
			}
			
			@Override
			protected void postStopThread(int friendliness) throws Exception 
			{	
			}
		};
		
		animationThread.startThread();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		super.addWindowListener( new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent e) 
			{
				super.windowClosing(e);
				
				if( animationThread != null )
				{
					animationThread.stopThread( IStoppableThread.FORCE_STOP );
				}
			}
		});
		
		contentPane = new JPanel();
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		frame = new Frame();
		frame.setFocusable( false );
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
		comboBox.setModel(new DefaultComboBoxModel( OPTS.values() ) );
		
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
				frame.setVisible( false );
				frame.removeAll();
				
				try 
				{
					synchronized( lock )
					{
						getSprite( (OPTS)ev.getItem() );
					}
				}
				catch ( Exception e) 
				{
				}
				finally 
				{
					frame.setVisible( true );
				}
			}
		});
	}	
	
	private void getSprite( OPTS idSprite ) throws Exception
	{				
		Settings defPlayer = ConfigApp.getDefaultSettings();
		defPlayer.setPlayer( new Player());
		
		List< Settings > players = new ArrayList< Settings >();
		players.add( defPlayer );
				
		this.lv = new Level( this.frame.getBounds(), players, null );
		
		ISprite sprite = null;
		int zIndex = Level.PLANE_BRACKGROUND;
		
		switch ( idSprite )
		{
			case MenuGameResults:
			{
				this.lv = null;
				List< Tuple< Player, Double > > res = new ArrayList<Tuple<Player,Double>>();
				
				for( int i = 0; i < ((Integer)spinner.getValue()); i++ )
				{
					Player p = new  Player( i, ( 'A' + i ) + "", null );
					double v = (int)( Math.random() * 10000 );
					
					res.add( new Tuple<Player, Double>( p, v ) );
				}
				
				MenuGameResults mgr = new MenuGameResults( this.frame.getSize(), res, null,true, true );
			
				this.frame.add( mgr.getMenuFrame(), BorderLayout.CENTER );  
				
				break;
			}
			case PauseScreen:
			{
				
				sprite = new Pause( this.frame.getSize(), "Pause" );	
				zIndex = Level.PLANE_PAUSE;
								
				break;
			}
			case LoadingScreen:
			{
				sprite = new Loading(  this.frame.getSize(), "Load" );
				zIndex = Level.PLANE_ALWAYS_IN_FRONT;
				
				break;
			}
			default:
			{
				break;				
			}
		}
		
		if( sprite != null && lv != null )
		{
			lv.add( sprite, zIndex );
			
			Frame f = new Frame();
			BufferedImage s = lv.getScene();
			f.setScene( s );
					
			this.frame.add( f, BorderLayout.CENTER );
		}
	}
}
