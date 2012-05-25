package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;


/**
 * Model for fence gates
 */
public class FenceGate extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		int dir = (data & 3);
		boolean open = (data & 4) != 0;

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		switch (dir)
		{
			case 1: rotate.rotate(0, 90, 0); break;
			case 2: rotate.rotate(0, 180, 0); break;
			case 3: rotate.rotate(0, -90, 0); break;
		}
		translate.translate(x, y, z);
		rt = translate.multiply(rotate);

		
		boolean[] drawSides;
		UV[] uvTop, uvFront, uvSide;
		UV[][] uvSides;
		
		if (open)
		{
			//left door v bars
			uvTop = new UV[] { new UV(0,7/16f), new UV(2/16f,7/16f), new UV(2/16f,9/16f), new UV(0,9/16f) };
			uvFront = new UV[] { new UV(0,5/16f), new UV(2/16f,5/16f), new UV(2/16f,1), new UV(0,1) };
			uvSide = new UV[] { new UV(7/16f,5/16f), new UV(9/16f,5/16f), new UV(9/16f,1), new UV(7/16f,1) };
			uvSides = new UV[][] { uvTop, uvFront, uvFront, uvSide, uvSide, uvTop };
			addBox(obj,
					-0.5f, -0.1875f, -0.0625f,
					-0.375f, 0.5f, 0.0625f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					null);

			uvTop = new UV[] { new UV(0,0), new UV(2/16f,0), new UV(2/16f,2/16f), new UV(0,2/16f) };
			uvFront = new UV[] { new UV(0,6/16f), new UV(2/16f,6/16f), new UV(2/16f,15/16f), new UV(0,15/16f) };
			uvSide = new UV[] { new UV(14/16f,6/16f), new UV(1,6/16f), new UV(1,15/16f), new UV(14/16f,15/16f) };
			uvSides = new UV[][] { uvTop, uvFront, uvFront, uvSide, uvSide, uvTop };
			addBox(obj,
					-0.5f, -0.125f, 0.375f,
					-0.375f, 0.4375f, 0.5f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					null);

			//left door h bars
			drawSides = new boolean[] {true,false,false,true,true,true};

			uvTop = new UV[] { new UV(0,2/16f), new UV(2/16f,2/16f), new UV(2/16f,7/16f), new UV(0,7/16f) };
			uvSide = new UV[] { new UV(9/16f,12/16f), new UV(14/16f,12/16f), new UV(14/16f,15/16f), new UV(9/16f,15/16f) };
			uvSides = new UV[][] { uvTop, null, null, uvSide, uvSide, uvTop };
			addBox(obj,
					-0.5f, 0.25f, 0.0625f,
					-0.375f, 0.4375f, 0.375f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					drawSides);

			uvTop = new UV[] { new UV(0,2/16f), new UV(2/16f,2/16f), new UV(2/16f,7/16f), new UV(0,7/16f) };
			uvSide = new UV[] { new UV(9/16f,6/16f), new UV(14/16f,6/16f), new UV(14/16f,9/16f), new UV(9/16f,9/16f) };
			uvSides = new UV[][] { uvTop, null, null, uvSide, uvSide, uvTop };
			addBox(obj,
					-0.5f, -0.125f, 0.0625f,
					-0.375f, 0.0625f, 0.375f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					drawSides);
			
			//right door v bars
			uvTop = new UV[] { new UV(14/16f,7/16f), new UV(1,7/16f), new UV(1,9/16f), new UV(14/16f,9/16f) };
			uvFront = new UV[] { new UV(14/16f,5/16f), new UV(1,5/16f), new UV(1,1), new UV(14/16f,1) };
			uvSide = new UV[] { new UV(7/16f,5/16f), new UV(9/16f,5/16f), new UV(9/16f,1), new UV(7/16f,1) };
			uvSides = new UV[][] { uvTop, uvFront, uvFront, uvSide, uvSide, uvTop };
			addBox(obj,
					0.375f, -0.1875f, -0.0625f,
					0.5f, 0.5f, 0.0625f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					null);

			uvTop = new UV[] { new UV(14/16f,0), new UV(1,0), new UV(1,2/16f), new UV(14/16f,2/16f) };
			uvFront = new UV[] { new UV(14/16f,6/16f), new UV(1,6/16f), new UV(1,15/16f), new UV(14/16f,15/16f) };
			uvSide = new UV[] { new UV(14/16f,6/16f), new UV(1,6/16f), new UV(1,15/16f), new UV(14/16f,15/16f) };
			uvSides = new UV[][] { uvTop, uvFront, uvFront, uvSide, uvSide, uvTop };
			addBox(obj,
					0.375f, -0.125f, 0.375f,
					0.5f, 0.4375f, 0.5f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					null);
			
			//right door h bars
			uvTop = new UV[] { new UV(14/16f,2/16f), new UV(1,2/16f), new UV(1,7/16f), new UV(14/16f,7/16f) };
			uvSide = new UV[] { new UV(9/16f,12/16f), new UV(14/16f,12/16f), new UV(14/16f,15/16f), new UV(9/16f,15/16f) };
			uvSides = new UV[][] { uvTop, null, null, uvSide, uvSide, uvTop };
			addBox(obj,
					0.375f, 0.25f, 0.0625f,
					0.5f, 0.4375f, 0.375f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					drawSides);

			uvTop = new UV[] { new UV(14/16f,2/16f), new UV(1,2/16f), new UV(1,7/16f), new UV(14/16f,7/16f) };
			uvSide = new UV[] { new UV(9/16f,6/16f), new UV(14/16f,6/16f), new UV(14/16f,9/16f), new UV(9/16f,9/16f) };
			uvSides = new UV[][] { uvTop, null, null, uvSide, uvSide, uvTop };
			addBox(obj,
					0.375f, -0.125f, 0.0625f,
					0.5f, 0.0625f, 0.375f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					drawSides);
		}
		else
		{
			//left v bar
			uvTop = new UV[] { new UV(0,7/16f), new UV(2/16f,7/16f), new UV(2/16f,9/16f), new UV(0,9/16f) };
			uvFront = new UV[] { new UV(0,5/16f), new UV(2/16f,5/16f), new UV(2/16f,1), new UV(0,1) };
			uvSide = new UV[] { new UV(7/16f,5/16f), new UV(9/16f,5/16f), new UV(9/16f,1), new UV(7/16f,1) };
			uvSides = new UV[][] { uvTop, uvFront, uvFront, uvSide, uvSide, uvTop };
			addBox(obj,
					-0.5f, -0.1875f, -0.0625f,
					-0.375f, 0.5f, 0.0625f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					null);

			//right v bar
			uvTop = new UV[] { new UV(14/16f,7/16f), new UV(1,7/16f), new UV(1,9/16f), new UV(14/16f,9/16f) };
			uvFront = new UV[] { new UV(14/16f,5/16f), new UV(1,5/16f), new UV(1,1), new UV(14/16f,1) };
			uvSide = new UV[] { new UV(7/16f,5/16f), new UV(9/16f,5/16f), new UV(9/16f,1), new UV(7/16f,1) };
			uvSides = new UV[][] { uvTop, uvFront, uvFront, uvSide, uvSide, uvTop };
			addBox(obj,
					0.375f, -0.1875f, -0.0625f,
					0.5f, 0.5f, 0.0625f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					null);

			//top h bar
			drawSides = new boolean[] {true,true,true,false,false,true};

			uvTop = new UV[] { new UV(2/16f,7/16f), new UV(14/16f,7/16f), new UV(14/16f,9/16f), new UV(2/16f,9/16f) };
			uvFront = new UV[] { new UV(2/16f,12/16f), new UV(14/16f,12/16f), new UV(14/16f,15/16f), new UV(2/16f,15/16f) };
			uvSides = new UV[][] { uvTop, uvFront, uvFront, null, null, uvTop };
			addBox(obj,
					-0.375f, 0.25f, -0.0625f,
					0.375f, 0.4375f, 0.0625f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					drawSides);

			//bottom h bar
			uvTop = new UV[] { new UV(2/16f,7/16f), new UV(14/16f,7/16f), new UV(14/16f,9/16f), new UV(2/16f,9/16f) };
			uvFront = new UV[] { new UV(2/16f,6/16f), new UV(14/16f,6/16f), new UV(14/16f,9/16f), new UV(2/16f,9/16f) };
			uvSides = new UV[][] { uvTop, uvFront, uvFront, null, null, uvTop };
			addBox(obj,
					-0.375f, -0.125f, -0.0625f,
					0.375f, 0.0625f, 0.0625f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					drawSides);

			//center bar
			drawSides = new boolean[] {false,true,true,true,true,false};

			uvFront = new UV[] { new UV(6/16f,9/16f), new UV(10/16f,9/16f), new UV(10/16f,12/16f), new UV(6/16f,12/16f) };
			uvSide = new UV[] { new UV(7/16f,9/16f), new UV(9/16f,9/16f), new UV(9/16f,12/16f), new UV(7/16f,12/16f) };
			uvSides = new UV[][] { null, uvFront, uvFront, uvSide, uvSide, null };
			addBox(obj,
					-0.125f, 0.0625f, -0.0625f,
					0.125f, 0.25f, 0.0625f, 
					rt, 
					getMtlSides(data), 
					uvSides, 
					drawSides);
		}

	}

}
