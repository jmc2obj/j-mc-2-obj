package org.jmc;

import java.awt.Color;
import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.jmc.models.BlockModel;
import org.jmc.models.None;
import org.jmc.registry.NamespaceID;
import org.jmc.registry.Registries;
import org.jmc.registry.TextureEntry;
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
		/** Special rules from model */
		CUSTOM,
		/** A closed transparent volume */
		VOLUME
	}

	
	/** Block id */
	protected String id;
	
	/** Block name */
	protected String name;

	/** Materials defined for this block */
	@Nonnull
	protected BlockMaterial materials;
	
	/** How this block occludes adjacent blocks */
	protected Occlusion occlusion;

	/** 3D model handler for this block */
	@Nonnull
	protected BlockModel model;
	
	/** Force this block to always have the waterlogged tag set*/
	protected boolean actWaterlogged;
	

	/** @return Block id */
	public String getId() {
		return id;
	}

	/** @return Block name */
	public String getName() {
		return name;
	}

	/** @return Materials defined for this block */
	public BlockMaterial getMaterials() {
		return materials;
	}

	/** @return How this block occludes adjacent blocks */
	public Occlusion getOcclusion() {
		return occlusion;
	}

	/** @return 3D model handler for this block */
	public BlockModel getModel() {
		return model;
	}

	public boolean getActWaterlogged() {
		return actWaterlogged;
	}

	
	/** Convenience constructor */
	BlockInfo(String id, String name, @CheckForNull BlockMaterial materials, Occlusion occlusion, @CheckForNull BlockModel model, boolean alwaysWaterlogged)
	{
		this.id = id;
		this.name = name;
		if (materials != null) {
			this.materials = materials;
		} else {
			this.materials = new BlockMaterial();
		}
		this.occlusion = occlusion;
		if (model != null) {
			this.model = model;
		} else {
			this.model = new None();
		}
		this.actWaterlogged = alwaysWaterlogged;
	}
	

	/**
	 * Convenience method to get the color to use for this block in the map preview.
	 * The color is taken from the first material in the block's material list.
	 *  
	 * @param blockData Block data
	 * @return Block color
	 */
	public Color getPreviewColor(BlockData blockData, int biome)
	{
		BlockMaterial mat = getMaterials();
		NamespaceID[] mtlNames = mat == null ? null : mat.get(blockData.state,biome);
		TextureEntry te;
		if (mtlNames == null || mtlNames.length == 0)
		{
			Log.debug("block " + getId() + " (" + getName() + ") has no mtl for data="+blockData);
			te = Registries.getTexture(Registries.UNKNOWN_TEX_ID);
		} else {
			te = Registries.getTexture(mtlNames[0]);
		}
		try {
			return te.getAverageColour();
		} catch (IOException e) {
			e.printStackTrace();
			return Color.MAGENTA;
		}
	}

}
