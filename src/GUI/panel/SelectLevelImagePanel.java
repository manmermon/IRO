/**
 * 
 */
package GUI.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfugue.theory.Note;

import GUI.ExpandablePanel;
import GUI.game.component.sprite.Background;
import GUI.game.component.sprite.Fret;
import GUI.game.component.sprite.MusicNoteGroup;
import GUI.game.component.sprite.Pentragram;
import GUI.game.screen.IScene;
import GUI.game.screen.level.Level;
import config.ConfigApp;
import config.ConfigParameter;
import exceptions.ConfigParameterException;
import music.IROTrack;

import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author manuel
 *
 */
public class SelectLevelImagePanel extends JPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6792905300220873072L;
	
	private JPanel contentPane;
	private JPanel panelImage;
	
	private JPanel resourcesPanel;
	private JPanel previewScene;
	
	public SelectLevelImagePanel()
	{
		super.setLayout( new BorderLayout() );
		this.add( this.getContainerPanel(), BorderLayout.CENTER );
		
		super.addComponentListener( new ComponentAdapter()
		{	
			@Override
			public void componentShown(ComponentEvent e)
			{
				setPreviewScene();
			}
		});
				
		this.setPreviewScene( );
	}
	
	private JPanel getContainerPanel()
	{
		if( this.contentPane == null )
		{
			this.contentPane = new JPanel();
			this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			this.contentPane.setLayout( new GridLayout( 1, 2 ) );
			
			this.contentPane.add( new JScrollPane( this.getImagePanel() )); //this.getImageTreePanel() ) );
			this.contentPane.add( new JScrollPane( this.getPreviewScene() ) );
		}
		
		return this.contentPane;
	}
	
	private JPanel getPreviewScene()
	{
		if( this.previewScene == null )
		{
			this.previewScene = new JPanel( new BorderLayout() );
		}
		
		return this.previewScene;
	}
	
	private void setPreviewScene( )
	{		
		JPanel preview = this.getPreviewScene();
		Dimension size = preview.getSize();
		
		if( size.width > 0 && size.height > 0 )
		{
			String bgPath = null;
			String notPath = null;
			
			ConfigParameter par = ConfigApp.getParameter( ConfigApp.BACKGROUND_IMAGE );
			Object p = par.getSelectedValue();
			if( p != null )
			{
				bgPath = p.toString();
			}
			
			par = ConfigApp.getParameter( ConfigApp.NOTE_IMAGE );
			p = par.getSelectedValue();
			if( p != null )
			{
				notPath = p.toString();
			}
			
			Level lv = new Level( size );
						
			Background back = new Background( size, IScene.BACKGROUND_ID, bgPath );
			back.setZIndex( -1 );
			lv.addBackgroud( back );
	
			Pentragram pen = new Pentragram( size, IScene.PENTRAGRAM_ID );
			pen.setZIndex( 0 );
			lv.addPentagram( pen );
			
			Fret fret = new Fret( pen, IScene.FRET_ID );
			fret.setZIndex( 2 );
			Point2D.Double loc = new Point2D.Double();
			loc.x = lv.getSize().width / 2;
			loc.y = 0;
			fret.setScreenLocation( loc );
			lv.addFret( fret );
			
			List< IROTrack > notes = new ArrayList< IROTrack >( );
			IROTrack tr = new IROTrack();
			tr.addNote( 0, new Note( "A" ) );
			notes.add( tr );
			MusicNoteGroup noteSprite1 = new MusicNoteGroup( "Test1"
															, notes
															, IScene.NOTE_ID
															, pen
															, (int)fret.getScreenLocation().x  
															, 0D
															, false
															, notPath );
			
			par = ConfigApp.getParameter( ConfigApp.ACTION_COLOR );
			Object c = par.getSelectedValue();
			if( c != null )
			{
				noteSprite1.setColor( (Color)c );
			}
			
			notes = new ArrayList< IROTrack >( );
			tr = new IROTrack();
			tr.addNote( 0, new Note( "D" ) );
			notes.add( tr );
			MusicNoteGroup noteSprite2 = new MusicNoteGroup( "Test2"
															, notes
															, IScene.NOTE_ID
															, pen
															, (int)fret.getScreenLocation().x  
															, 0D
															, false
															, notPath );
			
			par = ConfigApp.getParameter( ConfigApp.WAITING_ACTION_COLOR );
			c = par.getSelectedValue();
			if( c != null )
			{
				noteSprite2.setColor( (Color)c );
			}
			
			notes = new ArrayList< IROTrack >( );
			tr = new IROTrack();
			tr.addNote( 0, new Note( "C" ) );
			notes.add( tr );
			MusicNoteGroup noteSprite3 = new MusicNoteGroup( "Test3"
															, notes
															, IScene.NOTE_ID
															, pen
															, ( size.width + (int)fret.getBounds().getMaxX() ) / 2     
															, 0D
															, false
															, notPath );
															
			par = ConfigApp.getParameter( ConfigApp.PREACTION_COLOR );
			c = par.getSelectedValue();
			if( c != null )
			{
				noteSprite3.setColor( (Color)c );
			}
				
			lv.addNote( noteSprite1 );
			lv.addNote( noteSprite2 );
			lv.addNote( noteSprite3 );
			
			preview.setVisible( false );
			preview.removeAll();
			preview.add( lv.getScene(), BorderLayout.CENTER );
			preview.setVisible( true );			
		}
	}
	
	private JPanel getImagePanel()
	{
		if( this.panelImage == null )
		{
			this.panelImage = new JPanel( new BorderLayout() );
			
			this.panelImage.add( this.getImageResources(), BorderLayout.CENTER );
		}
		
		return this.panelImage;
	}
	
	private JPanel getImageResources()
	{
		if( this.resourcesPanel == null )
		{	
			this.resourcesPanel = new JPanel( new BorderLayout() );
			
			JPanel panel = new JPanel();
			panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
			
			panel.add( this.getImageExpandPanel( new File( ConfigApp.BACKGROUND_SPRITE_FILE_PATH ), ConfigApp.BACKGROUND_IMAGE ) );
			panel.add( this.getImageExpandPanel( new File( ConfigApp.NOTE_SPRITE_FILE_PATH ), ConfigApp.NOTE_IMAGE ) );
			
			this.resourcesPanel.add( panel, BorderLayout.NORTH );			
		}
		
		return this.resourcesPanel;
	}
	
	public ExpandablePanel getImageExpandPanel( File folder, final String configID )
	{
		ExpandablePanel expPanel = null;
		
		if( folder.isDirectory() )
		{	
			File[] files = folder.listFiles( new FilenameFilter()
			{
			    public boolean accept(File dir, String name) 
			    {
			    	String[] formatNames = ImageIO.getReaderFormatNames();
			    	boolean accept = false;
			    	
			    	for( int i = 0; i < formatNames.length && !accept; i++ )
			    	{
			    		String format = formatNames[ i ];
			    		accept = name.toLowerCase().endsWith( "." + format.toLowerCase() );
			    	}
			    	
			        return accept; 
			    }
			});
			
			if( files != null && files.length > 0)
			{
				expPanel =  new ExpandablePanel();
				
				JPanel panel = new JPanel( );
				panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
				
				ButtonGroup btgr = new ButtonGroup();
				for( File file : files )
				{
					JRadioButton b = new JRadioButton( file.getName() );
					
					final String filePath = file.getAbsolutePath();
					b.addChangeListener( new ChangeListener()
					{						
						@Override
						public void stateChanged(ChangeEvent arg0)
						{
							AbstractButton b = (AbstractButton)arg0.getSource();
							
							if( b.isSelected() )
							{
								ConfigParameter par = ConfigApp.getParameter( configID );
								if( par != null )
								{
									try
									{
										par.setSelectedValue( filePath );
									} 
									catch (ConfigParameterException ex)
									{
										ex.printStackTrace();
									}
								}
								
								setPreviewScene();
							}
						}
					});
					
					btgr.add( b );					
					panel.add( b );
				}
				
				expPanel.setText( folder.getName() );
				expPanel.setContentPanel( panel );
				
				Enumeration< AbstractButton > en = btgr.getElements();
				if( en.hasMoreElements() )
				{
					en.nextElement().setSelected( true );
				}
			}
		}
		
		return expPanel;		
	}
}
