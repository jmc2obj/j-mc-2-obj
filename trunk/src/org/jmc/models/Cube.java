package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJFile;


/**
 * Generic model for cube blocks.
 */
public class Cube extends BoxModel
{

	@Override
	public void addModel(OBJFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		addBox(obj,
				x - 0.5f, y - 0.5f, z - 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f, 
				drawSides(chunks, x, y, z), 
				getMtlSides(data));
	}

}
