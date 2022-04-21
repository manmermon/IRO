package gui.panel.samSurvey;

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
import gui.panel.samSurvey.EmotionParameter.Emotion;
import image.BasicPainter2D;
import image.icon.GeneralAppIcon;
import statistic.RegistrarStatistic;

public class SamSurvey extends JPanel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6303314182667921826L;
	
	private JToggleButton[] samValues = null;
	private JButton sendedButton = null;
	private String[] headerSAM = null;
	private Font fQuestions = new Font( Font.DIALOG, Font.BOLD, 22);

	private boolean samDominance = false;
	
	private List< EmotionParameter > samEmotionSet = new ArrayList< EmotionParameter >();
	
	private Dimension predSize = new Dimension(1, 1);
	
	private Player player = null;
	
	public SamSurvey( Player player, Dimension sceneSize, boolean dominanceQuestion, Window w ) 
	{	
		if( player == null )
		{
			throw new IllegalArgumentException( "Player null" );
		}
			
		this.player = player;
				
		this.samValues = new JToggleButton[3];
		this.headerSAM = new String[] { this.player.getName() + ": \u00BFC\u00F3mo de bien te sientes?" 
										, this.player.getName() + ": \u00BFC\u00F3mo de nervioso te sientes?" 
										, this.player.getName() + ": \u00BFQu\u00E9 nivel de control crees que tienes sobre tus sentimientos?"};
		
		if( !this.samDominance )
		{
			this.headerSAM[ this.headerSAM.length - 1 ] = "\u00BFQu\u00E9 emoci\u00F3n sientes?";
		}
		
		
		this.predSize = new Dimension( sceneSize.width
										, ( sceneSize.height - this.getSendedButton().getPreferredSize().height ) / samValues.length );

		
		this.samDominance = dominanceQuestion;
		
		this.samEmotionSet.add( new EmotionParameter( Emotion.NEUTRAL, "Neutral", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.HAPPINESS, "Contento", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.SURPIRSE, "Sorprendido", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.SADNESS, "Triste", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.FEAR, "Asustado", true ) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.DISGUST, "Asqueado", true) );
		this.samEmotionSet.add( new EmotionParameter( Emotion.ANGER, "Enfadado", true ) );
		
		this.init();
	}

	private JPanel getSAMPictsPanel( int samType, int size )
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout());
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), 
						this.headerSAM[samType], 
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

			if (samType == 0)
			{
				bt.setIcon(SamIcon.getSAMValence( ( j - 1 ) * step, side, Color.BLACK, Color.WHITE));
			}
			else if (samType == 1)
			{
				bt.setIcon(SamIcon.getSAMArousal( ( j - 1 ) * step, side, Color.BLACK, Color.WHITE));
			}
			else if ( this.samDominance )
			{
				bt.setIcon(SamIcon.getSAMDominance( ( j - 1 ) * step, side, Color.BLACK, Color.WHITE));
			}
			else
			{
				EmotionParameter emo = this.samEmotionSet.get( j - 1 );
				
				if( emo.isSelect() )
				{
					btTxt = emo.getText();
					bt.setIcon( SamIcon.getBasicEmotion( emo.getType().ordinal()+1, side, Color.BLACK, Color.WHITE, btTxt, bt.getFontMetrics(bt.getFont())));
					bt.setName( "" + (emo.getType().ordinal()+1) );
				}
				else
				{
					bt = null;
				}
			}

			
			if( bt != null )
			{
				final int samPos = samType;
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
							samValues[samPos] = b;

							if (!getSendedButton().isEnabled())
							{
								boolean enable = true;

								for (int i = 0; (i < samValues.length) && (enable); i++)
								{
									enable = samValues[i] != null;
								}

								getSendedButton().setEnabled(enable);
							}

							BasicPainter2D.changeColorPixels(Color.WHITE, Color.ORANGE, ((ImageIcon)b.getIcon()).getImage());
						}
						else
						{
							samValues[samPos] = null;
							getSendedButton().setEnabled(false);

							BasicPainter2D.changeColorPixels(Color.ORANGE, Color.WHITE, ((ImageIcon)b.getIcon()).getImage());
						}
					}
				});

				final String bTxt = btTxt;
				final int samAxis = samType;
				final float stepAux = step;
				bt.addComponentListener(new ComponentAdapter()
				{
					public void componentResized(ComponentEvent arg0) 
					{
						JToggleButton b = (JToggleButton)arg0.getSource();
						Dimension d = b.getSize();						

						int side = d.width;
						if (side > d.height)
						{
							side = d.height;
						}
						
						side -= 1;
						
						if (samAxis == 0)
						{
							b.setIcon(SamIcon.getSAMValence( ( new Integer(b.getName()) - 1 ) * stepAux, side, Color.BLACK, Color.WHITE));
						}
						else if (samAxis == 1)
						{
							b.setIcon(SamIcon.getSAMArousal( ( new Integer(b.getName() ) - 1 ) * stepAux, side, Color.BLACK, Color.WHITE));
						}
						else if ( samDominance)
						{
							b.setIcon(SamIcon.getSAMDominance( ( new Integer( b.getName() ) - 1 ) * stepAux, side, Color.BLACK, Color.WHITE));
						}
						else
						{
							Font f = new Font( Font.DIALOG, Font.BOLD, 18);
							FontMetrics fm = b.getFontMetrics(f);

							b.setIcon(SamIcon.getBasicEmotion(new Integer(b.getName()), side, Color.BLACK, Color.WHITE, bTxt, fm));
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
					/*
					try
					{
						Robot r = new Robot();
						Dimension d = getSize();

						r.mouseMove( d.width * 2, d.height * 2);
					}					
					catch (Exception ex) 
					{
					}
					*/
										
					String vals = "<";
					
					vals += samValues[ 0 ].getName();
					vals += ",";
					vals += samValues[ 1 ].getName();
					vals += ",";
					
					int i = Integer.parseInt( samValues[ 2 ].getName() );
					vals += Emotion.values()[ i - 1].name();
					vals += ">";
										
					RegistrarStatistic.addValenceArousalEmotionData( player.getId(), vals );
					
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
	
	private void init() 
	{
		this.setLayout( new BorderLayout() );
		
		JPanel samPanel = new JPanel();
		BoxLayout ly = new BoxLayout( samPanel, BoxLayout.Y_AXIS );
		samPanel.setLayout( ly );		
		
		int N = 10;
		
		JPanel panel = this.getSAMPictsPanel( 0, N );		
		samPanel.add( panel );
		
		panel = this.getSAMPictsPanel( 1, N );
		samPanel.add( panel );
		
		if( !this.samDominance )
		{
			N = Emotion.values().length;
		}
		panel = this.getSAMPictsPanel( 2, N );
		samPanel.add( panel );
		
		this.add( samPanel, BorderLayout.CENTER );
		this.add( this.getSendedButton(), BorderLayout.EAST );		
	}


}
