package org.jmc;

import org.jmc.models.BlockModel;
import org.jmc.models.None;


/**
 * A special-purpose implementation of BlockInfo used to represent unknown block types.
 * The behavior of this class varies according to the "renderUnknown" global option.
 */
public class NullBlockInfo extends BlockInfo
{
	private BlockModel noneModel;
	

	NullBlockInfo()
	{
		super("", "unknown", null, Occlusion.NONE, null, false);

		noneModel = new None();
	}

	
	@Override
	public Occlusion getOcclusion() {
		return Occlusion.NONE; 
	}

	@Override
	public BlockModel getModel() {
		return noneModel; 
	}

}
