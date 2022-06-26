package org.jmc.threading;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;

import org.jmc.BlockData;
import org.jmc.Chunk;
import org.jmc.Chunk.Blocks;
import org.jmc.ChunkDataBuffer;
import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Int;
import org.jmc.registry.NamespaceID;
import org.jmc.util.EmptyList;

public class ThreadChunkDeligate {

	private final ChunkDataBuffer chunkBuffer;
	
	private Point currChunkPoint;
	private Blocks currChunkBlocks;
	private final Rectangle xzBoundaries;
	private final Rectangle xyBoundaries;
	private final Map<Point,Blocks> auxChunks;
	
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
	
	public boolean isInBounds(int x, int y, int z) {
		int xmin, xmax, ymin, ymax, zmin, zmax;
		xmin = xyBoundaries.x;
		xmax = xmin + xyBoundaries.width - 1;
		ymin = xyBoundaries.y;
		ymax = ymin + xyBoundaries.height - 1;
		zmin = xzBoundaries.y;
		zmax = zmin + xzBoundaries.height - 1;
		
		return x >= xmin && x <= xmax && y >= ymin && y <= ymax && z >= zmin && z <= zmax;
	}

	@CheckForNull
	private Blocks getBlocks(Point p) {
		if (!p.equals(currChunkPoint)) {
			Blocks blocks = auxChunks.get(p);
			if (blocks == null) {
				blocks = chunkBuffer.getBlocks(p);
				auxChunks.put(p, blocks);
			}
			return blocks;
		}
		return currChunkBlocks;
	}
	
	@CheckForNull
	public BlockData getBlockData(int x, int y, int z)
	{
		if (!isInBounds(x, y, z)) {
			return new BlockData(NamespaceID.EXPORTEDGE);
		}
		Point chunk_p=Chunk.getChunkPos(x, z);
		Blocks blocks=getBlocks(chunk_p);
		
		if(blocks==null) return null;
		
		int rx=x-(chunk_p.x*16);
		int rz=z-(chunk_p.y*16);
		
		return blocks.getBlockData(rx, y, rz);
	}
	
	public int getBlockBiome(int x, int y, int z)
	{
		Point chunk_p=Chunk.getChunkPos(x, z);
		Blocks blocks=getBlocks(chunk_p);
		
		if(blocks==null) return 255;
		
		int rx=x-(chunk_p.x*16);
		int rz=z-(chunk_p.y*16);
		
		return blocks.getBiome(rx, y, rz);
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
		currChunkPoint = p;
		currChunkBlocks = chunkBuffer.getBlocks(p);
		auxChunks.clear();
	}
}
