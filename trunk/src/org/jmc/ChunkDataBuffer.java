package org.jmc;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.jmc.Chunk.Blocks;

public class ChunkDataBuffer {

	private Rectangle boundaries;
	private int export_ymin,export_ymax;
	Map<Point,Blocks> chunks;
	
	private boolean is_anvil;
	
	public ChunkDataBuffer(Rectangle block_boundaries, int ymin, int ymax)
	{
		boundaries=block_boundaries;
		export_ymin=ymin;
		export_ymax=ymax;
		
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
}
