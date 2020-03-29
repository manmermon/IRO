package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Collection;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import config.ConfigApp;
import config.ConfigParameter;
import config.ConfigParameter.ParameterType;
import config.language.Language;
import general.NumberRange;

public class AppSettingDialog extends JDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1219995574372606332L;
	
	private JPanel containerPanel = null;
	
	private JScrollPane scroll = null;

	private Dimension fieldSize = new Dimension( 450 / 5, 20 );
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			AppSettingDialog dialog = new AppSettingDialog();
			dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
			dialog.setVisible( true );
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AppSettingDialog()
	{		
		super.setBounds(100, 100, 450, 300);
		super.getContentPane().setLayout( new BorderLayout() );
		
		super.getContentPane().add( this.getContainerScroll(), BorderLayout.CENTER );
	}

	private JScrollPane getContainerScroll()	
	{
		if( this.scroll == null )
		{
			this.scroll = new JScrollPane( this.getContainerPanel() );
			
			this.scroll.setVisible( true );
		}
		
		return this.scroll;
	}
	
	private JPanel getContainerPanel()
	{
		if( this.containerPanel == null )
		{
			this.containerPanel = new JPanel();
			
			this.containerPanel.setLayout(new GridBagLayout() );
			
			SpringLayout ly = new SpringLayout();
			
			
			this.containerPanel.setLayout( ly );
			this.containerPanel.setBorder( new EmptyBorder(5, 5, 5, 5) );
			
			Collection< ConfigParameter > pars = ConfigApp.getParameters();
			
			int cols = 2;
			
			int numPars = 0;
			for( ConfigParameter p : pars )
			{
				GridBagConstraints gbc =  new GridBagConstraints();
				gbc.gridx = numPars % cols;
				gbc.gridy = numPars / cols;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.insets = new Insets(0, 0, 5, 0);
				
				//this.containerPanel.add( this.getParamenterPanel( p ), gbc );
				this.containerPanel.add( this.getParamenterPanel( p ) );
				
				numPars++;
			}
		}
		
		return this.containerPanel;
	}
	
	private JPanel getParamenterPanel( ConfigParameter par )
	{		
		JPanel panel = new JPanel();
		//*
		Dimension d  = new Dimension( fieldSize );
		
		
		panel.setPreferredSize( d );
		panel.setBackground( Color.GREEN);
		//*/
		
		if( par != null )
		{
			ConfigParameter lang = ConfigApp.getProperty( ConfigApp.LANGUAGE );
				
			String l = Language.defaultLanguage;
			
			Object[] languages = lang.getValues();
			if( languages != null && languages.length > 0 )
			{
				l = languages[ 0 ].toString();
			}
			
			panel.setLayout( new GridBagLayout() );
			
			JLabel lb = new JLabel();
			lb.setText( par.get_ID().getCaption( l ) );			
			lb.setPreferredSize( fieldSize );
						
			Component comp = this.getParComponent( par );
			comp.setPreferredSize( fieldSize );
			
			GridBagConstraints gbc_l =  new GridBagConstraints();
			gbc_l.gridx = 0;
			gbc_l.gridy = 0;
			gbc_l.fill = GridBagConstraints.HORIZONTAL;
			gbc_l.insets = new Insets(0, 0, 5, 0);			
			panel.add( lb, gbc_l );
			
			GridBagConstraints gbc =  new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0, 0, 0, 0);
			panel.add( comp, gbc );
		}
		
		panel.setVisible( true );
		
		return panel;
	}
	
	private Component getParComponent( ConfigParameter par )
	{
		Component c = null;
		
		if( par != null )
		{
			ParameterType type = par.get_type();
			Object[] values = par.getValues();
					
			if( values != null && values.length > 0 )
			{
				switch ( type )
				{
				case NUMBER:
				{
					if( values.length == 1 )
					{
						NumberRange rng = par.getNumberRange();
						
						if( rng == null )
						{
							rng = new NumberRange( Double.MIN_VALUE, Double.MAX_VALUE );
						}
						
						JSpinner sp = new JSpinner();
						SpinnerNumberModel model = new SpinnerNumberModel( (Number)values[0], rng.getMin(), rng.getMax(), 1 );
						sp.setModel( model );		
						
						c = sp;
					}
					else
					{
						JComboBox< Number > combox = new JComboBox<Number>();
						
						for( Object val : values )
						{
							combox.addItem( (Number)val );
						}
						
						combox.setSelectedIndex( 0 );
						
						c = combox;
					}					

					break;
				}
				case STRING:
				{
					if( values.length == 1 )
					{
						c = new JTextField( values[ 0 ].toString() );
						
					}
					else
					{
						JComboBox< String > combox = new JComboBox<String>();
						
						for( Object val : values )
						{
							combox.addItem( val.toString() );
						}
						combox.setSelectedIndex( 0 );
						
						c = combox;						
					}
					
					break;
				}
				case BOOLEAN:
				{
					JCheckBox cb = new JCheckBox();
					cb.setSelected( (Boolean)values[ 0 ] );
					
					c = cb;
							
					break;
				}
				default:
					break;
				}
			}
		}
		
		return c;
	}
}

