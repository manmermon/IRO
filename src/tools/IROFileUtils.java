package tools;

import java.io.File;
import java.io.FilenameFilter;

import javax.imageio.ImageIO;

public class IROFileUtils 
{
	public static File[] getImageFiles( File folder )
	{
		File[] files = new File[0];
		
		if( folder != null && folder.isDirectory() )
		{
			files = folder.listFiles( new FilenameFilter()
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
		}
		
		return files;
	}
}
