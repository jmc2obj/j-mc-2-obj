package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


public class Grindstone extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		String[] mtls = getMtlSides(data, biome);
		String[] mtls_Grindstone = new String [] { mtls[1], mtls[1], mtls[1], mtls[2], mtls[2], mtls[1] };
		String[] mtls_Pivot = new String [] { mtls[0], mtls[0], mtls[0], mtls[0], mtls[0], mtls[0] };
		String[] mtls_Poles = new String [] { mtls[3], mtls[3], mtls[3], mtls[3], mtls[3], mtls[3] };
		
		UV[] uvTop, uvSide, uvSide2;
		UV[][] uvSides;
		boolean[] drawSides;
		
		Direction dir = data.state.getDirection("facing", Direction.SOUTH);
		
		// Standing on the floor
		if (data.state.get("face").equals("floor"))
		{
			switch (dir)
			{
				case NORTH: rotate = Transform.rotation(0, 180, 0); break;
				default:
				case SOUTH: rotate = Transform.rotation(0, 0, 0); break;
				case WEST: rotate = Transform.rotation(0, 90, 0); break;
				case EAST: rotate = Transform.rotation(0, -90, 0); break;
			}
		}
		// Hanging from a ceiling
		else if (data.state.get("face").equals("ceiling"))
		{
			switch (dir)
			{
				case NORTH: rotate = Transform.rotation(180, 180, 0); break;
				default:
				case SOUTH: rotate = Transform.rotation(180, 0, 0); break;
				case WEST: rotate = Transform.rotation(180, 90, 0); break;
				case EAST: rotate = Transform.rotation(180, -90, 0); break;
			}
		}
		// Attached to the side of a wall
		else
		{
			switch (dir)
			{
				case NORTH: rotate = Transform.rotation(0, 90, 90); break;
				default:
				case SOUTH: rotate = Transform.rotation(0, -90, 90); break;
				case WEST: rotate = Transform.rotation(0, 0, 90); break;
				case EAST: rotate = Transform.rotation(0, 180, 90); break;
			}			
		}
		
		translate = Transform.translation(x, y, z);
		rt = translate.multiply(rotate);
		
		
		// Drawing the grindstone
		uvSide = new UV[] { new UV(0, 4/16f), new UV(8/16f, 4/16f), new UV(8/16f, 16/16f), new UV(0, 16/16f) };
		uvSide2 = new UV[] { new UV(0, 4/16f), new UV(12/16f, 4/16f), new UV(12/16f, 16/16f), new UV(0, 16/16f) };
		uvSides = new UV[][] { uvSide, uvSide, uvSide, uvSide2, uvSide2, uvSide };
		addBox(obj, -4/16f, -4/16f, -6/16f,	4/16f, 8/16f, 6/16f, rt, mtls_Grindstone, uvSides, null);
		
		// Drawing the pivots
		uvSide = new UV[] { new UV(6/16f, 10/16f), new UV(8/16f, 10/16f), new UV(8/16f, 16/16f), new UV(6/16f, 16/16f) };
		uvSide2 = new UV[] { new UV(0, 10/16f), new UV(6/16f, 10/16f), new UV(6/16f, 16/16f), new UV(0, 16/16f) };
		uvTop = new UV[] { new UV(8/16f, 10/16f), new UV(10/16f, 10/16f), new UV(10/16f, 16/16f), new UV(8/16f, 16/16f) };
		uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide2, uvSide2, uvTop };
		addBox(obj, -6/16f, -1/16f, -3/16f,	-4/16f, 5/16f, 3/16f, rt, mtls_Pivot, uvSides, null);
		addBox(obj, 4/16f, -1/16f, -3/16f,	6/16f, 5/16f, 3/16f, rt, mtls_Pivot, uvSides, null);
		
		// Drawing the poles
		drawSides = new boolean[] {false,true,true,true,true,true};
		uvSide = new UV[] { new UV(6/16f, 9/16f), new UV(8/16f, 9/16f), new UV(8/16f, 16/16f), new UV(6/16f, 16/16f) };
		uvSide2 = new UV[] { new UV(6/16f, 0), new UV(10/16f, 0), new UV(10/16f, 7/16f), new UV(6/16f, 7/16f) };
		uvTop = new UV[] { new UV(8/16f, 10/16f), new UV(10/16f, 10/16f), new UV(10/16f, 16/16f), new UV(8/16f, 16/16f) };
		uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide2, uvSide2, uvTop };
		addBox(obj, -6/16f, -8/16f, -2/16f, -4/16f, -1/16f, 2/16f, rt, mtls_Poles, uvSides, drawSides);
		addBox(obj, 4/16f, -8/16f, -2/16f,	6/16f, -1/16f, 2/16f, rt, mtls_Poles, uvSides, drawSides);
	}
}
