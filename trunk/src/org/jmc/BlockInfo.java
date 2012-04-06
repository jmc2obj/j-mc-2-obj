package org.jmc;

import java.awt.Color;

import org.jmc.models.BlockModel;


/**
 * Information about a minecraft block type.
 */
public class BlockInfo
{
	/** How a block occludes adjacent blocks */
	public enum Occlusion {
		/** Adj. faces are never drawn */
		FULL,
		/** Adj. faces are always drawn */
		NONE,
		/** Adj. faces are not drawn if they have the same block id */
		TRANSPARENT,
		/** The top face of the block below is not drawn */
		BOTTOM,
	}

	
	/** Block id */
	public int id;
	
	/** Block name */
	public String name;

	/** Color to draw in the interactive map */
	public Color color;
	
	/** How this block occludes adjacent blocks */
	public BlockInfo.Occlusion occlusion;

	/** 3D model handler for this block */
	public BlockModel model;
	
	
	/** Convenience constructor */
	BlockInfo(int id, String name, Color color, Occlusion occlusion, BlockModel model)
	{
		this.id = id;
		this.name = name;
		this.color = color;
		this.occlusion = occlusion;
		this.model = model;
	}
}