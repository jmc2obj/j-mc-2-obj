package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJFile;
import org.jmc.OBJFile.Side;
import org.jmc.Transform;
import org.jmc.Vertex;


/**
 * Model for doors.
 */
public class Door extends BlockModel
{

	@Override
	public void addModel(OBJFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		boolean top;
		int open;
		int reversed;
		int direction;
		
		top = (data & 8) != 0;
		if (top)
		{
			byte bottomData = chunks.getBlockData(x, y-1, z);
			open = (bottomData & 4) != 0 ? 1 : 0;
			reversed = (data & 1) != 0 ? 1 : 0;
			direction = (bottomData & 3);
		}
		else
		{
			byte topData = chunks.getBlockData(x, y+1, z);
			open = (data & 4) != 0 ? 1 : 0;
			reversed = (topData & 1) != 0 ? 1 : 0;
			direction = (data & 3);
		}

		String mtl = top ? materials.get(data)[0] : materials.get(data)[1];
		
		/*
		  The model is rendered in the middle of the block facing North, then 
		  rotated and translated according to this table:
		
			Open Rev  Dir  | Rot   Tx  Tz
			---------------+---------------
			0    0    00   | 270   -
			0    0    01   |   0       -
			0    0    10   |  90   +
			0    0    11   | 180       +
			0    1    00   |  90   -
			0    1    01   | 180       -
			0    1    10   | 270   +
			0    1    11   |   0       +
			1    0    00   | 180       -
			1    0    01   | 270   +
			1    0    10   |   0       +
			1    0    11   |  90   -
			1    1    00   | 180       +
			1    1    01   | 270   -
			1    1    10   |   0       -
			1    1    11   |  90   +
		 */
		float[][] table = new float[][] {
				{ 270, -0.40625f, 0 },
				{   0, 0, -0.40625f },
				{  90, +0.40625f, 0 },
				{ 180, 0, +0.40625f },
				{  90, -0.40625f, 0 },
				{ 180, 0, -0.40625f },
				{ 270, +0.40625f, 0 },
				{   0, 0, +0.40625f },
				{ 180, 0, -0.40625f },
				{ 270, +0.40625f, 0 },
				{   0, 0, +0.40625f },
				{  90, -0.40625f, 0 },
				{ 180, 0, +0.40625f },
				{ 270, -0.40625f, 0 },
				{   0, 0, -0.40625f },
				{  90, +0.40625f, 0 },
		};
		int lookup = direction | reversed<<2 | open<<3;
		
		Transform rotate = new Transform();
		rotate.rotate(0, table[lookup][0], 0);
		Transform translate = new Transform();
		translate.translate(x+table[lookup][1], y, z+table[lookup][2]);		
		Transform rt = translate.multiply(rotate);

		
		Vertex[] vertices = new Vertex[4];

		vertices[0] = new Vertex(-0.5f, -0.5f, -0.09375f);
		vertices[1] = new Vertex(-0.5f,  0.5f, -0.09375f);
		vertices[2] = new Vertex( 0.5f,  0.5f, -0.09375f);
		vertices[3] = new Vertex( 0.5f, -0.5f, -0.09375f);
		obj.addFace(vertices, rt, Side.FRONT, mtl);

		vertices[0] = new Vertex(-0.5f, -0.5f, 0.09375f);
		vertices[1] = new Vertex(-0.5f,  0.5f, 0.09375f);
		vertices[2] = new Vertex( 0.5f,  0.5f, 0.09375f);
		vertices[3] = new Vertex( 0.5f, -0.5f, 0.09375f);
		obj.addFace(vertices, rt ,Side.BACK, mtl);

		vertices[0] = new Vertex(-0.5f, -0.5f,  0.09375f);
		vertices[1] = new Vertex(-0.5f,  0.5f,  0.09375f);
		vertices[2] = new Vertex(-0.5f,  0.5f, -0.09375f);
		vertices[3] = new Vertex(-0.5f, -0.5f, -0.09375f);
		obj.addFace(vertices, rt, Side.LEFT, mtl);

		vertices[0] = new Vertex(0.5f, -0.5f, -0.09375f);
		vertices[1] = new Vertex(0.5f,  0.5f, -0.09375f);
		vertices[2] = new Vertex(0.5f,  0.5f,  0.09375f);
		vertices[3] = new Vertex(0.5f, -0.5f,  0.09375f);
		obj.addFace(vertices, rt, Side.RIGHT, mtl);
	}

}
