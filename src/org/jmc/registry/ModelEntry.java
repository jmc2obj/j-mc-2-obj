package org.jmc.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.jmc.geom.Vertex;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ModelEntry extends RegistryEntry {
	public RegistryModel model;
	private RegistryModel generatedModel;
	
	protected ModelEntry(NamespaceID id) {
		super(id);
	}

	public static ModelEntry parseJson(NamespaceID id, JsonObject json) {
		ModelEntry entry = new ModelEntry(id);
		entry.model = new Gson().fromJson(json, RegistryModel.class);
		if (entry.model.parent != null) {
			entry.model.parentEntry = Registries.getModel(entry.model.parent);
		}
		return entry;
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
			generatedModel = new Gson().fromJson(new Gson().toJson(model), model.getClass());// clone via serialisation
			generatedModel.parentEntry = model.parentEntry;
			if (generatedModel.parentEntry != null) {
				generatedModel.parentEntry.propagateToChild(generatedModel);
			}
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
				childModel.textures.put(textureEntry.getKey(), textureEntry.getValue());
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
