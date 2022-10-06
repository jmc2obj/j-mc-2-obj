package org.jmc;

import org.jmc.geom.BlockPos;
import org.jmc.registry.NamespaceID;

public class BlockDataPos extends BlockData{
	public final BlockPos pos;
	public final NamespaceID biome;
	
	public BlockDataPos(BlockData bd, BlockPos pos, NamespaceID biome){
		super(bd);
		this.pos = pos;
		this.biome = biome;
	}
}
