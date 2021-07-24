package org.jmc.registry;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jmc.util.Filesystem.JmcConfFile;
import org.jmc.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Registries {
	public enum RegType {
		BLOCKSTATE, MODEL, TEXTURE
	}

	public static final NamespaceID UNKNOWN_TEX_ID = new NamespaceID("jmc2obj", "unknown");

	private static HashMap<NamespaceID, BlockstateEntry> blockstates;
	private static HashMap<NamespaceID, ModelEntry> models;
	private static HashMap<NamespaceID, TextureEntry> textures;
	
	static String BASE_FOLDER = "C:\\Users\\James\\Desktop\\Java\\jmcTest\\1.17";
	
	public static Set<TextureEntry> objTextures = Collections.synchronizedSet(new HashSet<>());
	
	public static BlockstateEntry getBlockstate(NamespaceID id) {
		if (!blockstates.containsKey(id)) {
			BlockstateEntry entry = addNewBlockstate(id);
			return entry;
		}
		return blockstates.get(id);
	}

	private static synchronized BlockstateEntry addNewBlockstate(NamespaceID id) {
		File file = new File(BASE_FOLDER, getFilePath(id, RegType.BLOCKSTATE));
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
	
	public static ModelEntry getModel(NamespaceID id) {
		if (!models.containsKey(id)) {
			ModelEntry entry = addNewModel(id);
			return entry;
		}
		return models.get(id);
	}

	private static synchronized ModelEntry addNewModel(NamespaceID id) {
		File file = new File(BASE_FOLDER, getFilePath(id, RegType.MODEL));
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

	public static TextureEntry getTexture(NamespaceID id) {
		if (!textures.containsKey(id)) {
			TextureEntry entry = addNewTexture(id);
			return entry;
		}
		return textures.get(id);
	}

	private static synchronized TextureEntry addNewTexture(NamespaceID id) {
		TextureEntry entry = new TextureEntry(id);
		textures.put(id, entry);
		return entry;
	}

	public static Collection<TextureEntry> getTextures() {
		return Collections.unmodifiableCollection(textures.values());
	}

	public static String getFilePath(NamespaceID id, RegType model) {
		switch (model) {
		case BLOCKSTATE:
			return String.format("assets/%s/blockstates/%s.json", id.namespace, id.path);
		case MODEL:
			return String.format("assets/%s/models/%s.json", id.namespace, id.path);
		case TEXTURE:
			return String.format("assets/%s/textures/%s.png", id.namespace, id.path);
		default:
			throw new RuntimeException("Invalid registry type getting path for id");
		}
	}

	public static void initialize() {
		blockstates = new HashMap<>();
		models = new HashMap<>();
		textures = new HashMap<>();
		TextureEntry unkTexEntry = addNewTexture(UNKNOWN_TEX_ID);
		BufferedImage unkTex = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		unkTex.setRGB(0, 0, Color.MAGENTA.getRGB());
		unkTexEntry.setImage(unkTex);
		
		try (JmcConfFile texturesJsonFile = new JmcConfFile("conf/textures.json")) {
			Gson gson = new GsonBuilder()
					.registerTypeAdapter(Color.class, new JsonDeserializer<Color>() {
							@Override
							public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
								return new Color(Integer.parseInt(json.getAsString(), 16));
							}
						})
					.excludeFieldsWithoutExposeAnnotation()
					.create();
			TextureEntry[] textureJsonEntries = gson.fromJson(new InputStreamReader(texturesJsonFile.getInputStream()), TextureEntry[].class);
			for (TextureEntry textureJson : textureJsonEntries) {
				textures.put(textureJson.id, textureJson);
			}
		} catch (Exception e) {
			Log.error("Error loading textures.json", e);
		}
	}
}
