package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;


/**
 * Model for chests.
 */
public class Chest extends BlockModel
{

	/** Expand the materials to the full 6 side definition used by addBox */
	private String[] getMtlSides(byte data, byte biome, int i)
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

	/**	Checks whether the chest is able to form double-chests */
	private boolean canDouble()
	{
		return this.blockId != 130;
	}
	
	/** Checks whether the chest is part of a double-chest */
	private boolean checkConnect(short otherId)
	{
		return canDouble() && blockId == otherId;
	}


	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome)
	{
		String[] mtlSingle = getMtlSides(data, biome, 0);
		String[] mtlDouble = canDouble() ? getMtlSides(data, biome, 1) : null;
		boolean conn_l = false;
		boolean conn_r = false;
		
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		switch (data)
		{
			case 2:
				rotate.rotate(0, 0, 0);
				conn_l = checkConnect(chunks.getBlockID(x-1, y, z));
				conn_r = checkConnect(chunks.getBlockID(x+1, y, z));
				break;
			case 3:
				rotate.rotate(0, 180, 0);
				conn_l = checkConnect(chunks.getBlockID(x+1, y, z));
				conn_r = checkConnect(chunks.getBlockID(x-1, y, z));
				break;
			case 4:
				rotate.rotate(0, -90, 0);
				conn_l = checkConnect(chunks.getBlockID(x, y, z+1));
				conn_r = checkConnect(chunks.getBlockID(x, y, z-1));
				break;
			case 5:
				rotate.rotate(0, 90, 0);
				conn_l = checkConnect(chunks.getBlockID(x, y, z-1));
				conn_r = checkConnect(chunks.getBlockID(x, y, z+1));
				break;
		}
		translate.translate(x, y, z);		
		rt = translate.multiply(rotate);
		
		
		boolean[] drawSides;
		UV[][] uvSides;
		
		if (conn_l)
		{
			// body
			drawSides = new boolean[] { false, true, true, false, true, drawSides(chunks, x, y, z)[5] };
			uvSides = new UV[][] {
					null,
					{ new UV(14/128f, 21/64f), new UV(29/128f, 21/64f), new UV(29/128f, 31/64f), new UV(14/128f, 31/64f) },
					{ new UV(73/128f, 21/64f), new UV(88/128f, 21/64f), new UV(88/128f, 31/64f), new UV(73/128f, 31/64f) },
					null,
					{ new UV(44/128f, 21/64f), new UV(58/128f, 21/64f), new UV(58/128f, 31/64f), new UV(44/128f, 31/64f) },
					{ new UV(59/128f, 31/64f), new UV(74/128f, 31/64f), new UV(74/128f, 45/64f), new UV(59/128f, 45/64f) },
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
		else if (conn_r)
		{
			// body
			drawSides = new boolean[] { false, true, true, true, false, drawSides(chunks, x, y, z)[5] };
			uvSides = new UV[][] {
					null,
					{ new UV(29/128f, 21/64f), new UV(44/128f, 21/64f), new UV(44/128f, 31/64f), new UV(29/128f, 31/64f) },
					{ new UV(58/128f, 21/64f), new UV(73/128f, 21/64f), new UV(73/128f, 31/64f), new UV(58/128f, 31/64f) },
					{ new UV( 0/128f, 21/64f), new UV(14/128f, 21/64f), new UV(14/128f, 31/64f), new UV( 0/128f, 31/64f) },
					null,
					{ new UV(44/128f, 31/64f), new UV(59/128f, 31/64f), new UV(59/128f, 45/64f), new UV(44/128f, 45/64f) },
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
