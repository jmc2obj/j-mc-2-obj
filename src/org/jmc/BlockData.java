package org.jmc;

import java.util.HashMap;

@SuppressWarnings("serial")
public class BlockData extends HashMap<String, String> {
	
	public boolean matchesMask(BlockData dataMask) {
		for (Entry<String, String> keyPair : dataMask.entrySet()) {
			if (!keyPair.getValue().equals(this.get(keyPair.getKey())))
				return false;
		}
		return true;
	}
	
	/**
	 * @param key map key
	 * @return result of {@link Boolean#parseBoolean(String) parseBoolean} on value, will return false if key not found 
	 */
	public boolean getBool(String key) {
		if (this.containsKey(key)) {
			return Boolean.parseBoolean(this.get(key));
		} else {
			return false;
		}
	}
}
