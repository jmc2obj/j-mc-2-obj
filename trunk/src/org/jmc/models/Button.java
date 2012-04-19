package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;


/**
 * Model for Buttons.
 */
public class Button extends BoxModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		String[] mtlSides = getMtlSides(data);
		
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		switch (data & 7)
		{
			case 1:
				rotate.rotate(0, -90, 0);
				break;
			case 2:
				rotate.rotate(0, 90, 0);
				break;
			case 3:
				rotate.rotate(0, 0, 0);
				break;
			case 4:
				rotate.rotate(0, 180, 0);
				break;
		}
		translate.translate(x, y, z);		
			
		rt = translate.multiply(rotate);
		
		
		boolean[] drawSides = new boolean[] {true,false,true,true,true,true};
		
		UV[] uvTop = new UV[] { new UV(5/16f,14/16f), new UV(11/16f,14/16f), new UV(11/16f,1), new UV(5/16f,1) };
		UV[] uvFront = new UV[] { new UV(5/16f,6/16f), new UV(11/16f,6/16f), new UV(11/16f,10/16f), new UV(5/16f,10/16f) };
		UV[] uvSide = new UV[] { new UV(0,6/16f), new UV(2/16f,6/16f), new UV(2/16f,10/16f), new UV(0,10/16f) };
		UV[][] uvSides = new UV[][] { uvTop, uvFront, uvFront, uvSide, uvSide, uvTop };
		
		addBox(obj, -0.1875f,-0.125f,-0.5f, 0.1875f,0.125f,-0.375f, rt, mtlSides, uvSides, drawSides);
	}

}
