package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for farmland.
 */
public class Farmland extends BlockModel
{
	
	@Override
	protected String[] getMtlSides(BlockData data, int biome)
	{
		String[] abbrMtls = materials.get(data,biome);

		String[] mtlSides = new String[6];
		mtlSides[0] = data.get("moisture").equals("7") ? abbrMtls[0] : abbrMtls[1];
		mtlSides[1] = abbrMtls[2];
		mtlSides[2] = abbrMtls[2];
		mtlSides[3] = abbrMtls[2];
		mtlSides[4] = abbrMtls[2];
		mtlSides[5] = abbrMtls[2];
		return mtlSides;
	}
	

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		boolean[] drawSides = drawSides(chunks, x, y, z);
		
		UV[] uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,15/16f), new UV(0,15/16f) };
		UV[][] uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };

		addBox(obj,
				x-0.5f, y-0.5f, z-0.5f,
				x+0.5f, y+0.4375f, z+0.5f, 
				null, 
				getMtlSides(data,biome), 
				uvSides, 
				drawSides);
	}

}
