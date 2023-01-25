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
	private final JSpinner spinner = new JSpinner();
	private final JPanel panel_1 = new JPanel();
	private final JToggleButton tglbtnStart = new JToggleButton("Start");
	
	private JPanel mainPane = new JPanel( new BorderLayout() );
	private final JCheckBox chckbxBeepAct = new JCheckBox("Activar Beep");
	private DisabledPanel disablePane = new DisabledPanel( SelectSongPanel.getInstance() );
	
	private Timer taskTimer = new Timer( 750, false, new ActionTimerThread( new IAction() 
	{		
		@Override
		public void execute() 
		{
			mainPane.setBackground( Color.WHITE );
		}
	}) );
	private final JCheckBox chckbxSem = new JCheckBox("Sem\u00E1foro");
	private final JPanel panel_2 = new JPanel();
	private final JLabel lblBlqAct = new JLabel("Actividad (s)");
	private final JSpinner spinnerBlqAct = new JSpinner();
	private final JLabel lblBlqDesc = new JLabel("Descanso (s)");
	private final JSpinner spinnerDescanso = new JSpinner();
	private final JLabel lblSesion = new JLabel("Tiempo Sesi\u00F3n (s)");
	private final JSpinner spinner_1 = new JSpinner();
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
				taskTimer.startThread();
				super.wait( 100L );
				taskTimer.stopTimer();
			}
			catch (Exception e2) 
			{
				e2.printStackTrace();
			}
		}
		
		Dimension d = new Dimension( 50, 20 );
		
		spinner.setPreferredSize( d );
		spinner_1.setPreferredSize( d );
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
		this.spinner_1.setModel(new SpinnerNumberModel(new Double(180), new Double(30), null, new Double(1)));
		
		this.panel_2.add(this.spinner_1);
		
		this.panel_2.add(this.lblBlqAct);
		this.spinnerBlqAct.setModel(new SpinnerNumberModel(new Integer(45), new Integer(1), null, new Integer(1)));
		
		this.panel_2.add(this.spinnerBlqAct);
		
		this.panel_2.add(this.lblBlqDesc);
		this.spinnerDescanso.setModel(new SpinnerNumberModel(new Integer(15), new Integer(0), null, new Integer(1)));
		
		this.panel_2.add(this.spinnerDescanso);
		this.panel_2.add(this.lblBeep);
		this.panel_2.add(this.spinner);
		
		this.spinner.setModel(new SpinnerNumberModel(new Double(3), new Double(0), null, new Double(0)));
		
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
					
					mainPane.setVisible( false );
					mainPane.setBackground( Color.WHITE );
					
					disablePane.setEnabled( false );										
					disablePane.setVisible( !chckbxSem.isSelected() );
					//mainPane.removeAll();
					
					mainPane.setVisible( true );
					
					spinner.setEnabled( false );
					spinner_1.setEnabled( false );
					spinnerBlqAct.setEnabled( false );
					spinnerDescanso.setEnabled( false );
					chckbxBeepAct.setEnabled( false );
					
					chckbxSem.setEnabled( false );
					
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
												
												/*
												Pattern pt = new Pattern();
												short step = 20;
												for( Token tk :  pattern.getTokens() )
												{													
													if( Note.isValidNote( tk.toString() ) )
													{	
														Note n;
														try
														{
															n = new Note( tk.toString() );
																												
															short vol = n.getOnVelocity();
															vol -= step;
															
															if( vol < 0)
															{
																vol = 10;
															}
															
															n.setOnVelocity( (byte)vol );
															
															pt.add( n );
														}
														catch (Exception e) 
														{
															String t = tk.toString();
															
															int lastA = t.toLowerCase().lastIndexOf( "a" );
															int lastD = t.toLowerCase().lastIndexOf( "d" );
															
															if( lastA < 0 )
															{
																t += "a" + ( step > 64 ? 64 - step : 10 );
															}
															else
															{
																int end = lastD < 0 ? t.length() : lastD;
																							
																String vol = t.substring( lastA + 1, end );
																
																try
																{
																	int v = 0;
																	v = Integer.parseInt( vol ) - step;

																	if( v < 0 )
																	{
																		v = 10;
																	}
																	
																	String t1 = t.substring( 0, lastA );
																	String t2 = t.substring( lastD );
																	t = t1 + "a" + v + t2;
																}
																catch (Exception ex2) 
																{
																	t += "a" + ( step > 64 ? 64 - step : 10 );
																}
																 
																
																pt.add( t );
															}
														}
														
													}
													else
													{							
														pt.add( tk.toString() );
													}
												}
												
												pattern = pt;
												//*/
												
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
												ex1.printStackTrace();
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
						final long waitTime = (long)( 1000 * Double.parseDouble( spinner.getValue().toString() ) );
						
						if( waitTime > 0 )
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
												
												if( delay < waitTime )
												{
													this.wait( waitTime - delay );
												}
											}
	
											delay = System.currentTimeMillis();
											
											if( beepAct )
											{
												beep.play();
											}
											
											
											taskTimer.stopTimer();
											
											mainPane.setBackground( Color.GREEN );
											
											taskTimer.restartTimer();
											
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
						}
						
						SyncMarker.getInstance( "No"+ConfigApp.shortNameApp ).sendMarker( Marker.START_TEST );
						
						if( playerThr != null )
						{
							playerThr.start();
						}
						
						if( beepThr != null )
						{
							beepThr.start();
						}
						
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
					spinner_1.setEnabled( true );
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
		this.btnPause.setVisible( false );
	}
}
