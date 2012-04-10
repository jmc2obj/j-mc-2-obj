package org.jmc.models;


/**
 * Base class for blocks that are 6-sided boxes.
 */
public abstract class BoxModel extends BlockModel
{
	/**
	 * Expand the materials to the full 6 side definition used by addBox
	 */
	protected String[] getMtlSides(byte data)
	{
		String[] abbrMtls = materials.get(data);
		
		String[] mtlSides = new String[6];
		if (abbrMtls.length < 2)
		{
			mtlSides[0] = abbrMtls[0];
			mtlSides[1] = abbrMtls[0];
			mtlSides[2] = abbrMtls[0];
			mtlSides[3] = abbrMtls[0];
			mtlSides[4] = abbrMtls[0];
			mtlSides[5] = abbrMtls[0];
		}
		else if (abbrMtls.length < 3)
		{
			mtlSides[0] = abbrMtls[0];
			mtlSides[1] = abbrMtls[1];
			mtlSides[2] = abbrMtls[1];
			mtlSides[3] = abbrMtls[1];
			mtlSides[4] = abbrMtls[1];
			mtlSides[5] = abbrMtls[0];
		}
		else if (abbrMtls.length < 6)
		{
			mtlSides[0] = abbrMtls[0];
			mtlSides[1] = abbrMtls[1];
			mtlSides[2] = abbrMtls[1];
			mtlSides[3] = abbrMtls[1];
			mtlSides[4] = abbrMtls[1];
			mtlSides[5] = abbrMtls[2];
		}
		else
		{
			mtlSides[0] = abbrMtls[0];
			mtlSides[1] = abbrMtls[1];
			mtlSides[2] = abbrMtls[2];
			mtlSides[3] = abbrMtls[3];
			mtlSides[4] = abbrMtls[4];
			mtlSides[5] = abbrMtls[5];
		}
		
		return mtlSides;
	}

}
