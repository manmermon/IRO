package testing.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jfugue.midi.MidiDictionary;

public class testInstruments 
{
	public static void main(String[] args) 
	{
		List< String > INST = new ArrayList<String>( MidiDictionary.INSTRUMENT_STRING_TO_BYTE.keySet() );		
		Collections.sort( INST );
		
		for( String inst : INST )
		{
			System.out.println("testInstruments.main() " + inst + " byte = " + MidiDictionary.INSTRUMENT_STRING_TO_BYTE.get( inst ) );
		}		
	}
}
