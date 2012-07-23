package org.jmc.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * Utility methods for dealing with XML
 */
public class Xml {

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
	 * Loads a XML document from a URL and returns the corresponding DOM Document.
	 * 
	 * @param file Input file
	 * @return Parsed document
	 * @throws Exception if the loading or parsing fails
	 */
	public static Document loadDocument(URL url) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(url.openStream());
	}
	
	/**
	 * Loads a XML document from a stream and returns the corresponding DOM Document.
	 * 
	 * @param stream Input stream
	 * @return Parsed document
	 * @throws Exception if the loading or parsing fails
	 */
	public static Document loadDocument(InputStream stream) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(stream);
	}
	
	
	/**
	 * Creates a new document used for saving XML files.
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static Document newDocument() throws ParserConfigurationException
	{
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		docBuilder = dbfac.newDocumentBuilder();
		return docBuilder.newDocument();
	}
	
	public static void saveDocument(Document doc, File file) throws TransformerException
	{
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans;
		trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.STANDALONE, "yes");
		trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		Source source = new DOMSource(doc);
		Result result = new StreamResult(file);
		trans.transform(source, result);
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
