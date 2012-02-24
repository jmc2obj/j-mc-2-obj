package org.jmc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_IntArray;

public class Chunk {

	TAG_Compound root;
	
	public Chunk(InputStream is) throws Exception
	{
		root=(TAG_Compound) NBT_Tag.make(is);
		
		is.close();
	}
	
	public String toString()
	{
		return "Chunk:\n"+root.toString();
	}
	
	public BufferedImage getHeightImage()
	{
		int width=4*16;
		int height=4*16;
		BufferedImage ret=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g=ret.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		
		TAG_Compound level=(TAG_Compound)root.getElement("Level");
		TAG_IntArray heightMap=(TAG_IntArray)level.getElement("HeightMap");
		
		int i=0,h;
		for(int z=0; z<16; z++)
			for(int x=0; x<16; x++,i++)
			{
				h=heightMap.data[i];			
				g.setColor(new Color(h,h,h));
				g.fillRect(x*4, z*4, 4, 4);
			}
		
		return ret;
	}
}
