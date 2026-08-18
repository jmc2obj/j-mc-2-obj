package org.jmc.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.jmc.geom.Vertex;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ModelEntry extends RegistryEntry {
	public RegistryModel model;
	private RegistryModel generatedModel;
	
	protected ModelEntry(NamespaceID id) {
		super(id);
	}

	public static ModelEntry parseJson(NamespaceID id, JsonObject json) {
		ModelEntry entry = new ModelEntry(id);
		flattenTextureEntries(json);
		entry.model = new Gson().fromJson(json, RegistryModel.class);
		if (entry.model.parent != null) {
			entry.model.parentEntry = Registries.getModel(entry.model.parent);
		}
		return entry;
	}
	
	/**
	 * Since 26.1, resource packs can describe texture entries as objects, e.g.
	 * {"sprite": "minecraft:block/glass", "force_translucent": true}.
	 * Reduce each such entry to its sprite name, the only part of the entry
	 * this exporter uses.
	 */
	private static void flattenTextureEntries(JsonObject json) {
		JsonObject textures = json.getAsJsonObject("textures");
		if (textures == null) {
			return;
		}
		for (Entry<String, JsonElement> textureEntry : new ArrayList<>(textures.entrySet())) {
			JsonElement value = textureEntry.getValue();
			if (value.isJsonObject()) {
				JsonObject texture = value.getAsJsonObject();
				JsonElement sprite = texture.get("sprite");
				if (sprite != null) {
					textures.add(textureEntry.getKey(), sprite);
				} else {
					textures.remove(textureEntry.getKey());
				}
			}
		}
	}
	
	public RegistryModel generateModel() {
		//fast path
		if (generatedModel != null) {
			return generatedModel;
		}
		synchronized (this) {
			if (generatedModel != null) {
				return generatedModel;
			}
			RegistryModel newModel = new Gson().fromJson(new Gson().toJson(model), model.getClass());// clone via serialisation
			newModel.parentEntry = model.parentEntry;
			if (newModel.parentEntry != null) {
				newModel.parentEntry.propagateToChild(newModel);
			}
			generatedModel = newModel;
			return generatedModel;
		}
	}
	
	private void propagateToChild(RegistryModel childModel) {
		if (childModel.elements == null) {
			childModel.elements = model.elements;
		}
		for (Entry<String, String> textureEntry : model.textures.entrySet()) {
			if (textureEntry.getValue().startsWith("#")) {
				String refName = textureEntry.getValue().substring(1);
				String refTex = childModel.textures.get(refName);
				assert refTex != null;
				childModel.textures.putIfAbsent(textureEntry.getKey(), refTex);
			} else {
				childModel.textures.putIfAbsent(textureEntry.getKey(), textureEntry.getValue());
			}
		}
		if (model.parentEntry != null) {
			model.parentEntry.propagateToChild(childModel);
		}
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	public static class RegistryModel {
		private transient ModelEntry parentEntry;
		private NamespaceID parent;
		@Nonnull
		public Map<String, String> textures = new HashMap<>();
		public List<ModelElement> elements;
		
		private RegistryModel() {
		}
		
		@Override
		public String toString() {
			return new Gson().toJson(this);
		}
		
		public static class ModelElement {
			public Vertex from;
			public Vertex to;
			public ElementRotation rotation;
			@Nonnull
			public Map<String, ElementFace> faces = new HashMap<>();
			
			@Override
			public String toString() {
				return new Gson().toJson(this);
			}
			
			public static class ElementRotation {
				public Vertex origin;
				public String axis;
				public float angle = 0;
				public boolean rescale = false;
				public float x = 0;
				public float y = 0;
				public float z = 0;
				@Override
				public String toString() {
					return new Gson().toJson(this);
				}
			}
			
			public static class ElementFace {
				public float[] uv;
				public String texture;
				public String cullface;
				public int rotation;
				public float tintindex;
				@Override
				public String toString() {
					return new Gson().toJson(this);
				}
			}
		}
	}
	
}
