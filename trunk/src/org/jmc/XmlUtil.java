package org.jmc;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * Utility methods for dealing with XML
 */
public class XmlUtil {

	/**
	 * Loads a XML document from a file and returns the corresponding DOM Document.
	 * 
	 * @param file Input file
	 * @return Parsed document
	 * @throws Exception if the loading or parsing fails
	 */
	public static Document loadDocument(File file) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(file);
	}


	/**
	 * Gets the value of an attribute in a XML element 
	 * 
	 * @param element DOM node
	 * @param attrName Attribute name
	 * @return Attribute value, or null if not found
	 */
	public static String getAttribute(Node node, String attrName)
	{
		return getAttribute(node, attrName, null);
	}
	
	/**
	 * Gets the value of an attribute in a XML element 
	 * 
	 * @param element DOM node
	 * @param attrName Attribute name
	 * @param defaultVal Default value to return when the attribute is not found
	 * @return Attribute value, or 'defaultVal' if not found
	 */
	public static String getAttribute(Node node, String attrName, String defaultVal)
	{
		NamedNodeMap nm = node.getAttributes();
		if (nm == null)
			return defaultVal;
		
		Node attr = nm.getNamedItem(attrName);
		if (attr == null)
			return defaultVal;

		return attr.getNodeValue();
	}
	
}
