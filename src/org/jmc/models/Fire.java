package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;


/**
 * Model for fire
 */
public class Fire extends BlockModel
{

	/** Expand the materials to the full 6 side definition used by addBox */
	private String[] getMtlSides(byte data)
	{
		String[] abbrMtls = materials.get(data);

		String[] mtlSides = new String[6];
		mtlSides[0] = abbrMtls[0];
		mtlSides[1] = abbrMtls[0];
		mtlSides[2] = abbrMtls[0];
		mtlSides[3] = abbrMtls[0];
		mtlSides[4] = abbrMtls[0];
		mtlSides[5] = abbrMtls[0];
		return mtlSides;
	}

	
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
