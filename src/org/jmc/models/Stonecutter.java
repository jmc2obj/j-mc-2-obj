package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


public class Stonecutter extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		String[] mtls_bottom = new String [] { materials.get(data,biome)[3], materials.get(data,biome)[2], materials.get(data,biome)[2], materials.get(data,biome)[2], materials.get(data,biome)[2], materials.get(data,biome)[0] };	
		
		Transform rotate = Transform.rotation(data.state.getDirection("facing", Direction.NORTH));
		Transform translate = Transform.translation(x, y, z);
		Transform rt = translate.multiply(rotate);

        UV[] uvSide = new UV[] { new UV(0,0), new UV(1,0), new UV(1,9/16f), new UV(0,9/16f) };
        UV[][] uvSides = new UV[][] { null, uvSide, uvSide, uvSide, uvSide, null };
        
		addBox(obj,	-0.5f, -8/16f, -0.5f, 0.5f, 1/16f, 0.5f, rt, mtls_bottom, uvSides, null);
		
		Vertex[] vertices = new Vertex[4];
		UV[] uv = { new UV(0, 0), new UV(1, 0), new UV(1, 7/16f), new UV(0, 7/16f) };
		vertices[0] = new Vertex(-0.5f,1/16f,0);
		vertices[1] = new Vertex(+0.5f,1/16f,0);
		vertices[2] = new Vertex(+0.5f,8/16f,0);
		vertices[3] = new Vertex(-0.5f,8/16f,0);
		obj.addFace(vertices, uv, rt, materials.get(data,biome)[1]);		
	}
}
