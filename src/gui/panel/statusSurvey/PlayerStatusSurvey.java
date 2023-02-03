package gui.panel.statusSurvey;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import GUI.buttom.NoneSelectedButtonGroup;
import config.Player;
import gui.panel.statusSurvey.EmotionParameter.Emotion;
import image.BasicPainter2D;
import statistic.RegistrarStatistic;

public class PlayerStatusSurvey extends JPanel 
{
	public enum StatusSurvey { VALENCE, AROUSAL, DOMINANCE, PHYSICAL_EFFORT, EMOTION};
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6303314182667921826L;
	
	private JToggleButton[] statusSurveyValues = null;
	private JButton sendedButton = null;
	private String[] headerSAM = null;
	private Font fQuestions = new Font( Font.DIALOG, Font.BOLD, 22);
	
	private List< EmotionParameter > samEmotionSet = new ArrayList< EmotionParameter >();
	
	private StatusSurvey[] survey = null;
	
	private Dimension predSize = new Dimension(1, 1);
	
	private Player player = null;
	
	private String playerState = "";
	
	public PlayerStatusSurvey( Player player, Dimension sceneSize, StatusSurvey[] surveyTypes, Window w ) 
	{	
		if( player == null )
		{
			throw new IllegalArgumentException( "Player null" );
		}
		
		if( surveyTypes == null || surveyTypes.length == 0 )
		{
			throw new IllegalArgumentException( "Survey types null or empty" );
		}
		
		this.survey = surveyTypes;
			
		this.player = player;
		
		String playerName = this.player.getName();
		
		this.samEmotionSet.add( new EmotionParameter( Emotion.NEUTRAL, "Neutral", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.HAPPINESS, "Contento", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.SURPIRSE, "Sorprendido", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.SADNESS, "Triste", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.FEAR, "Asustado", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.DISGUST, "Asqueado", true) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.ANGER, "Enfadado", true ) );
		
				
		this.statusSurveyValues = new JToggleButton[ surveyTypes.length ];		
		this.headerSAM = new String[ surveyTypes.length ];
		
		for( int i = 0; i < surveyTypes.length; i++ )
		{
			StatusSurvey type = surveyTypes[ i ];
			String h = playerName + ": ";
			
			switch ( type )
			{
				case VALENCE:
				{
					h += "\u00BFC\u00F3mo de bien te sientes?";
					break;
				}
				case AROUSAL:
				{
					h += "\u00BFC\u00F3mo de nervioso/a te sientes?";
					
					break;
				}
				case DOMINANCE:
				{
					h += "\u00BFQu\u00E9 nivel de control crees que tienes sobre tus sentimientos?";
					
					break;
				}
				case PHYSICAL_EFFORT:
				{
					h += "\u00BFC\u00F3mo de cansado/a est\u00e1s?";
					
					break;
				}
				default:
				{
					h += "\u00BFQu\u00E9 emoci\u00F3n sientes?";
					
					break;
				}
			}
			
			this.headerSAM[ i ] = h;
		}
		
		this.predSize = new Dimension( sceneSize.width
										, ( sceneSize.height - this.getSendedButton().getPreferredSize().height ) / statusSurveyValues.length );

		this.init( surveyTypes, 3);
	}

	public String getPlayerState()
	{
		return playerState;
	}
	
	private JPanel getStatusSurveyPictsPanel( final StatusSurvey type, int surveyPosition, String header, int size )
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout());
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), 
						header, 
						TitledBorder.DEFAULT_JUSTIFICATION , 
						TitledBorder.LEFT, this.fQuestions));

		NoneSelectedButtonGroup gr = new NoneSelectedButtonGroup();

		float step = 1.0F / ( size - 1 );
		if( size - 1 <= 0 )
		{
			step = 1F;
		}
		
		for (int j = 1; j <= size; j++)
		{
			JToggleButton bt = new JToggleButton();
			bt.setName( j + "" );

			String btTxt = "";

			Dimension d = bt.getPreferredSize();
			int side = d.width;
			if (side > d.height)
			{
				side = d.height;
			}

			if ( type == StatusSurvey.VALENCE )
			{
				bt.setIcon(PlayerStatusIcon.getSAMValence( ( j - 1 ) * step, side, Color.BLACK, Color.WHITE));
			}
			else if (type == StatusSurvey.AROUSAL)
			{
				bt.setIcon(PlayerStatusIcon.getSAMArousal( ( j - 1 ) * step, side, Color.BLACK, Color.WHITE));
			}
			else if ( type == StatusSurvey.DOMINANCE )
			{
				bt.setIcon(PlayerStatusIcon.getSAMDominance( ( j - 1 ) * step, side, Color.BLACK, Color.WHITE));
			}
			else if ( type == StatusSurvey.PHYSICAL_EFFORT )
			{
				int level = (int)Math.floor( ( j - 1 ) * 10*step +1);
				 
				bt.setIcon(PlayerStatusIcon.getPhysicalEffort( level, side, Color.BLACK, Color.WHITE));
			}
			else
			{
				EmotionParameter emo = this.samEmotionSet.get( j - 1 );
				
				if( emo.isSelect() )
				{
					btTxt = emo.getText();
					bt.setIcon( PlayerStatusIcon.getBasicEmotion( emo.getType().ordinal()+1, side, Color.BLACK, Color.WHITE, btTxt, bt.getFontMetrics(bt.getFont())));
					bt.setName( "" + (emo.getType().ordinal()+1) );
				}
				else
				{
					bt = null;
				}
			}

			
			if( bt != null )
			{
				final int surveyPos = surveyPosition;
				bt.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) 
					{

					}
				});

				bt.addItemListener(new ItemListener()
				{
					@Override
					public void itemStateChanged(ItemEvent e)
					{
						JToggleButton b = (JToggleButton)e.getSource();

						if (b.isSelected())
						{
							statusSurveyValues[surveyPos] = b;

							if (!getSendedButton().isEnabled())
							{
								boolean enable = true;

								for (int i = 0; (i < statusSurveyValues.length) && (enable); i++)
								{
									enable = statusSurveyValues[i] != null;
								}

								getSendedButton().setEnabled(enable);
							}

							BasicPainter2D.changeColorPixels(Color.WHITE, Color.ORANGE, ((ImageIcon)b.getIcon()).getImage());
						}
						else
						{
							statusSurveyValues[surveyPos] = null;
							getSendedButton().setEnabled(false);

							BasicPainter2D.changeColorPixels(Color.ORANGE, Color.WHITE, ((ImageIcon)b.getIcon()).getImage());
						}
					}
				});

				final String bTxt = btTxt;
				final float stepAux = step;
				bt.addComponentListener(new ComponentAdapter()
				{
					public void componentResized(ComponentEvent arg0) 
					{
						JToggleButton b = (JToggleButton)arg0.getSource();
						Dimension d = b.getSize();						

						Color bodyColor = Color.WHITE;
						
						if( b.isSelected() )
						{
							bodyColor = Color.ORANGE;
						}
						
						int side = d.width;
						if (side > d.height)
						{
							side = d.height;
						}
						
						side -= 1;
						
						if ( type == StatusSurvey.VALENCE )
						{
							b.setIcon(PlayerStatusIcon.getSAMValence( ( new Integer(b.getName()) - 1 ) * stepAux, side, Color.BLACK, bodyColor));
						}
						else if ( type == StatusSurvey.AROUSAL)
						{
							b.setIcon(PlayerStatusIcon.getSAMArousal( ( new Integer(b.getName() ) - 1 ) * stepAux, side, Color.BLACK, bodyColor));
						}
						else if ( type == StatusSurvey.DOMINANCE)
						{
							b.setIcon(PlayerStatusIcon.getSAMDominance( ( new Integer( b.getName() ) - 1 ) * stepAux, side, Color.BLACK, bodyColor));
						}
						else if( type == StatusSurvey.PHYSICAL_EFFORT )
						{
							int level = (int)Math.floor( ( new Integer( b.getName() ) - 1 ) * 10*stepAux +1);
														
							b.setIcon( PlayerStatusIcon.getPhysicalEffort( level, side, Color.BLACK, bodyColor ) );
						}
						else
						{
							Font f = new Font( Font.DIALOG, Font.BOLD, 18);
							FontMetrics fm = b.getFontMetrics(f);

							b.setIcon(PlayerStatusIcon.getBasicEmotion(new Integer(b.getName()), side, Color.BLACK, bodyColor, bTxt, fm));
						}

						if (b.getIcon() == null)
						{
							Font f = new Font( Font.DIALOG, Font.BOLD, d.height / 2);
							FontMetrics fm = b.getFontMetrics(f);
							Insets pad = b.getInsets();

							while( fm.stringWidth( b.getText() ) >  d.width - pad.left - pad.right
									&& fm.getHeight() < d.height)
							{
								f = new Font( f.getName(), f.getStyle(), f.getSize() - 1 );
								fm = b.getFontMetrics( f );
							}

							while( fm.stringWidth( b.getText() ) <  d.width - pad.left - pad.right
									&& fm.getHeight() < d.height)
							{
								f = new Font( f.getName(), f.getStyle(), f.getSize() + 1 );
								fm = b.getFontMetrics( f );
							}

							if( fm.stringWidth( b.getText() ) >  d.width - pad.left - pad.right
									&& fm.getHeight() < d.height)
							{
								f = new Font( f.getName(), f.getStyle(), f.getSize() - 1 );					
							}

							b.setFont(f);
						}

					}

				});
				gr.add( bt );
				panel.add( bt );
			}
		}

		panel.setPreferredSize( this.predSize );
		panel.setBackground( Color.WHITE );
		
		return panel;
	}
	
	private void dispose()
	{
		Window topFrame = (Window) SwingUtilities.getWindowAncestor( this );
		if( topFrame != null )
		{
			topFrame.dispose();
		}
	}
	
	private JButton getSendedButton()
	{
		if (this.sendedButton == null)
		{
			this.sendedButton = new JButton();
			
			this.sendedButton.setIcon( new ImageIcon( BasicPainter2D.triangle( 50, 1.5F, Color.BLACK, Color.WHITE, BasicPainter2D.EAST ) ));
			
			this.sendedButton.setEnabled(false);
			this.sendedButton.setBackground( Color.WHITE );

			this.sendedButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{										
					String vals = "<";
					
					for( int j = 0; j < statusSurveyValues.length; j++ )
					{					
						String v = statusSurveyValues[ j ].getName();
						
						StatusSurvey type = survey[ j ];
						
						if( type == StatusSurvey.EMOTION )
						{
							int i = Integer.parseInt( statusSurveyValues[ 2 ].getName() );
							v = Emotion.values()[ i - 1].name();
						}
						
						vals += v;
						vals += ",";
					}
					
					vals = vals.substring(0,  vals.length()-1 ) + ">";
										
					//RegistrarStatistic.addValenceArousalEffortData( player.getId(), vals );
					playerState = vals;
					
					dispose();
				}

			});
			
			this.sendedButton.addPropertyChangeListener( "enabled", new PropertyChangeListener() 
			{
                @Override
                public void propertyChange( PropertyChangeEvent evt) 
                {
                    JButton b = (JButton)evt.getSource();
                    
                    if( b.isEnabled() )
                    {
                    	BasicPainter2D.changeColorPixels(Color.WHITE, Color.ORANGE, ((ImageIcon)b.getIcon()).getImage());
                    }
                    else
                    {
                    	BasicPainter2D.changeColorPixels(Color.ORANGE, Color.WHITE, ((ImageIcon)b.getIcon()).getImage());
                    }
                }
            });
			
			
			this.sendedButton.addComponentListener(new ComponentAdapter()
			{
				public void componentResized(ComponentEvent arg0)
				{
					/*
					JButton b = (JButton)arg0.getSource();
					Dimension d = b.getSize();
					Font f = new Font( Font.DIALOG, Font.BOLD, d.height / 2);
					FontMetrics fm = b.getFontMetrics(f);
					Insets pad = b.getInsets();          

					while( fm.stringWidth( b.getText() ) >  d.width - pad.left - pad.right 
							&& fm.getHeight() < d.height )
					{
						f = new Font( f.getName(), f.getStyle(), f.getSize() - 1 );
						fm = b.getFontMetrics( f );
					}

					while( fm.stringWidth( b.getText() ) <  d.width - pad.left - pad.right
							&& fm.getHeight() < d.height )
					{
						f = new Font( f.getName(), f.getStyle(), f.getSize() + 1 );
						fm = b.getFontMetrics( f );
					}

					if( fm.stringWidth( b.getText() ) >  d.width - pad.left - pad.right 
							&& fm.getHeight() < d.height )
					{
						f = new Font( f.getName(), f.getStyle(), f.getSize() - 1 );					
					}          

					b.setFont(f);
					*/
				}
			});
		}


		return this.sendedButton;
	}
	
	private void init( StatusSurvey[] surveyTypes, int N ) 
	{
		this.setLayout( new BorderLayout() );
		
		JPanel samPanel = new JPanel();
		BoxLayout ly = new BoxLayout( samPanel, BoxLayout.Y_AXIS );
		samPanel.setLayout( ly );		
		
		if( N < 1 )
		{
			N = 3;					
		}
	
		for( int surveyPosition = 0; surveyPosition < surveyTypes.length; surveyPosition++ )
		{	
			StatusSurvey type = surveyTypes[ surveyPosition ];
			String header = headerSAM[ surveyPosition ];
			JPanel panel = this.getStatusSurveyPictsPanel( type, surveyPosition, header, N );		
			if( type == StatusSurvey.EMOTION )
			{
				panel = this.getStatusSurveyPictsPanel( type, surveyPosition, header, Emotion.values().length );
			}
					
			samPanel.add( panel );
			
		}
		
		this.add( samPanel, BorderLayout.CENTER );
		this.add( this.getSendedButton(), BorderLayout.EAST );		
	}


}
