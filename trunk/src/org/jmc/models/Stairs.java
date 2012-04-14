package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;


/**
 * Model for stairs.
 */
public class Stairs extends BlockModel
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
		int dir = data & 3;
		int up = data & 4;
		String[] mtls = getMtlSides(data);
		boolean[] drawSides = drawSides(chunks, x, y, z);
		
		switch (dir)
		{
		case 0:
			if (up==0)
			{
				boolean b=drawSides[0]; drawSides[0]=true;
				addBox(obj, x-0.5f, y-0.5f, z-0.5f, x+0.5f, y, z+0.5f, drawSides, mtls);
				drawSides[0]=b; drawSides[1]=true;
				addBox(obj, x, y, z-0.5f, x+0.5f, y+0.5f, z+0.5f, drawSides, mtls);
			}
			else
			{
				boolean b=drawSides[5]; drawSides[5]=true;
				addBox(obj, x-0.5f, y, z-0.5f, x+0.5f, y+0.5f, z+0.5f, drawSides, mtls);
				drawSides[5]=b; drawSides[1]=true;
				addBox(obj, x, y-0.5f, z-0.5f, x+0.5f, y, z+0.5f, drawSides, mtls);
			}
			break;
		case 1:
			if (up==0)
			{
				boolean b=drawSides[0]; drawSides[0]=true;
				addBox(obj, x-0.5f, y-0.5f, z-0.5f, x+0.5f, y, z+0.5f, drawSides, mtls);
				drawSides[0]=b; drawSides[2]=true;
				addBox(obj, x-0.5f, y, z-0.5f, x, y+0.5f, z+0.5f, drawSides, mtls);
			}
			else
			{
				boolean b=drawSides[5]; drawSides[5]=true;
				addBox(obj, x-0.5f, y, z-0.5f, x+0.5f, y+0.5f, z+0.5f, drawSides, mtls);
				drawSides[5]=b; drawSides[2]=true;
				addBox(obj, x-0.5f, y-0.5f, z-0.5f, x, y, z+0.5f, drawSides, mtls);
			}
			break;
		case 2:
			if (up==0)
			{
				boolean b=drawSides[0]; drawSides[0]=true;
				addBox(obj, x-0.5f, y-0.5f, z-0.5f, x+0.5f, y, z+0.5f, drawSides, mtls);
				drawSides[0]=b; drawSides[3]=true;
				addBox(obj, x-0.5f, y, z, x+0.5f, y+0.5f, z+0.5f, drawSides, mtls);
			}
			else
			{
				boolean b=drawSides[5]; drawSides[5]=true;
				addBox(obj, x-0.5f, y, z-0.5f, x+0.5f, y+0.5f, z+0.5f, drawSides, mtls);
				drawSides[5]=b; drawSides[3]=true;
				addBox(obj, x-0.5f, y-0.5f, z, x+0.5f, y, z+0.5f, drawSides, mtls);
			}
			break;
		case 3:
			if (up==0)
			{
				boolean b=drawSides[0]; drawSides[0]=true;
				addBox(obj, x-0.5f, y-0.5f, z-0.5f, x+0.5f, y, z+0.5f, drawSides, mtls);
				drawSides[0]=b; drawSides[4]=true;
				addBox(obj, x-0.5f, y, z-0.5f, x+0.5f, y+0.5f, z, drawSides, mtls);
			}
			else
			{
				boolean b=drawSides[5]; drawSides[5]=true;
				addBox(obj, x-0.5f, y, z-0.5f, x+0.5f, y+0.5f, z+0.5f, drawSides, mtls);
				drawSides[5]=b; drawSides[4]=true;
				addBox(obj, x-0.5f, y-0.5f, z-0.5f, x+0.5f, y, z, drawSides, mtls);
			}
			break;		
		}
	}

}
