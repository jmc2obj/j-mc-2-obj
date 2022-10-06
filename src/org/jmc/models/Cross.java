package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ObjChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Generic model for blocks rendered as 2 crossed polygons, like saplings.
 */
public class Cross extends BlockModel
{

	@Override
	public void addModel(ObjChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		Transform move = Transform.translation(x, y, z);

		final NamespaceID material = materials.get(data.state, biome)[0];
		
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(+0.5f,-0.5f,-0.5f);
		vertices[1] = new Vertex(-0.5f,-0.5f,+0.5f);
		vertices[2] = new Vertex(-0.5f,+0.5f,+0.5f);
		vertices[3] = new Vertex(+0.5f,+0.5f,-0.5f);
		obj.addDoubleSidedFace(vertices, null, move, material);
		
		vertices[0] = new Vertex(-0.5f,-0.5f,-0.5f);
		vertices[1] = new Vertex(+0.5f,-0.5f,+0.5f);
		vertices[2] = new Vertex(+0.5f,+0.5f,+0.5f);
		vertices[3] = new Vertex(-0.5f,+0.5f,-0.5f);
		obj.addDoubleSidedFace(vertices, null, move, material);
	}

}
