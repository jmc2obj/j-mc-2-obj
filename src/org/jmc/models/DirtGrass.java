package org.jmc.models;

import java.util.ArrayList;
import java.util.HashMap;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.FaceUtils.Face;


/**
 * Model for grass blocks that change when covered by snow.
 */
public class DirtGrass extends Cube
{
	private boolean currSnow = false;
	@Override
	protected String[] getMtlSides(byte data, byte biome)
	{
		return getMtlSides(data, biome, currSnow);
	}
	
	protected String[] getMtlSides(byte data, byte biome, boolean snow)
	{
		String[] abbrMtls = materials.get(data,biome);
		
		String[] mtlSides = new String[6];
		mtlSides[0] = abbrMtls[0];
		mtlSides[1] = abbrMtls[snow ? 2 : 1];
		mtlSides[2] = abbrMtls[snow ? 2 : 1];
		mtlSides[3] = abbrMtls[snow ? 2 : 1];
		mtlSides[4] = abbrMtls[snow ? 2 : 1];
		mtlSides[5] = abbrMtls[3];
		
		return mtlSides;
	}
	

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome)
	{
		currSnow = chunks.getBlockID(x, y+1, z) == 78;
		
		addBox(obj,
				x - 0.5f, y - 0.5f, z - 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f, 
				null, 
				getMtlSides(data, biome, currSnow), 
				null, 
				drawSides(chunks, x, y, z));
	}
	
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome, HashMap<String, ArrayList<Face>> faceAxisArray){
		currSnow = chunks.getBlockID(x, y+1, z) == 78;
		super.addModel(obj, chunks, x, y, z, data, biome, faceAxisArray);
	}
}
