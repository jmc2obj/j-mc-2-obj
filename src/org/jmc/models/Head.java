package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for shrunken heads.
 */
public class Head extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		boolean onWall = false;
		String facing = "";
		int rot = 0;
		
		if (data.containsKey("facing")) { //If it has a "facing" state, then it's a wall skull
			onWall = true;
			facing = data.get("facing");
		} else {
			rot = Integer.parseInt(data.get("rotation"));
		}
			
		
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		float r = 0;
		float tx = 0, ty = 0, tz = 0;
		if (!onWall)// on the ground
		{
			switch (rot)
			{
				case 0: r = 0f; break;
				case 1: r = 22.5f; break;
				case 2: r = 45f; break;
				case 3: r = 67.5f; break;
				case 4: r = 90f; break;
				case 5: r = 112.5f; break;
				case 6: r = 135f; break;
				case 7: r = 157.5f; break;
				case 8: r = 180f; break;
				case 9: r = 202.5f; break;
				case 10: r = 225f; break;
				case 11: r = 247.5f; break;
				case 12: r = 270f; break;
				case 13: r = 292.5f; break;
				case 14: r = 315f; break;
				case 15: r = 337.5f; break;
			}
			ty = -0.25f;
		} 
		else // on wall
		{
			switch (facing)
			{
				// on wall, facing North
				case "north":
					r = 0f;
					tz = +0.25f;
					break;
				// on wall, facing South
				case "south":
					r = 180f;
					tz = -0.25f;
					break;
				// on wall, facing West
				case "west":
					r = -90f;
					tx = +0.25f;
					break;
				// on wall, facing East
				case "east":
					r = 90f;
					tx = -0.25f;
					break;
			}							
		}
		rotate.rotate(0, r, 0);
		translate.translate(x+tx, y+ty, z+tz);		
		rt = translate.multiply(rotate);

		String[] mtlSides = getMtlSides(data, biome);
		UV[][] uvSides = new UV[][] {
				new UV[] { new UV(8/64f, 24/32f), new UV(16/64f, 24/32f), new UV(16/64f, 32/32f), new UV(8/64f, 32/32f) },
				new UV[] { new UV(16/64f, 16/32f), new UV(8/64f, 16/32f), new UV(8/64f, 24/32f), new UV(16/64f, 24/32f) },
				new UV[] { new UV(24/64f, 16/32f), new UV(32/64f, 16/32f), new UV(32/64f, 24/32f), new UV(24/64f, 24/32f) },
				new UV[] { new UV(8/64f, 16/32f), new UV(0/64f, 16/32f), new UV(0/64f, 24/32f), new UV(8/64f, 24/32f) },
				new UV[] { new UV(24/64f, 16/32f), new UV(16/64f, 16/32f), new UV(16/64f, 24/32f), new UV(24/64f, 24/32f) },
				new UV[] { new UV(24/64f, 24/32f), new UV(16/64f, 24/32f), new UV(16/64f, 32/32f), new UV(24/64f, 32/32f) },
			};
		addBox(obj, -0.25f,-0.25f,-0.25f, 0.25f,0.25f,0.25f, rt, mtlSides, uvSides, null);
	}

}
