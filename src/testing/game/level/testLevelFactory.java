/**
 * 
 */
package testing.game.level;

import java.awt.Rectangle;
import java.io.File;

import GUI.game.screen.level.LevelFactory;
import config.ConfigApp;

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
			LevelFactory.getLevel( new File( ConfigApp.SONG_FILE_PATH + "FF7.mid"), new Rectangle(0,0, 100, 100), 2, 2);
		}
		catch (Exception ex) 
		{

			ex.printStackTrace();
		}
	}
}
