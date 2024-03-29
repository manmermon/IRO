/**
 * 
 */
package gui.panel;

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

import org.jfugue.theory.Note;

import gui.game.component.Frame;
import gui.game.component.sprite.Background;
import gui.game.component.sprite.Fret;
import gui.game.component.sprite.MusicNoteGroup;
import gui.game.component.sprite.MusicNoteGroup.State;
import gui.game.component.sprite.Stave;
import gui.game.screen.level.Level;
import GUI.panel.ExpandablePanel;
import config.ConfigApp;
import config.ConfigParameter;
import config.Player;
import config.Settings;
import config.language.Caption;
import config.language.Language;
import exceptions.ConfigParameterException;
import image.BasicPainter2D;
import music.sheet.IROTrack;
import stoppableThread.IStoppable;
import tools.IROFileUtils;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

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
	
	private static SelectLevelImagePanel selImgPanel = null;
	
	private JPanel contentPane;
	private JPanel panelImage;	
	private JPanel resourcesPanel;
	private JPanel imgListPanel;
	private JPanel previewScene;
	
	public static SelectLevelImagePanel getInstance()
	{
		if( selImgPanel == null )
		{
			selImgPanel = new SelectLevelImagePanel();
		}
		
		return selImgPanel;
	}
	
	private SelectLevelImagePanel( )
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
	}

	private JPanel getContainerPanel()
	{
		if( this.contentPane == null )
		{
			this.contentPane = new JPanel();
			this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			this.contentPane.setLayout( new GridLayout( 1, 2 ) );
			
			JScrollPane scroll1 = new JScrollPane( this.getImagePanel() );
			JScrollPane scroll2 = new JScrollPane( this.getPreviewScene() );
			
			scroll1.getVerticalScrollBar().setUnitIncrement( 5 );
			scroll2.getVerticalScrollBar().setUnitIncrement( 5 );
					
			this.contentPane.add( scroll1 ); //this.getImageTreePanel() ) );
			this.contentPane.add( scroll2 );
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
	
	public void updatePreviewLevelImages()
	{
		JPanel panel = this.getImageListPanel();
		panel.removeAll();
		
		panel.add( this.getImageExpandPanel( new File( ConfigApp.BACKGROUND_SPRITE_FILE_PATH ), ConfigApp.BACKGROUND_IMAGE , true ) );
		panel.add( this.getImageExpandPanel( new File( ConfigApp.NOTE_SPRITE_FILE_PATH ), ConfigApp.NOTE_IMAGE, false ) );
	}
	
 	private void setPreviewScene( )
	{		
		JPanel preview = this.getPreviewScene();
		Dimension size = preview.getSize();
		
		if( size.width > 0 && size.height > 0 )
		{
			String bgPath = null;
			String notPath = null;
		
			Player player = ConfigApp.getFirstPlayer();
			Settings cfg = null;
			
			if( player != null )
			{
				cfg = ConfigApp.getPlayerSetting( player );
			}
			
			if( cfg != null )
			{
				ConfigParameter par = cfg.getParameter( ConfigApp.BACKGROUND_IMAGE );
				Object p = par.getSelectedValue();
				if( p != null )
				{
					bgPath = p.toString();
				}
				
				par = cfg.getParameter( ConfigApp.NOTE_IMAGE );
				p = par.getSelectedValue();
				if( p != null )
				{
					notPath = p.toString();
				}
			}
						
			//Level lv = new Level( size, preview.getBounds() );
			Level lv = null;
			try 
			{
				Settings set = ConfigApp.getDefaultSettings();
				set.setPlayer( new Player() );
				List< Settings > ls = new ArrayList< Settings >();
				ls.add( set );				
				lv = new Level( new Rectangle( new Point(), size), ls, null, 0 );
				lv.stopActing( IStoppable.FORCE_STOP );
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			preview.setVisible( false );
			preview.removeAll();
			
			if( lv != null )
			{
				Background back = new Background( size, Level.BACKGROUND_ID );
				back.setZIndex( Level.PLANE_BRACKGROUND );
				lv.addBackgroud( back );
				if( bgPath != null )
				{
					try
					{
						File f = new File( bgPath );
						
						if( f.isDirectory() )
						{
							File[] imgFiles =IROFileUtils.getImageFiles( f );
							
							if( imgFiles != null && imgFiles.length > 0 )
							{
								int index = new Random().nextInt( imgFiles.length );
								f = imgFiles[ index ];
							}
							else
							{
								f = null;
							}
						}
						
						if( f != null )
						{
							Image img = ImageIO.read( f );
								
							img = img.getScaledInstance( back.getBounds().width
														, back.getBounds().height
														, Image.SCALE_FAST );
									
							back.setImage( (BufferedImage)BasicPainter2D.copyImage( img ) );
						}
					}
					catch (IOException ex)
					{	
					}
				}
				
		
				Stave pen = new Stave( size, Level.STAVE_ID );
				pen.setZIndex( 0 );
				lv.addStave( pen );
				
				Dimension sizeFret = new Dimension( pen.getStaveWidth() / 3, pen.getStaveHeigh() );
				//Fret fret = new Fret( pen, IScene.FRET_ID );
				Fret fret = new Fret( Level.FRET_ID, sizeFret );
				fret.setZIndex( 2 );
				Point2D.Double loc = new Point2D.Double();
				loc.x = lv.getSize().width / 2;
				loc.y = 0;
				fret.setScreenLocation( loc );
				
				float bg = 0;
				if( back != null )
				{
					bg = back.getAverageBrightness();
				}
				//System.out.println("SelectLevelImagePanel.setPreviewScene() " + bg);
				fret.setFretBrightness( bg );
				
				
				lv.addFret( fret );
								
				BufferedImage noteImg = null;				
				char n = 'A';
				int padding = 0;
				for( Settings setting : ConfigApp.getSettings() )
				{			
					List< IROTrack > notes = new ArrayList< IROTrack >( );
					IROTrack tr = new IROTrack();
					tr.addNote( 0, new Note( n + "" ) );
					notes.add( tr );
					MusicNoteGroup noteSprite1 = new MusicNoteGroup( "Test1"
																	, 0
																	, notes
																	, Level.NOTE_ID
																	//, pen
																	, pen.getRailHeight()
																	, (int)fret.getScreenLocation().x + padding
																	, 0D
																	, false );
					noteSprite1.setState( State.ACTION );
					
					if( notPath != null && noteImg == null )
					{
						try
						{
							File f = new File( notPath );
							
							if( f.isDirectory() )
							{
								File[] imgFiles = IROFileUtils.getImageFiles( f );
								
								if( imgFiles != null && imgFiles.length > 0 )
								{
									int index = new Random().nextInt( imgFiles.length );
									f = imgFiles[ index ];
								}
								else
								{
									f = null;
								}
							}
							
							if( f != null ) 
							{
								Image img = ImageIO.read( f );
								
								Dimension noteSize = noteSprite1.getSize();
								
								int l = (int)Math.max( noteSize.getWidth(), noteSize.getHeight() );
								int s = (int)Math.sqrt( l * l / 2 );  
								
								if( s <= 0 )
								{
									s = 1;
								}			
								
								noteImg = (BufferedImage)BasicPainter2D.copyImage( img.getScaledInstance( s , s
																						, BufferedImage.SCALE_SMOOTH ) );
							}
							
							/*
							Color bg = new Color( 255, 255, 255, 140 );
							Dimension s = noteSprite1.getBounds().getSize();
							noteImg = (BufferedImage)basicPainter2D.circle( 0, 0, s.width, bg, null );
							noteImg = (BufferedImage)basicPainter2D.composeImage( noteImg, 0, 0
																				, basicPainter2D.copyImage( 
																						img.getScaledInstance( noteImg.getWidth() 
																								, noteImg.getHeight()
																								, Image.SCALE_SMOOTH ) ) );
							
							noteImg = (BufferedImage)img;
							//*/
						}
						catch (Exception ex) 
						{
						}
					}
					noteSprite1.setImage( noteImg );
					
					ConfigParameter par = setting.getParameter( ConfigApp.ACTION_COLOR );
					Object c = par.getSelectedValue();
					if( c != null )
					{
						noteSprite1.setActionColor( (Color)c );
					}
					
					notes = new ArrayList< IROTrack >( );
					tr = new IROTrack();
					tr.addNote( 0, new Note( n + "" ) );
					notes.add( tr );
					MusicNoteGroup noteSprite2 = new MusicNoteGroup( "Test2"
																	, 0
																	, notes
																	, Level.NOTE_ID
																	//, pen
																	, pen.getRailHeight()
																	, (int)fret.getScreenLocation().x 
																		+ padding 
																		+ noteSprite1.getBounds().width
																	, 0D
																	, false );
					noteSprite2.setState( State.WAITING_ACTION );
					noteSprite2.setImage( noteImg );
					
					par = setting.getParameter( ConfigApp.WAITING_ACTION_COLOR );
					c = par.getSelectedValue();
					if( c != null )
					{
						noteSprite2.setWaitingActionColor( (Color)c );
					}
					
					notes = new ArrayList< IROTrack >( );
					tr = new IROTrack();
					tr.addNote( 0, new Note( n + "" ) );
					notes.add( tr );
					MusicNoteGroup noteSprite3 = new MusicNoteGroup( "Test3"
																	, 0
																	, notes
																	, Level.NOTE_ID
																	//, pen
																	, pen.getRailHeight()
																	, ( size.width + (int)fret.getBounds().getMaxX() ) / 2   
																		+ padding
																	, 0D
																	, false );
					noteSprite3.setState( State.PREACTION );
					noteSprite3.setImage( noteImg );
																	
					par = setting.getParameter( ConfigApp.PREACTION_COLOR );
					c = par.getSelectedValue();
					if( c != null )
					{
						noteSprite3.setPreactionColor( (Color)c);
					}
						
					lv.addNote( noteSprite1 );
					lv.addNote( noteSprite2 );
					lv.addNote( noteSprite3 );
					
					n++;
				}
				
				Frame fr = new Frame();
				fr.setScene(  lv.getScene() );
				
				preview.add( fr, BorderLayout.CENTER );				
			}
			
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
			
			this.resourcesPanel.add( this.getImageListPanel(), BorderLayout.NORTH );			
		}
		
		return this.resourcesPanel;
	}
	
	private JPanel getImageListPanel()
	{
		if( this.imgListPanel == null )
		{
			this.imgListPanel = new JPanel();
			this.imgListPanel.setLayout( new BoxLayout( this.imgListPanel, BoxLayout.Y_AXIS ) );
		}
		
		return this.imgListPanel;
	}
	
	private ExpandablePanel getImageExpandPanel( final File folder, final String configID, boolean enaDynamic )
	{
		ExpandablePanel expPanel = null;
		
		if( folder.isDirectory() )
		{	
			File[] files = IROFileUtils.getImageFiles( folder );
			
			if( files != null && files.length > 0)
			{				
				Settings cfg = null;
				Player player = ConfigApp.getFirstPlayer();
				final ConfigParameter par;
				
				
				if( player != null )
				{
					cfg = ConfigApp.getPlayerSetting( player );
				}
			
				if( cfg != null )
				{
					par = cfg.getParameter( configID );
				}
				else
				{
					par = null;
				}

				expPanel =  new ExpandablePanel();
				
				JPanel panel = new JPanel( );
				panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
				
				ButtonGroup btgr = new ButtonGroup();
				
				Caption cap = Language.getAllCaptions().get( Language.NONE );
				JRadioButton b = new JRadioButton( cap.getCaption( Language.getCurrentLanguage() ) );
								
				b.addActionListener( new ActionListener()
				{						
					@Override
					public void actionPerformed(ActionEvent e)
					{
						AbstractButton b = (AbstractButton)e.getSource();
						
						if( b.isSelected() )
						{	
							if( par != null )
							{
								try
								{																			
									par.removeSelectedValue();
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
				
				boolean selectedFileFound = false;
				if( enaDynamic )
				{
					cap = Language.getAllCaptions().get( Language.DYNAMIC );
					b = new JRadioButton( cap.getCaption( Language.getCurrentLanguage() ) );
									
					b.addActionListener( new ActionListener()
					{						
						@Override
						public void actionPerformed(ActionEvent e)
						{
							AbstractButton b = (AbstractButton)e.getSource();
							
							if( b.isSelected() )
							{	
								if( par != null )
								{
									try
									{
										String path = folder.getAbsolutePath();		
										if( !path.endsWith( File.separator ) )
										{
											path += File.separator;
										}
																			
										par.setSelectedValue(  path );
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
					
					if( par != null )
					{
						Object val = par.getSelectedValue();
						String pathFolder = folder.getAbsolutePath();
						if( !pathFolder.endsWith( File.separator ) )
						{
							pathFolder += File.separator;
						}
						
						if( val != null 
								&&
								val.toString().equals( pathFolder ) )
						{
							b.doClick();
							selectedFileFound = true;
						}
					}
					
					btgr.add( b );					
					panel.add( b );
				}
				
				for( File file : files )
				{
					b = new JRadioButton( file.getName() );
					
					final String filePath = file.getAbsolutePath();
					b.addActionListener( new ActionListener()
					{						
						@Override
						public void actionPerformed(ActionEvent e)
						{
							AbstractButton b = (AbstractButton)e.getSource();
							
							if( b.isSelected() )
							{
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
					
					if( par != null )
					{
						Object val = par.getSelectedValue();
						if( val != null 
								&&
								val.toString().equals( filePath ) )
						{
							b.doClick();
							selectedFileFound = true;
						}
					}
				}
				
				expPanel.setText( folder.getName() );
				expPanel.setContentPanel( panel );
				
				if( !selectedFileFound )
				{
					Enumeration< AbstractButton > en = btgr.getElements();
					if( en.hasMoreElements() )
					{
						en.nextElement().doClick();
					}
				}
			}
		}
		
		return expPanel;		
	}
}
