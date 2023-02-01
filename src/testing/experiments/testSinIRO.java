package testing.experiments;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import stoppableThread.IStoppable;
import testing.experiments.synMarker.SyncMarker;
import testing.experiments.synMarker.SyncMarker.Marker;
import thread.timer.ActionTimerThread;
import thread.timer.IAction;
import thread.timer.Timer;
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
import javax.swing.JCheckBox;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class testSinIRO extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private final JLabel lblBeep = new JLabel("Periodo (s): ");
	private final JPanel panel = new JPanel();
	private final JSpinner spinnerBeepTime = new JSpinner();
	private final JPanel panel_1 = new JPanel();
	private final JToggleButton tglbtnStart = new JToggleButton("Start");
	
	private JPanel mainPane = new JPanel( new BorderLayout() );
	private final JCheckBox chckbxBeepAct = new JCheckBox("Activar Beep");
	private DisabledPanel disablePane = new DisabledPanel( SelectSongPanel.getInstance() );
	
	private JLabel tiempoTranscurridoLabel = new JLabel( "Tiempo Transcurrido:");
	private JLabel tiempoTranscurridoValor = new JLabel( );
	
	private Timer semaforoTimer = new Timer( 750, false, new ActionTimerThread( new IAction() 
	{		
		@Override
		public void execute() 
		{
			mainPane.setBackground( Color.WHITE );
		}
	}) );
	
	private Timer sessionTimer = null;
	private Timer taskBlockTimer = null;
	private Timer tiempoTranscurridoTimer = null;
	private long restTime = 0;
	private Object sync = new Object();
	private boolean endSession = false;
	
	private final JCheckBox chckbxSem = new JCheckBox("Sem\u00E1foro");
	private final JPanel panel_2 = new JPanel();
	private final JLabel lblBlqAct = new JLabel("Actividad (s)");
	private final JSpinner spinnerBlqAct = new JSpinner();
	private final JLabel lblBlqDesc = new JLabel("Descanso (s)");
	private final JSpinner spinnerDescanso = new JSpinner();
	private final JLabel lblSesion = new JLabel("Tiempo Sesi\u00F3n (s)");
	private final JSpinner spinnerSession = new JSpinner();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JToggleButton btnPause = new JToggleButton("Pause");
	
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
		
		synchronized( this )
		{
			try 
			{
				semaforoTimer.setName( "Semaforo Timer");
				semaforoTimer.startThread();
				super.wait( 100L );
				semaforoTimer.stopTimer();
			}
			catch (Exception e2) 
			{
				e2.printStackTrace();
			}
		}
		
		Dimension d = new Dimension( 50, 20 );
		
		spinnerBeepTime.setPreferredSize( d );
		spinnerSession.setPreferredSize( d );
		spinnerBlqAct.setPreferredSize( d );
		spinnerDescanso.setPreferredSize( d );
		
		scrollPane.getHorizontalScrollBar().setBlockIncrement( 10 );
		scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
		
		SyncMarker.getInstance( "No"+ConfigApp.shortNameApp );
		setTitle( "Test sin " + ConfigApp.shortNameApp );
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 400);
		
		
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
		
		this.mainPane.add( disablePane );
		this.mainPane.setBackground( Color.WHITE );
		this.contentPane.add( this.mainPane, BorderLayout.CENTER );
		
		this.contentPane.add(this.panel, BorderLayout.NORTH);
		this.panel.setLayout(new BorderLayout(0, 0));
		FlowLayout flowLayout = (FlowLayout) this.panel_2.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		
		scrollPane.setViewportView( this.panel_2 );
		this.panel.add( this.scrollPane, BorderLayout.CENTER);
		
		this.panel_2.add(this.lblSesion);
		this.spinnerSession.setModel(new SpinnerNumberModel(new Double(180), new Double(30), null, new Double(1)));
		
		this.panel_2.add(this.spinnerSession);
		
		this.panel_2.add(this.lblBlqAct);
		this.spinnerBlqAct.setModel(new SpinnerNumberModel(new Integer(45), new Integer(1), null, new Integer(1)));
		
		this.panel_2.add(this.spinnerBlqAct);
		
		this.panel_2.add(this.lblBlqDesc);
		this.spinnerDescanso.setModel(new SpinnerNumberModel(new Integer(15), new Integer(0), null, new Integer(1)));
		
		this.panel_2.add(this.spinnerDescanso);
		this.panel_2.add(this.lblBeep);
		this.panel_2.add(this.spinnerBeepTime);
		
		this.spinnerBeepTime.setModel(new SpinnerNumberModel(new Double(3), new Double(0), null, new Double(0)));
		
		this.spinnerBeepTime.addMouseWheelListener( new MouseWheelListener() 
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
		this.panel_2.add(this.chckbxBeepAct);
		this.chckbxBeepAct.setSelected(true);
		this.panel_2.add(this.chckbxSem);
		
		this.panel.add(this.scrollPane, BorderLayout.CENTER);
		
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
					
					if( sessionTimer != null )
					{
						sessionTimer.stopThread( IStoppable.FORCE_STOP );
						sessionTimer = null;
					}
					
					if( taskBlockTimer != null )
					{
						taskBlockTimer.stopThread( IStoppable.FORCE_STOP );
						taskBlockTimer = null;
					}
					
					if( tiempoTranscurridoTimer != null )
					{
						tiempoTranscurridoTimer.stopThread( IStoppable.FORCE_STOP );
						tiempoTranscurridoTimer = null;
					}
					
					restTime = 0;
										
					mainPane.setVisible( false );
					mainPane.setBackground( Color.WHITE );
					
					disablePane.setEnabled( false );										
					disablePane.setVisible( !chckbxSem.isSelected() );
					//mainPane.removeAll();
					
					mainPane.setVisible( true );
					
					spinnerBeepTime.setEnabled( false );
					spinnerSession.setEnabled( false );
					spinnerBlqAct.setEnabled( false );
					spinnerDescanso.setEnabled( false );
					chckbxBeepAct.setEnabled( false );
					
					chckbxSem.setEnabled( false );
					endSession = false;
					
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
												
												bgm.setName( "BackgroundMusic" );
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
												if( !( ex1 instanceof InterruptedException ) )
												{
													ex1.printStackTrace();
												}
												if( bgm != null )
												{
													bgm.stopActing( BackgroundMusic.FORCE_STOP );
													bgm = null;
													
													break;
												}
											}					
										}
										
										synchronized ( sync )
										{
											if( !endSession )
											{
												endSession = true;

												tglbtnStart.doClick();
											}
										}
									}
								};
							
								playerThr.setName( "Player thread" );
							}
						}
					}	
					
					try 
					{
						long sessionTime = (long)(1000*Double.parseDouble( spinnerSession.getValue().toString() ));
						sessionTimer = new Timer( sessionTime, false, new ActionTimerThread( new IAction() {							
							@Override
							public void execute() 
							{	
								synchronized ( sync )
								{
									if( !endSession )
									{
										endSession = true;

										tglbtnStart.doClick();
									}
								}
							}
						}));
						sessionTimer.setName( "Session Timer");
						
						final long waitBeepTime = (long)( 1000 * Double.parseDouble( spinnerBeepTime.getValue().toString() ) );
						
						long taskBlockTime = (long)(1000*Double.parseDouble( spinnerBlqAct.getValue().toString() ));
						taskBlockTimer = new Timer(taskBlockTime, false, new ActionTimerThread( new IAction() 
						{	
							@Override
							public void execute() 
							{
								synchronized ( sync ) 
								{
									restTime = (long)(1000*Double.parseDouble( spinnerDescanso.getValue().toString() ));
									restTime -= waitBeepTime;
								}
							}
						} ) );
						taskBlockTimer.setName( "Task block timer" );
						
						if( waitBeepTime > 0 )
						{
							final boolean beepAct = chckbxBeepAct.isSelected();
							beepThr = new Thread()
							{
								boolean stop = false;
								
								public void run() 
								{										 
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
												
												if( delay < waitBeepTime )
												{
													boolean restartTaskBlockTimer =  false;
													
													long time = waitBeepTime - delay;
													
													synchronized( sync )
													{
														time += restTime;
														restartTaskBlockTimer = ( restTime > 0 );
														restTime = 0;															
													}
													
													this.wait( time );
													
													if( restartTaskBlockTimer )
													{
														taskBlockTimer.restartTimer();
													}
												}
											}
	
											delay = System.currentTimeMillis();
											
											if( beepAct )
											{
												beep.play();
												
												SyncMarker.getInstance( "No"+ConfigApp.shortNameApp ).sendMarker( Marker.BEEP );
											}											
											
											semaforoTimer.stopTimer();
											
											mainPane.setBackground( Color.GREEN );
											
											semaforoTimer.restartTimer();
											
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
						
							beepThr.setName( "Beep timer" );
						}
						
						SyncMarker.getInstance( "No"+ConfigApp.shortNameApp ).sendMarker( Marker.START_TEST );
						
						tiempoTranscurridoTimer = new Timer( 1000L, true, new ActionTimerThread( new IAction() 
						{
							private long timeRef = -1;
							@Override
							public void execute() 
							{
								if( timeRef < 0 )
								{
									timeRef = System.nanoTime();
								}
								
								long nanoseconds = System.nanoTime() - timeRef + 1_000_000_000L; // Le sumo 1 segundo que es el tiempo de la primera ejecucion)
								
								long microseconds = nanoseconds / 1000;
								long miliseconds = microseconds / 1000;
								long seconds = miliseconds / 1000;
								long minutes = seconds / 60;
								long hours = minutes / 60;

								String timeStamp = String.format( "%02d:%02d:%02d", hours, minutes%60, seconds%60 );
								tiempoTranscurridoValor.setText( timeStamp );
							}
						}));
						
						tiempoTranscurridoTimer.setName( "Tiempo transcurrido Timer");
						
						if( tiempoTranscurridoTimer != null )
						{
							tiempoTranscurridoTimer.startThread();
						}
						
						if( playerThr != null )
						{
							playerThr.start();
						}
						
						if( beepThr != null )
						{
							beepThr.start();
						}
						
						if( sessionTimer != null )
						{
							sessionTimer.startThread();
						}
						
						if( taskBlockTimer != null )
						{
							taskBlockTimer.startThread();
						}
					} 
					catch ( Exception e1) 
					{
						e1.printStackTrace();
						
						
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
						
						if( sessionTimer != null )
						{
							sessionTimer.stopThread( IStoppable.FORCE_STOP );
							
							sessionTimer = null;
						}
						
						if( taskBlockTimer != null )
						{
							taskBlockTimer.stopThread( IStoppable.FORCE_STOP );
							
							taskBlockTimer = null;
						}
						
						/*
						if( semaforoTimer != null )
						{
							semaforoTimer.stopThread( IStoppable.FORCE_STOP );
							
							semaforoTimer = null;
						}
						*/
						
						if( tiempoTranscurridoTimer != null )
						{
							tiempoTranscurridoTimer.stopThread( IStoppable.FORCE_STOP );
							tiempoTranscurridoTimer = null;
						}
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
					
					if( sessionTimer != null )
					{
						sessionTimer.stopThread( IStoppable.FORCE_STOP );
						
						sessionTimer = null;
					}
					
					if( taskBlockTimer != null )
					{
						taskBlockTimer.stopThread( IStoppable.FORCE_STOP );
						
						taskBlockTimer = null;
					}
					
					/*
					if( semaforoTimer != null )
					{
						semaforoTimer.stopThread( IStoppable.FORCE_STOP );
						
						semaforoTimer = null;
					}
					*/
					
					if( tiempoTranscurridoTimer != null )
					{
						tiempoTranscurridoTimer.stopThread( IStoppable.FORCE_STOP );
						tiempoTranscurridoTimer = null;
					}
				
					tglbtnStart.setText( "Start" );
					
					spinnerBeepTime.setEnabled( true );
					spinnerSession.setEnabled( true );
					spinnerBlqAct.setEnabled( true );
					spinnerDescanso.setEnabled( true );
					mainPane.setEnabled( true );
					chckbxBeepAct.setEnabled( true );
					
					chckbxSem.setEnabled( true );
					
					mainPane.setVisible( false );
					//mainPane.add( SelectSongPanel.getInstance() );
					disablePane.setEnabled( true );										
					disablePane.setVisible( true );
					
					mainPane.setVisible( true );
				}
			}
		});
		
		this.panel_1.add(this.tglbtnStart);
		
		this.panel_1.add(this.btnPause);
		this.panel_1.add( this.tiempoTranscurridoLabel );
		this.panel_1.add( this.tiempoTranscurridoValor );
		
		this.btnPause.setVisible( false );
	}
}
