package testing.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import config.ConfigApp;
import config.ConfigParameter;
import config.ConfigParameter.ParameterType;
import config.language.Caption;
import config.language.Language;
import exceptions.ConfigParameterException;
import gui.panel.statusSurvey.EmotionParameter;
import gui.panel.statusSurvey.PlayerStatusIcon;
import gui.panel.statusSurvey.EmotionParameter.Emotion;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;

public class testStateSurveyIcons extends JFrame {
	
	private List< EmotionParameter > samEmotionSet = new ArrayList< EmotionParameter >();

	
	
	private JPanel contentPane;
	private final JPanel panelSAM = new JPanel();
	private final JPanel panelArousal = new JPanel();
	private final JPanel panelDominance = new JPanel();
	private final JPanel panelEmotions = new JPanel();
	private final JPanel panelPhysicalEffort = new JPanel();
	private final JPanel panelMain = new JPanel();
	private final JPanel panelCtrl = new JPanel();
	private final JPanel panelValence = new JPanel();
	private final JLabel lblNewLabel = new JLabel("N\u00FAmero de iconos: ");
	private final JSpinner spinner = new JSpinner();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					testStateSurveyIcons frame = new testStateSurveyIcons();
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
	public testStateSurveyIcons() {
		initialize();
		
		this.samEmotionSet.add( new EmotionParameter( Emotion.NEUTRAL, "Neutral", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.HAPPINESS, "Contento", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.SURPIRSE, "Sorprendido", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.SADNESS, "Triste", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.FEAR, "Asustado", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.DISGUST, "Asqueado", true) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.ANGER, "Enfadado", true ) );
		
		setIcons();
	}
	
	private void setIcons()
	{
		int N = Integer.parseInt( spinner.getValue().toString() );
		
		int side = 100;
		
		boolean vis = false;
		
		panelValence.setVisible( vis );
		panelArousal.setVisible( vis );
		panelDominance.setVisible( vis );
		panelPhysicalEffort.setVisible( vis );
		panelEmotions.setVisible( vis );
		
		panelValence.removeAll();
		panelArousal.removeAll();
		panelDominance.removeAll();
		panelPhysicalEffort.removeAll();
		panelEmotions.removeAll();
		
		for( int i =1; i <= N; i++ )
		{
			for( int samType = 0; samType < 5; samType++ )
			{
				JButton bt = new JButton();
				bt.setName( i + "" );

				String btTxt = "";

				if (samType == 0)
				{
					bt.setIcon(PlayerStatusIcon.getSAMValence( i, side, Color.BLACK, Color.WHITE));
					panelValence.add( bt );
				}
				else if (samType == 1)
				{
					bt.setIcon(PlayerStatusIcon.getSAMArousal( i, side, Color.BLACK, Color.WHITE));
					panelArousal.add( bt );
				}
				else if ( samType == 2 )
				{
					bt.setIcon(PlayerStatusIcon.getSAMDominance( i, side, Color.BLACK, Color.WHITE));
					panelDominance.add( bt );
				}
				else if( samType == 3 )
				{
					bt.setIcon(PlayerStatusIcon.getPhysicalEffort( i, side, Color.BLACK, Color.WHITE));
					panelPhysicalEffort.add( bt );
				}
				else
				{
					if( i >= 1 && i <= this.samEmotionSet.size() )
					{				
						EmotionParameter emo = this.samEmotionSet.get( i-1 );
	
						if( emo.isSelect() )
						{
							btTxt = emo.getText();
							bt.setIcon( PlayerStatusIcon.getBasicEmotion( emo.getType().ordinal()+1, side, Color.BLACK, Color.WHITE, btTxt, bt.getFontMetrics(bt.getFont())));
							panelEmotions.add( bt );
						}
					}
				}
			}
		}
		
		vis = true;		
		panelValence.setVisible( vis );
		panelArousal.setVisible( vis );
		panelDominance.setVisible( vis );
		panelPhysicalEffort.setVisible( vis );
		panelEmotions.setVisible( vis );
	}
	
	private void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		this.contentPane.setLayout(new BorderLayout(0, 0));
		
		this.contentPane.add(this.panelMain);
		this.panelMain.setLayout(new BorderLayout(0, 0));
		this.panelMain.add(this.panelSAM);
		this.panelSAM.setLayout(new BoxLayout(this.panelSAM, BoxLayout.Y_AXIS));
		
		this.panelSAM.add(this.panelValence);
		this.panelSAM.add(this.panelArousal);
		this.panelSAM.add(this.panelDominance);
		this.panelSAM.add(this.panelPhysicalEffort);
		this.panelSAM.add(this.panelEmotions);
		FlowLayout flowLayout = (FlowLayout) this.panelCtrl.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		
		this.panelMain.add(this.panelCtrl, BorderLayout.NORTH);
		
		this.panelCtrl.add(this.lblNewLabel);
		this.spinner.setModel(new SpinnerNumberModel(10, 1, 10, 1));
		
		
		this.spinner.addChangeListener( new ChangeListener()
		{								
			@Override
			public void stateChanged(ChangeEvent e)
			{				
				setIcons();
			}
		});
		
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
		
		this.panelCtrl.add(this.spinner);
	}

}
