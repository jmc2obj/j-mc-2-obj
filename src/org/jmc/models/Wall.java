package org.jmc.models;

import java.util.HashMap;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for cobblestone walls.
 */
public class Wall extends BlockModel
{
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		String[] mtls = getMtlSides(data, biome);
		UV[] uvTop, uvSide;
		UV[][] uvSides;

		boolean conn_n = data.get("north").equals("true");
		boolean conn_s = data.get("south").equals("true");
		boolean conn_e = data.get("east").equals("true");
		boolean conn_w = data.get("west").equals("true");
		boolean conn_u = data.get("up").equals("true");
				
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
		// west wall
		if (conn_w)
		{
			uvTop = new UV[] { new UV(8/16f, 5/16f), new UV(1, 5/16f), new UV(1, 11/16f), new UV(8/16f, 11/16f) };
			uvSide = new UV[] { new UV(8/16f, 0), new UV(1, 0), new UV(1, 13/16f), new UV(8/16f, 13/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x-0.5f, y-0.5f, z-0.1875f, x, y+0.3125f, z+0.1875f, null, mtls, uvSides, null);
		}
		// east wall
		if (conn_e)
		{
			uvTop = new UV[] { new UV(0, 5/16f), new UV(8/16f, 5/16f), new UV(8/16f, 11/16f), new UV(0, 11/16f) };
			uvSide = new UV[] { new UV(0, 0), new UV(8/16f, 0), new UV(8/16f, 13/16f), new UV(0, 13/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, x, y-0.5f, z-0.1875f, x+0.5f, y+0.3125f, z+0.1875f, null, mtls, uvSides, null);
		}
	}

}
