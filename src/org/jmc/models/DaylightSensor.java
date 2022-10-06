package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ObjChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for daylight sensors
 */
public class DaylightSensor extends BlockModel
{

	@Override
	public void addModel(ObjChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		NamespaceID[] mtlSides = getMtlSides(data,biome);
		boolean[] drawSides = drawSides(chunks, x, y, z, data);

		Vertex[] vertices = new Vertex[4];

		int inverted = data.state.get("inverted").equals("true") ? 1 : 0;
		
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
		obj.addFace(vertices, uvTop, null, mtlSides[inverted]);
		// front
		if (drawSides[1])
		{
			vertices[0] = new Vertex(x+0.5f, y-0.5f, z-0.5f);
			vertices[1] = new Vertex(x-0.5f, y-0.5f, z-0.5f);
			vertices[2] = new Vertex(x-0.5f, y-0.125f, z-0.5f);
			vertices[3] = new Vertex(x+0.5f, y-0.125f, z-0.5f);
			obj.addFace(vertices, uvSide, null, mtlSides[2]);
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
			obj.addFace(vertices, uvSide, null, mtlSides[2]);
		}
		// right
		if (drawSides[4])
		{
			vertices[0] = new Vertex(x+0.5f, y-0.5f, z-0.5f);
			vertices[1] = new Vertex(x+0.5f, y-0.5f, z+0.5f);
			vertices[2] = new Vertex(x+0.5f, y-0.125f, z+0.5f);
			vertices[3] = new Vertex(x+0.5f, y-0.125f, z-0.5f);
			obj.addFace(vertices, uvSide, null, mtlSides[2]);
		}
		// bottom
		if (drawSides[5])
		{
			vertices[0] = new Vertex(x+0.5f, y-0.5f, z+0.5f);
			vertices[1] = new Vertex(x-0.5f, y-0.5f, z+0.5f);
			vertices[2] = new Vertex(x-0.5f, y-0.5f, z-0.5f);
			vertices[3] = new Vertex(x+0.5f, y-0.5f, z-0.5f);
			obj.addFace(vertices, uvTop, null, mtlSides[2]);
		}
	}

}
