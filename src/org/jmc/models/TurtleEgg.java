package org.jmc.models;

import java.util.Random;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ObjChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for Buttons.
 */
public class TurtleEgg extends BlockModel
{

	@Override
	public void addModel(ObjChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		// The amount of eggs (1-4)
		int eggs = Integer.parseInt(data.state.get("eggs"));
		
		NamespaceID[] mtlSides = getMtlSides(data, biome);
		
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

        // Generates a random number between 0 and 3 inclusively. 
        Random r = new Random();
        int randomRotation = r.nextInt(4);
		
        // Creates a rotation such that we will use to randomly rotate the egg(s) with. 
		switch (randomRotation)
		{
			case 0:
				rotate = Transform.rotation(0, 0, 0);
				break;
			case 1:
				rotate = Transform.rotation(0, 90, 0);
				break;
			case 2:
				rotate = Transform.rotation(0, 180, 0);
				break;
			case 3:
				rotate = Transform.rotation(0, 270, 0);
				break;
		}
		
		
		translate = Transform.translation(x, y, z);
		rt = translate.multiply(rotate);
		
		boolean[] drawSides = new boolean[] {true,true,true,true,true,false};
		
		// Adds the first egg
		UV[] uvTop = new UV[] { new UV(0,12/16f), new UV(0,1), new UV(4/16f,1), new UV(4/16f,12/16f) };
		UV[] uvSide = new UV[] { new UV(5/16f,5/16f), new UV(1/16f,5/16f), new UV(1/16f,12/16f), new UV(5/16f,12/16f) };
		UV[][] uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvSide };
		addBox(obj, -4/16f,-8/16f, -3/16f, 1/16f, -1/16f, 2/16f, rt, mtlSides, uvSides, drawSides);
		
		// Adds the second egg, if applicable
		if (eggs > 1) 
		{
			uvTop = new UV[] { new UV(6/16f,5/16f), new UV(6/16f,9/16f), new UV(10/16f,9/16f), new UV(10/16f,5/16f) };
			uvSide = new UV[] { new UV(14/16f,1/16f), new UV(10/16f,1/16f), new UV(10/16f,6/16f), new UV(14/16f,6/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvSide };
			addBox(obj, -1/16f,-8/16f, -7/16f, 3/16f, -3/16f, -3/16f, rt, mtlSides, uvSides, drawSides);
		}
		
		// Adds the third egg, if applicable
		if (eggs > 2) 
		{
			uvTop = new UV[] { new UV(8/16f,13/16f), new UV(5/16f,13/16f), new UV(5/16f,1), new UV(8/16f,1) };
			uvSide = new UV[] { new UV(11/16f,9/16f), new UV(8/16f,9/16f), new UV(8/16f,13/16f), new UV(11/16f,13/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvSide };
			addBox(obj, -1/16f,-8/16f, 3/16f, 2/16f, -4/16f, 6/16f, rt, mtlSides, uvSides, drawSides);
		}
		
		// Adds the fourth egg, if applicable
		if (eggs > 3) 
		{
			uvTop = new UV[] { new UV(8/16f,1/16f), new UV(4/16f,1/16f), new UV(4/16f,5/16f), new UV(8/16f,5/16f) };
			uvSide = new UV[] { new UV(4/16f,1/16f), new UV(0,1/16f), new UV(0,5/16f), new UV(4/16f,5/16f) };
			uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvSide };
			addBox(obj, 2/16f,-8/16f, -1/16f, 5/16f, -5/16f, 2/16f, rt, mtlSides, uvSides, drawSides);
		}
	}

}
