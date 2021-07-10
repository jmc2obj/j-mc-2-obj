package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for piston blocks.
 */
public class PistonBase extends BlockModel
{

	@Override
	protected String[] getMtlSides(BlockData data, int biome)
	{
		boolean open = data.state.get("extended").equals("true");
		String[] abbrMtls = materials.get(data.state,biome);

		String[] mtlSides = new String[6];
		mtlSides[0] = open ? abbrMtls[1] : abbrMtls[0];
		mtlSides[1] = abbrMtls[2];
		mtlSides[2] = abbrMtls[2];
		mtlSides[3] = abbrMtls[2];
		mtlSides[4] = abbrMtls[2];
		mtlSides[5] = abbrMtls[3];
		return mtlSides;
	}


	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		boolean open = data.state.get("extended").equals("true");
		String dir = data.state.get("facing");

		/*
		  The model is rendered facing up, then rotated
		*/
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		switch (dir)
		{
			case "down": rotate = Transform.rotation(180, 0, 0); break;
			case "north": rotate = Transform.rotation(-90, 0, 0); break;
			case "south": rotate = Transform.rotation(90, 0, 0); break;
			case "west": rotate = Transform.rotation(0, 0, 90); break;
			case "east": rotate = Transform.rotation(0, 0, -90); break;
		}
		translate = Transform.translation(x, y, z);		
		rt = translate.multiply(rotate);

		
		if (open)
		{
			UV[] uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,12/16f), new UV(0,12/16f) };
			UV[][] uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };

			addBox(obj,
					-0.5f, -0.5f, -0.5f,
					0.5f, 0.25f, 0.5f, 
					rt, 
					getMtlSides(data,biome), 
					uvSides, 
					null);
		}
		else
		{
			addBox(obj,
					-0.5f, -0.5f, -0.5f,
					0.5f, 0.5f, 0.5f, 
					rt, 
					getMtlSides(data,biome), 
					null, 
					null);
		}
	}

}
