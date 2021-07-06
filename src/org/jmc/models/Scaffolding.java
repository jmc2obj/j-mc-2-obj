package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;

public class Scaffolding extends BlockModel
{
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		Transform move = Transform.translation(x, y, z);
		
		String[] mtls = getMtlSides(data, biome);
		String[] mtls_Top = new String [] { mtls[0], mtls[1], mtls[1], mtls[1], mtls[1], mtls[5] };
		String[] mtls_Poles = new String [] { mtls[1], mtls[1], mtls[1], mtls[1], mtls[1], mtls[5] };
		
		UV[] uvBottom, uvSide, uvSide2;
		UV[][] uvSides;
		boolean[] drawSides;
		
		// Drawing the top portion. It draws regardless
		uvSide = new UV[] { new UV(0, 14/16f), new UV(1, 14/16f), new UV(1, 16/16f), new UV(0, 16/16f) };
		uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
		addBox(obj, -8/16f, 6/16f, -8/16f, 8/16f, 8/16f, 8/16f, move, mtls_Top, uvSides, null);
		
		// Now we're just drawing some extra faces to fill an inner gap
		drawSides = new boolean[] {false,true,true,true,true,false};
		uvSide = new UV[] { new UV(14/16f, 12/16f), new UV(2/16f, 12/16f), new UV(2/16f, 14/16f), new UV(14/16f, 14/16f) };
		uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
		addBox(obj, -6/16f, 6/16f, -6/16f, 6/16f, 8/16f, 6/16f, move, mtls_Top, uvSides, drawSides);
		
		// If there isn't a bottom (it is standing on the ground, not floating)
		// We'll draw 4 full posts
		if (!data.state.getBool("bottom", true))
		{
			// Don't need to draw the top of the poles
			drawSides = new boolean[] {false,true,true,true,true,true};
			
			uvSide = new UV[] { new UV(0, 0), new UV(2/16f, 0), new UV(2/16f, 14/16f), new UV(0, 14/16f) };
			uvSide2 = new UV[] { new UV(14/16f, 0), new UV(16/16f, 0), new UV(16/16f, 14/16f), new UV(14/16f, 14/16f) };
			uvBottom= new UV[] { new UV(0, 0), new UV(2/16f, 0), new UV(2/16f, 2/16f), new UV(0, 2/16f) };
			uvSides = new UV[][] { null, uvSide, uvSide, uvSide2, uvSide2, uvBottom };
			
			addBox(obj, -8/16f, -8/16f, 6/16f, -6/16f, 6/16f, 8/16f, move, mtls_Poles, uvSides, drawSides);
			addBox(obj, 6/16f, -8/16f, -8/16f, 8/16f, 6/16f, -6/16f, move, mtls_Poles, uvSides, drawSides);
			addBox(obj, -8/16f, -8/16f, -6/16f, -6/16f, 6/16f, -8/16f, move, mtls_Poles, uvSides, drawSides);
			addBox(obj, 6/16f, -8/16f, 8/16f, 8/16f, 6/16f, 6/16f, move, mtls_Poles, uvSides, drawSides);
		}
		// Otherwise it has a bottom (it is floating)
		// We'll draw 4 posts (slightly shorter) and the bottom
		else
		{
			// Don't need to draw the or bottom top of the poles
			drawSides = new boolean[] {false,true,true,true,true,false};
			
			uvSide = new UV[] { new UV(0, 2/16f), new UV(2/16f, 2/16f), new UV(2/16f, 14/16f), new UV(0, 14/16f) };
			uvSide2 = new UV[] { new UV(14/16f, 2/16f), new UV(16/16f, 2/16f), new UV(16/16f, 14/16f), new UV(14/16f, 14/16f) };
			uvBottom= new UV[] { new UV(0, 0), new UV(2/16f, 0), new UV(2/16f, 2/16f), new UV(0, 2/16f) };
			uvSides = new UV[][] { null, uvSide, uvSide, uvSide2, uvSide2, uvBottom };
			
			addBox(obj, -8/16f, -6/16f, 6/16f, -6/16f, 6/16f, 8/16f, move, mtls_Poles, uvSides, drawSides);
			addBox(obj, 6/16f, -6/16f, -8/16f, 8/16f, 6/16f, -6/16f, move, mtls_Poles, uvSides, drawSides);
			addBox(obj, -8/16f, -6/16f, -6/16f, -6/16f, 6/16f, -8/16f, move, mtls_Poles, uvSides, drawSides);
			addBox(obj, 6/16f, -6/16f, 8/16f, 8/16f, 6/16f, 6/16f, move, mtls_Poles, uvSides, drawSides);
			
			// And now the bottom 
			uvSide = new UV[] { new UV(0, 14/16f), new UV(1, 14/16f), new UV(1, 16/16f), new UV(0, 16/16f) };
			uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
			addBox(obj, -8/16f, -8/16f, -8/16f, 8/16f, -6/16f, 8/16f, move, mtls_Top, uvSides, null);
			
			// Now we're just drawing some extra faces to fill an inner gap
			drawSides = new boolean[] {false,true,true,true,true,false};
			uvSide = new UV[] { new UV(14/16f, 12/16f), new UV(2/16f, 12/16f), new UV(2/16f, 14/16f), new UV(14/16f, 14/16f) };
			uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
			addBox(obj, -6/16f, -8/16f, -6/16f, 6/16f, -6/16f, 6/16f, move, mtls_Top, uvSides, drawSides);
		}
	}
}