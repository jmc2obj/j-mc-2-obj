package org.jmc;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import org.jmc.Chunk.Blocks;

public class ChunkDataBuffer {

	private Rectangle xzBoundaries;
	private Rectangle xyBoundaries;
	Map<Point,Blocks> chunks;
	Map<Point, Integer> chunkUsers;
	
	private boolean is_anvil;

	public ChunkDataBuffer(int xmin, int xmax, int ymin, int ymax, int zmin, int zmax)
	{
		xzBoundaries = new Rectangle(xmin, zmin, xmax-xmin, zmax-zmin);
		xyBoundaries = new Rectangle(xmin, ymin, xmax-xmin, ymax-ymin);
		
		chunks=new HashMap<Point, Blocks>();
		chunkUsers=new HashMap<Point, Integer>();
	}
	
	public boolean addChunk(int x, int z)
	{
		Point p=new Point(x, z);
		
		synchronized (this) {
			if (chunks.containsKey(p)) {
				addChunkUser(p);
				return true;
			}
		}
		
		Chunk chunk;
		try {// if chunk exists
			Region region = Region.findRegion(Options.worldDir, Options.dimension, x, z);
			if (region == null)
				return false;
	
			chunk = region.getChunk(x, z);
			if (chunk == null)
				return false;
		} catch (Exception e) {
			return false;
		}
		
		Blocks blocks = chunk.getBlocks();
		synchronized (this) {
			chunks.put(p, blocks);
			addChunkUser(p);
			
			is_anvil=chunk.isAnvil();
			return true;
		}
	}
	
	public synchronized void removeChunk(int x, int z)
	{
		Point p=new Point(x, z);
		Integer currUsers = chunkUsers.get(p);
		if (currUsers != null && currUsers > 0) {
			currUsers--;
		} else {
			currUsers = 0;
		}
		chunkUsers.put(p, currUsers);
		if (currUsers == 0) {
			chunks.remove(p);
		}
	}
	
	private synchronized int addChunkUser(Point p)
	{
		Integer currUsers = chunkUsers.get(p);
		if (currUsers != null) {
			currUsers++;
		} else {
			currUsers = 1;
		}
		chunkUsers.put(p, currUsers);
		return currUsers;
	}
	
	public synchronized void removeAllChunks()
	{
		chunks.clear();
		chunkUsers.clear();
	}
	
	public synchronized int getChunkCount()
	{
		return chunks.size();
	}

	public synchronized Blocks getBlocks(Point p)
	{
		return chunks.get(p);
	}
	
	public Rectangle getXZBoundaries()
	{
		return xzBoundaries;
	}
	
	public Rectangle getXYBoundaries()
	{
		return xyBoundaries;
	}

	public synchronized boolean isAnvil() {
		return is_anvil;
	}
}
