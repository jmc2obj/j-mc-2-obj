package org.jmc.registry;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.jmc.Materials;
import org.jmc.Options;

public class TextureEntry extends RegistryEntry {

	private BufferedImage image;
	private Color avgCol;
	private Boolean hasAlpha;

	private TextureEntry(NamespaceID id, BufferedImage image) {
		super(id);
		this.image = image;
	}
	
	public Color getAverageColour() {
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

	public static TextureEntry fromStream(NamespaceID id, InputStream is) {
		BufferedImage image;
		try {
			image = ImageIO.read(is);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return new TextureEntry(id, image);
	}

	public String getTexFilePath() {
		return String.format("tex/%s/%s.png", id.namespace, id.path);
	}

	public String getMatName() {
		return id.toString().replace(':', '_').replace('/', '-');
	}
	
	public void addToMaterials() {
		Materials.addMaterial(getMatName(), getAverageColour(), getTexFilePath());
		Materials.writeMaterial(getMatName(), getAverageColour(), null, getTexFilePath(), hasAlpha() ? getTexFilePath() : null);
	}

	public void exportTexture() {
		try {
			File file = new File(Options.outputDir, getTexFilePath());
			if (!file.exists()) {
				file.mkdirs();
				ImageIO.write(image, "png", file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
