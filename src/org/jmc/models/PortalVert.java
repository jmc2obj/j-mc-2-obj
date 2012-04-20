package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;


/**
 * Generic model for vertical portals (only nether portal, currently).
 */
public class PortalVert extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		// find in which direction are the other portal blocks
		boolean ns = chunks.getBlockID(x, y, z-1) == blockId || chunks.getBlockID(x, y, z+1) == blockId;
		
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		if (ns)
			rotate.rotate(0, 90, 0);
		translate.translate(x, y, z);		
		rt = translate.multiply(rotate);
		
		Vertex[] vertices = new Vertex[4];
		
		vertices[0] = new Vertex( 0.5f, -0.5f, -0.125f);
		vertices[1] = new Vertex(-0.5f, -0.5f, -0.125f);
		vertices[2] = new Vertex(-0.5f,  0.5f, -0.125f);
		vertices[3] = new Vertex( 0.5f,  0.5f, -0.125f);
		obj.addFace(vertices, null, rt, materials.get(data)[0]);

		vertices[0] = new Vertex(-0.5f, -0.5f, 0.125f);
		vertices[1] = new Vertex( 0.5f, -0.5f, 0.125f);
		vertices[2] = new Vertex( 0.5f,  0.5f, 0.125f);
		vertices[3] = new Vertex(-0.5f,  0.5f, 0.125f);
		obj.addFace(vertices, null, rt, materials.get(data)[0]);
	}

}
