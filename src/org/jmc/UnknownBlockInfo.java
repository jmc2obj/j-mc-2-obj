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
		super("", "unknown", null, Occlusion.NONE, null, false);

		materials = new BlockMaterial();
		materials.put(new String[] { "unknown" });

		cubeModel = new Cube();
		cubeModel.setMaterials(materials);

		noneModel = new None();
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
