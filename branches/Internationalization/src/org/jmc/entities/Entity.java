package org.jmc.entities;

import org.jmc.OBJOutputFile;
import org.jmc.NBT.TAG_Compound;
import org.jmc.entities.models.EntityModel;

public abstract class Entity {
	
	protected EntityModel model;
	
	public void useModel(EntityModel model)
	{
		this.model=model;
	}

	public abstract void addEntity(OBJOutputFile obj, TAG_Compound entity);
	
}
