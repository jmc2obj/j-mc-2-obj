package org.jmc.models;

import java.util.Random;

import org.jmc.BlockData;
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
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		
        // Generates a random number to offset the grass in the x and y.
        Random rX = new Random();
        rX.setSeed((x+z)*1000);
        float randomX = -0.2f + rX.nextFloat() * 0.4f;
        
        Random rZ = new Random();
        rZ.setSeed((x+z)*2000);       
        float randomZ = -0.2f + rZ.nextFloat() * 0.4f;	
       		
		boolean top = data.state.get("half").equals("upper");
		if (top) {
			// must get the type of plant from the block below
			data = chunks.getBlockData(x, y-1, z);
		}

		String[] mtls = materials.get(data.state, biome);

		Transform t = Transform.translation(x+randomX, y, z+randomZ);
		
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

			if (chunks.getBlockID(x, y, z).equals("minecraft:sunflower")) {
				// Sunflower
				Double o = (double)(x*y*z);
				Transform r = Transform.rotation(0, (o.hashCode() % 30) - 15, 15);
				
				Transform rt;
				rt = t.multiply(r);

				// Front of sunflower
				vertices[0] = new Vertex(0.125f,-0.375f,+0.5f);
				vertices[1] = new Vertex(0.125f,-0.375f,-0.5f);
				vertices[2] = new Vertex(0.125f,+0.625f,-0.5f);
				vertices[3] = new Vertex(0.125f,+0.625f,+0.5f);
				obj.addFace(vertices, null, rt, mtls[3]);
				
				// Back of sunflower
				vertices[0] = new Vertex(0.1245f,-0.375f,+0.5f);
				vertices[1] = new Vertex(0.1245f,-0.375f,-0.5f);
				vertices[2] = new Vertex(0.1245f,+0.625f,-0.5f);
				vertices[3] = new Vertex(0.1245f,+0.625f,+0.5f);
				obj.addFace(vertices, null, rt, mtls[2]);
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
