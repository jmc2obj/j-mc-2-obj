package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for hatches (aka trapdoors)
 */
public class Hatch extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		boolean open = data.get("open").equals("true");
		boolean top = data.get("half").equals("top");
		String facing = data.get("facing");
		
		/*
		  The model is rendered in the middle of the block, then rotated
		*/
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform scale = new Transform();
		
		rotate.rotate(0, 0, 0);
		translate.translate(x, y, z);
		
		if (open) // open
		{
			if (facing.equals("south"))
			{
				rotate.rotate(-90, 0, 0);
				translate.translate(x, y, z-0.40625f);
				
				if (top) {
					scale.scale(1, 1, -1);
				}
			} else if (facing.equals("north")) {
				rotate.rotate(90, 0, 0);
				translate.translate(x, y, z+0.40625f);
				
				if (!top) {
					scale.scale(1, 1, -1);
				}
			} else if (facing.equals("west")){
				rotate.rotate(-90, 180, 90);
				translate.translate(x+0.40625f, y, z);
				
				if (!top) {
					scale.scale(-1, 1, -1);
				}
			} else
			{
				rotate.rotate(-90, 0, 90);
				translate.translate(x-0.40625f, y, z);
				
				if (top) {
					scale.scale(-1, 1, -1);
				}
			}
			

		} else // closed
		{
			if (facing.equals("south"))
			{
				rotate.rotate(0, 0, 0);
			} else if (facing.equals("north")) {
				rotate.rotate(0, 180, 0);
			} else if (facing.equals("west")) {
				rotate.rotate(0, 90, 0);
			} else {
				rotate.rotate(0, 270, 0);
			}	
			
			if (top) {
				translate.translate(x, y+0.40625f, z);
			} else {
				translate.translate(x, y-0.40625f, z);
			}
		}
			
		
		Transform rt = translate.multiply(rotate).multiply(scale);

		
		UV[] uvSide = new UV[] { new UV(1,13/16f), new UV(0,13/16f), new UV(0,1), new UV(1,1) };
		UV[][] uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };

		addBox(obj,
				-0.5f, -0.09375f, -0.5f,
				0.5f, 0.09375f, 0.5f, 
				rt, 
				getMtlSides(data,biome), 
				uvSides, 
				null);

	}

}
