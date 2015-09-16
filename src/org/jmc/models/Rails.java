package org.jmc.models;

import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for minecart rails.
 */
public class Rails extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, byte data, byte biome)
	{
		String mtl;
		if (blockId == 27 || blockId == 157) {
			// powered rail (off / on)
			mtl = (data & 8) == 0 ? materials.get(data,biome)[1] : materials.get(data,biome)[0];
			data = (byte)(data & 7);
		}
		else if (blockId == 28)
		{
			// detector
			mtl = materials.get(data,biome)[0];
		}
		else
		{
			// regular rail (straight / curved)
			mtl = data < 6 ? materials.get(data,biome)[0] : materials.get(data,biome)[1];
		}
		
		
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		switch (data)
		{
			case 1:
			case 2:
			case 7:
				rotate.rotate(0, 90, 0);
				break;
			case 3:
			case 9:
				rotate.rotate(0, -90, 0);
				break;
			case 5:
			case 8:
				rotate.rotate(0, 180, 0);
				break;
		}
		translate.translate(x, y, z);		
			
		rt = translate.multiply(rotate);
		
		
		Vertex[] vertices = new Vertex[4];
		if (data < 2 || data > 5)
		{
			// flat
			vertices[0] = new Vertex(-0.5f, -0.47f,  0.5f);
			vertices[1] = new Vertex( 0.5f, -0.47f,  0.5f);
			vertices[2] = new Vertex( 0.5f, -0.47f, -0.5f);			
			vertices[3] = new Vertex(-0.5f, -0.47f, -0.5f);
			obj.addFace(vertices, null, rt, mtl);
		}
		else
		{
			// ascending
			vertices[0] = new Vertex(-0.5f, -0.47f,  0.5f); 				
			vertices[1] = new Vertex( 0.5f, -0.47f,  0.5f);	
			vertices[2] = new Vertex( 0.5f,  0.53f, -0.5f);				
			vertices[3] = new Vertex(-0.5f,  0.53f, -0.5f);
			obj.addFace(vertices, null, rt, mtl);
		}
	}

}
