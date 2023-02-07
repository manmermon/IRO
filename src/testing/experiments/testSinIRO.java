package testing.experiments;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

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
import gui.panel.statusSurvey.PlayerStatusSurvey;
import gui.panel.statusSurvey.PlayerStatusSurvey.StatusSurvey;
import image.icon.GeneralAppIcon;
import stoppableThread.AbstractStoppableThread;
import stoppableThread.IStoppable;
import testing.experiments.synMarker.SyncMarker;
import testing.experiments.synMarker.SyncMarker.Marker;
import thread.timer.ActionTimerThread;
import thread.timer.IAction;
import thread.timer.Timer;
import tools.MusicSheetTools;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JToggleButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;

public class testSinIRO extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String surveyFolder = System.getProperty( "user.dir" );
	private char separate = File.separatorChar;
	private String surveyFileName = "userStatusSurvey.log";
	
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
	
	private JPanel contentPane;
	private final JLabel lblBeep = new JLabel("Periodo (s): ");
	private final JPanel panelSettings = new JPanel();
	private final JSpinner spinnerBeepTime = new JSpinner();
	private final JPanel panel_1 = new JPanel();
	private final JButton tglbtnStart = new JButton("Start");
	
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
	private boolean startSession = false;
	
	private final JCheckBox chckbxSem = new JCheckBox("Sem\u00E1foro");
	private final JPanel panelSettingContainer = new JPanel();
	private final JLabel lblBlqAct = new JLabel("Actividad (s)");
	private final JSpinner spinnerBlqAct = new JSpinner();
	private final JLabel lblBlqDesc = new JLabel("Descanso (s)");
	private final JSpinner spinnerDescanso = new JSpinner();
	private final JLabel lblSesion = new JLabel("Tiempo Sesi\u00F3n (s)");
	private final JSpinner spinnerSession = new JSpinner();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JToggleButton btnPause = new JToggleButton("Pause");
	private final JCheckBox chbStateSurvey = new JCheckBox("Encuesta de estado");
	private final JPanel panelTimeSettings = new JPanel();
	private final JPanel panelOthers = new JPanel();
	private final JTextField textFilePath = new JTextField();
	private final JButton btnFilePath = new JButton();
	private final JPanel panelFile = new JPanel();
	private final JTextField txtIdsujeto = new JTextField();
	private final JLabel lblSbj = new JLabel("Sujeto: ");
	private final JButton btnStop = new JButton("Stop");
	
	private AbstractStoppableThread beepThr = null;
	private AbstractStoppableThread playerThr = null;
	
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
	public testSinIRO() 
	{
		this.txtIdsujeto.setText("idSujeto");
		this.txtIdsujeto.setColumns(10);		
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
		
		scrollPane.getHorizontalScrollBar().setBlockIncrement( 10 );
		scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
		
		SyncMarker.getInstance( "No"+ConfigApp.shortNameApp );
		setTitle( "Test sin " + ConfigApp.shortNameApp );
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		
		
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
		
		this.mainPane.add( disablePane );
		this.mainPane.setBackground( Color.WHITE );
		this.contentPane.add( this.mainPane, BorderLayout.CENTER );
		
		this.contentPane.add(this.panelSettings, BorderLayout.NORTH);
		this.panelSettings.setLayout(new BorderLayout(0, 0));
		this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		
		scrollPane.setViewportView( this.panelSettingContainer );
		this.panelSettings.add( this.scrollPane, BorderLayout.CENTER);
		this.panelSettingContainer.setLayout(new BoxLayout(this.panelSettingContainer, BoxLayout.Y_AXIS));
		this.panelTimeSettings.setBorder(new EmptyBorder(0, 0, 5, 0));
		
		this.panelSettingContainer.add(this.panelTimeSettings);
		GridBagLayout gbl_panelTimeSettings = new GridBagLayout();
		gbl_panelTimeSettings.columnWidths = new int[]{84, 50, 60, 50, 62, 50, 59, 50, 0};
		gbl_panelTimeSettings.rowHeights = new int[]{20, 0};
		gbl_panelTimeSettings.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelTimeSettings.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		this.panelTimeSettings.setLayout(gbl_panelTimeSettings);
		GridBagConstraints gbc_lblSesion = new GridBagConstraints();
		gbc_lblSesion.anchor = GridBagConstraints.WEST;
		gbc_lblSesion.insets = new Insets(0, 0, 0, 5);
		gbc_lblSesion.gridx = 0;
		gbc_lblSesion.gridy = 0;
		this.panelTimeSettings.add(this.lblSesion, gbc_lblSesion);
		GridBagConstraints gbc_spinnerSession = new GridBagConstraints();
		gbc_spinnerSession.anchor = GridBagConstraints.NORTHWEST;
		gbc_spinnerSession.insets = new Insets(0, 0, 0, 5);
		gbc_spinnerSession.gridx = 1;
		gbc_spinnerSession.gridy = 0;
		this.panelTimeSettings.add(this.spinnerSession, gbc_spinnerSession);
		spinnerSession.setPreferredSize( d );
		this.spinnerSession.setModel(new SpinnerNumberModel(new Double(180), new Double(30), null, new Double(1)));
		GridBagConstraints gbc_lblBlqAct = new GridBagConstraints();
		gbc_lblBlqAct.anchor = GridBagConstraints.WEST;
		gbc_lblBlqAct.insets = new Insets(0, 0, 0, 5);
		gbc_lblBlqAct.gridx = 2;
		gbc_lblBlqAct.gridy = 0;
		this.panelTimeSettings.add(this.lblBlqAct, gbc_lblBlqAct);
		GridBagConstraints gbc_spinnerBlqAct = new GridBagConstraints();
		gbc_spinnerBlqAct.anchor = GridBagConstraints.NORTHWEST;
		gbc_spinnerBlqAct.insets = new Insets(0, 0, 0, 5);
		gbc_spinnerBlqAct.gridx = 3;
		gbc_spinnerBlqAct.gridy = 0;
		this.panelTimeSettings.add(this.spinnerBlqAct, gbc_spinnerBlqAct);
		spinnerBlqAct.setPreferredSize( d );
		this.spinnerBlqAct.setModel(new SpinnerNumberModel(new Integer(45), new Integer(1), null, new Integer(1)));
		GridBagConstraints gbc_lblBlqDesc = new GridBagConstraints();
		gbc_lblBlqDesc.anchor = GridBagConstraints.WEST;
		gbc_lblBlqDesc.insets = new Insets(0, 0, 0, 5);
		gbc_lblBlqDesc.gridx = 4;
		gbc_lblBlqDesc.gridy = 0;
		this.panelTimeSettings.add(this.lblBlqDesc, gbc_lblBlqDesc);
		GridBagConstraints gbc_spinnerDescanso = new GridBagConstraints();
		gbc_spinnerDescanso.anchor = GridBagConstraints.NORTHWEST;
		gbc_spinnerDescanso.insets = new Insets(0, 0, 0, 5);
		gbc_spinnerDescanso.gridx = 5;
		gbc_spinnerDescanso.gridy = 0;
		this.panelTimeSettings.add(this.spinnerDescanso, gbc_spinnerDescanso);
		spinnerDescanso.setPreferredSize( d );
		this.spinnerDescanso.setModel(new SpinnerNumberModel(new Integer(15), new Integer(0), null, new Integer(1)));
		GridBagConstraints gbc_lblBeep = new GridBagConstraints();
		gbc_lblBeep.anchor = GridBagConstraints.WEST;
		gbc_lblBeep.insets = new Insets(0, 0, 0, 5);
		gbc_lblBeep.gridx = 6;
		gbc_lblBeep.gridy = 0;
		this.panelTimeSettings.add(this.lblBeep, gbc_lblBeep);
		GridBagConstraints gbc_spinnerBeepTime = new GridBagConstraints();
		gbc_spinnerBeepTime.anchor = GridBagConstraints.NORTHWEST;
		gbc_spinnerBeepTime.gridx = 7;
		gbc_spinnerBeepTime.gridy = 0;
		this.panelTimeSettings.add(this.spinnerBeepTime, gbc_spinnerBeepTime);
		
		spinnerBeepTime.setPreferredSize( d );
		
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
		
		this.panelSettingContainer.add(this.panelOthers);
		GridBagLayout gbl_panelOthers = new GridBagLayout();
		gbl_panelOthers.columnWidths = new int[] {87, 71, 121, 0, 0};
		gbl_panelOthers.rowHeights = new int[]{23, 0};
		gbl_panelOthers.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
		gbl_panelOthers.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		this.panelOthers.setLayout(gbl_panelOthers);
		GridBagConstraints gbc_chckbxBeepAct = new GridBagConstraints();
		gbc_chckbxBeepAct.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxBeepAct.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxBeepAct.gridx = 0;
		gbc_chckbxBeepAct.gridy = 0;
		this.panelOthers.add(this.chckbxBeepAct, gbc_chckbxBeepAct);
		this.chckbxBeepAct.setSelected(true);
		GridBagConstraints gbc_chckbxSem = new GridBagConstraints();
		gbc_chckbxSem.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxSem.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxSem.gridx = 1;
		gbc_chckbxSem.gridy = 0;
		this.panelOthers.add(this.chckbxSem, gbc_chckbxSem);
		GridBagConstraints gbc_chbStateSurvey = new GridBagConstraints();
		gbc_chbStateSurvey.anchor = GridBagConstraints.NORTHWEST;
		gbc_chbStateSurvey.insets = new Insets(0, 0, 0, 5);
		gbc_chbStateSurvey.gridx = 2;
		gbc_chbStateSurvey.gridy = 0;
		this.panelOthers.add(this.chbStateSurvey, gbc_chbStateSurvey);
		this.chbStateSurvey.setSelected(true);
		
		this.panelSettingContainer.add(this.panelFile);
		GridBagLayout gbl_panelFile = new GridBagLayout();
		gbl_panelFile.columnWidths = new int[] {38, 38, 50, 51, 30};
		gbl_panelFile.rowHeights = new int[]{23, 0};
		gbl_panelFile.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0};
		gbl_panelFile.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		this.panelFile.setLayout(gbl_panelFile);
		
		GridBagConstraints gbc_lblSbj = new GridBagConstraints();
		gbc_lblSbj.anchor = GridBagConstraints.WEST;
		gbc_lblSbj.insets = new Insets(0, 0, 0, 5);
		gbc_lblSbj.gridx = 0;
		gbc_lblSbj.gridy = 0;
		this.panelFile.add(this.lblSbj, gbc_lblSbj);
		GridBagConstraints gbc_txtIdsujeto = new GridBagConstraints();
		gbc_txtIdsujeto.anchor = GridBagConstraints.WEST;
		gbc_txtIdsujeto.insets = new Insets(0, 0, 0, 5);
		gbc_txtIdsujeto.gridx = 1;
		gbc_txtIdsujeto.gridy = 0;
		this.panelFile.add(this.txtIdsujeto, gbc_txtIdsujeto);
		GridBagConstraints gbc_btnFilePath = new GridBagConstraints();
		gbc_btnFilePath.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnFilePath.insets = new Insets(0, 0, 0, 5);
		gbc_btnFilePath.gridx = 2;
		gbc_btnFilePath.gridy = 0;
		this.panelFile.add(this.btnFilePath, gbc_btnFilePath);
		
		btnFilePath.setIcon( GeneralAppIcon.Folder( 20, 16, Color.BLACK, Color.YELLOW ) );
		
		this.btnFilePath.addActionListener( new ActionListener() 
		{			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String format = "log";
				String[] filters = new String[] { format }; 
					
				String folder = System.getProperty( "user.dir" );
				try
				{
					File f = new File( textFilePath.getText() );
					folder = f.getCanonicalPath();
				}
				catch( Exception ex)
				{}
				
				String path[] = selectUserFile( folder
												, false, false, JFileChooser.FILES_ONLY, format
												, filters, System.getProperty("user.dir") );
				if( path != null )
				{
					File f = new File( path[ 0 ] );
					String file = surveyFolder + separate + surveyFileName;
					try
					{
						file = f.getCanonicalPath();
					}
					catch( Exception ex)
					{						
					}
					
					if( f.isDirectory() )
					{
						if( !file.endsWith( "" + separate ) )
						{
							file += separate;
						}
						
						file += surveyFileName;
					}
					
					textFilePath.setText( file  );
				}
			}
		});
		GridBagConstraints gbc_textFilePath = new GridBagConstraints();
		gbc_textFilePath.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFilePath.insets = new Insets(0, 0, 0, 5);
		gbc_textFilePath.gridx = 3;
		gbc_textFilePath.gridy = 0;
		this.panelFile.add(this.textFilePath, gbc_textFilePath);
		
		this.textFilePath.setColumns(10);
		
		textFilePath.setText( surveyFolder + separate + surveyFileName  );
		
		this.panelSettings.add(this.scrollPane, BorderLayout.CENTER);
		FlowLayout flowLayout = (FlowLayout) this.panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		
		this.contentPane.add(this.panel_1, BorderLayout.SOUTH);
		this.tglbtnStart.addActionListener( new ActionListener() 
		{								
			@Override
			public void actionPerformed(ActionEvent e)  
			{	
				enableStartStopBottons( true );
				
				startSession = true;
				
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
				
				if( beepThr != null )
				{
					beepThr.stopActing( IStoppable.FORCE_STOP );
					beepThr = null;
				}
					
				
				if( playerThr != null )
				{
					playerThr.stopActing( IStoppable.FORCE_STOP );
					playerThr = null;
				}
				
				restTime = 0;

				mainPane.setVisible( false );
				mainPane.setBackground( Color.WHITE );

				enableSettings( false );

				disablePane.setVisible( !chckbxSem.isSelected() );
				mainPane.setVisible( true );

				showStatusSurveyDialog( chbStateSurvey.isSelected() );
					
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
							playerThr = new AbstractStoppableThread() 
							{
								protected void preStopThread(int friendliness) throws Exception 
								{	
								}
								
								@Override
								protected void postStopThread(int friendliness) throws Exception 
								{	
								}
								
								@Override
								protected void runInLoop() throws Exception 
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

									stopSessionByTimer();
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
							stopSessionByTimer();
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
						beepThr = new AbstractStoppableThread() 
						{					
							boolean stop = false;
							
							protected void preStopThread(int friendliness) throws Exception 
							{	
							}
							
							@Override
							protected void postStopThread(int friendliness) throws Exception 
							{	
							}
							
							@Override
							protected void runInLoop() throws Exception 
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
							}
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
						playerThr.startActing();
					}

					if( beepThr != null )
					{
						beepThr.startActing();
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
						beepThr.stopActing( IStoppable.FORCE_STOP );

						beepThr = null;
					}

					if( playerThr != null )
					{
						playerThr.stopActing( IStoppable.FORCE_STOP );

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
					
					if( tiempoTranscurridoTimer != null )
					{
						tiempoTranscurridoTimer.stopThread( IStoppable.FORCE_STOP );
						tiempoTranscurridoTimer = null;
					}
				}
			}
			
		});
		
		this.panel_1.add(this.tglbtnStart);
		
		btnStop.addActionListener( new ActionListener() 
		{			
			@Override
			public void actionPerformed(ActionEvent e) 
			{	
				stopSessionButton();
			}
		});
		
		this.btnStop.setEnabled( false );
		
		this.panel_1.add(this.btnStop);
		
		this.panel_1.add(this.btnPause);
		this.panel_1.add( this.tiempoTranscurridoLabel );
		this.panel_1.add( this.tiempoTranscurridoValor );
		
		this.btnPause.setVisible( false );
	}
	
	private synchronized void stopSessionByTimer()
	{
		Runnable run = null;
		synchronized ( sync )
		{								
			if( startSession )
			{
				startSession = false;
				
				run = new Runnable() {
					
					@Override
					public void run() 
					{
						stopSessionButton();
					}
				};
			}								
		}
		
		if( run != null )
		{
			//btnStop.doClick();
			try 
			{
				SwingUtilities.invokeAndWait( run );
			}
			catch (InvocationTargetException | InterruptedException e) 
			{
				//e.printStackTrace();
			}

		}
	}
	
	private void stopSessionButton()
	{
		synchronized ( sync )
		{
			startSession = false;
		}
				
		SyncMarker.getInstance( "No"+ConfigApp.shortNameApp ).sendMarker( Marker.STOP_TEST );
		
		if( beepThr != null )
		{
			beepThr.stopActing( IStoppable.FORCE_STOP );
			
			beepThr = null;
		}
		
		if( playerThr != null )
		{
			playerThr.stopActing( IStoppable.FORCE_STOP );
			
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
	
		enableStartStopBottons( false );
		enableSettings( true );
		
		mainPane.setVisible( false );
												
		disablePane.setVisible( true );
		
		mainPane.setVisible( true );
	
		showStatusSurveyDialog( chbStateSurvey.isSelected() );
	}
	
	
	private void enableSettings( boolean ena )
	{
		SelectSongPanel.getInstance().setEnabled( ena );
		
		spinnerBeepTime.setEnabled( ena );
		spinnerSession.setEnabled( ena );
		spinnerBlqAct.setEnabled( ena );
		spinnerDescanso.setEnabled( ena );
		chckbxBeepAct.setEnabled( ena );
		chbStateSurvey.setEnabled( ena );
		textFilePath.setEnabled( ena );
		btnFilePath.setEnabled( ena );
		textFilePath.setEnabled( ena );
		
		chckbxSem.setEnabled( ena );
		disablePane.setEnabled( ena );
	}
	
	private void enableStartStopBottons( boolean start )
	{
		tglbtnStart.setEnabled( !start );
		btnStop.setEnabled( start );
	}
	
	private void showStatusSurveyDialog( boolean show )
	{
		if( show )
		{	
			JDialog statusSurveyDialog = new JDialog( this, ModalityType.APPLICATION_MODAL );
			statusSurveyDialog.setVisible( false );
			JPanel container = new JPanel( new BorderLayout() );
			statusSurveyDialog.setContentPane( container );
	
			statusSurveyDialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
			
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
	
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Insets pads = Toolkit.getDefaultToolkit().getScreenInsets( gc );
			screenSize.width += -( pads.left + pads.right );
			screenSize.height += -( pads.top + pads.bottom );
	
			statusSurveyDialog.setLocation( pads.left, pads.top );
			statusSurveyDialog.setSize( screenSize );
						
			Player p = new Player( 0, "", null );
			PlayerStatusSurvey sae = new PlayerStatusSurvey( p, screenSize, new StatusSurvey[] { StatusSurvey.VALENCE, StatusSurvey.AROUSAL, StatusSurvey.PHYSICAL_EFFORT }, statusSurveyDialog );
			container.add( sae, BorderLayout.CENTER );

			statusSurveyDialog.setVisible( true );
						
			String playesState = sae.getPlayerState();
			writeSurverStatus( textFilePath.getText(), playesState );
			
			statusSurveyDialog.dispose();
			statusSurveyDialog = null;
		}
	}
	
	public String[] selectUserFile(String defaultName, boolean mustExist
			, boolean multiSelection, int selectionModel
			, String descrFilter, String[] filterExtensions
			, String defaultFolder )
	{
		File[] f = selectFile( defaultName, "Fichero"
				, JFileChooser.OPEN_DIALOG
				, multiSelection, selectionModel, descrFilter, filterExtensions, defaultFolder );

		int N = 1;

		if( f != null && f.length > 0 )
		{
			N = f.length;
		}

		String[] path = null;

		if (f != null)
		{			
			boolean allFileExist = true;
			for( int iF = 0; iF < N && allFileExist; iF++ )
			{
				allFileExist = f[ iF ].exists();				
			}


			if ( mustExist && !allFileExist )
			{
				path = null;

				JOptionPane.showMessageDialog( this, "Fichero no existe." );
			}
			else
			{
				path = new String[ N ];

				for( int iF = 0; iF < N; iF++ )
				{
					path[ iF ] = f[ iF ].getAbsolutePath();					
				}
			}
		}		

		return path;
	}

	public File[] selectFile(String defaulName, String titleDialog
			, int typeDialog, boolean multiSelection
			, int selectionModel, String descrFilter
			, String[] filterExtensions, String defaultFolder )
	{		
		FileNameExtensionFilter filter = null;

		if( filterExtensions != null && filterExtensions.length > 0 )
		{
			filter = new FileNameExtensionFilter( descrFilter, filterExtensions );
		}


		File[] file = null;

		JFileChooser jfc = null;

		jfc = new JFileChooser( defaultFolder );

		jfc.setMultiSelectionEnabled(multiSelection);

		jfc.setDialogTitle(titleDialog);
		jfc.setDialogType(typeDialog);
		jfc.setFileSelectionMode(selectionModel);
		jfc.setSelectedFile(new File(defaulName));

		if( filter != null )
		{
			jfc.setFileFilter( filter );
		}
  
		int returnVal = jfc.showDialog( this, null);

		if (returnVal == JFileChooser.APPROVE_OPTION )
		{
			if (multiSelection)
			{
				file = jfc.getSelectedFiles();
			}
			else
			{
				file = new File[1];
				file[0] = jfc.getSelectedFile();
			}
		}

		return file;
	}
	
	private void writeSurverStatus( String filePath, String values )
	{
		File file = new File( filePath );
		if( file != null  )
		{
			try 
			{				
				if( !file.exists() )
				{
					file.createNewFile();
				}
								
				if( file.isFile() && file.canWrite() )
				{   
				    PrintWriter  out = new PrintWriter( new BufferedWriter( new FileWriter( file, true ) ), false );
				    
				    String log = "[" + dtf.format( LocalDateTime.now() ) + "] " + txtIdsujeto.getText() + ": " + values + "\n";
				    
				    out.print( log );				    
				    out.close();
				}
				else
				{
					JOptionPane.showMessageDialog( this, "No se puede escribir." );
				}
			} 
			catch (IOException e)
			{
				JOptionPane.showMessageDialog( this, e.getMessage() );
			}
		}
		else
		{
			JOptionPane.showMessageDialog( this, "Fichero de salida nulo." );
		}
	}
}
