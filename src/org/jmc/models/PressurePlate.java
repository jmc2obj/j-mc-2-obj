package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for pressure plates
 */
public class PressurePlate extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		String[] mtlSides = getMtlSides(data,biome);
		boolean[] drawSides = drawSides(chunks, x, y, z, data);

		Vertex[] vertices = new Vertex[4];

		UV[] uvTop = new UV[] {
				new UV(1/16f, 1/16f),
				new UV(15/16f, 1/16f),
				new UV(15/16f, 15/16f),
				new UV(1/16f, 15/16f),
		};
		UV[] uvSide = new UV[] {
				new UV(1/16f, 0),
				new UV(15/16f, 0),
				new UV(15/16f, 1/16f),
				new UV(1/16f, 1/16f),
		};

		// top
		vertices[0] = new Vertex(x-0.4375f, y-0.4375f, z+0.4375f);
		vertices[1] = new Vertex(x+0.4375f, y-0.4375f, z+0.4375f);
		vertices[2] = new Vertex(x+0.4375f, y-0.4375f, z-0.4375f);
		vertices[3] = new Vertex(x-0.4375f, y-0.4375f, z-0.4375f);
		obj.addFace(vertices, uvTop, null, mtlSides[0]);
		// front
		vertices[0] = new Vertex(x+0.4375f, y-0.5f, z-0.4375f);
		vertices[1] = new Vertex(x-0.4375f, y-0.5f, z-0.4375f);
		vertices[2] = new Vertex(x-0.4375f, y-0.4375f, z-0.4375f);
		vertices[3] = new Vertex(x+0.4375f, y-0.4375f, z-0.4375f);
		obj.addFace(vertices, uvSide, null, mtlSides[1]);
		// back
		vertices[0] = new Vertex(x-0.4375f, y-0.5f, z+0.4375f);
		vertices[1] = new Vertex(x+0.4375f, y-0.5f, z+0.4375f);
		vertices[2] = new Vertex(x+0.4375f, y-0.4375f, z+0.4375f);
		vertices[3] = new Vertex(x-0.4375f, y-0.4375f, z+0.4375f);
		obj.addFace(vertices, uvSide, null, mtlSides[2]);
		// left
		vertices[0] = new Vertex(x-0.4375f, y-0.5f, z-0.4375f);
		vertices[1] = new Vertex(x-0.4375f, y-0.5f, z+0.4375f);
		vertices[2] = new Vertex(x-0.4375f, y-0.4375f, z+0.4375f);
		vertices[3] = new Vertex(x-0.4375f, y-0.4375f, z-0.4375f);
		obj.addFace(vertices, uvSide, null, mtlSides[3]);
		// right
		vertices[0] = new Vertex(x+0.4375f, y-0.5f, z-0.4375f);
		vertices[1] = new Vertex(x+0.4375f, y-0.5f, z+0.4375f);
		vertices[2] = new Vertex(x+0.4375f, y-0.4375f, z+0.4375f);
		vertices[3] = new Vertex(x+0.4375f, y-0.4375f, z-0.4375f);
		obj.addFace(vertices, uvSide, null, mtlSides[4]);
		// bottom
		if (drawSides[5])
		{
			vertices[0] = new Vertex(x+0.4375f, y-0.5f, z+0.4375f);
			vertices[1] = new Vertex(x-0.4375f, y-0.5f, z+0.4375f);
			vertices[2] = new Vertex(x-0.4375f, y-0.5f, z-0.4375f);
			vertices[3] = new Vertex(x+0.4375f, y-0.5f, z-0.4375f);
			obj.addFace(vertices, uvTop, null, mtlSides[5]);
		}
	}

}
