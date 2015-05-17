package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;


/**
 * Model for beds.
 */
public class Bed extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome)
	{
		int dir = (data & 3);
		boolean head = (data & 8) != 0;


		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

		switch (dir)
		{
			case 0: rotate.rotate(0, 180, 0); break;
			case 1: rotate.rotate(0, -90, 0); break;
			case 3: rotate.rotate(0, 90, 0); break;
		}
		translate.translate(x, y, z);		
		rt = translate.multiply(rotate);

		
		Vertex[] vertices = new Vertex[4];
		UV[] uv = new UV[4];
		
		if (head)
		{
			// top
			vertices[0] = new Vertex(-0.5f, 0.0625f,  0.5f); uv[0] = new UV(0, 1);
			vertices[1] = new Vertex( 0.5f, 0.0625f,  0.5f); uv[1] = new UV(0, 0);
			vertices[2] = new Vertex( 0.5f, 0.0625f, -0.5f); uv[2] = new UV(1, 0);
			vertices[3] = new Vertex(-0.5f, 0.0625f, -0.5f); uv[3] = new UV(1, 1);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[0]);
			// front
			vertices[0] = new Vertex( 0.5f,   -0.5f, -0.5f); uv[0] = new UV(0, 0);
			vertices[1] = new Vertex(-0.5f,   -0.5f, -0.5f); uv[1] = new UV(1, 0);
			vertices[2] = new Vertex(-0.5f, 0.0625f, -0.5f); uv[2] = new UV(1, 9/16f);
			vertices[3] = new Vertex( 0.5f, 0.0625f, -0.5f); uv[3] = new UV(0, 9/16f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[2]);
			// left
			vertices[0] = new Vertex(-0.5f,   -0.5f, -0.5f); uv[0] = new UV(1, 0);
			vertices[1] = new Vertex(-0.5f,   -0.5f,  0.5f); uv[1] = new UV(0, 0);
			vertices[2] = new Vertex(-0.5f, 0.0625f,  0.5f); uv[2] = new UV(0, 9/16f);
			vertices[3] = new Vertex(-0.5f, 0.0625f, -0.5f); uv[3] = new UV(1, 9/16f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[1]);
			// right
			vertices[0] = new Vertex(0.5f,   -0.5f,  0.5f); uv[0] = new UV(0, 0);
			vertices[1] = new Vertex(0.5f,   -0.5f, -0.5f); uv[1] = new UV(1, 0);
			vertices[2] = new Vertex(0.5f, 0.0625f, -0.5f); uv[2] = new UV(1, 9/16f);
			vertices[3] = new Vertex(0.5f, 0.0625f,  0.5f); uv[3] = new UV(0, 9/16f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[1]);
		}
		else
		{
			// top
			vertices[0] = new Vertex(-0.5f, 0.0625f,  0.5f); uv[0] = new UV(0, 1);
			vertices[1] = new Vertex( 0.5f, 0.0625f,  0.5f); uv[1] = new UV(0, 0);
			vertices[2] = new Vertex( 0.5f, 0.0625f, -0.5f); uv[2] = new UV(1, 0);
			vertices[3] = new Vertex(-0.5f, 0.0625f, -0.5f); uv[3] = new UV(1, 1);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[3]);
			// left
			vertices[0] = new Vertex(-0.5f,   -0.5f, -0.5f); uv[0] = new UV(1, 0);
			vertices[1] = new Vertex(-0.5f,   -0.5f,  0.5f); uv[1] = new UV(0, 0);
			vertices[2] = new Vertex(-0.5f, 0.0625f,  0.5f); uv[2] = new UV(0, 9/16f);
			vertices[3] = new Vertex(-0.5f, 0.0625f, -0.5f); uv[3] = new UV(1, 9/16f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[4]);
			// right
			vertices[0] = new Vertex(0.5f,   -0.5f,  0.5f); uv[0] = new UV(0, 0);
			vertices[1] = new Vertex(0.5f,   -0.5f, -0.5f); uv[1] = new UV(1, 0);
			vertices[2] = new Vertex(0.5f, 0.0625f, -0.5f); uv[2] = new UV(1, 9/16f);
			vertices[3] = new Vertex(0.5f, 0.0625f,  0.5f); uv[3] = new UV(0, 9/16f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[4]);
			// back
			vertices[0] = new Vertex( 0.5f,   -0.5f, 0.5f); uv[0] = new UV(0, 0);
			vertices[1] = new Vertex(-0.5f,   -0.5f, 0.5f); uv[1] = new UV(1, 0);
			vertices[2] = new Vertex(-0.5f, 0.0625f, 0.5f); uv[2] = new UV(1, 9/16f);
			vertices[3] = new Vertex( 0.5f, 0.0625f, 0.5f); uv[3] = new UV(0, 9/16f);
			obj.addFace(vertices, uv, rt, materials.get(data, biome)[5]);
		}
		
	}

}
