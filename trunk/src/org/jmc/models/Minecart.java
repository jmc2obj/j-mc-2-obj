package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.entities.EntityModel;
import org.jmc.geom.Transform;

public class Minecart extends BlockModel implements EntityModel
{

	@Override
	public void addEntity(OBJOutputFile obj, Transform transform)
	{
		
		String[] mtlSides = getMtlSides((byte) 0);
		boolean[] drawSides = new boolean[] {true,true,true,true,true,true};
		
		addBox(obj,-0.25f,-0.5f,-0.5f,0.25f,0.5f,0.5f, transform, mtlSides, null, drawSides);		
	}

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
	}
	

}
