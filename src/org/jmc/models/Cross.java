package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJFile;
import org.jmc.OBJFile.Side;
import org.jmc.Transform;
import org.jmc.Vertex;


/**
 * Generic model for blocks rendered as 2 crossed polygons, like saplings.
 */
public class Cross extends BlockModel
{

	@Override
	public void addModel(OBJFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		Transform move = new Transform();
		move.translate(x, y, z);		
		
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(-0.5f,-0.5f,+0.5f);
		vertices[1] = new Vertex(-0.5f,+0.5f,+0.5f); 				
		vertices[2] = new Vertex(+0.5f,+0.5f,-0.5f);	
		vertices[3] = new Vertex(+0.5f,-0.5f,-0.5f);				
		obj.addFace(vertices, move, Side.FRONTRIGHT, materials.get(data)[0]);
		
		vertices[0] = new Vertex(+0.5f,-0.5f,+0.5f);
		vertices[1] = new Vertex(+0.5f,+0.5f,+0.5f);	
		vertices[2] = new Vertex(-0.5f,+0.5f,-0.5f);
		vertices[3] = new Vertex(-0.5f,-0.5f,-0.5f);		
		obj.addFace(vertices, move, Side.BACKRIGHT, materials.get(data)[0]);						
	}

}
