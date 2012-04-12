package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJFile;
import org.jmc.OBJFile.Side;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;


/**
 * Model for ladders.
 */
public class Ladder extends BlockModel
{

	@Override
	public void addModel(OBJFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
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
		
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex( 0.5f, -0.5f, -0.49f);
		vertices[1] = new Vertex( 0.5f,  0.5f, -0.49f);			
		vertices[2] = new Vertex(-0.5f,  0.5f, -0.49f);
		vertices[3] = new Vertex(-0.5f, -0.5f, -0.49f);
		obj.addFace(vertices, rt, Side.FRONT, materials.get(data)[0]);
	}

}
