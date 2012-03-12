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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jmc.OBJFile.MeshType;
import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Byte;
import org.jmc.NBT.TAG_Byte_Array;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Float;
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
	 * Collection of IDs of transparent blocks.
	 * These blocks allow us to see what's behind them.
	 * Non-transparent blocks make the sides of neighboring blocks not render.
	 */
	private Set<Integer> transparent_blocks;
	/**
	 * A map of mesh types for given block IDs.
	 */
	private Map<Integer, MeshType> mesh_types;

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
		transparent_blocks=new TreeSet<Integer>();
		transparent_blocks.add(-1);
		transparent_blocks.add(0);
		transparent_blocks.add(8);
		transparent_blocks.add(9);
		transparent_blocks.add(20);
		transparent_blocks.add(26);
		transparent_blocks.add(27);
		transparent_blocks.add(30);
		transparent_blocks.add(31);
		transparent_blocks.add(32);
		transparent_blocks.add(34);
		transparent_blocks.add(37);
		transparent_blocks.add(38);
		transparent_blocks.add(39);
		transparent_blocks.add(40);
		transparent_blocks.add(50);
		transparent_blocks.add(51);
		transparent_blocks.add(52);
		transparent_blocks.add(53);
		transparent_blocks.add(55);
		transparent_blocks.add(59);
		transparent_blocks.add(63);

		mesh_types=new TreeMap<Integer, OBJFile.MeshType>();
		mesh_types.put(1, MeshType.BLOCK);
		mesh_types.put(2, MeshType.BLOCK);
		mesh_types.put(3, MeshType.BLOCK);
		mesh_types.put(4, MeshType.BLOCK);
		mesh_types.put(5, MeshType.BLOCK);
		mesh_types.put(7, MeshType.BLOCK);
		mesh_types.put(8, MeshType.BLOCK);
		mesh_types.put(9, MeshType.BLOCK);
		mesh_types.put(10, MeshType.BLOCK);//LAVA F
		mesh_types.put(11, MeshType.BLOCK);//LAVA S
		mesh_types.put(12, MeshType.BLOCK);		
		mesh_types.put(13, MeshType.BLOCK);
		mesh_types.put(14, MeshType.BLOCK);
		mesh_types.put(15, MeshType.BLOCK);
		mesh_types.put(16, MeshType.BLOCK);
		mesh_types.put(17, MeshType.BLOCK);
		mesh_types.put(18, MeshType.BLOCK);//LEAVES(alpha)
		mesh_types.put(19, MeshType.BLOCK);
		mesh_types.put(20, MeshType.BLOCK);//GLASS(alpha)
		mesh_types.put(21, MeshType.BLOCK);
		mesh_types.put(22, MeshType.BLOCK);
		mesh_types.put(23, MeshType.BLOCK);
		mesh_types.put(24, MeshType.BLOCK);
		mesh_types.put(25, MeshType.BLOCK);
		mesh_types.put(35, MeshType.BLOCK);//WOOL
		mesh_types.put(41, MeshType.BLOCK);
		mesh_types.put(42, MeshType.BLOCK);
		mesh_types.put(43, MeshType.BLOCK);
		mesh_types.put(45, MeshType.BLOCK);
		mesh_types.put(46, MeshType.BLOCK);
		mesh_types.put(47, MeshType.BLOCK);
		mesh_types.put(48, MeshType.BLOCK);
		mesh_types.put(49, MeshType.BLOCK);
		mesh_types.put(52, MeshType.BLOCK);//SPAWNER(alpha)
		mesh_types.put(54, MeshType.BLOCK);
		mesh_types.put(56, MeshType.BLOCK);
		mesh_types.put(57, MeshType.BLOCK);
		mesh_types.put(58, MeshType.BLOCK);
		mesh_types.put(60, MeshType.BLOCK);
		mesh_types.put(61, MeshType.BLOCK);
		mesh_types.put(62, MeshType.BLOCK);
		mesh_types.put(73, MeshType.BLOCK);
		mesh_types.put(74, MeshType.BLOCK);
		mesh_types.put(79, MeshType.BLOCK);//ICE(alpha?)
		mesh_types.put(80, MeshType.BLOCK);
		mesh_types.put(81, MeshType.BLOCK);
		mesh_types.put(82, MeshType.BLOCK);
		mesh_types.put(84, MeshType.BLOCK);
		mesh_types.put(86, MeshType.BLOCK);
		mesh_types.put(87, MeshType.BLOCK);
		mesh_types.put(88, MeshType.BLOCK);
		mesh_types.put(89, MeshType.BLOCK);
		mesh_types.put(91, MeshType.BLOCK);
		mesh_types.put(95, MeshType.BLOCK);
		mesh_types.put(97, MeshType.BLOCK);
		mesh_types.put(98, MeshType.BLOCK);
		mesh_types.put(103, MeshType.BLOCK);
		mesh_types.put(110, MeshType.BLOCK);
		mesh_types.put(112, MeshType.BLOCK);
		mesh_types.put(121, MeshType.BLOCK);
		mesh_types.put(123, MeshType.BLOCK);
		mesh_types.put(124, MeshType.BLOCK);		
		
		mesh_types.put(53, MeshType.STAIRS);//wood
		mesh_types.put(67, MeshType.STAIRS);//cobble
		mesh_types.put(108, MeshType.STAIRS);//brick
		mesh_types.put(109, MeshType.STAIRS);//stone brick
		mesh_types.put(114, MeshType.STAIRS);//nether brick
		



		this.is_anvil=is_anvil;

		root=(TAG_Compound) NBT_Tag.make(is);		
		is.close();

		colors = new Colors();

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
	private Blocks getBlocks()
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

	/**
	 * Private method for retrieving integer values from the array containing block IDs.
	 * @param array the block id array
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param ymax height of chunk
	 * @return ID of the block at given coordinates
	 */
	private final int getValue(int [] array, int x, int y, int z, int ymax)
	{
		if(x<0 || x>15 || y<0 || y>=ymax || z<0 || z>15) return -1;
		if(is_anvil)
			return array[x + (z * 16) + (y * 16) * 16];
		else
			return array[y + (z * 128) + (x * 128) * 16];
	}
	
	/**
	 * Private method for retrieving byte values from the array containing block data.
	 * @param array the block data array
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param ymax height of chunk
	 * @return meta-data of the block at given coordinates
	 */
	private final byte getValue(byte [] array, int x, int y, int z, int ymax)
	{
		if(x<0 || x>15 || y<0 || y>=ymax || z<0 || z>15) return -1;
		if(is_anvil)
			return array[x + (z * 16) + (y * 16) * 16];
		else
			return array[y + (z * 128) + (x * 128) * 16];
	}

	/**
	 * Creates and retrieves an OBJ file object.
	 * @param material MTL file object used in this OBJ file generation
	 * @param bounds X and Z limits in player coordinates
	 * @param ymin height level above which the export is performed
	 * @return OBJ file object
	 */
	public OBJFile getOBJ(MTLFile material, Rectangle bounds, int ymin)
	{
		int xmin=bounds.x-pos_x*16;
		int zmin=bounds.y-pos_z*16;
		int xmax=bounds.x+bounds.width-pos_x*16;
		int zmax=bounds.y+bounds.height-pos_z*16;

		if(xmin>15 || zmin>15 || xmax<0 || zmax<0) return null;

		if(xmin<0) xmin=0;
		if(zmin<0) zmin=0;
		if(xmax>15) xmax=15;
		if(zmax>15) zmax=15;

		OBJFile ret=new OBJFile("chunk."+pos_x+"."+pos_z,material);

		boolean drawside[]=new boolean[6];

		int BlockID;
		byte BlockData;
		Blocks bd=getBlocks();
		int blocks[]=bd.id;
		byte data[]=bd.data;

		int ymax=0;
		if(is_anvil)
			ymax=blocks.length/(16*16);
		else 
			ymax=128;

		int x,y,z;
		for(z = zmin; z <= zmax; z++)
		{
			for(x = xmin; x <= xmax; x++)
			{
				for(y = ymin; y < ymax; y++)
				{						
					BlockID=getValue(blocks, x, y, z, ymax);
					BlockData=getValue(data, x, y, z, ymax);

					if(BlockID==0) continue;

					if(y==ymax-1 || isDrawable(BlockID,getValue(blocks,x,y+1,z,ymax)))
						drawside[0]=true; else drawside[0]=false;
					if(y==ymin || isDrawable(BlockID,getValue(blocks,x,y-1,z,ymax)))
						drawside[1]=true; else drawside[1]=false;
					if(x==xmin || isDrawable(BlockID,getValue(blocks,x-1,y,z,ymax)))
						drawside[2]=true; else drawside[2]=false;
					if(x==xmax || isDrawable(BlockID,getValue(blocks,x+1,y,z,ymax)))
						drawside[3]=true; else drawside[3]=false;
					if(z==zmin || isDrawable(BlockID,getValue(blocks,x,y,z-1,ymax)))
						drawside[4]=true; else drawside[4]=false;
					if(z==zmax || isDrawable(BlockID,getValue(blocks,x,y,z+1,ymax)))
						drawside[5]=true; else drawside[5]=false;

					MeshType mt=mesh_types.get(BlockID);
					if(mt!=null)
					{
						switch(mt)
						{
						case BLOCK:
							ret.addCube(x, y, z, BlockID, BlockData, drawside,1.0f,1.0f,1.0f);
							break;
						case STAIRS:
							ret.addStairs(x, y, z, BlockID, BlockData, drawside);
							break;
						default:
						}
					}
				}									
			}
		}		

		return ret;
	}

	/**
	 * Private method used for checking if the given block ID is drawable 
	 * from the point of view of of a neighboring block.
	 * @param block_id block id of block being checked
	 * @param neighbour_id block id of its neighbor
	 * @return is it drawable
	 */
	private boolean isDrawable(int block_id, int neighbour_id)
	{
		Color nc=colors.getColor(neighbour_id,(byte) 0);
		MeshType nm=mesh_types.get(neighbour_id);

		if(block_id==8 && nc!=null && nm==MeshType.BLOCK) return false;
		if(block_id==9 && nc!=null && nm==MeshType.BLOCK) return false;			

		if(transparent_blocks.contains(neighbour_id) || nc==null || nm!=MeshType.BLOCK)
			return true;

		//TODO: this is linked to not drawing unknown chunks - remove when removing other comment
		if(colors.getColor(block_id,(byte) 0)==null)
			return true;

		return false;
	}
}
