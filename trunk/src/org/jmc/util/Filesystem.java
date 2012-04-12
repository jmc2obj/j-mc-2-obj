package org.jmc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;


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

}
