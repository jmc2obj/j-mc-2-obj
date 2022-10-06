package org.jmc.entities;

import org.jmc.NBT.*;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ObjChunkProcessor;

public class Bell extends Entity {


	public Bell(String id) {
		super(id);
	}

	@Override
	public void addEntity(ObjChunkProcessor obj, TAG_Compound entity) {
		Vertex pos = getPosition(entity);
		Transform translate = Transform.translation(pos.x, pos.y, pos.z);
		
		model.addEntity(obj, translate);
	}

	@Override
	public Vertex getPosition(TAG_Compound entity) {
		TAG_Int xTag = (TAG_Int) entity.getElement("x");
		TAG_Int yTag = (TAG_Int) entity.getElement("y");
		TAG_Int zTag = (TAG_Int) entity.getElement("z");
		return new Vertex(xTag.value, yTag.value, zTag.value);
	}

}
