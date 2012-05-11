package org.jmc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jmc.util.Log;
import org.jmc.util.Filesystem;
import org.jmc.util.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Utility class that can extract the individual textures from minecraft texture packs.  
 */
public class Texsplit
{
	private static final String CONFIG_FILE = "conf/texsplit.conf";


	private static BufferedImage loadImageFromFile(File file) throws IOException
	{
		return ImageIO.read(file);
	}

	private static BufferedImage loadImageFromZip(File zipfile, String imagePath) throws IOException
	{
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipfile));

		ZipEntry entry = null;
		while ((entry = zis.getNextEntry()) != null)
			if (!entry.isDirectory() && entry.getName().equals(imagePath))
				break;

		if (entry == null)
			throw new IOException("Couldn't find " + imagePath + " in " + zipfile.getName());

		BufferedImage result = ImageIO.read(zis);
		zis.close();

		return result;
	}


	private static BufferedImage convertImageType(BufferedImage image)
	{
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage ret = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);

		int[] pixels = new int[w*h];
		image.getRGB(0, 0, w, h, pixels, 0, w);
		ret.setRGB(0, 0, w, h, pixels, 0, w);
		
		return ret;
	}

	private static void convertToAlpha(BufferedImage img) throws ImagingOpException
	{
		int w=img.getWidth();
		int h=img.getHeight();
		int c=img.getColorModel().getPixelSize()/8;

		if(c!=4)
			throw new ImagingOpException("Texture is not 32-bit!");

		int buffer[]=new int[w*h*c];

		WritableRaster raster=img.getRaster();
		raster.getPixels(0, 0, w, h, buffer);

		for(int i=0; i<w*h; i++)
		{
			buffer[4*i]=buffer[4*i+3];
			buffer[4*i+1]=buffer[4*i+3];
			buffer[4*i+2]=buffer[4*i+3];
		}

		raster.setPixels(0, 0, w, h, buffer);
	}

	private static void tintImage(BufferedImage img, Color tint) throws ImagingOpException
	{
		int w=img.getWidth();
		int h=img.getHeight();
		int c=img.getColorModel().getPixelSize()/8;

		if(c!=4)
			throw new ImagingOpException("Texture is not 32-bit!");

		int buffer[]=new int[w*h*c];

		WritableRaster raster=img.getRaster();
		raster.getPixels(0, 0, w, h, buffer);

		int r=tint.getRed();
		int g=tint.getGreen();
		int b=tint.getBlue();

		for(int i=0; i<w*h; i++)
		{
			c=(buffer[4*i]*r)>>8;
			if(c>255) c=255;
			buffer[4*i]=c;
	
			c=(buffer[4*i+1]*g)>>8;
			if(c>255) c=255;
			buffer[4*i+1]=c;
	
			c=(buffer[4*i+2]*b)>>8;
			if(c>255) c=255;
			buffer[4*i+2]=c;
		}

		raster.setPixels(0, 0, w, h, buffer);
	}


	/**
	 * Reads the configuration file "texsplit.conf".
	 * Reads a Minecraft texture pack and splits the individual block textures into .png images.
	 * 
	 * @param destination Directory to place the output files.
	 * @param texturePack A Minecraft texture pack file. If null, will use minecraft's default textures.
	 * @param alphas Whether to export separate alpha masks.
	 * @param progress If not null, the exporter will invoke this callback to inform on the operation's progress.
	 * @throws Exception if there is an error.
	 */
	public static void splitTextures(File destination, File texturePack, boolean alphas, ProgressCallback progress) throws Exception
	{
		if(destination==null)
			throw new IllegalArgumentException("destination cannot be null");
		
		if(!destination.exists() || !destination.isDirectory())
		{
			if(destination.exists()) throw new RuntimeException("Cannot create texture directory! Filen is in the way!");
			if(!destination.mkdir())  throw new RuntimeException("Cannot create texture directory!");
		}

		File zipfile;
		if (texturePack == null)
			zipfile = new File(Filesystem.getMinecraftDir(), "bin/minecraft.jar");
		else
			zipfile = texturePack;
		if (!zipfile.canRead())
			throw new Exception("Cannot open " + zipfile.getName());

		File confFile = new File(Filesystem.getDatafilesDir(), CONFIG_FILE);
		if (!confFile.canRead())
			throw new Exception("Cannot open configuration file " + CONFIG_FILE);		
		
		Document doc = Xml.loadDocument(confFile);
		XPath xpath = XPathFactory.newInstance().newXPath();

		NodeList fileNodes = (NodeList)xpath.evaluate("/texsplit/file", doc, XPathConstants.NODESET);
		for (int i = 0; i < fileNodes.getLength(); i++)
		{
			Node fileNode = fileNodes.item(i);
			String source = Xml.getAttribute(fileNode, "source", "texturepack");
			String fileName = Xml.getAttribute(fileNode, "name");
			int rows = Integer.parseInt(Xml.getAttribute(fileNode, "rows", "1"), 10);
			int cols = Integer.parseInt(Xml.getAttribute(fileNode, "cols", "1"), 10);

			if (fileName == null || fileName.length() == 0)
				throw new Exception("In " + CONFIG_FILE + ": 'file' tag is missing required attribute 'name'.");

			BufferedImage image;
			if (source.equalsIgnoreCase("texturepack"))
				image = loadImageFromZip(zipfile, fileName);
			else
				image = loadImageFromFile(new File(Filesystem.getDatafilesDir(), fileName));

			if (image.getType() != BufferedImage.TYPE_4BYTE_ABGR)
				image = convertImageType(image);
			
			int width = image.getWidth() / cols;
			int height = image.getHeight() / rows;

			NodeList texNodes = (NodeList)xpath.evaluate("tex", fileNode, XPathConstants.NODESET);
			for (int j = 0; j < texNodes.getLength(); j++)
			{
				Node texNode = texNodes.item(j);
				String pos = Xml.getAttribute(texNode, "pos", "1,1");
				String texName = Xml.getAttribute(texNode, "name");
				String tint = Xml.getAttribute(texNode, "tint");

				if (texName == null)
					continue;

				String[] parts = pos.split("\\s*,\\s*");
				if (parts.length != 2)
					throw new Exception("In " + CONFIG_FILE + ": attribute 'pos' has invalid format.");
				int rowPos = Integer.parseInt(parts[0], 10) - 1;
				int colPos = Integer.parseInt(parts[1], 10) - 1;

				BufferedImage texture = new BufferedImage(width, height, image.getType());
				image.getSubimage(colPos*width, rowPos*height, width, height).copyData(texture.getRaster());

				if (tint != null && tint.length() > 0)
				{
					try{
						tintImage(texture, new Color(Integer.parseInt(tint, 16)));
					}catch(ImagingOpException e)
					{
						Log.info("Cannot tint image: "+texName+" ("+e.getMessage()+")");
					}
				}

				ImageIO.write(texture, "png", new File(destination, texName + ".png"));

				if(alphas)
				{
					try{
						convertToAlpha(texture);
						ImageIO.write(texture, "png", new File(destination, texName + "_a.png"));
					}catch(ImagingOpException e)
					{
						Log.info("Cannot save alpha for: "+texName+" ("+e.getMessage()+")");
					}
				}

			}

			if (progress != null)
				progress.setProgress((i+1) / (float)fileNodes.getLength());
		}
	}

}
