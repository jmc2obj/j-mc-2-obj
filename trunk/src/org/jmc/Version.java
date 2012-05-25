package org.jmc;


/**
 * Version constants.
 */
public class Version
{
	public static final String VERSION = "0.15-dev";
	public static final String REVISION = "r208";
	
	public static int compareRevisions(String a, String b)
	{
		if(a.isEmpty() || !a.matches("r[0-9]+")) a="r0";
		if(b.isEmpty() || !b.matches("r[0-9]+")) b="r0";
		
		int arev=Integer.parseInt(a.substring(1));
		int brev=Integer.parseInt(b.substring(1));
		
		return arev-brev;
	}
}
