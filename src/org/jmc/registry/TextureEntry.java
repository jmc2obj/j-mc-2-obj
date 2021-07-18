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

	TextureEntry(NamespaceID id) {
		super(id);
	}
	
	public Color getAverageColour() {
		BufferedImage image;
		try {
			image = getImage();
		} catch (IOException e) {
			Log.error("Error getting image for " + id, e);
			return Color.MAGENTA;
		}
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

	public boolean hasAlpha() {
		BufferedImage image;
		try {
			image = getImage();
		} catch (IOException e) {
			Log.error("Error getting image for " + id, e);
			return false;
		}
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
			try (InputStream is = new FileInputStream(new File(Registries.BASE_FOLDER, getPackPath()))) {
				setImage(ImageIO.read(is));
			}
			if (tint != null) {
				setImage(TextureExporter.convertImageType(buffImage));
				TextureExporter.tintImage(buffImage, tint);
			}
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

	public void exportTexture() {
		try {
			File file = new File(Options.outputDir, getExportFilePath());
			if (!file.exists()) {
				file.mkdirs();
				ImageIO.write(getImage(), "png", file);
			}
		} catch (IOException e) {
			Log.error("Error exporting texture " + id, e);
		}
	}
	
}
