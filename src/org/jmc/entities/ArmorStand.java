package org.jmc.entities;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
import org.jmc.threading.ChunkProcessor;
import org.jmc.util.Filesystem.JmcConfFile;
import org.jmc.util.Log;

public class ArmorStand extends Entity {
	
	public ArmorStand(@Nonnull String id) {
		super(id);
	}
	
	private Transform getTranslate(TAG_Compound entity, float x, float y, float z, float scale) {
		Vertex vert = Vertex.add(getPosition(entity), new Vertex(x, y, z));
		Transform translate = Transform.translation(vert.x, vert.y, vert.z);
		
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

	private static final Set<String> armor_parts = new HashSet<>(
			Arrays.asList("helmet", "chestplate", "leggings", "boots")
	);
	
	@Override
	public void addEntity(ChunkProcessor obj, TAG_Compound entity) {
		model.addEntity(obj, getTranslate(entity).multiply(getRotate(entity, 90)));
		
		// equipment
		// Log.info("ArmorStand found: "+entity.getElement("Equipment"));
		TAG_List Equipment = (TAG_List) entity.getElement("ArmorItems");
		
		for(int i=0; i<4; i++) {
			TAG_Compound armor = ((TAG_Compound)Equipment.getElement(i));
			TAG_String item_id_tag = (TAG_String)armor.getElement("id");
			if (item_id_tag == null) {
				Log.debug("Armour stand "+getPosition(entity).toString()+" slot " + i + " not armed");
				continue;
			}
			NamespaceID item_id = NamespaceID.fromString(item_id_tag.value);
			String[] item_name_parts = item_id.path.split("_");
			String armor_material = null;
			if (item_name_parts.length == 2 && armor_parts.contains(item_name_parts[1])) {
				armor_material = item_name_parts[0];
			}
			TAG_Compound tag = ((TAG_Compound)armor.getElement("tag"));
			
			TAG_List enchanted = null;
			if (tag != null) {
				enchanted = ((TAG_List)tag.getElement("Enchantments"));
			}
			
			// internal: offset x, y, z, rotate, initialscale
			// blender:  offset ?, z, -y, rotate, initialscale
			double[][] posCorrection = {
					{0, 0.194, -0.03, 0, 1}, // feet
					{0, 0.778, 0.01, 0, 1}, // legs
					{0, 1.14, 0.002, 180, 1}, // chest
					{0, 1.71, 0, 180, 1} // helmet
			};
			
			String[] tex = {
					"layer_2", //feet
					"layer_2", //legs
					"layer_1", //chest
					"layer_1"  //helmet
			};
			
			String[] model = {
					"feet", //feet
					"legs", //legs
					"chest", //chest
					"helmet"  //helmet
			};
			
			if (item_id.equals(new NamespaceID("minecraft", "player_head"))) {
				Transform t = getTranslate(entity, 0, 1.64f, 0, 1).multiply(getRotate(entity, 180));
				Head.addPlayerHead(obj, t, tag, null);
				continue;
			}
			
			// base material
			if (armor_material != null) {
				if (armor_material.equals("golden")) armor_material = "gold";
				addArmor("conf/models/armor_"+model[i]+".obj", 
						NamespaceID.fromString("models/armor/" + armor_material + "_"+tex[i]),
						obj, entity, 
						posCorrection[i][0], 
						posCorrection[i][1],
						posCorrection[i][2],
						1 * posCorrection[i][4],
						posCorrection[i][3]);
				
				// leather overlay
				if (armor_material.equals("leather")) {
					addArmor("conf/models/armor_"+model[i]+".obj",
							NamespaceID.fromString("models/armor/" + armor_material + "_"+tex[i]+"_overlay"),
							obj, entity,
							posCorrection[i][0],
							posCorrection[i][1],
							posCorrection[i][2],
							1.04 * posCorrection[i][4],
							posCorrection[i][3]);
				}
				
				// is enchanted
				if (enchanted != null) {
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

	public void addArmor(String objFileName, NamespaceID material, ChunkProcessor obj, TAG_Compound entity, double x, double y, double z, double scale, double rotation) {
		
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
}
