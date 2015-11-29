package org.jmc.models;

import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Generic model for liquids.
 */
public class Liquid extends BlockModel
{
	
	/**
	 * Gets the block height (from -0.5 to 0.5) corresponding to the fluid level.
	 * Fluid level goes from 0 (highest) to 7 (lowest)
	 */
	private static float heigthForLevel(int level)
	{
		return 0.375f - 0.12f * (float)level;
	}

    public static int min(int a, int b, int c, int d)
    {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }

    
	private boolean isSameLiquid(int otherBlockId)
	{
		if (blockId == otherBlockId)
			return true;
		if ((blockId == 9 || blockId == 8) && (otherBlockId == 9 || otherBlockId == 8))
			return true;
		if ((blockId == 11 || blockId == 10) && (otherBlockId == 11 || otherBlockId == 10))
			return true;
		return false;
	}

	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, byte data, byte biome)
	{
		int level = data & 7;
		boolean falling = (data & 8) != 0;
		// heights for each block corner
		float h_nw, h_ne, h_se, h_sw;
		// flow direction
		boolean flow_nw, flow_n, flow_ne, flow_e, flow_se, flow_s, flow_sw, flow_w;

		String mtl;
		boolean[] drawSides = drawSides(chunks, x, y, z);

		
		boolean same_up_nw = isSameLiquid(chunks.getBlockID(x-1, y+1, z-1));
		boolean same_up_n  = isSameLiquid(chunks.getBlockID(x, y+1, z-1));
		boolean same_up_ne = isSameLiquid(chunks.getBlockID(x+1, y+1, z-1));
		boolean same_up_e  = isSameLiquid(chunks.getBlockID(x+1, y+1, z));
		boolean same_up_se = isSameLiquid(chunks.getBlockID(x+1, y+1, z+1));
		boolean same_up_s  = isSameLiquid(chunks.getBlockID(x, y+1, z+1));
		boolean same_up_sw = isSameLiquid(chunks.getBlockID(x-1, y+1, z+1));
		boolean same_up_w  = isSameLiquid(chunks.getBlockID(x-1, y+1, z));
		
		boolean same_up = isSameLiquid(chunks.getBlockID(x, y+1, z));
		boolean same_down = isSameLiquid(chunks.getBlockID(x, y-1, z));

		if (same_up)
		{
			h_nw = h_ne = h_se = h_sw = 0.5f;
			flow_nw = flow_n = flow_ne = flow_e = flow_se = flow_s = flow_sw = flow_w = false;
		}
		else if (level == 0)
		{
			h_nw = h_ne = h_se = h_sw = heigthForLevel(0);
			if (same_up_nw || same_up_n || same_up_w) h_nw = 0.5f;
			if (same_up_ne || same_up_n || same_up_e) h_ne = 0.5f;
			if (same_up_se || same_up_s || same_up_e) h_se = 0.5f;
			if (same_up_sw || same_up_s || same_up_w) h_sw = 0.5f;

			flow_nw = flow_n = flow_ne = flow_e = flow_se = flow_s = flow_sw = flow_w = false;

			drawSides[0] = true;
		}
		else
		{
			boolean same_nw = isSameLiquid(chunks.getBlockID(x-1, y, z-1));
			boolean same_n  = isSameLiquid(chunks.getBlockID(x, y, z-1));
			boolean same_ne = isSameLiquid(chunks.getBlockID(x+1, y, z-1));
			boolean same_e  = isSameLiquid(chunks.getBlockID(x+1, y, z));
			boolean same_se = isSameLiquid(chunks.getBlockID(x+1, y, z+1));
			boolean same_s  = isSameLiquid(chunks.getBlockID(x, y, z+1));
			boolean same_sw = isSameLiquid(chunks.getBlockID(x-1, y, z+1));
			boolean same_w  = isSameLiquid(chunks.getBlockID(x-1, y, z));
			
			int lvl_nw = same_nw ? chunks.getBlockData(x-1, y, z-1) & 7 : 8;
			int lvl_n  = same_n ? chunks.getBlockData(x, y, z-1) & 7 : 8;
			int lvl_ne = same_ne ? chunks.getBlockData(x+1, y, z-1) & 7 : 8;
			int lvl_e  = same_e ? chunks.getBlockData(x+1, y, z) & 7 : 8;
			int lvl_se = same_se ? chunks.getBlockData(x+1, y, z+1) & 7 : 8;
			int lvl_s  = same_s ? chunks.getBlockData(x, y, z+1) & 7 : 8;
			int lvl_sw = same_sw ? chunks.getBlockData(x-1, y, z+1) & 7 : 8;
			int lvl_w  = same_w ? chunks.getBlockData(x-1, y, z) & 7 : 8;

			/*
			 * Calculate corner heights
			 * 
			 * TODO The logic here doesn't exactly match Minecraft's. Specifically, I can't 
			 * figure out how Minecraft determines the situations when the corners should be 
			 * drawn *lower* than the block's own level. The hack implemented here (the 
			 * conditions involving same_down) produces the correct result in some situations 
			 * but not all.
			 */
			if (same_up_nw || same_up_n || same_up_w)
				h_nw = 0.5f;
			else if (same_nw || same_n || same_w || !same_down)
				h_nw = heigthForLevel(min(level, lvl_nw, lvl_n, lvl_w));
			else
				h_nw = heigthForLevel(7);

			if (same_up_ne || same_up_n || same_up_e)
				h_ne = 0.5f;
			else if (same_ne || same_n || same_e || !same_down)
				h_ne = heigthForLevel(min(level, lvl_ne, lvl_n, lvl_e));
			else
				h_ne = heigthForLevel(7);

			if (same_up_se || same_up_s || same_up_e)
				h_se = 0.5f;
			else if (same_se || same_s || same_e || !same_down)
				h_se = heigthForLevel(min(level, lvl_se, lvl_s, lvl_e));
			else
				h_se = heigthForLevel(7);
			
			if (same_up_sw || same_up_s || same_up_w)
				h_sw = 0.5f;
			else if (same_sw || same_s || same_w || !same_down)
				h_sw = heigthForLevel(min(level, lvl_sw, lvl_s, lvl_w));
			else
				h_sw = heigthForLevel(7);
			
			/*
			 * Calculate flow direction
			 */
			// diagonal flows: when we're the middle block in a 3-block L-shaped flow
			boolean f_nw = ((same_up_s || lvl_w - lvl_s == 2) && lvl_w - level == 1) || ((same_up_e || lvl_n - lvl_e == 2) && lvl_n - level == 1);
			boolean f_ne = ((same_up_s || lvl_e - lvl_s == 2) && lvl_e - level == 1) || ((same_up_w || lvl_n - lvl_w == 2) && lvl_n - level == 1);
			boolean f_se = ((same_up_n || lvl_e - lvl_n == 2) && lvl_e - level == 1) || ((same_up_w || lvl_s - lvl_w == 2) && lvl_s - level == 1);
			boolean f_sw = ((same_up_n || lvl_w - lvl_n == 2) && lvl_w - level == 1) || ((same_up_e || lvl_s - lvl_e == 2) && lvl_s - level == 1);

			// straight flows: when we're the middle block in a 3-block flow
			boolean f_n = (same_up_s || lvl_n - lvl_s == 2) && lvl_n - level == 1;
			boolean f_e = (same_up_w || lvl_e - lvl_w == 2) && lvl_e - level == 1;
			boolean f_s = (same_up_n || lvl_s - lvl_n == 2) && lvl_s - level == 1;
			boolean f_w = (same_up_e || lvl_w - lvl_e == 2) && lvl_w - level == 1;
			if (!(f_n||f_e||f_s||f_w))
			{
				f_n = (same_up_s || lvl_n > lvl_s) && lvl_n > level;
				f_e = (same_up_w || lvl_e > lvl_w) && lvl_e > level;
				f_s = (same_up_n || lvl_s > lvl_n) && lvl_s > level;
				f_w = (same_up_e || lvl_w > lvl_e) && lvl_w > level;
			}
			
			// cancel out diagonal flows at 90deg angles
			flow_nw = f_nw && !(f_sw || f_ne);
			flow_n  = f_n;
			flow_ne = f_ne && !(f_nw || f_se);
			flow_e  = f_e;
			flow_se = f_se && !(f_ne || f_sw);
			flow_s  = f_s;
			flow_sw = f_sw && !(f_nw || f_se);
			flow_w  = f_w;

			
			drawSides[0] = true;
		}
		
		mtl = (level != 0 || falling) ? materials.get(data,biome)[1] : materials.get(data,biome)[0];

		Vertex[] vertices = new Vertex[4];
		UV[] uv = new UV[4];
		
		if (drawSides[0])
		{	// top
			if (flow_nw)
			{
				 uv[0] = new UV(1.2071f,0.5f);
				 uv[1] = new UV(0.5f,1.2071f);
				 uv[2] = new UV(-0.2071f,0.5f);
				 uv[3] = new UV(0.5f,-0.2071f);
			}
			else if (flow_ne)
			{
				 uv[0] = new UV(0.5f,1.2071f);
				 uv[1] = new UV(-0.2071f,0.5f);
				 uv[2] = new UV(0.5f,-0.2071f);
				 uv[3] = new UV(1.2071f,0.5f);
			}
			else if (flow_se)
			{
				 uv[0] = new UV(-0.2071f,0.5f);
				 uv[1] = new UV(0.5f,-0.2071f);
				 uv[2] = new UV(1.2071f,0.5f);
				 uv[3] = new UV(0.5f,1.2071f);
			}
			else if (flow_sw)
			{
				 uv[0] = new UV(0.5f,-0.2071f);
				 uv[1] = new UV(1.2071f,0.5f);
				 uv[2] = new UV(0.5f,1.2071f);
				 uv[3] = new UV(-0.2071f,0.5f);
			}
			else if (flow_n)
			{
				 uv[0] = new UV(1,1);
				 uv[1] = new UV(0,1);
				 uv[2] = new UV(0,0);
				 uv[3] = new UV(1,0);
			}
			else if (flow_s)
			{
				 uv[0] = new UV(0,0);
				 uv[1] = new UV(1,0);
				 uv[2] = new UV(1,1);
				 uv[3] = new UV(0,1);
			}
			else if (flow_w)
			{
				 uv[0] = new UV(1,0);
				 uv[1] = new UV(1,1);
				 uv[2] = new UV(0,1);
				 uv[3] = new UV(0,0);
			}
			else if (flow_e)
			{
				 uv[0] = new UV(0,1);
				 uv[1] = new UV(0,0);
				 uv[2] = new UV(1,0);
				 uv[3] = new UV(1,1);
			}
			else
			{
				 uv[0] = new UV(0,0);
				 uv[1] = new UV(1,0);
				 uv[2] = new UV(1,1);
				 uv[3] = new UV(0,1);
			}
			vertices[0] = new Vertex(x-0.5f, y+h_sw, z+0.5f);
			vertices[1] = new Vertex(x+0.5f, y+h_se, z+0.5f);
			vertices[2] = new Vertex(x+0.5f, y+h_ne, z-0.5f);
			vertices[3] = new Vertex(x-0.5f, y+h_nw, z-0.5f);
			obj.addFace(vertices, uv, null, mtl);
		}
		if (drawSides[1])
		{	// front
			vertices[0] = new Vertex(x+0.5f, y-0.5f, z-0.5f); uv[0] = new UV(1,0);
			vertices[1] = new Vertex(x-0.5f, y-0.5f, z-0.5f); uv[1] = new UV(0,0);
			vertices[2] = new Vertex(x-0.5f, y+h_nw, z-0.5f); uv[2] = new UV(0,0.5f+h_ne);
			vertices[3] = new Vertex(x+0.5f, y+h_ne, z-0.5f); uv[3] = new UV(1,0.5f+h_nw);
			obj.addFace(vertices, uv, null, mtl);
		}
		if (drawSides[2])
		{	// back
			vertices[0] = new Vertex(x-0.5f, y-0.5f, z+0.5f); uv[0] = new UV(0,0);
			vertices[1] = new Vertex(x+0.5f, y-0.5f, z+0.5f); uv[1] = new UV(1,0);
			vertices[2] = new Vertex(x+0.5f, y+h_se, z+0.5f); uv[2] = new UV(1,0.5f+h_se);
			vertices[3] = new Vertex(x-0.5f, y+h_sw, z+0.5f); uv[3] = new UV(0,0.5f+h_sw);
			obj.addFace(vertices, uv, null, mtl);
		}
		if (drawSides[3])
		{	// left
			vertices[0] = new Vertex(x-0.5f, y-0.5f, z-0.5f); uv[0] = new UV(0,0);
			vertices[1] = new Vertex(x-0.5f, y-0.5f, z+0.5f); uv[1] = new UV(1,0);
			vertices[2] = new Vertex(x-0.5f, y+h_sw, z+0.5f); uv[2] = new UV(1,0.5f+h_sw);
			vertices[3] = new Vertex(x-0.5f, y+h_nw, z-0.5f); uv[3] = new UV(0,0.5f+h_nw);
			obj.addFace(vertices, uv, null, mtl);
		}
		if (drawSides[4])
		{	// right
			vertices[0] = new Vertex(x+0.5f, y-0.5f, z+0.5f); uv[0] = new UV(1,0);
			vertices[1] = new Vertex(x+0.5f, y-0.5f, z-0.5f); uv[1] = new UV(0,0);
			vertices[2] = new Vertex(x+0.5f, y+h_ne, z-0.5f); uv[2] = new UV(0,0.5f+h_se);
			vertices[3] = new Vertex(x+0.5f, y+h_se, z+0.5f); uv[3] = new UV(1,0.5f+h_ne);
			obj.addFace(vertices, uv, null, mtl);
		}
		if (drawSides[5])
		{	// bottom
			vertices[0] = new Vertex(x+0.5f, y-0.5f, z+0.5f);
			vertices[1] = new Vertex(x-0.5f, y-0.5f, z+0.5f);
			vertices[2] = new Vertex(x-0.5f, y-0.5f, z-0.5f);
			vertices[3] = new Vertex(x+0.5f, y-0.5f, z-0.5f);
			obj.addFace(vertices, null, null, mtl);
		}
	}

}
