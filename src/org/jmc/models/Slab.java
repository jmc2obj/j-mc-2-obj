package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Direction;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for slabs (aka half-blocks).
 */
public class Slab extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		boolean[] drawSides = drawSides(chunks, x, y, z, data);
		UV[] uvSide;
		UV[][] uvSides;
		float ys, ye;
		
		if (data.state.get("type").equals("bottom")) // slab occupies the lower half
		{
			
			drawSides[0] = true;
			uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,0.5f), new UV(0,0.5f) };
			ys = -0.5f;
			ye = 0.0f;
		}
		else if (data.state.get("type").equals("top")) // slab occupies the upper half
		{
			
			drawSides[5] = true;
			uvSide = new UV[] { new UV(0,0.5f), new UV(1,0.5f), new UV(1,1), new UV(0,1) };
			ys = 0.0f;
			ye = 0.5f;
			
			//data = (byte)(data & 0x7);   Why was this code here?
		} 
		else // it's a double slab (full block)
		{
			uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,1), new UV(0,1) };
			ys = -0.5f;
			ye = 0.5f;
		}

		uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
		
		addBox(obj,
				x - 0.5f, y + ys, z - 0.5f,
				x + 0.5f, y + ye, z + 0.5f, 
				null, 
				getMtlSides(data,biome),
				uvSides, 
				drawSides);
	}
	
	@Override
	protected boolean getCustomOcclusion(Direction side, BlockData neighbourData, BlockData data) {
		if (data.state.get("type").equals("bottom"))
		{
			switch (side) {
				case DOWN:
					return true;
				case UP:
					return false;
				case SOUTH:
				case NORTH:
				case WEST:
				case EAST:
					return data.equalData(neighbourData);
				default:
					return false;
			}
		}
		else if (data.state.get("type").equals("top"))
		{
			switch (side) {
			case DOWN:
				return false;
			case UP:
				return true;
			case SOUTH:
			case NORTH:
			case WEST:
			case EAST:
				return data.equalData(neighbourData);
			default:
				return false;
		}
			
		} 
		else
		{
			return true;
		}
	}

}
