package org.jmc.registry;

import java.util.HashMap;

import org.jmc.BlockMaterial;
import org.jmc.Blockstate;
import org.jmc.registry.BlockstateEntry.ModelInfo;
import org.jmc.registry.BlockstateEntry.ModelListWeighted;
import org.jmc.registry.ModelEntry.RegistryModel;

import javax.annotation.Nonnull;

public class RegistryBlockMaterial extends BlockMaterial {
	private NamespaceID id;
	
	public RegistryBlockMaterial(NamespaceID id) {
		super();
		this.id = id;
	}
	
	@Nonnull
	@Override
	public NamespaceID[] get(Blockstate state, NamespaceID biomeValue) {
		if (isEmpty()) {
			NamespaceID[] mats = new NamespaceID[1];
			HashMap<String, Integer> textureCount = new HashMap<>();
			int highestUses = 0;
			BlockstateEntry bse = Registries.getBlockstate(id);
			if (bse != null) {
				for (ModelListWeighted modelList : bse.getModelsFor(state)) {
					for (ModelInfo modelInfo : modelList.models) {
						ModelEntry modelEntry = Registries.getModel(modelInfo.id);
						if (modelEntry == null) {
							continue;
						}
						RegistryModel model = modelEntry.generateModel();
						for (String texture : model.textures.values()) {
							if (texture == null) continue;
							textureCount.put(texture, textureCount.getOrDefault(texture, 0) + 1);
							if (textureCount.get(texture) > highestUses) {
								mats[0] = NamespaceID.fromString(texture);
								highestUses++;
							}
						}
						
					}
				}
			}
			if (mats[0] == null)
				mats[0] = Registries.UNKNOWN_TEX_ID;
			return mats;
		} else {
			return super.get(state, biomeValue);
		}
	}
	
}
