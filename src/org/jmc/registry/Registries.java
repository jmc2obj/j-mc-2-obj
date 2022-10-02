package org.jmc.registry;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;

import org.jmc.BlockTypes;
import org.jmc.entities.ItemFrame;
import org.jmc.models.Banner;
import org.jmc.models.Head;
import org.jmc.models.Mesh;
import org.jmc.util.CachedGetter;
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

	private final static CachedGetter<NamespaceID, BlockstateEntry> blockstates;
	private final static CachedGetter<NamespaceID, ModelEntry> models;
	private final static CachedGetter<NamespaceID, TextureEntry> textures;
	
	public final static Set<TextureEntry> objTextures = Collections.synchronizedSet(new HashSet<>());
	
	static {
		blockstates = new CachedGetter<NamespaceID, BlockstateEntry>() {
			@Override
			public BlockstateEntry make(NamespaceID key) {
				try {
					List<Reader> readers = ResourcePackIO.loadAllText(getFilePath(key, RegType.BLOCKSTATE));
					BlockstateEntry entry = null;
					for (Reader reader : readers) {
						JsonObject json = null;
						json = new Gson().fromJson(reader, JsonObject.class);
						if (entry == null) {
							entry = BlockstateEntry.parseJson(key, json);
						} else if (entry instanceof BlockstateVariantEntry) {
							((BlockstateVariantEntry)entry).addStates(json);
						}
						reader.close();
					}
					return entry;
				} catch (FileNotFoundException e) {
					Log.error(String.format("Couldn't find blockstate %s in any resource pack!", key), null, false);
					return null;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		};
		models = new CachedGetter<NamespaceID, ModelEntry>() {
			@Override
			public ModelEntry make(NamespaceID key) {
				JsonObject json = null;
				try (Reader reader = ResourcePackIO.loadText(getFilePath(key, RegType.MODEL))){
					json = new Gson().fromJson(reader, JsonObject.class);
				} catch (FileNotFoundException e) {
					Log.error(String.format("Couldn't find model %s in any resource pack!", key), null, false);
					return null;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				return ModelEntry.parseJson(key, json);
			}
		};
		textures = new CachedGetter<NamespaceID, TextureEntry>() {
			@Override
			public TextureEntry make(NamespaceID key) {
				return new TextureEntry(key);
			}
		};
	}
	
	@CheckForNull
	public static BlockstateEntry getBlockstate(NamespaceID id) {
		return blockstates.get(id);
	}
	
	@CheckForNull
	public static ModelEntry getModel(NamespaceID id) {
		return models.get(id);
	}

	public static TextureEntry getTexture(NamespaceID id) {
		return textures.get(id);
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
		TextureEntry unkTexEntry = new TextureEntry(NamespaceID.UNKNOWN);
		textures.put(NamespaceID.UNKNOWN, unkTexEntry);
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
		Log.resetSingles();
		initialize();
		try {
			BlockTypes.initialize();
		} catch (Exception e) {
			Log.error("Couldn't reload resource packs.", e);
		}
		Banner.clearExported();
		Head.clearExported();
		ItemFrame.clearExported();
		Mesh.clearCache();
	}
}
