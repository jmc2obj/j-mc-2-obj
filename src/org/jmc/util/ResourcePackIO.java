package org.jmc.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.jmc.Options;

public class ResourcePackIO {
	
	public static BufferedImage loadImage(String imagePath) throws IOException {
		try (InputStream is = loadResourceAsStream(imagePath)) {
			return ImageIO.read(is);
		}
	}
	
	public static Reader loadText(String textPath) throws FileNotFoundException {
		return new InputStreamReader(new BOMInputStream(loadResourceAsStream(textPath)));
	}
	
	public static InputStream loadResourceAsStream(String filePath) throws FileNotFoundException {
		return new ByteArrayInputStream(loadResource(filePath));
	}
	
	/**
	 *  tries to load the resource from any of the {@link Options#resourcePacks}
	 *  @return the file as bytes
	 */
	public static byte[] loadResource(String filePath) throws FileNotFoundException {
		List<File> packs = Options.resourcePacks;
		byte[] data = null;
		synchronized (packs) {
			for (File pack : packs) {
				try {
					data = loadResource(pack, filePath);
					break;
				} catch (IOException e) {
					continue;
				}
			}
		}
		if (data == null) {
			throw new FileNotFoundException(String.format("Couldn't find %s in current resource packs", filePath));
		}
		return data;
	}
	
	public static byte[] loadResource(File packPath, String filePath) throws IOException {
		if (packPath.isDirectory()) {
			try (FileInputStream fis = new FileInputStream(new File(packPath, filePath))) {
				return IOUtils.toByteArray(fis);
			}
		} else {
			try (ZipFile zipFile = new ZipFile(packPath)) {
				ZipEntry entry = null;
				entry = zipFile.getEntry(filePath);
				if (entry == null)
					throw new IOException("Couldn't find " + filePath + " in " + packPath.getName());
				return IOUtils.toByteArray(zipFile.getInputStream(entry));
			}
		}
	}
}
