package org.jmc;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;


/**
 * Version constants.
 */
public class Version
{
	public static final String VERSION = "0.2-dev";

	private static String revstr=null;
	public static String REVISION()
	{
		if(revstr==null)
		{
			revstr=Version.class.getPackage().getImplementationVersion();
			if(revstr==null)
				revstr= "(local)";
		}
		return revstr;

	}

	private static Date dateval=null;
	public static Date DATE()
	{
		if(dateval==null)
		{
			try{
				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd hhmm");

				InputStream stream = Version.class.getResourceAsStream("/META-INF/MANIFEST.MF");				
				
				Manifest manifest = new Manifest(stream);            

				Attributes attributes = manifest.getMainAttributes();

				String datestr=attributes.getValue("Built-Date");

				if(datestr==null) datestr="";

				dateval=sdf.parse(datestr);

			}catch (Exception e) {
				dateval=new Date(0);
			}
		}

		return dateval;
	}

	public static int compareRevisions(String a, String b)
	{
		if(a.isEmpty() || !a.matches("r[0-9]+")) a="r0";
		if(b.isEmpty() || !b.matches("r[0-9]+")) b="r0";

		int arev=Integer.parseInt(a.substring(1));
		int brev=Integer.parseInt(b.substring(1));

		return arev-brev;
	}
}
