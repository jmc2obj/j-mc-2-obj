package org.jmc.registry;

import java.util.ArrayList;
import java.util.List;

import org.jmc.Blockstate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BlockstateMultipartEntry extends BlockstateEntry {
	private final List<MultipartCase> multi = new ArrayList<>();
	
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
	
	private static class MultipartCase {
		enum ConditionType {
			AND, OR, SINGLE, NONE
		}
		
		private final ModelListWeighted models = new ModelListWeighted();
		private final List<ConditionalBlockstate> conditions = new ArrayList<>();
		private final ConditionType conditionType;
		
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
				JsonElement andElem = when.get("AND");
				assert !(orElem != null && andElem != null);
				if (orElem != null && orElem.isJsonArray()) {
					JsonArray orArr = orElem.getAsJsonArray();
					conditionType = ConditionType.OR;
					for (JsonElement stateElem : orArr) {
						conditions.add(parseConditionalState(stateElem.getAsJsonObject()));
					}
				} else if (andElem != null && andElem.isJsonArray()) {
					JsonArray andArr = andElem.getAsJsonArray();
					conditionType = ConditionType.AND;
					for (JsonElement stateElem : andArr) {
						conditions.add(parseConditionalState(stateElem.getAsJsonObject()));
					}
				} else {
					conditions.add(parseConditionalState(when));
					conditionType = ConditionType.SINGLE;
				}
			} else {
				conditionType = ConditionType.NONE;
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
			switch (conditionType) {
				case AND:
					for (ConditionalBlockstate condition : conditions) {
						if (!condition.maskMatches(state)) {
							return false;
						}
					}
					return true;
				case OR:
					for (ConditionalBlockstate condition : conditions) {
						if (condition.maskMatches(state)) {
							return true;
						}
					}
					return false;
				case SINGLE:
					assert conditions.size() == 1;
					return conditions.get(0).maskMatches(state);
				case NONE:
					assert conditions.isEmpty();
					return true;
				default:
					throw new RuntimeException(String.format("Invalid condition type! %s", conditionType));
			}
		}
		
		public ModelListWeighted getModels() {
			return models;
		}
		
		private static class ConditionalBlockstate extends Blockstate {
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
