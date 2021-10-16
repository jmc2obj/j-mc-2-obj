package org.jmc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;

import org.jmc.registry.NamespaceID;
import org.jmc.registry.TextureEntry;
import org.jmc.util.Log;
import org.jmc.util.Messages;
import org.jmc.util.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that can extract the individual textures from minecraft texture
 * packs.
 */
public class TextureExporter {
	@Nonnull
	public static BufferedImage convertImageType(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage ret = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);

		int[] pixels = new int[w * h];
		image.getRGB(0, 0, w, h, pixels, 0, w);
		ret.setRGB(0, 0, w, h, pixels, 0, w);

		return ret;
	}

	public static BufferedImage convertToAlpha(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		
		BufferedImage aImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int alpha = new Color(img.getRGB(x, y), true).getAlpha();
				aImage.setRGB(x, y, new Color(alpha, alpha, alpha).getRGB());
			}
		}
		
		return aImage;
	}

	public static void tintImage(BufferedImage img, Color tint) {
		int w = img.getWidth();
		int h = img.getHeight();
		int c = img.getColorModel().getPixelSize() / 8;

		if (c != 4)
			throw new ImagingOpException("Texture is not 32-bit!");

		int[] buffer = new int[w * h * c];

		WritableRaster raster = img.getRaster();
		raster.getPixels(0, 0, w, h, buffer);

		int r = tint.getRed();
		int g = tint.getGreen();
		int b = tint.getBlue();

		for (int i = 0; i < w * h; i++) {
			c = (buffer[4 * i] * r) >> 8;
			if (c > 255)
				c = 255;
			buffer[4 * i] = c;

			c = (buffer[4 * i + 1] * g) >> 8;
			if (c > 255)
				c = 255;
			buffer[4 * i + 1] = c;

			c = (buffer[4 * i + 2] * b) >> 8;
			if (c > 255)
				c = 255;
			buffer[4 * i + 2] = c;
		}

		raster.setPixels(0, 0, w, h, buffer);
	}

	public static BufferedImage scaleImage(BufferedImage img, double factor) {
		int w = img.getWidth();
		int h = img.getHeight();
		int new_w = (int) (w * factor);
		int new_h = (int) (h * factor);

		BufferedImage result = cloneImageType(img, new_w, new_h);
		for (int y = 0; y < new_h; y++) {
			for (int x = 0; x < new_w; x++) {
				int src = img.getRGB((int) (x / factor), (int) (y / factor));
				result.setRGB(x, y, src);
			}
		}

		return result;
	}

	/**
	 * Makes a new {@link BufferedImage} of the same type with the specified width and height
	 * Copies the {@link IndexColorModel} of indexed images. 
	 * 
	 * @param img The image to copy from
	 * @param width
	 * @param height
	 * @return An empty {@link BufferedImage} with matching type
	 */
	public static BufferedImage cloneImageType(BufferedImage img, int width, int height) {
		BufferedImage result;
		if ((img.getType() == BufferedImage.TYPE_BYTE_INDEXED || img.getType() == BufferedImage.TYPE_BYTE_BINARY) && img.getColorModel() instanceof IndexColorModel) {
			result = new BufferedImage(width, height, img.getType(), (IndexColorModel) img.getColorModel());
		} else {
			result = new BufferedImage(width, height, img.getType());
		}
		return result;
	}
	
	/**
	 * Exports all the textures 
	 *
	 * @param textures The {@link TextureEntry TextureEntrys} to export
	 * @param progress
	 *            If not null, the exporter will invoke this callback to inform
	 *            on the operation's progress.
	 * @throws IOException 
	 * @throws Exception
	 *             if there is an error.
	 */
	public static void exportTextures(Set<TextureEntry> textures, ProgressCallback progress) throws IOException {
		if (progress != null)
			progress.setMessage(Messages.getString("Progress.TEX"));
		int count = 0;
		for (TextureEntry texture : textures) {
			File file = new File(Options.outputDir, texture.getExportFilePath());
			if (Options.textureDiffuse && (Options.textureOverwrite || !file.exists())) {
				file.getParentFile().mkdirs();
				try {
					ImageIO.write(scaleImage(texture.getImage(), Options.textureScale), "png", file);
				} catch (IOException e) {
					Log.error("Couldn't export texture " + texture.id.toString(), e);
				}
			}
			if (Options.textureAlpha) {
				File alphaFile = new File(Options.outputDir, texture.getExportFilePathAlpha());
				if (Options.textureOverwrite || !alphaFile.exists()) {
					alphaFile.getParentFile().mkdirs();
					try {
						ImageIO.write(scaleImage(convertToAlpha(texture.getImage()), Options.textureScale), "png", alphaFile);
					} catch (IOException e) {
						Log.debug(String.format("Couldn't export alpha texture for '%s' error: %s", texture.id.toString(), e.getMessage()));
					}
				}
			}
			if (Options.textureNormal) {
				File normalFile = new File(Options.outputDir, texture.getExportFilePathNormal());
				if (Options.textureOverwrite || !normalFile.exists()) {
					normalFile.getParentFile().mkdirs();
					try {
						ImageIO.write(scaleImage(texture.getNormalMap(), Options.textureScale), "png", normalFile);
					} catch (IOException e) {
						Log.debug(String.format("Couldn't export normal texture for '%s' error: %s", texture.id.toString(), e.getMessage()));
					}
				}
			}
			if (Options.textureSpecular) {
				File specularFile = new File(Options.outputDir, texture.getExportFilePathSpecular());
				if (Options.textureOverwrite || !specularFile.exists()) {
					specularFile.getParentFile().mkdirs();
					try {
						ImageIO.write(scaleImage(texture.getSpecularMap(), Options.textureScale), "png", specularFile);
					} catch (IOException e) {
						Log.debug(String.format("Couldn't export specular texture for '%s' error: %s", texture.id.toString(), e.getMessage()));
					}
				}
			}
			if (progress != null)
				progress.setProgress((float)++count / textures.size());
		}
	}
	
	/**
	 * Reads a Minecraft texture pack and splits the individual block textures
	 * into separate images then merges them into a single file containing all
	 * the textures.
	 *
	 * @param textures The textures to merge
	 * @param progress
	 *            If not null, the exporter will invoke this callback to inform
	 *            on the operation's progress.
	 * @throws Exception
	 *             if there is an error.
	 */
	public static void mergeTextures(Set<TextureEntry> textures, ProgressCallback progress) throws Exception {
		if (progress != null)
			progress.setMessage(Messages.getString("Progress.TEX"));
		Map<NamespaceID, Rectangle> ret = new HashMap<>();
		
		// calculate maxwidth so to keep the size of the final file more or less
		// square
		double surface = 0;
		for (TextureEntry texture : textures) {
			if (texture.repeating)
				surface += texture.getImage().getWidth() * texture.getImage().getHeight() * 9.0;
			surface += texture.getImage().getWidth() * texture.getImage().getHeight();
		}
		int maxwidth = (int) Math.sqrt(surface);
		
		// calculate coordinates for placing the textures in the file
		int wused = 0, hused = 0, hcurr = 0, wmax = 0, hmax = 0;
		for (TextureEntry texture : textures) {
			if (wused > maxwidth) {
				wused = 0;
				hused += hcurr;
				hcurr = 0;
			}
			
			int w = texture.getImage().getWidth();
			int h = texture.getImage().getHeight();

			if (texture.repeating) {
				ret.put(texture.id, new Rectangle(wused + w, hused + h, w, h));
				w *= 3;
				h *= 3;
			} else {
				ret.put(texture.id, new Rectangle(wused, hused, w, h));
			}

			wused += w;
			if (hcurr < h)
				hcurr = h;

			if (wmax < wused)
				wmax = wused;
			if (hmax < (hused + hcurr))
				hmax = hused + hcurr;
		}

		for (int x = 1; x < Short.MAX_VALUE; x *= 2)
			if (x >= wmax) {
				wmax = x;
				break;
			}
		for (int x = 1; x < Short.MAX_VALUE; x *= 2)
			if (x >= hmax) {
				hmax = x;
				break;
			}

		Document doc = Xml.newDocument();
		Element root = doc.createElement("textures");
		root.setAttribute("width", "" + wmax);
		root.setAttribute("height", "" + hmax);
		doc.appendChild(root);

		for (Entry<NamespaceID, Rectangle> entry : ret.entrySet()) {
			Element el = doc.createElement("texture");
			el.setTextContent(entry.getKey().toString());
			Rectangle rect = entry.getValue();
			el.setAttribute("u", "" + rect.x);
			el.setAttribute("v", "" + rect.y);
			el.setAttribute("w", "" + rect.width);
			el.setAttribute("h", "" + rect.height);
			root.appendChild(el);
		}

		Xml.saveDocument(doc, new File(Options.outputDir, "texture.uv"));

		BufferedImage textureimage = new BufferedImage(wmax, hmax, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D gtex = textureimage.createGraphics();

		BufferedImage lumaimage = new BufferedImage(wmax, hmax, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D gtexluma = lumaimage.createGraphics();

		float texnum = textures.size();
		float count = 0;

		for (TextureEntry texture : textures) {
			Rectangle rect = ret.get(texture.id);

			if (texture.repeating) {
				for (int x = -1; x <= 1; x++)
					for (int y = -1; y <= 1; y++) {
						int sx = rect.x + x * rect.width;
						int sy = rect.y + y * rect.height;
						gtex.drawImage(texture.getImage(), sx, sy, sx + rect.width, sy + rect.height, 0, 0, rect.width, rect.height, null);
						
						if(Options.textureLight)
							if(texture.luma)
								gtexluma.drawImage(texture.getImage(), sx, sy, sx + rect.width, sy + rect.height, 0, 0, rect.width, rect.height, null);
					}
			} else {
				gtex.drawImage(texture.getImage(), rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, 0, 0, rect.width, rect.height, null);
				if(Options.textureLight)
					if(texture.luma)
						gtexluma.drawImage(texture.getImage(), rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, 0, 0, rect.width, rect.height, null);
			}
			
			if (progress != null)
				progress.setProgress((++count) / texnum);
		}
		
		ImageIO.write(textureimage, "png", new File(Options.outputDir, "tex/texture.png"));
		if(Options.textureLight)
			ImageIO.write(lumaimage, "png", new File(Options.outputDir, "tex/texture_luma.png"));
		
		if (Options.textureAlpha) {
			try {
				ImageIO.write(convertToAlpha(textureimage), "png", new File(Options.outputDir, "tex/texture_a.png"));
			} catch (Exception e) {
				Log.info("Cannot save alpha (" + e.getMessage() + ")");
			}
		}
	}

}
