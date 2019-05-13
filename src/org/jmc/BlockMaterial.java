package org.jmc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Holds the material definitions for a block type.
 */
public class BlockMaterial
{
	public String blockID;
	
	public BlockMaterial(String blockID) {
		this.blockID = blockID;
	}
	
	
	private String[] baseMaterials = null;

	private Map<BlockData, String[]> dataMaterials = new LinkedHashMap<BlockData, String[]>();

	private Map<Integer, Map<BlockData, String[]>> biomeMaterials = new LinkedHashMap<Integer, Map<BlockData, String[]>>();


	/**
	 * Sets the materials to use by default, i.e., when there isn't a specific material 
	 * for the block's data value.
	 * 
	 * @param mtlNames 
	 */
	public void put(String[] mtlNames)
	{
		if (mtlNames == null || mtlNames.length == 0)
			throw new IllegalArgumentException("mtlNames must not be null or empty");

		baseMaterials = mtlNames;
	}


	/**
	 * Sets the materials to use when the block has a specific data value.
	 * 
	 * @param dataValue
	 * @param mtlNames 
	 */
	public void put(BlockData dataValue, String[] mtlNames)
	{
		if (dataValue == null)
			throw new IllegalArgumentException("dataValue must not be null");
		if (mtlNames == null || mtlNames.length == 0)
			throw new IllegalArgumentException("mtlNames must not be null or empty");

		dataMaterials.put(dataValue, mtlNames);
	}

	/**
	 * Sets the materials to use when the block has a biome defined. If dataValue isn't defined
	 * for the block, simply use -1 for its value.
	 * 
	 * @param biomeValue
	 * @param dataValue
	 * @param mtlNames
	 */
	public void put(int biomeValue, BlockData dataValue, String[] mtlNames)
	{
		if (dataValue == null)
			throw new IllegalArgumentException("dataValue must not be null");			
		if (mtlNames == null || mtlNames.length == 0)
			throw new IllegalArgumentException("mtlNames must not be null or empty");

		Map<BlockData, String[]> mtls = null;

		if(biomeMaterials.containsKey(biomeValue))
			mtls=biomeMaterials.get(biomeValue);
		else
		{
			mtls = new LinkedHashMap<BlockData, String[]>();
			biomeMaterials.put(biomeValue, mtls);
		}

		mtls.put(dataValue, mtlNames);
	}


	/**
	 * Looks up the materials to use, given the block's data and biome values.
	 * First it checks if biomes are defined for the particular material and uses
	 * the biome specific data/mask values just like below.
	 * If specific materials for that data value are not defined, returns the 
	 * default materials; if the default materials are not defined returns the
	 * materials for the lowest data value defined.
	 * 
	 * @param data Block data value. If null empty BlockData is used.
	 * @param biomeValue Block biome value.
	 * @return Array of material names, or null.
	 */
	public String[] get(BlockData data, int biomeValue)
	{
		if (data == null)
			data = new BlockData(blockID);
		
		String[] mtlNames = null;
		if(Options.renderBiomes && biomeMaterials.containsKey(biomeValue))
		{
			Map<BlockData, String[]> mtls=biomeMaterials.get(biomeValue);
			mtlNames = mtls.get(data);
			if (mtlNames == null)
				mtlNames = getMasked(mtls, data);
			if (mtlNames == null)
				mtlNames = mtls.get(new BlockData(blockID));
			if (mtlNames == null)
				mtlNames = getFirstMtl(mtls);
		}
		else
		{
			mtlNames = dataMaterials.get(data);
			if (mtlNames == null)
				mtlNames = getMasked(dataMaterials, data);
			if (mtlNames == null)
				mtlNames = baseMaterials;
			if (mtlNames == null)
				mtlNames = getFirstMtl(dataMaterials);

			if (mtlNames == null && !biomeMaterials.isEmpty())
			{
				Map<BlockData, String[]> mtls = biomeMaterials.values().iterator().next();
				mtlNames = mtls.get(data);
				if (mtlNames == null)
					mtlNames = getMasked(mtls, data);
				if (mtlNames == null)
					mtlNames = mtls.get(new BlockData(blockID));
				if (mtlNames == null)
					mtlNames = getFirstMtl(mtls);
			}
		}

		if (mtlNames == null)
			throw new RuntimeException("materials definition is empty!");
		return mtlNames;
	}
	
	private String[] getMasked(Map<BlockData, String[]> mtls, BlockData data) {
		for (Entry<BlockData, String[]> eMtl : mtls.entrySet()) {
			if (data.matchesMask(eMtl.getKey())) {
				return eMtl.getValue();
			}
		}
		return null;
	}


	private String[] getFirstMtl(Map<?, String[]> map) {
		for (String[] mtl : map.values()) {
			if (mtl != null) {
				return mtl;
			}
		}
		
		return null;
	}

}
