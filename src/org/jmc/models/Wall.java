package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;

public class Wall extends BlockModel
{
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		Transform trans = Transform.translation(x, y, z);
		
		NamespaceID[] mtls = getMtlSides(data, biome);
		
		String north, south, east, west;
		
		north = data.state.get("north");
		if (north == null || north.equals("false")) {
			north = "none";
		}
		if (north.equals("false")) {
			north = "low";
		}

		south = data.state.get("south");
		if (south == null || south.equals("false")) {
			south = "none";
		}
		if (south.equals("false")) {
			south = "low";
		}

		east = data.state.get("east");
		if (east == null || east.equals("false")) {
			east = "none";
		}
		if (east.equals("false")) {
			east = "low";
		}

		west = data.state.get("west");
		if (west == null || west.equals("false")) {
			west = "none";
		}
		else if (west.equals("false")) {
			west = "low";
		}
		
		boolean up = data.state.getBool("up", true);
		
		// center column
		if (up) {
			addBoxCubeUV(obj, -4/16f, -8/16f, -4/16f, 4/16f, 8/16f, 4/16f, trans, mtls, null);
		}
		// north wall
		if (north.equals("low") || north.equals("tall")) {
			int height = north.equals("tall") ? 16 : 14;
			addBoxCubeUV(obj, -3/16f, -8/16f, -8/16f, 3/16f, (height-8)/16f, 0, trans, mtls, null);
		}
		// south wall
		if (south.equals("low") || south.equals("tall")) {
			int height = south.equals("tall") ? 16 : 14;
			addBoxCubeUV(obj, -3/16f, -8/16f, 0, 3/16f, (height-8)/16f, 8/16f, trans, mtls, null);
		}
		// west wall
		if (west.equals("low") || west.equals("tall")) {
			int height = west.equals("tall") ? 16 : 14;
			addBoxCubeUV(obj, -8/16f, -8/16f, -3/16f, 0, (height-8)/16f, 3/16f, trans, mtls, null);
		}
		// east wall
		if (east.equals("low") || east.equals("tall")) {
			int height = east.equals("tall") ? 16 : 14;
			addBoxCubeUV(obj, 0, -8/16f, -3/16f, 8/16f, (height-8)/16f, 3/16f, trans, mtls, null);
		}
	}
}
