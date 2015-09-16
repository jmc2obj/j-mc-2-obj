package org.jmc.threading;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import org.jmc.Chunk;
import org.jmc.Chunk.Blocks;
import org.jmc.ChunkDataBuffer;
import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Int;
import org.jmc.util.EmptyList;

public class ThreadChunkDeligate {

	private ChunkDataBuffer chunkBuffer;
	
	private Point cached_chunkp;
	private Blocks cached_chunkb;
	private boolean isAnvil;
	private Rectangle xzBoundaries;
	private Rectangle xyBoundaries;
	
	public ThreadChunkDeligate(ChunkDataBuffer chunkBuffer) {
		super();
		this.chunkBuffer = chunkBuffer;
		xzBoundaries = chunkBuffer.getXZBoundaries();
		xyBoundaries = chunkBuffer.getXYBoundaries();
	}
	
	public Rectangle getXZBoundaries()
	{
		return xzBoundaries;
	}
	
	public Rectangle getXYBoundaries()
	{
		return xyBoundaries;
	}

	private Blocks getBlocks(Point p)
	{
		if ((cached_chunkp == null) || (cached_chunkp.x != p.x) || (cached_chunkp.y != p.y))
		{
			cached_chunkp = p;
			cached_chunkb = chunkBuffer.getBlocks(p);
			isAnvil = chunkBuffer.isAnvil();
		}

		return cached_chunkb;
	}
	
	public short getBlockID(int x, int y, int z)
	{
		if(y<0) return 0;
		
		Point chunk_p=Chunk.getChunkPos(x, z);
		Blocks blocks=getBlocks(chunk_p);
		
		if(blocks==null) return 0;
		
		int rx=x-(chunk_p.x*16);
		int rz=z-(chunk_p.y*16);		
					
		if(isAnvil)
		{			
			if(y>=blocks.id.length/(16*16)) return 0;
			return blocks.id[rx + (rz * 16) + (y * 16) * 16];			
		}
		else
		{
			if(y>=128) return 0;
			return blocks.id[y + (rz * 128) + (rx * 128) * 16];			
		}
	}
	
	public byte getBlockData(int x, int y, int z)
	{
		if(y<0) return 0;
		
		Point chunk_p=Chunk.getChunkPos(x, z);		
		Blocks blocks=getBlocks(chunk_p);
		
		if(blocks==null) return 0;
		
		int rx=x-(chunk_p.x*16);
		int rz=z-(chunk_p.y*16);				
				
		if(isAnvil)
		{						
			if(y>=blocks.id.length/(16*16)) return 0;
			return blocks.data[rx + (rz * 16) + (y * 16) * 16];			
		}
		else
		{
			if(y>=128) return 0;
			return blocks.data[y + (rz * 128) + (rx * 128) * 16];			
		}
	}
	
	public byte getBlockBiome(int x, int z)
	{
		Point chunk_p=Chunk.getChunkPos(x, z);
		Blocks blocks=getBlocks(chunk_p);
		
		if(blocks==null) return (byte)255;
		
		int rx=x-(chunk_p.x*16);
		int rz=z-(chunk_p.y*16);		
					
		return blocks.biome[rx*16+rz];
	}
	
	public List<TAG_Compound> getEntities(int cx, int cz)
	{
		Blocks blocks=getBlocks(new Point(cx, cz));

		if(blocks==null)
			return new EmptyList<TAG_Compound>();
		
		return blocks.entities;
	}
	
	public List<TAG_Compound> getTileEntities(int cx, int cz)
	{
		Blocks blocks=getBlocks(new Point(cx, cz));

		if(blocks==null)
			return new EmptyList<TAG_Compound>();
		
		return blocks.tile_entities;
	}

	public TAG_Compound getTileEntity(int x, int y, int z)
	{
		Point chunk_p=Chunk.getChunkPos(x, z);
		
		for (TAG_Compound tag : getTileEntities(chunk_p.x, chunk_p.y))
		{
			int entx = Integer.MAX_VALUE;
			int enty = Integer.MAX_VALUE;
			int entz = Integer.MAX_VALUE;
			for (NBT_Tag subtag : tag.elements)
			{
				if (subtag.getName().equals("x")) entx = ((TAG_Int)subtag).value;
				if (subtag.getName().equals("y")) enty = ((TAG_Int)subtag).value;
				if (subtag.getName().equals("z")) entz = ((TAG_Int)subtag).value;
			}
			if (entx == x && enty == y && entz == z)
				return tag;
		}
		
		return null;
	}
}
