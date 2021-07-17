package org.jmc.registry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Registries {
	private static HashMap<NamespaceID, BlockstateEntry> blockstates = new HashMap<>();
	private static HashMap<NamespaceID, ModelEntry> models = new HashMap<>();
	private static HashMap<NamespaceID, TextureEntry> textures = new HashMap<>();
	
	static String BS_FOLDER = "C:\\Users\\James\\Desktop\\Java\\jmcTest\\1.17\\assets\\minecraft\\blockstates";
	public static BlockstateEntry getBlockstate(NamespaceID id) {
		if (!blockstates.containsKey(id)) {
			BlockstateEntry entry = addNewBlockstate(id);
			return entry;
		}
		return blockstates.get(id);
	}

	private static synchronized BlockstateEntry addNewBlockstate(NamespaceID id) {
		File file = new File(BS_FOLDER, id.path + ".json");
		JsonObject json = null;
		try (FileReader fr = new FileReader(file)){
			json = new Gson().fromJson(fr, JsonObject.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		BlockstateEntry entry = BlockstateEntry.parseJson(id, json);
		blockstates.put(id, entry);
		return entry;
	}

	static String MDL_FOLDER = "C:\\Users\\James\\Desktop\\Java\\jmcTest\\1.17\\assets\\minecraft\\models";
	public static ModelEntry getModel(NamespaceID id) {
		if (!models.containsKey(id)) {
			ModelEntry entry = addNewModel(id);
			return entry;
		}
		return models.get(id);
	}

	private static synchronized ModelEntry addNewModel(NamespaceID id) {
		File file = new File(MDL_FOLDER, id.path + ".json");
		JsonObject json = null;
		try (FileReader fr = new FileReader(file)){
			json = new Gson().fromJson(fr, JsonObject.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		ModelEntry entry = ModelEntry.parseJson(id, json);
		models.put(id, entry);
		return entry;
	}

	static String TEX_FOLDER = "C:\\Users\\James\\Desktop\\Java\\jmcTest\\1.17\\assets\\minecraft\\textures";
	public static TextureEntry getTexture(NamespaceID id) {
		if (!textures.containsKey(id)) {
			TextureEntry entry = addNewTexture(id);
			return entry;
		}
		return textures.get(id);
	}

	private static synchronized TextureEntry addNewTexture(NamespaceID id) {
		File file = new File(TEX_FOLDER, id.path + ".png");
		if (!file.exists()) {
			return null;
		}
		TextureEntry entry;
		try (InputStream is = new FileInputStream(file)) {
			entry = TextureEntry.fromStream(id, is);
			entry.addToMaterials();
			entry.exportTexture();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		textures.put(id, entry);
		return entry;
	}
}
