package org.jmc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.CodeSource;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jmc.util.Filesystem;
import org.jmc.util.Log;
import org.jmc.util.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class used for exporting configuration resources from within JAR file
 * 
 * @author danijel
 *
 */
public class Configuration
{
	private static String HASH_FILE = "conf/hash";

	/**
	 * Looks for resources in the "conf" subdirectory and extracts them if necessary.
	 * @throws RuntimeException
	 */
	public static void initialize() throws RuntimeException
	{
		try {
			List<ZipEntry> files=createResourceList("conf/");
			boolean hasheschanged=false;
			
			if(files.isEmpty()) return;

			Map<String,String> newhashmap=new HashMap<String, String>();
			Map<String,String> localhashmap=new HashMap<String, String>();
			
			File hashfile = new File(Filesystem.getDatafilesDir(), HASH_FILE);
			if(!hashfile.canRead())
			{
				Log.info("WARNING: cannot find the hash file! I will create backups of any existing configuration files.\n"+
						"You can delete them if you didn't make any changes to them.");
			}
			else
			{
				readHashFile(hashfile, localhashmap);				
			}

			for(ZipEntry f:files)
			{
				String name=f.getName();

				if(name.endsWith("/"))
				{
					File dir=new File(name);
					if(!dir.exists() || !dir.isDirectory())
					{
						if(dir.exists()) throw new RuntimeException("Cannot create directory: "+dir+"! File is in the way!");
						if(!dir.mkdir()) throw new RuntimeException("Cannot create directory: "+dir+"!");
					}
					continue;					
				}

				String path=f.getName();
				InputStream istr=Configuration.class.getClassLoader().getResourceAsStream(path);
				String newhash=Filesystem.getHash(istr);
				newhashmap.put(path, newhash);
				
				String localhash=localhashmap.get(name);
				
				checkFile(f,localhash,newhash);
				
				if(localhash==null || !localhash.equals(newhash))
					hasheschanged=true;
			}
			
			if(hasheschanged)
				saveHashFile(HASH_FILE, newhashmap);


		} catch (Exception e) {			
			Log.error("Error checking configuration files", e);
		}
	}

	/**
	 * Creates a list of resources.
	 * @param root string that has to appear in the beginning of the resource path
	 * @return list of resources
	 * @throws IOException
	 */
	private static List<ZipEntry> createResourceList(String root) throws IOException
	{
		CodeSource src = Configuration.class.getProtectionDomain().getCodeSource();		
		List<ZipEntry> ret=new LinkedList<ZipEntry>();
		String name;

		if( src != null ) 
		{			
			ZipInputStream zip = new ZipInputStream(src.getLocation().openStream());
			ZipEntry ze = null;
			while( ( ze = zip.getNextEntry() ) != null ) 
			{
				name=ze.getName();
				if(name.startsWith(root))
				{
					ret.add(ze);
				}
			}
		}

		return ret;
	}

	
	/** 
	 * Checks if file exists and copies it from the JAR if it doesn't.
	 * @param entry ZipEntry (containing name and size) of the file that needs to be checked
	 * @param disthash hash of the file from the distribution
	 * @param newhash hash of the file in the archive
	 * @throws IOException
	 */
	private static void checkFile(ZipEntry entry, String disthash, String newhash) throws IOException
	{		
		String path=entry.getName();
		File dest=new File(path);
		if(dest.exists())
		{	
			if(!newhash.equals(disthash))
			{
				String localhash="";
				try {
					localhash = Filesystem.getHash(new FileInputStream(dest));
				} catch (NoSuchAlgorithmException e) {
					Log.info("ERROR: Cannot calculate local file hash: "+e.getMessage());
				}

				if(!localhash.equals(disthash))
				{
					File backup=new File(dest.getAbsolutePath()+".bak");
					Log.info("File "+path+" is modified! I will create a backup to "+backup.getAbsolutePath()+" and recreate the new file.");
					Filesystem.moveFile(dest,backup);
					createFile(dest,path,(int)entry.getSize());
				}
				else
				{
					Log.info("Upgrading file: "+path);
					createFile(dest,path,(int)entry.getSize());
				}
			}

		}
		else
		{
			Log.info("File "+path+" doesn't exist! Recreating...");
			createFile(dest,path,(int)entry.getSize());
		}

	}


	/**
	 * Extracts the file from the archive and saves it to the given location
	 * @param dest destination location
	 * @param path path in the archive
	 * @param size size of the file
	 * @throws IOException
	 */
	private static void createFile(File dest, String path, int size) throws IOException
	{
		InputStream src = Configuration.class.getClassLoader().getResourceAsStream(path);		
		ReadableByteChannel input = Channels.newChannel(src);

		FileOutputStream output = new FileOutputStream(dest);
		output.getChannel().transferFrom(input, 0, size);
		output.close();
	}


	/**
	 * Reads file hash.
	 * @param file
	 * @param map map of hashes: filepath -> hash
	 * @return revision of the hashfile
	 */
	private static String readHashFile(File hashfile, Map<String,String> map)
	{		
		try{
			Document doc = Xml.loadDocument(hashfile);
			XPath xpath = XPathFactory.newInstance().newXPath();

			String rev=(String) xpath.evaluate("/hashfile/revision", doc, XPathConstants.STRING);

			NodeList fileNodes = (NodeList)xpath.evaluate("/hashfile/files/file", doc, XPathConstants.NODESET);
			for (int i = 0; i < fileNodes.getLength(); i++)
			{
				Node fileNode = fileNodes.item(i);

				String path=(String) xpath.evaluate("path", fileNode, XPathConstants.STRING);
				String hash=(String) xpath.evaluate("hash", fileNode, XPathConstants.STRING);

				map.put(path, hash);
			}

			return rev;
		} catch (Exception e) {
			Log.error("Cannot read hash file", e);
			return "";
		}
	}

	private static void saveHashFile(String file, Map<String,String> hashes)
	{
		try {		
			Document doc=Xml.newDocument();

			Element eRoot=doc.createElement("hashfile");
			doc.appendChild(eRoot);

			Element eRevision=doc.createElement("revision");
			eRevision.setTextContent(Version.REVISION());
			eRoot.appendChild(eRevision);

			Element eFiles=doc.createElement("files");
			eRoot.appendChild(eFiles);

			for(Entry<String,String> fh:hashes.entrySet())
			{
				Element eFile=doc.createElement("file");
				Element ePath=doc.createElement("path");
				ePath.setTextContent(fh.getKey());
				Element eHash=doc.createElement("hash");
				eHash.setTextContent(fh.getValue());
				eFile.appendChild(ePath);
				eFile.appendChild(eHash);
				eFiles.appendChild(eFile);				
			}


			Xml.saveDocument(doc, new File(file));

		} catch (Exception e) {
			Log.error("Cannot save hash file", e);
			return;
		}
	}
}
