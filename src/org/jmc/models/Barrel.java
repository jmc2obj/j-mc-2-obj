package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


public class Barrel extends BlockModel
{
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		
		Direction dir = data.state.getDirection("facing", Direction.NORTH);

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		switch (dir)
		{
			case UP: rotate = Transform.rotation(0, 180, 0); break;
			case DOWN: rotate = Transform.rotation(180, 180, 0); break;
			case NORTH: rotate = Transform.rotation(-90, 0, 0); break;
			case SOUTH: rotate = Transform.rotation(-90, 0, 180); break;
			case WEST: rotate = Transform.rotation(-90, 0, 90); break;
			case EAST: rotate = Transform.rotation(-90, 0, -90); break;
		}
		translate = Transform.translation(x, y, z);
		rt = translate.multiply(rotate);
		
		addBox(obj, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, rt, getMtlSides(data,biome), null, null);
	}

}