package org.jmc.util;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import org.jmc.BlockData;
import org.jmc.Blockstate;
import org.jmc.registry.NamespaceID;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts between block ID ints and ID strings.
 * 
 * @author mmdanggg2
 *
 */
public class IDConvert {
	private static final String CONFIG_FILE = "conf/idmapping.json";
	
	public static Map<Integer, OldBlockID> blockMappings = new HashMap<>();
	public static Map<Integer, String> biomeMappings = new HashMap<>();
	
	/**
	 * Reads the configuration file.
	 * Must be called once at the start of the program.
	 */
	public static void initialize()
	{
		// create the blocks table
		Log.info("Reading ID conversion configuration file...");
		
		readConfig();
		
		Log.info(String.format("Loaded %d block and %d biome mappings.", blockMappings.size(), biomeMappings.size()));
	}
	
	private static void readConfig() {
		try (Filesystem.JmcConfFile texturesJsonFile = new Filesystem.JmcConfFile(CONFIG_FILE)) {
			Gson gson = new GsonBuilder()
					.registerTypeAdapter(OldBlockID.class, new OldBlockID.Adapter())
					.excludeFieldsWithoutExposeAnnotation()
					.create();
			JsonObject root = gson.fromJson(new InputStreamReader(texturesJsonFile.getInputStream()), JsonObject.class);
			biomeMappings = gson.fromJson(root.get("biomes"), new TypeToken<Map<Integer, String>>() {}.getType());
			blockMappings = gson.fromJson(root.get("blocks"), new TypeToken<Map<Integer, OldBlockID>>() {}.getType());
		} catch (Exception e) {
			Log.error("Error loading " + CONFIG_FILE, e);
		}
	}

	public static BlockData convertBlock(int id , byte data) {
		BlockData block = new BlockData(NamespaceID.UNKNOWN);
		OldBlockID newId = blockMappings.get(id);
		if (newId != null) {
			try {
				block = newId.getBlockData(data);
			} catch (OldBlockID.InvalidOldBlockDefinitionException e) {
				Log.errorOnce(String.format("Invalid block id definition for '%d'!", id), e, true);
			}
			//Log.debugOnce(String.format("block id '%d:%d' converted to '%s'", id, Byte.toUnsignedInt(data), block));
        } else {
			Log.debugOnce(String.format("unknown block id: %d:%d", id, Byte.toUnsignedInt(data)));
		}
		return block;
	}
	
	public static NamespaceID convertBiome(int id) {
		NamespaceID biome = NamespaceID.UNKNOWN;
		String newId = biomeMappings.get(id);
		if (newId != null) {
			biome = NamespaceID.fromString(newId);
			//Log.debugOnce(String.format("biome id '%d' converted to '%s'", id, biome));
		} else {
			Log.debugOnce("unknown biome id: " + id);
		}
		return biome;
	}
	
	@JsonAdapter(OldBlockID.Adapter.class)
	private static class OldBlockID {
		@Expose
		@CheckForNull
		NamespaceID id;
		@Expose
		@CheckForNull
		Blockstate state;
		@Expose
		@CheckForNull
		Map<Integer, BlockData> dataValues;
		
		@Nonnull
		public BlockData getBlockData(byte data) throws InvalidOldBlockDefinitionException {
			if (dataValues == null) {
				if (id == null) {
					throw new InvalidOldBlockDefinitionException("No id or dataValues!");
				} else if (state == null) {
					return new BlockData(id);
				} else {
					return new BlockData(id, state);
				}
			} else {
				int dataInt = Byte.toUnsignedInt(data);
				BlockData bd = dataValues.get(dataInt);
				bd = bd == null ? new BlockData() : new BlockData(bd);
				if (id == null && bd.id == NamespaceID.NULL) {
					throw new InvalidOldBlockDefinitionException(String.format("No id for block or data '%d'!", dataInt));
				} else if (bd.id == NamespaceID.NULL) {
					bd.id = id;
				}
				if (state != null) {
					bd.state.putAll(state);
				}
				return bd;
			}
		}
		
		private static class InvalidOldBlockDefinitionException extends Exception {
			public InvalidOldBlockDefinitionException(String message) {
				super(message);
			}
		}
		
		private static class Adapter implements JsonDeserializer<OldBlockID> {
			@Override
			public OldBlockID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				OldBlockID obi = new OldBlockID();
				if (json.isJsonPrimitive()) {
					obi.id = NamespaceID.fromString(json.getAsString());
				} else if (json.isJsonObject()) {
					JsonObject obiObj = json.getAsJsonObject();
					JsonElement id = obiObj.get("id");
					if (id != null) {
						obi.id = context.deserialize(id, NamespaceID.class);
					}
					JsonElement state = obiObj.get("state");
					if (state != null) {
						obi.state = context.deserialize(state, Blockstate.class);
					}
					JsonElement dataValuesElem = obiObj.get("dataValues");
					if (dataValuesElem != null) {
						obi.dataValues = new HashMap<>();
						Map<String, BlockData> dataValues = context.deserialize(dataValuesElem, new TypeToken<Map<String, BlockData>>(){}.getType());
						for (Map.Entry<String, BlockData> dataEntry : dataValues.entrySet()) {
							String datasStr = dataEntry.getKey();
							String[] datas = datasStr.split(",");
							for (String data : datas) {
								obi.dataValues.put(Integer.parseInt(data), dataEntry.getValue());
							}
						}
					}
				} else {
					throw new JsonParseException("OldBlockID element not string or object!");
				}
				return obi;
			}
		}
	}
}
