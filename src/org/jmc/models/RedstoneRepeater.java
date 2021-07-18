package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for redstone repeaters.
 */
public class RedstoneRepeater extends BlockModel
{

	@Override
	protected NamespaceID[] getMtlSides(BlockData data, int biome)
	{
		NamespaceID[] abbrMtls = materials.get(data.state,biome);

		NamespaceID[] mtlSides = new NamespaceID[6];
		
		if (data.state.get("powered").equals("true")) {
			mtlSides[0] = abbrMtls[0];			
		} else {
			mtlSides[0] = abbrMtls[1];	
		}	
		mtlSides[1] = abbrMtls[2];
		mtlSides[2] = abbrMtls[2];
		mtlSides[3] = abbrMtls[2];
		mtlSides[4] = abbrMtls[2];
		mtlSides[5] = abbrMtls[3];
		return mtlSides;
	}
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		String dir = data.state.get("facing");
		int delay = Integer.parseInt(data.state.get("delay"))-1;
		boolean locked = Boolean.parseBoolean(data.state.get("locked"));
		

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		switch (dir)
		{
			case "west":
				rotate = Transform.rotation(0, 90, 0);
				break;
			case "north":
				rotate = Transform.rotation(0, 180, 0);
				break;
			case "east":
				rotate = Transform.rotation(0, -90, 0);
				break;
		}
		translate = Transform.translation(x, y, z);		
		rt = translate.multiply(rotate);

		
		NamespaceID[] mtlsBase = getMtlSides(data,biome);
		NamespaceID mtlTorch;
		if (data.state.get("powered").equals("true")) {
			mtlTorch = materials.get(data.state,biome)[4];		
		} else {
			mtlTorch = materials.get(data.state,biome)[5];	
		}			

		// base
		boolean[] drawSides = new boolean[] { true, true, true, true, true, drawSides(chunks,x,y,z, data)[5] };

		UV[] uvSide = new UV[] { new UV(0, 0), new UV(1, 0), new UV(1, 2/16f), new UV(0, 2/16f) };
		UV[][] uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };

		addBox(obj,
				-0.5f, -0.5f, -0.5f,
				0.5f, -0.375f, 0.5f, 
				rt, 
				mtlsBase, 
				uvSides, 
				drawSides);

		uvSide = new UV[] { new UV(5/16f, 5/16f), new UV(11/16f, 5/16f), new UV(11/16f, 1), new UV(5/16f, 1) };		
		UV[] uvTop = new UV[] { new UV(7/16f, 8/16f), new UV(9/16f, 8/16f), new UV(9/16f, 10/16f), new UV(7/16f, 10/16f) };		
		
		// fixed torch
		// front
		obj.addFace(new Vertex[] {new Vertex(3/16f, -6/16f, -6/16f), new Vertex(-3/16f, -6/16f, -6/16f), new Vertex(-3/16f, 5/16f, -6/16f), new Vertex(3/16f, 5/16f, -6/16f)}, uvSide, rt, mtlTorch);
		// back
		obj.addFace(new Vertex[] {new Vertex(-3/16f, -6/16f, -4/16f), new Vertex(3/16f, -6/16f, -4/16f), new Vertex(3/16f, 5/16f, -4/16f), new Vertex(-3/16f, 5/16f, -4/16f)}, uvSide, rt, mtlTorch);
		// left
		obj.addFace(new Vertex[] {new Vertex(-1/16f, -6/16f, -8/16f), new Vertex(-1/16f, -6/16f, -2/16f), new Vertex(-1/16f, 5/16f, -2/16f), new Vertex(-1/16f, 5/16f, -8/16f)}, uvSide, rt, mtlTorch);
		// right
		obj.addFace(new Vertex[] {new Vertex(1/16f, -6/16f, -8/16f), new Vertex(1/16f, -6/16f, -2/16f), new Vertex(1/16f, 5/16f, -2/16f), new Vertex(1/16f, 5/16f, -8/16f)}, uvSide, rt, mtlTorch);
		// top
		obj.addFace(new Vertex[] {new Vertex(1/16f, -1/16f, -4/16f), new Vertex(1/16f, -1/16f, -6/16f), new Vertex(-1/16f, -1/16f, -6/16f), new Vertex(-1/16f, -1/16f, -4/16f)}, uvTop, rt, mtlTorch);
		
		if (locked) {
			// delay bar
			NamespaceID[] barSides = new NamespaceID[6];
			java.util.Arrays.fill(barSides, materials.get(data.state,biome)[6]);
			
			UV[][] uvBarSides = new UV[][] {
				new UV[] { new UV( 9/16f,  2/16f), new UV( 9/16f, 14/16f), new UV( 7/16f, 14/16f), new UV( 7/16f,  2/16f) }, //t
				new UV[] { new UV( 2/16f,  7/16f), new UV(14/16f,  7/16f), new UV(14/16f,  9/16f), new UV( 2/16f,  9/16f) }, //f
				new UV[] { new UV( 2/16f,  7/16f), new UV(14/16f,  7/16f), new UV(14/16f,  9/16f), new UV( 2/16f,  9/16f) }, //b
				new UV[] { new UV( 7/16f,  7/16f), new UV( 9/16f,  7/16f), new UV( 9/16f,  9/16f), new UV( 7/16f,  9/16f) }, //l
				new UV[] { new UV( 7/16f,  7/16f), new UV( 9/16f,  7/16f), new UV( 9/16f,  9/16f), new UV( 7/16f,  9/16f) }, //r
				new UV[] { new UV( 9/16f,  2/16f), new UV( 9/16f, 14/16f), new UV( 7/16f, 14/16f), new UV( 7/16f,  2/16f) }};//b
				
			addBox(obj, -6/16f, -6/16f, (2*delay-2)/16f, 6/16f, -4/16f, (2*delay-0)/16f,  rt,  barSides,  uvBarSides,  null);
		} else {
			// delay torch
			// front
			obj.addFace(new Vertex[] {new Vertex(3/16f, -6/16f, (2*delay-2)/16f), new Vertex(-3/16f, -6/16f, (2*delay-2)/16f), new Vertex(-3/16f, 5/16f, (2*delay-2)/16f), new Vertex(3/16f, 5/16f, (2*delay-2)/16f)}, uvSide, rt, mtlTorch);
			// back
			obj.addFace(new Vertex[] {new Vertex(-3/16f, -6/16f, (2*delay-0)/16f), new Vertex(3/16f, -6/16f, (2*delay-0)/16f), new Vertex(3/16f, 5/16f, (2*delay-0)/16f), new Vertex(-3/16f, 5/16f, (2*delay-0)/16f)}, uvSide, rt, mtlTorch);
			// left
			obj.addFace(new Vertex[] {new Vertex(-1/16f, -6/16f, (2*delay-4)/16f), new Vertex(-1/16f, -6/16f, (2*delay+2)/16f), new Vertex(-1/16f, 5/16f, (2*delay+2)/16f), new Vertex(-1/16f, 5/16f, (2*delay-4)/16f)}, uvSide, rt, mtlTorch);
			// right
			obj.addFace(new Vertex[] {new Vertex(1/16f, -6/16f, (2*delay-4)/16f), new Vertex(1/16f, -6/16f, (2*delay+2)/16f), new Vertex(1/16f, 5/16f, (2*delay+2)/16f), new Vertex(1/16f, 5/16f, (2*delay-4)/16f)}, uvSide, rt, mtlTorch);
			// top
			obj.addFace(new Vertex[] {new Vertex(1/16f, -1/16f, (2*delay-0)/16f), new Vertex(1/16f, -1/16f, (2*delay-2)/16f), new Vertex(-1/16f, -1/16f, (2*delay-2)/16f), new Vertex(-1/16f, -1/16f, (2*delay-0)/16f)}, uvTop, rt, mtlTorch);
		}
	}

}
