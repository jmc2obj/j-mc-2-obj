package org.jmc;

import java.util.HashMap;
import java.util.Map;


/**
 * Holds the material definitions for a block type.
 */
public class BlockMaterial
{
	private String[] baseMaterials = null;

	private String[][] dataMaterials = new String[16][];

	private Map<Byte,String[][]> biomeMaterials = new HashMap<Byte, String[][]>();

	private byte dataMask = (byte)0x0F;


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
	public void put(byte dataValue, String[] mtlNames)
	{
		if (dataValue < 0 || dataValue > 15)
			throw new IllegalArgumentException("dataValue must be between 0 and 15");
		if (mtlNames == null || mtlNames.length == 0)
			throw new IllegalArgumentException("mtlNames must not be null or empty");

		dataMaterials[dataValue] = mtlNames;
	}

	/**
	 * Sets the materials to use when the block has a biome defined. If dataValue isn't defined
	 * for the block, simply use -1 for its value.
	 * 
	 * @param biomeValue
	 * @param dataValue
	 * @param mtlNames
	 */
	public void put(byte biomeValue, byte dataValue, String[] mtlNames)
	{
		if (dataValue < -1 || dataValue > 15)
			throw new IllegalArgumentException("dataValue must be between -1 and 15");				
		if (mtlNames == null || mtlNames.length == 0)
			throw new IllegalArgumentException("mtlNames must not be null or empty");

		String[][] mtls = null;

		if(biomeMaterials.containsKey(biomeValue))
			mtls=biomeMaterials.get(biomeValue);
		else
		{
			mtls = new String[17][];
			biomeMaterials.put(biomeValue, mtls);
		}

		if(dataValue>=0)
			mtls[dataValue] = mtlNames;
		else
			mtls[16] = mtlNames;
	}


	/**
	 * Sets the bit mask to use when looking up materials by data value.
	 */
	public void setDataMask(byte val)
	{
		dataMask = val;
	}


	/**
	 * Looks up the materials to use, given the block's data and biome values.
	 * First it checks if biomes are defined for the particular material and uses
	 * the biome specific data/mask values just like below.
	 * If specific materials for that data value are not defined, returns the 
	 * default materials; if the default materials are not defined returns the
	 * materials for the lowest data value defined.
	 * 
	 * @param dataValue Block data value.
	 * @param biomeValue Block biome value.
	 * @return Array of material names, or null.
	 */
	public String[] get(byte dataValue, byte biomeValue)
	{		
		String[] mtlNames = null;
		if(Options.renderBiomes && biomeMaterials.containsKey(biomeValue))
		{
			String[][] mtls=biomeMaterials.get(biomeValue);
			mtlNames = mtls[dataValue & dataMask];
			if (mtlNames == null)
				mtlNames = mtls[16];
			if (mtlNames == null)
				for (int i = 0; i < mtls.length; i++)
					if (mtls[i] != null) {
						mtlNames = mtls[i];
						break;
					}
		}
		else
		{
			mtlNames = dataMaterials[dataValue & dataMask];
			if (mtlNames == null)
				mtlNames = baseMaterials;
			if (mtlNames == null)
				for (int i = 0; i < dataMaterials.length; i++)
					if (dataMaterials[i] != null) {
						mtlNames = dataMaterials[i];
						break;
					}

			if (mtlNames == null && !biomeMaterials.isEmpty())
			{
				String[][] mtls = biomeMaterials.values().iterator().next();
				mtlNames = mtls[dataValue & dataMask];
				if (mtlNames == null)
					mtlNames = mtls[16];
				if (mtlNames == null)
					for (int i = 0; i < mtls.length; i++)
						if (mtls[i] != null) {
							mtlNames = mtls[i];
							break;
						}
			}
		}

		if (mtlNames == null)
			throw new RuntimeException("materials definition is empty!");
		return mtlNames;
	}

}
