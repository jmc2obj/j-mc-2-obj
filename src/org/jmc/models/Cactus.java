package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for cactus
 */
public class Cactus extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		String[] mtlSides = getMtlSides(data,biome);
		boolean[] drawSides = drawSides(chunks, x, y, z);

		Vertex[] vertices = new Vertex[4];

		UV[] uv = new UV[4];
		uv[0] = new UV(1/16f, 1/16f);
		uv[1] = new UV(15/16f, 1/16f);
		uv[2] = new UV(15/16f, 15/16f);
		uv[3] = new UV(1/16f, 15/16f);

		if (drawSides[0])
		{	// top
			vertices[0] = new Vertex(x-7/16f, y+0.5f, z+7/16f);
			vertices[1] = new Vertex(x+7/16f, y+0.5f, z+7/16f);
			vertices[2] = new Vertex(x+7/16f, y+0.5f, z-7/16f);
			vertices[3] = new Vertex(x-7/16f, y+0.5f, z-7/16f);
			obj.addFace(vertices, uv, null, mtlSides[0]);
		}
		if (drawSides[1])
		{	// front
			vertices[0] = new Vertex(x+0.5f, y-0.5f, z-7/16f);
			vertices[1] = new Vertex(x-0.5f, y-0.5f, z-7/16f);
			vertices[2] = new Vertex(x-0.5f, y+0.5f, z-7/16f);
			vertices[3] = new Vertex(x+0.5f, y+0.5f, z-7/16f);
			obj.addFace(vertices, null, null, mtlSides[1]);
		}
		if (drawSides[2])
		{	// back
			vertices[0] = new Vertex(x-0.5f, y-0.5f, z+7/16f);
			vertices[1] = new Vertex(x+0.5f, y-0.5f, z+7/16f);
			vertices[2] = new Vertex(x+0.5f, y+0.5f, z+7/16f);
			vertices[3] = new Vertex(x-0.5f, y+0.5f, z+7/16f);
			obj.addFace(vertices, null, null, mtlSides[2]);
		}
		if (drawSides[3])
		{	// left
			vertices[0] = new Vertex(x-7/16f, y-0.5f, z-0.5f);
			vertices[1] = new Vertex(x-7/16f, y-0.5f, z+0.5f);
			vertices[2] = new Vertex(x-7/16f, y+0.5f, z+0.5f);
			vertices[3] = new Vertex(x-7/16f, y+0.5f, z-0.5f);
			obj.addFace(vertices, null, null, mtlSides[3]);
		}
		if (drawSides[4])
		{	// right
			vertices[0] = new Vertex(x+7/16f, y-0.5f, z-0.5f);
			vertices[1] = new Vertex(x+7/16f, y-0.5f, z+0.5f);
			vertices[2] = new Vertex(x+7/16f, y+0.5f, z+0.5f);
			vertices[3] = new Vertex(x+7/16f, y+0.5f, z-0.5f);
			obj.addFace(vertices, null, null, mtlSides[4]);
		}
		if (drawSides[5])
		{	// bottom
			vertices[0] = new Vertex(x+7/16f, y-0.5f, z+7/16f);
			vertices[1] = new Vertex(x-7/16f, y-0.5f, z+7/16f);
			vertices[2] = new Vertex(x-7/16f, y-0.5f, z-7/16f);
			vertices[3] = new Vertex(x+7/16f, y-0.5f, z-7/16f);
			obj.addFace(vertices, uv, null, mtlSides[5]);
		}
	}

}
