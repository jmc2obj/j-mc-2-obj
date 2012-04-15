package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Vertex;


/**
 * Generic model for blocks that lie flat, like lilypads.
 */
public class Flat extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(x-0.5f, y-0.49f, z+0.5f);
		vertices[1] = new Vertex(x+0.5f, y-0.49f, z+0.5f);
		vertices[2] = new Vertex(x+0.5f, y-0.49f, z-0.5f);			
		vertices[3] = new Vertex(x-0.5f, y-0.49f, z-0.5f);
		obj.addFace(vertices, null, null, materials.get(data)[0]);
	}

}
