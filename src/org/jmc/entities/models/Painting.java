package org.jmc.entities.models;

import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;


/**
 * Model for ladders.
 */
public class Painting extends EntityModel
{
	@Override
	public void addEntity(ChunkProcessor obj, Transform transform) 
	{
		addObject(obj, transform, materials.get(null, NamespaceID.NULL)[0]);
	}
	
	private void addObject(ChunkProcessor obj, Transform transform, NamespaceID mat)
	{
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(-0.5d, -0.5d, -0.47d);
		vertices[1] = new Vertex( 0.5d, -0.5d, -0.47d);
		vertices[2] = new Vertex( 0.5d,  0.5d, -0.47d);
		vertices[3] = new Vertex(-0.5d,  0.5d, -0.47d);
		obj.addFace(vertices, null, transform, mat);
	}

}
