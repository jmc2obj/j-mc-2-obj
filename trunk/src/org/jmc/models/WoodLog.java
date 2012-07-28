package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.UV;


/**
 * Model for wood logs.
 */
public class WoodLog extends BlockModel
{

	protected String[] getMtlSides(byte data, byte biome, int dir)
	{
		String[] mat = materials.get(data, biome);
		
		if (dir == 1)
			return new String[] { mat[1], mat[1], mat[1], mat[0], mat[0], mat[1] };
		else if (dir == 2)
			return new String[] { mat[1], mat[0], mat[0], mat[1], mat[1], mat[1] };
		else
			return new String[] { mat[0], mat[1], mat[1], mat[1], mat[1], mat[0] };
	}
	
	protected UV[][] getUvSides(int dir)
	{
		UV[] uv1 = new UV[] { new UV(0,0), new UV(1,0), new UV(1,1), new UV(0,1) };
		UV[] uv2 = new UV[] { new UV(1,0), new UV(1,1), new UV(0,1), new UV(0,0) };
		
		if (dir == 1)
			return new UV[][] { uv2, uv2, uv2, uv2, uv2, uv2 };
		else if (dir == 2)
			return new UV[][] { uv1, uv2, uv2, uv2, uv2, uv1 };
		else
			return new UV[][] { uv1, uv1, uv1, uv1, uv1, uv1 };
	}


	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome)
	{
		// 0 - upright
		// 1 - lying east-west
		// 2 - lying north-south
		int dir = (data & 15) >> 2;
		
		addBox(obj,
				x - 0.5f, y - 0.5f, z - 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f, 
				null, 
				getMtlSides(data, biome, dir), 
				getUvSides(dir), 
				drawSides(chunks, x, y, z));
	}

}
