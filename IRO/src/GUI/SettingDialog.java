package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class SettingDialog extends JDialog {

	private JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public SettingDialog( Window owner ) 
	{
		super( owner );
		
		setBounds(100, 100, 450, 300);
		
		super.getContentPane().setLayout( new BorderLayout() );
		super.getContentPane().add( this.getMainPanel(), BorderLayout.CENTER );
	}
	
	private JPanel getMainPanel()
	{
		if( this.contentPanel == null )		
		{
			this.contentPanel = new JPanel();
			this.contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
			contentPanel.setLayout(new BorderLayout(0, 0));
		}
		
		return this.contentPanel;
	}
	

}
