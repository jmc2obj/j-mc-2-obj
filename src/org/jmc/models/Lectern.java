package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;

public class Lectern extends BlockModel
{
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		
		String[] mtls = getMtlSides(data, biome);
		String[] mtls_Base = new String [] { mtls[0], mtls[0], mtls[0], mtls[0], mtls[0], mtls[4] };
		String[] mtls_Post = new String [] { mtls[0], mtls[1], mtls[1], mtls[2], mtls[2], mtls[4] };
		String[] mtls_Top = new String [] { mtls[3], mtls[2], mtls[2], mtls[2], mtls[2], mtls[4] };
		
		UV[] uvTop, uvSide, uvSide2, uvFront, uvBack;
		UV[][] uvSides;
		boolean[] drawSides;
		
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		Direction facing = data.state.getDirection("facing", Direction.NORTH);
		
		switch (facing)
		{
			case WEST: rotate.rotate(0, 90, 0); break;
			case NORTH: rotate.rotate(0, 180, 0); break;
			case EAST: rotate.rotate(0, -90, 0); break;
			default: break;
		}
		translate.translate(x, y, z);
		rt = translate.multiply(rotate);
		
		// Draw the base
		uvSide = new UV[] { new UV(0, 8/16f), new UV(16/16f, 8/16f), new UV(16/16f, 10/16f), new UV(0, 10/16f) };
		uvSide2 = new UV[] { new UV(0, 0), new UV(16/16f, 0), new UV(16/16f, 2/16f), new UV(0, 2/16f) };
		uvSides = new UV[][] { null, uvSide, uvSide2, uvSide, uvSide, null };
		addBox(obj, -8/16f, -8/16f, -8/16f, 8/16f, -6/16f, 8/16f, rt, mtls_Base, uvSides, null);
		
		// Draw the podium post
		drawSides = new boolean[] {false,true,true,true,true,false};
		uvFront = new UV[] { new UV(0, 3/16f), new UV(8/16f, 3/16f), new UV(8/16f, 15/16f), new UV(0, 15/16f) };
		uvBack = new UV[] { new UV(9/16f, 0/16f), new UV(16/16f, 0/16f), new UV(16/16f, 12/16f), new UV(9/16f, 12/16f) };
		uvSide = new UV[] { new UV(15/16f, 0), new UV(15/16f, 8/16f), new UV(3/16f, 8/16f), new UV(3/16f, 0) };
		uvSide2 =  new UV[] { new UV(15/16f, 8/16f), new UV(15/16f, 0), new UV(3/16f, 0), new UV(3/16f, 8/16f), new UV(0, 0) };
		uvSides = new UV[][] { null, uvBack, uvFront, uvSide2, uvSide, null };
		addBox(obj, -4/16f, -6/16f, -4/16f, 4/16f, 6/16f, 4/16f, rt, mtls_Post, uvSides, drawSides);
		
		// Draw the podium top
		switch (facing)
		{
			case WEST: rotate.rotate(0, 180, -20); translate.translate(x-1/16f, y+6/16f, z); break;
			default:
			case NORTH: rotate.rotate(0, -90, -20); translate.translate(x, y+6/16f, z-1/16f); break;
			case EAST: rotate.rotate(0, 0, -20); translate.translate(x+1/16f, y+6/16f, z); break;
			case SOUTH: rotate.rotate(0, 90, -20); translate.translate(x, y+6/16f, z+1/16f); break;
		}
		rt = translate.multiply(rotate);
		
		uvFront = new UV[] { new UV(0, 12/16f), new UV(1, 12/16f), new UV(1, 1), new UV(0, 1) };
		uvTop = new UV[] { new UV(0, 15/16f), new UV(0, 2/16f), new UV(1, 2/16f), new UV(1, 15/16f) };
		uvSide = new UV[] { new UV(13/16f, 8/16f), new UV(0, 8/16f), new UV(0, 12/16f), new UV(13/16f, 12/16f), new UV(0, 12/16f) };
		uvBack = new UV[] { new UV(16/16f, 8/16f), new UV(0, 8/16f), new UV(0, 12/16f), new UV(16/16f, 12/16f), new UV(0, 12/16f) };
		uvSides = new UV[][] { uvTop, uvSide, uvSide, uvBack, uvFront, uvTop };
		addBox(obj, -13/32f, -2/16f, 8/16f, 13/32f, 2/16f, -8/16f, rt, mtls_Top, uvSides, null);
	}
}
	
