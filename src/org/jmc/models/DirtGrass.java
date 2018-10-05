package org.jmc.models;

import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for grass blocks that change when covered by snow.
 */
public class DirtGrass extends BlockModel
{
	
	protected String[] getMtlSides(byte data, int biome, boolean snow)
	{
		String[] abbrMtls = materials.get(data,biome);
		
		String[] mtlSides = new String[6];
		mtlSides[0] = abbrMtls[0];
		mtlSides[1] = abbrMtls[snow ? 2 : 1];
		mtlSides[2] = abbrMtls[snow ? 2 : 1];
		mtlSides[3] = abbrMtls[snow ? 2 : 1];
		mtlSides[4] = abbrMtls[snow ? 2 : 1];
		mtlSides[5] = abbrMtls[3];
		
		return mtlSides;
	}
	

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, byte data, int biome)
	{
		boolean snow = chunks.getBlockID(x, y+1, z).equals("minecraft:snow");
		
		addBox(obj,
				x - 0.5f, y - 0.5f, z - 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f, 
				null, 
				getMtlSides(data, biome, snow), 
				null, 
				drawSides(chunks, x, y, z));
	}
}
