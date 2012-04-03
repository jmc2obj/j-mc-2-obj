package org.jmc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;


/**
 * Utility class that can extract the individual textures from minecraft texture packs.  
 */
public class Texsplit
{
	final static int ROWS = 16;
	final static int COLS = 16;
	

	private static BufferedImage loadImageFromZip(File zipfile, String imagePath) throws IOException
	{
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipfile));

		ZipEntry entry = null;
		while ((entry = zis.getNextEntry()) != null)
		{
			if (!entry.isDirectory() && entry.getName().equals(imagePath))
				break;
		}

		if (entry == null)
			throw new IOException("Couldn't find " + imagePath + " in " + zipfile.getName());

		BufferedImage result = ImageIO.read(zis);
		zis.close();

		return result;
	}
	
	private static void convertToAlpha(BufferedImage img)
	{
		int w=img.getWidth();
		int h=img.getHeight();
		int c=img.getColorModel().getPixelSize()/8;
		
		if(c!=4)
		{
			MainWindow.log("ERROR: texture is not 32-bit!");
			return;
		}
		
		int buffer[]=new int[w*h*c];

		WritableRaster raster=img.getRaster();
		raster.getPixels(0, 0, w, h, buffer);
		
		for(int i=0; i<w*h; i++)
		{
			buffer[4*i]=buffer[4*i+3];
			buffer[4*i+1]=buffer[4*i+3];
			buffer[4*i+2]=buffer[4*i+3];
			buffer[4*i+3]=255;
		}
		
		raster.setPixels(0, 0, w, h, buffer);
	}
	
	private static void tintImage(BufferedImage img, Color tint)
	{
		int w=img.getWidth();
		int h=img.getHeight();
		int c=img.getColorModel().getPixelSize()/8;
		
		if(c!=4)
		{
			MainWindow.log("ERROR: texture is not 32-bit!");
			return;
		}
		
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
	 * Reads a Minecraft texture pack and splits the individual block textures into .png images.
	 * 
	 * @param destination Directory to place the output files.
	 * @param texturePack A Minecraft texture pack file. If null, will use minecraft's default textures.
	 */
	public static void splitTextures(File destination, File texturePack) throws Exception
	{
		if(destination==null)
			throw new IllegalArgumentException("destination cannot be null");
	
		File zipfile;
		if (texturePack == null)
			zipfile = new File(Utility.getMinecraftDir(), "bin/minecraft.jar");
		else
			zipfile = texturePack;

		if (!zipfile.canRead())
			throw new Exception("Cannot open " + zipfile.getName());
		
		BufferedImage image = loadImageFromZip(zipfile, "terrain.png");

		int width = image.getWidth() / COLS;
		int height = image.getHeight() / ROWS;
		BufferedImage[] textures = new BufferedImage[ROWS * COLS];
		
		int counter = 0;
		for(int i = 0; i < ROWS; i++)
		{
			for(int j = 0; j < COLS; j++)
			{
				textures[(i * COLS) + j] = image.getSubimage(j * width, i * height, width, height);
				BufferedImage im = textures[(i * COLS) + j];
				counter++;

				if (counter < ROWS*COLS)
				{
					if(counter==1 || counter==53 || counter==40 || counter==144)
					{
						tintImage(im, Color.green);
					}
					File f=new File(destination.getAbsolutePath() + "/" + counter + ".png");
					ImageIO.write(im, "png", f);
					MainWindow.log("Saving texture to: "+f.getAbsolutePath());
					
					convertToAlpha(im);
					File f2=new File(destination.getAbsolutePath() + "/" + counter + "_a.png");
					ImageIO.write(im, "png", f2);				
					MainWindow.log("Saving texture alpha to: "+f2.getAbsolutePath());
				}
			}
		}
	}
	
}
