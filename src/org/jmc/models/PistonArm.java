package org.jmc.models;

import java.util.HashMap;

import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for piston extension block.
 */
public class PistonArm extends BlockModel
{

	private String[] getMtlSidesTop(byte data, int biome)
	{
		boolean sticky = (data & 8) != 0;
		String[] abbrMtls = materials.get(data,biome);

		String[] mtlSides = new String[6];
		mtlSides[0] = sticky ? abbrMtls[1] : abbrMtls[0];
		mtlSides[1] = abbrMtls[2];
		mtlSides[2] = abbrMtls[2];
		mtlSides[3] = abbrMtls[2];
		mtlSides[4] = abbrMtls[2];
		mtlSides[5] = abbrMtls[0];
		return mtlSides;
	}

	private String[] getMtlSidesArm(byte data, int biome)
	{
		String[] abbrMtls = materials.get(data,biome);

		String[] mtlSides = new String[6];
		mtlSides[0] = abbrMtls[2];
		mtlSides[1] = abbrMtls[2];
		mtlSides[2] = abbrMtls[2];
		mtlSides[3] = abbrMtls[2];
		mtlSides[4] = abbrMtls[2];
		mtlSides[5] = abbrMtls[2];
		return mtlSides;
	}

	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		int dir = (data & 7);


		/*
		  The model is rendered facing up, then rotated
		*/
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		switch (dir)
		{
			case 0: rotate.rotate(180, 0, 0); break;
			case 2: rotate.rotate(-90, 0, 0); break;
			case 3: rotate.rotate(90, 0, 0); break;
			case 4: rotate.rotate(0, 0, 90); break;
			case 5: rotate.rotate(0, 0, -90); break;
		}
		translate.translate(x, y, z);		
		rt = translate.multiply(rotate);


		boolean[] drawSides;
		UV[] uvSide;
		UV[][] uvSides;

		// top
		uvSide = new UV[] { new UV(0,12/16f), new UV(1,12/16f), new UV(1,1), new UV(0,1) };
		uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
		addBox(obj,
				-0.5f, 0.25f, -0.5f,
				0.5f, 0.5f, 0.5f, 
				rt, 
				getMtlSidesTop(data,biome), 
				uvSides, 
				null);

		// arm (extends outside the block)
		drawSides = new boolean[] {false,true,true,true,true,false};
		uvSide = new UV[] { new UV(1,12/16f), new UV(1,1), new UV(0,1), new UV(0,12/16f) };
		uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
		addBox(obj,
				-0.125f, -0.75f, -0.125f,
				0.125f, 0.25f, 0.125f, 
				rt, 
				getMtlSidesArm(data,biome), 
				uvSides, 
				drawSides);
		
	}

}
