package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for ladders.
 */
public class Ladder extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		switch (data.state.get("facing"))
		{
			case "north":
				rotate.rotate(0, 180, 0);
				break;
			case "east":
				rotate.rotate(0, -90, 0);
				break;
			case "south":
				//rotate.rotate(0, 0, 0);
				break;
			case "west":
				rotate.rotate(0, 90, 0);
				break;
			default:
				
				break;
		}
		translate.translate(x, y, z);		
			
		rt = translate.multiply(rotate);
		
		addObject(obj, rt, materials.get(data,biome)[0]);
	}
	
	private void addObject(ChunkProcessor obj, Transform transform, String mat)
	{
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(-0.5f, -0.5f, -0.47f);
		vertices[1] = new Vertex( 0.5f, -0.5f, -0.47f);
		vertices[2] = new Vertex( 0.5f,  0.5f, -0.47f);
		vertices[3] = new Vertex(-0.5f,  0.5f, -0.47f);
		obj.addFace(vertices, null, transform, mat);
	}

}
