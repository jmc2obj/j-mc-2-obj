package org.jmc.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jmc.BlockData;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * The property values of each block's default state, for worlds that leave them out.
 * <p>
 * Since 26.3 a section palette entry for a block in its default state has no properties, so the
 * block's models, which are picked by property values, would match nothing. The table comes from the
 * game's own data generator ({@code net.minecraft.data.Main --reports}, reports/blocks.json): one entry
 * per block that has properties, holding the values of its default state.
 */
public class DefaultStates {
	private static final String CONFIG_FILE = "conf/default-states.json";

	private static volatile Map<String, Map<String, String>> defaults;

	/**
	 * Puts the default value of each property the block has into its state, keeping the values it
	 * already has. A block not in the table is left as it is.
	 */
	public static void fill(BlockData block) {
		Map<String, String> blockDefaults = table().get(block.id.toString());
		if (blockDefaults == null) {
			return;
		}
		for (Map.Entry<String, String> entry : blockDefaults.entrySet()) {
			block.state.putIfAbsent(entry.getKey(), entry.getValue());
		}
	}

	private static Map<String, Map<String, String>> table() {
		Map<String, Map<String, String>> table = defaults;
		if (table == null) {
			synchronized (DefaultStates.class) {
				table = defaults;
				if (table == null) {
					defaults = table = readConfig();
				}
			}
		}
		return table;
	}

	private static Map<String, Map<String, String>> readConfig() {
		try (Filesystem.JmcConfFile file = new Filesystem.JmcConfFile(CONFIG_FILE)) {
			Map<String, Map<String, String>> table = new Gson().fromJson(
					new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8),
					new TypeToken<Map<String, Map<String, String>>>() {}.getType());
			Log.info(String.format("Loaded default states of %d blocks.", table.size()));
			return table;
		} catch (Exception e) {
			Log.error("Error loading " + CONFIG_FILE, e);
			return Collections.emptyMap();
		}
	}
}
