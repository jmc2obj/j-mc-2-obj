package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


public class Barrel extends BlockModel
{
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		
		Direction dir = data.getDirection("facing", Direction.NORTH);

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		switch (dir)
		{
			case UP: rotate.rotate(0, 180, 0); break;
			case DOWN: rotate.rotate(180, 180, 0); break;
			case NORTH: rotate.rotate(-90, 0, 0); break;
			case SOUTH: rotate.rotate(-90, 0, 180); break;
			case WEST: rotate.rotate(-90, 0, 90); break;
			case EAST: rotate.rotate(-90, 0, -90); break;
		}
		translate.translate(x, y, z);
		rt = translate.multiply(rotate);
		
		addBox(obj, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, rt, getMtlSides(data,biome), null, drawSides(chunks, x, y, z));
	}

}