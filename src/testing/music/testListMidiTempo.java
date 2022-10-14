package testing.music;

import java.io.File;
import java.io.FilenameFilter;

import config.ConfigApp;
import tools.MusicSheetTools;

public class testListMidiTempo 
{
	public static void main(String[] args) 
	{
		try
		{	
			File f = new File( ConfigApp.SONG_FILE_PATH );
			
			FilenameFilter filter = new FilenameFilter() 
			{
			    @Override
			    public boolean accept(File dir, String name) {
			        return name.endsWith(".mid");
			    }
			};
			
			File[] files = f.listFiles( filter );
			
			if( files != null && files.length > 0 )
			{
				String[] filePaths = new String[ files.length ];
				for( int i = 0; i < files.length; i++ )
				{
						filePaths[ i ] = files[ i ].getName();
				}
			}
			
			for( File file : files )
			{
				int t = MusicSheetTools.getTempo( MusicSheetTools.getPatternFromMidi( file ).toString() );
				
				System.out.println( file.getName() + ": Tempo " + t );
			}	
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
