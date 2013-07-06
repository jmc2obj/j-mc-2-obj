package org.jmc.models;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;


/**
 * Model for daylight sensors
 */
public class DaylightSensor extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome)
	{
		String[] mtlSides = getMtlSides(data,biome);
		boolean[] drawSides = drawSides(chunks, x, y, z);

		Vertex[] vertices = new Vertex[4];

		UV[] uvTop = new UV[] {
				new UV(0, 0),
				new UV(1, 0),
				new UV(1, 1),
				new UV(0, 1),
		};
		UV[] uvSide = new UV[] {
				new UV(0, 0),
				new UV(1, 0),
				new UV(1, 6/16f),
				new UV(0, 6/16f),
		};

		// top
		vertices[0] = new Vertex(x-0.5f, y-0.125f, z+0.5f);
		vertices[1] = new Vertex(x+0.5f, y-0.125f, z+0.5f);
		vertices[2] = new Vertex(x+0.5f, y-0.125f, z-0.5f);
		vertices[3] = new Vertex(x-0.5f, y-0.125f, z-0.5f);
		obj.addFace(vertices, uvTop, null, mtlSides[0]);
		// front
		if (drawSides[1])
		{
			vertices[0] = new Vertex(x+0.5f, y-0.5f, z-0.5f);
			vertices[1] = new Vertex(x-0.5f, y-0.5f, z-0.5f);
			vertices[2] = new Vertex(x-0.5f, y-0.125f, z-0.5f);
			vertices[3] = new Vertex(x+0.5f, y-0.125f, z-0.5f);
			obj.addFace(vertices, uvSide, null, mtlSides[1]);
		}
		// back
		if (drawSides[2])
		{
			vertices[0] = new Vertex(x-0.5f, y-0.5f, z+0.5f);
			vertices[1] = new Vertex(x+0.5f, y-0.5f, z+0.5f);
			vertices[2] = new Vertex(x+0.5f, y-0.125f, z+0.5f);
			vertices[3] = new Vertex(x-0.5f, y-0.125f, z+0.5f);
			obj.addFace(vertices, uvSide, null, mtlSides[2]);
		}
		// left
		if (drawSides[3])
		{
			vertices[0] = new Vertex(x-0.5f, y-0.5f, z-0.5f);
			vertices[1] = new Vertex(x-0.5f, y-0.5f, z+0.5f);
			vertices[2] = new Vertex(x-0.5f, y-0.125f, z+0.5f);
			vertices[3] = new Vertex(x-0.5f, y-0.125f, z-0.5f);
			obj.addFace(vertices, uvSide, null, mtlSides[3]);
		}
		// right
		if (drawSides[4])
		{
			vertices[0] = new Vertex(x+0.5f, y-0.5f, z-0.5f);
			vertices[1] = new Vertex(x+0.5f, y-0.5f, z+0.5f);
			vertices[2] = new Vertex(x+0.5f, y-0.125f, z+0.5f);
			vertices[3] = new Vertex(x+0.5f, y-0.125f, z-0.5f);
			obj.addFace(vertices, uvSide, null, mtlSides[4]);
		}
		// bottom
		if (drawSides[5])
		{
			vertices[0] = new Vertex(x+0.5f, y-0.5f, z+0.5f);
			vertices[1] = new Vertex(x-0.5f, y-0.5f, z+0.5f);
			vertices[2] = new Vertex(x-0.5f, y-0.5f, z-0.5f);
			vertices[3] = new Vertex(x+0.5f, y-0.5f, z-0.5f);
			obj.addFace(vertices, uvTop, null, mtlSides[5]);
		}
	}

}
