package org.jmc;

import java.util.AbstractMap;

public class BlockData {
	
	/**
	 * Sets id to "" empty string.
	 */
	public BlockData() {
		this("");
	}
	
	public BlockData(String id) {
		this(id, new Blockstate());
	}
	
	public BlockData(String id, Blockstate state) {
		this.id = id;
		this.state = state;
	}
	
	public String id;
	public Blockstate state;
	
	@Override
	public boolean equals(Object o) {
		if (super.equals(o) && o instanceof BlockData) {
			BlockData bd = (BlockData)o;
			return id.equals(bd.id) && state.equals(bd.state);
		}
		return false;
	}
	
	/**
	 * @param other
	 * @return result of {@link AbstractMap#equals(Object)} ignoring block id
	 */
	public boolean equalData(BlockData other) {
		return state.equals(other.state);
	}
	
	@Override
	public String toString() {
		return String.format("id=%s %s", id, state.toString());
	}
}
