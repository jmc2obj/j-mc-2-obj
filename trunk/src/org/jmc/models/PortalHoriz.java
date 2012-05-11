package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Vertex;


/**
 * Generic model for horizontal portals (only end portal, currently).
 */
public class PortalHoriz extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		String mtl = materials.get(data)[0];
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
