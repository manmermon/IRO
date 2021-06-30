/*
 * JFugue, an Application Programming Interface (API) for Music Programming
 * http://www.jfugue.org
 *
 * Copyright (C) 2003-2014 David Koelle
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package music.jfugueIRO;

import org.jfugue.player.ManagedPlayer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;

import org.jfugue.midi.MidiParserListener;
import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.PatternProducer;
import org.staccato.StaccatoParser;

/**
 * This is a player that is optimized for defining and playing music in a program. 
 * It specifically parses music with a StaccatoParser and converts the music to
 * MIDI using a MidiParserListener.
 * 
 * This Player uses a ManagedPlayer but does not expose any of the ManagedPlayer's 
 * ability to be managed. 
 */
public class PlayerMod  
{
	private StaccatoParser staccatoParser;
	private MidiParserListener midiParserListener;
	private ManagedPlayerMod managedPlayer;
	
	private boolean emptySeq = true;
	
	public PlayerMod() 
	{
		managedPlayer = new ManagedPlayerMod();
		staccatoParser = new StaccatoParser();
		midiParserListener = new MidiParserListener();
		staccatoParser.addParserListener( midiParserListener );
	}
	
	public Sequence getSequence(PatternProducer... patternProducers) 
	{
	    return getSequence(new Pattern(patternProducers));
	}

	public Sequence getSequence(PatternProducer patternProducer) 
	{
	    return getSequence(patternProducer.getPattern().toString());
	}

	public Sequence getSequence(String... strings) 
	{
		return getSequence(new Pattern(strings));
	}

	public Sequence getSequence(String string) 
	{
		staccatoParser.parse(string);
		return midiParserListener.getSequence();
	}
	
	public void load(PatternProducer... patternProducers) throws MidiUnavailableException, InvalidMidiDataException 
	{
		load(new Pattern(patternProducers));
	}
	
	public void load(PatternProducer patternProducer) throws MidiUnavailableException, InvalidMidiDataException
	{
		load(patternProducer.getPattern().toString());
	}
	
	public void load(String... strings) throws MidiUnavailableException, InvalidMidiDataException 
	{
		load( new Pattern( strings ) );
	}

	public void load(String string) throws MidiUnavailableException, InvalidMidiDataException 
	{
		load( getSequence( string ) );
	}
	
	public void load( Sequence sequence ) throws MidiUnavailableException, InvalidMidiDataException
	{
		this.emptySeq = false;
		managedPlayer.loadSequence( sequence );		
	}
	
	public boolean isEmpty()
	{
		return this.emptySeq;
	}
	
	public void play() 
	{
		try
		{
			managedPlayer.start();
		} 
		catch (IllegalStateException e) 
		{
			throw new RuntimeException(e);
		}
	}
	
	public void delayPlay(final long millisToDelay ) 
	{
		Thread thread = new Thread() 
		{
			public void run() 
			{
				try 
				{
					Thread.sleep( millisToDelay );
				} 
				catch (InterruptedException e) 
				{
				}
				
				PlayerMod.this.play( );
			}
		};
		thread.setName( this.getClass().getSimpleName() + "-" + thread.getId() );		
		thread.start();
	}
	
	/**
	 * Returns the ManagedPlayer behind this Player. You can start, pause, stop, resume, and seek a ManagedPlayer.
	 * @see ManagedPlayer
	 */
	public ManagedPlayerMod getManagedPlayer() 
	{
		return this.managedPlayer;
	}
	
	/**
	 * Returns the StaccatoParser used by this Player. The only thing you might want to do with this is set whether the parser
	 * throws an exception if an unknown token is found.
	 * @see StaccatoParser
	 */
	public StaccatoParser getStaccatoParser()
	{		
		return this.staccatoParser;
	}

	/**
	 * Returns the MidiParserListener used by this Player. 
	 * @see MidiParserListener
	 */
	public MidiParserListener getMidiParserListener() 
	{ 
		return this.midiParserListener;
	}
}
