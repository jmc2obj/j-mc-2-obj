/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Byte;
import org.jmc.NBT.TAG_Byte_Array;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Int;
import org.jmc.NBT.TAG_List;
/**
 * Class describing a chunk. A chunk is a 16x16 group of blocks of 
 * varying heights (in Anvil) or 128 (in Region).
 * @author danijel
 *
 */
public class Chunk {

	/**
	 * Root of the loaded chunk structure.
	 */
	private TAG_Compound root;
	/**
	 * Color scheme used for the given chunk.
	 */
	private Colors colors;

	/**
	 * Position of chunk.
	 */
	private int pos_x,pos_z;

	/**
	 * Is the chunk type new Anvil or not.
	 * Used to determine how to properly analyze the data.
	 */
	private boolean is_anvil;

	
	/**
	 * 64x64 color image of topmost blocks in chunk.  
	 */
	private BufferedImage block_image;	
	/**
	 * 64x64 grey-scale image of height of topmost blocks.
	 */
	private BufferedImage height_image;

	/**
	 * Main constructor of chunks. 
	 * @param is input stream located at the place in the file where the chunk begins 
	 * @param is_anvil is the file new Anvil or old Region format
	 * @throws Exception throws errors while parsing the chunk
	 */
	public Chunk(InputStream is, boolean is_anvil) throws Exception
	{
		this.is_anvil=is_anvil;

		root=(TAG_Compound) NBT_Tag.make(is);		
		is.close();

		colors = MainWindow.settings.minecraft_colors;

		TAG_Compound level = (TAG_Compound) root.getElement("Level");

		TAG_Int xPos=(TAG_Int) level.getElement("xPos");
		TAG_Int zPos=(TAG_Int) level.getElement("zPos");

		pos_x = xPos.value;
		pos_z = zPos.value;

		block_image=null;
		height_image=null;
	}

	/**
	 * Gets X position of chunk.
	 * @return position in X coordinate
	 */
	public int getPosX()
	{
		return pos_x;
	}

	/**
	 * Gets Z position of chunk.
	 * @return position in Z coordinate
	 */
	public int getPosZ()
	{
		return pos_z;
	}

	/**
	 * Prints the description and contents of the chunk.
	 */
	public String toString()
	{
		return "Chunk:\n"+root.toString();
	}
	
	/**
	 * Small internal class defining the return values of getBlocks method. 
	 * @author danijel
	 *
	 */
	class Blocks
	{
		/**
		 * Main constructor.
		 * @param num number of blocks to allocate
		 */
		public Blocks(int num)
		{
			id=new int[num];
			data=new byte[num];
		}
		/**
		 * Block IDs.
		 */
		public int [] id;
		/**
		 * Block meta-data.
		 */
		public byte [] data;
	}

	/**
	 * Private method for retrieving block data from within chunk data structure.
	 * @return block data as a byte array
	 */
	public Blocks getBlocks()
	{		
		Blocks ret=null;
		
		if(is_anvil)
		{
			int ymax=0;
			TAG_Compound level = (TAG_Compound) root.getElement("Level");
			TAG_List sections = (TAG_List) level.getElement("Sections");
			for(NBT_Tag section: sections.elements)
			{
				TAG_Compound c_section = (TAG_Compound) section;					
				TAG_Byte yval = (TAG_Byte) c_section.getElement("Y");
				if(yval.value>ymax) ymax=yval.value;
			}

			ymax=(ymax+1)*16;
			
			ret=new Blocks(16*16*ymax);
			
			byte add1,add2;

			for(NBT_Tag section: sections.elements)
			{
				TAG_Compound c_section = (TAG_Compound) section;
				TAG_Byte_Array data = (TAG_Byte_Array) c_section.getElement("Data");
				TAG_Byte_Array blocks = (TAG_Byte_Array) c_section.getElement("Blocks");			
				TAG_Byte_Array add = (TAG_Byte_Array) c_section.getElement("AddBlocks");
				TAG_Byte yval = (TAG_Byte) c_section.getElement("Y");

				int base=yval.value*16*16*16;
				for(int i=0; i<blocks.data.length; i++)
					ret.id[base+i]=blocks.data[i];
				
				if(add!=null)
				{
					for(int i=0; i<add.data.length; i++)
					{
						add1=(byte) (add.data[i]&0x0f);
						add2=(byte) (add.data[i]>>4);
						ret.id[base+2*i]+=(add1<<8);
						ret.id[base+2*i+1]+=(add2<<8);
						//TODO: not sure if this works until there are block IDs higher than 256 limit
					}
				}
				
				for(int i=0; i<data.data.length; i++)
				{
					add1=(byte) (data.data[i]&0x0f);
					add2=(byte) (data.data[i]>>4);
					ret.data[base+2*i]=add1;
					ret.data[base+2*i+1]=add2;
				}
			}			
		}
		else
		{
			TAG_Compound level = (TAG_Compound) root.getElement("Level");
			TAG_Byte_Array blocks = (TAG_Byte_Array) level.getElement("Blocks");
			TAG_Byte_Array data = (TAG_Byte_Array) level.getElement("Data");
			
			byte add1,add2;
			ret=new Blocks(blocks.data.length);
						
			for(int i=0; i<blocks.data.length; i++)
				ret.id[i]=blocks.data[i];
			
			for(int i=0; i<data.data.length; i++)
			{
				add1=(byte) (data.data[i]&0x0f);
				add2=(byte) (data.data[i]>>4);
				ret.data[2*i]=add1;
				ret.data[2*i+1]=add2;
			}
		}
		
		return ret;
	}	

	/**
	 * Renders the block and height images.
	 */
	public void renderImages()
	{
		int width = 4 * 16;
		int height = 4 * 16;
		block_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		height_image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		Graphics2D gb = block_image.createGraphics();
		gb.setColor(Color.white);
		gb.fillRect(0, 0, width, height);

		Graphics2D gh = height_image.createGraphics();
		gb.setColor(Color.black);
		gb.fillRect(0, 0, width, height);

		int BlockID=0;
		byte BlockData=0;
		Color c;
		Blocks bd=getBlocks();
		
		/*for(int i=0; i<bd.id.length; i++)
			System.out.print(bd.id[i]+"/"+bd.data[i]+",");
		System.out.println();*/
		
		

		int ymax=0;
		if(is_anvil)
			ymax=bd.id.length/(16*16);
		else 
			ymax=128;


		int ids[]=new int[16*16];
		byte data[]=new byte[16*16];
		int himage[]=new int[16*16];

		int x,y,z;
		for(z = 0; z < 16; z++)
		{
			for(x = 0; x < 16; x++)
			{
				ids[z*16+x]=0;

				for(y = 0; y < ymax; y++)
				{
					if(is_anvil)
					{
						BlockID = bd.id[x + (z * 16) + (y * 16) * 16];
						BlockData = bd.data[x + (z * 16) + (y * 16) * 16];
					}
					else
					{
						BlockID = bd.id[y + (z * 128) + (x * 128) * 16];
						BlockData = bd.data[y + (z * 128) + (x * 128) * 16];
					}

					c=colors.getColor(BlockID,BlockData);
					if(c != null)
					{
						ids[z*16+x]=BlockID;
						data[z*16+x]=BlockData;
						himage[z*16+x]=y;
					}
				}
			}
		}


		for(z = 0; z < 16; z++)
		{
			for(x = 0; x < 16; x++)
			{				
				c=colors.getColor(ids[z*16+x],data[z*16+x]);
				if(c!=null)
				{
					gb.setColor(c);
					gb.fillRect(x*4, z*4, 4, 4);
				}				
			}
		}

		int h;
		for(z = 0; z < 16; z++)
		{
			for(x = 0; x < 16; x++)
			{				
				h=himage[z*16+x]%256;
				gh.setColor(new Color(h,h,h));
				gh.fillRect(x*4, z*4, 4, 4);				
			}
		}
	}

	/**
	 * Retrieves block image. Must run renderImages first!
	 * @return image of topmost blocks
	 */
	public BufferedImage getBlockImage()
	{
		return block_image;
	}

	/**
	 * Retrieves height image. Must run renderImages first!
	 * @return image of topmost block heights
	 */
	public BufferedImage getHeightImage()
	{
		return height_image;
	}
	
	public boolean isAnvil()
	{
		return is_anvil;
	}
}