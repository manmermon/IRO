package testing;

import java.util.ArrayList;
import java.util.List;

import general.ArrayTreeMap;

public class testArrayTreeMap {

	public static void main(String[] args) 
	{
		ArrayTreeMap< Integer, Integer > h = new ArrayTreeMap< Integer, Integer>();
		
		System.out.println("test.main() empty " + h.isEmpty() );
		
		h.put( 0, 3 );
		h.put( 0,  2 );
		h.put( 1,  1 );
		h.put( 1,  1 );
		h.put( 0,  1 );
		h.put( 2,  1 );
		h.put( 2,  0 );
		
		System.out.println("test.main() empty " + h.isEmpty() );
		
		System.out.println("test.main() h = " + h);
		
		System.out.println("test.main() containkey = " + h.containsKey( 0 ) );
		System.out.println("test.main() containkey = " + h.containsKey( 1 ) );
		System.out.println("test.main() containkey = " + h.containsKey( 2 ) );
		System.out.println("test.main() containkey = " + h.containsKey( 3 ) );
		System.out.println("test.main() containkey = " + h.containsKey( 4 ) );
		
		System.out.println("test.main() containValue = " + h.containsValue( 0 ) );
		System.out.println("test.main() containValue = " + h.containsValue( 1 ) );
		System.out.println("test.main() containValue = " + h.containsValue( 2 ) );
		System.out.println("test.main() containValue = " + h.containsValue( 3 ) );
		System.out.println("test.main() containValue = " + h.containsValue( 4 ) );
		
		
		System.out.println("test.main() get " + h.get( 1 ));
		System.out.println("test.main() get " + h.get( 0 ));
		System.out.println("test.main() get " + h.get( 2 ));
		System.out.println("test.main() get " + h.get( 3 ));
		
		List< Integer > l = new ArrayList< Integer >();
		l.add( 5 ); l.add( 7 );
		h.put( 5, l );		
		h.put( 2, l );
		System.out.println("test.main() h = " + h);
		
		ArrayTreeMap< Integer, Integer > h2 = new ArrayTreeMap< Integer, Integer>();
		h2.putAll( h );
		System.out.println("test.main() h2 putAll = " + h2 );
		
		h.put( 7, 8 );
		h2.put( 9, 9 );
		System.out.println("test.main() h = " +h);
		System.out.println("test.main() h2 = " +h2);
		
		h.put( 0, 15 );
		System.out.println("test.main() h = " +h);
		System.out.println("test.main() h2 = " +h2);
		
		System.out.println("test.main() size h = " + h.size() );
		
		System.out.println("test.main() keyset h = "+  h.keySet() );
		System.out.println("test.main() values h = "+  h.values() );
		
		h.remove( 0 );
		System.out.println("test.main() h = " +h);
		System.out.println("test.main() h2 = " +h2);
		
		System.out.println("test.main() remove contain more " + h.removeValue( 1, 1 ) );
		System.out.println("test.main() h = " +h);
		System.out.println("test.main() h2 = " +h2);
		
	}

}
