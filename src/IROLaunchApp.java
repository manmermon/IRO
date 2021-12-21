

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
import java.lang.reflect.Field;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.sun.jna.Platform;

import gui.AppIcon;
import gui.MainAppUI;
import GUI.dialog.OpeningDialog;
import GUI.text.TextAreaPrintStream;
import config.ConfigApp;
import config.language.Language;
import control.RefreshControl;
import control.ScreenControl;
import image.icon.GeneralAppIcon;

public class IROLaunchApp
{
	/*
	 * @param args
	 */
	public static void main(String[] args)
	{
		try 
		{
			String OS = System.getProperty("os.name").toLowerCase();
						
			String p = System.getProperty( "user.dir" ) + "/" + ConfigApp.SYSTEM_LIB_WIN_PATH;
			
			if( Platform.getOSType() == Platform.LINUX )
			{
				p = System.getProperty( "user.dir" ) + "/" + ConfigApp.SYSTEM_LIB_LINUX_PATH;
			}
			else if( Platform.getOSType() == Platform.MAC )
			{
				p = System.getProperty( "user.dir" ) + "/" + ConfigApp.SYSTEM_LIB_MACOS_PATH;
			}
			
			try 
			{
				addLibraryPath( p );
			} 
			catch (Exception e) 
			{
				//showError( e, false );
			}
			
			
			if( (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) )
			{
				UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
			}
			else
			{
				UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
			}
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
			e2.printStackTrace();
			
			showError( e2, true );
		}
		finally
		{			
		}		
	}

	private static void addLibraryPath(String pathToAdd) throws Exception 
	{
		Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
		usrPathsField.setAccessible(true);
		String[] paths = (String[]) usrPathsField.get(null);
		for (String path : paths)
		{
			if (path.equals(pathToAdd))
			{
				return;
			}
		}

		String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
		newPaths[newPaths.length - 1] = pathToAdd;
		usrPathsField.set(null, newPaths);
	}

	private static void createApplication() throws Throwable
	{
		MainAppUI ui = createAppGUI();		
		createAppCoreControl( ui );
	}

	private static void createAppCoreControl( MainAppUI ui )
	{
		try
		{
			ScreenControl screenCtr = ScreenControl.getInstance();
			screenCtr.startThread();
			
			RefreshControl drawCtrl = RefreshControl.getInstance();
			drawCtrl.startThread();
			
			/*
			InputControl inCtrl = InputControl.getInstance();
			inCtrl.startThread();
			*/			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			showError( e, true );
		}
	}

	private static MainAppUI createAppGUI() throws Exception
	{	
		Language.changeLanguage( "es-es" );
		
		Dimension openDim = new Dimension( 500, 200 );
		
		Toolkit t = Toolkit.getDefaultToolkit();
		Dimension dm = t.getScreenSize();
		
		OpeningDialog open = new OpeningDialog( openDim 
												,  AppIcon.appIcon( 128 )
												, ConfigApp.shortNameApp
												, "<html><center><h1>Opening " + ConfigApp.fullNameApp + ".<br>Wait please...</h1></center></html>" 
												, Color.WHITE );
		
		open.setDefaultCloseOperation( OpeningDialog.DISPOSE_ON_CLOSE );
				
		open.setLocation( dm.width / 2 - openDim.width / 2, dm.height / 2 - openDim.height / 2 );
		open.setVisible( true );
		
		MainAppUI ui = MainAppUI.getInstance();		

		Insets pad = t.getScreenInsets( ui.getGraphicsConfiguration() );
		
		ui.setIconImage( AppIcon.appIcon( 128 ) );
		ui.setTitle(  ConfigApp.fullNameApp + " - " + ConfigApp.shortNameApp );
		
		ui.setBackground( SystemColor.info );

		dm.width = ( ( 2 * dm.width ) / 3 - (pad.left + pad.right));
		dm.height = (( 2 * dm.height ) / 3 - (pad.top + pad.bottom));

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
		
		if( ConfigApp.checkDBFile() )
		{
			JOptionPane.showMessageDialog( ui, "Database file no found. A new one was created."
										, "Database", JOptionPane.WARNING_MESSAGE);
		}
		
		
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

		
		//Icon icono = UIManager.getIcon( "OptionPane.warningIcon" );
		Icon icon = GeneralAppIcon.Warning( 16, Color.ORANGE );
					
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		
		BufferedImage img = gc.createCompatibleImage( w, h, BufferedImage.TYPE_INT_ARGB ); 
		Graphics2D g = img.createGraphics();
		icon.paintIcon( null, g, 0, 0 );
		p.setIconImage( img );
		
		p.setTitle( "Problem" );
		
		if( fatalError )
		{
			p.setTitle( "Fatal Error" );
		}
		
		Dimension d = new Dimension( (int)( wd * 1.25D ), fm.getHeight() * 10 );
		if( d.width >= Toolkit.getDefaultToolkit().getScreenSize().width )
		{
			d.width = (int)( Toolkit.getDefaultToolkit().getScreenSize().width * 0.8 ) ;
		}
		
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
