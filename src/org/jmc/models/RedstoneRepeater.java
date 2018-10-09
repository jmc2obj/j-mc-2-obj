package org.jmc.models;

import java.util.HashMap;

import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for redstone repeaters.
 */
public class RedstoneRepeater extends BlockModel
{

	@Override
	protected String[] getMtlSides(HashMap<String, String> data, int biome)
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
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		String dir = data.get("facing");
		int delay = Integer.parseInt(data.get("delay"))-1;
		

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		switch (dir)
		{
			case "west":
				rotate.rotate(0, 90, 0);
				break;
			case "north":
				rotate.rotate(0, 180, 0);
				break;
			case "east":
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
