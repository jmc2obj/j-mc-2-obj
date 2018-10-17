package org.jmc.models;

import java.util.HashMap;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;

/**
 * Model for fences.
 */
public class Fence extends BlockModel
{
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		String[] mtls = getMtlSides(data, biome);
		UV[] uvTop, uvSide;
		UV[][] uvSides;

		// fence post
		uvTop = new UV[] { new UV(6/16f, 6/16f), new UV(10/16f, 6/16f), new UV(10/16f, 10/16f), new UV(6/16f, 10/16f) };
		uvSide = new UV[] { new UV(6/16f, 0), new UV(10/16f, 0), new UV(10/16f, 1), new UV(6/16f, 1) };
		uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
		addBox(obj, x-0.125f, y-0.5f, z-0.125f, x+0.125f, y+0.5f, z+0.125f, null, mtls, uvSides, null);

		// north connector
		if (data.get("north").equals("true"))
		{
			uvTop = new UV[] { new UV(7/16f, 10/16f), new UV(9/16f, 10/16f), new UV(9/16f, 1), new UV(7/16f, 1) };
			uvSide = new UV[] { new UV(10/16f, 12/16f), new UV(1, 12/16f), new UV(1, 15/16f), new UV(10/16f, 15/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x-0.0625f, y+0.250f, z-0.5f, x+0.0625f, y+0.4375f, z-0.125f, null, mtls, uvSides, null);

			uvSide = new UV[] { new UV(10/16f, 6/16f), new UV(1, 6/16f), new UV(1, 9/16f), new UV(10/16f, 9/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x-0.0625f, y-0.125f, z-0.5f, x+0.0625f, y+0.0625f, z-0.125f, null, mtls, uvSides, null);
		}
		// south connector
		if (data.get("south").equals("true"))
		{
			uvTop = new UV[] { new UV(7/16f, 0), new UV(9/16f, 0), new UV(9/16f, 6/16f), new UV(7/16f, 6/16f) };
			uvSide = new UV[] { new UV(0, 12/16f), new UV(6/16f, 12/16f), new UV(6/16f, 15/16f), new UV(0, 15/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x-0.0625f, y+0.250f, z+0.125f, x+0.0625f, y+0.4375f, z+0.5f, null, mtls, uvSides, null);

			uvSide = new UV[] { new UV(0, 6/16f), new UV(6/16f, 6/16f), new UV(6/16f, 9/16f), new UV(0, 9/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x-0.0625f, y-0.125f, z+0.125f, x+0.0625f, y+0.0625f, z+0.5f, null, mtls, uvSides, null);
		}
		// west connector
		if (data.get("west").equals("true"))
		{
			uvTop = new UV[] { new UV(10/16f, 7/16f), new UV(1, 7/16f), new UV(1, 9/16f), new UV(10/16f, 9/16f) };
			uvSide = new UV[] { new UV(10/16f, 12/16f), new UV(1, 12/16f), new UV(1, 15/16f), new UV(10/16f, 15/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x-0.5f, y+0.250f, z-0.0625f, x-0.125f, y+0.4375f, z+0.0625f, null, mtls, uvSides, null);

			uvSide = new UV[] { new UV(10/16f, 6/16f), new UV(1, 6/16f), new UV(1, 9/16f), new UV(10/16f, 9/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x-0.5f, y-0.125f, z-0.0625f, x-0.125f, y+0.0625f, z+0.0625f, null, mtls, uvSides, null);
		}
		// east connector
		if (data.get("east").equals("true"))
		{
			uvTop = new UV[] { new UV(0, 7/16f), new UV(6/16f, 7/16f), new UV(6/16f, 9/16f), new UV(0, 9/16f) };
			uvSide = new UV[] { new UV(0, 12/16f), new UV(6/16f, 12/16f), new UV(6/16f, 15/16f), new UV(0, 15/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x+0.125f, y+0.250f, z-0.0625f, x+0.5f, y+0.4375f, z+0.0625f, null, mtls, uvSides, null);

			uvSide = new UV[] { new UV(0, 6/16f), new UV(6/16f, 6/16f), new UV(6/16f, 9/16f), new UV(0, 9/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x+0.125f, y-0.125f, z-0.0625f, x+0.5f, y+0.0625f, z+0.0625f, null, mtls, uvSides, null);
		}
	}

}
