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
}
