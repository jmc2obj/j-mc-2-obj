package org.jmc.models;

import java.util.HashMap;

import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for beds.
 */
public class Bed extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		String dir = data.get("facing");
		boolean head = data.get("part").equals("head");


		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		switch (dir)
		{
			case "south": rotate.rotate(0, 180, 0); break;
			case "west": rotate.rotate(0, -90, 0); break;
			case "east": rotate.rotate(0, 90, 0); break;
		}
		translate.translate(x, y, z);		
		rt = translate.multiply(rotate);

		
		Vertex[] vertices = new Vertex[4];
		UV[] uv = new UV[4];
		
		/*    
		 *          Top
		 *           -z
		 *      -x ________  +x
		 *        | Bed   |
		 *Left    | Head  |     Right
		 *        |       |
		 *           +z
		 */
		
		
		if (head)
		{
			// top
			vertices[0] = new Vertex(-0.5f, 0.0625f,  0.5f); uv[0] = new UV(22/64f, 42/64f);
			vertices[1] = new Vertex( 0.5f, 0.0625f,  0.5f); uv[1] = new UV(6/64f, 42/64f);
			vertices[2] = new Vertex( 0.5f, 0.0625f, -0.5f); uv[2] = new UV(6/64f, 58/64f);
			vertices[3] = new Vertex(-0.5f, 0.0625f, -0.5f); uv[3] = new UV(22/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			// bottom
			vertices[0] = new Vertex(-0.5f, -0.3125f,  0.5f); uv[0] = new UV(44/64f, 42/64f);
			vertices[1] = new Vertex( 0.5f, -0.3125f,  0.5f); uv[1] = new UV(28/64f, 42/64f);
			vertices[2] = new Vertex( 0.5f, -0.3125f, -0.5f); uv[2] = new UV(28/64f, 58/64f);
			vertices[3] = new Vertex(-0.5f, -0.3125f, -0.5f); uv[3] = new UV(44/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);			
			// front
			vertices[0] = new Vertex( 0.5f, -0.3125f, -0.5f); uv[2] = new UV(22/64f, 58/64f);
			vertices[1] = new Vertex(-0.5f, -0.3125f, -0.5f); uv[3] = new UV(6/64f, 58/64f);
			vertices[2] = new Vertex(-0.5f, 0.0625f, -0.5f); uv[0] = new UV(6/64f, 64/64f);
			vertices[3] = new Vertex( 0.5f, 0.0625f, -0.5f); uv[1] = new UV(22/64f, 64/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			// left (when you're laying on the bed looking up, it's the right)
			vertices[0] = new Vertex(-0.5f, -0.3125f, -0.5f); uv[0] = new UV(0, 58/64f);
			vertices[1] = new Vertex(-0.5f, -0.3125f,  0.5f); uv[1] = new UV(0, 42/64f);
			vertices[2] = new Vertex(-0.5f, 0.0625f,  0.5f); uv[2] = new UV(6/64f, 42/64f);
			vertices[3] = new Vertex(-0.5f, 0.0625f, -0.5f); uv[3] = new UV(6/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			// right (when you're laying on the bed looking up, it's the left)
			vertices[0] = new Vertex(0.5f, -0.3125f,  0.5f); uv[0] = new UV(28/64f, 42/64f);
			vertices[1] = new Vertex(0.5f, -0.3125f, -0.5f); uv[1] = new UV(28/64f, 58/64f);
			vertices[2] = new Vertex(0.5f, 0.0625f, -0.5f); uv[2] = new UV(22/64f, 58/64f);
			vertices[3] = new Vertex(0.5f, 0.0625f,  0.5f); uv[3] = new UV(22/64f, 42/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			
			//leg post left, left
			vertices[0] = new Vertex(-0.5f, -0.3125f,  -0.5f); uv[0] = new UV(53/64f, 61/64f);
			vertices[1] = new Vertex(-0.5f, -0.3125f, -0.3125f); uv[1] = new UV(56/64f, 61/64f);
			vertices[2] = new Vertex(-0.5f, -0.5f, -0.3125f); uv[2] = new UV(56/64f, 58/64f);
			vertices[3] = new Vertex(-0.5f, -0.5f,  -0.5f); uv[3] = new UV(53/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			//leg post left, left (opposite)
			vertices[0] = new Vertex(-0.3125f, -0.3125f,  -0.5f); uv[0] = new UV(56/64f, 61/64f);
			vertices[1] = new Vertex(-0.3125f, -0.3125f, -0.3125f); uv[1] = new UV(59/64f, 61/64f);
			vertices[2] = new Vertex(-0.3125f, -0.5f, -0.3125f); uv[2] = new UV(59/64f, 58/64f);
			vertices[3] = new Vertex(-0.3125f, -0.5f,  -0.5f); uv[3] = new UV(56/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			//leg post left, top
			vertices[0] = new Vertex(-0.5f, -0.3125f,  -0.5f); uv[0] = new UV(53/64f, 61/64f);
			vertices[1] = new Vertex(-0.3125f, -0.3125f, -0.5f); uv[1] = new UV(56/64f, 61/64f);
			vertices[2] = new Vertex(-0.3125f, -0.5f, -0.5f); uv[2] = new UV(56/64f, 58/64f);
			vertices[3] = new Vertex(-0.5f, -0.5f,  -0.5f); uv[3] = new UV(53/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			//leg post left, top (opposite)
			vertices[0] = new Vertex(-0.5f, -0.3125f,  -0.3125f); uv[0] = new UV(56/64f, 61/64f);
			vertices[1] = new Vertex(-0.3125f, -0.3125f, -0.3125f); uv[1] = new UV(59/64f, 61/64f);
			vertices[2] = new Vertex(-0.3125f, -0.5f, -0.3125f); uv[2] = new UV(59/64f, 58/64f);
			vertices[3] = new Vertex(-0.5f, -0.5f,  -0.3125f); uv[3] = new UV(56/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);			
			//leg post left, bottom
			vertices[0] = new Vertex(-0.5f, -0.5f,  -0.3125f); uv[2] = new UV(56/64f, 61/64f);
			vertices[1] = new Vertex(-0.5f, -0.5f, -0.5f); uv[3] = new UV(59/64f, 61/64f);
			vertices[2] = new Vertex(-0.3125f, -0.5f, -0.5f); uv[0] = new UV(59/64f, 64/64f);
			vertices[3] = new Vertex(-0.3125f, -0.5f,  -0.3125f); uv[1] = new UV(56/64f, 64/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			
			//leg post right, right
			vertices[0] = new Vertex(0.5f, -0.3125f,  -0.5f); uv[0] = new UV(53/64f, 61/64f);
			vertices[1] = new Vertex(0.5f, -0.3125f, -0.3125f); uv[1] = new UV(56/64f, 61/64f);
			vertices[2] = new Vertex(0.5f, -0.5f, -0.3125f); uv[2] = new UV(56/64f, 58/64f);
			vertices[3] = new Vertex(0.5f, -0.5f,  -0.5f); uv[3] = new UV(53/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			//leg post right, right (opposite)
			vertices[0] = new Vertex(0.3125f, -0.3125f,  -0.5f); uv[0] = new UV(56/64f, 61/64f);
			vertices[1] = new Vertex(0.3125f, -0.3125f, -0.3125f); uv[1] = new UV(59/64f, 61/64f);
			vertices[2] = new Vertex(0.3125f, -0.5f, -0.3125f); uv[2] = new UV(59/64f, 58/64f);
			vertices[3] = new Vertex(0.3125f, -0.5f,  -0.5f); uv[3] = new UV(56/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);			
			//leg post right, top
			vertices[0] = new Vertex(0.5f, -0.3125f,  -0.5f); uv[0] = new UV(53/64f, 61/64f);
			vertices[1] = new Vertex(0.3125f, -0.3125f, -0.5f); uv[1] = new UV(56/64f, 61/64f);
			vertices[2] = new Vertex(0.3125f, -0.5f, -0.5f); uv[2] = new UV(56/64f, 58/64f);
			vertices[3] = new Vertex(0.5f, -0.5f,  -0.5f); uv[3] = new UV(53/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);	
			//leg post right, top (opposite)
			vertices[0] = new Vertex(0.5f, -0.3125f,  -0.3125f); uv[0] = new UV(56/64f, 61/64f);
			vertices[1] = new Vertex(0.3125f, -0.3125f, -0.3125f); uv[1] = new UV(59/64f, 61/64f);
			vertices[2] = new Vertex(0.3125f, -0.5f, -0.3125f); uv[2] = new UV(59/64f, 58/64f);
			vertices[3] = new Vertex(0.5f, -0.5f,  -0.3125f); uv[3] = new UV(56/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);	
			//leg post right, bottom
			vertices[0] = new Vertex(0.5f, -0.5f,  -0.3125f); uv[2] = new UV(56/64f, 61/64f);
			vertices[1] = new Vertex(0.5f, -0.5f, -0.5f); uv[3] = new UV(59/64f, 61/64f);
			vertices[2] = new Vertex(0.3125f, -0.5f, -0.5f); uv[0] = new UV(59/64f, 64/64f);
			vertices[3] = new Vertex(0.3125f, -0.5f,  -0.3125f); uv[1] = new UV(56/64f, 64/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);		
		}
		else // Foot of the bed
		{
			// top
			vertices[0] = new Vertex(-0.5f, 0.0625f,  0.5f); uv[0] = new UV(22/64f, 20/64f);
			vertices[1] = new Vertex( 0.5f, 0.0625f,  0.5f); uv[1] = new UV(6/64f, 20/64f);
			vertices[2] = new Vertex( 0.5f, 0.0625f, -0.5f); uv[2] = new UV(6/64f, 36/64f);
			vertices[3] = new Vertex(-0.5f, 0.0625f, -0.5f); uv[3] = new UV(22/64f, 36/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			// bottom
			vertices[0] = new Vertex(-0.5f, -0.3125f,  0.5f); uv[0] = new UV(44/64f, 20/64f);
			vertices[1] = new Vertex( 0.5f, -0.3125f,  0.5f); uv[1] = new UV(28/64f, 20/64f);
			vertices[2] = new Vertex( 0.5f, -0.3125f, -0.5f); uv[2] = new UV(28/64f, 36/64f);
			vertices[3] = new Vertex(-0.5f, -0.3125f, -0.5f); uv[3] = new UV(44/64f, 36/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);			
			
			// left
			vertices[0] = new Vertex(-0.5f,   -0.3125f, -0.5f); uv[0] = new UV(0/64f, 36/64f);
			vertices[1] = new Vertex(-0.5f,   -0.3125f,  0.5f); uv[1] = new UV(0/64f, 20/64f);
			vertices[2] = new Vertex(-0.5f, 0.0625f,  0.5f); uv[2] = new UV(6/64f, 20/64f);
			vertices[3] = new Vertex(-0.5f, 0.0625f, -0.5f); uv[3] = new UV(6/64f, 36/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			// right
			vertices[0] = new Vertex(0.5f,   -0.3125f,  0.5f); uv[0] = new UV(28/64f, 20/64f);
			vertices[1] = new Vertex(0.5f,   -0.3125f, -0.5f); uv[1] = new UV(28/64f, 36/64f);
			vertices[2] = new Vertex(0.5f, 0.0625f, -0.5f); uv[2] = new UV(22/64f, 36/64f);
			vertices[3] = new Vertex(0.5f, 0.0625f,  0.5f); uv[3] = new UV(22/64f, 20/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			// back
			vertices[0] = new Vertex( 0.5f,   -0.3125f, 0.5f); uv[0] = new UV(38/64f, 42/64f);
			vertices[1] = new Vertex(-0.5f,   -0.3125f, 0.5f); uv[1] = new UV(22/64f, 42/64f);
			vertices[2] = new Vertex(-0.5f, 0.0625f, 0.5f); uv[2] = new UV(22/64f, 36/64f);
			vertices[3] = new Vertex( 0.5f, 0.0625f, 0.5f); uv[3] = new UV(38/64f, 36/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			
			//leg post left, left
			vertices[0] = new Vertex(-0.5f, -0.3125f,  0.5f); uv[0] = new UV(53/64f, 61/64f);
			vertices[1] = new Vertex(-0.5f, -0.3125f, 0.3125f); uv[1] = new UV(56/64f, 61/64f);
			vertices[2] = new Vertex(-0.5f, -0.5f, 0.3125f); uv[2] = new UV(56/64f, 58/64f);
			vertices[3] = new Vertex(-0.5f, -0.5f,  0.5f); uv[3] = new UV(53/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			//leg post left, left (opposite)
			vertices[0] = new Vertex(-0.3125f, -0.3125f,  0.5f); uv[0] = new UV(56/64f, 61/64f);
			vertices[1] = new Vertex(-0.3125f, -0.3125f, 0.3125f); uv[1] = new UV(59/64f, 61/64f);
			vertices[2] = new Vertex(-0.3125f, -0.5f, 0.3125f); uv[2] = new UV(59/64f, 58/64f);
			vertices[3] = new Vertex(-0.3125f, -0.5f,  0.5f); uv[3] = new UV(56/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			//leg post left, top
			vertices[0] = new Vertex(-0.5f, -0.3125f,  0.5f); uv[0] = new UV(53/64f, 61/64f);
			vertices[1] = new Vertex(-0.3125f, -0.3125f, 0.5f); uv[1] = new UV(56/64f, 61/64f);
			vertices[2] = new Vertex(-0.3125f, -0.5f, 0.5f); uv[2] = new UV(56/64f, 58/64f);
			vertices[3] = new Vertex(-0.5f, -0.5f,  0.5f); uv[3] = new UV(53/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			//leg post left, top (opposite)
			vertices[0] = new Vertex(-0.5f, -0.3125f,  0.3125f); uv[0] = new UV(56/64f, 61/64f);
			vertices[1] = new Vertex(-0.3125f, -0.3125f, 0.3125f); uv[1] = new UV(59/64f, 61/64f);
			vertices[2] = new Vertex(-0.3125f, -0.5f, 0.3125f); uv[2] = new UV(59/64f, 58/64f);
			vertices[3] = new Vertex(-0.5f, -0.5f,  0.3125f); uv[3] = new UV(56/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);			
			//leg post left, bottom
			vertices[0] = new Vertex(-0.5f, -0.5f,  0.3125f); uv[2] = new UV(56/64f, 61/64f);
			vertices[1] = new Vertex(-0.5f, -0.5f, 0.5f); uv[3] = new UV(59/64f, 61/64f);
			vertices[2] = new Vertex(-0.3125f, -0.5f, 0.5f); uv[0] = new UV(59/64f, 64/64f);
			vertices[3] = new Vertex(-0.3125f, -0.5f,  0.3125f); uv[1] = new UV(56/64f, 64/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			
			//leg post right, right
			vertices[0] = new Vertex(0.5f, -0.3125f,  0.5f); uv[0] = new UV(53/64f, 61/64f);
			vertices[1] = new Vertex(0.5f, -0.3125f, 0.3125f); uv[1] = new UV(56/64f, 61/64f);
			vertices[2] = new Vertex(0.5f, -0.5f, 0.3125f); uv[2] = new UV(56/64f, 58/64f);
			vertices[3] = new Vertex(0.5f, -0.5f,  0.5f); uv[3] = new UV(53/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			//leg post right, right (opposite)
			vertices[0] = new Vertex(0.3125f, -0.3125f,  0.5f); uv[0] = new UV(56/64f, 61/64f);
			vertices[1] = new Vertex(0.3125f, -0.3125f, 0.3125f); uv[1] = new UV(59/64f, 61/64f);
			vertices[2] = new Vertex(0.3125f, -0.5f, 0.3125f); uv[2] = new UV(59/64f, 58/64f);
			vertices[3] = new Vertex(0.3125f, -0.5f,  0.5f); uv[3] = new UV(56/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);			
			//leg post right, top
			vertices[0] = new Vertex(0.5f, -0.3125f,  0.5f); uv[0] = new UV(53/64f, 61/64f);
			vertices[1] = new Vertex(0.3125f, -0.3125f, 0.5f); uv[1] = new UV(56/64f, 61/64f);
			vertices[2] = new Vertex(0.3125f, -0.5f, 0.5f); uv[2] = new UV(56/64f, 58/64f);
			vertices[3] = new Vertex(0.5f, -0.5f,  0.5f); uv[3] = new UV(53/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);	
			//leg post right, top (opposite)
			vertices[0] = new Vertex(0.5f, -0.3125f,  0.3125f); uv[0] = new UV(56/64f, 61/64f);
			vertices[1] = new Vertex(0.3125f, -0.3125f, 0.3125f); uv[1] = new UV(59/64f, 61/64f);
			vertices[2] = new Vertex(0.3125f, -0.5f, 0.3125f); uv[2] = new UV(59/64f, 58/64f);
			vertices[3] = new Vertex(0.5f, -0.5f,  0.3125f); uv[3] = new UV(56/64f, 58/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);	
			//leg post right, bottom
			vertices[0] = new Vertex(0.5f, -0.5f,  0.3125f); uv[2] = new UV(56/64f, 61/64f);
			vertices[1] = new Vertex(0.5f, -0.5f, 0.5f); uv[3] = new UV(59/64f, 61/64f);
			vertices[2] = new Vertex(0.3125f, -0.5f, 0.5f); uv[0] = new UV(59/64f, 64/64f);
			vertices[3] = new Vertex(0.3125f, -0.5f,  0.3125f); uv[1] = new UV(56/64f, 64/64f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);					
		}
		
	}

}
