package org.jmc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;

import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Byte;
import org.jmc.NBT.TAG_Byte_Array;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_IntArray;
import org.jmc.NBT.TAG_List;

  //induce conflict :)

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
	
	public BufferedImage getBlocks()
	{
		int width = 4 * 16;
		int height = 4 * 16;
		BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g = ret.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		
		TAG_Compound level = (TAG_Compound) root.getElement("Level");
		TAG_List sections = (TAG_List) level.getElement("Sections");
		for(NBT_Tag section: sections.elements)
		{
			TAG_Compound c_section = (TAG_Compound) section;
			TAG_Byte_Array blocks = (TAG_Byte_Array) c_section.getElement("Blocks");
			TAG_Byte yval = (TAG_Byte) c_section.getElement("Y");
			
			int x,y,z;
			for(x = 0; x < 16; x++)
			{
				for(z = 0; z < 16; z++)
				{
					for(y = 0; y < 16; y++)
					{
						byte BlockID = blocks.data[y + (z * 16) + (x * 16) * 16];
						g.setColor(new Color(BlockID*2,BlockID*2,BlockID*2));
						if(BlockID == 0)
						{
							g.setColor(Color.white);
						}
						if(BlockID == 1)
						{
							g.setColor(Color.gray);
						}
						if(BlockID == 2)
						{
							g.setColor(Color.green);
						}
						if(BlockID == 3)
						{
							g.setColor(Color.red);
						}
						if(BlockID == 5)
						{
							g.setColor(Color.blue);
						}
						g.fillRect(x*4, z*4, 4,4);
						//System.out.println(BlockID);
					}
				}
			}
			
		}
		
		return ret;
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
