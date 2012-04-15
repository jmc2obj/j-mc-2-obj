package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.UV;


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
		
		UV[] uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,12/16f), new UV(0,12/16f) };
		UV[][] uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };

		addBox(obj,
				x-0.5f, y-0.5f, z-0.5f,
				x+0.5f, y+0.25f, z+0.5f, 
				null, 
				getMtlSides(data), 
				uvSides, 
				drawSides);
	}

}
