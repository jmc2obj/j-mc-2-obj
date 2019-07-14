package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


public class Barrel extends BlockModel
{
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		
		String dir = data.get("facing");

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		switch (dir)
		{
			case "uo": rotate.rotate(0, 180, 0); break;
			case "down": rotate.rotate(180, 180, 0); break;
			case "north": rotate.rotate(-90, 0, 0); break;			
			case "south": rotate.rotate(-90, 0, 180); break;
			case "west": rotate.rotate(-90, 0, 90); break;
			case "east": rotate.rotate(-90, 0, -90); break;		
		}
		translate.translate(x, y, z);		
		rt = translate.multiply(rotate);
		
		addBox(obj, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, rt, getMtlSides(data,biome), null, drawSides(chunks, x, y, z));
	}

}