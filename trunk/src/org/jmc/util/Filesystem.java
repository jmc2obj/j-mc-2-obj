package org.jmc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * File and directory related methods.
 */
public class Filesystem
{

	/**
	 * Gets the directory that Minecraft keeps its save files in.
	 * It works on all systems that Minecraft 1.2 works in.
	 * @return path to the Minecraft dir
	 */
	public static File getMinecraftDir()
	{
		String minecraft="minecraft";
		String osname = System.getProperty("os.name").toLowerCase();
		String default_home = System.getProperty("user.home", ".");

		if(osname.contains("solaris") || osname.contains("sunos") || osname.contains("linux") || osname.contains("unix"))
		{
			return new File(default_home, "." + minecraft);
		}

		if(osname.contains("win"))
		{
			String win_home = System.getenv("APPDATA");
			if(win_home != null)
				return new File(win_home, "." + minecraft);
			else
				return new File(default_home, "." + minecraft);
		}

		if(osname.contains("mac"))
		{
			return new File(default_home, "Library/Application Support/" + minecraft);
		}

		return null;
	}


	/**
	 * Gets the directory where the program's data files are.
	 * 
	 * @return
	 */
	public static File getDatafilesDir()
	{
		//This checks if we are running from within an EXE file created using JSmooth
		Object val=System.getProperties().get("jsmooth");
		if(val!=null)
		{
			return new File(".");
		}
		
		
		try {
			String codePath = URLDecoder.decode(
					Filesystem.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
					"UTF-8");

			// ASSUMPTION:
			// - If the code is in a .jar file, then the data files are in the same directory as 
			//   the .jar file
			// - If not, the data files are assumed to be in the parent directory. This is intended
			//   for running the program directly from the bin/ directory in the source distribution
			//   (for example, when running inside an IDE).

			// ... in practice both cases mean returning the parent path
			return new File(codePath).getParentFile();

		} catch (UnsupportedEncodingException ex) {
			// should never happen, UTF-8 encoding always exists
			throw new RuntimeException(ex);
		}
	}


	/**
	 * Convenience function to copy a file.
	 * If the destination file exists it is overwritten.
	 * 
	 * @param origPath Original path
	 * @param destPath Destination path
	 * @throws FileNotFoundException if either file exists but is a directory rather 
	 * than a regular file, does not exist but cannot be read/created, or cannot be 
	 * opened for any other reason.
	 * @throws IOException If the operation fails during the data copy phase.
	 */
	public static void copyFile(File origPath, File destPath) throws IOException
	{
		FileChannel in = null;
		FileChannel out = null;
		try {
			in = new FileInputStream(origPath).getChannel();
			out = new FileOutputStream(destPath).getChannel();
			in.transferTo(0, in.size(), out);
		}
		finally {
			if (in != null) in.close();
			if (out != null) out.close();
		}
	}

	/**
	 *  Convenience function to move a file.
	 * 
	 * @param origPath
	 * @param destPath
	 * @throws IOException
	 */
	public static void moveFile(File origPath, File destPath) throws IOException
	{
		copyFile(origPath, destPath);
		if(!origPath.delete())
			throw new IOException("Cannot delete file: "+origPath.getName());
	}

	/**
	 * Calculates the hash of an InputStream (for example file).
	 * @param is
	 * @return hash as a string of hex characters
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static String getHash(InputStream is) throws IOException, NoSuchAlgorithmException
	{

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte buffer[]=new byte[1024];
		int ret;
		while(true)
		{
			ret=is.read(buffer);
			if(ret<0) break;
			md.update(buffer, 0, ret);
		}
		is.close();

		byte[] digest = md.digest();

		char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		char[] hexChars = new char[digest.length*2];
		int v;
		for (int i = 0; i < digest.length; i++ ) 
		{
			v = digest[i] & 0xFF;
			hexChars[2*i] = hexArray[v>>4];
			hexChars[2*i+1] = hexArray[v&0x0f];
		}
		return new String(hexChars, 0, digest.length*2); 
	}
}



