package org.jmc.registry;

import java.util.ArrayList;
import java.util.List;

import org.jmc.Blockstate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BlockstateMultipartEntry extends BlockstateEntry {
	private List<MultipartCase> multi = new ArrayList<>();
	
	public BlockstateMultipartEntry(NamespaceID name) {
		super(name);
	}
	
	@Override
	protected void parseJson(JsonElement multipartsElem) {
		for (JsonElement multipartElem : multipartsElem.getAsJsonArray()) {
			if (multipartElem.isJsonObject()) {
				multi.add(new MultipartCase(multipartElem.getAsJsonObject()));
			}
		}
	}
	
	@Override
	public List<ModelListWeighted> getModelsFor(Blockstate state) {
		List<ModelListWeighted> models = new ArrayList<>();
		for (MultipartCase multiCase : multi) {
			if (multiCase.matches(state)) {
				models.add(multiCase.getModels());
			}
		}
		return models;
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s", super.toString(), multi.toString());
	}
	
	private class MultipartCase {
		private ModelListWeighted models = new ModelListWeighted();
		private List<ConditionalBlockstate> conditions = new ArrayList<>();
		
		private MultipartCase(JsonObject multiCase) {
			JsonElement applyElem = multiCase.get("apply");
			if (applyElem.isJsonArray()) {
				JsonArray variantArr = applyElem.getAsJsonArray();
				for (JsonElement modelElem : variantArr) {
					ModelInfo model = new Gson().fromJson(modelElem, ModelInfo.class);
					models.addModel(model);
				}
			} else {
				ModelInfo model = new Gson().fromJson(applyElem, ModelInfo.class);
				models.addModel(model);
			}
			
			JsonObject when = multiCase.getAsJsonObject("when");
			if (when != null) {
				JsonElement orElem = when.get("OR");
				if (orElem != null && orElem.isJsonArray()) {
					JsonArray orArr = orElem.getAsJsonArray();
					for (JsonElement stateElem : orArr) {
						conditions.add(parseConditionalState(stateElem.getAsJsonObject()));
					}
				} else {
					conditions.add(parseConditionalState(when));
				}
			}
		}
	
		private ConditionalBlockstate parseConditionalState(JsonObject stateObj) {
			ConditionalBlockstate condition = new ConditionalBlockstate();
			for (String key : stateObj.keySet()) {
				condition.put(key, stateObj.get(key).getAsString());
			}
			return condition;
		}
		
		public boolean matches(Blockstate state) {
			if (conditions.isEmpty()) {
				return true;
			}
			for (ConditionalBlockstate condition : conditions) {
				if (condition.maskMatches(state)) {
					return true;
				}
			}
			return false;
		}
		
		public ModelListWeighted getModels() {
			return models;
		}
		
		@SuppressWarnings("serial")
		private class ConditionalBlockstate extends Blockstate {
			/**
			 * Uses this state as a mask applied to data 
			 * @param data
			 * @return true if data matches this mask
			 */
			@Override
			public boolean maskMatches(Blockstate data) {
				for (Entry<String, String> keyPair : entrySet()) {
					String[] values = keyPair.getValue().split("\\|");
					boolean matches = false;
					for (String value : values) {
						matches |= value.trim().equals(data.get(keyPair.getKey()));
					}
					if (!matches)
						return false;
				}
				return true;
			}
		}
	}
}
