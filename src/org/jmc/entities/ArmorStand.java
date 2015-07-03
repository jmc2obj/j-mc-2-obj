package org.jmc.entities;

import java.io.File;
import java.io.IOException;

import org.jmc.OBJInputFile;
import org.jmc.OBJInputFile.OBJGroup;
import org.jmc.OBJOutputFile;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Double;
import org.jmc.NBT.TAG_Float;
import org.jmc.NBT.TAG_List;
import org.jmc.NBT.TAG_String;
import org.jmc.geom.Transform;
import org.jmc.util.Log;

public class ArmorStand extends Entity {
	
	private TAG_List pos;
	private TAG_List rot;
	

	private Transform getTranslate(float x, float y, float z) {
		float ex=(float)((TAG_Double)pos.getElement(0)).value-0.5f + x;				
		float ey=(float)((TAG_Double)pos.getElement(1)).value-0.5f + y;
		float ez=(float)((TAG_Double)pos.getElement(2)).value-0.5f + z;
		Transform translate = new Transform();
		translate.translate(ex, ey, ez);
		return translate;
	}
	
	private Transform getTranslate() {
		float x = 0;
		float y = 0;
		float z = 0;
		return getTranslate(x, y, z);
	}
	
	
	private Transform getRotate() {
		float yaw=((TAG_Float)rot.getElement(0)).value;				
		float pitch=((TAG_Float)rot.getElement(1)).value;
		Transform rotate = new Transform();
		rotate.rotate2(yaw+90, pitch ,0);	
		return rotate;
	}

	
	@Override
	public void addEntity(OBJOutputFile obj, TAG_Compound entity) {
		
		pos = (TAG_List) entity.getElement("Pos");
		rot = (TAG_List) entity.getElement("Rotation");
		
		model.addEntity(obj, getTranslate().multiply(getRotate()));	
		
		// equipment
		// Log.info("ArmorStand found: "+entity.getElement("Equipment"));
		TAG_List Equipment = (TAG_List) entity.getElement("Equipment");
		
		for(int i=1; i<5; i++) {
			TAG_Compound armor = ((TAG_Compound)Equipment.getElement(i));
			String item_id = "";
			try {
				item_id = ((TAG_String)armor.getElement("id")).value;
			}
			catch (Exception e) {
				Log.info("Slot "+i+" not armed");
				continue;
			}
			
			//   <file name="assets/minecraft/textures/entity/armorstand/wood.png" source="texturepack"><tex name="armor_stand"/></file>
			 // <file name="assets/minecraft/textures/models/armor/gold_layer_1.png" source="texturepack"><tex name="armor_gold"/></file>

			String mcMaterial = item_id.substring(item_id.indexOf(":")+1, item_id.indexOf("_"));
			Log.info("- Slot "+i+" armed ("+mcMaterial+")");
			
			
			
			switch(i) {
				// 0: The item being held in the entity's hand.
				case 0:
					break;
				// 1: Armor (Feet)
				case 1:
					addArmor("conf/models/armor_feet.obj", "armor_" + mcMaterial + "_feet", obj, 0.04, 0.23, 0);
					break;
				// 2: Armor (Legs)
				case 2:
					addArmor("conf/models/armor_legs.obj", "armor_" + mcMaterial + "_legs", obj, 0, 0.7, 0);
					break;
				// 3: Armor (Chest)
				case 3:
					addArmor("conf/models/armor_chest.obj", "armor_" + mcMaterial + "_chest", obj, 0, 1.2, 0);
					break;
				// 4: Armor (Head)
				case 4:
					addArmor("conf/models/armor_helmet.obj", "armor_" + mcMaterial + "_helmet", obj, 0, 1.85, 0);
					break;
			}
			
			if (mcMaterial.equals("leather")) {
				Log.info("- - leather detected");
				switch(i) {
					// 0: The item being held in the entity's hand.
					case 0:
						break;
					// 1: Armor (Feet)
					case 1:
						addArmor("conf/models/armor_feet.obj", "armor_" + mcMaterial + "_feet_overlay", obj, 0.04, 0.23, 0);
						break;
					// 2: Armor (Legs)
					case 2:
						addArmor("conf/models/armor_legs.obj", "armor_" + mcMaterial + "_legs_overlay", obj, 0, 0.7, 0);
						break;
					// 3: Armor (Chest)
					case 3:
						addArmor("conf/models/armor_chest.obj", "armor_" + mcMaterial + "_chest_overlay", obj, 0, 1.2, 0);
						break;
					// 4: Armor (Head)
					case 4:
						addArmor("conf/models/armor_helmet.obj", "armor_" + mcMaterial + "_helmet_overlay", obj, 0, 1.85, 0);
						break;
				}
				
			}
			
			
		}
		
		
		
	}

	public void addArmor(String objFileName, String material, OBJOutputFile obj, double x, double y, double z) {
		
		OBJInputFile objFile = new OBJInputFile();
		File objMeshFile = new File(objFileName);
		
		
		
		try {
			objFile.loadFile(objMeshFile, material);
		} catch (IOException e) {
			Log.error("Cant read Armor_Stand Equipment", e, true);
		}
		
				
		OBJGroup myObjGroup = objFile.getDefaultObject();
		objFile.overwriteMaterial(myObjGroup, material);
		Log.info("myObjGroup: "+myObjGroup);
		objFile.addObjectToOutput(myObjGroup, getTranslate((float)x, (float)y, (float)z), obj);
	}
	
}
