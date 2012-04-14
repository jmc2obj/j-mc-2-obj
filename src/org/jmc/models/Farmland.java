package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;


/**
 * Model for farmland.
 */
public class Farmland extends BlockModel
{
	
	/** Expand the materials to the full 6 side definition used by addBox */
	private String[] getMtlSides(byte data)
	{
		String[] abbrMtls = materials.get(data);

		String[] mtlSides = new String[6];
		mtlSides[0] = data == 0 ? abbrMtls[0] : abbrMtls[1];
		mtlSides[1] = abbrMtls[2];
		mtlSides[2] = abbrMtls[2];
		mtlSides[3] = abbrMtls[2];
		mtlSides[4] = abbrMtls[2];
		mtlSides[5] = abbrMtls[2];
		return mtlSides;
	}
	

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		boolean[] drawSides = drawSides(chunks, x, y, z);
		
		addBox(obj,
				x-0.5f, y-0.5f, z-0.5f,
				x+0.5f, y+0.4375f, z+0.5f, 
				drawSides, 
				getMtlSides(data));
	}

}
