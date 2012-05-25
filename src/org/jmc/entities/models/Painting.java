package org.jmc.entities.models;

import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;


/**
 * Model for ladders.
 */
public class Painting extends EntityModel
{
	@Override
	public void addEntity(OBJOutputFile obj, Transform transform) 
	{
		addObject(obj, transform, materials.get((byte) 0)[0]);		
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
