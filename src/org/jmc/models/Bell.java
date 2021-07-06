package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;

public class Bell extends BlockModel
{
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		
		String[] mtls = materials.get(data,biome);
		String[] mtls_BellBottom = new String [] { mtls[2], mtls[1], mtls[1], mtls[1], mtls[1], mtls[2] };
		String[] mtls_BellTop = new String [] 	{ mtls[0], mtls[1], mtls[1], mtls[1], mtls[1], mtls[2] };
		String[] mtls_Wood = new String [] 		{ mtls[4], mtls[4], mtls[4], mtls[4], mtls[4], mtls[4] };
		String[] mtls_Stone = new String [] 	{ mtls[3], mtls[3], mtls[3], mtls[3], mtls[3], mtls[3] };
		
		UV[] uvTop, uvBottom, uvSide, uvSide2;
		UV[][] uvSides;
		boolean[] drawSides;
				
		// Drawing the bottom wider rectangle of bell (8x8)
		uvTop= new UV[] { new UV(0, 8/16f), new UV(8/16f, 8/16f), new UV(8/16f, 16/16f), new UV(0, 16/16f) };
		uvBottom= new UV[] { new UV(0, 8/16f), new UV(8/16f, 8/16f), new UV(8/16f, 16/16f), new UV(0, 16/16f) };
		uvSide = new UV[] { new UV(0, 7/16f), new UV(8/16f, 7/16f), new UV(8/16f, 9/16f), new UV(0, 9/16f) };
		uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvBottom };
		addBox(obj, x-4/16f, y-4/16f, z-4/16f, x+4/16f, y-2/16f, z+4/16f, null, mtls_BellBottom, uvSides, null);
		

		// Drawing the main portion of the bell (6x6)
		drawSides = new boolean[] {true,true,true,true,true,false};
		uvTop= new UV[] { new UV(1/16f, 9/16f), new UV(7/16f, 9/16f), new UV(7/16f, 15/16f), new UV(1/16f, 15/16f) };
		uvBottom= new UV[] { new UV(1/16f, 9/16f), new UV(7/16f, 9/16f), new UV(7/16f, 15/16f), new UV(1/16f, 15/16f) };
		uvSide = new UV[] { new UV(1/16f, 9/16f), new UV(7/16f, 9/16f), new UV(7/16f, 16/16f), new UV(1/16f, 16/16f) };
		uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvBottom };
		addBox(obj, x-3/16f, y-2/16f, z-3/16f, x+3/16f, y+5/16f, z+3/16f, null, mtls_BellTop, uvSides, drawSides);
		
		
		// If it's hanging from the ceiling
		if (data.state.get("attachment").equals("ceiling"))
		{
			drawSides = new boolean[] {false,true,true,true,true,false};
			uvSide = new UV[] { new UV(0, 11/16f), new UV(2/16f, 11/16f), new UV(2/16f, 14/16f), new UV(0, 14/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvBottom };
			addBox(obj, x-1/16f, y+5/16f, z-1/16f, x+1/16f, y+8/16f, z+1/16f, null, mtls_Wood, uvSides, drawSides);
		}
		else
		{
			Transform rotate = new Transform();
			Transform translate = new Transform();
			Transform rt;
			
			switch (data.state.getDirection("facing", Direction.SOUTH))
			{
				case WEST: rotate = Transform.rotation(0, 90, 0); break;
				case NORTH: rotate = Transform.rotation(0, 180, 0); break;
				case EAST: rotate = Transform.rotation(0, -90, 0); break;
				default: break;
			}
			translate = Transform.translation(x, y, z);
			rt = translate.multiply(rotate);
			
			if (data.state.get("attachment").equals("floor"))
			{
				drawSides = new boolean[] {true,true,true,false,false,true};
				uvSide = new UV[] { new UV(0, 0), new UV(12/16f, 0), new UV(12/16f, 2/16f), new UV(0, 2/16f) };
				uvSides = new UV[][] { uvSide, uvSide, uvSide, uvSide, uvSide, uvSide };
				addBox(obj, -6/16f, 5/16f, -1/16f, 6/16f, 7/16f, 1/16f, rt, mtls_Wood, uvSides, drawSides);
				
				// Drawing the two stone supports
				drawSides = new boolean[] {true,true,true,true,true,false};
				uvTop = new UV[] { new UV(0, 0), new UV(2/16f, 0), new UV(2/16f, 4/16f), new UV(0, 4/16f) };
				uvSide = new UV[] { new UV(0, 0), new UV(2/16f, 0), new UV(2/16f, 16/16f), new UV(0, 16/16f) };
				uvSide2 = new UV[] { new UV(0, 0), new UV(4/16f, 0), new UV(4/16f, 16/16f), new UV(0, 16/16f) };
				uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide2, uvSide2, uvTop };
				addBox(obj, -8/16f, -8/16f, -2/16f, -6/16f, 8/16f, 2/16f, rt, mtls_Stone, uvSides, drawSides);
				addBox(obj, 6/16f, -8/16f, -2/16f, 8/16f, 8/16f, 2/16f, rt, mtls_Stone, uvSides, drawSides);
			}
			else if (data.state.get("attachment").equals("single_wall"))
			{
				drawSides = new boolean[] {true,true,false,true,true,true};
				uvSide = new UV[] { new UV(0, 0), new UV(12/16f, 0), new UV(12/16f, 2/16f), new UV(0, 2/16f) };
				uvSide2 = new UV[] { new UV(0, 0), new UV(2/16f, 0), new UV(2/16f, 2/16f), new UV(0, 2/16f) };
				uvTop = new UV[] { new UV(12/16f, 0), new UV(12/16f, 2/16f), new UV(0, 2/16f), new UV(0, 0) };
				uvSides = new UV[][] { uvTop, uvSide2, uvSide, uvSide, uvSide, uvTop };
				addBox(obj, -1/16f, 5/16f, -4/16f, 1/16f, 7/16f, 8/16f, rt, mtls_Wood, uvSides, drawSides);
			}
			// Attached to double wall
			else 
			{
				drawSides = new boolean[] {true,false,false,true,true,true};
				uvSide = new UV[] { new UV(0, 0), new UV(16/16f, 0), new UV(16/16f, 2/16f), new UV(0, 2/16f) };
				uvSide2 = new UV[] { new UV(12/16f, 0), new UV(12/16f, 2/16f), new UV(0, 2/16f), new UV(0, 0) };
				uvSides = new UV[][] { uvSide2, uvSide, uvSide, uvSide, uvSide, uvSide2 };
				addBox(obj, -1/16f, 5/16f, -8/16f, 1/16f, 7/16f, 8/16f, rt, mtls_Wood, uvSides, drawSides);
			}
		}
		
	}
}
	
