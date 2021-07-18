package org.jmc.models;

import java.util.Random;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;

public class Bamboo extends BlockModel
{
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		Transform move = Transform.translation(x, y, z);
		
		NamespaceID[] mtls = getMtlSides(data, biome);
		NamespaceID[] mtls_Stalk = new NamespaceID [] { mtls[0], mtls[0], mtls[0], mtls[0], mtls[0], mtls[0] };
		
        // Generates a random number to offset the bamboo in the x and z.
        Random rX = new Random();
        rX.setSeed((x+z)*1000);
        float randomX = -0.35f + rX.nextFloat() * 0.7f;
        
        Random rZ = new Random();
        rZ.setSeed((x+z)*2000);       
        float randomZ = -0.35f + rZ.nextFloat() * 0.7f;
		
		UV[] uvTop, uvSide;
		UV[][] uvSides;

		// If age 0 (thinner bamboo, 2x2)
		if (data.state.getInt("age") == 0)
		{
			uvTop = new UV[] { new UV(14/16f, 14/16f), new UV(16/16f, 14/16f), new UV(16/16f, 16/16f), new UV(14/16f, 16/16f) };
			uvSide = new UV[] { new UV(6/16f, 0), new UV(8/16f, 0), new UV(8/16f, 16/16f), new UV(6/16f, 16/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, -1/16f + randomX, -0.5f, -1/16f + randomZ, 1/16f + randomX, 0.5f, 1/16f + randomZ, move, mtls_Stalk, uvSides, null);
		}
		// Otherwise age is 1 (thicker bamboo, 3x3)
		else 
		{
			uvTop = new UV[] { new UV(13/16f, 13/16f), new UV(16/16f, 13/16f), new UV(16/16f, 16/16f), new UV(13/16f, 16/16f) };
			uvSide = new UV[] { new UV(6/16f, 0), new UV(9/16f, 0), new UV(9/16f, 16/16f), new UV(6/16f, 16/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
			addBox(obj, -3/32f + randomX, -0.5f, -3/32f + randomZ, 3/32f + randomX, 0.5f, 3/32f + randomZ, move, mtls_Stalk, uvSides, null);
		}
		
		// Large leaves
		if (data.state.get("leaves").equals("large"))
		{
			Vertex[] vertices = new Vertex[4];
			vertices[0] = new Vertex(randomX,-0.5f,-0.5f + randomZ);
			vertices[1] = new Vertex(randomX,-0.5f,+0.5f + randomZ);
			vertices[2] = new Vertex(randomX,+0.5f,+0.5f + randomZ);
			vertices[3] = new Vertex(randomX,+0.5f,-0.5f + randomZ);
			obj.addFace(vertices, null, move, materials.get(data.state,biome)[1]);
			
			vertices[0] = new Vertex(-0.5f + randomX,-0.5f,randomZ);
			vertices[1] = new Vertex(+0.5f + randomX,-0.5f,randomZ);
			vertices[2] = new Vertex(+0.5f + randomX,+0.5f,randomZ);
			vertices[3] = new Vertex(-0.5f + randomX,+0.5f,randomZ);
			obj.addFace(vertices, null, move, materials.get(data.state,biome)[1]);
		}
		// Small leaves
		else if (data.state.get("leaves").equals("small"))
		{
			Vertex[] vertices = new Vertex[4];
			vertices[0] = new Vertex(randomX,-0.5f,-0.5f + randomZ);
			vertices[1] = new Vertex(randomX,-0.5f,+0.5f + randomZ);
			vertices[2] = new Vertex(randomX,+0.5f,+0.5f + randomZ);
			vertices[3] = new Vertex(randomX,+0.5f,-0.5f + randomZ);
			obj.addFace(vertices, null, move, materials.get(data.state,biome)[2]);
			
			vertices[0] = new Vertex(-0.5f + randomX,-0.5f,randomZ);
			vertices[1] = new Vertex(+0.5f + randomX,-0.5f,randomZ);
			vertices[2] = new Vertex(+0.5f + randomX,+0.5f,randomZ);
			vertices[3] = new Vertex(-0.5f + randomX,+0.5f,randomZ);
			obj.addFace(vertices, null, move, materials.get(data.state,biome)[2]);
		}
		// No leaves
		else 
		{
			
		}
		
	}
}
