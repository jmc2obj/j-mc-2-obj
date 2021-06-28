package org.jmc.models;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.jmc.BlockData;
import org.jmc.Materials;
import org.jmc.Options;
import org.jmc.NBT.NBT_Tag;
import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_List;
import org.jmc.NBT.TAG_String;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;


/**
 * Model for shrunken heads.
 */
public class Head extends BlockModel
{

	private static Set<String> addedMaterials = new HashSet<String>();
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
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
		rotate.rotate(0, r, 0);
		translate.translate(x+tx, y+ty, z+tz);
		rt = translate.multiply(rotate);
		
		String[] mtlSides = getMtlSides(data, biome);
		UV[][] uvSides = new UV[][] {
				new UV[] { new UV(16/64f, 32/32f), new UV(8/64f, 32/32f), new UV(8/64f, 24/32f), new UV(16/64f, 24/32f) },
				new UV[] { new UV(8/64f, 16/32f), new UV(16/64f, 16/32f), new UV(16/64f, 24/32f), new UV(8/64f, 24/32f) },
				new UV[] { new UV(24/64f, 16/32f), new UV(32/64f, 16/32f), new UV(32/64f, 24/32f), new UV(24/64f, 24/32f) },
				new UV[] { new UV(16/64f, 16/32f), new UV(24/64f, 16/32f), new UV(24/64f, 24/32f), new UV(16/64f, 24/32f) },
				new UV[] { new UV(0/64f, 16/32f), new UV(8/64f, 16/32f), new UV(8/64f, 24/32f), new UV(0/64f, 24/32f) },
				new UV[] { new UV(24/64f, 24/32f), new UV(16/64f, 24/32f), new UV(16/64f, 32/32f), new UV(24/64f, 32/32f) },
			};
		
		TAG_Compound te = chunks.getTileEntity(x, y, z);
		
		if (te != null) {
			NBT_Tag skullOwnerNbt = te.getElement("SkullOwner");
			if (skullOwnerNbt instanceof TAG_Compound) {
				TAG_Compound skullOwnerTag = (TAG_Compound)skullOwnerNbt;
				NBT_Tag nameNbt = skullOwnerTag.getElement("Name");
				if (nameNbt instanceof TAG_String) {
					String name = ((TAG_String)nameNbt).value;
					String mtlName = "player_" + name;
					Arrays.fill(mtlSides, mtlName);
					boolean newMat;
					synchronized (addedMaterials) {
						newMat = addedMaterials.add(mtlName);
					}
					if (newMat) {
						Log.debug("Downloading new player head texture: " + mtlName);
						Materials.addMaterial(mtlName, Color.pink, Color.black, "tex/"+mtlName+".png", null);
						exportSkullOwnerTexture(skullOwnerTag, mtlName);
					}
				}
			}
		}
		
		addBox(obj, -0.25f,-0.25f,-0.25f, 0.25f,0.25f,0.25f, rt, mtlSides, uvSides, null);
	}
	
	private void exportSkullOwnerTexture(TAG_Compound skullOwnerTag, String mtlName) {
		String textureB64 = getSkullOwnerTextureValue(skullOwnerTag);
		if (textureB64 == null) {
			Log.error("Couldn't read SkillOwner properties!", null, false);
			return;
		}
		String textureStr = new String(Base64.getDecoder().decode(textureB64));
		String textureUrl;
		try {
			JsonElement textureJson = JsonParser.parseString(textureStr);
			textureUrl = textureJson.getAsJsonObject().getAsJsonObject("textures").getAsJsonObject("SKIN").getAsJsonPrimitive("url").getAsString();
		} catch (JsonParseException | IllegalStateException | NullPointerException e) {
			Log.error("Couldn't read head SkullOwner texture JSON", e, false);
			return;
		}
		
		File texFile = new File(Options.outputDir+"/tex", mtlName+".png");
		try (BufferedInputStream inputStream = new BufferedInputStream(new URL(textureUrl).openStream())) {
			FileOutputStream fileOS = new FileOutputStream(texFile);
			byte data[] = new byte[1024];
			int byteContent;
			while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
				fileOS.write(data, 0, byteContent);
			}
			fileOS.close();
			
			BufferedImage texture = ImageIO.read(texFile);
			if (texture.getHeight() == 64) { // Crop new 64px high player textures to old 32px size
				Log.debug("Cropping "+mtlName+" from 64px to 32px");
				BufferedImage croppedTex = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
				croppedTex.getGraphics().drawImage(texture, 0, 0, 64, 32, 0, 0, 64, 32, null);
				ImageIO.write(croppedTex, "png", texFile);
			}
		} catch (IOException e) {
			Log.error("Error downloading head texture!", e, false);
			return;
		}
		
	}
	
	private String getSkullOwnerTextureValue(TAG_Compound skullOwnerTag) {
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

}
