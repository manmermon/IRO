/**
 * 
 */
package testing.game.level;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gui.game.screen.level.Level;
import config.ConfigApp;
import config.ConfigParameter;
import config.Player;
import config.Settings;
import control.controller.IControllerMetadata;
import deprecated.level.build.LevelMusicBuilder;
import exceptions.ConfigParameterException;
import general.NumberRange;
import tools.MusicSheetTools;

/**
 * @author manuel
 *
 */
public class testLevelFactory
{
	public static void main(String[] args) 
	{
		try
		{
			List< Settings > settings = new ArrayList<Settings>();
			for( Player player : ConfigApp.getPlayers() )
			{
				settings.add( ConfigApp.getPlayerSetting( player ) );
			}
			
			Level lv = LevelMusicBuilder.getLevel( new File( ConfigApp.SONG_FILE_PATH + "zelda.mid"), new Dimension(100, 100), settings );
			LevelMusicBuilder.changeLevelSpeed( lv );
			
			int t = 73;
			System.out.println( "testLevelFactory.main() " + MusicSheetTools.getQuarterTempo2Second( t ) );
			System.out.println( "testLevelFactory.main() " + MusicSheetTools.getWholeSecond2Tempo( MusicSheetTools.getQuarterTempo2Second( t ) ) );
				
		}
		catch (Exception ex) 
		{

			ex.printStackTrace();
		}
	}
}
