package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.UV;


/**
 * Model for slime blocks.
 */
public class Slime extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome)
	{
		String[] mtlSides = getMtlSides(data, biome);
		boolean[] drawSides = drawSides(chunks, x, y, z);
		
		UV[] uv = { new UV(3f/16f, 3f/16f), new UV(13f/16f, 3f/16f), new UV(13f/16f, 13f/16f), new UV(3f/16f, 13f/16f) };
		UV[][] innerUv = { uv, uv, uv, uv, uv, uv };
		
		// outer box
		addBox(obj,
				x - 0.5f, y - 0.5f, z - 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f, 
				null, 
				mtlSides, 
				null, 
				drawSides);

		// inner box
		addBox(obj,
				x - 0.3125f, y - 0.3125f, z - 0.3125f,
				x + 0.3125f, y + 0.3125f, z + 0.3125f, 
				null, 
				mtlSides, 
				innerUv, 
				drawSides);
	}

}
