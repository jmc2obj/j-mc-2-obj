package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJFile;


/**
 * Model for ground snow.
 */
public class Snow extends BoxModel
{

	@Override
	public void addModel(OBJFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		boolean[] drawSides = drawSides(chunks, x, y, z);
		drawSides[0] = true;
		
		if (data > 8)
			data -= 8;
		float ys = -0.5f;
		float ye = ys + (data+1) / 8.0f;

		addBox(obj,
				x - 0.5f, y + ys, z - 0.5f,
				x + 0.5f, y + ye, z + 0.5f, 
				drawSides, 
				getMtlSides(data));
	}

}
