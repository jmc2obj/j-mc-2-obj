package org.jmc.models;

import java.util.HashMap;

import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for levers.
 */
public class Lever extends BlockModel
{

	private String[] getMtlSidesBase(HashMap<String, String> data, int biome)
	{
		String[] abbrMtls = materials.get(data,biome);

		String[] mtlSides = new String[6];
		mtlSides[0] = abbrMtls[1];
		mtlSides[1] = abbrMtls[1];
		mtlSides[2] = abbrMtls[1];
		mtlSides[3] = abbrMtls[1];
		mtlSides[4] = abbrMtls[1];
		mtlSides[5] = abbrMtls[1];
		return mtlSides;
	}

	private String[] getMtlSidesLever(HashMap<String, String> data, int biome)
	{
		String[] abbrMtls = materials.get(data,biome);

		String[] mtlSides = new String[6];
		mtlSides[0] = abbrMtls[0];
		mtlSides[1] = abbrMtls[0];
		mtlSides[2] = abbrMtls[0];
		mtlSides[3] = abbrMtls[0];
		mtlSides[4] = abbrMtls[0];
		mtlSides[5] = abbrMtls[0];
		return mtlSides;
	}

	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		boolean on = data.get("powered").equals("true");
		String dir = data.get("facing");
		String face = data.get("face");
		String orientation = face + dir; // Puts it together such as wallnorth, ceilingsouth
		
		/*
		 The model is rendered vertically facing south and then rotated  
		*/
		Transform rotate = new Transform();
		Transform rotate2 = new Transform();
		Transform translate = new Transform();
		Transform rt;

		boolean[] drawSides;
		UV[] uvTop, uvFront, uvSide, uvSide2;
		UV[][] uvSides;
		
		
		// base
		switch (orientation)
		{
			case "walleast": rotate.rotate(0, -90, 0); 	break;	// wall e
			case "wallwest": rotate.rotate(0, 90, 0); break;		// wall w
			case "wallsouth": rotate.rotate(0, 0, 0); break;		// wall s
			case "wallnorth": rotate.rotate(0, 180, 0); break;	// wall n
			case "floornorth": rotate.rotate(-90, 0, 0); break;	// ground n-s
			case "floorsouth": rotate.rotate(-90, 0, 0); break;	// ground n-s
			case "floorwest": rotate.rotate(-90, 0, 90); break;	// ground e-w
			case "flooreast": rotate.rotate(-90, 0, 90); break;	// ground e-w
			case "ceilingnorth": rotate.rotate(90, 0, 0); break;		// ceiling n-s
			case "ceilingsouth": rotate.rotate(90, 0, 0); break;		// ceiling n-s
			case "ceilingwest": rotate.rotate(90, 0, 90); break;	// ceiling e-w
			case "ceilingeast": rotate.rotate(90, 0, 90); break;	// ceiling e-w
		}
		translate.translate(x, y, z);
		rt = translate.multiply(rotate);

		drawSides = new boolean[] {true,false,true,true,true,true};

		uvTop = new UV[] { new UV(5/16f,13/16f), new UV(11/16f,13/16f), new UV(11/16f,1), new UV(5/16f,1) };
		uvFront = new UV[] { new UV(5/16f,4/16f), new UV(11/16f,4/16f), new UV(11/16f,12/16f), new UV(5/16f,12/16f) };
		uvSide = new UV[] { new UV(0,6/16f), new UV(3/16f,6/16f), new UV(3/16f,10/16f), new UV(0,10/16f) };
		uvSides = new UV[][] { uvTop, uvFront, uvFront, uvSide, uvSide, uvTop };
		
		addBox(obj, 
				-0.1875f, -0.25f, -0.5f, 
				0.1875f, 0.25f, -0.3125f, 
				rt, 
				getMtlSidesBase(data,biome),
				uvSides,
				drawSides);

		// lever
		switch (orientation)
		{
			case "walleast":
				rotate2.rotate(0, 0, on ? -35 : 35);
				rotate.rotate(0, -90, 0);
				translate.translate(x, y + (on ? -0.35f : 0.35f), z);
				break;
			case "wallwest":
				rotate2.rotate(0, 0, on ? 35 : -35);
				rotate.rotate(0, 90, 0);
				translate.translate(x, y + (on ? -0.35f : 0.35f), z);
				break;
			case "wallsouth":
				rotate.rotate(on ? 35 : -35, 0, 0);
				translate.translate(x, y + (on ? -0.35f : 0.35f), z);
				break;
			case "wallnorth":
				rotate.rotate(on ? -35 : 35, 180, 0);
				translate.translate(x, y + (on ? -0.35f : 0.35f), z);
				break;
			case "floornorth":
				rotate.rotate(-90 + (on ? -35 : 35), 0, 0);
				translate.translate(x, y, z + (on ? -0.35f : 0.35f));
				break;
			case "floorsouth":
				rotate.rotate(-90 + (on ? 35 : -35), 0, 0);
				translate.translate(x, y, z + (on ? 0.35f : -0.35f));
				break;				
			case "floorwest":
				rotate.rotate(-90, (on ? 35 : -35), 90);
				translate.translate(x + (on ? -0.35f : 0.35f), y, z);
				break;
			case "flooreast":
				rotate.rotate(-90, (on ? -35 : 35), 90);
				translate.translate(x + (on ? 0.35f : -0.35f), y, z);
				break;				
			case "ceilingnorth":
				rotate.rotate(90 + (on ? 35 : -35), 0, 0);
				translate.translate(x, y, z + (on ? -0.35f : 0.35f));
				break;
			case "ceilingsouth":
				rotate.rotate(90 + (on ? -35 : 35), 0, 0);
				translate.translate(x, y, z + (on ? 0.35f : -0.35f));
				break;					
			case "ceilingwest":
				rotate.rotate(90, (on ? 35 : -35), 90);
				translate.translate(x + (on ? -0.35f : 0.35f), y, z);
				break;
			case "ceilingeast":
				rotate.rotate(90, (on ? -35 : 35), 90);
				translate.translate(x + (on ? 0.35f : -0.35f), y, z);
				break;				
		}
		rt = translate.multiply(rotate2.multiply(rotate));

		drawSides = new boolean[] {true,false,true,true,true,true};

		uvTop = new UV[] { new UV(9/16f,10/16f), new UV(7/16f,10/16f), new UV(7/16f,0), new UV(9/16f,0) };
		uvFront = new UV[] { new UV(7/16f,8/16f), new UV(9/16f,8/16f), new UV(9/16f,10/16f), new UV(7/16f,10/16f) };
		uvSide = new UV[] { new UV(9/16f,0), new UV(9/16f,10/16f), new UV(7/16f,10/16f), new UV(7/16f,0) };
		uvSide2 = new UV[] { new UV(7/16f,10/16f), new UV(7/16f,0), new UV(9/16f,0), new UV(9/16f,10/16f) };
		uvSides = new UV[][] { uvTop, uvFront, uvFront, uvSide, uvSide2, uvTop };
		
		addBox(obj, 
				-0.0625f, -0.0625f, -0.5f, 
				0.0625f, 0.0625f, 0.125f,
				rt, 
				getMtlSidesLever(data,biome),
				uvSides,
				drawSides);
	}

}
