package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;


/**
 * Model for ladders.
 */
public class Ladder extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		switch (data)
		{
			case 2:
				rotate.rotate(0, 180, 0);
				break;
			case 4:
				rotate.rotate(0, 90, 0);
				break;
			case 5:
				rotate.rotate(0, -90, 0);
				break;
		}
		translate.translate(x, y, z);		
			
		rt = translate.multiply(rotate);
		
		addObject(obj, rt, materials.get(data)[0]);
	}
	
	private void addObject(OBJOutputFile obj, Transform transform, String mat)
	{
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(-0.5f, -0.5f, -0.47f);
		vertices[1] = new Vertex( 0.5f, -0.5f, -0.47f);
		vertices[2] = new Vertex( 0.5f,  0.5f, -0.47f);
		vertices[3] = new Vertex(-0.5f,  0.5f, -0.47f);
		obj.addFace(vertices, null, transform, mat);
	}

}
