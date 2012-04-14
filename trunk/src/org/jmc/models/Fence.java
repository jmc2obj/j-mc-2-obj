package org.jmc.models;

import java.util.HashSet;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;


/**
 * Model for stairs.
 */
public class Fence extends BlockModel
{
	/** Blocks to which fences will connect */
	private static HashSet<Short> connectable;
	
	static
	{
		short[] ids = new short[] {
				1,2,3,4,5,7,12,13,14,15,16,17,19,21,22,23,24,25,35,41,42,43,45,47,48,49,56,57,58,
				60,61,62,73,74,80,82,84,85,86,87,88,91,97,98,107,110,112,113,121,123,124
			};
		
		connectable = new HashSet<Short>(ids.length);
		for (int i = 0; i < ids.length; i++)
			connectable.add(ids[i]);
	}
	
	
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
		String[] mtls = getMtlSides(data);
		boolean[] drawSides = new boolean[] {true,true,true,true,true,true};

		// fence post
		addBox(obj, x-0.125f, y-0.5f, z-0.125f, x+0.125f, y+0.5f, z+0.125f, drawSides, mtls);
		// north connector
		if (connectable.contains(chunks.getBlockID(x, y, z-1)))
		{
			addBox(obj, x-0.0625f, y+0.250f, z-0.5f, x+0.0625f, y+0.4375f, z-0.125f, drawSides, mtls);
			addBox(obj, x-0.0625f, y-0.125f, z-0.5f, x+0.0625f, y+0.0625f, z-0.125f, drawSides, mtls);
		}
		// south connector
		if (connectable.contains(chunks.getBlockID(x, y, z+1)))
		{
			addBox(obj, x-0.0625f, y+0.250f, z+0.125f, x+0.0625f, y+0.4375f, z+0.5f, drawSides, mtls);
			addBox(obj, x-0.0625f, y-0.125f, z+0.125f, x+0.0625f, y+0.0625f, z+0.5f, drawSides, mtls);
		}
		// east connector
		if (connectable.contains(chunks.getBlockID(x-1, y, z)))
		{
			addBox(obj, x-0.5f, y+0.250f, z-0.0625f, x-0.125f, y+0.4375f, z+0.0625f, drawSides, mtls);
			addBox(obj, x-0.5f, y-0.125f, z-0.0625f, x-0.125f, y+0.0625f, z+0.0625f, drawSides, mtls);
		}
		// west connector
		if (connectable.contains(chunks.getBlockID(x+1, y, z)))
		{
			addBox(obj, x+0.125f, y+0.250f, z-0.0625f, x+0.5f, y+0.4375f, z+0.0625f, drawSides, mtls);
			addBox(obj, x+0.125f, y-0.125f, z-0.0625f, x+0.5f, y+0.0625f, z+0.0625f, drawSides, mtls);
		}
	}

}
