package org.jmc.models;

import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for wall signs.
 */
public class SignBoard extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, byte data, byte biome)
	{
		String[] mtlSides = getMtlSides(data,biome);
		boolean[] drawSides = new boolean[] { true, true, false, true, true, true };
		
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		switch (data)
		{
			case 2:
				rotate.rotate(0, 0, 0);
				break;
			case 3:
				rotate.rotate(0, 180, 0);
				break;
			case 4:
				rotate.rotate(0, -90, 0);
				break;
			case 5:
				rotate.rotate(0, 90, 0);
				break;
		}
		translate.translate(x, y, z);		
			
		rt = translate.multiply(rotate);
		
		
		UV[][] uvSides = new UV[][] {
				new UV[] { new UV(26/64f, 32/32f), new UV(2/64f, 32/32f), new UV(2/64f, 30/32f), new UV(26/64f, 30/32f) },
				new UV[] { new UV(2/64f, 18/32f), new UV(26/64f, 18/32f), new UV(26/64f, 30/32f), new UV(2/64f, 30/32f) },
				new UV[] { new UV(28/64f, 18/32f), new UV(52/64f, 18/32f), new UV(52/64f, 30/32f), new UV(28/64f, 30/32f) },
				new UV[] { new UV(26/64f, 18/32f), new UV(28/64f, 18/32f), new UV(28/64f, 30/32f), new UV(26/64f, 30/32f) },
				new UV[] { new UV(0, 18/32f), new UV(2/64f, 18/32f), new UV(2/64f, 30/32f), new UV(0, 30/32f) },
				new UV[] { new UV(26/64f, 30/32f), new UV(50/64f, 30/32f), new UV(50/64f, 32/32f), new UV(26/64f, 32/32f) },
			};
		
		addBox(obj, -0.5f,-0.25f,0.417f, 0.5f,0.25f,0.5f, rt, mtlSides, uvSides, drawSides);
	}

}
