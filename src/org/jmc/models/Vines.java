package org.jmc.models;

import org.jmc.BlockInfo;
import org.jmc.BlockTypes;
import org.jmc.ChunkDataBuffer;
import org.jmc.OBJFile;
import org.jmc.OBJFile.Side;
import org.jmc.Transform;
import org.jmc.Vertex;


/**
 * Model for vines.
 */
public class Vines extends BlockModel
{

	@Override
	public void addModel(OBJFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		short topId = chunks.getBlockID(x, y+1, z);
		
		boolean top = topId != 0 && BlockTypes.get(topId).occlusion == BlockInfo.Occlusion.FULL;
		boolean s = (data & 1) != 0;
		boolean w = (data & 2) != 0;
		boolean n = (data & 4) != 0;
		boolean e = (data & 8) != 0;

		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex( 0.5f, -0.5f, -0.49f);
		vertices[1] = new Vertex( 0.5f,  0.5f, -0.49f);			
		vertices[2] = new Vertex(-0.5f,  0.5f, -0.49f);
		vertices[3] = new Vertex(-0.5f, -0.5f, -0.49f);

		Transform rot = new Transform();
		Transform trans = new Transform();
		
		if (n)
		{
			trans.translate(x, y, z);		
			obj.addFace(vertices, trans, Side.FRONT, materials.get(data)[0]);
		}
		if (s)
		{
			rot.rotate(0, 180, 0);
			trans.translate(x, y, z);		
			obj.addFace(vertices, trans.multiply(rot), Side.FRONT, materials.get(data)[0]);
		}
		if (e)
		{
			rot.rotate(0, 90, 0);
			trans.translate(x, y, z);		
			obj.addFace(vertices, trans.multiply(rot), Side.FRONT, materials.get(data)[0]);
		}
		if (w)
		{
			rot.rotate(0, -90, 0);
			trans.translate(x, y, z);		
			obj.addFace(vertices, trans.multiply(rot), Side.FRONT, materials.get(data)[0]);
		}
		if (top)
		{
			rot.rotate(90, 0, 0);
			trans.translate(x, y, z);		
			obj.addFace(vertices, trans.multiply(rot), Side.FRONT, materials.get(data)[0]);
		}
			
		
	}

}
