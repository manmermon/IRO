package control.inputStream.biosignal;

import javax.swing.JOptionPane;

import biosignal.Biosignal;
import config.IOwner;
import config.language.Language;
import control.events.IEnabledInputLSLDataListener;
import general.StringTuple;
import gui.GameManager;
import gui.MainAppUI;
import gui.game.component.IPossessable;
import lslInput.LSLStreamInfo;
import lslInput.LSLUtils;
import lslInput.LSLStreamInfo.StreamType;
import lslInput.event.InputLSLDataReader;
import lslInput.stream.IInputStreamMetadata;
import lslInput.stream.IInputStreamMetadata.InputSourceType;
import lslInput.event.InputLSLDataEvent.LSLDataEventType;
import statistic.RegistrarStatistic;
import statistic.RegistrarStatistic.GameFieldType;
import stoppableThread.IStoppable;

public class LSLInputBiosignalStreamReader extends InputLSLDataReader implements IPossessable, IEnabledInputLSLDataListener
{
	private IOwner owner = null;
	private int ownerID = IOwner.ANONYMOUS;

	private IInputStreamMetadata metadata; 
	
	private Biosignal.Type bioType = null;
	
	private boolean enable = true;
	
	public LSLInputBiosignalStreamReader( IInputStreamMetadata inDataInfo ) throws Exception
	{
		if( inDataInfo == null )
		{
			throw new IllegalArgumentException( "Metadata null." );
		}
		
		this.metadata = inDataInfo;
		
		String contentType = "";
		
		if( inDataInfo.getInputSourceType() == InputSourceType.LSLSTREAM )
		{
			contentType = ((LSLStreamInfo)this.metadata.getInputSourseSetting()).content_type(); 
		}
		
		this.bioType = Biosignal.getBiosignalType( contentType);

		this.setThreadName();
	}
		
	@Override
	public void close() 
	{
		super.stopActing( IStoppable.FORCE_STOP );		
	}

	@Override
	protected void readInputData(lslInput.event.InputLSLDataEvent ev) throws Exception 
	{
		if( !ev.getType().equals( LSLDataEventType.DATA ) )
		{	
			RegistrarStatistic.addGameData( this.ownerID, GameFieldType.ERROR_CONTROLLER_DISCONNECTED );
			
			GameManager.getInstance().stopLevel( false );
			JOptionPane.showMessageDialog( MainAppUI.getInstance(), "Controller error: Player " + this.owner + " disconnected"
											, Language.getLocalCaption( Language.ERROR )
											, JOptionPane.ERROR_MESSAGE
											, null );
		}
		else
		{
			if( this.enable )
			{
				double[] values = ev.getInputValues();
				
				double time = ev.getTime();
				
				double[] vt = new double[ values.length + 1 ];
				System.arraycopy( values, 0, vt, 0, values.length );
				vt[ vt.length -1 ] = time;
				
				RegistrarStatistic.addBiosignalData( this.ownerID, this.metadata, vt );
			}
		}
	}

	@Override
	protected void preStopThread(int friendliness) throws Exception 
	{	
	}

	@Override
	protected void postStopThread(int friendliness) throws Exception 
	{	
	}

	/*(non-Javadoc)
	 * @see @see GUI.game.component.IPossessable#setOwner(config.IOwner)
	 */
	@Override
	public void setOwner(IOwner owner)
	{
		this.owner = owner;
		if( owner != null )
		{
			this.ownerID = this.owner.getId();
		}
		
		this.setThreadName();
	}

	/*(non-Javadoc)
	 * @see @see GUI.game.component.IPossessable#getOwner()
	 */
	@Override
	public IOwner getOwner()
	{
		return this.owner;
	}
	
	private void setThreadName()
 	{
 		super.setName( this.getClass().getSimpleName() + "-" + this.ownerID );
 	}

	@Override
	public void setEnableInputStream(boolean enable) 
	{
		this.enable = enable;
	}

}
