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
	private static final Object syncobj=new Object();

	private static void initialize()
	{	
		synchronized(syncobj)
		{
			if(rev!=null && dateval!=null && commit!=null) return;

			try{
				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd hhmm");

				InputStream stream = Version.class.getClassLoader().getResourceAsStream("data/version.xml");

				if(stream != null)
				{

					Document doc = Xml.loadDocument(stream);
					XPath xpath = XPathFactory.newInstance().newXPath();            

					rev=Integer.valueOf((String) xpath.evaluate("version/revision", doc, XPathConstants.STRING));
					commit=(String) xpath.evaluate("version/commit", doc, XPathConstants.STRING);
					String datestr=(String) xpath.evaluate("version/date", doc, XPathConstants.STRING);

					if(datestr==null) datestr="";

					dateval=sdf.parse(datestr);
				}
				else
				{
					dateval=new Date(0);
					commit="Unknown";
					rev=0;
				}

			} catch (Exception e) {

				Log.error("Cannot load program version 2", e, false);
				dateval=new Date(0);
				commit="Unknown";
				rev=0;

			}
		}
	}


	private static Integer rev=null;
	public static int VERSION()
	{
		initialize();
		return rev;
	}
	
	private static String commit=null;
	public static String COMMIT()
	{
		initialize();
		return commit;
	}

	private static Date dateval=null;
	public static Date DATE()
	{
		initialize();
		return dateval;
	}
}
