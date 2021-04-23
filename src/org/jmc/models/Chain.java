package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Generic model for blocks rendered as 2 crossed polygons, like saplings.
 */
public class Chain extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		Transform move = new Transform();
		move.translate(x, y, z);



		UV[] aUV = new UV[] {
				new UV(0/16f,  0/16f), new UV(3/16f,  0/16f),
				new UV(3/16f, 16/16f), new UV(0/16f, 16/16f)
		};
		UV[] bUV = new UV[] {
				new UV(3/16f,  0/16f), new UV(6/16f,  0/16f),
				new UV(6/16f, 16/16f), new UV(3/16f, 16/16f)
		};


		Transform rotation = new Transform();
		String axis = data.get("axis");
		if (axis == null) {
			throw new RuntimeException("Missing axis value!");
		}
		switch (axis) {
			case "x":
				rotation.rotate(0, 0, 90);
				break;
			case "z":
				rotation.rotate(90, 0, 0);
				break;
			case "y":
				// keep upright
				break;
			default:
				throw new RuntimeException("Unknown axis: " + axis);

		}

		final Transform transform = move.multiply(rotation);
		final String material = materials.get(data, biome)[0];
		
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(1/16f ,-0.5f,-1/16f);
		vertices[1] = new Vertex(-1/16f,-0.5f,1/16f);
		vertices[2] = new Vertex(-1/16f,+0.5f,1/16f);
		vertices[3] = new Vertex(1/16f ,+0.5f,-1/16f);
		obj.addDoubledFace(vertices, aUV, transform, material);

		vertices[0] = new Vertex(-1/16f,-0.5f,-1/16f);
		vertices[1] = new Vertex(1/16f ,-0.5f,1/16f);
		vertices[2] = new Vertex(1/16f ,+0.5f,1/16f);
		vertices[3] = new Vertex(-1/16f,+0.5f,-1/16f);
		obj.addDoubledFace(vertices, bUV, transform, material);
	}

}
