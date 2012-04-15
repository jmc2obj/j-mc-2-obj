package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.UV;


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
		UV[] uvSide, uvFront, uvTop;
		UV[][] uvSides;
		
		uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,12/16f), new UV(0,12/16f) };
		uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };

		switch (dir)
		{
		case 0:
			if (up==0)
			{
				boolean b=drawSides[0]; drawSides[0]=true;
				uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
				addBox(obj, x-0.5f, y-0.5f, z-0.5f, x+0.5f, y, z+0.5f, null, mtls, uvSides, drawSides);

				drawSides[0]=b; drawSides[1]=true;
				uvTop = new UV[] { new UV(0.5f,0), new UV(1,0), new UV(1,1), new UV(0.5f,1) };
				uvFront = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSide = new UV[] { new UV(0.5f,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0.5f,1) };
				uvSides = new UV[][] { uvTop, uvSide, uvSide, uvFront, uvFront, uvTop };
				addBox(obj, x, y, z-0.5f, x+0.5f, y+0.5f, z+0.5f, null, mtls, uvSides, drawSides);
			}
			else
			{
				boolean b=drawSides[5]; drawSides[5]=true;
				uvSide = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
				addBox(obj, x-0.5f, y, z-0.5f, x+0.5f, y+0.5f, z+0.5f, null, mtls, uvSides, drawSides);

				drawSides[5]=b; drawSides[1]=true;
				uvTop = new UV[] { new UV(0.5f,0), new UV(1,0), new UV(1,1), new UV(0.5f,1) };
				uvFront = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSide = new UV[] { new UV(0.5f,0), new UV(1,0), new UV(1,0.5f), new UV(0.5f,0.5f) };
				uvSides = new UV[][] { uvTop, uvSide, uvSide, uvFront, uvFront, uvTop };
				addBox(obj, x, y-0.5f, z-0.5f, x+0.5f, y, z+0.5f, null, mtls, uvSides, drawSides);
			}
			break;
		case 1:
			if (up==0)
			{
				boolean b=drawSides[0]; drawSides[0]=true;
				uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
				addBox(obj, x-0.5f, y-0.5f, z-0.5f, x+0.5f, y, z+0.5f, null, mtls, uvSides, drawSides);

				drawSides[0]=b; drawSides[2]=true;
				uvTop = new UV[] { new UV(0,0), new UV(0.5f,0), new UV(0.5f,1), new UV(0,1) };
				uvFront = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSide = new UV[] { new UV(0,0.5f), new UV(0.5f,0.5f), new UV(0.5f,1), new UV(0,1) };
				uvSides = new UV[][] { uvTop, uvSide, uvSide, uvFront, uvFront, uvTop };
				addBox(obj, x-0.5f, y, z-0.5f, x, y+0.5f, z+0.5f, null, mtls, uvSides, drawSides);
			}
			else
			{
				boolean b=drawSides[5]; drawSides[5]=true;
				uvSide = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
				addBox(obj, x-0.5f, y, z-0.5f, x+0.5f, y+0.5f, z+0.5f, null, mtls, uvSides, drawSides);

				drawSides[5]=b; drawSides[2]=true;
				uvTop = new UV[] { new UV(0,0), new UV(0.5f,0), new UV(0.5f,1), new UV(0,1) };
				uvFront = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSide = new UV[] { new UV(0,0), new UV(0.5f,0), new UV(0.5f,0.5f), new UV(0,0.5f) };
				uvSides = new UV[][] { uvTop, uvSide, uvSide, uvFront, uvFront, uvTop };
				addBox(obj, x-0.5f, y-0.5f, z-0.5f, x, y, z+0.5f, null, mtls, uvSides, drawSides);
			}
			break;
		case 2:
			if (up==0)
			{
				boolean b=drawSides[0]; drawSides[0]=true;
				uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
				addBox(obj, x-0.5f, y-0.5f, z-0.5f, x+0.5f, y, z+0.5f, null, mtls, uvSides, drawSides);

				drawSides[0]=b; drawSides[3]=true;
				uvTop = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvFront = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSide = new UV[] { new UV(0.5f,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0.5f,1) };
				uvSides = new UV[][] { uvTop, uvFront, uvFront, uvSide, uvSide, uvTop };
				addBox(obj, x-0.5f, y, z, x+0.5f, y+0.5f, z+0.5f, null, mtls, uvSides, drawSides);
			}
			else
			{
				boolean b=drawSides[5]; drawSides[5]=true;
				uvSide = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
				addBox(obj, x-0.5f, y, z-0.5f, x+0.5f, y+0.5f, z+0.5f, null, mtls, uvSides, drawSides);

				drawSides[5]=b; drawSides[3]=true;
				uvTop = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvFront = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSide = new UV[] { new UV(0.5f,0), new UV(1,0), new UV(1,0.5f), new UV(0.5f,0.5f) };
				uvSides = new UV[][] { uvTop, uvFront, uvFront, uvSide, uvSide, uvTop };
				addBox(obj, x-0.5f, y-0.5f, z, x+0.5f, y, z+0.5f, null, mtls, uvSides, drawSides);
			}
			break;
		case 3:
			if (up==0)
			{
				boolean b=drawSides[0]; drawSides[0]=true;
				uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
				addBox(obj, x-0.5f, y-0.5f, z-0.5f, x+0.5f, y, z+0.5f, null, mtls, uvSides, drawSides);

				drawSides[0]=b; drawSides[4]=true;
				uvTop = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvFront = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSide = new UV[] { new UV(0,0.5f), new UV(0.5f,0.5f), new UV(0.5f,1), new UV(0,1) };
				uvSides = new UV[][] { uvTop, uvFront, uvFront, uvSide, uvSide, uvTop };
				addBox(obj, x-0.5f, y, z-0.5f, x+0.5f, y+0.5f, z, null, mtls, uvSides, drawSides);
			}
			else
			{
				boolean b=drawSides[5]; drawSides[5]=true;
				uvSide = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
				addBox(obj, x-0.5f, y, z-0.5f, x+0.5f, y+0.5f, z+0.5f, null, mtls, uvSides, drawSides);
				
				drawSides[5]=b; drawSides[4]=true;
				uvTop = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
				uvFront = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
				uvSide = new UV[] { new UV(0,0), new UV(0.5f,0), new UV(0.5f,0.5f), new UV(0,0.5f) };
				uvSides = new UV[][] { uvTop, uvFront, uvFront, uvSide, uvSide, uvTop };
				addBox(obj, x-0.5f, y-0.5f, z-0.5f, x+0.5f, y, z, null, mtls, uvSides, drawSides);
			}
			break;		
		}
	}

}
