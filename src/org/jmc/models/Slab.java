package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;


/**
 * Model for slabs (aka half-blocks).
 */
public class Slab extends BoxModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		boolean[] drawSides = drawSides(chunks, x, y, z);
		float ys, ye;
		
		if ((data & 0x8) == 0)
		{
			// slab occupies the lower half
			drawSides[0] = true;
			ys = -0.5f;
			ye = 0.0f;
		}
		else
		{
			// slab occupies the upper half
			drawSides[5] = true;
			ys = 0.0f;
			ye = 0.5f;
		}
		
		addBox(obj,
				x - 0.5f, y + ys, z - 0.5f,
				x + 0.5f, y + ye, z + 0.5f, 
				drawSides, 
				getMtlSides(data));
	}

}
