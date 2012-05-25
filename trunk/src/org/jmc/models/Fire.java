package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;


/**
 * Model for fire
 */
public class Fire extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		boolean[] drawSides = new boolean[] { false, true, true, true, true, false};
		
		addBox(obj,
				x-0.49f, y-0.49f, z-0.49f,
				x+0.49f, y+0.49f, z+0.49f, 
				null, 
				getMtlSides(data), 
				null, 
				drawSides);
	}

}
