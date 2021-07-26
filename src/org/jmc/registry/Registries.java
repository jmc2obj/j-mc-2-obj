package org.jmc.registry;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.CheckForNull;

import org.jmc.util.Filesystem.JmcConfFile;
import org.jmc.util.Log;
import org.jmc.util.ResourcePackIO;

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

	private static Map<NamespaceID, BlockstateEntry> blockstates = new ConcurrentHashMap<>();
	private static Map<NamespaceID, Lock> blockstatesLocks = new HashMap<>();
	private static Map<NamespaceID, ModelEntry> models = new ConcurrentHashMap<>();
	private static Map<NamespaceID, Lock> modelsLocks = new HashMap<>();
	private static Map<NamespaceID, TextureEntry> textures = new ConcurrentHashMap<>();
	private static Map<NamespaceID, Lock> texturesLocks = new HashMap<>();
	
	public static Set<TextureEntry> objTextures = Collections.synchronizedSet(new HashSet<>());
	
	private static Lock getLock(Map<NamespaceID, Lock> map, NamespaceID id) {
		Lock lock = map.get(id);
		if (lock == null) {
			lock = new ReentrantLock();
			map.put(id, lock);
		}
		return lock;
	}
	
	@CheckForNull
	public static BlockstateEntry getBlockstate(NamespaceID id) {
		Lock idLock;
		boolean hasLock = false;
		synchronized (blockstatesLocks) {
			if (blockstates.containsKey(id)) {
				return blockstates.get(id);// if it exists then just return
			} else {
				idLock = getLock(blockstatesLocks, id);
				hasLock = idLock.tryLock();// try and lock to create
			}
		}
		try {
			if (hasLock) {// we have the lock to create, add to map
				BlockstateEntry entry = addNewBlockstate(id);
				return entry;
			} else {
				idLock.lock();// wait for the creation thread to finish
				return blockstates.get(id);
			}
		} finally {
			idLock.unlock();
		}
	}

	private static BlockstateEntry addNewBlockstate(NamespaceID id) {
		JsonObject json = null;
		try (InputStream is = ResourcePackIO.loadResourceAsStream(getFilePath(id, RegType.BLOCKSTATE))){
			json = new Gson().fromJson(new InputStreamReader(is), JsonObject.class);
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
	
	@CheckForNull
	public static ModelEntry getModel(NamespaceID id) {
		Lock idLock;
		boolean hasLock = false;
		synchronized (modelsLocks) {
			if (models.containsKey(id)) {
				return models.get(id);
			} else {
				idLock = getLock(modelsLocks, id);
				hasLock = idLock.tryLock();
			}
		}
		try {
			if (hasLock) {
				ModelEntry entry = addNewModel(id);
				return entry;
			} else {
				idLock.lock();
				return models.get(id);
			}
		} finally {
			idLock.unlock();
		}
	}

	private static ModelEntry addNewModel(NamespaceID id) {
		JsonObject json = null;
		try (InputStream is = ResourcePackIO.loadResourceAsStream(getFilePath(id, RegType.MODEL))){
			json = new Gson().fromJson(new InputStreamReader(is), JsonObject.class);
		} catch (FileNotFoundException e) {
			Log.info("Couldn't find ");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		ModelEntry entry = ModelEntry.parseJson(id, json);
		models.put(id, entry);
		return entry;
	}

	public static TextureEntry getTexture(NamespaceID id) {
		Lock idLock;
		boolean hasLock = false;
		synchronized (texturesLocks) {
			if (textures.containsKey(id)) {
				return textures.get(id);
			} else {
				idLock = getLock(texturesLocks, id);
				hasLock = idLock.tryLock();
			}
		}
		try {
			if (hasLock) {
				TextureEntry entry = addNewTexture(id);
				return entry;
			} else {
				idLock.lock();
				return textures.get(id);
			}
		} finally {
			idLock.unlock();
		}
	}

	private static TextureEntry addNewTexture(NamespaceID id) {
		TextureEntry entry = new TextureEntry(id);
		textures.put(id, entry);
		return entry;
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
			throw new RuntimeException("Invalid registry type getting path for id " + id);
		}
	}

	public static void initialize() {
		blockstates.clear();
		models.clear();
		textures.clear();
		objTextures.clear();
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
				if (textureJson.id == null) {
					throw new Exception("Texture id is null!");
				}
				textures.put(textureJson.id, textureJson);
			}
		} catch (Exception e) {
			Log.error("Error loading textures.json", e);
		}
	}

	public static void reloadResourcePacks() {
		initialize();
	}
}
