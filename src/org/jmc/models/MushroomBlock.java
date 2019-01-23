package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for wood logs (and, accidentally, also hay blocks).
 */
public class MushroomBlock extends BlockModel
{

	protected String[] getMtlSides(BlockData data, int biome)
	{
		String[] mat = materials.get(data, biome);
		// Mushroom blocks are fully textured at first.
		//                               top     north   south   west    east    south
		String[] sides = new String[] { mat[0], mat[0], mat[0], mat[0], mat[0], mat[0] };

		// If directions are "false," then this side takes the pores texture instead.
		if(data.get("up").equals("false")) {
			sides[0] = mat[1]; }		
		if(data.get("north").equals("false")) {
			sides[1] = mat[1]; }		
		if(data.get("south").equals("false")) {
			sides[2] = mat[1]; }		
		if(data.get("west").equals("false")) {
			sides[3] = mat[1]; }		
		if(data.get("east").equals("false")) {
			sides[4] = mat[1]; }		
		if(data.get("down").equals("false")) {
			sides[5] = mat[1]; }
			
		return sides;
	}
	
	protected UV[][] getUvSides()
	{
		UV[] uv2 = new UV[] { new UV(0,0), new UV(1,0), new UV(1,1), new UV(0,1) };
		return new UV[][] { uv2, uv2, uv2, uv2, uv2, uv2 };
	}


	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		addBox(obj,
				x - 0.5f, y - 0.5f, z - 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f, 
				null, 
				getMtlSides(data, biome), 
				getUvSides(), 
				drawSides(chunks, x, y, z));
	}
}
