package org.jmc;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmc.Chunk.Blocks;
import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Int;
import org.jmc.util.EmptyList;

public class ChunkDataBuffer {

	private Rectangle xzBoundaries;
	private Rectangle xyBoundaries;
	Map<Point,Blocks> chunks;
	
	private boolean is_anvil;
	
	public ChunkDataBuffer(int xmin, int xmax, int ymin, int ymax, int zmin, int zmax)
	{
		xzBoundaries = new Rectangle(xmin, zmin, xmax-xmin, zmax-zmin);
		xyBoundaries = new Rectangle(xmin, ymin, xmax-xmin, ymax-ymin);
		
		chunks=new HashMap<Point, Blocks>();
	}
	
	public void addChunk(Chunk chunk)
	{
		
		Point p=new Point();
		p.x=chunk.getPosX();
		p.y=chunk.getPosZ();
		chunks.put(p, chunk.getBlocks());
		
		is_anvil=chunk.isAnvil();
	}
	
	public void removeChunk(int x, int z)
	{
		Point p=new Point();
		p.x=x;
		p.y=z;
		chunks.remove(p);
	}
	
	public void removeAllChunks()
	{
		chunks.clear();
	}
	
	public int getChunkCount()
	{
		return chunks.size();
	}
	
	public boolean hasChunk(int x, int z)
	{
		Point p=new Point();
		p.x=x;
		p.y=z;
		return chunks.containsKey(p);
	}
	
	public Rectangle getXZBoundaries()
	{
		return xzBoundaries;
	}
	
	public Rectangle getXYBoundaries()
	{
		return xyBoundaries;
	}
	
	public short getBlockID(int x, int y, int z)
	{
		if(y<0) return 0;
		
		Point chunk_p=new Point();
		chunk_p.x=(int)Math.floor(x/16.0);
		chunk_p.y=(int)Math.floor(z/16.0);
		
		Blocks blocks=chunks.get(chunk_p);
		
		if(blocks==null) return 0;
		
		int rx=x-(chunk_p.x*16);
		int rz=z-(chunk_p.y*16);		
					
		if(is_anvil)
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
		
		Point chunk_p=new Point();
		chunk_p.x=(int)Math.floor(x/16.0);
		chunk_p.y=(int)Math.floor(z/16.0);
		
		Blocks blocks=chunks.get(chunk_p);
		
		if(blocks==null) return 0;
		
		int rx=x-(chunk_p.x*16);
		int rz=z-(chunk_p.y*16);				
				
		if(is_anvil)
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
		Point chunk_p=new Point();
		chunk_p.x=(int)Math.floor(x/16.0);
		chunk_p.y=(int)Math.floor(z/16.0);
		
		Blocks blocks=chunks.get(chunk_p);
		
		if(blocks==null) return (byte)255;
		
		int rx=x-(chunk_p.x*16);
		int rz=z-(chunk_p.y*16);		
					
		return blocks.biome[rx*16+rz];
	}
	
	public List<TAG_Compound> getEntities(int cx, int cz)
	{
		Point chunk_p=new Point();
		chunk_p.x=cx;
		chunk_p.y=cz;
		
		Blocks blocks=chunks.get(chunk_p);
		if(blocks==null)
			return new EmptyList<TAG_Compound>();
		
		return blocks.entities;
	}
	
	public List<TAG_Compound> getTileEntities(int cx, int cz)
	{
		Point chunk_p=new Point();
		chunk_p.x=cx;
		chunk_p.y=cz;
		
		Blocks blocks=chunks.get(chunk_p);
		if(blocks==null)
			return new EmptyList<TAG_Compound>();
		
		return blocks.tile_entities;
	}

	public TAG_Compound getTileEntity(int x, int y, int z)
	{
		int cx = (int)Math.floor(x/16.0);
		int cz = (int)Math.floor(z/16.0);
		
		for (TAG_Compound tag : getTileEntities(cx, cz))
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
