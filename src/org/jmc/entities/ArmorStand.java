package org.jmc.entities;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.jmc.OBJInputFile;
import org.jmc.OBJInputFile.OBJGroup;
import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Double;
import org.jmc.NBT.TAG_Float;
import org.jmc.NBT.TAG_List;
import org.jmc.NBT.TAG_String;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.models.Head;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ObjChunkProcessor;
import org.jmc.util.Filesystem.JmcConfFile;
import org.jmc.util.Log;

public class ArmorStand extends Entity {
	
	public ArmorStand(@Nonnull String id) {
		super(id);
	}
	
	private Transform getTranslate(TAG_Compound entity, float x, float y, float z, float scale) {
		TAG_List pos = (TAG_List) entity.getElement("Pos");
		float ex=(float)((TAG_Double)pos.getElement(0)).value-0.5f + x;
		float ey=(float)((TAG_Double)pos.getElement(1)).value-0.5f + y;
		float ez=(float)((TAG_Double)pos.getElement(2)).value-0.5f + z;
		Transform translate = Transform.translation(ex, ey, ez);
		
		Transform tScale = Transform.scale(scale, scale, scale);
		
		translate = translate.multiply(tScale);
		
		return translate;
	}
	
	private Transform getTranslate(TAG_Compound entity) {
		return getTranslate(entity, 0, 0, 0, 1);
	}
	
	
	private Transform getRotate(TAG_Compound entity, float offset) {
		TAG_List rot = (TAG_List) entity.getElement("Rotation");
		float yaw=((TAG_Float)rot.getElement(0)).value;
		float pitch=((TAG_Float)rot.getElement(1)).value;
		Transform rotate = Transform.rotation2(yaw+offset, pitch ,0);
		return rotate;
	}

	
	@Override
	public void addEntity(ObjChunkProcessor obj, TAG_Compound entity) {
		model.addEntity(obj, getTranslate(entity).multiply(getRotate(entity, 90)));
		
		// equipment
		// Log.info("ArmorStand found: "+entity.getElement("Equipment"));
		TAG_List Equipment = (TAG_List) entity.getElement("ArmorItems");
		
		for(int i=0; i<4; i++) {
			TAG_Compound armor = ((TAG_Compound)Equipment.getElement(i));
			String item_id = "";
			try {
				item_id = ((TAG_String)armor.getElement("id")).value;
			} catch (NullPointerException e) {
				Log.debug("Slot "+i+" not armed");
				continue;
			}
			TAG_Compound tag = ((TAG_Compound)armor.getElement("tag"));
			
			TAG_List enchanted = null;
			try {
				enchanted = ((TAG_List)tag.getElement("ench"));
			} catch (Exception e) {
			}
			
			String mcMaterial = item_id.substring(item_id.indexOf(":")+1, item_id.indexOf("_"));
			
			// internal: offset x, y, z, rotate, initialscale
			// blender:  offset ?, z, -y, rotate, initialscale
			double posCorrection[][] = {
					{0, 0.194, -0.03, 0, 1}, // feet
					{0, 0.778, 0.01, 0, 1}, // legs
					{0, 1.14, 0.002, 180, 1}, // chest
					{0, 1.71, 0, 180, 1} // helmet
			};
			
			String tex[] = {
					"layer_2", //feet
					"layer_2", //legs
					"layer_1", //chest
					"layer_1"  //helmet
			};
			
			String model[] = {
					"feet", //feet
					"legs", //legs
					"chest", //chest
					"helmet"  //helmet
			};
			
			if (mcMaterial.equals("golden")) mcMaterial = "gold";
			if (mcMaterial.equals("player")) {
				NBT_Tag skullOwnerNbt = tag.getElement("SkullOwner");
				if (skullOwnerNbt instanceof TAG_Compound) {
					Transform t = getTranslate(entity, 0, 1.64f, 0, 1).multiply(getRotate(entity, 180));
					Head.addPlayerHead(obj, t, (TAG_Compound)skullOwnerNbt, null);
					continue;
				}
			}
			
			// base material
			if (model[i] != null) {
				addArmor("conf/models/armor_"+model[i]+".obj", 
						NamespaceID.fromString("models/armor/" + mcMaterial + "_"+tex[i]), 
						obj, entity, 
						posCorrection[i][0], 
						posCorrection[i][1],
						posCorrection[i][2],
						1 * posCorrection[i][4],
						posCorrection[i][3]);
			}
			
			// leather overlay
			if (mcMaterial.equals("leather")) {
				if (model[i] != null) {
					addArmor("conf/models/armor_"+model[i]+".obj", 
							NamespaceID.fromString("models/armor/" + mcMaterial + "_"+tex[i]+"_overlay"), 
							obj, entity, 
							posCorrection[i][0], 
							posCorrection[i][1],
							posCorrection[i][2],
							1.04 * posCorrection[i][4],
							posCorrection[i][3]);
				}
			}
			
			// is enchanted
			if (enchanted != null) {
				if (model[i] != null) {
					addArmor("conf/models/armor_"+model[i]+".obj", 
							NamespaceID.fromString("misc/enchanted_item_glint"), 
							obj, entity, 
							posCorrection[i][0], 
							posCorrection[i][1],
							posCorrection[i][2],
							1.08 * posCorrection[i][4],
							posCorrection[i][3]);
				}
			}
			 
		}
		
	}

	public void addArmor(String objFileName, NamespaceID material, ObjChunkProcessor obj, TAG_Compound entity, double x, double y, double z, double scale, double rotation) {
		
		OBJInputFile objFile = new OBJInputFile();
		
		try (JmcConfFile objFileStream = new JmcConfFile(objFileName)) {
			objFile.loadFile(objFileStream, material.getExportSafeString());
		} catch (IOException e) {
			Log.error("Cant read Armor_Stand Equipment obj", e, true);
			return;
		}
		
		OBJGroup myObjGroup = objFile.getDefaultObject();
		myObjGroup = objFile.overwriteMaterial(myObjGroup, material);
		// Log.info("myObjGroup: "+myObjGroup);
		Transform translate = getTranslate(entity, (float)x, (float)y, (float)z, (float)scale).multiply(getRotate(entity, (float) rotation));
		
		objFile.addObjectToOutput(myObjGroup, translate, obj, false);
	}

	@Override
	public Vertex getPosition(TAG_Compound entity) {
		TAG_List pos = (TAG_List) entity.getElement("Pos");
		float ex=(float)((TAG_Double)pos.getElement(0)).value-0.5f;
		float ey=(float)((TAG_Double)pos.getElement(1)).value-0.5f;
		float ez=(float)((TAG_Double)pos.getElement(2)).value-0.5f;
		return new Vertex(ex, ey, ez);
	}
	
}
