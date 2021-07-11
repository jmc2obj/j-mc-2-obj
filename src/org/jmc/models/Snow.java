package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Direction;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for ground snow.
 */
public class Snow extends BlockModel
{
	
	@Override
	protected boolean getCustomOcclusion(Direction side, BlockData neighbourData, BlockData data) {
		int layers = data.state.getInt("layers");
		if (side == Direction.DOWN || layers >= 8) {
			return true;
		}
		
		Integer neighbourLayers = neighbourData.state.getInt("layers");
		if (data.id.equals(neighbourData.id) && neighbourLayers != null && neighbourLayers <= layers) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		boolean[] drawSides = drawSides(chunks, x, y, z, data);
		int layers = data.state.getInt("layers", 0);
		
		if (layers < 8) 
			drawSides[0] = true;
		
		if (layers > 8)
			layers = 8;
		float height = (layers) / 8.0f;

		UV[] uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,height), new UV(0,height) };
		UV[][] uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
		
		addBox(obj,
				x-0.5f, y-0.5f, z-0.5f,
				x+0.5f, y-0.5f+height, z+0.5f, 
				null, 
				getMtlSides(data,biome), 
				uvSides, 
				drawSides);
	}

}
