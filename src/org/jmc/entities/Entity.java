package org.jmc.entities;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Double;
import org.jmc.NBT.TAG_List;
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

    public Vertex getPosition(TAG_Compound entity) {
        TAG_List pos = (TAG_List) entity.getElement("Pos");
        double ex=((TAG_Double)pos.getElement(0)).value-0.5d;
        double ey=((TAG_Double)pos.getElement(1)).value-0.5d;
        double ez=((TAG_Double)pos.getElement(2)).value-0.5d;
        return new Vertex(ex, ey, ez);
    }
}
