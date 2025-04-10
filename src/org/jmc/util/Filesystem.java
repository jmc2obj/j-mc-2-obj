package org.jmc.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.CheckForNull;

/**
 * File and directory related methods.
 */
public class Filesystem {
	/**
	 * Compare two version numbers
	 * 
	 * @return =0 if they are equal, >0 if v1>v2 and <0 if v1<v2
	 */
	private static int compareVersions(String v1, String v2) {
		String[] parts1 = v1.split("\\.");
		String[] parts2 = v2.split("\\.");

		int len = Math.max(parts1.length, parts2.length);
		for (int i = 0; i < len; i++) {
			int p1 = 0;
			int p2 = 0;
			try {
				p1 = Integer.parseInt(parts1[i], 10);
			} catch (Exception ex) {
			}
			try {
				p2 = Integer.parseInt(parts2[i], 10);
			} catch (Exception ex) {
			}
			int comp = p1 - p2;
			if (comp != 0)
				return comp;
		}
		return 0;
	}

	/**
	 * Gets the directory that Minecraft keeps its save files in. It works on
	 * all systems that Minecraft 1.2 works in.
	 * 
	 * @return path to the Minecraft dir
	 */
	public static File getMinecraftDir() {
		String minecraft = "minecraft";
		String osname = System.getProperty("os.name").toLowerCase();
		String default_home = System.getProperty("user.home", ".");

		if (osname.contains("solaris") || osname.contains("sunos") || osname.contains("linux")
				|| osname.contains("unix")) {
			File mcDir = new File(default_home, "." + minecraft);
			if (mcDir.isDirectory()) {
				return mcDir;
			} else {
				return new File(default_home, ".var/app/com.mojang.Minecraft/." + minecraft);
			}
		}

		if (osname.contains("win")) {
			String win_home = System.getenv("APPDATA");
			if (win_home != null)
				return new File(win_home, "." + minecraft);
			else
				return new File(default_home, "." + minecraft);
		}

		if (osname.contains("mac")) {
			return new File(default_home, "Library/Application Support/" + minecraft);
		}

		return null;
	}

	/**
	 * Gets the location of the Minecraft .jar file.
	 * 
	 * Prior to version 1.6 the file is always "bin/minecraft.jar"
	 * 
	 * In version 1.6 and later there can be multiple versions of the game,
	 * which will be in "versions/x.x.x/x.x.x.jar", where x.x.x is the Minecraft
	 * version. When there are multiple versions installed this function tries
	 * to return the highest one.
	 * 
	 * @return Path to the Minecraft .jar file.
	 */
	@CheckForNull
	public static File getMinecraftJar() {
		File mcdir = getMinecraftDir();
		File versdir = new File(mcdir, "versions");

		if (versdir.isDirectory()) {
			File best = null;
			for (File entry : versdir.listFiles()) {
				if (entry.isDirectory() && new File(entry, entry.getName() + ".jar").exists()) {
					if (best == null || compareVersions(entry.getName(), best.getName()) > 0)
						best = entry;
				}
			}
			if (best != null) {
				return new File(best, best.getName() + ".jar");
			}
		}
		
		File jar = new File(mcdir, "bin/minecraft.jar");
		
		if (!jar.exists()) {
			return null;
		}

		return jar;
	}

	/**
	 * Gets the directory where the program's data files are.
	 * 
	 * @return
	 */
	public static File getDatafilesDir() {
		// This checks if we are running from within an EXE file created using
		// JSmooth
		Object val = System.getProperties().get("jsmooth");
		if (val != null) {
			return new File(".");
		}

		try {
			String codePath = URLDecoder.decode(Filesystem.class.getProtectionDomain().getCodeSource().getLocation()
					.getPath(), "UTF-8");

			// ASSUMPTION:
			// - If the code is in a .jar file, then the data files are in the
			// same directory as
			// the .jar file
			// - If not, the data files are assumed to be in the parent
			// directory. This is intended
			// for running the program directly from the bin/ directory in the
			// source distribution
			// (for example, when running inside an IDE).

			// ... in practice both cases mean returning the parent path
			return new File(codePath).getParentFile();

		} catch (UnsupportedEncodingException ex) {
			// should never happen, UTF-8 encoding always exists
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Gets the binary used to run the program.
	 * 
	 * @return
	 */
	public static File getProgramExecutable() {
		// This checks if we are running from within an EXE file created using
		// JSmooth
		Object val = System.getProperties().get("jsmooth");
		if (val != null) {
			// TODO: figure out how to deal with this
			return null;
		}

		try {
			String codePath = URLDecoder.decode(Filesystem.class.getProtectionDomain().getCodeSource().getLocation()
					.getPath(), "UTF-8");

			if (!codePath.endsWith(".jar"))
				return null;

			return new File(codePath);

		} catch (UnsupportedEncodingException ex) {
			// should never happen, UTF-8 encoding always exists
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Convenience function to copy a file. If the destination file exists it is
	 * overwritten.
	 * 
	 * @param origPath
	 *            Original path
	 * @param destPath
	 *            Destination path
	 * @throws FileNotFoundException
	 *             if either file exists but is a directory rather than a
	 *             regular file, does not exist but cannot be read/created, or
	 *             cannot be opened for any other reason.
	 * @throws IOException
	 *             If the operation fails during the data copy phase.
	 */
	public static void copyFile(File origPath, File destPath) throws IOException {
		try (FileInputStream ins = new FileInputStream(origPath);) {
			writeFile(ins, destPath);
		}
	}

	/**
	 * Convenience function to write a file from an {@link InputStream}. If the destination file exists it is
	 * overwritten.
	 * 
	 * @param origPath
	 *            Original path
	 * @param destPath
	 *            Destination path
	 * @throws FileNotFoundException
	 *             if either file exists but is a directory rather than a
	 *             regular file, does not exist but cannot be read/created, or
	 *             cannot be opened for any other reason.
	 * @throws IOException
	 *             If the operation fails during the data copy phase.
	 */
	public static void writeFile(InputStream source, File destPath) throws IOException {
		try (FileOutputStream dest = new FileOutputStream(destPath)) {
			copyStream(source, dest);
		}
	}

	/**
	 * Convenience function to move a file.
	 * 
	 * @param origPath
	 * @param destPath
	 * @throws IOException
	 */
	public static void moveFile(File origPath, File destPath) throws IOException {
		copyFile(origPath, destPath);
		if (!origPath.delete())
			throw new IOException("Cannot delete file: " + origPath.getName());
	}

	/**
	 * Calculates the hash of an InputStream (for example file).
	 * 
	 * @param is
	 * @return hash as a string of hex characters
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static String getHash(InputStream is) throws IOException, NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte buffer[] = new byte[1024];
		int ret;
		while (true) {
			ret = is.read(buffer);
			if (ret < 0)
				break;
			md.update(buffer, 0, ret);
		}
		is.close();

		byte[] digest = md.digest();

		char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[digest.length * 2];
		int v;
		for (int i = 0; i < digest.length; i++) {
			v = digest[i] & 0xFF;
			hexChars[2 * i] = hexArray[v >> 4];
			hexChars[2 * i + 1] = hexArray[v & 0x0f];
		}
		return new String(hexChars, 0, digest.length * 2);
	}
	
	
	/**
	 * Copies data from an {@link InputStream} to an {@link OutputStream}
	 * 
	 * @param source
	 * @param target
	 * @throws IOException
	 */
	public static void copyStream(InputStream source, OutputStream target) throws IOException {
		byte[] buf = new byte[8192];
		int length;
		while ((length = source.read(buf)) > 0) {
			target.write(buf, 0, length);
		}
	}
	
	public static class JmcConfFile implements Closeable {
		private String path;
		private InputStream inputStream;
		
		public JmcConfFile(String path) throws IOException {
			if (!path.startsWith("conf")) {
				Log.error("Trying to create JmcFile not in conf folder!", null, false);
			}
			this.path = path;
			this.inputStream = openInputStream(path);
		}
		
		/**
		 * Attempts to read an extracted conf file, falls back to file bundled in .jar
		 * 
		 * @param filePath the file to read
		 * @return {@link InputStream} of the file
		 * @throws IOException 
		 */
		private InputStream openInputStream(String path) throws IOException {
			File extrFile = new File(getDatafilesDir(), path);
			if (extrFile.canRead()) {
				return new FileInputStream(extrFile);
			} else {
				Log.debug("Cannot read extracted conf file, attempting bundled " + path);
				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
				if (is == null) {
					throw new IOException("Could not read " + path + " from conf or .jar!");
				}
				return is;
			}
		}
		
		public String getPath() {
			return path;
		}
		
		/** 
		 * returns true if the input stream is not null
		 */
		public boolean hasStream() {
			return inputStream != null;
		}
		
		public InputStream getInputStream() {
			return inputStream;
		}

		@Override
		public void close() throws IOException {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		
	}
}
