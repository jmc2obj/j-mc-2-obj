package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Side;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;


/**
 * Model for minecart rails.
 * 
 * TODO: fix normal in inclined rail
 */
public class Rails extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		String mtl;
		if (blockId == 27) {
			// powered rail (off / on)
			mtl = (data & 8) == 0 ? materials.get(data)[1] : materials.get(data)[0];
			data = (byte)(data & 7);
		}
		else if (blockId == 28)
		{
			// detector
			mtl = materials.get(data)[0];
		}
		else
		{
			// regular rail (straight / curved)
			mtl = data < 6 ? materials.get(data)[0] : materials.get(data)[1];
		}
		
		
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		switch (data)
		{
			case 1:
			case 2:
			case 7:
				rotate.rotate(0, 90, 0);
				break;
			case 3:
			case 9:
				rotate.rotate(0, -90, 0);
				break;
			case 5:
			case 8:
				rotate.rotate(0, 180, 0);
				break;
		}
		translate.translate(x, y, z);		
			
		rt = translate.multiply(rotate);
		
		
		Vertex[] vertices = new Vertex[4];
		if (data < 2 || data > 5)
		{
			// flat
			vertices[0] = new Vertex( 0.5f, -0.49f,  0.5f);
			vertices[1] = new Vertex( 0.5f, -0.49f, -0.5f);			
			vertices[2] = new Vertex(-0.5f, -0.49f, -0.5f);
			vertices[3] = new Vertex(-0.5f, -0.49f,  0.5f);
			obj.addFace(vertices, rt, Side.TOP, mtl);
		}
		else
		{
			// ascending
			vertices[0] = new Vertex( 0.5f, -0.5f,  0.5f);	
			vertices[1] = new Vertex( 0.5f,  0.5f, -0.5f);				
			vertices[2] = new Vertex(-0.5f,  0.5f, -0.5f);
			vertices[3] = new Vertex(-0.5f, -0.5f,  0.5f); 				
			obj.addFace(vertices, rt, Side.TOP, mtl);
		}
	}

}
