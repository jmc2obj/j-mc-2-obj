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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jmc.BlockInfo.Occlusion;
import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Byte;
import org.jmc.NBT.TAG_Byte_Array;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Int;
import org.jmc.NBT.TAG_Int_Array;
import org.jmc.NBT.TAG_List;
import org.jmc.NBT.TAG_Long_Array;
import org.jmc.NBT.TAG_String;
import org.jmc.models.None;
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
			id=new String[block_num];
			data=new ArrayList<HashMap<String, String>>(block_num);
			for (int i = 0; i < block_num; i++) 
				data.add(new HashMap<String, String>());
			biome=new int[biome_num];
			Arrays.fill(biome, (byte)255);
			entities=new LinkedList<TAG_Compound>();
			tile_entities=new LinkedList<TAG_Compound>();
		}
		/**
		 * Block IDs.
		 */
		public String [] id;
		/**
		 * Block meta-data.
		 */
		public List<HashMap<String, String>> data;

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
				
				int blockBits = Math.max((tagBlockStates.data.length * 64) / 4096, 4); // Minimum of 4 bits.
				BitSet blockStates = BitSet.valueOf(tagBlockStates.data);
				for (int i = 0; i < 4096; i++) {
					long blockPid;
					BitSet blockBitArr = blockStates.get(i*blockBits, (i+1)*blockBits);
					if (blockBitArr.length() < 1) {
						blockPid = 0;
					} else {
						blockPid = blockBitArr.toLongArray()[0];
					}
					
					TAG_Compound blockTag = (TAG_Compound)tagPalette.elements[(int)blockPid];
					TAG_String blockName = (TAG_String)blockTag.getElement("Name");
					//Log.debug(String.format("blockPid = %d, blockName = %s", blockPid, blockName.value));
					
					ret.id[base+i] = blockName.value;
					
					HashMap<String, String> data = new HashMap<String, String>();
					TAG_Compound propertiesTag = (TAG_Compound)blockTag.getElement("Properties");
					if (propertiesTag != null) {
						for (NBT_Tag tag : propertiesTag.elements) {
							TAG_String propTag = (TAG_String)tag;
							data.put(propTag.getName(), propTag.value);
						}
					}
					
					ret.data.set(base+i, data);//data from nbt tags??? probably needs special treatment for each block type
				}

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
				ret.id[i]=IDConvert.intToStr(blocks.data[i]);//TODO old format conversion

			for(int i=0; i<data.data.length; i++)
			{
				add1=(byte) (data.data[i]&0x0f);
				add2=(byte) (data.data[i]>>4);
				//ret.data[2*i]=add1;
				//ret.data[2*i+1]=add2;
				//TODO old format conversion
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

		String blockID="minecraft:air";
		HashMap<String, String> blockData=new HashMap<String, String>();
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


		String ids[]=new String[16*16];
		List<HashMap<String, String>> data=new ArrayList<HashMap<String, String>>(16*16);
		for (int i = 0; i < 16*16; i++)
			data.add(i, new HashMap<String, String>());
		int biome[]=new int[16*16];
		int himage[]=null;
		if(!fastmode)
			himage=new int[16*16];

		int x,y,z;
		for(z = 0; z < 16; z++)
		{
			for(x = 0; x < 16; x++)
			{
				ids[z*16+x]="minecraft:air";

				for(y = floor; y < ceiling; y++)
				{
					blockBiome = bd.biome[x*16+z]; 

					if(is_anvil)
					{
						blockID = bd.id[x + (z * 16) + (y * 16) * 16];
						blockData = bd.data.get(x + (z * 16) + (y * 16) * 16);
					}
					else
					{
						blockID = bd.id[y + (z * 128) + (x * 128) * 16];
						blockData = bd.data.get(y + (z * 128) + (x * 128) * 16);
					}

					if(blockID != null && !BlockTypes.get(blockID).getOcclusion().equals(Occlusion.NONE))
					{
						ids[z*16+x]=blockID;
						data.set(z*16+x, blockData);
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
				blockData = data.get(z*16+x);
				blockBiome = biome[z*16+x];
				
				if(blockID != null && BlockTypes.get(blockID).getModel().getClass() != None.class)
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
