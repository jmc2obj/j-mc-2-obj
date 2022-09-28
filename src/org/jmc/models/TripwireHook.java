package org.jmc.models;

import javax.annotation.Nonnull;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for tripwire hooks
 */
public class TripwireHook extends BlockModel
{

	@Nonnull
	private NamespaceID[] getMtlSides(BlockData data, NamespaceID biome, int i)
	{
		NamespaceID[] abbrMtls = materials.get(data.state,biome);

		NamespaceID[] mtlSides = new NamespaceID[6];
		mtlSides[0] = abbrMtls[i];
		mtlSides[1] = abbrMtls[i];
		mtlSides[2] = abbrMtls[i];
		mtlSides[3] = abbrMtls[i];
		mtlSides[4] = abbrMtls[i];
		mtlSides[5] = abbrMtls[i];
		return mtlSides;
	}


	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		String dir = data.state.get("facing");
		boolean connected = data.state.get("attached").equals("true");
		
		/*
		 The model is rendered facing south and then rotated  
		*/
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform baseTrans;

		switch (dir)
		{
			case "south": rotate = Transform.rotation(0, 0, 0); break;
			case "west": rotate = Transform.rotation(0, 90, 0); break;
			case "north": rotate = Transform.rotation(0, 180, 0); break;
			case "east": rotate = Transform.rotation(0, -90, 0); break;
		}
		translate = Transform.translation(x, y, z);
		baseTrans = translate.multiply(rotate);
		
		boolean[] drawSides;
		UV[] uvTop, uvFront, uvSide;
		UV[][] uvSides;
		
		
		// base
		drawSides = new boolean[] {true,false,true,true,true,true};

		uvTop = new UV[] { new UV(6/16f,14/16f), new UV(10/16f,14/16f), new UV(10/16f,1), new UV(6/16f,1) };
		uvFront = new UV[] { new UV(6/16f,1/16f), new UV(10/16f,1/16f), new UV(10/16f,9/16f), new UV(6/16f,9/16f) };
		uvSide = new UV[] { new UV(0,1/16f), new UV(2/16f,1/16f), new UV(2/16f,9/16f), new UV(0,9/16f) };
		uvSides = new UV[][] { uvTop, null, uvFront, uvSide, uvSide, uvTop };
		
		addBox(obj, 
				-0.125f, -0.4375f, -0.5f, 
				0.125f, 0.0625f, -0.375f, 
				baseTrans, 
				getMtlSides(data,biome,0),
				uvSides,
				drawSides);

		// hook lever
		if (!connected)
		{
			rotate = Transform.rotation(-35, 0, 0);
			translate = Transform.translation(0f, 0.0625f, -0.1f);
		}
		else
		{
			rotate = Transform.rotation(10, 0, 0);
			translate = Transform.translation(0f, -0.1875f, -0.05f);
		}
		
		uvTop = new UV[] { new UV(9/16f,7/16f), new UV(7/16f,7/16f), new UV(7/16f,0), new UV(9/16f,0) };
		uvFront = new UV[] { new UV(7/16f,5/16f), new UV(9/16f,5/16f), new UV(9/16f,7/16f), new UV(7/16f,7/16f) };
		uvSide = new UV[] { new UV(9/16f,0), new UV(9/16f,7/16f), new UV(7/16f,7/16f), new UV(7/16f,0) };
		uvSides = new UV[][] { uvTop, null, uvFront, uvSide, uvSide, uvTop };

		addBox(obj, 
				-0.05f, -0.05f, -0.4f, 
				0.05f, 0.05f, -0.05f, 
				baseTrans.multiply(translate).multiply(rotate), 
				getMtlSides(data,biome,1),
				uvSides,
				drawSides);

		// hook ring
		if (!connected)
		{
			rotate = Transform.rotation(50, 0, 0);
			translate = Transform.translation(0f, -0.03f, -0.09f);
		}
		else
		{
			rotate = Transform.rotation(10, 0, 0);
			translate = Transform.translation(0f, -0.1875f, -0.025f);
		}

		uvTop = new UV[] { new UV(5/16f,7/16f), new UV(11/16f,7/16f), new UV(11/16f,13/16f), new UV(5/16f,13/16f) };
		uvSide = new UV[] { new UV(5/16f,11/16f), new UV(11/16f,11/16f), new UV(11/16f,13/16f), new UV(5/16f,13/16f) };
		uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };

		addBox(obj, 
				-0.1f, -0.02f, -0.1f, 
				0.1f, 0.02f, 0.1f, 
				baseTrans.multiply(translate).multiply(rotate), 
				getMtlSides(data,biome,1),
				uvSides,
				null);
		
		// wire
		if (connected)
		{
			Vertex[] vertices = new Vertex[4];
			uvTop = new UV[] { new UV(0,14/16f), new UV(0,12/16f), new UV(1,12/16f), new UV(1,14/16f) };
			
			vertices[0] = new Vertex(-0.0156f, -0.4375f, 0.5f);
			vertices[1] = new Vertex( 0.0156f, -0.4375f, 0.5f);
			vertices[2] = new Vertex( 0.0156f, -0.4375f, 0.25f);
			vertices[3] = new Vertex(-0.0156f, -0.4375f, 0.25f);
			obj.addFace(vertices, uvTop, baseTrans, materials.get(data.state,biome)[2]);

			vertices[0] = new Vertex(-0.0156f, -0.4375f, 0.25f);
			vertices[1] = new Vertex( 0.0156f, -0.4375f, 0.25f);
			vertices[2] = new Vertex( 0.0156f, -0.25f, 0.1f);
			vertices[3] = new Vertex(-0.0156f, -0.25f, 0.1f);
			obj.addFace(vertices, uvTop, baseTrans, materials.get(data.state,biome)[2]);
		}
	}

}
