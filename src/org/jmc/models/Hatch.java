package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for hatches (aka trapdoors)
 */
public class Hatch extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		boolean open = data.state.get("open").equals("true");
		boolean top = data.state.get("half").equals("top");
		String facing = data.state.get("facing");
		
		/*
		  The model is rendered in the middle of the block, then rotated
		*/
		Transform rotate = Transform.rotation(0, 0, 0);
		Transform translate = Transform.translation(x, y, z);
		Transform scale = new Transform();
		
		if (open) // open
		{
			if (facing.equals("south"))
			{
				rotate = Transform.rotation(-90, 0, 0);
				translate = Transform.translation(x, y, z-0.40625f);
				
				if (top) {
					scale = Transform.scale(1, 1, -1);
				}
			} else if (facing.equals("north")) {
				rotate = Transform.rotation(90, 0, 0);
				translate = Transform.translation(x, y, z+0.40625f);
				
				if (!top) {
					scale = Transform.scale(1, 1, -1);
				}
			} else if (facing.equals("west")){
				rotate = Transform.rotation(-90, 180, 90);
				translate = Transform.translation(x+0.40625f, y, z);
				
				if (!top) {
					scale = Transform.scale(-1, 1, -1);
				}
			} else
			{
				rotate = Transform.rotation(-90, 0, 90);
				translate = Transform.translation(x-0.40625f, y, z);
				
				if (top) {
					scale = Transform.scale(-1, 1, -1);
				}
			}
			

		} else // closed
		{
			if (facing.equals("south"))
			{
				rotate = Transform.rotation(0, 0, 0);
			} else if (facing.equals("north")) {
				rotate = Transform.rotation(0, 180, 0);
			} else if (facing.equals("west")) {
				rotate = Transform.rotation(0, 90, 0);
			} else {
				rotate = Transform.rotation(0, 270, 0);
			}	
			
			if (top) {
				translate = Transform.translation(x, y+0.40625f, z);
			} else {
				translate = Transform.translation(x, y-0.40625f, z);
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
