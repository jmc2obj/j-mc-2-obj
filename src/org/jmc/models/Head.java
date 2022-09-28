package org.jmc.models;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

import org.jmc.BlockData;
import org.jmc.OBJInputFile;
import org.jmc.OBJInputFile.OBJGroup;
import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_List;
import org.jmc.NBT.TAG_String;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.registry.NamespaceID;
import org.jmc.registry.Registries;
import org.jmc.registry.TextureEntry;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Filesystem.JmcConfFile;
import org.jmc.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;


/**
 * Model for shrunken heads.
 */
public class Head extends BlockModel
{
	private static Set<NamespaceID> addedMaterials = new HashSet<>();
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		boolean onWall = false;
		String facing = "";
		int rot = 0;
		
		if (data.state.containsKey("facing")) { //If it has a "facing" state, then it's a wall skull
			onWall = true;
			facing = data.state.get("facing");
		} else {
			rot = Integer.parseInt(data.state.get("rotation"));
		}
		
		
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		float r = 0;
		float tx = 0, ty = 0, tz = 0;
		if (!onWall)// on the ground
		{
			switch (rot)
			{
				case 0: r = 0f; break;
				case 1: r = 22.5f; break;
				case 2: r = 45f; break;
				case 3: r = 67.5f; break;
				case 4: r = 90f; break;
				case 5: r = 112.5f; break;
				case 6: r = 135f; break;
				case 7: r = 157.5f; break;
				case 8: r = 180f; break;
				case 9: r = 202.5f; break;
				case 10: r = 225f; break;
				case 11: r = 247.5f; break;
				case 12: r = 270f; break;
				case 13: r = 292.5f; break;
				case 14: r = 315f; break;
				case 15: r = 337.5f; break;
			}
			ty = -0.25f;
		} 
		else // on wall
		{
			switch (facing)
			{
				// on wall, facing North
				case "north":
					r = 0f;
					tz = +0.25f;
					break;
				// on wall, facing South
				case "south":
					r = 180f;
					tz = -0.25f;
					break;
				// on wall, facing West
				case "west":
					r = -90f;
					tx = +0.25f;
					break;
				// on wall, facing East
				case "east":
					r = 90f;
					tx = -0.25f;
					break;
			}
		}
		rotate = Transform.rotation(0, r, 0);
		translate = Transform.translation(x+tx, y+ty, z+tz);
		rt = translate.multiply(rotate);
		
		NamespaceID[] mtlSides = getMtlSides(data, biome);
		
		TAG_Compound te = chunks.getTileEntity(x, y, z);
		
		if (te != null) {
			NBT_Tag skullOwnerNbt = te.getElement("SkullOwner");
			if (skullOwnerNbt instanceof TAG_Compound) {
				addPlayerHead(obj, rt, (TAG_Compound)skullOwnerNbt, mtlSides[0]);
				return;
			}
		}
		String headType = getConfigNodeValue("headtype", 0);
		if (headType != null && headType.equals("MobHalfTex")) {
			UV[][] uvSides = new UV[][] {
				new UV[] { new UV(16/64f, 32/32f), new UV(8/64f, 32/32f), new UV(8/64f, 24/32f), new UV(16/64f, 24/32f) },
				new UV[] { new UV(8/64f, 16/32f), new UV(16/64f, 16/32f), new UV(16/64f, 24/32f), new UV(8/64f, 24/32f) },
				new UV[] { new UV(24/64f, 16/32f), new UV(32/64f, 16/32f), new UV(32/64f, 24/32f), new UV(24/64f, 24/32f) },
				new UV[] { new UV(16/64f, 16/32f), new UV(24/64f, 16/32f), new UV(24/64f, 24/32f), new UV(16/64f, 24/32f) },
				new UV[] { new UV(0/64f, 16/32f), new UV(8/64f, 16/32f), new UV(8/64f, 24/32f), new UV(0/64f, 24/32f) },
				new UV[] { new UV(24/64f, 24/32f), new UV(16/64f, 24/32f), new UV(16/64f, 32/32f), new UV(24/64f, 32/32f) },
			};
			addBox(obj, -0.25f,-0.25f,-0.25f, 0.25f,0.25f,0.25f, rt, mtlSides, uvSides, null);
		} else {
			addHead(obj, rt, mtlSides[0]);
		}
	}

	@ParametersAreNonnullByDefault
	public static void addPlayerHead(ChunkProcessor obj, Transform rt, TAG_Compound skullOwnerTag, @CheckForNull NamespaceID texID) {
		if (texID == null) {
			texID = NamespaceID.fromString("entity/steve");
		}
		String textureB64 = getSkullOwnerTextureValue(skullOwnerTag);
		if (textureB64 != null) {
			NBT_Tag nameNbt = skullOwnerTag.getElement("Name");
			String name;
			if (nameNbt != null) {
				name = ((TAG_String)nameNbt).value;
			} else {
				String url = extractSkullOwnerTextureUrl(textureB64);
				name = url.substring(url.lastIndexOf('/') + 1);
			}
			texID = new NamespaceID("jmc2obj", "head/player_" + name);
			synchronized (addedMaterials) {
				if (!addedMaterials.contains(texID)) {
					Log.info("Downloading new player head texture: " + texID);
					if (exportSkullOwnerTexture(skullOwnerTag, texID))
						addedMaterials.add(texID);
				}
			}
		}
		addHead(obj, rt, texID);
	}
	
	public static void addHead(ChunkProcessor obj, Transform t, NamespaceID texID) {
		OBJInputFile objFile = new OBJInputFile();
		
		try (JmcConfFile objFileStream = new JmcConfFile("conf/models/player_head.obj")) {
			objFile.loadFile(objFileStream, texID.getExportSafeString());
		} catch (IOException e) {
			Log.error("Cant read player_head.obj", e, true);
			return;
		}
		
		OBJGroup myObjGroup = objFile.getDefaultObject();
		myObjGroup = objFile.overwriteMaterial(myObjGroup, texID);
		
		objFile.addObjectToOutput(myObjGroup, t, obj, false);
	}

	private static String extractSkullOwnerTextureUrl(String textureB64) {
		String textureStr = new String(Base64.getDecoder().decode(textureB64));
		try {
			JsonElement textureJson = JsonParser.parseString(textureStr);
			return textureJson.getAsJsonObject().getAsJsonObject("textures").getAsJsonObject("SKIN").getAsJsonPrimitive("url").getAsString();
		} catch (JsonParseException | IllegalStateException | NullPointerException e) {
			Log.error("Couldn't read head SkullOwner texture JSON", e, false);
			return "";
		}

	}

	private static boolean exportSkullOwnerTexture(TAG_Compound skullOwnerTag, NamespaceID texId) {
		String textureB64 = getSkullOwnerTextureValue(skullOwnerTag);
		if (textureB64 == null) {
			Log.error("Couldn't read SkullOwner properties!", null, false);
			return false;
		}

		String textureUrl = extractSkullOwnerTextureUrl(textureB64);

		try (InputStream inputStream = new URL(textureUrl).openStream()) {
			BufferedImage texture = ImageIO.read(inputStream);
			if (texture.getHeight() == 32) { // Expand old 32px high player textures to new 64px size
				Log.debug("Expanding "+texId+" from 32px to 64px");
				BufferedImage expandedTex = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
				expandedTex.getGraphics().drawImage(texture, 0, 0, 64, 32, 0, 0, 64, 32, null);
				texture = expandedTex;
			}
			TextureEntry texEntry = Registries.getTexture(texId);
			texEntry.setImage(texture);
			return true;
		} catch (IOException e) {
			Log.error("Error downloading head texture!", e, false);
			return false;
		}
	}
	
	private static String getSkullOwnerTextureValue(TAG_Compound skullOwnerTag) {
		NBT_Tag propertiesNbt = skullOwnerTag.getElement("Properties");
		if (propertiesNbt instanceof TAG_Compound) {
			NBT_Tag texturesNbt = ((TAG_Compound)propertiesNbt).getElement("textures");
			if (texturesNbt instanceof TAG_List) {
				NBT_Tag textureNbt = ((TAG_List)texturesNbt).getElement(0);
				if (textureNbt instanceof TAG_Compound) {
					NBT_Tag valueNbt = ((TAG_Compound)textureNbt).getElement("Value");
					if (valueNbt instanceof TAG_String) {
						return ((TAG_String)valueNbt).value;
					}
				}
			}
		}
		return null;
	}
	
	public static void clearExported() {
		synchronized (addedMaterials) {
			addedMaterials.clear();
		}
	}

}
