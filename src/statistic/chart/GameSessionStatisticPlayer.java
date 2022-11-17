package statistic.chart;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import general.ArrayTreeMap;

public class GameSessionStatisticPlayer 
{
	private long idSession;
	private int player;
		
	private ControllerMetadataExtender controllerInfo;
	private Double[][] controllerData;
	
	private Map< String, Number > sessionInfo;
	
	private final String SCORE = "SCORE";
	private final String MUTE = "MUTE";
	private final String SESSION_TIME_LIMIT = "SESSION_TIME_LIMIT";		
	private final String VALENCE_VALUE = "VALENCE_VALUE";
	private final String AROUSAL_VALUE = "AROUSAL_VALUE";
	private final String EMOTION_VALUE = "EMOTION_VALUE";
	private final String REACTION_TIME = "REACTION_TIME";
	private final String RECOVER_TIME = "RECOVER_TIME";
	private final String REPETITIONS = "REPETITIONS";
	
	private final String TASK_BLOCK_TIME = "TASK_BLOCK";
	private final String REST_TASK_BLOCK = "REST_TASK_BLOCK";
	
	private ArrayTreeMap< Long, String > gameEvent;
		
	public GameSessionStatisticPlayer( long id, int player )
	{
		this.idSession = id;
		this.player = player;
		
		this.sessionInfo = new HashMap< String, Number >();
		
		this.controllerInfo = new ControllerMetadataExtender();
		this.gameEvent = new ArrayTreeMap< Long, String>();		
	}
	
	public long getSessionID()
	{
		return this.idSession;
	}
	
	public int getPlayerID()
	{
		return this.player;
	}
	
	public long getStartSessionInMillis()
	{
		return this.idSession;
	}
	
	public Date getSessionDate()
	{
		return new Date( this.idSession );
	}
	
	//
	// CONTROLLER
	//****************
	
	public ControllerMetadataExtender getControllerInfo( )
	{
		return this.controllerInfo;
	}
	
	public void setControllerName( String name )
	{		
		this.controllerInfo.setName( name );
	}
	
	public void setSamplingRate( double samplingRate )
	{
		this.controllerInfo.setSamplingRate( samplingRate );
	}
	
	public void setNumberOfChannels( int nch )
	{
		this.controllerInfo.setNumberOfChannels( nch );
	}
	
	public void setSelectedChannel( int ch )
	{
		this.controllerInfo.setSelectedChannel( ch );
	}
	
	public void setRecoverLevel( double value )
	{
		this.controllerInfo.setRecoverInputLevel( value );
	}
	
	public void setActionLevel( double action )
	{
		this.controllerInfo.setActionInputLevel( action );
	}
	
	public void setTargetTimeInLevelAction( double time )
	{
		this.controllerInfo.setTargetTimeInLevelAction( time );
	}
	
	public Double[][] getControllerData()
	{
		return this.controllerData;
	}
	
	public void setSessionControllerData( Double[][] data )
	{
		this.controllerData = data;
	}
	
	//
	// SESSION INFO
	//****************
	
	public void setMuteSession( int mute )
	{
		this.sessionInfo.put( this.MUTE, mute );
	}
	
	public boolean getMuteSession( )
	{
		Number mute = this.sessionInfo.get( this.MUTE );
		
		boolean m = ( mute != null && mute.intValue() > 0 ) ? true : false;
		
		return m;
	}
	
	public void setLimitSessionTime( int limit )
	{
		this.sessionInfo.put( this.SESSION_TIME_LIMIT, limit );
	}
	
	public int getSessionTimeLimit( )
	{
		Number limit = this.sessionInfo.get( this.SESSION_TIME_LIMIT );
		
		int lim = ( limit != null ) ? limit.intValue() : 0;
		
		return lim;
	}
	
	public void setScore( int score )
	{
		this.sessionInfo.put( this.SCORE, score );
	}
	
	public int getScore( )
	{
		Number score = this.sessionInfo.get( this.SCORE );
		
		int sc = ( score != null ) ? score.intValue() : 0;
		
		return sc;
	}
	
	public void setEmotionValues( int valence, int arousal, int emotion )
	{
		this.sessionInfo.put( this.VALENCE_VALUE, valence );
		this.sessionInfo.put( this.AROUSAL_VALUE, arousal );
		this.sessionInfo.put( this.EMOTION_VALUE, emotion );
	}
			
	public int[] getEmotionValues( )
	{
		Number v = this.sessionInfo.get( this.VALENCE_VALUE );
		Number a = this.sessionInfo.get( this.AROUSAL_VALUE );
		Number e = this.sessionInfo.get( this.EMOTION_VALUE );
		
		int[] emots = new int[] { -1, -1, -1 };
		
		emots[ 0 ] = ( v != null ) ? v.intValue() : -1;
		emots[ 1 ] = ( a != null ) ? a.intValue() : -1;
		emots[ 2 ] = ( e != null ) ? e.intValue() : -1;
		
		return emots;
	}
	
	public void setReactionTime( double time )
	{
		this.sessionInfo.put( this.REACTION_TIME, time );
	}
	
	public double getReactionTime( )
	{
		Number time = this.sessionInfo.get( this.REACTION_TIME );
		
		double t = ( time != null ) ? time.doubleValue() : 0;
		
		return t;
	}
	
	public void setRecoverTime( double time )
	{
		this.sessionInfo.put( this.RECOVER_TIME, time );
	}
	
	public double getRecoverTime( )
	{
		Number time = this.sessionInfo.get( this.RECOVER_TIME );
		
		double t = ( time != null ) ? time.doubleValue() : 0;
		
		return t;
	}
	
	public void setRepetitions( int rep )
	{
		this.sessionInfo.put( this.REPETITIONS, rep );
	}
	
	public int getRepetitions( )
	{
		Number rep = this.sessionInfo.get( this.REPETITIONS );
		
		int r = ( rep != null ) ? rep.intValue() : 0;
		
		return r;
	}
	
	
	public void setTaskBlockTime( int taskBlockTime )
	{
		this.sessionInfo.put( this.TASK_BLOCK_TIME, taskBlockTime );
	}
	
	public int getTaskBlockTime( )
	{
		Number time = this.sessionInfo.get( this.TASK_BLOCK_TIME );
		
		int r = ( time != null ) ? time.intValue() : 0;
		
		return r;
	}
	
	public void setRestBlockTime( int time )
	{
		this.sessionInfo.put( this.REST_TASK_BLOCK, time );
	}
	
	public int getRestBlockTime( )
	{
		Number time = this.sessionInfo.get( this.REST_TASK_BLOCK );
		
		int r = ( time != null ) ? time.intValue() : 0;
		
		return r;
	}
	
	//
	// GAME EVENTS
	//****************
	
	public void addGameEvent( long time, String event )
	{
		this.gameEvent.put( time, event );
	}
	
	public ArrayTreeMap< Long, String > getGameEvent( )
	{
		return this.gameEvent;
	}
	
}
