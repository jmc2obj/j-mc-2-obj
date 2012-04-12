package org.jmc;

import java.awt.Color;

import org.jmc.models.BlockModel;
import org.jmc.util.Log;


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

	/** Materials defined for this block */
	public BlockMaterial materials;
	
	/** How this block occludes adjacent blocks */
	public Occlusion occlusion;

	/** 3D model handler for this block */
	public BlockModel model;
	
	
	/** Convenience constructor */
	BlockInfo(int id, String name, BlockMaterial materials, Occlusion occlusion, BlockModel model)
	{
		this.id = id;
		this.name = name;
		this.materials = materials;
		this.occlusion = occlusion;
		this.model = model;
	}
	

	/**
	 * Convenience method to get the color to use for this block in the map preview.
	 * The color is taken from the first material in the block's material list.
	 *  
	 * @param data Block data
	 * @return Block color
	 */
	public Color getPreviewColor(byte data)
	{
		String[] mtlNames = materials.get(data);
		if (mtlNames == null || mtlNames.length == 0)
		{
			Log.debug("block " + id + " (" + name + ") has no mtl for data="+data);
			return Materials.getColor("unknown");
		}
		return Materials.getColor(mtlNames[0]);
	}
	

}