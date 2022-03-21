package testing.other;

public class testByte 
{
	public static void main(String[] args) 
	{
		short b = 0b1000_0000;
		System.out.println("testByte.main() " + b);
		System.out.println("testByte.main() " + (byte)(b & 0x00FF));	
	}
}
