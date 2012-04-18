package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;


/**
 * Model for hatches (aka trapdoors)
 */
public class Hatch extends BlockModel
{

	/** Expand the materials to the full 6 side definition used by addBox */
	private String[] getMtlSides(byte data)
	{
		String[] abbrMtls = materials.get(data);

		String[] mtlSides = new String[6];
		mtlSides[0] = abbrMtls[0];
		mtlSides[1] = abbrMtls[0];
		mtlSides[2] = abbrMtls[0];
		mtlSides[3] = abbrMtls[0];
		mtlSides[4] = abbrMtls[0];
		mtlSides[5] = abbrMtls[0];
		return mtlSides;
	}

	
	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		int dir = (data & 3);
		boolean open = (data & 4) != 0;

		/*
		  The model is rendered in the middle of the block, then rotated
		*/
		Transform rotate = new Transform();
		Transform translate = new Transform();

		if (open)
		{
			switch (dir)
			{
				case 0:
					rotate.rotate(-90, 0, 0);
					translate.translate(x, y, z+0.40625f);
					break;
				case 1:
					rotate.rotate(90, 0, 0);
					translate.translate(x, y, z-0.40625f);
					break;
				case 2:
					rotate.rotate(0, 0, 90);
					translate.translate(x+0.40625f, y, z);
					break;
				case 3:
					rotate.rotate(0, 0, -90);
					translate.translate(x-0.40625f, y, z);
					break;
			}
		}
		else
		{
			translate.translate(x, y-0.40625f, z);
		}
		
		Transform rt = translate.multiply(rotate);

		
		UV[] uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,3/16f), new UV(0,3/16f) };
		UV[][] uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };

		addBox(obj,
				-0.5f, -0.09375f, -0.5f,
				0.5f, 0.09375f, 0.5f, 
				rt, 
				getMtlSides(data), 
				uvSides, 
				null);

	}

}
