package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;

public class Lantern extends BlockModel
{
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		NamespaceID[] mtls = getMtlSides(data, biome);
		UV[] uvTop, uvSide;
		UV[][] uvSides;
		
		boolean hanging = data.state.getBool("hanging", false);
		// If hanging, we'll need to move 1 pixel up
		float hangOffset = hanging ? 1/16f : 0;
		
		
		// Bottom lantern portion
		uvTop = new UV[] { new UV(0, 1/16f), new UV(6/16f, 1/16f), new UV(6/16f, 7/16f), new UV(0, 7/16f) };
		uvSide = new UV[] { new UV(0, 7/16f), new UV(6/16f, 7/16f), new UV(6/16f, 14/16f), new UV(0, 14/16f) };
		uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
		addBox(obj, x-3/16f, y-8/16f+hangOffset, z-3/16f, x+3/16f, y-1/16f+hangOffset, z+3/16f, null, mtls, uvSides, null);
		
		// Small chunk above main portion
		uvTop = new UV[] { new UV(1/16f, 2/16f), new UV(5/16f, 2/16f), new UV(5/16f, 6/16f), new UV(1/16f, 6/16f) };
		uvSide = new UV[] { new UV(1/16f, 14/16f), new UV(5/16f, 14/16f), new UV(5/16f, 16/16f), new UV(1/16f, 16/16f) };
		uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
		addBox(obj, x-2/16f, y-1/16f+hangOffset, z-2/16f, x+2/16f, y+1/16f+hangOffset, z+2/16f, null, mtls, uvSides, null);		

		// If hanging, draw full chain
		final NamespaceID material = materials.get(data.state, biome)[0];
		if (hanging)
		{
			Transform move = Transform.translation(x, y, z);
			Vertex[] vertices = new Vertex[4];
			
			uvSide = new UV[] { new UV(11/16f, 10/16f), new UV(14/16f, 10/16f), new UV(14/16f, 16/16f), new UV(11/16f, 16/16f) };
			vertices[0] = new Vertex(1/16f,2/16f,-1/16f);
			vertices[1] = new Vertex(-1/16f,2/16f,1/16f);
			vertices[2] = new Vertex(-1/16f,8/16f,1/16f);
			vertices[3] = new Vertex(1/16f,8/16f,-1/16f);
			obj.addDoubleSidedFace(vertices, uvSide, move, material);
            
			uvSide = new UV[] { new UV(11/16f, 4/16f), new UV(14/16f, 4/16f), new UV(14/16f, 10/16f), new UV(11/16f, 10/16f) };			
			vertices[0] = new Vertex(-1/16f,2/16f,-1/16f);
			vertices[1] = new Vertex(1/16f,2/16f,1/16f);
			vertices[2] = new Vertex(1/16f,8/16f,1/16f);
			vertices[3] = new Vertex(-1/16f,8/16f,-1/16f);
			obj.addDoubleSidedFace(vertices, uvSide, move, material);
		}	
		// Otherwise we'll just draw the small chain hook
		else
		{
			Transform move = Transform.translation(x, y, z);
			Vertex[] vertices = new Vertex[4];
			
			uvSide = new UV[] { new UV(11/16f, 4/16f), new UV(14/16f, 4/16f), new UV(14/16f, 6/16f), new UV(11/16f, 6/16f) };	
			vertices[0] = new Vertex(1/16f,1/16f,-1/16f);
			vertices[1] = new Vertex(-1/16f,1/16f,1/16f);
			vertices[2] = new Vertex(-1/16f,3/16f,1/16f);
			vertices[3] = new Vertex(1/16f,3/16f,-1/16f);
			obj.addDoubleSidedFace(vertices, uvSide, move, material);
            		
			vertices[0] = new Vertex(-1/16f,1/16f,-1/16f);
			vertices[1] = new Vertex(1/16f,1/16f,1/16f);
			vertices[2] = new Vertex(1/16f,3/16f,1/16f);
			vertices[3] = new Vertex(-1/16f,3/16f,-1/16f);
			obj.addDoubleSidedFace(vertices, uvSide, move, material);
		}
	}
}
