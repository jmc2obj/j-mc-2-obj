package org.jmc.models;

import java.util.HashMap;

import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;
import org.jmc.util.Log;


/**
 * Model for doors.
 */
public class Door extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		boolean top = data.get("half").equals("upper");
		int open = Boolean.parseBoolean(data.get("open")) ? 1 : 0;
		int reversed = data.get("hinge").equals("right") ? 1 : 0;// left hinge was default
		int direction;
		
		switch (data.get("facing")) {
		case "north":
			direction = 3;
			break;
		case "east":
			direction = 0;
			break;
		case "south":
			direction = 1;
			break;
		case "west":
			direction = 2;
			break;

		default:
			Log.error("Unknown door facing value! " + data.get("facing"), null, false);
			direction = 0;
		}

		String mtl = top ? materials.get(data,biome)[0] : materials.get(data,biome)[1];
		
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

		boolean[] drawSides = drawSides(chunks, x, y, z);
		Vertex[] vertices = new Vertex[4];
		UV[] uv = new UV[4];

		// top
		if (drawSides[0])
		{
			vertices[0] = new Vertex(-0.5f, 0.5f,  0.09375f); uv[0] = new UV(13/16f,1);
			vertices[1] = new Vertex( 0.5f, 0.5f,  0.09375f); uv[1] = new UV(13/16f,0);
			vertices[2] = new Vertex( 0.5f, 0.5f, -0.09375f); uv[2] = new UV(1,0);
			vertices[3] = new Vertex(-0.5f, 0.5f, -0.09375f); uv[3] = new UV(1,1);
			obj.addFace(vertices, uv, rt, mtl);
		}

		// front
		vertices[0] = new Vertex( 0.5f, -0.5f, -0.09375f);
		vertices[1] = new Vertex(-0.5f, -0.5f, -0.09375f);
		vertices[2] = new Vertex(-0.5f,  0.5f, -0.09375f);
		vertices[3] = new Vertex( 0.5f,  0.5f, -0.09375f);
		obj.addFace(vertices, null, rt, mtl);

		// back (texture is flipped horizontally)
		vertices[0] = new Vertex(-0.5f, -0.5f, 0.09375f); uv[0] = new UV(1,0);
		vertices[1] = new Vertex( 0.5f, -0.5f, 0.09375f); uv[1] = new UV(0,0);
		vertices[2] = new Vertex( 0.5f,  0.5f, 0.09375f); uv[2] = new UV(0,1);
		vertices[3] = new Vertex(-0.5f,  0.5f, 0.09375f); uv[3] = new UV(1,1);
		obj.addFace(vertices, uv, rt, mtl);

		// left
		vertices[0] = new Vertex(-0.5f, -0.5f, -0.09375f); uv[0] = new UV(13/16f,0);
		vertices[1] = new Vertex(-0.5f, -0.5f,  0.09375f); uv[1] = new UV(1,0);
		vertices[2] = new Vertex(-0.5f,  0.5f,  0.09375f); uv[2] = new UV(1,1);
		vertices[3] = new Vertex(-0.5f,  0.5f, -0.09375f); uv[3] = new UV(13/16f,1);
		obj.addFace(vertices, uv, rt, mtl);

		// right
		vertices[0] = new Vertex(0.5f, -0.5f,  0.09375f); uv[0] = new UV(0,0);
		vertices[1] = new Vertex(0.5f, -0.5f, -0.09375f); uv[1] = new UV(3/16f,0);
		vertices[2] = new Vertex(0.5f,  0.5f, -0.09375f); uv[2] = new UV(3/16f,1);
		vertices[3] = new Vertex(0.5f,  0.5f,  0.09375f); uv[3] = new UV(0,1);
		obj.addFace(vertices, uv, rt, mtl);

		// bottom
		if (drawSides[5])
		{
			vertices[0] = new Vertex( 0.5f, -0.5f,  0.09375f); uv[0] = new UV(13/16f,1);
			vertices[1] = new Vertex(-0.5f, -0.5f,  0.09375f); uv[1] = new UV(13/16f,0);
			vertices[2] = new Vertex(-0.5f, -0.5f, -0.09375f); uv[2] = new UV(1,0);
			vertices[3] = new Vertex( 0.5f, -0.5f, -0.09375f); uv[3] = new UV(1,1);
			obj.addFace(vertices, uv, rt, mtl);
		}
	}
}
