package org.jmc;

import org.jmc.models.BlockModel;
import org.jmc.models.Cube;
import org.jmc.models.None;
import org.jmc.registry.NamespaceID;
import org.jmc.registry.Registries;


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
		super(NamespaceID.NULL, "unknown", null, Occlusion.NONE, null, false, null);

		materials = new BlockMaterial();
		materials.put(new NamespaceID[] { Registries.UNKNOWN_TEX_ID });

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
