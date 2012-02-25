package org.jmc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Byte_Array;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_IntArray;
import org.jmc.NBT.TAG_List;

public class Chunk {

	TAG_Compound root;
	Colors colors;
	
	public Chunk(InputStream is) throws Exception
	{
		root=(TAG_Compound) NBT_Tag.make(is);
		colors = new Colors();
		
		is.close();
	}
	
	public String toString()
	{
		return "Chunk:\n"+root.toString();
	}
	
	public BufferedImage getBlocks()
	{
		int width = 16;
		int height = 16;
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
			TAG_Byte_Array data = (TAG_Byte_Array) c_section.getElement("Data");
			TAG_Byte_Array tiles = (TAG_Byte_Array) c_section.getElement("TileEntities");
			TAG_Byte_Array light = (TAG_Byte_Array) c_section.getElement("SkyLight");
			//TAG_Byte yval = (TAG_Byte) c_section.getElement("Y");
			
			int x,y,z;
			for(z = 0; z < 16; z++)
			{
				for(x = 0; x < 16; x++)
				{
					for(y = 0; y < 16; y++)
					{
						byte BlockID = blocks.data[x + (z * 16) + (y * 16) * 16];
						byte DataID = data.data[(x + (z * 16) + (y * 16) * 16)/2];
						byte LightID = light.data[(x + (z * 16) + (y * 16) * 16)/2];
						
						if(BlockID > 0)
						{
							g.setColor(colors.getColor(BlockID));
							g.fillRect(x, z, 1, 1);
							if(DataID > 0)
							{
								//System.out.println(BlockID + "\t" + DataID + "\t" + LightID + "\t" + (x + (z * 16) + (y * 16) * 16));
							}
						}
					}
				}
			}
			
		}
		
		return ret;
	}
	
	public BufferedImage getHeightImage()
	{
		int width=16;
		int height=16;
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
				g.fillRect(x, z, 1, 1);
			}
		
		return ret;
	}
}
