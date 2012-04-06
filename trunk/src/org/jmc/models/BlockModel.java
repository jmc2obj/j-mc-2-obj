package org.jmc.models;

import java.util.HashMap;


/**
 * Base class for the block model handlers.
 * These handlers are responsible for rendering the geometry that represents the blocks.
 */
public abstract class BlockModel
{
	protected int blockId = -1;
	protected HashMap<Integer, String[]> materials = null;
	

	/**
	 * Id of the block this model will be rendering.
	 * This information may influence the behavior of the model.
	 */
	public void setBlockId(int val) {
		this.blockId = val;
	}
	
	/**
	 * XXX describe the expected indexes
	 */
	public void setMaterials(HashMap<Integer, String[]> val)
	{
		this.materials = val;
	}


	
	/*
	 * TODO define interface
	 */
}
