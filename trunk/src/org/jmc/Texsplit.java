package org.jmc;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Texsplit
{
	final int width = 16;
	final int height = 16;
	final int rows = 16;
	final int cols = 16;
	
	public File terrain_file;
	
	public Texsplit(File terrain_file)
	{
		this.terrain_file=terrain_file;
	}
	
	public Texsplit()
	{
		this.terrain_file=null;
	}
	
	
	private BufferedImage loadImageFromFile(File file)
	{
		try
		{
			return ImageIO.read(terrain_file);			
		} 
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(MainWindow.main, "Cannot open terrain file:\n"+e.getMessage());
			return null;
		}
	}
	
	private BufferedImage loadImageFromMinecraft()
	{
		File minecraft_jar=new File(MainPanel.getMinecraftDir()+"/bin/minecraft.jar");
		if(!minecraft_jar.canRead())
		{
			JOptionPane.showMessageDialog(MainWindow.main, "Cannot open minecraft.jar file. Are you sure you have minecraft installed?");
			return null;
		}
				
		try {								
			JarInputStream jis=new JarInputStream(new FileInputStream(minecraft_jar));
			
			JarEntry entry=null;
			while((entry=jis.getNextJarEntry())!=null)
			{
				if(!entry.isDirectory() && entry.getName().equals("terrain.png"))
					break;
			}
			
			if(entry==null)
			{
				MainWindow.log("Couldn't find terrain.png in minecraft.jar!");
				return null;
			}
			
			return ImageIO.read(jis);
						
			
		} catch (IOException e) {
			MainWindow.log("Error reading minecraft jar file: "+e.getMessage());
			return null;
		}
	}
	
	public void splitTextures(File destination, boolean save_alpha)
	{
	
		if(destination==null)
			return;
		
		BufferedImage texture=null;
		if(terrain_file!=null)
			texture=loadImageFromFile(terrain_file);
		else 
			texture=loadImageFromMinecraft();
			
		if(texture==null) return;
	
		BufferedImage[] textures = new BufferedImage[rows * cols];
		int counter = 0;
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < cols; j++)
			{
				textures[(i * cols) + j] = texture.getSubimage(j * width, i * height, width, height);
				BufferedImage im = textures[(i * cols) + j];
				counter++;
				try
				{
					if (counter < rows*cols)
					{
						File f=new File(destination.getAbsolutePath() + "/" + counter + ".png");
						ImageIO.write(im, "png", f);				
						MainWindow.log("Saving texture to: "+f.getAbsolutePath());
						
						if(save_alpha)
						{
							convertToAlpha(im);
							File f2=new File(destination.getAbsolutePath() + "/" + counter + "_a.png");
							ImageIO.write(im, "png", f2);				
							MainWindow.log("Saving texture alpha to: "+f2.getAbsolutePath());
						}
					}
				} 
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(MainWindow.main, "Cannot save textures:\n"+e.getMessage());
					return;
				}
			}
		}
	}
	
	private int buffer[]=null;
	
	private void convertToAlpha(BufferedImage img)
	{
		int w=img.getWidth();
		int h=img.getHeight();
		int c=img.getColorModel().getPixelSize()/8;
		
		if(c!=4)
		{
			MainWindow.log("ERROR: texture is not 32-bit!");
		}
		
		if(buffer==null || buffer.length<w*h*c)
			buffer=new int[w*h*c];
		
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
}
