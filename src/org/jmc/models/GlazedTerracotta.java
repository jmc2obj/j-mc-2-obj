package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;

import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;

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
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		String dir = data.state.get("facing");

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		// Drawn as the "north" facing glazed terracotta block first, then rotated.
		switch (dir)
		{
			case "south": rotate = Transform.rotation(0, 180, 0); break;
			case "west": rotate = Transform.rotation(0, -90, 0); break;
			case "east": rotate = Transform.rotation(0, 90, 0); break;
		}
		translate = Transform.translation(x, y, z);		
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
