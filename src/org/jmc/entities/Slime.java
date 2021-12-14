package org.jmc.entities;

import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Double;
import org.jmc.NBT.TAG_Float;
import org.jmc.NBT.TAG_Int;
import org.jmc.NBT.TAG_List;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;

public class Slime extends Entity {

	public Slime(String id) {
		super(id);
	}

	@Override
	public void addEntity(ChunkProcessor obj, TAG_Compound entity) {
		Vertex pos = getPosition(entity);
		Transform translate = Transform.translation(pos.x, pos.y, pos.z);
		
		TAG_List rot = (TAG_List) entity.getElement("Rotation");
		float yaw=((TAG_Float)rot.getElement(0)).value;
		float pitch=((TAG_Float)rot.getElement(1)).value;
		
		Transform rotate = Transform.rotation2(yaw+90, pitch ,0);
		
		double size = (((TAG_Int) entity.getElement("Size")).value + 1) * 0.51d;
		
		Transform offset = Transform.translation(0, 0.5, 0);
		Transform scale = Transform.scale(size, size, size);
		
		model.addEntity(obj, translate.multiply(scale.multiply(offset.multiply(rotate))));
		
	}
	
	@Override
	public Vertex getPosition(TAG_Compound entity) {
		TAG_List pos = (TAG_List) entity.getElement("Pos");
		double ex=((TAG_Double)pos.getElement(0)).value-0.5d;
		double ey=((TAG_Double)pos.getElement(1)).value-0.5d;
		double ez=((TAG_Double)pos.getElement(2)).value-0.5d;
		return new Vertex(ex, ey, ez);
	}
}
