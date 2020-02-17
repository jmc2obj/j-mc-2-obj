package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for chests.
 */
public class Chest extends BlockModel
{

	/** Expand the materials to the full 6 side definition used by addBox */
	private String[] getMtlSides(BlockData data, int biome, int i)
	{
		String[] abbrMtls = materials.get(data,biome);

		String[] mtlSides = new String[6];
		mtlSides[0] = abbrMtls[i];
		mtlSides[1] = abbrMtls[i];
		mtlSides[2] = abbrMtls[i];
		mtlSides[3] = abbrMtls[i];
		mtlSides[4] = abbrMtls[i];
		mtlSides[5] = abbrMtls[i];
		return mtlSides;
	}


	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		String[] mtlSingle = getMtlSides(data, biome, 0);
		String[] mtlDouble = null;
		if (data.containsKey("type")) // If able to be double chest 
		{ 
				mtlDouble = !data.get("type").equals("single") ? getMtlSides(data, biome, 1) : null; 
		}
				
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		String dir = data.get("facing");
		
		switch (dir)
		{
			case "north":
				rotate.rotate(0, 0, 0);
				break;
			case "south":
				rotate.rotate(0, 180, 0);
				break;
			case "east":
				rotate.rotate(0, 90, 0);
				break;
			case "west":
				rotate.rotate(0, -90, 0);
				break;
		}
		translate.translate(x, y, z);		
		rt = translate.multiply(rotate);
		
		
		boolean[] drawSides;
		UV[][] uvSides;
		
		if (data.containsKey("type") && data.get("type").equals("right"))
		{
			// body
			drawSides = new boolean[] { false, true, true, false, true, drawSides(chunks, x, y, z)[5] };
			uvSides = new UV[][] {
					null,
					{ new UV(14/128f, 21/64f), new UV(29/128f, 21/64f), new UV(29/128f, 31/64f), new UV(14/128f, 31/64f) },
					{ new UV(73/128f, 21/64f), new UV(88/128f, 21/64f), new UV(88/128f, 31/64f), new UV(73/128f, 31/64f) },
					null,
					{ new UV(44/128f, 21/64f), new UV(58/128f, 21/64f), new UV(58/128f, 31/64f), new UV(44/128f, 31/64f) },
					{ new UV(74/128f, 45/64f), new UV(59/128f, 45/64f), new UV(59/128f, 31/64f), new UV(74/128f, 31/64f) },
				};
			addBox(obj, -0.5f,-0.5f,-0.4375f, 0.4375f,0.125f,0.4375f, rt, mtlDouble, uvSides, drawSides);
			
			// lid
			drawSides = new boolean[] { true, true, true, false, true, false };
			uvSides = new UV[][] {
					{ new UV(29/128f, 50/64f), new UV(44/128f, 50/64f), new UV(44/128f, 64/64f), new UV(29/128f, 64/64f) },
					{ new UV(14/128f, 46/64f), new UV(29/128f, 46/64f), new UV(29/128f, 50/64f), new UV(14/128f, 50/64f) },
					{ new UV(73/128f, 46/64f), new UV(88/128f, 46/64f), new UV(88/128f, 50/64f), new UV(73/128f, 50/64f) },
					null,
					{ new UV(44/128f, 46/64f), new UV(58/128f, 46/64f), new UV(58/128f, 50/64f), new UV(44/128f, 50/64f) },
					null,
				};
			addBox(obj, -0.5f,0.125f,-0.4375f, 0.4375f,0.375f,0.4375f, rt, mtlDouble, uvSides, drawSides);

			// latch
			drawSides = new boolean[] { true, true, false, true, true, true };
			uvSides = new UV[][] {
					{ new UV(1/128f, 63/64f), new UV(3/128f, 63/64f), new UV(3/128f, 64/64f), new UV(1/128f, 64/64f) },
					{ new UV(1/128f, 59/64f), new UV(3/128f, 59/64f), new UV(3/128f, 63/64f), new UV(1/128f, 63/64f) },
					null,
					{ new UV(0/128f, 59/64f), new UV(1/128f, 59/64f), new UV(1/128f, 63/64f), new UV(0/128f, 63/64f) },
					{ new UV(5/128f, 59/64f), new UV(6/128f, 59/64f), new UV(6/128f, 63/64f), new UV(5/128f, 63/64f) },
					{ new UV(3/128f, 63/64f), new UV(5/128f, 63/64f), new UV(5/128f, 64/64f), new UV(3/128f, 64/64f) },
				};
			addBox(obj, -0.5625f,-0.0625f,-0.5f, -0.4375f,0.1875f,-0.4375f, rt, mtlDouble, uvSides, drawSides);
		}
		else if (data.containsKey("type") && data.get("type").equals("left"))
		{
			// body
			drawSides = new boolean[] { false, true, true, true, false, drawSides(chunks, x, y, z)[5] };
			uvSides = new UV[][] {
					null,
					{ new UV(29/128f, 21/64f), new UV(44/128f, 21/64f), new UV(44/128f, 31/64f), new UV(29/128f, 31/64f) },
					{ new UV(58/128f, 21/64f), new UV(73/128f, 21/64f), new UV(73/128f, 31/64f), new UV(58/128f, 31/64f) },
					{ new UV( 0/128f, 21/64f), new UV(14/128f, 21/64f), new UV(14/128f, 31/64f), new UV( 0/128f, 31/64f) },
					null,
					{ new UV(59/128f, 45/64f), new UV(44/128f, 45/64f), new UV(44/128f, 31/64f), new UV(59/128f, 31/64f) },
				};
			addBox(obj, -0.4375f,-0.5f,-0.4375f, 0.5f,0.125f,0.4375f, rt, mtlDouble, uvSides, drawSides);
			
			// lid
			drawSides = new boolean[] { true, true, true, true, false, false };
			uvSides = new UV[][] {
					{ new UV(14/128f, 50/64f), new UV(29/128f, 50/64f), new UV(29/128f, 64/64f), new UV(14/128f, 64/64f) },
					{ new UV(29/128f, 46/64f), new UV(44/128f, 46/64f), new UV(44/128f, 50/64f), new UV(29/128f, 50/64f) },
					{ new UV(58/128f, 46/64f), new UV(73/128f, 46/64f), new UV(73/128f, 50/64f), new UV(58/128f, 50/64f) },
					{ new UV( 0/128f, 46/64f), new UV(14/128f, 46/64f), new UV(14/128f, 50/64f), new UV( 0/128f, 50/64f) },
					null,
					null,
				};
			addBox(obj, -0.4375f,0.125f,-0.4375f, 0.5f,0.375f,0.4375f, rt, mtlDouble, uvSides, drawSides);

			// (latch was drawn by the other side)
		}
		else
		{
			// body
			drawSides = new boolean[] { false, true, true, true, true, drawSides(chunks, x, y, z)[5] };
			uvSides = new UV[][] {
					null,
					{ new UV(14/64f, 21/64f), new UV(28/64f, 21/64f), new UV(28/64f, 31/64f), new UV(14/64f, 31/64f) },
					{ new UV(42/64f, 21/64f), new UV(56/64f, 21/64f), new UV(56/64f, 31/64f), new UV(42/64f, 31/64f) },
					{ new UV( 0/64f, 21/64f), new UV(14/64f, 21/64f), new UV(14/64f, 31/64f), new UV( 0/64f, 31/64f) },
					{ new UV(28/64f, 21/64f), new UV(42/64f, 21/64f), new UV(42/64f, 31/64f), new UV(28/64f, 31/64f) },
					{ new UV(28/64f, 31/64f), new UV(42/64f, 31/64f), new UV(42/64f, 45/64f), new UV(28/64f, 45/64f) },
				};
			addBox(obj, -0.4375f,-0.5f,-0.4375f, 0.4375f,0.125f,0.4375f, rt, mtlSingle, uvSides, drawSides);
			
			// lid
			drawSides = new boolean[] { true, true, true, true, true, false };
			uvSides = new UV[][] {
					{ new UV(14/64f, 50/64f), new UV(28/64f, 50/64f), new UV(28/64f, 64/64f), new UV(14/64f, 64/64f) },
					{ new UV(14/64f, 46/64f), new UV(28/64f, 46/64f), new UV(28/64f, 50/64f), new UV(14/64f, 50/64f) },
					{ new UV(42/64f, 46/64f), new UV(56/64f, 46/64f), new UV(56/64f, 50/64f), new UV(42/64f, 50/64f) },
					{ new UV( 0/64f, 46/64f), new UV(14/64f, 46/64f), new UV(14/64f, 50/64f), new UV( 0/64f, 50/64f) },
					{ new UV(28/64f, 46/64f), new UV(42/64f, 46/64f), new UV(42/64f, 50/64f), new UV(28/64f, 50/64f) },
					null,
				};
			addBox(obj, -0.4375f,0.125f,-0.4375f, 0.4375f,0.375f,0.4375f, rt, mtlSingle, uvSides, drawSides);
			
			// latch
			drawSides = new boolean[] { true, true, false, true, true, true };
			uvSides = new UV[][] {
					{ new UV(1/64f, 63/64f), new UV(3/64f, 63/64f), new UV(3/64f, 64/64f), new UV(1/64f, 64/64f) },
					{ new UV(1/64f, 59/64f), new UV(3/64f, 59/64f), new UV(3/64f, 63/64f), new UV(1/64f, 63/64f) },
					null,
					{ new UV(0/64f, 59/64f), new UV(1/64f, 59/64f), new UV(1/64f, 63/64f), new UV(0/64f, 63/64f) },
					{ new UV(5/64f, 59/64f), new UV(6/64f, 59/64f), new UV(6/64f, 63/64f), new UV(5/64f, 63/64f) },
					{ new UV(3/64f, 63/64f), new UV(5/64f, 63/64f), new UV(5/64f, 64/64f), new UV(3/64f, 64/64f) },
				};
			addBox(obj, -0.0625f,-0.0625f,-0.5f, 0.0625f,0.1875f,-0.4375f, rt, mtlSingle, uvSides, drawSides);
		}
	}

}
