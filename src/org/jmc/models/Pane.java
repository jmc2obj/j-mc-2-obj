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
		// connects to itself and other solid blocks
		if (otherId == 0)
			return false;
		if (otherId == blockId)
			return true;
		return BlockTypes.get(otherId).occlusion == BlockInfo.Occlusion.FULL;
	}

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data)
	{
		boolean n = checkConnect(chunks.getBlockID(x, y, z-1));
		boolean s = checkConnect(chunks.getBlockID(x, y, z+1));
		boolean e = checkConnect(chunks.getBlockID(x+1, y, z));
		boolean w = checkConnect(chunks.getBlockID(x-1, y, z));
		boolean none = !(n || s || e || w);
		
		Transform t = new Transform();
		t.translate(x, y, z);		
		
		Vertex[] vertices = new Vertex[4];
		UV[] uv = new UV[4];
		if (n || none)
		{
			vertices[0] = new Vertex(0.0f, -0.5f, -0.5f); uv[0] = new UV(0, 0);
			vertices[1] = new Vertex(0.0f, -0.5f,  0.0f); uv[1] = new UV(0.5f, 0);
			vertices[2] = new Vertex(0.0f,  0.5f,  0.0f); uv[2] = new UV(0.5f, 1);
			vertices[3] = new Vertex(0.0f,  0.5f, -0.5f); uv[3] = new UV(0, 1);
			obj.addFace(vertices, uv, t, materials.get(data)[0]);
		}
		if (s || none)
		{
			vertices[0] = new Vertex(0.0f, -0.5f, 0.0f); uv[0] = new UV(0.5f, 0);
			vertices[1] = new Vertex(0.0f, -0.5f, 0.5f); uv[1] = new UV(1, 0);
			vertices[2] = new Vertex(0.0f,  0.5f, 0.5f); uv[2] = new UV(1, 1);
			vertices[3] = new Vertex(0.0f,  0.5f, 0.0f); uv[3] = new UV(0.5f, 1);
			obj.addFace(vertices, uv, t, materials.get(data)[0]);
		}
		if (e || none)
		{
			vertices[0] = new Vertex(0.5f, -0.5f, 0.0f); uv[0] = new UV(0, 0);
			vertices[1] = new Vertex(0.0f, -0.5f, 0.0f); uv[1] = new UV(0.5f, 0);
			vertices[2] = new Vertex(0.0f,  0.5f, 0.0f); uv[2] = new UV(0.5f, 1);
			vertices[3] = new Vertex(0.5f,  0.5f, 0.0f); uv[3] = new UV(0, 1);
			obj.addFace(vertices, uv, t, materials.get(data)[0]);
		}
		if (w || none)
		{
			vertices[0] = new Vertex( 0.0f, -0.5f, 0.0f); uv[0] = new UV(0.5f, 0);
			vertices[1] = new Vertex(-0.5f, -0.5f, 0.0f); uv[1] = new UV(1, 0);
			vertices[2] = new Vertex(-0.5f,  0.5f, 0.0f); uv[2] = new UV(1, 1);
			vertices[3] = new Vertex( 0.0f,  0.5f, 0.0f); uv[3] = new UV(0.5f, 1);
			obj.addFace(vertices, uv, t, materials.get(data)[0]);
		}
		
	}

}
