package testing.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import config.Player;
import gui.panel.statusSurvey.PlayerStatusSurvey;
import gui.panel.statusSurvey.PlayerStatusSurvey.StatusSurvey;

public class testStateSurvey extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					testStateSurvey frame = new testStateSurvey();
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
	public testStateSurvey() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
		
		PlayerStatusSurvey pss = new PlayerStatusSurvey( new Player(), super.getSize(), new StatusSurvey[] { StatusSurvey.VALENCE, StatusSurvey.AROUSAL, StatusSurvey.PHYSICAL_EFFORT }, this );
		
		this.contentPane.add( pss, BorderLayout.CENTER );
	}

}
