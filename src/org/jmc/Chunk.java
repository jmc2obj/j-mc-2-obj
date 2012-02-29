package org.jmc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;

import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Byte;
import org.jmc.NBT.TAG_Byte_Array;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Int;
import org.jmc.NBT.TAG_Int_Array;
import org.jmc.NBT.TAG_List;

public class Chunk {

	private TAG_Compound root;
	private Colors colors;

	private int pos_x,pos_z;

	private Set<Integer> transparent_blocks;

	private boolean is_anvil;

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

		this.is_anvil=is_anvil;

		root=(TAG_Compound) NBT_Tag.make(is);		
		is.close();

		colors = new Colors();

		TAG_Compound level = (TAG_Compound) root.getElement("Level");

		TAG_Int xPos=(TAG_Int) level.getElement("xPos");
		TAG_Int zPos=(TAG_Int) level.getElement("zPos");

		pos_x = xPos.value;
		pos_z = zPos.value;
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


	public BufferedImage getBlockImage()
	{
		int width = 4 * 16;
		int height = 4 * 16;
		BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = ret.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);

		byte BlockID=0;
		byte blocks[]=getBlockData();

		int ymax=0;
		if(is_anvil)
			ymax=blocks.length/(16*16);
		else 
			ymax=128;
		
		int x,y,z;
		for(z = 0; z < 16; z++)
		{
			for(x = 0; x < 16; x++)
			{
				for(y = 0; y < ymax; y++)
				{
					if(is_anvil)
						BlockID = blocks[x + (z * 16) + (y * 16) * 16];
					else
						BlockID = blocks[y + (z * 128) + (x * 128) * 16];

					if(BlockID > 0)
					{
						g.setColor(colors.getColor(BlockID));
						g.fillRect(x*4, z*4, 4, 4);
					}
				}
			}
		}


		return ret;
	}

	public BufferedImage getHeightImage()
	{
		int width=16*4;
		int height=16*4;
		BufferedImage ret=new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		Graphics2D g=ret.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);

		TAG_Compound level=(TAG_Compound)root.getElement("Level");
		
		TAG_Int_Array intHeightMap=null;
		TAG_Byte_Array byteHeightMap=null;
		
		if(is_anvil)
			intHeightMap=(TAG_Int_Array)level.getElement("HeightMap");
		else
			byteHeightMap=(TAG_Byte_Array)level.getElement("HeightMap");

		int h;
	
		for(int z=0,i=0; z<16; z++)
		{
			for(int x=0; x<16; x++,i++)
			{
				if(is_anvil)				
					h=intHeightMap.data[i];
				else
					h=byteHeightMap.data[i];
				
				h=h%256;
				
				g.setColor(new Color(h,h,h));
				g.fillRect(x*4, z*4, 4, 4);
			}
		}

		return ret;
	}

	private final int getValue(byte [] array, int x, int y, int z, int ymax)
	{
		if(x<0 || x>15 || y<0 || y>=ymax || z<0 || z>15) return -1;
		if(is_anvil)
			return array[x + (z * 16) + (y * 16) * 16];
		else
			return array[y + (z * 128) + (x * 128) * 16];
	}

	public OBJFile getOBJ(MTLFile material)
	{
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
		for(z = 0; z < 16; z++)
		{
			for(x = 0; x < 16; x++)
			{
				for(y = 0; y < ymax; y++)
				{						
					BlockID=getValue(blocks, x, y, z, ymax);

					if(BlockID==0) continue;

					if(isDrawable(BlockID,getValue(blocks,x,y+1,z,ymax)))
						drawside[0]=true; else drawside[0]=false;
					if(isDrawable(BlockID,getValue(blocks,x,y-1,z,ymax)))
						drawside[1]=true; else drawside[1]=false;
					if(isDrawable(BlockID,getValue(blocks,x-1,y,z,ymax)))
						drawside[2]=true; else drawside[2]=false;
					if(isDrawable(BlockID,getValue(blocks,x+1,y,z,ymax)))
						drawside[3]=true; else drawside[3]=false;
					if(isDrawable(BlockID,getValue(blocks,x,y,z-1,ymax)))
						drawside[4]=true; else drawside[4]=false;
					if(isDrawable(BlockID,getValue(blocks,x,y,z+1,ymax)))
						drawside[5]=true; else drawside[5]=false;

					ret.addCube(x, y, z, BlockID, drawside);						
				}									
			}
		}		

		return ret;
	}

	private boolean isDrawable(int block_id, int neighbour_id)
	{
		if(block_id==8 && neighbour_id!=0) return false;
		if(block_id==9 && neighbour_id!=0) return false;			

		if(transparent_blocks.contains(neighbour_id))
			return true;

		return false;
	}
}
