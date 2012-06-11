package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;


/**
 * Model for redstone repeaters.
 */
public class RedstoneRepeater extends BlockModel
{

	@Override
	protected String[] getMtlSides(byte data, byte biome)
	{
		String[] abbrMtls = materials.get(data,biome);

		String[] mtlSides = new String[6];
		mtlSides[0] = abbrMtls[0];
		mtlSides[1] = abbrMtls[1];
		mtlSides[2] = abbrMtls[1];
		mtlSides[3] = abbrMtls[1];
		mtlSides[4] = abbrMtls[1];
		mtlSides[5] = abbrMtls[2];
		return mtlSides;
	}
	
	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome)
	{
		int dir = data & 3;
		int delay = (data>>2) & 3;
		

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		switch (dir)
		{
			case 1:
				rotate.rotate(0, 90, 0);
				break;
			case 2:
				rotate.rotate(0, 180, 0);
				break;
			case 3:
				rotate.rotate(0, -90, 0);
				break;
		}
		translate.translate(x, y, z);		
		rt = translate.multiply(rotate);

		
		String[] mtlsBase = getMtlSides(data,biome);
		String mtlTorch = materials.get(data,biome)[3];

		// base
		boolean[] drawSides = new boolean[] { true, true, true, true, true, drawSides(chunks,x,y,z)[5] };

		UV[] uvSide = new UV[] { new UV(0, 0), new UV(1, 0), new UV(1, 2/16f), new UV(0, 2/16f) };
		UV[][] uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };

		addBox(obj,
				-0.5f, -0.5f, -0.5f,
				0.5f, -0.375f, 0.5f, 
				rt, 
				mtlsBase, 
				uvSides, 
				drawSides);

		// fixed torch
		uvSide = new UV[] { new UV(5/16f, 5/16f), new UV(11/16f, 5/16f), new UV(11/16f, 1), new UV(5/16f, 1) };
		// front
		obj.addFace(new Vertex[] {new Vertex(3/16f, -6/16f, -6/16f), new Vertex(-3/16f, -6/16f, -6/16f), new Vertex(-3/16f, 5/16f, -6/16f), new Vertex(3/16f, 5/16f, -6/16f)}, uvSide, rt, mtlTorch);
		// back
		obj.addFace(new Vertex[] {new Vertex(-3/16f, -6/16f, -4/16f), new Vertex(3/16f, -6/16f, -4/16f), new Vertex(3/16f, 5/16f, -4/16f), new Vertex(-3/16f, 5/16f, -4/16f)}, uvSide, rt, mtlTorch);
		// left
		obj.addFace(new Vertex[] {new Vertex(-1/16f, -6/16f, -8/16f), new Vertex(-1/16f, -6/16f, -2/16f), new Vertex(-1/16f, 5/16f, -2/16f), new Vertex(-1/16f, 5/16f, -8/16f)}, uvSide, rt, mtlTorch);
		// right
		obj.addFace(new Vertex[] {new Vertex(1/16f, -6/16f, -8/16f), new Vertex(1/16f, -6/16f, -2/16f), new Vertex(1/16f, 5/16f, -2/16f), new Vertex(1/16f, 5/16f, -8/16f)}, uvSide, rt, mtlTorch);
		
		// delay torch
		// front
		obj.addFace(new Vertex[] {new Vertex(3/16f, -6/16f, (2*delay-2)/16f), new Vertex(-3/16f, -6/16f, (2*delay-2)/16f), new Vertex(-3/16f, 5/16f, (2*delay-2)/16f), new Vertex(3/16f, 5/16f, (2*delay-2)/16f)}, uvSide, rt, mtlTorch);
		// back
		obj.addFace(new Vertex[] {new Vertex(-3/16f, -6/16f, (2*delay-0)/16f), new Vertex(3/16f, -6/16f, (2*delay-0)/16f), new Vertex(3/16f, 5/16f, (2*delay-0)/16f), new Vertex(-3/16f, 5/16f, (2*delay-0)/16f)}, uvSide, rt, mtlTorch);
		// left
		obj.addFace(new Vertex[] {new Vertex(-1/16f, -6/16f, (2*delay-4)/16f), new Vertex(-1/16f, -6/16f, (2*delay+2)/16f), new Vertex(-1/16f, 5/16f, (2*delay+2)/16f), new Vertex(-1/16f, 5/16f, (2*delay-4)/16f)}, uvSide, rt, mtlTorch);
		// right
		obj.addFace(new Vertex[] {new Vertex(1/16f, -6/16f, (2*delay-4)/16f), new Vertex(1/16f, -6/16f, (2*delay+2)/16f), new Vertex(1/16f, 5/16f, (2*delay+2)/16f), new Vertex(1/16f, 5/16f, (2*delay-4)/16f)}, uvSide, rt, mtlTorch);
	}

}
