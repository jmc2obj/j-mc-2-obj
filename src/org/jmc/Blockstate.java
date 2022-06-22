package org.jmc;

import java.util.HashMap;

import org.jmc.geom.Direction;

@SuppressWarnings("serial")
public class Blockstate extends HashMap<String, String> {
	public Blockstate() {
		super(8);
	}

	/**
	 * Uses dataMask state as a mask applied to this state 
	 * @param dataMask
	 * @return true if this matches the mask
	 */
	public boolean matchesMask(Blockstate dataMask) {
		for (Entry<String, String> keyPair : dataMask.entrySet()) {
			if (!keyPair.getValue().equals(this.get(keyPair.getKey())))
				return false;
		}
		return true;
	}
	
	/**
	 * Uses this state as a mask applied to data 
	 * @param data
	 * @return true if data matches this mask
	 */
	public boolean maskMatches(Blockstate data) {
		for (Entry<String, String> keyPair : entrySet()) {
			if (!keyPair.getValue().equals(data.get(keyPair.getKey())))
				return false;
		}
		return true;
	}
	
	/**
	 * @param key map key
	 * @return result of {@link Boolean#parseBoolean(String) parseBoolean} on value, will return null if key not found 
	 */
	public Boolean getBool(String key) {
		if (this.containsKey(key)) {
			return Boolean.parseBoolean(this.get(key));
		} else {
			return null;
		}
	}
	
	/**
	 * @param key map key
	 * @param def default value
	 * @return result of {@link Boolean#parseBoolean(String) parseBoolean} on value, will return default if key not found 
	 */
	public Boolean getBool(String key, Boolean def) {
		Boolean b = getBool(key);
		if (b == null) b = def;
		return b;
	}
	
	/**
	 * @param key map key
	 * @return result of {@link Integer#parseInt(String) parseInt} on value, will return null if key not found 
	 */
	public Integer getInt(String key) {
		if (this.containsKey(key)) {
			return Integer.parseInt(this.get(key));
		} else {
			return null;
		}
	}
	
	/**
	 * @param key map key
	 * @param def default value
	 * @return result of {@link Integer#parseInt(String) parseInt} on value, will return default if key not found 
	 */
	public Integer getInt(String key, Integer def) {
		Integer i = getInt(key);
		if (i == null) i = def;
		return i;
	}
	
	/**
	 * @param key map key
	 * @return result of Direction.valueOf on upper case value, will return null if key not found 
	 */
	public Direction getDirection(String key) {
		if (this.containsKey(key)) {
			return Direction.valueOf(this.get(key).toUpperCase());
		} else {
			return null;
		}
	}
	
	/**
	 * @param key map key
	 * @param def default value
	 * @return result of Direction.valueOf on upper case value, will return default if key not found 
	 */
	public Direction getDirection(String key, Direction def) {
		Direction d = getDirection(key);
		if (d == null) d = def;
		return d;
	}
}
