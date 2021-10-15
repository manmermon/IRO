package testing.music;

import java.io.File;

import org.jfugue.pattern.Pattern;

import tools.MusicSheetTools;

public class testJFuguePatterns {

	public static void main(String[] args) 
	{
		try
		{
			String path = "G:\\Sync_datos\\WorkSpace\\GitHub\\IRO\\IRO\\src\\sheets\\zeldaLink2Past.mid";
			path = ".\\sheets\\zelda.mid";

			Pattern p = MusicSheetTools.getPatternFromMidi( new File( path ) );
			System.out.println("Tempo 1: " + p.toString());
			
			String tx = p.toString().replaceAll( "T[0-9]+", "T120");
			p = new Pattern( tx );
			System.out.println("Tempo 2: " + p.toString());
			System.out.println("testJFuguePatterns.main() " + p.getTokens() );
		}
		catch (Exception e) 
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
