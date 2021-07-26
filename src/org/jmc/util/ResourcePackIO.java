package org.jmc.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.jmc.Options;

public class ResourcePackIO {
	
	public static BufferedImage loadImage(String imagePath) throws IOException {
		try (InputStream is = loadResourceAsStream(imagePath)) {
			return ImageIO.read(is);
		}
	}
	
	public static InputStream loadResourceAsStream(String filePath) throws FileNotFoundException {
		return new ByteArrayInputStream(loadResource(filePath));
	}
	
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
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		if (packPath.isDirectory()) {
			try (FileInputStream fis = new FileInputStream(new File(packPath, filePath))) {
				Filesystem.copyStream(fis, buffer);
			}
		} else {
			try (ZipFile zipFile = new ZipFile(packPath)) {
				ZipEntry entry = null;
				entry = zipFile.getEntry(filePath);
				if (entry == null)
					throw new IOException("Couldn't find " + filePath + " in " + packPath.getName());
				Filesystem.copyStream(zipFile.getInputStream(entry), buffer);
			}
		}
		return buffer.toByteArray();
	}
}
