package org.jmc;

import java.util.AbstractMap;
import java.util.HashMap;

import org.jmc.geom.Direction;

@SuppressWarnings("serial")
public class BlockData extends HashMap<String, String> {
	
	/**
	 * Sets id to "" empty string.
	 */
	public BlockData() {
		this.id = "";
	}
	
	public BlockData(String id) {
		this.id = id;
	}
	
	public String id;
	
	public boolean matchesMask(BlockData dataMask) {
		for (Entry<String, String> keyPair : dataMask.entrySet()) {
			if (!keyPair.getValue().equals(this.get(keyPair.getKey())))
				return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if (super.equals(o) && o instanceof BlockData) {
			return this.id.equals(((BlockData)o).id);
		}
		return false;
	}
	
	/**
	 * @param other
	 * @return result of {@link AbstractMap#equals(Object)} ignoring block id
	 */
	public boolean equalData(BlockData other) {
		return super.equals(other);
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
	
	@Override
	public String toString() {
		return String.format("id=%s %s", id, super.toString());
	}
}
