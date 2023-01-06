package org.jmc;

import org.jmc.geom.BlockPos;
import org.jmc.registry.NamespaceID;

public class BlockDataPos {
	public BlockPos pos;
	public BlockData data;
	public NamespaceID biome;
	
	public BlockDataPos(BlockPos pos, BlockData data, NamespaceID biome) {
		this.pos = pos;
		this.data = data;
		this.biome = biome;
	}
	
	@Override
	public String toString() {
		return String.format("%s = %s; biome = %s", pos.toString(), data.toString(), biome.toString());
	}
}
