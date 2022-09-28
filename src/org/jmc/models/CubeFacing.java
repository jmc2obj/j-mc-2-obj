package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Generic model for cube blocks.
 */
public class CubeFacing extends BlockModel
{
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		Transform trans = Transform.translation(x, y, z);
		
		Direction dir = data.state.getDirection("facing");
		Transform rotate = Transform.rotation(dir);
		
		boolean[] dSides = drawSides(chunks, x, y, z, data);
		boolean[] dSidesRot = new boolean[6];
		//  or top, front, back, left, right, bottom
		//    (top, north, south, west, east, bottom)
		//      0     1      2     3     4      5
		// un-rotate the occlusion otherwise it rotates with the block.
		switch (dir)
		{
			default:
			case NORTH:
				dSidesRot = dSides;
				break;
			case SOUTH:
				dSidesRot[0] = dSides[0];
				dSidesRot[1] = dSides[2];
				dSidesRot[2] = dSides[1];
				dSidesRot[3] = dSides[4];
				dSidesRot[4] = dSides[3];
				dSidesRot[5] = dSides[5];
				break;
			case EAST:
				dSidesRot[0] = dSides[0];
				dSidesRot[1] = dSides[4];
				dSidesRot[2] = dSides[3];
				dSidesRot[3] = dSides[1];
				dSidesRot[4] = dSides[2];
				dSidesRot[5] = dSides[5];
				break;
			case WEST:
				dSidesRot[0] = dSides[0];
				dSidesRot[1] = dSides[3];
				dSidesRot[2] = dSides[4];
				dSidesRot[3] = dSides[2];
				dSidesRot[4] = dSides[1];
				dSidesRot[5] = dSides[5];
				break;
			case UP:
				dSidesRot[0] = dSides[2];
				dSidesRot[1] = dSides[0];
				dSidesRot[2] = dSides[5];
				dSidesRot[3] = dSides[3];
				dSidesRot[4] = dSides[4];
				dSidesRot[5] = dSides[1];
				break;
			case DOWN:
				dSidesRot[0] = dSides[1];
				dSidesRot[1] = dSides[5];
				dSidesRot[2] = dSides[0];
				dSidesRot[3] = dSides[3];
				dSidesRot[4] = dSides[4];
				dSidesRot[5] = dSides[2];
				break;
		}
		
		addBox(obj,
				-0.5f, -0.5f, -0.5f,
				0.5f, 0.5f, 0.5f, 
				trans.multiply(rotate), 
				getMtlSides(data,biome), 
				null, 
				dSidesRot);
	}

}
