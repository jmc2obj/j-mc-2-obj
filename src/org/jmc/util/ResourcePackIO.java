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
import java.util.ArrayList;
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
	public static List<Reader> loadAllText(String textPath) throws FileNotFoundException {
		List<Reader> readers = new ArrayList<>();
		for (InputStream stream : loadAllResourcesAsStreams(textPath)) {
			readers.add(new InputStreamReader(new BOMInputStream(stream)));
		}
		return readers;
	}
	
	public static InputStream loadResourceAsStream(String filePath) throws FileNotFoundException {
		return new ByteArrayInputStream(loadResource(filePath));
	}
	public static List<InputStream> loadAllResourcesAsStreams(String filePath) throws FileNotFoundException {
		List<InputStream> streams = new ArrayList<>();
		for (byte[] resource : loadAllResources(filePath)) {
			streams.add(new ByteArrayInputStream(resource));
		}
		return streams;
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
	
	/**
	 *  tries to load the resource from all of the {@link Options#resourcePacks}
	 *  @return the file/s as array of bytes
	 */
	public static List<byte[]> loadAllResources(String filePath) throws FileNotFoundException {
		List<File> packs = Options.resourcePacks;
		ArrayList<byte[]> resources = new ArrayList<>();
		synchronized (packs) {
			for (File pack : packs) {
				try {
					byte[] data = loadResource(pack, filePath);
					if (data != null) {
						resources.add(data);
					}
				} catch (IOException e) {
					continue;
				}
			}
		}
		if (resources.isEmpty()) {
			throw new FileNotFoundException(String.format("Couldn't find %s in current resource packs", filePath));
		}
		return resources;
	}
	
	public static byte[] loadResource(File packPath, String filePath) throws IOException {
		if (packPath.isFile() && packPath.getName().equals("pack.mcmeta")) {
			packPath = packPath.getParentFile();
		}
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
