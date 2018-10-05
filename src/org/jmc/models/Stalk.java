package org.jmc.models;

import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for melon and pumpkin stalks.
 */
public class Stalk extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, byte data, int biome)
	{
		boolean n,s,e,w;
		n=s=e=w=false;
		
		if (blockId.equals( "minecraft:attached_pumpkin_stem"))
		{
			// we're a pumpkin stalk, look for pumpkins around
			n = chunks.getBlockID(x, y, z-1).equals("minecraft:pumpkin");
			s = chunks.getBlockID(x, y, z+1).equals("minecraft:pumpkin");
			e = chunks.getBlockID(x+1, y, z).equals("minecraft:pumpkin");
			w = chunks.getBlockID(x-1, y, z).equals("minecraft:pumpkin");
		}
		else if (blockId.equals("minecraft:attached_melon_stem"))
		{
			// we're a melon stalk, look for melons around 
			n = chunks.getBlockID(x, y, z-1).equals("minecraft:melon");
			s = chunks.getBlockID(x, y, z+1).equals("minecraft:melon");
			e = chunks.getBlockID(x+1, y, z).equals("minecraft:melon");
			w = chunks.getBlockID(x-1, y, z).equals("minecraft:melon");
		}
		
		if (blockId.equals("minecraft:attached_pumpkin_stem") || blockId.equals("minecraft:attached_melon_stem") && (n||s||e||w))
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
