package testing.music.wav;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.SourceDataLine;

import config.ConfigApp;
import general.ConvertTo;

public class testWavPlayer 
{	
	public static void main(String[] args) 
	{
		try
		{

			playSound( ConfigApp.SONG_FILE_PATH + "Aitana-mariposas.wav" );
			System.out.println("testWavPlayer.main()");

		}
		catch (Exception e) 
		{
		}
	}


	/**
	 * @param filename the name of the file that is going to be played
	 */
	public static void playSound(String filename) throws Exception
	{
		final int BUFFER_SIZE = 128000;
		File soundFile;
		AudioInputStream audioStream;
		AudioFormat audioFormat;
		SourceDataLine sourceLine;
		SourceDataLine sourceLine2;
		
		String strFilename = filename;

		soundFile = new File(strFilename);
		audioStream = AudioSystem.getAudioInputStream(soundFile);

		audioFormat = audioStream.getFormat();
		
		float fs = audioFormat.getSampleRate();
		
		AudioFormat af2 = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED, fs * 2, 16, audioFormat.getChannels(), 2 * audioFormat.getChannels(), fs, audioFormat.isBigEndian() );
		
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		DataLine.Info info2 = new DataLine.Info(SourceDataLine.class, af2);
		
		sourceLine = (SourceDataLine) AudioSystem.getLine(info);		
		sourceLine2 = (SourceDataLine) AudioSystem.getLine( info2 );

		sourceLine.addLineListener( new LineListener() 
		{			
			@Override
			public void update(LineEvent event) 
			{
				if( event.getType() ==LineEvent.Type.OPEN)					
				{
					System.out.println("testWavPlayer.playSound() OPEN");
				}
				else if( event.getType() ==LineEvent.Type.CLOSE)
				{					
					System.out.println("testWavPlayer.playSound() CLOSE");
				}
				else if( event.getType() ==LineEvent.Type.START)
				{
					System.out.println("testWavPlayer.playSound() START");
				}
				else if( event.getType() ==LineEvent.Type.STOP )
				{
					System.out.println("testWavPlayer.playSound() STOP");
				}
			}
		});
		
		sourceLine.open(audioFormat);  
		sourceLine.start();
		
		sourceLine2.open( af2 );
		sourceLine2.start();

		int nBytesRead = 0;
		byte[] abData = new byte[BUFFER_SIZE];		
		
		
		int nf = 0;
		boolean eff = false;
		while (nBytesRead != -1) 
		{
			nf++;
			System.out.println("testWavPlayer.playSound() " + nf);
			try {
				nBytesRead = audioStream.read(abData, 0, abData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (nBytesRead >= 0) {
				//@SuppressWarnings("unused")
				//int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
					
				eff = ( nf % 8 == 0 ) ? !eff : eff;
				
				if( !eff )
				{
					int nBytesWritten = sourceLine.write( abData, 0, nBytesRead );
				}
				else
				{
					double[] d = ConvertTo.ByteArray2DoubleArray( abData, audioFormat.isBigEndian() );
					
					for (int i = 0; i < d.length; i++ )
					{
						d[ i ] = d[ i ] * 2 * Math.cos( 2 * Math.PI * ( 100 / fs ) * i  );
					}
					
					abData = ConvertTo.doubleArray2byteArray( d, audioFormat.isBigEndian() );
					
					System.out.println("testWavPlayer.playSound()");
					int nBytesWritten = sourceLine.write( abData, 0, nBytesRead );
				}
			}
		}		
		sourceLine.drain();
		sourceLine.close();
	}
}
