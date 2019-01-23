package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Generic model for vertical portals (only nether portal, currently).
 */
public class PortalVert extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		// find in which direction are the other portal blocks
		boolean ns = data.get("axis").equals("y");
		
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
		obj.addFace(vertices, null, rt, materials.get(data,biome)[0]);

		vertices[0] = new Vertex(-0.5f, -0.5f, 0.125f);
		vertices[1] = new Vertex( 0.5f, -0.5f, 0.125f);
		vertices[2] = new Vertex( 0.5f,  0.5f, 0.125f);
		vertices[3] = new Vertex(-0.5f,  0.5f, 0.125f);
		obj.addFace(vertices, null, rt, materials.get(data,biome)[0]);
	}

}
