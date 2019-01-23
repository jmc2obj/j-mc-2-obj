package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for lilypads.
 */
public class Lilypad extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		// Minecraft decides which direction the lilypad faces based on some 
		// on-the-fly calculation (it's not stored in the data value)
		// I don't know what the game's algorithm is so I just made one up.
		// The directions won't match the game but that's probably ok.
		int dir = (x^z) % 4;

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		switch (dir)
		{
			case 0:
				rotate.rotate(0, -90, 0);
				break;
			case 1:
				rotate.rotate(0, 180, 0);
				break;
			case 2:
				rotate.rotate(0, 90, 0);
				break;
		}
		translate.translate(x, y, z);		
		rt = translate.multiply(rotate);
		
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(-0.5f, -0.49f,  0.5f);
		vertices[1] = new Vertex( 0.5f, -0.49f,  0.5f);
		vertices[2] = new Vertex( 0.5f, -0.49f, -0.5f);			
		vertices[3] = new Vertex(-0.5f, -0.49f, -0.5f);
		obj.addFace(vertices, null, rt, materials.get(data,biome)[0]);
	}

}
