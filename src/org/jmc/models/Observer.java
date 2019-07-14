package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


public class Observer extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		/*
		  The model is rendered facing south, then rotated
		*/
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		String dir = data.get("facing");
		
		switch (dir)
		{
			case "up": rotate.rotate(-90, 0, 180); break;
			case "down": rotate.rotate(90, 0, 180); break;
			case "north": rotate.rotate(0, 180, 0); break;
			case "west": rotate.rotate(0, 90, 0); break;
			case "east": rotate.rotate(0, -90, 0); break;
		}
		translate.translate(x, y, z);		
		rt = translate.multiply(rotate);

		addBox(obj,
				-0.5f, -0.5f, -0.5f,
				0.5f, 0.5f, 0.5f, 
				rt, 
				getMtlSides(data,biome), 
				null, 
				null);		
	}
}
