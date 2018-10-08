package org.jmc.models;

import java.util.HashMap;

import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for 2 block tall plants.
 */
public class DoublePlant extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		boolean top = (data & 8) != 0;
		if (top) {
			// must get the type of plant from the block below
			data = chunks.getBlockData(x, y-1, z);
		}

		String[] mtls = materials.get(data, biome);

		Transform t = new Transform();
		t.translate(x, y, z);
		
		if (top) {
			Vertex[] vertices = new Vertex[4];
			vertices[0] = new Vertex(+0.5f,-0.5f,-0.5f);
			vertices[1] = new Vertex(-0.5f,-0.5f,+0.5f);
			vertices[2] = new Vertex(-0.5f,+0.5f,+0.5f);
			vertices[3] = new Vertex(+0.5f,+0.5f,-0.5f);
			obj.addFace(vertices, null, t, mtls[1]);
			
			vertices[0] = new Vertex(-0.5f,-0.5f,-0.5f);
			vertices[1] = new Vertex(+0.5f,-0.5f,+0.5f);
			vertices[2] = new Vertex(+0.5f,+0.5f,+0.5f);
			vertices[3] = new Vertex(-0.5f,+0.5f,-0.5f);
			obj.addFace(vertices, null, t, mtls[1]);

			if (data == 0) {
				// Sunflower
				Transform r = new Transform();
				Double o = (double)(x*y*z);
				r.rotate(0, (o.hashCode() % 30) - 15, 15);
				
				Transform rt;
				rt = t.multiply(r);

				vertices[0] = new Vertex(0.125f,-0.375f,+0.5f);
				vertices[1] = new Vertex(0.125f,-0.375f,-0.5f);
				vertices[2] = new Vertex(0.125f,+0.625f,-0.5f);
				vertices[3] = new Vertex(0.125f,+0.625f,+0.5f);
				obj.addFace(vertices, null, rt, mtls[3]);
			}
		}
		else {
			Vertex[] vertices = new Vertex[4];
			vertices[0] = new Vertex(+0.5f,-0.5f,-0.5f);
			vertices[1] = new Vertex(-0.5f,-0.5f,+0.5f);
			vertices[2] = new Vertex(-0.5f,+0.5f,+0.5f);
			vertices[3] = new Vertex(+0.5f,+0.5f,-0.5f);
			obj.addFace(vertices, null, t, mtls[0]);
			
			vertices[0] = new Vertex(-0.5f,-0.5f,-0.5f);
			vertices[1] = new Vertex(+0.5f,-0.5f,+0.5f);
			vertices[2] = new Vertex(+0.5f,+0.5f,+0.5f);
			vertices[3] = new Vertex(-0.5f,+0.5f,-0.5f);
			obj.addFace(vertices, null, t, mtls[0]);
		}
	}

}
