package gui.panel.samSurvey;

public class EmotionParameter 
{
	public enum Emotion { SADNESS, SURPIRSE, ANGER, DISGUST, FEAR, HAPPINESS, NEUTRAL }			
	
	public static final String DEFAULT_TEXT_SADNESS = "Sadness";
	public static final String DEFAULT_TEXT_SURPRISE = "Surprise";
	public static final String DEFAULT_TEXT_ANGER = "Anger";
	public static final String DEFAULT_TEXT_DISGUST = "Disgust";
	public static final String DEFAULT_TEXT_FEAR = "Fear";
	public static final String DEFAULT_TEXT_HAPPINESS = "Happiness";
	public static final String DEFAULT_TEXT_NEUTRAL = "Neutral";			
	
	
	private boolean select;
	private Emotion type;
	private String text;
	
	public EmotionParameter( Emotion type ) 
	{		
		String t = "";
		
		switch ( type ) 
		{
			case SADNESS:
			{
				t = DEFAULT_TEXT_SADNESS;
				break;
			}
			case SURPIRSE:
			{
				t = DEFAULT_TEXT_SURPRISE;
				break;
			}
			case ANGER:
			{
				t = DEFAULT_TEXT_ANGER;
				break;
			}
			case DISGUST:
			{
				t = DEFAULT_TEXT_DISGUST;
				break;
			}
			case FEAR:
			{
				t = DEFAULT_TEXT_FEAR;
				break;
			}
			case HAPPINESS:
			{
				t = DEFAULT_TEXT_HAPPINESS;
				break;
			}
			case NEUTRAL:
			{
				t = DEFAULT_TEXT_NEUTRAL;
				break;
			}
		}
		
		this.type = type;
		this.text = t;
		this.select = true;
	}
	
	public EmotionParameter( Emotion type, boolean sel )
	{
		this( type );
		this.select = sel;
	}
	
	public EmotionParameter( Emotion type, String text, boolean sel ) 
	{
		this.type = type;
		this.text = text;
		this.select = sel;
	}
	
	public Emotion getType()
	{
		return this.type;
	}
	
	public boolean isSelect()
	{
		return this.select;
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public void setType( Emotion type )
	{
		this.type = type;
	}
	
	public void setSelect( boolean sel )
	{
		this.select = sel;
	}
	
	public void setText( String text )
	{
		this.text = text;
	}
	
	@Override
	public String toString() 
	{
		return "<" + this.type + "," + this.text + "," + this.select + ">";
	}
}
