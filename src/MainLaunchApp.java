

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import GUI.AppUI;
import GUI.OpeningDialog;
import GUI.TextAreaPrintStream;
import GUI.AppIcons;
import config.ConfigApp;
import config.language.Language;
import control.RefreshControl;
import control.ScreenControl;
import control.inputs.InputControl;

public class MainLaunchApp
{
	/*
	 * @param args
	 */
	public static void main(String[] args)
	{
		try 
		{
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
			//UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
		} 
		catch (Exception e) 
		{
			try 
			{
				// Set cross-platform Java L&F (also called "Metal")
				UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
			}
			catch ( Exception e1) 
			{
			}
		}
		
		try
		{
			createApplication();
		}
		catch (Throwable e2)
		{
			showError( e2, true );
		}
		finally
		{			
		}		
	}


	private static void createApplication() throws Throwable
	{
		AppUI ui = createAppGUI();		
		createAppCoreControl( ui );
	}

	private static void createAppCoreControl( AppUI ui )
	{
		try
		{
			ScreenControl screenCtr = ScreenControl.getInstance();
			screenCtr.startThread();
			
			RefreshControl drawCtrl = RefreshControl.getInstance();
			drawCtrl.startThread();
			
			InputControl inCtrl = InputControl.getInstance();
			inCtrl.startThread();
		}
		catch (Exception e)
		{
			showError( e, true );
		}
	}

	private static AppUI createAppGUI() throws Exception
	{	
		Language.changeLanguage( "es-es" );
		
		Dimension openDim = new Dimension( 500, 200 );
		
		AppUI ui = AppUI.getInstance();
		
		Toolkit t = Toolkit.getDefaultToolkit();
		Dimension dm = t.getScreenSize();
		Insets pad = t.getScreenInsets( ui.getGraphicsConfiguration() );
		
		OpeningDialog open = new OpeningDialog( openDim 
												,  AppIcons.appIcon( 128 )
												, ConfigApp.shortNameApp
												, "<html><center><h1>Opening " + ConfigApp.fullNameApp + ".<br>Wait please...</h1></center></html>" 
												, Color.WHITE );
		open.setVisible( false );
		open.setDefaultCloseOperation( OpeningDialog.DISPOSE_ON_CLOSE );
				
		open.setLocation( dm.width / 2 - openDim.width / 2, dm.height / 2 - openDim.height / 2 );
		
		ui.setIconImage( AppIcons.appIcon( 128 ) );
		ui.setTitle(  ConfigApp.fullNameApp + " - " + ConfigApp.shortNameApp );
		
		ui.setBackground( SystemColor.info );

		dm.width = (dm.width / 2 - (pad.left + pad.right));
		dm.height = (dm.height / 2 - (pad.top + pad.bottom));

		if( dm.width < 650 )
		{
			dm.width = 650;
		}
		
		if( dm.height < 650 )
		{
			dm.height = 650;
		}
		
		ui.setSize( dm );

		ui.toFront();
		Dimension d = new Dimension( dm );
		d.width /= 5;

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gd.getDefaultConfiguration());
		ui.setLocation( insets.left + 1, insets.top + 1 );

		ui.setVisible(true);
		
		open.dispose();
		
		return ui;
	}

	private static void showError( Throwable e, final boolean fatalError )
	{
		JTextArea jta = new JTextArea();
		jta.setAutoscrolls( true );
		jta.setEditable( false );
		jta.setLineWrap( true );
		jta.setTabSize( 0 );

		TextAreaPrintStream log = new TextAreaPrintStream( jta, new ByteArrayOutputStream() );

		e.printStackTrace( log );

		String[] lines = jta.getText().split( "\n" );
		int wd = Integer.MIN_VALUE;
		FontMetrics fm = jta.getFontMetrics( jta.getFont() );
		for (int i = 0; i < lines.length; i++)
		{
			if (wd < fm.stringWidth( lines[i] ) )
			{
				wd = fm.stringWidth( lines[i] );
			}
		}

		final JDialog p = new JDialog();

		Icon icono = UIManager.getIcon( "OptionPane.warningIcon" );
		
		int w = icono.getIconWidth();
		int h = icono.getIconHeight();
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		
		BufferedImage img = gc.createCompatibleImage( w, h, BufferedImage.TYPE_INT_ARGB ); 
		Graphics2D g = img.createGraphics();
		icono.paintIcon( null, g, 0, 0 );
		p.setIconImage( img );

		p.setTitle( "Problem" );
		
		if( fatalError )
		{
			p.setTitle( "Fatal Error" );
		}
		
		Dimension d = new Dimension( (int)( wd * 1.25D ), fm.getHeight() * 10 );
		p.setSize( d );

		Point pos = ge.getCenterPoint();
		pos.x -= d.width / 2;
		pos.y -= d.height / 2;
		p.setLocation(pos);

		p.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				if( fatalError )
				{
					System.exit( 0 );
				}
			}

		});
		
		JButton close = new JButton("Cerrar");
		close.addActionListener(new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				if( fatalError )
				{
					System.exit( 0 );
				}
				else
				{
					p.dispose();
				}
			}

		});
		
		p.add( new JScrollPane( jta ), BorderLayout.CENTER );
		p.add( close, BorderLayout.SOUTH );
		p.toFront();
		p.setVisible( true );		
	}
}
