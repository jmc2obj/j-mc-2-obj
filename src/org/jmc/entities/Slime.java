package org.jmc.entities;

import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Double;
import org.jmc.NBT.TAG_Float;
import org.jmc.NBT.TAG_Int;
import org.jmc.NBT.TAG_List;
import org.jmc.geom.Transform;
import org.jmc.threading.ChunkProcessor;

public class Slime extends Entity {

	@Override
	public void addEntity(ChunkProcessor obj, TAG_Compound entity) {
		
		TAG_List pos = (TAG_List) entity.getElement("Pos");
		float ex=(float)((TAG_Double)pos.getElement(0)).value-0.5f;
		float ey=(float)((TAG_Double)pos.getElement(1)).value-0.5f;
		float ez=(float)((TAG_Double)pos.getElement(2)).value-0.5f;

		Transform translate = Transform.translation(ex, ey, ez);
		
		TAG_List rot = (TAG_List) entity.getElement("Rotation");
		float yaw=((TAG_Float)rot.getElement(0)).value;
		float pitch=((TAG_Float)rot.getElement(1)).value;
		
		Transform rotate = Transform.rotation2(yaw+90, pitch ,0);
		
		int size = ((TAG_Int) entity.getElement("Size")).value;
		
		Transform scale = Transform.scale(size, size, size);
		
		model.addEntity(obj, translate.multiply(rotate.multiply(scale)));
		
	}
	
	

}
