package org.jmc.entities.models;

import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;


/**
 * Model for ladders.
 */
public class Painting extends EntityModel
{
	@Override
	public void addEntity(ChunkProcessor obj, Transform transform) 
	{
		addObject(obj, transform, materials.get(null, -1)[0]);		
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
