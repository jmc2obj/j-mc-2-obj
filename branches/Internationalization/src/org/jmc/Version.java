package org.jmc;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.jmc.util.Log;


/**
 * Version constants.
 */
public class Version
{
	public static final String VERSION = "0.2-dev";

	private static final Object syncobj=new Object();

	private static String revstr=null;
	public static String REVISION()
	{
		synchronized(syncobj)
		{
			if(revstr==null)
			{
				revstr=Version.class.getPackage().getImplementationVersion();
				if(revstr==null)
					revstr= "(local)";
			}
		}

		return revstr;
	}

	private static Date dateval=null;
	public static Date DATE()
	{
		synchronized(syncobj)
		{
			if(dateval==null)
			{
				try{
					SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd hhmm");

					InputStream stream = Version.class.getResourceAsStream("/META-INF/MANIFEST.MF");

					if(stream != null)
					{

						Manifest manifest = new Manifest(stream);            

						Attributes attributes = manifest.getMainAttributes();

						String datestr=attributes.getValue("Built-Date");

						if(datestr==null) datestr="";

						dateval=sdf.parse(datestr);
					}
					else
					{
						Log.error("Cannot load manifest", null, false);
						dateval=new Date(0);
					}

				}catch (Exception e) {
					Log.error("Cannot find date of current version",e,false);					
					dateval=new Date(0);
				}
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
