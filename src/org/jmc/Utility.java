package org.jmc;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * Misc. utility methods.
 */
public class Utility {

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
			{
				return new File(win_home, "." + minecraft);
			} else
			{
				return new File(default_home, "." + minecraft);
			}
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
					Utility.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
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
	
}
