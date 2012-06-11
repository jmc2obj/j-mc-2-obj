package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;


/**
 * Model for torches.
 */
public class Torch extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome)
	{
		String[] mtls = materials.get(data,biome);

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform txTorch, txFlame;

		switch(data)
		{
			case 1:
				rotate.rotate(0, 0, -25);
				translate.translate(x-0.3f, y+0.125f, z);
				txTorch = translate.multiply(rotate);

				translate.translate(x-0.26f, y+0.1f, z);
				txFlame = translate;
				break;
			case 2:
				rotate.rotate(0, 0, 25);
				translate.translate(x+0.3f, y+0.125f, z);
				txTorch = translate.multiply(rotate);

				translate.translate(x+0.26f, y+0.1f, z);
				txFlame = translate;
				break;
			case 3:
				rotate.rotate(25, 0, 0);			
				translate.translate(x, y+0.125f, z-0.3f);
				txTorch = translate.multiply(rotate);

				translate.translate(x, y+0.1f, z-0.26f);
				txFlame = translate;
				break;
			case 4:
				rotate.rotate(-25, 0, 0);
				translate.translate(x, y+0.125f, z+0.3f);
				txTorch = translate.multiply(rotate);

				translate.translate(x, y+0.1f, z+0.26f);
				txFlame = translate;
				break;
			default:
				translate.translate(x, y, z);
				txTorch = translate;
				txFlame = translate;
				break;
		}
		
		Vertex[] vertices = new Vertex[4];

		// front
		vertices[0] = new Vertex( 0.5f, -0.5f, -1/16f);
		vertices[1] = new Vertex(-0.5f, -0.5f, -1/16f);
		vertices[2] = new Vertex(-0.5f,  0.5f, -1/16f);
		vertices[3] = new Vertex( 0.5f,  0.5f, -1/16f);
		obj.addFace(vertices, null, txTorch, mtls[0]);

		// back
		vertices[0] = new Vertex(-0.5f, -0.5f, 1/16f);
		vertices[1] = new Vertex( 0.5f, -0.5f, 1/16f);
		vertices[2] = new Vertex( 0.5f,  0.5f, 1/16f);
		vertices[3] = new Vertex(-0.5f,  0.5f, 1/16f);
		obj.addFace(vertices, null, txTorch, mtls[0]);

		// left
		vertices[0] = new Vertex(-1/16f, -0.5f, -0.5f);
		vertices[1] = new Vertex(-1/16f, -0.5f,  0.5f);
		vertices[2] = new Vertex(-1/16f,  0.5f,  0.5f);
		vertices[3] = new Vertex(-1/16f,  0.5f, -0.5f);
		obj.addFace(vertices, null, txTorch, mtls[0]);

		// right
		vertices[0] = new Vertex(1/16f, -0.5f, -0.5f);
		vertices[1] = new Vertex(1/16f, -0.5f,  0.5f);
		vertices[2] = new Vertex(1/16f,  0.5f,  0.5f);
		vertices[3] = new Vertex(1/16f,  0.5f, -0.5f);
		obj.addFace(vertices, null, txTorch, mtls[0]);
		
		// flame (only if a texture was specified for it)
		if (mtls.length > 1)
		{
			vertices[0] = new Vertex( 0.125f, 0.125f, -0.125f);				
			vertices[1] = new Vertex(-0.125f, 0.125f,  0.125f);
			vertices[2] = new Vertex(-0.125f, 0.375f,  0.125f); 				
			vertices[3] = new Vertex( 0.125f, 0.375f, -0.125f);	
			obj.addFace(vertices, null, txFlame, mtls[1]);
			
			vertices[0] = new Vertex(-0.125f, 0.125f, -0.125f);		
			vertices[1] = new Vertex( 0.125f, 0.125f,  0.125f);
			vertices[2] = new Vertex( 0.125f, 0.375f,  0.125f);	
			vertices[3] = new Vertex(-0.125f, 0.375f, -0.125f);
			obj.addFace(vertices, null, txFlame, mtls[1]);
		}
	}

}
