package org.jmc.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import org.jmc.Blockstate;
import org.jmc.Options;
import org.jmc.geom.BlockPos;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public abstract class BlockstateEntry extends RegistryEntry {
	
	protected BlockstateEntry(NamespaceID name) {
		super(name);
	}
	
	public static BlockstateEntry parseJson(NamespaceID name, JsonObject json) {
		BlockstateEntry entry = null;
		JsonElement variantsElem = json.get("variants");
		JsonElement multipartsElem = json.get("multipart");
		if (variantsElem != null) {
			entry = new BlockstateVariantEntry(name);
			entry.parseJson(variantsElem);
		} else if (multipartsElem != null) {
			entry = new BlockstateMultipartEntry(name);
			entry.parseJson(multipartsElem);
		}
		return entry;
	}
	
	protected abstract void parseJson(JsonElement json);
	
	@Nonnull
	public abstract List<ModelListWeighted> getModelsFor(Blockstate state);
	
	protected static HashMap<String, String> parseStateString(String stateStr) {
		HashMap<String, String> map = new HashMap<>();
		for (String state : stateStr.split(",")) {
			String[] keyVal = state.split("=");
			if (keyVal.length == 2) {
				map.put(keyVal[0].trim(), keyVal[1].trim());
			}
		}
		return map;
	}
	
	public static class ModelInfo {
		@SerializedName("model")
		public NamespaceID id;
		public int x, y = 0;
		public boolean uvlock = false;
		public int weight = 1;
		
		private ModelInfo() {}
		
		@Override
		public String toString() {
			return new Gson().toJson(this);
		}
	}
	
	public static class ModelListWeighted {
		List<ModelInfo> models = new ArrayList<>();
		
		protected void addModel(ModelInfo model) {
			models.add(model);
		}
		
		public ModelInfo getRandomModel(BlockPos pos) {
			if (!Options.randBlockVariations) {
				return models.get(0);
			}
			int maxWeight = 0;
			for (ModelInfo object : models) {
				maxWeight += object.weight;
			}
			float randVal = pos.getRandom().nextFloat()*maxWeight;
			for (ModelInfo object : models) {
				if (randVal < object.weight) {
					return object;
				}
				randVal -= object.weight;
			}
			return models.get(0);
		}
	}
}
