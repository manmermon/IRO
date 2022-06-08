package testing.experiments;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfugue.pattern.Pattern;

import GUI.panel.DisabledPanel;
import config.ConfigApp;
import config.ConfigParameter;
import config.Player;
import config.Settings;
import control.events.BackgroundMusicEvent;
import control.events.BackgroundMusicEventListener;
import gui.game.screen.level.music.BackgroundMusic;
import gui.panel.SelectSongPanel;
import testing.experiments.synMarker.SyncMarker;
import testing.experiments.synMarker.SyncMarker.Marker;
import tools.MusicSheetTools;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JToggleButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.awt.event.ActionEvent;

public class testSinIRO extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private final JLabel lblBeep = new JLabel("Periodo Beeps (s): ");
	private final JPanel panel = new JPanel();
	private final JSpinner spinner = new JSpinner();
	private final JPanel panel_1 = new JPanel();
	private final JToggleButton tglbtnStart = new JToggleButton("Start");
	
	private DisabledPanel disablePane = new DisabledPanel( SelectSongPanel.getInstance() );
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					testSinIRO frame = new testSinIRO();
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
	public testSinIRO() {
		initialize();
	}
	
	private void initialize() {
		
		SyncMarker.getInstance( "No"+ConfigApp.shortNameApp );
		setTitle( "Test sin " + ConfigApp.shortNameApp );
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
		
		this.contentPane.add( this.disablePane, BorderLayout.CENTER );
		
		this.contentPane.add(this.panel, BorderLayout.NORTH);
		this.panel.setLayout(new BorderLayout(0, 0));
		this.panel.add(this.lblBeep, BorderLayout.WEST );
		
		
		this.spinner.setModel(new SpinnerNumberModel(new Double(1), new Double(0.1), null, new Double(0.5)));
				
		this.spinner.addMouseWheelListener( new MouseWheelListener() 
		{				
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) 
			{
				if( e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL )
				{
					try
					{	
						JSpinner sp = (JSpinner)e.getSource();
						
						int d = e.getWheelRotation();
						
						if( d > 0 )
						{
							sp.setValue( sp.getModel().getPreviousValue() );
						}
						else
						{
							sp.setValue( sp.getModel().getNextValue() );
						}	
					}
					catch( IllegalArgumentException ex )
					{												
					}
				}
			}
		});
		
		this.panel.add(this.spinner, BorderLayout.CENTER);
		
		this.contentPane.add(this.panel_1, BorderLayout.SOUTH);
		this.tglbtnStart.addActionListener(new ActionListener() 
		{
			Thread beepThr = null;
			Thread playerThr = null;
			
			public void actionPerformed(ActionEvent e) 
			{	
				if( tglbtnStart.isSelected() )
				{
					SelectSongPanel.getInstance().setEnabled( false );
					tglbtnStart.setText( "Stop" );
					
					disablePane.setEnabled( false );
					
					spinner.setEnabled( false );
					
					Player player = ConfigApp.getFirstPlayer();
					Settings cfg = null;
					if( player != null )
					{
						cfg = ConfigApp.getPlayerSetting( player );
					}
							
					if( cfg != null )
					{
						ConfigParameter par = cfg.getParameter( ConfigApp.SONG_LIST );
							
						if( par != null )
						{
							final Object songs = par.getSelectedValue();
							
							if( songs != null )
							{
								playerThr = new Thread() 
								{
									public void run() 
									{
										BackgroundMusic bgm = null;
										
										final Thread playerThread = playerThr;
										for( String song : songs.toString().split( ";") )
										{	
											try
											{
												Pattern pattern = MusicSheetTools.getPatternFromMidi( new File( song ) );
												
												bgm = new BackgroundMusic();
												bgm.addBackgroundMusicEventListener( new BackgroundMusicEventListener() 
												{													
													@Override
													public void BackgroundMusicEvent(BackgroundMusicEvent event) 
													{	
														if( event.getType() == BackgroundMusicEvent.END )
														{
															synchronized( playerThread )
															{
																playerThread.notify();
															}
														}
													}
												});
												
												bgm.setPattern( pattern );
												bgm.startActing();
												
												SyncMarker.getInstance( "No"+ConfigApp.shortNameApp ).sendMarker( Marker.START_MUSIC );
												
												synchronized( this )
												{
													this.wait();
												}
											}
											catch ( Exception ex1)
											{
												if( bgm != null )
												{
													bgm.stopActing( BackgroundMusic.FORCE_STOP );
													bgm = null;
													
													break;
												}
											}					
										}
										
										tglbtnStart.doClick();
									}
								};
							}
						}
					}	
					
					try 
					{
						beepThr = new Thread()
						{
							boolean stop = false;
							
							public void run() 
							{	
								long waitTime = (long)( 1000 * Double.parseDouble( spinner.getValue().toString() ) ); 
								SelectionBeep beep = null;
								
								try 
								{
									beep = new SelectionBeep();
									
									beep.startThread();
								} 
								catch ( Exception e1) 
								{
									e1.printStackTrace();
									
									stop = true;
								}
								
								long delay = 0;
								
								while( !stop )
								{
									try
									{
										synchronized( this )
										{											
											if( delay > 0 )
											{
												delay = ( System.currentTimeMillis() - delay );
											}
											
											if( delay < waitTime )
											{
												this.wait( waitTime - delay );
											}
										}

										delay = System.currentTimeMillis();
										
										beep.play();
									}
									catch ( InterruptedException e) 
									{
										if( beep != null )
										{
											beep.stopThread( SelectionBeep.FORCE_STOP );
											beep = null;
										}
										
										stop = true;
									}
								}
							};
						};
						
						SyncMarker.getInstance( "No"+ConfigApp.shortNameApp ).sendMarker( Marker.START_TEST );
						
						playerThr.start();
						beepThr.start();
						
					} 
					catch ( Exception e1) 
					{
						e1.printStackTrace();
						
						beepThr = null;
					}
				}
				else
				{
					SyncMarker.getInstance( "No"+ConfigApp.shortNameApp ).sendMarker( Marker.STOP_TEST );
					
					if( beepThr != null )
					{
						beepThr.interrupt();
						
						beepThr = null;
					}
					
					if( playerThr != null )
					{
						playerThr.interrupt();
						
						playerThr = null;
					}
				
					tglbtnStart.setText( "Start" );
					
					spinner.setEnabled( true );
					disablePane.setEnabled( true );
				}
			}
		});
		
		this.panel_1.add(this.tglbtnStart);
	}
}
