package org.jmc;

import java.util.AbstractMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jmc.registry.NamespaceID;

@ParametersAreNonnullByDefault
public class BlockData {
	
	/**
	 * Sets id to NamespaceID.NULL.
	 */
	public BlockData() {
		this(NamespaceID.NULL);
	}
	
	public BlockData(NamespaceID id) {
		this(id, new Blockstate());
	}
	
	public BlockData(NamespaceID id, Blockstate state) {
		this.id = id;
		this.state = state;
	}
	
	@Nonnull
	public NamespaceID id;
	@Nonnull
	public Blockstate state;
	
	@Override
	public boolean equals(@CheckForNull Object o) {
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
