package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Side;
import org.jmc.geom.Vertex;


/**
 * Generic model for blocks rendered as 4 crossed polygons, like crops.
 */
public class Crosshatch extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		Vertex[] vertices = new Vertex[4];

		vertices[0] = new Vertex(x-0.5f, y-0.5f, z-0.25f);
		vertices[1] = new Vertex(x-0.5f, y+0.5f, z-0.25f);
		vertices[2] = new Vertex(x+0.5f, y+0.5f, z-0.25f);
		vertices[3] = new Vertex(x+0.5f, y-0.5f, z-0.25f);
		obj.addFace(vertices, null, Side.FRONT, materials.get(data)[0]);

		vertices[0] = new Vertex(x+0.5f, y-0.5f, z+0.25f);
		vertices[1] = new Vertex(x+0.5f, y+0.5f, z+0.25f);
		vertices[2] = new Vertex(x-0.5f, y+0.5f, z+0.25f);
		vertices[3] = new Vertex(x-0.5f, y-0.5f, z+0.25f);
		obj.addFace(vertices, null ,Side.BACK, materials.get(data)[0]);

		vertices[0] = new Vertex(x-0.25f, y-0.5f, z+0.5f);
		vertices[1] = new Vertex(x-0.25f, y+0.5f, z+0.5f);
		vertices[2] = new Vertex(x-0.25f, y+0.5f, z-0.5f);
		vertices[3] = new Vertex(x-0.25f, y-0.5f, z-0.5f);
		obj.addFace(vertices, null, Side.LEFT, materials.get(data)[0]);

		vertices[0] = new Vertex(x+0.25f, y-0.5f, z+0.5f);
		vertices[1] = new Vertex(x+0.25f, y+0.5f, z+0.5f);
		vertices[2] = new Vertex(x+0.25f, y+0.5f, z-0.5f);
		vertices[3] = new Vertex(x+0.25f, y-0.5f, z-0.5f);
		obj.addFace(vertices, null, Side.RIGHT, materials.get(data)[0]);
	}

}
