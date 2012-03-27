package org.jmc;

import java.awt.Rectangle;

import javax.swing.JOptionPane;

import org.jmc.Chunk.Blocks;

public class ChunkDataBuffer {

	private Rectangle boundaries;
	private int export_ymin,export_ymax;
	private short blocks[];
	private byte data[];
	
	public ChunkDataBuffer(Rectangle block_boundaries, int ymin, int ymax) throws OutOfMemoryError
	{
		boundaries=block_boundaries;
		export_ymin=ymin;
		export_ymax=ymax;
		
		int size=boundaries.width*boundaries.height*(ymax-ymin);
		
		if(size>34952533)
		{
			int ret=JOptionPane.showConfirmDialog(null, "WARNING: You are trying to allocate a map that is over 100MB in size.\nAre you sure you want to do that?");
			if(ret!=JOptionPane.YES_OPTION)
			{
				throw new OutOfMemoryError("User didn't want to allocate too much memory!");
			}
		}
		
		blocks=new short[size];
		data=new byte[size];
	}
	
	public void addChunk(Chunk chunk)
	{
		int pos_x=chunk.getPosX()*16;
		int pos_z=chunk.getPosZ()*16;
		int xmin=boundaries.x-pos_x;
		int zmin=boundaries.y-pos_z;
		int xmax=boundaries.x+boundaries.width-pos_x;
		int zmax=boundaries.y+boundaries.height-pos_z;
		int ymin=export_ymin;
		int ymax=export_ymax;

		if(xmin>16 || zmin>16 || xmax<0 || zmax<0) return;

		if(xmin<0) xmin=0;
		if(zmin<0) zmin=0;
		if(xmax>16) xmax=16;
		if(zmax>16) zmax=16;
		
		
		boolean is_anvil=chunk.isAnvil();
		
		Blocks chunk_blocks=chunk.getBlocks();
		
		
		int ymax_f;
		if(is_anvil)
			ymax_f=chunk_blocks.id.length/(16*16);
		else 
			ymax_f=128;
		
		if(ymax>ymax_f)
			ymax=ymax_f;			

		int x,y,z,rx,rz,ry;
		int sz,sy;
		
		sz=boundaries.width;
		sy=sz*boundaries.height;
		
		for(z = zmin; z < zmax; z++)
		{
			for(x = xmin; x < xmax; x++)
			{
				for(y = ymin; y < ymax; y++)
				{					
					rx=pos_x+x-boundaries.x;
					rz=pos_z+z-boundaries.y;
					ry=y-ymin;
					
					if(is_anvil)
					{						
						blocks[ry*sy+rz*sz+rx]=chunk_blocks.id[x + (z * 16) + (y * 16) * 16];
						data[ry*sy+rz*sz+rx]=chunk_blocks.data[x + (z * 16) + (y * 16) * 16];
					}
					else
					{
						blocks[ry*sy+rz*sz+rx]=chunk_blocks.id[y + (z * 128) + (x * 128) * 16];
						data[ry*sy+rz*sz+rx]=chunk_blocks.data[y + (z * 128) + (x * 128) * 16];
					}
				}
			}
		}
	}
	
	public Rectangle getXZBoundaries()
	{
		return boundaries;
	}
	
	public Rectangle getXYBoundaries()
	{
		Rectangle ret=new Rectangle();
		ret.x=boundaries.x;
		ret.y=export_ymin;
		ret.width=boundaries.width;
		ret.height=export_ymax-export_ymin;
		return ret;
	}
	
	public short getBlockID(int x, int y, int z)
	{
		int rx=x-boundaries.x;
		int rz=z-boundaries.y;
		int ry=y-export_ymin;
		int sz=boundaries.width;
		int sy=sz*boundaries.height;
		return blocks[ry*sy+rz*sz+rx];
	}
	
	public byte getBlockData(int x, int y, int z)
	{
		int rx=x-boundaries.x;
		int rz=z-boundaries.y;
		int ry=y-export_ymin;
		int sz=boundaries.width;
		int sy=sz*boundaries.height;
		return data[ry*sy+rz*sz+rx];
	}
}
