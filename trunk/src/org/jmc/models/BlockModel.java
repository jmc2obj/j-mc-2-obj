package org.jmc.models;

import org.jmc.BlockMaterial;


/**
 * Base class for the block model handlers.
 * These handlers are responsible for rendering the geometry that represents the blocks.
 */
public abstract class BlockModel
{
	protected int blockId = -1;
	protected BlockMaterial materials = null;
	

	/**
	 * Id of the block this model will be rendering.
	 * This information may influence the behavior of the model.
	 */
	public void setBlockId(int val) {
		this.blockId = val;
	}
	
	/**
	 * Set the materials for this block.
	 */
	public void setMaterials(BlockMaterial val)
	{
		this.materials = val;
	}


	
	/*
	 * TODO define interface
	 */
}
