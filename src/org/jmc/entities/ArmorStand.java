package org.jmc.entities;

import java.io.IOException;

import org.jmc.OBJInputFile;
import org.jmc.OBJInputFile.OBJGroup;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Double;
import org.jmc.NBT.TAG_Float;
import org.jmc.NBT.TAG_List;
import org.jmc.NBT.TAG_String;
import org.jmc.geom.Transform;
import org.jmc.threading.ChunkProcessor;
import org.jmc.util.Filesystem.JmcConfFile;
import org.jmc.util.Log;

public class ArmorStand extends Entity {
	
	private TAG_List pos;
	private TAG_List rot;
	

	private Transform getTranslate(float x, float y, float z, float scale) {

		float ex=(float)((TAG_Double)pos.getElement(0)).value-0.5f + x;
		float ey=(float)((TAG_Double)pos.getElement(1)).value-0.5f + y;
		float ez=(float)((TAG_Double)pos.getElement(2)).value-0.5f + z;
		Transform translate = Transform.translation(ex, ey, ez);
		
		Transform tScale = Transform.scale(scale, scale, scale);
		
		translate = translate.multiply(tScale);
		
		return translate;
	}
	
	private Transform getTranslate() {
		float x = 0;
		float y = 0;
		float z = 0;
		return getTranslate(x, y, z, 1);
	}
	
	
	private Transform getRotate(float offset) {
		float yaw=((TAG_Float)rot.getElement(0)).value;
		float pitch=((TAG_Float)rot.getElement(1)).value;
		Transform rotate = Transform.rotation2(yaw+offset, pitch ,0);
		return rotate;
	}

	
	@Override
	public void addEntity(ChunkProcessor obj, TAG_Compound entity) {
		
		pos = (TAG_List) entity.getElement("Pos");
		rot = (TAG_List) entity.getElement("Rotation");
		
		model.addEntity(obj, getTranslate().multiply(getRotate((float) 90)));
		
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

			// internal: offset x, y, z, rotate, initialscale
			// blender:  offset ?, z, -y, rotate, initialscale
			double posCorrection[][] = {
					{0, 0, 0, 180, 1}, // item
					{0, 0.194, -0.03, 0, 1}, // feet
					{0, 0.778, 0.01, 0, 1}, // legs
					{0, 1.14, 0.002, 180, 1}, // chest
					{0, 1.71, 0, 180, 1} // helmet
			};
			
			// base material
			if (baseItemName[i] != null) {
				addArmor("conf/models/armor_"+baseItemName[i]+".obj", 
						"armor_" + mcMaterial + "_"+baseItemName[i], 
						obj, 
						posCorrection[i][0], 
						posCorrection[i][1],
						posCorrection[i][2],
						1 * posCorrection[i][4],
						posCorrection[i][3]);
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
							1.04 * posCorrection[i][4],
							posCorrection[i][3]);
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
							1.08 * posCorrection[i][4],
							posCorrection[i][3]);
				}
			}
			 
		}
		
	}

	public void addArmor(String objFileName, String material, ChunkProcessor obj, double x, double y, double z, double scale, double rotation) {
		
		OBJInputFile objFile = new OBJInputFile();
		
		
		try (JmcConfFile objFileStream = new JmcConfFile(objFileName)) {
			objFile.loadFile(objFileStream, material);
		} catch (IOException e) {
			Log.error("Cant read Armor_Stand Equipment", e, true);
		}
		
		
		OBJGroup myObjGroup = objFile.getDefaultObject();
		myObjGroup = objFile.overwriteMaterial(myObjGroup, material);
		// Log.info("myObjGroup: "+myObjGroup);
		Transform translate = getTranslate((float)x, (float)y, (float)z, (float)scale).multiply(getRotate((float) rotation));
		
		objFile.addObjectToOutput(myObjGroup, translate, obj);
	}
	
}
