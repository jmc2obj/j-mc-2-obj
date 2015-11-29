package org.jmc.models;

import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for quartz blocks.
 */
public class Quartz extends BlockModel
{

	@Override
	protected String[] getMtlSides(byte data, byte biome)
	{
		String[] mat = materials.get(data, biome);
		
		if (data == 2)		// east-west
			return new String[] { mat[0], mat[1], mat[1], mat[1], mat[1], mat[0] };
		else if (data == 4)	// north-south 
			return new String[] { mat[1], mat[0], mat[0], mat[1], mat[1], mat[1] };
		else if (data == 3)	// upright
			return new String[] { mat[1], mat[1], mat[1], mat[0], mat[0], mat[1] };
		else	// default
			return super.getMtlSides(data, biome);
	}
	
	protected UV[][] getUvSides(byte data)
	{
		UV[] uv1 = new UV[] { new UV(0,0), new UV(1,0), new UV(1,1), new UV(0,1) };
		UV[] uv2 = new UV[] { new UV(1,0), new UV(1,1), new UV(0,1), new UV(0,0) };
		
		if (data == 2)
			return new UV[][] { uv1, uv1, uv1, uv1, uv1, uv1 };
		else if (data == 4)
			return new UV[][] { uv1, uv2, uv2, uv2, uv2, uv1 };
		else if (data == 3)
			return new UV[][] { uv2, uv2, uv2, uv2, uv2, uv2 };
		else	// default
			return null;
	}


	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, byte data, byte biome)
	{
		addBox(obj,
				x - 0.5f, y - 0.5f, z - 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f, 
				null, 
				getMtlSides(data, biome), 
				getUvSides(data), 
				drawSides(chunks, x, y, z));
	}

}
