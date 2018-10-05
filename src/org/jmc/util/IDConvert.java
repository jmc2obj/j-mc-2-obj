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
	
	public static final Map<String, Integer> mappings;
	
	static {
		Map<String, Integer>map = new HashMap<String, Integer>();
		//probably should load these from a config file...
		map.put("minecraft:air", 0);
		map.put("minecraft:cave_air", 0);
		map.put("minecraft:void_air", 0);
		map.put("minecraft:stone", 1);
		map.put("minecraft:grass", 2);
		map.put("minecraft:dirt", 3);
		map.put("minecraft:cobblestone", 4);
		map.put("minecraft:planks", 5);
		map.put("minecraft:sapling", 6);
		map.put("minecraft:bedrock", 7);
		map.put("minecraft:flowing_water", 8);
		map.put("minecraft:water", 9);
		map.put("minecraft:leaves", 18);
		map.put("minecraft:grass", 31);
		mappings = Collections.unmodifiableMap(map);
	}

	public static int strToInt(String str) {
		int ret = 1;
		if (mappings.containsKey(str)) {
			ret = mappings.get(str);
		}
		return ret;
	}

	public static String intToStr(int id) {
		String str = "";
        for(Map.Entry<String, Integer> entry: mappings.entrySet()){
            if(id == entry.getValue()){
                str = entry.getKey();
                break;
            }
        }
		return str;
	}
	
}
