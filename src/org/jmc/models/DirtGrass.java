package org.jmc.models;

import javax.annotation.Nonnull;

import org.jmc.BlockData;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for grass blocks that change when covered by snow.
 */
public class DirtGrass extends BlockModel
{
	
	@Nonnull
	protected NamespaceID[] getMtlSides(BlockData data, int biome, boolean snow)
	{
		NamespaceID[] abbrMtls = materials.get(data.state,biome);
		
		NamespaceID[] mtlSides = new NamespaceID[6];
		mtlSides[0] = abbrMtls[0];
		mtlSides[1] = abbrMtls[snow ? 2 : 1];
		mtlSides[2] = abbrMtls[snow ? 2 : 1];
		mtlSides[3] = abbrMtls[snow ? 2 : 1];
		mtlSides[4] = abbrMtls[snow ? 2 : 1];
		mtlSides[5] = abbrMtls[3];
		
		return mtlSides;
	}
	

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		BlockData above = chunks.getBlockData(x, y+1, z);
		boolean snow = above != null && above.id.equals("minecraft:snow");
		
		addBox(obj,
				x - 0.5f, y - 0.5f, z - 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f, 
				null, 
				getMtlSides(data, biome, snow), 
				null, 
				drawSides(chunks, x, y, z, data));
	}
}
