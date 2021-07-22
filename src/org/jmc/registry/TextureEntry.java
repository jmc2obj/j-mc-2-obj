package org.jmc.registry;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import javax.imageio.ImageIO;

import org.jmc.Options;
import org.jmc.TextureExporter;
import org.jmc.registry.Registries.RegType;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;

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
			try (InputStream is = new FileInputStream(new File(Registries.BASE_FOLDER, getPackPath()+".mcmeta"))) {
				Meta meta = new Gson().fromJson(new InputStreamReader(is), Meta.class);
				if (meta != null && meta.animation != null) {
					Meta.Animation anim = meta.animation;
					int baseFrame = 0;
					if (anim.frames != null && anim.frames.length > 0) {
						Meta.Animation.Frame frameMeta = anim.frames[0];
						if (frameMeta != null) {
							baseFrame = anim.frames[0].index;
						}
					}
					int width = image.getWidth();
					int height = image.getHeight();
					BufferedImage frame = new BufferedImage(width, width, image.getType());
					image.getSubimage(0, width*baseFrame, width, width).copyData(frame.getRaster());
					image = frame;
				}
			} catch (IOException e) {
			} catch (JsonParseException e) {
				e.printStackTrace();
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
		File file = new File(Options.outputDir, getExportFilePath());
		if (true || !file.exists()) {
			file.getParentFile().mkdirs();
			ImageIO.write(TextureExporter.scaleImage(getImage(), Options.textureScale), "png", file);
		}
	}
	
	private class Meta {
		public Animation animation;
		private class Animation {
			public boolean interoplate;
			public int width = -1;
			public int height = -1;
			public int frametime;
			public Frame[] frames;
			@JsonAdapter(Frame.class)
			private class Frame implements JsonDeserializer<Frame> {
				public int index;
				public Integer time;
				
				@Override
				public Frame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
					try {
						Frame frame = new Frame();
						if (json.isJsonPrimitive()) {
							frame.index = json.getAsInt();
						} else {
							JsonObject obj = json.getAsJsonObject();
							frame.index = obj.get("index").getAsInt();
							JsonElement timeElem = obj.get("time");
							if (timeElem != null) {
								frame.time = timeElem.getAsInt();
							}
						}
						return frame;
					} catch (ClassCastException | IllegalStateException e) {
						throw new JsonParseException(e);
					}
				}
			}
		}
	}
	
}
