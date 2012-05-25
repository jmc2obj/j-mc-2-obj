package org.jmc.entities.models;

import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;

public class Minecart extends EntityModel
{

	@Override
	public void addEntity(OBJOutputFile obj, Transform transform)
	{
		
		String[] mtlSides = getMtlSides((byte) 0);
		boolean[] drawSides = new boolean[] {true,true,true,true,true,true};
		
		addBox(obj,-0.25f,-0.5f,-0.5f,0.25f,0.5f,0.5f, transform, mtlSides, null, drawSides);		
	}

}
