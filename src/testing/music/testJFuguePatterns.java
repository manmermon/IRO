package testing.music;

import java.io.File;
import java.util.regex.Matcher;

import org.jfugue.pattern.Pattern;

import config.ConfigApp;
import tools.MusicSheetTools;

public class testJFuguePatterns {

	public static void main(String[] args) 
	{
		try
		{
			String path = "zeldaLink2Past.mid";
			path = ConfigApp.SONG_FILE_PATH +  "zelda.mid";

			Pattern p = MusicSheetTools.getPatternFromMidi( new File( path ) );
			System.out.println("Tempo 1: " + p.toString());
			
			java.util.regex.Pattern pat = java.util.regex.Pattern.compile( "T[0-9]+" );
			Matcher matcher = pat.matcher( p.toString() );
			int t = 0;
			if (matcher.find())
			{
			    t = Integer.parseInt( matcher.group(0).trim().replaceAll( "T", "" ) );
			}
			
			System.out.println("testJFuguePatterns.main() " + matcher.group(0).trim());
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
