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
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Byte;
import org.jmc.NBT.TAG_Byte_Array;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Int;
import org.jmc.NBT.TAG_Int_Array;
import org.jmc.NBT.TAG_List;
import org.jmc.NBT.TAG_Long_Array;
import org.jmc.NBT.TAG_String;
import org.jmc.util.IDConvert;
import org.jmc.util.Log;
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

	public static Point getChunkPos(int x, int z)
	{
		Point p = new Point();

		p.x = (x<0) ? ((x-15)/16) : (x/16);
		p.y = (z<0) ? ((z-15)/16) : (z/16);

		return p;
	}

	/**
	 * Small internal class defining the return values of getBlocks method. 
	 * @author danijel
	 *
	 */
	public class Blocks
	{
		/**
		 * Main constructor.
		 * @param num number of blocks to allocate
		 */
		public Blocks(int block_num, int biome_num)
		{
			id=new short[block_num];
			data=new byte[block_num];
			biome=new int[biome_num];
			Arrays.fill(biome, (byte)255);
			entities=new LinkedList<TAG_Compound>();
			tile_entities=new LinkedList<TAG_Compound>();
		}
		/**
		 * Block IDs.
		 */
		public short [] id;
		/**
		 * Block meta-data.
		 */
		public byte [] data;

		/**
		 * Biome IDSs (only XZ axes).
		 */
		public int [] biome;

		/**
		 * Entities.
		 */
		public List<TAG_Compound> entities;

		/**
		 * Tile entities.
		 */
		public List<TAG_Compound> tile_entities;
	}

	/**
	 * Private method for retrieving block data from within chunk data structure.
	 * @return block data as a byte array
	 */
	public Blocks getBlocks()
	{		
		Blocks ret=null;		
		TAG_Compound level = (TAG_Compound) root.getElement("Level");

		if(is_anvil)// can we detect if the level format is >= 1.13?
		{
			int ymax=0;			
			TAG_List sections = (TAG_List) level.getElement("Sections");
			for(NBT_Tag section: sections.elements)
			{
				TAG_Compound c_section = (TAG_Compound) section;					
				TAG_Byte yval = (TAG_Byte) c_section.getElement("Y");
				if(yval.value>ymax) ymax=yval.value;
			}

			ymax=(ymax+1)*16;

			ret=new Blocks(16*16*ymax,16*16);

			for(NBT_Tag section: sections.elements)
			{
				TAG_Compound c_section = (TAG_Compound) section;
				TAG_List tagPalette = (TAG_List) c_section.getElement("Palette");
				TAG_Long_Array tagBlockStates = (TAG_Long_Array) c_section.getElement("BlockStates");
				TAG_Int_Array tagBiomes = (TAG_Int_Array) level.getElement("Biomes");
				//TAG_Byte_Array tagAdd = (TAG_Byte_Array) c_section.getElement("Add");
				TAG_Byte yval = (TAG_Byte) c_section.getElement("Y");

				int base=yval.value*16*16*16;
				
				int blockBits = Math.max(tagBlockStates.data.length / 4096, 4); // Minimum of 4 bits.
				for (int i = 0; i < tagBlockStates.data.length; i++) {
					long blockState = tagBlockStates.data[i];
					for (int bit = 0; bit < 64/*64 bit longs*/; bit += blockBits) {
						long blockPid = blockState >> bit;// probably a horrifically bad way of doing this but i suck at bit manipulation.
						long bitMask = 0;
						for (int b = 0; b < blockBits ; b++) {
							bitMask |= 1L << b;
						}
						blockPid &= bitMask;
						
						TAG_Compound blockTag = (TAG_Compound)tagPalette.elements[(int)blockPid];
						TAG_String blockName = (TAG_String)blockTag.getElement("Name");
						//Log.debug(String.format("blockPid = %d, blockName = %s", blockPid, blockName.value));
						
						ret.id[base+i+(bit/blockBits)] = (short) IDConvert.strToInt(blockName.value);
						//ret.data[] = //data from nbt tags??? probably needs special treatment for each block type
					}
				}
				/*
				for(int i=0; i<tagBlocks.data.length; i++)
					ret.id[base+i] = (short)(tagBlocks.data[i]&0xff);	// convert signed to unsigned

				if(tagAdd!=null)
				{
					for(int i=0; i<tagAdd.data.length; i++)
					{
						short add = (short)(tagAdd.data[i]&0xff);	// convert signed to unsigned
						short add1 = (short)(add&0x0f);
						short add2 = (short)(add>>4);
						ret.id[base+2*i] += (add1<<8);
						ret.id[base+2*i+1] += (add2<<8);
					}
				}

				for(int i=0; i<tagData.data.length; i++)
				{
					byte add1=(byte)(tagData.data[i]&0x0f);
					byte add2=(byte)(tagData.data[i]>>4);
					ret.data[base+2*i]=add1;
					ret.data[base+2*i+1]=add2;
				}*/

				if(tagBiomes!=null)
				{
					for(int i=0; i<tagBiomes.data.length; i++)
						ret.biome[i]=tagBiomes.data[i];
				}
				else
				{
					for(int i=0; i<ret.biome.length; i++)
						ret.biome[i]=1; //if biomes are missing set everything to plains
				}
			}			
		}
		else
		{
			TAG_Byte_Array blocks = (TAG_Byte_Array) level.getElement("Blocks");
			TAG_Byte_Array data = (TAG_Byte_Array) level.getElement("Data");

			byte add1,add2;
			ret=new Blocks(blocks.data.length,256);

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

		TAG_List entities = (TAG_List) level.getElement("Entities");
		if(entities!=null && entities.elements.length>0)
		{
			for(int i=0; i<entities.elements.length; i++)
			{
				ret.entities.add((TAG_Compound)entities.elements[i]);
			}
		}


		TAG_List tile_entities = (TAG_List) level.getElement("TileEntities");
		if(tile_entities!=null && tile_entities.elements.length>0)
		{
			for(int i=0; i<tile_entities.elements.length; i++)
			{
				ret.tile_entities.add((TAG_Compound)tile_entities.elements[i]);
			}
		}

		return ret;
	}	

	/**
	 * Renders the block and height images.
	 * @param floor floor boundary
	 * @param ceiling ceiling boundary
	 */
	public void renderImages(int floor, int ceiling, boolean fastmode)
	{
		int width = 4 * 16;
		int height = 4 * 16;
		block_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		if(!fastmode)
			height_image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		Graphics2D gb = block_image.createGraphics();
		gb.setColor(Color.white);
		gb.fillRect(0, 0, width, height);

		Graphics2D gh = null;
		if(!fastmode)
			gh = height_image.createGraphics();
		gb.setColor(Color.black);
		gb.fillRect(0, 0, width, height);

		short blockID=0;
		byte blockData=0;
		int blockBiome=0;
		Color c;
		Blocks bd=getBlocks();		

		int ymax=0;
		if(is_anvil)
			ymax=bd.id.length/(16*16);
		else 
			ymax=128;

		if(floor>ymax)
			return;
		if(ceiling>ymax)
			ceiling=ymax;
		if(ceiling<1)
			ceiling=1;
		if(floor>=ceiling)
			floor=ceiling-1;


		short ids[]=new short[16*16];
		byte data[]=new byte[16*16];
		int biome[]=new int[16*16];
		int himage[]=null;
		if(!fastmode)
			himage=new int[16*16];

		int x,y,z;
		for(z = 0; z < 16; z++)
		{
			for(x = 0; x < 16; x++)
			{
				ids[z*16+x]=0;

				for(y = floor; y < ceiling; y++)
				{
					blockBiome = bd.biome[x*16+z]; 

					if(is_anvil)
					{
						blockID = bd.id[x + (z * 16) + (y * 16) * 16];
						blockData = bd.data[x + (z * 16) + (y * 16) * 16];
					}
					else
					{
						blockID = bd.id[y + (z * 128) + (x * 128) * 16];
						blockData = bd.data[y + (z * 128) + (x * 128) * 16];
					}

					if(blockID != 0)
					{
						ids[z*16+x]=blockID;
						data[z*16+x]=blockData;
						biome[z*16+x]=blockBiome;
						if(!fastmode)
							himage[z*16+x]=y;
					}
				}
			}
		}


		for(z = 0; z < 16; z++)
		{
			for(x = 0; x < 16; x++)
			{
				blockID = ids[z*16+x];
				blockData = data[z*16+x];
				blockBiome = biome[z*16+x];
				if(blockID != 0)
				{
					c = BlockTypes.get(blockID).getPreviewColor(blockData,blockBiome);
					if(c!=null)
					{
						gb.setColor(c);
						gb.fillRect(x*4, z*4, 4, 4);
					}
				}
			}
		}

		if(!fastmode){
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
