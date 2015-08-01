package org.jmc;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jmc.util.Log;
import org.jmc.util.Xml;
import org.w3c.dom.Document;


/**
 * Version constants.
 */
public class Version
{
	public static final String VERSION = "0.2-dev";

	private static final Object syncobj=new Object();

	private static void initialize()
	{	
		synchronized(syncobj)
		{
			if(revstr!=null && dateval!=null) return;

			try{
				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd hhmm");

				InputStream stream = Version.class.getClassLoader().getResourceAsStream("data/version.xml");

				if(stream != null)
				{

					Document doc = Xml.loadDocument(stream);
					XPath xpath = XPathFactory.newInstance().newXPath();            

					revstr=(String) xpath.evaluate("version/revision", doc, XPathConstants.STRING);
					String datestr=(String) xpath.evaluate("version/date", doc, XPathConstants.STRING);

					if(datestr==null) datestr="";

					dateval=sdf.parse(datestr);
				}
				else
				{
					dateval=new Date(0);
					revstr="r0";
				}

			} catch (Exception e) {

				Log.error("Cannot load program version 2", e, false);
				dateval=new Date(0);
				revstr="r0";

			}
		}
	}


	private static String revstr=null;
	public static String REVISION()
	{
		initialize();
		return revstr;
	}

	private static Date dateval=null;
	public static Date DATE()
	{
		initialize();
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
