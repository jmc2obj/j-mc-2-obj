package org.jmc.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.jmc.Blockstate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BlockstateVariantEntry extends BlockstateEntry {
	private HashMap<Blockstate, ModelListWeighted> models = new HashMap<>();
	
	public BlockstateVariantEntry(NamespaceID name) {
		super(name);
	}
	
	@Override
	protected void parseJson(JsonElement variantsElem) {
		JsonObject variantsObj = variantsElem.getAsJsonObject();
		for (String variantKey : variantsObj.keySet()) {
			Blockstate blockstate = new Blockstate();
			blockstate.putAll(parseStateString(variantKey));
			ModelListWeighted stateModels = new ModelListWeighted();
			models.put(blockstate, stateModels);
			
			JsonElement variantElem = variantsObj.get(variantKey);
			if (variantElem.isJsonArray()) {
				JsonArray variantArr = variantElem.getAsJsonArray();
				for (JsonElement modelElem : variantArr) {
					ModelInfo model = new Gson().fromJson(modelElem, ModelInfo.class);
					stateModels.addModel(model);
				}
			} else {
				ModelInfo model = new Gson().fromJson(variantElem, ModelInfo.class);
				stateModels.addModel(model);
			}
		}
	}
	
	@Override
	public List<ModelListWeighted> getModelsFor(Blockstate state) {
		List<ModelListWeighted> stateModels = new ArrayList<>();
		for (Entry<Blockstate, ModelListWeighted> modelEntry : models.entrySet()) {
			if (modelEntry.getKey().maskMatches(state)) {
				stateModels.add(modelEntry.getValue());
				return stateModels;// Only 1 matching state for variant type
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s", super.toString(), models.toString());
	}
	
}
