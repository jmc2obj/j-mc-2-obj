package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.BlockInfo;
import org.jmc.BlockTypes;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ObjChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for glass panes and iron bars.
 */
public class Pane extends BlockModel
{
	
	/** Checks whether the pane should connect to another block */
	private boolean checkConnect(BlockData other)
	{
		// connects to other panes, glass, and any solid blocks
		if (other.id.path.endsWith("air"))
			return false;
		if (other.id.equals(new NamespaceID("minecraft", "iron_bars")) || other.id.path.endsWith("glass_pane") || other.id.path.endsWith("glass"))
			return true;
		return other.getInfo().getOcclusion() == BlockInfo.Occlusion.FULL;
	}

	@Override
	public void addModel(ObjChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		NamespaceID mtl = materials.get(data.state,biome)[0];
		NamespaceID mtlSide = materials.get(data.state,biome)[1];
		
		boolean n = data.state.get("north").equals("true");
		boolean s = data.state.get("south").equals("true");
		boolean e = data.state.get("east").equals("true");
		boolean w = data.state.get("west").equals("true");
		boolean none = !(n || s || e || w);

		boolean up = checkConnect(chunks.getBlockData(x, y+1, z));
		boolean down = checkConnect(chunks.getBlockData(x, y-1, z));
		
		Transform t = Transform.translation(x, y, z);		
		
		Vertex[] vertices = new Vertex[4];
		UV[] uv = new UV[4];
		
		if (none)
		{
			uv[0] = new UV(9/16f, 0);
			uv[1] = new UV(7/16f, 0);
			uv[2] = new UV(7/16f, 1);
			uv[3] = new UV(9/16f, 1);
			vertices[0] = new Vertex(1/16f, -0.5f, -1/16f);
			vertices[1] = new Vertex(1/16f, -0.5f,  1/16f);
			vertices[2] = new Vertex(1/16f,  0.5f,  1/16f);
			vertices[3] = new Vertex(1/16f,  0.5f, -1/16f);
			obj.addFace(vertices, uv, t, mtl);
			
			vertices[0] = new Vertex(-1/16f, -0.5f, 1/16f);
			vertices[1] = new Vertex(-1/16f, -0.5f,  -1/16f);
			vertices[2] = new Vertex(-1/16f,  0.5f,  -1/16f);
			vertices[3] = new Vertex(-1/16f,  0.5f, 1/16f);
			obj.addFace(vertices, uv, t, mtl);			
			
			vertices[0] = new Vertex(1/16f, -0.5f, 1/16f);
			vertices[1] = new Vertex(-1/16f, -0.5f,  1/16f);
			vertices[2] = new Vertex(-1/16f,  0.5f,  1/16f);
			vertices[3] = new Vertex(1/16f,  0.5f, 1/16f);
			obj.addFace(vertices, uv, t, mtl);
			
			vertices[0] = new Vertex(-1/16f, -0.5f, -1/16f);
			vertices[1] = new Vertex(1/16f, -0.5f,  -1/16f);
			vertices[2] = new Vertex(1/16f,  0.5f,  -1/16f);
			vertices[3] = new Vertex(-1/16f,  0.5f, -1/16f);
			obj.addFace(vertices, uv, t, mtl);	
			
			uv[0] = new UV(9/16f, 7/16f);
			uv[1] = new UV(7/16f, 7/16f);
			uv[2] = new UV(7/16f, 9/16f);
			uv[3] = new UV(9/16f, 9/16f);
			if (!up)
			{
				vertices[0] = new Vertex(-1/16f, 0.5f, -1/16f);
				vertices[1] = new Vertex(1/16f, 0.5f,  -1/16f);
				vertices[2] = new Vertex(1/16f,  0.5f,  1/16f);
				vertices[3] = new Vertex(-1/16f,  0.5f, 1/16f);
				obj.addFace(vertices, uv, t, mtlSide);				
			}
			if (!down)
			{
				vertices[0] = new Vertex(-1/16f, -0.5f, -1/16f);
				vertices[1] = new Vertex(1/16f, -0.5f,  -1/16f);
				vertices[2] = new Vertex(1/16f,  -0.5f,  1/16f);
				vertices[3] = new Vertex(-1/16f,  -0.5f, 1/16f);				
			}
		}
		
		
		if (n)
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
		
		if (s)
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

		if (e)
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
		
		if (w)
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
