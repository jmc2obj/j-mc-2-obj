package org.jmc;

import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jmc.util.Log;
import org.jmc.util.Xml;
import org.w3c.dom.Document;

public class CheckUpdate {

	public static boolean isAvailable()
	{
		try{
			
			URL updateLink=new URL("http://jmc2obj.net/update.xml");
			Document doc = Xml.loadDocument(updateLink);
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			String datestr = (String)xpath.evaluate("/update/date", doc, XPathConstants.STRING);
			
			DateFormat df=DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
			Date newdate=df.parse(datestr);
			
			Date currdate=Version.DATE();
			
			return newdate.after(currdate);
			
		}catch (Exception e) {
			Log.error("Cannot check update ", e);
			return false;
		}
	}
	
	public static Date getDate()
	{
		try{
			
			URL updateLink=new URL("http://jmc2obj.net/update.xml");
			Document doc = Xml.loadDocument(updateLink);
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			String datestr = (String)xpath.evaluate("/update/date", doc, XPathConstants.STRING);
			
			DateFormat df=DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
			Date newdate=df.parse(datestr);
			
			return newdate;
			
		}catch (Exception e) {
			Log.error("Cannot check update ", e);
			return new Date(0);
		}
	}
	
	public static String getUrl()
	{
		try{
			URL updateLink=new URL("http://jmc2obj.net/update.xml");
			Document doc = Xml.loadDocument(updateLink);
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			String url = (String)xpath.evaluate("/update/url", doc, XPathConstants.STRING);
			
			return url;
			
		}catch (Exception e) {
			Log.error("Cannot check update ", e);
			return "";
		}
	}
	
}
