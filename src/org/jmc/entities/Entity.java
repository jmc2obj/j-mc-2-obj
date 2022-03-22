package org.jmc.entities;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jmc.NBT.TAG_Compound;
import org.jmc.entities.models.EntityModel;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;

@ParametersAreNonnullByDefault
public abstract class Entity {
	
	protected EntityModel model;
	
	final public String id;
	
	public Entity(String id) {
		this.id = id;
	}
	
	public void useModel(EntityModel model)
	{
		this.model=model;
	}

	public abstract void addEntity(ChunkProcessor obj, TAG_Compound entity);
	
	public abstract Vertex getPosition(TAG_Compound entity);
	
}
