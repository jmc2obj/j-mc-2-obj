package org.jmc.registry;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.io.FilenameUtils;
import org.jmc.TextureExporter;
import org.jmc.registry.Registries.RegType;
import org.jmc.util.ResourcePackIO;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.jmc.util.Log;
import com.google.gson.annotations.Expose;

public class TextureEntry extends RegistryEntry {
	
	//The texture
	@CheckForNull
	private BufferedImage buffImage;
	private transient BufferedImage untintedImage;
	//Overrides the 'id' for when the image is read from a resource pack
	@Expose
	@CheckForNull
	private NamespaceID sourceIdOverride;
	//cached average colour
	@CheckForNull
	private Color avgCol;
	//cached has alpha
	@CheckForNull
	private Boolean hasAlpha;
	//tint to apply to texture
	@Expose
	@CheckForNull
	private Color tint;
	
	//for texture exporter
	@Expose
	public boolean repeating;
	@Expose
	public boolean luma;

	TextureEntry(NamespaceID id) {
		super(id);
	}
	
	@Nonnull
	public Color getAverageColour() throws IOException {
		BufferedImage image = getImage();
		if (avgCol != null) {
			return avgCol;
		} else {
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
			return avgCol;
		}
	}

	public boolean hasAlpha() throws IOException {
		BufferedImage image = getImage();
		if (hasAlpha != null) {
			return hasAlpha;
		} else {
			int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
			for (int pixelData : pixels) {
				Color pixel = new Color(pixelData, true);
				float[] values = pixel.getRGBComponents(null);
				if (values[3] < 1) {
					hasAlpha = true;
					return hasAlpha;
				}
			}
			hasAlpha = false;
			return hasAlpha;
		}
	}
	
	public BufferedImage getImage() throws IOException {
		if (buffImage != null) {
			return buffImage;
		} else {
			BufferedImage image = ResourcePackIO.loadImage(getFilePath());
			// Keep the complete spritesheet; OBJ animation is configured by the user.
			if (tint != null) {
				image = TextureExporter.convertImageType(image);
				TextureExporter.tintImage(image, tint);
			}
			setImage(image);
			return buffImage;
		}
	}

	/** The model's tintindex decides coloration; conf/textures.json must not
	 * pre-color the source before a face-specific tint or overlay is applied. */
	public synchronized BufferedImage getUntintedImage() throws IOException {
		if (untintedImage == null) {
			untintedImage = ResourcePackIO.loadImage(getFilePath());
		}
		return untintedImage;
	}

	public Color getConfiguredTint() { return tint; }

	public TextureEntry derived(NamespaceID derivedId, BufferedImage image) {
		TextureEntry result = new TextureEntry(derivedId);
		result.sourceIdOverride = sourceIdOverride == null ? id : sourceIdOverride;
		result.repeating = repeating;
		result.luma = luma;
		result.metadataOverride = metadataOverride == null ? null : metadataOverride.deepCopy();
		result.setImage(image);
		return result;
	}
	
	public BufferedImage getNormalMap() throws IOException {
		String filePath = getFilePath();
		String basePath = FilenameUtils.removeExtension(filePath);
		String ext = FilenameUtils.getExtension(filePath);
		BufferedImage image = ResourcePackIO.loadImage(basePath + "_n." + ext);
		// Keep the complete spritesheet; OBJ animation is configured by the user.
		return image;
	}
	
	public BufferedImage getSpecularMap() throws IOException {
		String filePath = getFilePath();
		String basePath = FilenameUtils.removeExtension(filePath);
		String ext = FilenameUtils.getExtension(filePath);
		BufferedImage image = ResourcePackIO.loadImage(basePath + "_s." + ext);
		// Keep the complete spritesheet; OBJ animation is configured by the user.
		return image;
	}
	
	private transient JsonObject metadataOverride;

	/** Preserve source metadata, including frame order, timing and interpolation. */
	public JsonObject getMetadata() throws IOException {
		if (metadataOverride != null) return metadataOverride.deepCopy();
		try (Reader reader = ResourcePackIO.loadText(getFilePath() + ".mcmeta")) {
			JsonObject metadata = new Gson().fromJson(reader, JsonObject.class);
			return metadata == null ? null : metadata;
		} catch (java.io.FileNotFoundException absent) {
			return null;
		} catch (JsonParseException malformed) {
			Log.errorOnce("Cannot read texture metadata for " + getFilePath(), malformed, false);
			return null;
		}
	}

	public void setMetadata(JsonObject metadata) {
		metadataOverride = metadata == null ? null : metadata.deepCopy();
	}

	public void setImage(BufferedImage image) {
		avgCol = null;
		hasAlpha = null;
		buffImage = image;
	}

	public String getFilePath() {
		if (sourceIdOverride != null) {
			return Registries.getFilePath(sourceIdOverride, RegType.TEXTURE);
		} else {
			return Registries.getFilePath(id, RegType.TEXTURE);
		}
	}

	public String getExportFilePath() {
		return String.format("tex/%s/%s.png", id.namespace, id.path);
	}

	public String getExportFilePathAlpha() {
		return String.format("tex/%s/%s_a.png", id.namespace, id.path);
	}

	public String getExportFilePathNormal() {
		return String.format("tex/%s/%s_n.png", id.namespace, id.path);
	}

	public String getExportFilePathSpecular() {
		return String.format("tex/%s/%s_s.png", id.namespace, id.path);
	}

	public String getMatName() {
		return id.getExportSafeString();
	}
	
}
