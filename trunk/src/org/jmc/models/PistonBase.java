package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;


/**
 * Model for piston blocks.
 */
public class PistonBase extends BlockModel
{

	@Override
	protected String[] getMtlSides(byte data)
	{
		boolean open = (data & 8) != 0;
		String[] abbrMtls = materials.get(data);

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
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		boolean open = (data & 8) != 0;
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

		
		if (open)
		{
			UV[] uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,12/16f), new UV(0,12/16f) };
			UV[][] uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };

			addBox(obj,
					-0.5f, -0.5f, -0.5f,
					0.5f, 0.25f, 0.5f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					null);
		}
		else
		{
			addBox(obj,
					-0.5f, -0.5f, -0.5f,
					0.5f, 0.5f, 0.5f, 
					rt, 
					getMtlSides(data), 
					null, 
					null);
		}
	}

}
