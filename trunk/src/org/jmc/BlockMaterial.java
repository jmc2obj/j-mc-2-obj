package org.jmc;


/**
 * Holds the material definitions for a block type.
 */
public class BlockMaterial
{
	private String[] baseMaterials = null;
	
	private String[][] dataMaterials = new String[16][];
	
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
	 * Sets the bit mask to use when looking up materials by data value.
	 */
	public void setDataMask(byte val)
	{
		dataMask = val;
	}

	
	/**
	 * Looks up the materials to use, given the block's data value.
	 * If specific materials for that data value are not defined, returns the 
	 * default materials; if the default materials are not defined returns null.
	 * 
	 * @param dataValue Block data value.
	 * @return Array of material names, or null.
	 */
	public String[] get(byte dataValue)
	{
		String[] mtlNames = dataMaterials[dataValue & dataMask];
		if (mtlNames == null)
			mtlNames = baseMaterials;
		return mtlNames;
	}

}
