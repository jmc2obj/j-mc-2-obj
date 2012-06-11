package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;


/**
 * Model for melon and pumpkin stalks.
 */
public class Stalk extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome)
	{
		boolean n,s,e,w;
		
		if (blockId == 104)
		{
			// we're a pumpkin stalk, look for pumpkins around
			n = chunks.getBlockID(x, y, z-1) == 86;
			s = chunks.getBlockID(x, y, z+1) == 86;
			e = chunks.getBlockID(x+1, y, z) == 86;
			w = chunks.getBlockID(x-1, y, z) == 86;
		}
		else
		{
			// we're a melon stalk, look for melons around 
			n = chunks.getBlockID(x, y, z-1) == 103;
			s = chunks.getBlockID(x, y, z+1) == 103;
			e = chunks.getBlockID(x+1, y, z) == 103;
			w = chunks.getBlockID(x-1, y, z) == 103;
		}
		
		if (data == 7 && (n||s||e||w))
		{
			// bent stalk
			Transform translate = new Transform();
			translate.translate(x, y, z);
			
			Transform rotate = new Transform();
			if (n)		rotate.rotate(0, -90, 0);
			else if (s)	rotate.rotate(0, 90, 0);
			else if (e)	rotate.rotate(0, 0, 0);
			else if (w)	rotate.rotate(0, 180, 0);
			
			Vertex[] vertices = new Vertex[4];
			vertices[0] = new Vertex( 0.5f, -0.5f, 0.0f);				
			vertices[1] = new Vertex(-0.5f, -0.5f, 0.0f);
			vertices[2] = new Vertex(-0.5f,  0.5f, 0.0f); 				
			vertices[3] = new Vertex( 0.5f,  0.5f, 0.0f);	
			obj.addFace(vertices, null, translate.multiply(rotate), materials.get(data,biome)[1]);
		}
		else
		{
			// straight stalk
			Transform translate = new Transform();
			translate.translate(x, y, z);
			
			Vertex[] vertices = new Vertex[4];
			vertices[0] = new Vertex( 0.5f, -0.5f, -0.5f);				
			vertices[1] = new Vertex(-0.5f, -0.5f,  0.5f);
			vertices[2] = new Vertex(-0.5f,  0.5f,  0.5f); 				
			vertices[3] = new Vertex( 0.5f,  0.5f, -0.5f);	
			obj.addFace(vertices, null, translate, materials.get(data,biome)[0]);
			
			vertices[0] = new Vertex(-0.5f, -0.5f, -0.5f);		
			vertices[1] = new Vertex( 0.5f, -0.5f,  0.5f);
			vertices[2] = new Vertex( 0.5f,  0.5f,  0.5f);	
			vertices[3] = new Vertex(-0.5f,  0.5f, -0.5f);
			obj.addFace(vertices, null, translate, materials.get(data,biome)[0]);						
		}
	}

}
