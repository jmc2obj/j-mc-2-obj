package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.UV;


/**
 * Model for the end portal frame.
 * 
 * TODO model the inserted eye of ender
 */
public class EndPortalFrame extends BlockModel
{
	
	/** Expand the materials to the full 6 side definition used by addBox */
	private String[] getMtlSides(byte data)
	{
		String[] abbrMtls = materials.get(data);

		String[] mtlSides = new String[6];
		mtlSides[0] = abbrMtls[0];
		mtlSides[1] = abbrMtls[1];
		mtlSides[2] = abbrMtls[1];
		mtlSides[3] = abbrMtls[1];
		mtlSides[4] = abbrMtls[1];
		mtlSides[5] = abbrMtls[2];
		return mtlSides;
	}
	

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		boolean[] drawSides = drawSides(chunks, x, y, z);
		drawSides[0] = true;
		
		UV[] uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,13/16f), new UV(0,13/16f) };
		UV[][] uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };

		addBox(obj,
				x-0.5f, y-0.5f, z-0.5f,
				x+0.5f, y+0.3125f, z+0.5f, 
				null, 
				getMtlSides(data), 
				uvSides,
				drawSides);
	}

}
