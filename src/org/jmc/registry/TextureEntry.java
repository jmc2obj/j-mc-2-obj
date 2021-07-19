package org.jmc.registry;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.jmc.Options;
import org.jmc.TextureExporter;
import org.jmc.registry.Registries.RegType;
import org.jmc.util.Log;

public class TextureEntry extends RegistryEntry {

	//The texture
	private BufferedImage buffImage;
	//Overrides the 'id' for when the image is read from a resource pack
	private NamespaceID sourceIdOverride;
	//cached average colour
	private Color avgCol;
	//cached has alpha
	private Boolean hasAlpha;
	//tint to apply to texture
	private Color tint;
	//if true, don't export, purely for internal use
	public boolean virtual;
	
	//for texture exporter
	public boolean repeating;
	public boolean luma;

	TextureEntry(NamespaceID id) {
		super(id);
	}
	
	public Color getAverageColour() throws IOException {
		BufferedImage image = getImage();
		if (avgCol == null) {
			float red = 0;
			float green = 0;
			float blue = 0;
			int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
			int count = 0;
			for (int pixelData : pixels) {
				Color pixel = new Color(pixelData, true);
				float[] values = pixel.getRGBComponents(null);
				if (values[3] == 0)
					continue;// pixel has 0 alpha
				red += values[0];
				green += values[1];
				blue += values[2];
				count++;
			}
			red /= count;
			green /= count;
			blue /= count;
			avgCol = new Color(red, green, blue);
		}
		return avgCol;
	}

	public boolean hasAlpha() throws IOException {
		BufferedImage image = getImage();
		if (hasAlpha == null) {
			hasAlpha = false;
			int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
			for (int pixelData : pixels) {
				Color pixel = new Color(pixelData, true);
				float[] values = pixel.getRGBComponents(null);
				if (values[3] < 1) {
					hasAlpha = true;
					break;
				}
			}
		}
		return hasAlpha;
	}
	
	public BufferedImage getImage() throws IOException {
		if (buffImage == null) {
			BufferedImage image;
			try (InputStream is = new FileInputStream(new File(Registries.BASE_FOLDER, getPackPath()))) {
				image = ImageIO.read(is);
			}
			if (tint != null) {
				image = TextureExporter.convertImageType(image);
				TextureExporter.tintImage(image, tint);
			}
			setImage(image);
		}
		return buffImage;
	}
	
	public void setImage(BufferedImage image) {
		avgCol = null;
		hasAlpha = null;
		buffImage = image;
	}

	public String getPackPath() {
		if (sourceIdOverride != null) {
			return Registries.getFilePath(sourceIdOverride, RegType.TEXTURE);
		} else {
			return Registries.getFilePath(id, RegType.TEXTURE);
		}
	}

	public String getExportFilePath() {
		return String.format("tex/%s/%s.png", id.namespace, id.path);
	}

	public String getMatName() {
		return id.getExportSafeString();
	}

	public void exportTexture() throws IOException {
		if (virtual) return;
		File file = new File(Options.outputDir, getExportFilePath());
		if (true || !file.exists()) {
			file.getParentFile().mkdirs();
			ImageIO.write(getImage(), "png", file);
		}
	}
	
}
