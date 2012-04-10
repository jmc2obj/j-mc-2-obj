package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJFile;
import org.jmc.OBJFile.Side;
import org.jmc.Transform;
import org.jmc.Vertex;


/**
 * Model for torches.
 * 
 * TODO fix model
 */
public class Torch extends BlockModel
{

	@Override
	public void addModel(OBJFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		switch(data)
		{
		case 1:
			rotate.rotate(0, 0, -30);
			translate.translate(x-0.3f, y, z);
			rt = translate.multiply(rotate);
			break;
		case 2:
			rotate.rotate(0, 0, 30);
			translate.translate(x+0.3f, y, z);
			rt = translate.multiply(rotate);
			break;
		case 3:
			rotate.rotate(30, 0, 0);			
			translate.translate(x, y, z-0.3f);
			rt = translate.multiply(rotate);
			break;
		case 4:
			rotate.rotate(-30, 0, 0);
			translate.translate(x, y, z+0.3f);
			rt = translate.multiply(rotate);
			break;
		default:
			translate.translate(x, y, z);
			rt = translate;
			break;
		}
		
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(-0.5f,-0.5f,+0.5f);
		vertices[1] = new Vertex(-0.5f,+0.5f,+0.5f); 				
		vertices[2] = new Vertex(+0.5f,+0.5f,-0.5f);	
		vertices[3] = new Vertex(+0.5f,-0.5f,-0.5f);				
		obj.addFace(vertices, rt, Side.FRONTRIGHT, materials.get(data)[0]);
		
		vertices[0] = new Vertex(+0.5f,-0.5f,+0.5f);
		vertices[1] = new Vertex(+0.5f,+0.5f,+0.5f);	
		vertices[2] = new Vertex(-0.5f,+0.5f,-0.5f);
		vertices[3] = new Vertex(-0.5f,-0.5f,-0.5f);		
		obj.addFace(vertices, rt, Side.BACKRIGHT, materials.get(data)[0]);						
	}

}
