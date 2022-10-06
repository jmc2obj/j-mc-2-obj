package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ObjChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for wall signs.
 */
public class SignBoard extends BlockModel
{

	@Override
	public void addModel(ObjChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		NamespaceID[] mtlSides = getMtlSides(data,biome);
		boolean[] drawSides = new boolean[] { true, true, false, true, true, true };
		
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		switch (data.state.get("facing"))
		{
			case "north":
				rotate = Transform.rotation(0, 0, 0);
				break;
			case "west":
			  	rotate = Transform.rotation(0, -90, 0);
				break;
			case "south":
				rotate = Transform.rotation(0, 180, 0);
				break;
			case "east":
				rotate = Transform.rotation(0, 90, 0);
				break;		  
		}
		translate = Transform.translation(x, y, z);		
			
		rt = translate.multiply(rotate);
		
		
		UV[][] uvSides = new UV[][] {
				new UV[] { new UV(26/64f, 32/32f), new UV(2/64f, 32/32f), new UV(2/64f, 30/32f), new UV(26/64f, 30/32f) },
				new UV[] { new UV(2/64f, 18/32f), new UV(26/64f, 18/32f), new UV(26/64f, 30/32f), new UV(2/64f, 30/32f) },
				new UV[] { new UV(28/64f, 18/32f), new UV(52/64f, 18/32f), new UV(52/64f, 30/32f), new UV(28/64f, 30/32f) },
				new UV[] { new UV(26/64f, 18/32f), new UV(28/64f, 18/32f), new UV(28/64f, 30/32f), new UV(26/64f, 30/32f) },
				new UV[] { new UV(0, 18/32f), new UV(2/64f, 18/32f), new UV(2/64f, 30/32f), new UV(0, 30/32f) },
				new UV[] { new UV(26/64f, 30/32f), new UV(50/64f, 30/32f), new UV(50/64f, 32/32f), new UV(26/64f, 32/32f) },
			};
		
		addBox(obj, -0.5f,-0.25f,0.417f, 0.5f,0.25f,0.5f, rt, mtlSides, uvSides, drawSides);
	}

}
