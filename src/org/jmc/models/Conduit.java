package org.jmc.models;

import java.util.HashMap;

import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for Buttons.
 */
public class Conduit extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		// Splitting the conduit 16/16 texture into the four 6/6 textures
		UV[] uv1 = new UV[] { new UV(2/16f,8/16f), new UV(2/16f,14/16f), new UV(8/16f,14/16f), new UV(8/16f,8/16f) };
		UV[] uv2 = new UV[] { new UV(8/16f,8/16f), new UV(8/16f,14/16f), new UV(14/16f,14/16f), new UV(14/16f,8/16f) };
		UV[] uv3 = new UV[] { new UV(2/16f,2/16f), new UV(2/16f,8/16f), new UV(8/16f,8/16f), new UV(8/16f,2/16f) };
		UV[] uv4 = new UV[] { new UV(8/16f,2/16f), new UV(8/16f,8/16f), new UV(14/16f,8/16f), new UV(14/16f,2/16f) };
		
		UV[][] uvSides = new UV[][] { uv1, uv2, uv3, uv4, uv3, uv1 };
	
		boolean[] drawSides = new boolean[] {true,true,true,true,true,true};		
		
		addBox(obj, x-3/16f, y-3/16f, z-3/16f, x+3/16f, y+3/16f, z+3/16f, null, getMtlSides(data, biome), uvSides, drawSides);
	}

}
