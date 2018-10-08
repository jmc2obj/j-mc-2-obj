package org.jmc;

import java.util.HashMap;
import java.util.Map;


/**
 * Holds the material definitions for a block type.
 */
public class BlockMaterial
{
	private String[] baseMaterials = null;

	private HashMap<HashMap<String, String>, String[]> dataMaterials = new HashMap<HashMap<String, String>, String[]>();

	private Map<Integer, HashMap<HashMap<String, String>, String[]>> biomeMaterials = new HashMap<Integer, HashMap<HashMap<String, String>, String[]>>();


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
	public void put(HashMap<String, String> dataValue, String[] mtlNames)
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
	public void put(int biomeValue, HashMap<String, String> dataValue, String[] mtlNames)
	{
		if (dataValue == null)
			throw new IllegalArgumentException("dataValue must not be null");			
		if (mtlNames == null || mtlNames.length == 0)
			throw new IllegalArgumentException("mtlNames must not be null or empty");

		HashMap<HashMap<String, String>, String[]> mtls = null;

		if(biomeMaterials.containsKey(biomeValue))
			mtls=biomeMaterials.get(biomeValue);
		else
		{
			mtls = new HashMap<HashMap<String,String>, String[]>();
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
	 * @param blockData Block data value. If null empty HashMap is used.
	 * @param biomeValue Block biome value.
	 * @return Array of material names, or null.
	 */
	public String[] get(HashMap<String, String> blockData, int biomeValue)
	{
		if (blockData == null)
			blockData = new HashMap<String, String>();
		
		String[] mtlNames = null;
		if(Options.renderBiomes && biomeMaterials.containsKey(biomeValue))
		{
			HashMap<HashMap<String, String>, String[]> mtls=biomeMaterials.get(biomeValue);
			mtlNames = mtls.get(blockData);
			if (mtlNames == null)
				mtlNames = mtls.get(new HashMap<String, String>());
			if (mtlNames == null)
				for (String[] mtl : mtls.values())
					if (mtl != null) {
						mtlNames = mtl;
						break;
					}
		}
		else
		{
			mtlNames = dataMaterials.get(blockData);
			if (mtlNames == null)
				mtlNames = baseMaterials;
			if (mtlNames == null)
				for (String[] mtl : dataMaterials.values())
					if (mtl != null) {
						mtlNames = mtl;
						break;
					}

			if (mtlNames == null && !biomeMaterials.isEmpty())
			{
				Map<HashMap<String, String>, String[]> mtls = biomeMaterials.values().iterator().next();
				mtlNames = mtls.get(blockData);
				if (mtlNames == null)
					mtlNames = mtls.get(new HashMap<String, String>());
				if (mtlNames == null)
					for (String[] mtl : mtls.values())
						if (mtl != null) {
							mtlNames = mtl;
							break;
						}
			}
		}

		if (mtlNames == null)
			throw new RuntimeException("materials definition is empty!");
		return mtlNames;
	}

}
