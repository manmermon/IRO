/**
 * 
 */
package config.language;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolTip;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import general.ArrayMap;



/**
 * @author manuel
 *
 */
public class TranslateComponents
{
	private static ArrayMap< Component, Caption > translateTable = new ArrayMap<Component, Caption>();
	
	public static void add( Component c, Caption caption )
	{
		if( c != null && caption != null )
		{
			translateTable.put( c, caption );
		}
	}

	public static void translate( String lang )
	{
		Language.changeLanguage( lang );
		
		for( Component c : translateTable.keySet() )
		{
			List< Caption > cap = translateTable.get( c );
			String translate  = "";
			
			if( !cap.isEmpty() )
			{
				translate = cap.get( 0 ).getCaption( lang );
			}
			
			if( c instanceof JPanel )
			{
				JPanel p = (JPanel)c;
				p.setBorder( BorderFactory.createTitledBorder( translate ) );
			}
			else if( c instanceof JScrollPane )
			{
				JScrollPane p = (JScrollPane)c;
				p.setBorder( BorderFactory.createTitledBorder( translate ) );
			}
			else if( c instanceof AbstractButton )
			{
				AbstractButton b = (AbstractButton)c;				
				b.setText( translate );
			}
			else if( c instanceof JLabel )
			{
				JLabel l = (JLabel)c;
				l.setText( translate );
			}
			else if( c instanceof JToolTip )
			{
				JToolTip tt = (JToolTip)c;
				tt.setToolTipText( translate );
			}
			else if( c instanceof JTableHeader )
			{
				JTableHeader th = (JTableHeader)c;
				
				TableColumnModel tcm = th.getColumnModel();
				
				for( int i = 0; i < cap.size() && i < tcm.getSelectedColumnCount(); i++ )
				{
					translate = cap.get( i ).getCaption( lang );
					
					TableColumn tc = tcm.getColumn( i );					
					tc.setHeaderValue( translate );
				}
				
				th.repaint();
			}				
		}
	}
}
