package org.jmc.models;

import org.jmc.BlockData;
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
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		boolean n,s,e,w;
		n=s=e=w=false;
		
		if (data.state.containsKey("facing")) 
		{
			switch (data.state.get("facing")) {
			case "north":
				n = true;
				break;
			case "south":
				s = true;
				break;
			case "west":
				w = true;
				break;
			case "east":
				e = true;
				break;				
			default:
				n = true;
				break;
			}
		}

		
		if (blockId.equals("minecraft:attached_pumpkin_stem") || blockId.equals("minecraft:attached_melon_stem") && (n||s||e||w))
		{
			// bent stalk
			Transform translate = Transform.translation(x, y, z);
			
			Transform rotate = new Transform();
			if (n)		rotate = Transform.rotation(0, -90, 0);
			else if (s)	rotate = Transform.rotation(0, 90, 0);
			else if (e)	rotate = Transform.rotation(0, 0, 0);
			else if (w)	rotate = Transform.rotation(0, 180, 0);
			
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
			Transform translate = Transform.translation(x, y, z);
			
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
