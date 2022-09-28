package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for ladders.
 */
public class Ladder extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		switch (data.state.get("facing"))
		{
			case "north":
				rotate = Transform.rotation(0, 180, 0);
				break;
			case "east":
				rotate = Transform.rotation(0, -90, 0);
				break;
			case "south":
				//rotate.rotate(0, 0, 0);
				break;
			case "west":
				rotate = Transform.rotation(0, 90, 0);
				break;
			default:
				
				break;
		}
		translate = Transform.translation(x, y, z);		
			
		rt = translate.multiply(rotate);
		
		addObject(obj, rt, materials.get(data.state,biome)[0]);
	}
	
	private void addObject(ChunkProcessor obj, Transform transform, NamespaceID mat)
	{
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(-0.5f, -0.5f, -0.47f);
		vertices[1] = new Vertex( 0.5f, -0.5f, -0.47f);
		vertices[2] = new Vertex( 0.5f,  0.5f, -0.47f);
		vertices[3] = new Vertex(-0.5f,  0.5f, -0.47f);
		obj.addFace(vertices, null, transform, mat);
	}

}
