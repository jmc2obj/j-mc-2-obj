package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;


/**
 * Model for wall signs.
 */
public class SignPost extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome)
	{
		String[] mtlSides = getMtlSides(data,biome);
		UV[][] uvSides;
		boolean[] drawSides;
		
		
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		float r = 0;
		switch (data & 15)
		{
			case 0: r = -180f; break;
			case 1: r = -157.5f; break;
			case 2: r = -135f; break;
			case 3: r = -112.5f; break;
			case 4: r = -90f; break;
			case 5: r = -67.5f; break;
			case 6: r = -45f; break;
			case 7: r = -22.5f; break;
			case 8: r = 0f; break;
			case 9: r = 22.5f; break;
			case 10: r = 45f; break;
			case 11: r = 67.5f; break;
			case 12: r = 90f; break;
			case 13: r = 112.5f; break;
			case 14: r = 135f; break;
			case 15: r = 157.5f; break;
		}
		rotate.rotate(0, r, 0);
		translate.translate(x, y, z);		
		rt = translate.multiply(rotate);
		

		// post
		drawSides = new boolean[] { false, true, true, true, true, false };
		uvSides = new UV[][] {
				null,
				new UV[] { new UV(2/64f, 2/32f), new UV(4/64f, 2/32f), new UV(4/64f, 16/32f), new UV(2/64f, 16/32f) },
				new UV[] { new UV(6/64f, 2/32f), new UV(8/64f, 2/32f), new UV(8/64f, 16/32f), new UV(6/64f, 16/32f) },
				new UV[] { new UV(0/64f, 2/32f), new UV(2/64f, 2/32f), new UV(2/64f, 16/32f), new UV(0/64f, 16/32f) },
				new UV[] { new UV(4/64f, 2/32f), new UV(6/64f, 2/32f), new UV(6/64f, 16/32f), new UV(4/64f, 16/32f) },
				null
			};
		addBox(obj, -0.0417f,-0.5f,-0.0417f, 0.0417f,0.0833f,0.0417f, rt, mtlSides, uvSides, drawSides);

		// sign
		uvSides = new UV[][] {
				new UV[] { new UV(26/64f, 32/32f), new UV(2/64f, 32/32f), new UV(2/64f, 30/32f), new UV(26/64f, 30/32f) },
				new UV[] { new UV(2/64f, 18/32f), new UV(26/64f, 18/32f), new UV(26/64f, 30/32f), new UV(2/64f, 30/32f) },
				new UV[] { new UV(28/64f, 18/32f), new UV(52/64f, 18/32f), new UV(52/64f, 30/32f), new UV(28/64f, 30/32f) },
				new UV[] { new UV(26/64f, 18/32f), new UV(28/64f, 18/32f), new UV(28/64f, 30/32f), new UV(26/64f, 30/32f) },
				new UV[] { new UV(0, 18/32f), new UV(2/64f, 18/32f), new UV(2/64f, 30/32f), new UV(0, 30/32f) },
				new UV[] { new UV(26/64f, 30/32f), new UV(50/64f, 30/32f), new UV(50/64f, 32/32f), new UV(26/64f, 32/32f) },
			};
		addBox(obj, -0.5f,0.0833f,-0.0417f, 0.5f,0.5833f,0.0417f, rt, mtlSides, uvSides, null);
	}

}
