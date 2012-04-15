package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;


/**
 * Generic model for liquids.
 * 
 * TODO: model running liquids
 */
public class Liquid extends BlockModel
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
		boolean[] drawSides = drawSides(chunks, x, y, z);
		float ye;

		if (chunks.getBlockID(x, y+1, z) == blockId)
		{
			ye = 0.5f;
		}
		else
		{
			ye = 0.375f;
			drawSides[0] = true;
		}
		
		addBox(obj,
				x - 0.5f, y - 0.5f, z - 0.5f,
				x + 0.5f, y + ye, z + 0.5f, 
				null, 
				getMtlSides(data), 
				null, 
				drawSides);
	}

}
