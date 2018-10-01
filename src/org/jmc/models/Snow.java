package org.jmc.models;

import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for ground snow.
 */
public class Snow extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, byte data, int biome)
	{
		boolean[] drawSides = drawSides(chunks, x, y, z);
		drawSides[0] = true;
		
		if (data > 8)
			data -= 8;
		float height = (data+1) / 8.0f;

		UV[] uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,height), new UV(0,height) };
		UV[][] uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
		
		addBox(obj,
				x-0.5f, y-0.5f, z-0.5f,
				x+0.5f, y-0.5f+height, z+0.5f, 
				null, 
				getMtlSides(data,biome), 
				uvSides, 
				drawSides);
	}

}
