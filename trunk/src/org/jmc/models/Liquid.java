package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Vertex;


/**
 * Generic model for liquids.
 * 
 * TODO textures
 * 
 * TODO The logic for determining the height of the corners of flowing blocks doesn't exactly
 * match Minecraft's. Specifically, I can't figure out how Minecraft determines the situations
 * when the corners should be drawn *lower* than the block's own level. The hack implemented 
 * here (the conditions involving same_down) produces the correct result in some situations 
 * but not all.
 */
public class Liquid extends BlockModel
{
	
	/**
	 * Gets the block height (from -0.5 to 0.5) corresponding to the fluid level.
	 * Fluid level goes form 0 (highest) to 7 (lowest)
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
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		String mtl = materials.get(data)[0];
		boolean[] drawSides = drawSides(chunks, x, y, z);

//		// XXX
//		if ((data & 8) != 0){
//			addBox(obj,
//					x - 0.25f, y - 0.25f, z - 0.25f,
//					x + 0.25f, y + 0.25f, z + 0.25f, 
//					null, 
//					new String[] {"unknown","unknown","unknown","unknown","unknown","unknown"}, 
//					null, 
//					null);
//			return;
//		}
		
		int level = data & 7;
		float h_nw, h_ne, h_se, h_sw;	// heights for each block corner

		
		if (isSameLiquid(chunks.getBlockID(x, y+1, z)))
		{
			h_nw = h_ne = h_se = h_sw = 0.5f;
		}
		else if (level == 0)
		{
			h_nw = h_ne = h_se = h_sw = heigthForLevel(0);
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
			
			boolean same_up_nw = isSameLiquid(chunks.getBlockID(x-1, y+1, z-1));
			boolean same_up_n  = isSameLiquid(chunks.getBlockID(x, y+1, z-1));
			boolean same_up_ne = isSameLiquid(chunks.getBlockID(x+1, y+1, z-1));
			boolean same_up_e  = isSameLiquid(chunks.getBlockID(x+1, y+1, z));
			boolean same_up_se = isSameLiquid(chunks.getBlockID(x+1, y+1, z+1));
			boolean same_up_s  = isSameLiquid(chunks.getBlockID(x, y+1, z+1));
			boolean same_up_sw = isSameLiquid(chunks.getBlockID(x-1, y+1, z+1));
			boolean same_up_w  = isSameLiquid(chunks.getBlockID(x-1, y+1, z));
			
			boolean same_down = isSameLiquid(chunks.getBlockID(x, y-1, z));
			
			int lvl_nw = same_nw ? chunks.getBlockData(x-1, y, z-1) & 7 : 7;
			int lvl_n  = same_n ? chunks.getBlockData(x, y, z-1) & 7 : 7;
			int lvl_ne = same_ne ? chunks.getBlockData(x+1, y, z-1) & 7 : 7;
			int lvl_e  = same_e ? chunks.getBlockData(x+1, y, z) & 7 : 7;
			int lvl_se = same_se ? chunks.getBlockData(x+1, y, z+1) & 7 : 7;
			int lvl_s  = same_s ? chunks.getBlockData(x, y, z+1) & 7 : 7;
			int lvl_sw = same_sw ? chunks.getBlockData(x-1, y, z+1) & 7 : 7;
			int lvl_w  = same_w ? chunks.getBlockData(x-1, y, z) & 7 : 7;

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
			
			drawSides[0] = true;
		}
		

		Vertex[] vertices = new Vertex[4];
		
		if (drawSides[0])
		{	// top
			vertices[0] = new Vertex(x-0.5f, y+h_sw, z+0.5f);
			vertices[1] = new Vertex(x+0.5f, y+h_se, z+0.5f);
			vertices[2] = new Vertex(x+0.5f, y+h_ne, z-0.5f);
			vertices[3] = new Vertex(x-0.5f, y+h_nw, z-0.5f);
			obj.addFace(vertices, null, null, mtl);
		}
		if (drawSides[1])
		{	// front
			vertices[0] = new Vertex(x+0.5f, y-0.5f, z-0.5f);
			vertices[1] = new Vertex(x-0.5f, y-0.5f, z-0.5f);
			vertices[2] = new Vertex(x-0.5f, y+h_nw, z-0.5f);
			vertices[3] = new Vertex(x+0.5f, y+h_ne, z-0.5f);
			obj.addFace(vertices, null, null, mtl);
		}
		if (drawSides[2])
		{	// back
			vertices[0] = new Vertex(x-0.5f, y-0.5f, z+0.5f);
			vertices[1] = new Vertex(x+0.5f, y-0.5f, z+0.5f);
			vertices[2] = new Vertex(x+0.5f, y+h_se, z+0.5f);
			vertices[3] = new Vertex(x-0.5f, y+h_sw, z+0.5f);
			obj.addFace(vertices, null, null, mtl);
		}
		if (drawSides[3])
		{	// left
			vertices[0] = new Vertex(x-0.5f, y-0.5f, z-0.5f);
			vertices[1] = new Vertex(x-0.5f, y-0.5f, z+0.5f);
			vertices[2] = new Vertex(x-0.5f, y+h_sw, z+0.5f);
			vertices[3] = new Vertex(x-0.5f, y+h_nw, z-0.5f);
			obj.addFace(vertices, null, null, mtl);
		}
		if (drawSides[4])
		{	// right
			vertices[0] = new Vertex(x+0.5f, y-0.5f, z+0.5f);
			vertices[1] = new Vertex(x+0.5f, y-0.5f, z-0.5f);
			vertices[2] = new Vertex(x+0.5f, y+h_ne, z-0.5f);
			vertices[3] = new Vertex(x+0.5f, y+h_se, z+0.5f);
			obj.addFace(vertices, null, null, mtl);
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
