package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Direction;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


public class Campfire extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{

		NamespaceID[] mtls = getMtlSides(data, biome);
		NamespaceID[] mtls_LogsBottom, mtls_LogsTop, mtls_Ash;
		
		mtls_LogsBottom = new NamespaceID [] { mtls[1], mtls[1], mtls[1], mtls[1], mtls[1], mtls[1] };	
		
		if (data.state.getBool("lit", false)) {
			mtls_LogsTop = new NamespaceID [] { mtls[1], mtls[2], mtls[2], mtls[1], mtls[1], mtls[1] };
			mtls_Ash = new NamespaceID [] { mtls[2], mtls[2], mtls[2], mtls[2], mtls[2], mtls[2] };
		} else {
			mtls_LogsTop = mtls_LogsBottom;
			mtls_Ash = new NamespaceID [] { mtls[1], mtls[1], mtls[1], mtls[1], mtls[1], mtls[1] };
		}
			
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;
		
		UV[] uvTop, uvSide, uvSide2;
		UV[][] uvSides;
		boolean[] drawSides;
		
		Direction dir = data.state.getDirection("facing", Direction.SOUTH);
		
		switch (dir)
		{
			case NORTH: rotate = Transform.rotation(0, 180, 0); break;
			default:
			case SOUTH: rotate = Transform.rotation(0, 0, 0); break;
			case WEST: rotate = Transform.rotation(0, 90, 0); break;
			case EAST: rotate = Transform.rotation(0, -90, 0); break;
		}
		translate = Transform.translation(x, y, z);
		rt = translate.multiply(rotate);
		
		// Bottom two logs
		uvSide = new UV[] { new UV(0, 12/16f), new UV(16/16f, 12/16f), new UV(16/16f, 16/16f), new UV(0, 16/16f) };
		uvTop = new UV[] { new UV(16/16f, 12/16f), new UV(16/16f, 16/16f), new UV(0, 16/16f), new UV(0, 12/16f) };
		uvSide2 = new UV[] { new UV(0, 8/16f), new UV(4/16f, 8/16f), new UV(4/16f, 12/16f), new UV(0, 12/16f) };
		uvSides = new UV[][] { uvTop, uvSide2, uvSide2, uvSide, uvSide, uvTop };
		addBox(obj,	-7/16f, -8/16f, -8/16f, -3/16f, -4/16f, 8/16f, rt, mtls_LogsBottom, uvSides, null);
		addBox(obj,	3/16f, -8/16f, -8/16f, 7/16f, -4/16f, 8/16f, rt, mtls_LogsBottom, uvSides, null);
		
		// Top two logs
		uvTop = new UV[] { new UV(0, 12/16f), new UV(16/16f, 12/16f), new UV(16/16f, 16/16f), new UV(0, 16/16f) };
		uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide2, uvSide2, uvTop };
		addBox(obj,	8/16f, -5/16f, -3/16f, -8/16f, -1/16f, -7/16f, rt, mtls_LogsTop, uvSides, null);
		addBox(obj,	8/16f, -5/16f, 7/16f, -8/16f, -1/16f, 3/16f, rt, mtls_LogsTop, uvSides, null);
		
		// Fire cross
		if (data.state.getBool("lit", false))
		{
			Transform move = new Transform();
			move = Transform.translation(x, y, z);
			
			Vertex[] vertices = new Vertex[4];
			vertices[0] = new Vertex(+0.5f,-0.5f,-0.5f);
			vertices[1] = new Vertex(-0.5f,-0.5f,+0.5f);
			vertices[2] = new Vertex(-0.5f,+0.5f,+0.5f);
			vertices[3] = new Vertex(+0.5f,+0.5f,-0.5f);
			obj.addFace(vertices, null, move, mtls[0]);
			
			vertices[0] = new Vertex(-0.5f,-0.5f,-0.5f);
			vertices[1] = new Vertex(+0.5f,-0.5f,+0.5f);
			vertices[2] = new Vertex(+0.5f,+0.5f,+0.5f);
			vertices[3] = new Vertex(-0.5f,+0.5f,-0.5f);
			obj.addFace(vertices, null, move, mtls[0]);
		}
		
		// Ashes
		drawSides = new boolean[] {true,true,true,false,false,false};
		uvSide = new UV[] { new UV(0, 0), new UV(6/16f, 0), new UV(6/16f, 1/16f), new UV(0, 1/16f) };
		uvSide2 = new UV[] { new UV(10/16f, 0), new UV(16/16f, 0), new UV(16/16f, 1/16f), new UV(10/16f, 1/16f) };
		uvTop = new UV[] { new UV(16/16f, 2/16f), new UV(16/16f, 8/16f), new UV(0, 8/16f), new UV(0, 2/16f) };
		uvSides = new UV[][] { uvTop, uvSide, uvSide2, null, null, null };
		addBox(obj,	-3/16f, -8/16f, -8/16f, 3/16f, -7/16f, 8/16f, rt, mtls_Ash, uvSides, drawSides);
	}
}
