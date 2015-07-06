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
	

	private Transform getTranslate(float x, float y, float z, float scale) {

		float ex=(float)((TAG_Double)pos.getElement(0)).value-0.5f + x;				
		float ey=(float)((TAG_Double)pos.getElement(1)).value-0.5f + y;
		float ez=(float)((TAG_Double)pos.getElement(2)).value-0.5f + z;
		Transform translate = new Transform();
		translate.translate(ex, ey, ez);
		
		Transform tScale = new Transform();
		tScale.scale(scale, scale, scale);
		
		translate = translate.multiply(tScale);
		
		return translate;
	}
	
	private Transform getTranslate() {
		float x = 0;
		float y = 0;
		float z = 0;
		return getTranslate(x, y, z, 1);
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
			TAG_Compound tag = ((TAG_Compound)armor.getElement("tag"));
			
			TAG_List enchanted = null;
			try {
				enchanted = ((TAG_List)tag.getElement("ench"));
			}
			catch (Exception e) {
			}
			

			
			//   <file name="assets/minecraft/textures/entity/armorstand/wood.png" source="texturepack"><tex name="armor_stand"/></file>
			 // <file name="assets/minecraft/textures/models/armor/gold_layer_1.png" source="texturepack"><tex name="armor_gold"/></file>

			String mcMaterial = item_id.substring(item_id.indexOf(":")+1, item_id.indexOf("_"));
			
			// Log.info("----------------------------------------------");
			// Log.info("- Slot "+i+" armed ("+mcMaterial+")");
			// Log.info(""+armor);
			
			String baseItemName[] = {
					null, 
					"feet", 
					"legs", 
					"chest", 
					"helmet"
			};

			double posCorrection[][] = {
					{0, 0, 0}, // item
					{0.04, 0.23, 0}, // feet
					{0, 0.57, 0}, // legs
					{0, 1.12, 0}, // chest
					{0, 1.78, 0} // helmet
			};
			
			// base material
			if (baseItemName[i] != null) {
				addArmor("conf/models/armor_"+baseItemName[i]+".obj", 
						"armor_" + mcMaterial + "_"+baseItemName[i], 
						obj, 
						posCorrection[i][0], 
						posCorrection[i][1],
						posCorrection[i][2],
						1);
			}
			
			// leather overlay
			if (mcMaterial.equals("leather")) {
				if (baseItemName[i] != null) {
					addArmor("conf/models/armor_"+baseItemName[i]+".obj", 
							"armor_" + mcMaterial + "_"+baseItemName[i]+"_overlay", 
							obj, 
							posCorrection[i][0], 
							posCorrection[i][1],
							posCorrection[i][2],
							1.01);
				}
			}
			
			// is enchanted
			if (enchanted != null) {
				if (baseItemName[i] != null) {
					addArmor("conf/models/armor_"+baseItemName[i]+".obj", 
							"armor_enchanted", 
							obj, 
							posCorrection[i][0], 
							posCorrection[i][1],
							posCorrection[i][2],
							1.02);
				}
			}
		
		}
		
	}

	public void addArmor(String objFileName, String material, OBJOutputFile obj, double x, double y, double z, double scale) {
		
		OBJInputFile objFile = new OBJInputFile();
		File objMeshFile = new File(objFileName);
		
		
		
		try {
			objFile.loadFile(objMeshFile, material);
		} catch (IOException e) {
			Log.error("Cant read Armor_Stand Equipment", e, true);
		}
		
				
		OBJGroup myObjGroup = objFile.getDefaultObject();
		objFile.overwriteMaterial(myObjGroup, material);
		// Log.info("myObjGroup: "+myObjGroup);
		Transform translate = getTranslate((float)x, (float)y, (float)z, (float)scale);
		objFile.addObjectToOutput(myObjGroup, translate, obj);
	}
	
}
