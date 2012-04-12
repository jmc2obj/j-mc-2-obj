package org.jmc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.CodeSource;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jmc.util.Log;

/**
 * Class used for exporting configuration resources from within JAR file
 * 
 * @author danijel
 *
 */
public class Configuration
{
	/**
	 * Looks for resources in the "conf" subdirectory and extracts them if necessary.
	 * @throws RuntimeException
	 */
	public static void initialize() throws RuntimeException
	{
		try {
			List<ZipEntry> files=createResourceList("conf/");
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
				
				checkCreateFile(f);
			}

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
	 * @throws IOException
	 */
	private static void checkCreateFile(ZipEntry entry) throws IOException
	{		
		String path=entry.getName();
		File dest=new File(path);
		if(dest.exists())
		{
			Log.info("File "+path+" exists...");
			return;
		}
		
		if(!dest.createNewFile())
		{
			throw new IOException("Cannot create file: "+path);
		}

		Log.info("File "+path+" doesn't exist! Recreating...");

		InputStream src=Configuration.class.getClassLoader().getResourceAsStream(path);		
		ReadableByteChannel input=Channels.newChannel(src);
		
		FileChannel output=new FileOutputStream(dest).getChannel();
		
		output.transferFrom(input, 0, (int)entry.getSize());
	}
}
