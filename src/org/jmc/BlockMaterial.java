package org.jmc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jmc.registry.NamespaceID;


/**
 * Holds the material definitions for a block type.
 */
@ParametersAreNonnullByDefault
public class BlockMaterial
{
	private NamespaceID[] baseMaterials = null;

	@Nonnull
	private Map<Blockstate, NamespaceID[]> dataMaterials = new LinkedHashMap<>();

	private Map<Integer, Map<Blockstate, NamespaceID[]>> biomeMaterials = new LinkedHashMap<>();

	public boolean isEmpty() {
		return biomeMaterials.isEmpty() && dataMaterials.isEmpty() && (baseMaterials == null || baseMaterials.length < 1);
	}

	/**
	 * Sets the materials to use by default, i.e., when there isn't a specific material 
	 * for the block's data value.
	 * 
	 * @param mtlNames 
	 */
	public void put(NamespaceID[] mtlNames)
	{
		baseMaterials = mtlNames;
	}


	/**
	 * Sets the materials to use when the block has a specific data value.
	 * @param mtlNames 
	 * @param dataValue
	 */
	public void put(NamespaceID[] mtlNames, Blockstate state)
	{
		if (mtlNames.length == 0)
			throw new IllegalArgumentException("mtlNames must not be empty");

		dataMaterials.put(state, mtlNames);
	}

	/**
	 * Sets the materials to use when the block has a biome defined. If dataValue isn't defined
	 * for the block, simply use -1 for its value.
	 * @param mtlNames
	 * @param state
	 * @param biomeValue
	 */
	public void put(NamespaceID[] mtlNames, Blockstate state, int biomeValue)
	{		
		if (mtlNames.length == 0)
			throw new IllegalArgumentException("mtlNames must not be empty");

		Map<Blockstate, NamespaceID[]> mtls = null;

		if(biomeMaterials.containsKey(biomeValue))
			mtls=biomeMaterials.get(biomeValue);
		else
		{
			mtls = new LinkedHashMap<Blockstate, NamespaceID[]>();
			biomeMaterials.put(biomeValue, mtls);
		}

		mtls.put(state, mtlNames);
	}


	/**
	 * Looks up the materials to use, given the block's data and biome values.
	 * First it checks if biomes are defined for the particular material and uses
	 * the biome specific data/mask values just like below.
	 * If specific materials for that data value are not defined, returns the 
	 * default materials; if the default materials are not defined returns the
	 * materials for the lowest data value defined.
	 * 
	 * @param state Block data value. If null empty Blockstate is used.
	 * @param biomeValue Block biome value.
	 * @return Array of material names.
	 */
	@Nonnull
	public NamespaceID[] get(@CheckForNull Blockstate state, int biomeValue)
	{
		if (state == null)
			state = new Blockstate();
		
		NamespaceID[] mtlNames = null;
		if(Options.renderBiomes && biomeMaterials.containsKey(biomeValue))
		{
			Map<Blockstate, NamespaceID[]> mtls=biomeMaterials.get(biomeValue);
			mtlNames = mtls.get(state);
			if (mtlNames == null)
				mtlNames = getMasked(mtls, state);
			if (mtlNames == null)
				mtlNames = mtls.get(new Blockstate());
			if (mtlNames == null)
				mtlNames = getFirstMtl(mtls);
		}
		else
		{
			mtlNames = dataMaterials.get(state);
			if (mtlNames == null)
				mtlNames = getMasked(dataMaterials, state);
			if (mtlNames == null)
				mtlNames = baseMaterials;
			if (mtlNames == null)
				mtlNames = getFirstMtl(dataMaterials);
			
			if (mtlNames == null && !biomeMaterials.isEmpty())
			{
				Map<Blockstate, NamespaceID[]> mtls = biomeMaterials.values().iterator().next();
				mtlNames = mtls.get(state);
				if (mtlNames == null)
					mtlNames = getMasked(mtls, state);
				if (mtlNames == null)
					mtlNames = mtls.get(new Blockstate());
				if (mtlNames == null)
					mtlNames = getFirstMtl(mtls);
			}
		}
		
		if (mtlNames == null)
			throw new RuntimeException("materials definition is empty!");
		return mtlNames;
	}
	
	private NamespaceID[] getMasked(Map<Blockstate, NamespaceID[]> mtls, Blockstate state) {
		for (Entry<Blockstate, NamespaceID[]> eMtl : mtls.entrySet()) {
			if (state.matchesMask(eMtl.getKey())) {
				return eMtl.getValue();
			}
		}
		return null;
	}


	private NamespaceID[] getFirstMtl(Map<?, NamespaceID[]> map) {
		for (NamespaceID[] mtl : map.values()) {
			if (mtl != null) {
				return mtl;
			}
		}
		
		return null;
	}

}
