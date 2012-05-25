package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;


/**
 * Model for cactus
 */
public class Cactus extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		String[] mtlSides = getMtlSides(data);
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
