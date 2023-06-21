package testing.other;

import java.io.File;

public class testFile {

	public static void main(String[] args) 
	{
		File f = new File( "./userStatusSurvey.log");
		File f2 = new File( "./userStatusSurvey.log_copy");
		
		f.renameTo( f2 );
		
		if( f.exists() )
		{
			System.out.println("testFile.main() exist" );
		}
		else
		{
			System.out.println("testFile.main() remove");
		}
	}

}
