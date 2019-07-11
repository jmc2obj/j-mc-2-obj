package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Generic model for cube blocks.
 */
public class CubeFacing extends BlockModel
{
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		Transform trans = new Transform();
		Transform rotate = new Transform();
		
		Direction dir = data.getDirection("facing");
		
		trans.translate(x, y, z);
		rotate.rotate(dir);
		
		addBox(obj,
				-0.5f, -0.5f, -0.5f,
				0.5f, 0.5f, 0.5f, 
				trans.multiply(rotate), 
				getMtlSides(data,biome), 
				null, 
				drawSides(chunks, x, y, z));
	}

}
