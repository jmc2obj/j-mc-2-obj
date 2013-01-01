package org.jmc;

import org.jmc.models.BlockModel;
import org.jmc.models.Cube;
import org.jmc.models.None;


/**
 * A special-purpose implementation of BlockInfo used to represent unknown block types.
 * The behavior of this class varies according to the "renderUnknown" global option.
 */
public class UnknownBlockInfo extends BlockInfo
{
	private BlockModel cubeModel;
	private BlockModel noneModel;
	

	UnknownBlockInfo()
	{
		super(-1, "unknown", null, Occlusion.NONE, null);

		materials = new BlockMaterial();
		materials.put(new String[] { "unknown" });

		cubeModel = new Cube();
		cubeModel.setBlockId((short)-1);
		cubeModel.setMaterials(materials);

		noneModel = new None();
		noneModel.setBlockId((short)-1);
		noneModel.setMaterials(materials);
	}

	
	@Override
	public Occlusion getOcclusion() {
		return Options.renderUnknown ? Occlusion.FULL : Occlusion.NONE; 
	}

	@Override
	public BlockModel getModel() {
		return Options.renderUnknown ? cubeModel : noneModel; 
	}

}
