package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Generic model for blocks rendered as 4 crossed polygons, like crops.
 */
public class Crosshatch extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		Vertex[] vertices = new Vertex[4];

		// front
		vertices[0] = new Vertex(x+0.5f, y-0.5f, z-0.25f);
		vertices[1] = new Vertex(x-0.5f, y-0.5f, z-0.25f);
		vertices[2] = new Vertex(x-0.5f, y+0.5f, z-0.25f);
		vertices[3] = new Vertex(x+0.5f, y+0.5f, z-0.25f);
		obj.addFace(vertices, null, null, materials.get(data,biome)[0]);

		// back
		vertices[0] = new Vertex(x-0.5f, y-0.5f, z+0.25f);
		vertices[1] = new Vertex(x+0.5f, y-0.5f, z+0.25f);
		vertices[2] = new Vertex(x+0.5f, y+0.5f, z+0.25f);
		vertices[3] = new Vertex(x-0.5f, y+0.5f, z+0.25f);
		obj.addFace(vertices, null, null, materials.get(data,biome)[0]);

		// left
		vertices[0] = new Vertex(x-0.25f, y-0.5f, z-0.5f);
		vertices[1] = new Vertex(x-0.25f, y-0.5f, z+0.5f);
		vertices[2] = new Vertex(x-0.25f, y+0.5f, z+0.5f);
		vertices[3] = new Vertex(x-0.25f, y+0.5f, z-0.5f);
		obj.addFace(vertices, null, null, materials.get(data,biome)[0]);

		// right
		vertices[0] = new Vertex(x+0.25f, y-0.5f, z-0.5f);
		vertices[1] = new Vertex(x+0.25f, y-0.5f, z+0.5f);
		vertices[2] = new Vertex(x+0.25f, y+0.5f, z+0.5f);
		vertices[3] = new Vertex(x+0.25f, y+0.5f, z-0.5f);
		obj.addFace(vertices, null, null, materials.get(data,biome)[0]);
	}

}
