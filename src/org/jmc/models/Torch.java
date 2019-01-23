package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for torches.
 */
public class Torch extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		String[] mtls = materials.get(data,biome);

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform txTorch, txFlame;
		
		if (data.containsKey("facing")) 
		{
			switch(data.get("facing"))
			{
				case "east":
					rotate.rotate(0, 0, -25);
					translate.translate(x-0.3f, y+0.125f, z);
					txTorch = translate.multiply(rotate);
	
					translate.translate(x-0.26f, y+0.125f, z);
					txFlame = translate;
					break;
				case "west":
					rotate.rotate(0, 0, 25);
					translate.translate(x+0.3f, y+0.125f, z);
					txTorch = translate.multiply(rotate);
	
					translate.translate(x+0.26f, y+0.125f, z);
					txFlame = translate;
					break;
				case "south":
					rotate.rotate(25, 0, 0);			
					translate.translate(x, y+0.125f, z-0.3f);
					txTorch = translate.multiply(rotate);
	
					translate.translate(x, y+0.125f, z-0.26f);
					txFlame = translate;
					break;
				case "north":
					rotate.rotate(-25, 0, 0);
					translate.translate(x, y+0.125f, z+0.3f);
					txTorch = translate.multiply(rotate);
	
					translate.translate(x, y+0.125f, z+0.26f);
					txFlame = translate;
					break;
				default:
					translate.translate(x, y, z);
					txTorch = translate;
					txFlame = translate;
					break;					
			}
		} 
		else 
		{
			translate.translate(x, y, z);
			txTorch = translate;
			txFlame = translate;			
		}
		
		Vertex[] vertices = new Vertex[4];
		UV[] uv = new UV[4];

		// top
		vertices[0] = new Vertex(-1/16f, 2/16f,  1/16f); uv[0] = new UV(7/16f, 8/16f);
		vertices[1] = new Vertex( 1/16f, 2/16f,  1/16f); uv[1] = new UV(9/16f, 8/16f);
		vertices[2] = new Vertex( 1/16f, 2/16f, -1/16f); uv[2] = new UV(9/16f, 10/16f);
		vertices[3] = new Vertex(-1/16f, 2/16f, -1/16f); uv[3] = new UV(7/16f, 10/16f);
		obj.addFace(vertices, uv, txTorch, mtls[0]);
		
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
		
		// bottom
		if (data.containsKey("facing") || drawSides(chunks,x,y,z)[5])
		{
			vertices[0] = new Vertex( 1/16f, -0.5f,  1/16f); uv[0] = new UV(7/16f, 0);
			vertices[1] = new Vertex(-1/16f, -0.5f,  1/16f); uv[1] = new UV(9/16f, 0);
			vertices[2] = new Vertex(-1/16f, -0.5f, -1/16f); uv[2] = new UV(9/16f, 2/16f);
			vertices[3] = new Vertex( 1/16f, -0.5f, -1/16f); uv[3] = new UV(7/16f, 2/16f);
			obj.addFace(vertices, uv, txTorch, mtls[0]);
		}
		
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
