package org.jmc.models;

import java.util.HashSet;

import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for cobblestone walls.
 */
public class Wall extends BlockModel
{
	/** Blocks to which walls will connect */
	private static HashSet<Short> connectable;
	
	static
	{
		short[] ids = new short[] {
				1,2,3,4,5,7,12,13,14,15,16,17,19,21,22,23,24,25,35,41,42,43,45,47,48,49,56,57,58,
				60,61,62,73,74,80,82,84,86,87,88,91,97,98,107,110,112,121,123,124,129
			};
		
		connectable = new HashSet<Short>(ids.length);
		for (int i = 0; i < ids.length; i++)
			connectable.add(ids[i]);
	}
	
	private boolean checkConnect(ThreadChunkDeligate chunks, byte data, int x, int y, int z)
	{
		int otherId = chunks.getBlockID(x, y, z);
		if (connectable.contains(otherId))
			return true;

		int otherData = chunks.getBlockData(x, y, z);
		return otherId == this.blockId && otherData == data;
	}
	
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, byte data, byte biome)
	{
		String[] mtls = getMtlSides(data, biome);
		UV[] uvTop, uvSide;
		UV[][] uvSides;

		boolean conn_n = checkConnect(chunks, data, x, y, z-1);
		boolean conn_s = checkConnect(chunks, data, x, y, z+1);
		boolean conn_e = checkConnect(chunks, data, x-1, y, z);
		boolean conn_w = checkConnect(chunks, data, x+1, y, z);
		boolean conn_u = checkConnect(chunks, data, x, y+1, z);
				
		// center column
		if (!(conn_n && conn_s && !conn_e && !conn_w) && !(!conn_n && !conn_s && conn_e && conn_w) || conn_u)
		{
			uvTop = new UV[] { new UV(4/16f, 4/16f), new UV(12/16f, 4/16f), new UV(12/16f, 12/16f), new UV(4/16f, 12/16f) };
			uvSide = new UV[] { new UV(4/16f, 0), new UV(12/16f, 0), new UV(12/16f, 1), new UV(4/16f, 1) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x-0.25f, y-0.5f, z-0.25f, x+0.25f, y+0.5f, z+0.25f, null, mtls, uvSides, null);
		}
		// north wall
		if (conn_n)
		{
			uvTop = new UV[] { new UV(5/16f, 8/16f), new UV(11/16f, 8/16f), new UV(11/16f, 1), new UV(5/16f, 1) };
			uvSide = new UV[] { new UV(8/16f, 0), new UV(1, 0), new UV(1, 13/16f), new UV(8/16f, 13/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x-0.1875f, y-0.5f, z-0.5f, x+0.1875f, y+0.3125f, z, null, mtls, uvSides, null);
		}
		// south wall
		if (conn_s)
		{
			uvTop = new UV[] { new UV(5/16f, 0), new UV(11/16f, 0), new UV(11/16f, 8/16f), new UV(5/16f, 8/16f) };
			uvSide = new UV[] { new UV(0, 0), new UV(8/16f, 0), new UV(8/16f, 13/16f), new UV(0, 13/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x-0.1875f, y-0.5f, z, x+0.1875f, y+0.3125f, z+0.5f, null, mtls, uvSides, null);
		}
		// east wall
		if (conn_e)
		{
			uvTop = new UV[] { new UV(8/16f, 5/16f), new UV(1, 5/16f), new UV(1, 11/16f), new UV(8/16f, 11/16f) };
			uvSide = new UV[] { new UV(8/16f, 0), new UV(1, 0), new UV(1, 13/16f), new UV(8/16f, 13/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x-0.5f, y-0.5f, z-0.1875f, x, y+0.3125f, z+0.1875f, null, mtls, uvSides, null);
		}
		// west wall
		if (conn_w)
		{
			uvTop = new UV[] { new UV(0, 5/16f), new UV(8/16f, 5/16f), new UV(8/16f, 11/16f), new UV(0, 11/16f) };
			uvSide = new UV[] { new UV(0, 0), new UV(8/16f, 0), new UV(8/16f, 13/16f), new UV(0, 13/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x, y-0.5f, z-0.1875f, x+0.5f, y+0.3125f, z+0.1875f, null, mtls, uvSides, null);
		}
	}

}
