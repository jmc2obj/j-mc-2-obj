package org.jmc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texsplit
{
	final int width = 16;
	final int height = 16;
	final int rows = 16;
	final int cols = 16;
	
	public Texsplit()
	{
		try
		{
			BufferedImage terrain = ImageIO.read(new File("tex\\terrain.png"));
			splitTextures(terrain);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void splitTextures(BufferedImage texture)
	{
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
						boolean image = ImageIO.write(im, "png", new File("tex\\" + counter + ".png"));				
					}
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
