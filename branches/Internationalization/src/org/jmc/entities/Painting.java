package org.jmc.entities;

import org.jmc.BlockMaterial;
import org.jmc.OBJOutputFile;
import org.jmc.NBT.TAG_Byte;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Int;
import org.jmc.NBT.TAG_String;
import org.jmc.geom.Transform;

public class Painting extends Entity {

	@Override
	public void addEntity(OBJOutputFile obj, TAG_Compound entity) {
		
		int x=((TAG_Int)entity.getElement("TileX")).value;
		int y=((TAG_Int)entity.getElement("TileY")).value;
		int z=((TAG_Int)entity.getElement("TileZ")).value;
		
		byte dir=((TAG_Byte)entity.getElement("Dir")).value;
		
		String motiv=((TAG_String)entity.getElement("Motive")).value;

		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		switch (dir)
		{
			case 0:
				z--;
				rotate.rotate(0, 180, 0);
				break;
			case 1:
				x--;
				rotate.rotate(0, 90, 0);				
				break;
			case 2:
				z++;
				break;				
			case 3:
				x++;
				rotate.rotate(0, -90, 0);				
				break;
		}
		
		translate.translate(x, y, z);		
			
		rt = translate.multiply(rotate);
		
		BlockMaterial materials=new BlockMaterial();
		String [] matname={motiv.toLowerCase()+"_painting"};
		materials.put((byte) 0, matname );
		model.setMaterials(materials);
		
		model.setMaterials(materials);
		
		model.addEntity(obj, rt);		
	}

}
