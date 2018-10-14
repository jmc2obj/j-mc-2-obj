package org.jmc.models;

import java.util.HashMap;

import org.jmc.geom.Transform;
import org.jmc.geom.UV;

import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;

/**
 * Model for piston blocks.
 */
public class GlazedTerracotta extends BlockModel
{
	
	protected UV[][] getUvSides()
	{
		UV[] uv1 = new UV[] { new UV(0,0), new UV(1,0), new UV(1,1), new UV(0,1) };
		UV[] uv2 = new UV[] { new UV(1,0), new UV(1,1), new UV(0,1), new UV(0,0) };
		UV[] uv3 = new UV[] { new UV(1,1), new UV(0,1), new UV(0,0), new UV(1,0) };
		UV[] uv4 = new UV[] { new UV(0,1), new UV(0,0), new UV(1,0), new UV(1,1) };
		
		                 // top, north, south, west, east, bottom 
		return new UV[][] { uv3, uv4,   uv2,   uv3,  uv1,  uv1 };
	}
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		String dir = data.get("facing");

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		// Drawn as the "north" facing glazed terracotta block first, then rotated.
		switch (dir)
		{
			case "south": rotate.rotate(0, 180, 0); break;
			case "west": rotate.rotate(0, -90, 0); break;
			case "east": rotate.rotate(0, 90, 0); break;
		}
		translate.translate(x, y, z);		
		rt = translate.multiply(rotate);

		addBox(obj,
				-0.5f, -0.5f, -0.5f,
				0.5f, 0.5f, 0.5f, 
				rt, 
				getMtlSides(data,biome), 
				getUvSides(), 
				null);		
	}

}
