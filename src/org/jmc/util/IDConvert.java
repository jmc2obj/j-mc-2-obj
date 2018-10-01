package org.jmc.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts between block ID ints and ID strings.
 * 
 * @author mmdanggg2
 *
 */
public class IDConvert {
	
	public static final Map<Integer, String> mappings;
	
	static {
		Map<Integer, String>map = new HashMap<Integer, String>();
		//probably should load these from a config file...
		map.put(0, "minecraft:air");
		map.put(1, "minecraft:stone");
		map.put(2, "minecraft:grass");
		map.put(3, "minecraft:dirt");
		map.put(4, "minecraft:cobblestone");
		map.put(5, "minecraft:planks");
		map.put(6, "minecraft:sapling");
		map.put(7, "minecraft:bedrock");
		map.put(8, "minecraft:flowing_water");
		map.put(9, "minecraft:water");
		map.put(18, "minecraft:leaves");
		map.put(31, "minecraft:grass");
		mappings = Collections.unmodifiableMap(map);
	}

	public static int strToInt(String str) {
		int id = 0;
        for(Map.Entry<Integer, String> entry: mappings.entrySet()){
            if(str.equals(entry.getValue())){
                id = entry.getKey();
                break;
            }
        }
		return id;
	}

	public static String intToStr(int id) {
		return mappings.get(id);
	}
	
}
