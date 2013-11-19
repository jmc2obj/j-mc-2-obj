package org.jmc.models;

import org.jmc.BlockInfo;
import org.jmc.BlockTypes;
import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;


/**
 * Model for glass panes and iron bars.
 */
public class Pane extends BlockModel
{
	
	/** Checks whether the pane should connect to another block */
	private boolean checkConnect(short otherId)
	{
		// connects to other panes, glass, and any solid blocks
		if (otherId == 0)
			return false;
		if (otherId == 101 || otherId == 102 || otherId == 160 || otherId == 20 || otherId == 95)
			return true;
		return BlockTypes.get(otherId).getOcclusion() == BlockInfo.Occlusion.FULL;
	}

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome)
	{
		String mtl = materials.get(data,biome)[0];
		String mtlSide = materials.get(data,biome)[1];
		
		boolean n = checkConnect(chunks.getBlockID(x, y, z-1));
		boolean s = checkConnect(chunks.getBlockID(x, y, z+1));
		boolean e = checkConnect(chunks.getBlockID(x+1, y, z));
		boolean w = checkConnect(chunks.getBlockID(x-1, y, z));
		boolean none = !(n || s || e || w);

		boolean up = checkConnect(chunks.getBlockID(x, y+1, z));
		boolean down = checkConnect(chunks.getBlockID(x, y-1, z));
		
		Transform t = new Transform();
		t.translate(x, y, z);		
		
		Vertex[] vertices = new Vertex[4];
		UV[] uv = new UV[4];
		if (n || none)
		{
			uv[0] = new UV(0, 0);
			uv[1] = new UV(0.5f, 0);
			uv[2] = new UV(0.5f, 1);
			uv[3] = new UV(0, 1);
			vertices[0] = new Vertex(0.0f, -0.5f, -0.5f);
			vertices[1] = new Vertex(0.0f, -0.5f,  0.0f);
			vertices[2] = new Vertex(0.0f,  0.5f,  0.0f);
			vertices[3] = new Vertex(0.0f,  0.5f, -0.5f);
			obj.addFace(vertices, uv, t, mtl);

			uv[0] = new UV(7/16f, 0.5f);
			uv[1] = new UV(9/16f, 0.5f);
			uv[2] = new UV(9/16f, 1);
			uv[3] = new UV(7/16f, 1);
			if (!up)
			{
				vertices[0] = new Vertex(-1/16f, 0.5f, 0);
				vertices[1] = new Vertex( 1/16f, 0.5f, 0);
				vertices[2] = new Vertex( 1/16f, 0.5f, -0.5f);
				vertices[3] = new Vertex(-1/16f, 0.5f, -0.5f);
				obj.addFace(vertices, uv, t, mtlSide);
			}
			if (!down)
			{
				vertices[0] = new Vertex( 1/16f, -0.5f, 0);
				vertices[1] = new Vertex(-1/16f, -0.5f, 0);
				vertices[2] = new Vertex(-1/16f, -0.5f, -0.5f);
				vertices[3] = new Vertex( 1/16f, -0.5f, -0.5f);
				obj.addFace(vertices, uv, t, mtlSide);
			}
		}
		else if (s && !e && !w)
		{
			uv[0] = new UV(7/16f, 0);
			uv[1] = new UV(9/16f, 0);
			uv[2] = new UV(9/16f, 1);
			uv[3] = new UV(7/16f, 1);
			vertices[0] = new Vertex( 1/16f, -0.5f, 0);
			vertices[1] = new Vertex(-1/16f, -0.5f, 0);
			vertices[2] = new Vertex(-1/16f,  0.5f, 0);
			vertices[3] = new Vertex( 1/16f,  0.5f, 0);
			obj.addFace(vertices, uv, t, mtlSide);
		}
		
		if (s || none)
		{
			uv[0] = new UV(0.5f, 0);
			uv[1] = new UV(1, 0);
			uv[2] = new UV(1, 1);
			uv[3] = new UV(0.5f, 1);
			vertices[0] = new Vertex(0.0f, -0.5f, 0.0f);
			vertices[1] = new Vertex(0.0f, -0.5f, 0.5f);
			vertices[2] = new Vertex(0.0f,  0.5f, 0.5f);
			vertices[3] = new Vertex(0.0f,  0.5f, 0.0f);
			obj.addFace(vertices, uv, t, mtl);

			uv[0] = new UV(7/16f, 0);
			uv[1] = new UV(9/16f, 0);
			uv[2] = new UV(9/16f, 0.5f);
			uv[3] = new UV(7/16f, 0.5f);
			if (!up)
			{
				vertices[0] = new Vertex(-1/16f, 0.5f, 0.5f);
				vertices[1] = new Vertex( 1/16f, 0.5f, 0.5f);
				vertices[2] = new Vertex( 1/16f, 0.5f, 0);
				vertices[3] = new Vertex(-1/16f, 0.5f, 0);
				obj.addFace(vertices, uv, t, mtlSide);
			}
			if (!down)
			{
				vertices[0] = new Vertex( 1/16f, -0.5f, 0.5f);
				vertices[1] = new Vertex(-1/16f, -0.5f, 0.5f);
				vertices[2] = new Vertex(-1/16f, -0.5f, 0);
				vertices[3] = new Vertex( 1/16f, -0.5f, 0);
				obj.addFace(vertices, uv, t, mtlSide);
			}
		}
		else if (n && !e && !w)
		{
			uv[0] = new UV(7/16f, 0);
			uv[1] = new UV(9/16f, 0);
			uv[2] = new UV(9/16f, 1);
			uv[3] = new UV(7/16f, 1);
			vertices[0] = new Vertex(-1/16f, -0.5f, 0);
			vertices[1] = new Vertex( 1/16f, -0.5f, 0);
			vertices[2] = new Vertex( 1/16f,  0.5f, 0);
			vertices[3] = new Vertex(-1/16f,  0.5f, 0);
			obj.addFace(vertices, uv, t, mtlSide);
		}

		if (e || none)
		{
			uv[0] = new UV(0, 0);
			uv[1] = new UV(0.5f, 0);
			uv[2] = new UV(0.5f, 1);
			uv[3] = new UV(0, 1);
			vertices[0] = new Vertex(0.5f, -0.5f, 0.0f);
			vertices[1] = new Vertex(0.0f, -0.5f, 0.0f);
			vertices[2] = new Vertex(0.0f,  0.5f, 0.0f);
			vertices[3] = new Vertex(0.5f,  0.5f, 0.0f);
			obj.addFace(vertices, uv, t, mtl);

			uv[0] = new UV(9/16f, 0.5f);
			uv[1] = new UV(9/16f, 1);
			uv[2] = new UV(7/16f, 1);
			uv[3] = new UV(7/16f, 0.5f);
			if (!up)
			{
				vertices[0] = new Vertex(0, 0.5f,  1/16f);
				vertices[1] = new Vertex(0.5f, 0.5f,  1/16f);
				vertices[2] = new Vertex(0.5f, 0.5f, -1/16f);
				vertices[3] = new Vertex(0, 0.5f, -1/16f);
				obj.addFace(vertices, uv, t, mtlSide);
			}
			if (!down)
			{
				vertices[0] = new Vertex(0.5f, -0.5f,  1/16f);
				vertices[1] = new Vertex(0, -0.5f,  1/16f);
				vertices[2] = new Vertex(0, -0.5f, -1/16f);
				vertices[3] = new Vertex(0.5f, -0.5f, -1/16f);
				obj.addFace(vertices, uv, t, mtlSide);
			}
		}
		else if (w && !n && !s)
		{
			uv[0] = new UV(7/16f, 0);
			uv[1] = new UV(9/16f, 0);
			uv[2] = new UV(9/16f, 1);
			uv[3] = new UV(7/16f, 1);
			vertices[0] = new Vertex(0, -0.5f,  1/16f);
			vertices[1] = new Vertex(0, -0.5f, -1/16f);
			vertices[2] = new Vertex(0,  0.5f, -1/16f);
			vertices[3] = new Vertex(0,  0.5f,  1/16f);
			obj.addFace(vertices, uv, t, mtlSide);
		}
		
		if (w || none)
		{
			uv[0] = new UV(0.5f, 0);
			uv[1] = new UV(1, 0);
			uv[2] = new UV(1, 1);
			uv[3] = new UV(0.5f, 1);
			vertices[0] = new Vertex( 0.0f, -0.5f, 0.0f);
			vertices[1] = new Vertex(-0.5f, -0.5f, 0.0f);
			vertices[2] = new Vertex(-0.5f,  0.5f, 0.0f);
			vertices[3] = new Vertex( 0.0f,  0.5f, 0.0f);
			obj.addFace(vertices, uv, t, mtl);

			uv[0] = new UV(9/16f, 0);
			uv[1] = new UV(9/16f, 0.5f);
			uv[2] = new UV(7/16f, 0.5f);
			uv[3] = new UV(7/16f, 0);
			if (!up)
			{
				vertices[0] = new Vertex(-0.5f, 0.5f,  1/16f);
				vertices[1] = new Vertex(0, 0.5f,  1/16f);
				vertices[2] = new Vertex(0, 0.5f, -1/16f);
				vertices[3] = new Vertex(-0.5f, 0.5f, -1/16f);
				obj.addFace(vertices, uv, t, mtlSide);
			}
			if (!down)
			{
				vertices[0] = new Vertex(0, -0.5f,  1/16f);
				vertices[1] = new Vertex(-0.5f, -0.5f,  1/16f);
				vertices[2] = new Vertex(-0.5f, -0.5f, -1/16f);
				vertices[3] = new Vertex(0, -0.5f, -1/16f);
				obj.addFace(vertices, uv, t, mtlSide);
			}
		}
		else if (e && !n && !s)
		{
			uv[0] = new UV(7/16f, 0);
			uv[1] = new UV(9/16f, 0);
			uv[2] = new UV(9/16f, 1);
			uv[3] = new UV(7/16f, 1);
			vertices[0] = new Vertex(0, -0.5f, -1/16f);
			vertices[1] = new Vertex(0, -0.5f,  1/16f);
			vertices[2] = new Vertex(0,  0.5f,  1/16f);
			vertices[3] = new Vertex(0,  0.5f, -1/16f);
			obj.addFace(vertices, uv, t, mtlSide);
		}
	}

}
