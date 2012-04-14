package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;


/**
 * Model for enchantment tables.
 * 
 * TODO model the floating book
 */
public class EnchantTable extends BoxModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		boolean[] drawSides = drawSides(chunks, x, y, z);
		drawSides[0] = true;
		
		addBox(obj,
				x-0.5f, y-0.5f, z-0.5f,
				x+0.5f, y+0.25f, z+0.5f, 
				drawSides, 
				getMtlSides(data));
	}

}
