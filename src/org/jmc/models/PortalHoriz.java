package org.jmc.models;

import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Generic model for horizontal portals (only end portal, currently).
 */
public class PortalHoriz extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, byte data, byte biome)
	{
		String mtl = materials.get(data,biome)[0];
		Vertex[] vertices = new Vertex[4];
		
		vertices[0] = new Vertex(x-0.5f, y+0.25f, z+0.5f);
		vertices[1] = new Vertex(x+0.5f, y+0.25f, z+0.5f);
		vertices[2] = new Vertex(x+0.5f, y+0.25f, z-0.5f);
		vertices[3] = new Vertex(x-0.5f, y+0.25f, z-0.5f);
		obj.addFace(vertices, null, null, mtl);

		vertices[0] = new Vertex(x+0.5f, y-0.25f, z+0.5f);
		vertices[1] = new Vertex(x-0.5f, y-0.25f, z+0.5f);
		vertices[2] = new Vertex(x-0.5f, y-0.25f, z-0.5f);
		vertices[3] = new Vertex(x+0.5f, y-0.25f, z-0.5f);
		obj.addFace(vertices, null, null, mtl);
	}

}
