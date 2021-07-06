package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Generic model for blocks rendered as 2 crossed polygons, like saplings.
 */
public class CoralFan extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		Transform move = Transform.translation(x, y, z);
		Transform rotate = new Transform();
		Transform rt;
		
		// If it's not a wall fan 
		if (!data.state.containsKey("facing")) 
		{
			Vertex[] vertices = new Vertex[4];
			vertices[0] = new Vertex(0.5f,-0.5f,0.0f);
			vertices[1] = new Vertex(-0.5f,-0.5f,0.0f);	
			vertices[2] = new Vertex(-0.5f,-0.05f,0.8f);
			vertices[3] = new Vertex(0.5f,-0.05f,0.8f);
			obj.addFace(vertices, null, move, materials.get(data,biome)[0]);
		
			vertices[0] = new Vertex(-0.5f,-0.5f,0.0f);
			vertices[1] = new Vertex(0.5f,-0.5f,0.0f);
			vertices[2] = new Vertex(0.5f,-0.05f,-0.8f);
			vertices[3] = new Vertex(-0.5f,-0.05f,-0.8f);
			obj.addFace(vertices, null, move, materials.get(data,biome)[0]);

			vertices[0] = new Vertex(0.0f,-0.5f,0.5f);
			vertices[1] = new Vertex(0.0f,-0.5f,-0.5f);	
			vertices[2] = new Vertex(-0.8f,-0.05f,-0.5f);		
			vertices[3] = new Vertex(-0.8f,-0.05f,0.5f);
			obj.addFace(vertices, null, move, materials.get(data,biome)[0]);	
			
			vertices[0] = new Vertex(0.0f,-0.5f,-0.5f);
			vertices[1] = new Vertex(0.0f,-0.5f,0.5f);	
			vertices[2] = new Vertex(0.8f,-0.05f,0.5f);		
			vertices[3] = new Vertex(0.8f,-0.05f,-0.5f);
			obj.addFace(vertices, null, move, materials.get(data,biome)[0]);		
		}
		else // It is a wall fan
		{
			switch (data.state.get("facing"))
			{
				case "north":
					rotate = Transform.rotation(0, 0, 0);
					break;
				case "east":
					rotate = Transform.rotation(0, 90, 0);
					break;
				case "south":
					rotate = Transform.rotation(0, 180, 0);
					break;
				case "west":
					rotate = Transform.rotation(0, -90, 0);
					break;
				default:
					
					break;
			}
			rt = move.multiply(rotate);
			
			Vertex[] vertices = new Vertex[4];
			vertices[0] = new Vertex(-0.5f, -0.04f, 0.5f);
			vertices[1] = new Vertex( 0.5f, -0.04f, 0.5f);
			vertices[2] = new Vertex( 0.5f,  0.28f, -0.5f);
			vertices[3] = new Vertex(-0.5f,  0.28f, -0.5f);
			obj.addFace(vertices, null, rt, materials.get(data,biome)[0]);			
			
			vertices[0] = new Vertex(-0.5f, 0.04f, 0.5f);
			vertices[1] = new Vertex( 0.5f, 0.04f, 0.5f);
			vertices[2] = new Vertex( 0.5f,  -0.28f, -0.5f);
			vertices[3] = new Vertex(-0.5f,  -0.28f, -0.5f);
			obj.addFace(vertices, null, rt, materials.get(data,biome)[0]);				
		}	
	}

}
