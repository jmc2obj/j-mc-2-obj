package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for fire
 */
public class Fire extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		String[] mtlSides = getMtlSides(data,biome);

		Transform t = new Transform();
		t.translate(x, y, z);
		
		Vertex[] vertices = new Vertex[4];
		
		// front
		vertices[0] = new Vertex( 0.5f, -0.5f, -0.5f);
		vertices[1] = new Vertex(-0.5f, -0.5f, -0.5f);
		vertices[2] = new Vertex(-0.45f,  0.6f, -0.45f);
		vertices[3] = new Vertex( 0.45f,  0.6f, -0.45f);
		obj.addFace(vertices, null, t, mtlSides[1]);

		vertices[0] = new Vertex( 0.49f, -0.5f, -0.25f);
		vertices[1] = new Vertex(-0.49f, -0.5f, -0.25f);
		vertices[2] = new Vertex(-0.44f,  0.75f, 0.25f);
		vertices[3] = new Vertex( 0.44f,  0.75f, 0.25f);
		obj.addFace(vertices, null, t, mtlSides[3]);

		// back
		vertices[0] = new Vertex(-0.5f, -0.5f,  0.5f);
		vertices[1] = new Vertex( 0.5f, -0.5f,  0.5f);
		vertices[2] = new Vertex( 0.45f,  0.6f,  0.45f);
		vertices[3] = new Vertex(-0.45f,  0.6f,  0.45f);
		obj.addFace(vertices, null, t, mtlSides[2]);

		vertices[0] = new Vertex(-0.49f, -0.5f,  0.25f);
		vertices[1] = new Vertex( 0.49f, -0.5f,  0.25f);
		vertices[2] = new Vertex( 0.44f,  0.75f, -0.25f);
		vertices[3] = new Vertex(-0.44f,  0.75f, -0.25f);
		obj.addFace(vertices, null, t, mtlSides[4]);

		// left
		vertices[0] = new Vertex(-0.5f, -0.5f, -0.5f);
		vertices[1] = new Vertex(-0.5f, -0.5f,  0.5f);
		vertices[2] = new Vertex(-0.45f,  0.6f,  0.45f);
		vertices[3] = new Vertex(-0.45f,  0.6f, -0.45f);
		obj.addFace(vertices, null, t, mtlSides[3]);

		vertices[0] = new Vertex(-0.25f, -0.5f, -0.49f);
		vertices[1] = new Vertex(-0.25f, -0.5f,  0.49f);
		vertices[2] = new Vertex( 0.25f,  0.75f,  0.44f);
		vertices[3] = new Vertex( 0.25f,  0.75f, -0.44f);
		obj.addFace(vertices, null, t, mtlSides[1]);

		// right
		vertices[0] = new Vertex( 0.5f, -0.5f,  0.5f);
		vertices[1] = new Vertex( 0.5f, -0.5f, -0.5f);
		vertices[2] = new Vertex( 0.45f,  0.6f, -0.45f);
		vertices[3] = new Vertex( 0.45f,  0.6f,  0.45f);
		obj.addFace(vertices, null, t, mtlSides[4]);

		vertices[0] = new Vertex( 0.25f, -0.5f,  0.49f);
		vertices[1] = new Vertex( 0.25f, -0.5f, -0.49f);
		vertices[2] = new Vertex(-0.25f,  0.75f, -0.44f);
		vertices[3] = new Vertex(-0.25f,  0.75f,  0.44f);
		obj.addFace(vertices, null, t, mtlSides[2]);
	}

}
