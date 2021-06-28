package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


public class Jigsaw extends BlockModel
{
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		
		String orientation = data.state.getOrDefault("orientation", "up_north");

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		switch (orientation)
		{
			case "up_north": rotate.rotate(0, 0, 0); break;
			case "up_east": rotate.rotate(0, 90, 0); break;
			case "up_south": rotate.rotate(0, 180, 0); break;
			case "up_west": rotate.rotate(0, 270, 0); break;
			case "down_north": rotate.rotate(0, 0, 180); break;
			case "down_east": rotate.rotate(0, 90, 180); break;
			case "down_south": rotate.rotate(0, 180, 180); break;
			case "down_west": rotate.rotate(0, 270, 180); break;
			case "south_up": rotate.rotate(90, 0, 0); break;
			case "west_up": rotate.rotate(90, 0, 90); break;
			case "north_up": rotate.rotate(90, 0, 180); break;
			case "east_up": rotate.rotate(90, 0, 270); break;
		}
		translate.translate(x, y, z);
		rt = translate.multiply(rotate);
		
		addBox(obj, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, rt, getMtlSides(data,biome), null, null);
	}

}