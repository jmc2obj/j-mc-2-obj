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
import java.awt.image.Raster;
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
		byte[] bytes = loadResource(imagePath);
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		if (image == null) throw new IOException("Unsupported image: " + imagePath);
		// Minecraft treats PNG samples as texture channel values. ImageIO assigns
		// grayscale PNGs a linear gray color space, so getRGB would brighten them.
		// Java 8 also ignores grayscale/truecolor tRNS transparency. Decode those
		// channels explicitly, before any tinting, compositing or PNG export.
		if (bytes.length < 33 || bytes[0] != (byte)137 || bytes[1] != 'P'
				|| bytes[2] != 'N' || bytes[3] != 'G') return image;
		int depth = bytes[24] & 255, type = bytes[25] & 255;
		boolean gray = type == 0 || type == 4;
		int[] transparent = null;
		for (int offset = 8; offset <= bytes.length - 12;) {
			int length = readInt(bytes, offset);
			if (length < 0 || length > bytes.length - offset - 12) break;
			if (bytes[offset+4] == 't' && bytes[offset+5] == 'R'
					&& bytes[offset+6] == 'N' && bytes[offset+7] == 'S'
					&& ((type == 0 && length == 2) || (type == 2 && length == 6))) {
				transparent = new int[length / 2];
				for (int i = 0; i < transparent.length; i++)
					transparent[i] = ((bytes[offset+8+i*2] & 255) << 8) | (bytes[offset+9+i*2] & 255);
			}
			offset += length + 12;
		}
		if (!gray && transparent == null) return image;
		Raster raster = image.getRaster();
		int max = (1 << depth) - 1;
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = image.getRGB(x, y);
				if (gray) {
					int sample = raster.getSample(x, y, 0);
					int value = (sample * 255 + max / 2) / max;
					int alpha = type == 4 ? (raster.getSample(x, y, 1) * 255 + max / 2) / max : 255;
					if (transparent != null && sample == transparent[0]) alpha = 0;
					pixel = (alpha << 24) | (value << 16) | (value << 8) | value;
				} else if (raster.getSample(x, y, 0) == transparent[0]
						&& raster.getSample(x, y, 1) == transparent[1]
						&& raster.getSample(x, y, 2) == transparent[2]) {
					pixel &= 0xffffff;
				}
				result.setRGB(x, y, pixel);
			}
		}
		return result;
	}

	private static int readInt(byte[] data, int offset) {
		return ((data[offset] & 255) << 24) | ((data[offset+1] & 255) << 16)
				| ((data[offset+2] & 255) << 8) | (data[offset+3] & 255);
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
		try {
			return loadResourceExact(packPath, filePath);
		} catch (IOException original) {
			String alias = getLegacyAlias(filePath);
			if (alias != null) {
				try {
					return loadResourceExact(packPath, alias);
				} catch (IOException missingAlias) {
					// Keep the original missing-resource diagnostic.
				}
			}
			throw original;
		}
	}

	private static String getLegacyAlias(String path) {
		if (path.equals("assets/minecraft/textures/entity/end_portal.png")) {
			return "assets/minecraft/textures/entity/end_portal/end_portal.png";
		}
		if (path.equals("assets/minecraft/textures/entity/banner_base.png")) {
			return "assets/minecraft/textures/entity/banner/banner_base.png";
		}
		if (path.equals("assets/minecraft/blockstates/chain.json")) {
			return "assets/minecraft/blockstates/iron_chain.json";
		}
		if (path.equals("assets/minecraft/models/block/chain.json")) {
			return "assets/minecraft/models/block/iron_chain.json";
		}
		if (path.equals("assets/minecraft/textures/block/chain.png")) {
			return "assets/minecraft/textures/block/iron_chain.png";
		}
		return null;
	}

	private static byte[] loadResourceExact(File packPath, String filePath) throws IOException {
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
