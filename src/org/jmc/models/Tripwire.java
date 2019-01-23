package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for tripwires
 */
public class Tripwire extends BlockModel
{

	private boolean isConnectable(String otherBlockId)
	{
		return otherBlockId.equals(blockId) || otherBlockId.equals("minecraft:tripwire_hook");
	}
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		String mtl = materials.get(data,biome)[0];
		
		boolean active = data.get("powered").equals("true");
		
		
		/////////////////////
		// For some reason none of these seem to be working! Need to look at them some more
		/////////////////////
		boolean conn_n = isConnectable(chunks.getBlockID(x, y, z-1));
		boolean conn_s = isConnectable(chunks.getBlockID(x, y, z+1));
		boolean conn_w = isConnectable(chunks.getBlockID(x-1, y, z));
		boolean conn_e = isConnectable(chunks.getBlockID(x+1, y, z));

		if (!(conn_n || conn_s || conn_w || conn_e))
			conn_n = conn_s = true;
		
		
		Vertex[] vertices = new Vertex[4];
		UV[] uv_ns, uv_we;
		
		if (active)
		{
			uv_ns = new UV[] { new UV(0,14/16f), new UV(0,12/16f), new UV(2,12/16f), new UV(2,14/16f) };
			uv_we = new UV[] { new UV(0,12/16f), new UV(2,12/16f), new UV(2,14/16f), new UV(0,14/16f) };
		}
		else
		{
			uv_ns = new UV[] { new UV(0,1), new UV(0,14/16f), new UV(2,14/16f), new UV(2,1) };
			uv_we = new UV[] { new UV(0,14/16f), new UV(2,14/16f), new UV(2,1), new UV(0,1) };
		}
		
		if (conn_n)
		{
			vertices[0] = new Vertex(x-0.0156f, y-0.4375f, z+0.0156f);
			vertices[1] = new Vertex(x+0.0156f, y-0.4375f, z+0.0156f);
			vertices[2] = new Vertex(x+0.0156f, y-0.4375f, z-0.5f);
			vertices[3] = new Vertex(x-0.0156f, y-0.4375f, z-0.5f);
			obj.addFace(vertices, uv_ns, null, mtl);
		}
		if (conn_s)
		{
			vertices[0] = new Vertex(x-0.0156f, y-0.4375f, z+0.5f);
			vertices[1] = new Vertex(x+0.0156f, y-0.4375f, z+0.5f);
			vertices[2] = new Vertex(x+0.0156f, y-0.4375f, z-0.0156f);			
			vertices[3] = new Vertex(x-0.0156f, y-0.4375f, z-0.0156f);
			obj.addFace(vertices, uv_ns, null, mtl);
		}
		if (conn_w)
		{
			vertices[0] = new Vertex(x-0.5f, y-0.4375f, z+0.0156f);
			vertices[1] = new Vertex(x+0.0156f, y-0.4375f, z+0.0156f);
			vertices[2] = new Vertex(x+0.0156f, y-0.4375f, z-0.0156f);
			vertices[3] = new Vertex(x-0.5f, y-0.4375f, z-0.0156f);
			obj.addFace(vertices, uv_we, null, mtl);
		}
		if (conn_e)
		{
			vertices[0] = new Vertex(x-0.0156f, y-0.4375f, z+0.0156f);
			vertices[1] = new Vertex(x+0.5f, y-0.4375f, z+0.0156f);
			vertices[2] = new Vertex(x+0.5f, y-0.4375f, z-0.0156f);
			vertices[3] = new Vertex(x-0.0156f, y-0.4375f, z-0.0156f);
			obj.addFace(vertices, uv_we, null, mtl);
		}
	}

}
