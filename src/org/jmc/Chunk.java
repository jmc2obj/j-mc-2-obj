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
import org.jmc.NBT.TAG_Int;
import org.jmc.NBT.TAG_List;

public class Chunk {

	private TAG_Compound root;
	private Colors colors;

	private int pos_x,pos_z;

	private Set<Integer> transparent_blocks;
	private Map<Integer, MeshType> mesh_types;

	private boolean is_anvil;

	private BufferedImage block_image, height_image;

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

	public int getPosX()
	{
		return pos_x;
	}

	public int getPosZ()
	{
		return pos_z;
	}

	public String toString()
	{
		return "Chunk:\n"+root.toString();
	}

	private byte [] getBlockData()
	{
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

			byte block_buf[]=new byte[16*16*ymax];

			for(NBT_Tag section: sections.elements)
			{
				TAG_Compound c_section = (TAG_Compound) section;
				TAG_Byte_Array blocks = (TAG_Byte_Array) c_section.getElement("Blocks");			
				TAG_Byte yval = (TAG_Byte) c_section.getElement("Y");

				System.arraycopy(blocks.data, 0, block_buf, yval.value*16*16*16 , 16*16*16);
			}

			return block_buf;
		}
		else
		{
			TAG_Compound level = (TAG_Compound) root.getElement("Level");
			TAG_Byte_Array blocks = (TAG_Byte_Array) level.getElement("Blocks");
			return blocks.data;			
		}
	}


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

		byte BlockID=0;
		Color c;
		byte blocks[]=getBlockData();

		int ymax=0;
		if(is_anvil)
			ymax=blocks.length/(16*16);
		else 
			ymax=128;


		byte image[]=new byte[16*16];
		int himage[]=new int[16*16];

		int x,y,z;
		for(z = 0; z < 16; z++)
		{
			for(x = 0; x < 16; x++)
			{
				image[z*16+x]=0;

				for(y = 0; y < ymax; y++)
				{
					if(is_anvil)
						BlockID = blocks[x + (z * 16) + (y * 16) * 16];
					else
						BlockID = blocks[y + (z * 128) + (x * 128) * 16];

					c=colors.getColor(BlockID);
					if(c != null)
					{
						image[z*16+x]=BlockID;
						himage[z*16+x]=y;
					}
				}
			}
		}


		for(z = 0; z < 16; z++)
		{
			for(x = 0; x < 16; x++)
			{				
				c=colors.getColor(image[z*16+x]);
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

	public BufferedImage getBlockImage()
	{
		return block_image;
	}

	public BufferedImage getHeightImage()
	{
		return height_image;
	}

	private final int getValue(byte [] array, int x, int y, int z, int ymax)
	{
		if(x<0 || x>15 || y<0 || y>=ymax || z<0 || z>15) return -1;
		if(is_anvil)
			return array[x + (z * 16) + (y * 16) * 16];
		else
			return array[y + (z * 128) + (x * 128) * 16];
	}

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
		byte blocks[] = getBlockData();

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
							ret.addCube(x, y, z, BlockID, drawside);
							break;
						default:
						}
					}
				}									
			}
		}		

		return ret;
	}

	private boolean isDrawable(int block_id, int neighbour_id)
	{
		Color nc=colors.getColor(neighbour_id);
		MeshType nm=mesh_types.get(neighbour_id);

		if(block_id==8 && nc!=null && nm==MeshType.BLOCK) return false;
		if(block_id==9 && nc!=null && nm==MeshType.BLOCK) return false;			

		if(transparent_blocks.contains(neighbour_id) || nc==null || nm!=MeshType.BLOCK)
			return true;

		//TODO: this is linked to not drawing unknown chunks - remove when removing other comment
		if(colors.getColor(block_id)==null)
			return true;

		return false;
	}
}
