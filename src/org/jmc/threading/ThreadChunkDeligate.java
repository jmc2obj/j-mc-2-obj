package org.jmc.threading;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmc.BlockData;
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
	private Map<Point,Blocks> auxChunks;
	
	public ThreadChunkDeligate(ChunkDataBuffer chunkBuffer) {
		super();
		this.chunkBuffer = chunkBuffer;
		xzBoundaries = chunkBuffer.getXZBoundaries();
		xyBoundaries = chunkBuffer.getXYBoundaries();
		auxChunks = new HashMap<Point, Blocks>();
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
		if (cached_chunkp == null) {
			cached_chunkp = p;
			cached_chunkb = chunkBuffer.getBlocks(p);
		}
		if ((cached_chunkp.x != p.x) || (cached_chunkp.y != p.y))
		{
			if (auxChunks.containsKey(p)){
				return auxChunks.get(p);
			} else {
				Blocks blks = chunkBuffer.getBlocks(p);
				auxChunks.put(p, blks);
				return blks;
			}
		}
		
		return cached_chunkb;
	}
	
	public String getBlockID(int x, int y, int z)
	{
		return getBlockData(x, y, z).id;
	}
	
	public BlockData getBlockData(int x, int y, int z)
	{
		if(y<0) return new BlockData("minecraft:air");
		
		Point chunk_p=Chunk.getChunkPos(x, z);
		Blocks blocks=getBlocks(chunk_p);
		
		if(blocks==null) return new BlockData("minecraft:air");
		
		int rx=x-(chunk_p.x*16);
		int rz=z-(chunk_p.y*16);
		
		if(isAnvil)
		{
			if(y>=blocks.size/(16*16)) return new BlockData("minecraft:air");
			return blocks.data[rx + (rz * 16) + (y * 16) * 16];
		}
		else
		{
			if(y>=128) return new BlockData("minecraft:air");
			return blocks.data[y + (rz * 128) + (rx * 128) * 16];
		}
	}
	
	public int getBlockBiome(int x, int y, int z)
	{
		Point chunk_p=Chunk.getChunkPos(x, z);
		Blocks blocks=getBlocks(chunk_p);
		
		if(blocks==null) return 255;
		
		int rx=x-(chunk_p.x*16);
		int rz=z-(chunk_p.y*16);
		
		if(y>=blocks.size/(16*16)) return 1;
		return blocks.biome[rx + (rz * 16) + (y * 16) * 16];
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
	
	public void setCurrentChunk(Point p) {
		auxChunks.clear();
		cached_chunkp = p;
		cached_chunkb = chunkBuffer.getBlocks(p);
		isAnvil = chunkBuffer.isAnvil();
	}
}
