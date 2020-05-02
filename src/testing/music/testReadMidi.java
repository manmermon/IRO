package testing.music;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.jfugue.midi.MidiParser;
import org.jfugue.midi.MidiParserListener;
import org.jfugue.player.Player;
import org.jfugue.theory.Note;
import org.staccato.StaccatoParser;
import org.staccato.StaccatoParserListener;

import general.ArrayTreeMap;

public class testReadMidi 
{
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

	public static void main(String[] args) throws Exception 
	{
		String path = "./sheets/zelda.mid";
		//String path = "G:\\Sync_datos\\WorkSpace\\GitHub\\IRO\\IRO\\src\\sheets\\tes7.mid";
		File midiMusicSheelFile = new File( path );

		//*

		StaccatoParserListener listener = new StaccatoParserListener();

		MidiParser parser = new MidiParser();
		parser.addParserListener( listener );
		parser.parse( MidiSystem.getSequence( midiMusicSheelFile ) );        

		System.out.println("testReadMidi.main() " + listener.getPattern() );
		
		StaccatoParser staccatoParser = new StaccatoParser();
		MidiParserListener midiParserListener = new MidiParserListener();
		staccatoParser.addParserListener( midiParserListener );
		staccatoParser.parse( listener.getPattern() );
		Sequence sequence = midiParserListener.getSequence();

		long millis = sequence.getMicrosecondLength(); //micros
		String time = String.format("%02d:%02d:%02d", 
								TimeUnit.MICROSECONDS.toHours(millis),
								TimeUnit.MICROSECONDS.toMinutes(millis) -  
								TimeUnit.HOURS.toMinutes(TimeUnit.MICROSECONDS.toHours(millis)), // The change is in this line
								TimeUnit.MICROSECONDS.toSeconds(millis) - 
								TimeUnit.MINUTES.toSeconds(TimeUnit.MICROSECONDS.toMinutes(millis)));
		
		
		System.out.println("testReadMidi.main() " + listener.getPattern() );
		System.out.println("testReadMidi.main() " + time );
		
		
		int trackNumber = 0;        
		Map< Integer,  ArrayTreeMap< Long, Note > > noteChannels = new HashMap< Integer,  ArrayTreeMap< Long, Note > >();

		Sequence seq = MidiSystem.getSequence( midiMusicSheelFile );

		int quarterTicks = seq.getResolution(); //sequence.getResolution();
		int BPM = 120;
		
		for (Track track : MidiSystem.getSequence( midiMusicSheelFile ).getTracks() )  
		{   
			System.out.println("Track " + trackNumber + ": size = " + track.size() + ", ticks = " +track.ticks() );
			for( int i = 0; i < track.size(); i++ )
			{
				System.out.println( "-> " + track.get( i ).getMessage());
			}
			System.out.println();
		}
		
		for (Track track : MidiSystem.getSequence( midiMusicSheelFile ).getTracks() )  
		{   
			System.out.println("Track " + trackNumber + ": size = " + track.size() + ", ticks = " +track.ticks() );
			System.out.println();
			
			ArrayTreeMap< Integer, MidiEvent > eventsByChannel = new ArrayTreeMap< Integer, MidiEvent >();

			for (int i=0; i < track.size(); i++) 
			{ 
				MidiEvent event = track.get( i );
				MidiMessage message = event.getMessage();

				if (message instanceof ShortMessage)
				{
					eventsByChannel.put( ((ShortMessage) message).getChannel(), event );
				}
				else if( message instanceof MetaMessage )
				{                	
					MetaMessage msg = (MetaMessage)message;

					if( msg.getType() == 81 ) // tempo
					{
						byte[] data = msg.getData();
						int tempo = getIntValue( data );//(data[0] & 0xff) << 16 | (data[1] & 0xff) << 8 | (data[2] & 0xff);
						BPM = 60_000_000 / tempo;

						System.out.println("MetaMessage: type "  + msg.getType() + " bpm " + BPM + " tempo " + tempo );
					}
					else if( msg.getType() == 1 ) // Text
					{
						System.out.println("MetaMessage: type "  + msg.getType() + " text " + new String( msg.getData() ) );
					}
					else if( msg.getType() == 2 ) // Copy right
					{
						System.out.println("MetaMessage: type "  + msg.getType() + " Copyright "+ new String( msg.getData() ) );
					}
					else if( msg.getType() == 3 ) // Track name
					{
						System.out.println("MetaMessage: type "  + msg.getType() + " track name "+ new String( msg.getData() ) );
					}
					else if( msg.getType() == 47 ) // End of track
					{
						System.out.println("MetaMessage: type "  + msg.getType() + " end of track " );
					}
					
				}
				else 
				{
					System.out.println("Other message: " + message.getClass());
				}
			}

			for( Integer channel : eventsByChannel.keySet() )
			{
				Map< Note, Long > notesOn = new HashMap< Note, Long >(); // Note's initial time
				Map< Integer, Note > notesOnValue = new HashMap< Integer, Note >();            
				
				for( MidiEvent event : eventsByChannel.get( channel ) )
				{
					MidiMessage message = event.getMessage();
					ShortMessage sm = (ShortMessage) message;

					if (sm.getCommand() == ShortMessage.NOTE_ON ) 
					{
						int key = sm.getData1();
						int velocity = sm.getData2();
						int c = sm.getChannel();

						ArrayTreeMap< Long, Note > notesByTime = noteChannels.get( c );
						if( notesByTime == null )
						{
							notesByTime = new ArrayTreeMap< Long, Note >();
							noteChannels.put( c, notesByTime );
						}

						if( velocity > 0 )
						{                        
							long initTime = event.getTick();

							Note note = new Note( key );
							note.setOnVelocity( (byte)velocity );

							notesOn.put( note, initTime );                        
							notesOnValue.put( key, note );

							notesByTime.put( initTime,  note );
						}
						else
						{
							long endTime = event.getTick();

							Note note = notesOnValue.get( key );
							if (note != null )
							{
								note.setOffVelocity( (byte)velocity );
							}

							long initTime = notesOn.get( note );

							notesOnValue.remove( key );
							notesOn.remove( note );
							System.out.println("testReadMidi.main() " + note);
							String duration = getNoteDuration( endTime - initTime, quarterTicks );

							note.setDuration( duration );
						}
					}
					else if (sm.getCommand() == ShortMessage.NOTE_OFF ) 
					{
						int key = sm.getData1();
						int velocity = sm.getData2();                        
						int c = sm.getChannel();
						long endTime = event.getTick();

						ArrayTreeMap< Long, Note > notesByTime = noteChannels.get( c );
						if( notesByTime != null )
						{	
							Note note = notesOnValue.get( key );
							if (note != null )
							{
								note.setOffVelocity( (byte)velocity );
							}

							long initTime = notesOn.get( note );

							notesOnValue.remove( key );
							notesOn.remove( note );

							String duration = getNoteDuration( endTime - initTime, quarterTicks );
							
							note.setDuration( duration );
						}						
					}
					else
					{
						System.out.println("testReadMidi.main() " + sm.getCommand() + " " + sm.getData1() + " - "+sm.getData2());
					}
				}
			}
		}   

		trackNumber++;

		ArrayTreeMap< Long, Note > notesByTime = noteChannels.get( 0 );
		System.out.println("testReadMidi.main() " + notesByTime );

		List< Long > times = new ArrayList<Long>();
		times.addAll( notesByTime.keySet() );
		Collections.sort( times );
		String pat = "T73 V0 ";
		for( Long t : times )
		{
			List< Note > notes = notesByTime.get( t );

			for( int i = 0; i < notes.size() - 1; i++ )
			{
				pat += notes.get( i ) + "+";
			}

			pat += notes.get( notes.size() -1 ) + " ";
		}

		Player player = new Player();
		player.play( pat );
	}
	//*/

	private static int getIntValue( byte[] bytes )
	{
		int val = 0;

		int byteSize = Byte.SIZE;

		for( byte b : bytes )
		{
			val = ( val << byteSize ) | ( b & 0xFF );
		}

		return val;
	}

	private static String getNoteDuration( long noteTickDuration, int quarterTicks )
	{
		String duration = "";

		double scale = quarterTicks / ((double) noteTickDuration );

		scale = roundDurationScale( scale, new double[] { 32, 16, 8, 4, 2, 1, 0.5, 0.25 } );

		if( scale == 1 )
		{
			duration = "q";
		}
		else if( scale == 2)
		{
			duration = "i";
		}
		else if( scale == 4 )
		{
			duration = "s";
		}
		else if( scale == 8 )
		{
			duration = "t";
		}
		else if( scale == 16 )
		{
			duration = "x";
		}
		else if( scale == 32 )
		{
			duration = "o";
		}
		else if( scale == 0.5 )
		{
			duration = "h";
		}
		else if( scale == 0.25 )
		{
			duration = "w";
		}

		return duration.toUpperCase();
	}

	private static double roundDurationScale( double scale, double[] scales  )
	{
		double durScale = 1;

		if( scales != null )
		{
			int pos = 0;
			double dif = Double.MAX_VALUE; 
			for( int i = 0; i < scales.length; i++ )
			{
				double aux = Math.abs( scale - scales[ i ] );
				if( aux < dif )
				{
					dif = aux;
					pos = i;
				}
			}

			durScale = scales[ pos ];
		}

		return durScale;
	}
}
