package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


public class Composter extends BlockModel
{
	
	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		int level = data.state.getInt("level", 0);
		
		String[] mtls = materials.get(data.state,biome);
		String[] mtls_mainBox = new String [] { mtls[4], mtls[3], mtls[3], mtls[3], mtls[3], mtls[0] };
				
		UV[] uvTop, uvSide, uvCompost;
		UV[][] uvSides;
		
		// Draw the main box of the composter
		addBox(obj, x - 0.5f, y - 0.5f, z - 0.5f, x + 0.5f, y + 0.5f, z + 0.5f, null, mtls_mainBox, null, drawSides(chunks, x, y, z));
		
		// Draw the inner box so it appears thick and 3D
		uvTop = new UV[] { new UV(2/16f, 2/16f), new UV(14/16f, 2/16f), new UV(14/16f, 14/16f), new UV(2/16f, 14/16f) };
		uvSide = new UV[] { new UV(2/16f, 2/16f), new UV(14/16f, 2/16f), new UV(14/16f, 16/16f), new UV(2/16f, 16/16f) };
		uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvTop };
		addBox(obj, x - 6/16f, y - 6/16f, z - 6/16f, x + 6/16f, y + 8/16f, z + 6/16f, null, mtls_mainBox, uvSides, null);
		
		// If the level of compost is between 1 and 7, we'll need to add some compost inside.
		if (level > 0 && level < 8)
		{
			Transform move = new Transform();
			// We're offsetting the move to the bottom of the composter to make it a bit easier to do the math when adding the compost.
			move = Transform.translation(x, y-6/16f, z);
			
			uvCompost = new UV[] { new UV(2/16f, 2/16f), new UV(14/16f, 2/16f), new UV(14/16f, 14/16f), new UV(2/16f, 14/16f) };
			
			Vertex[] vertices = new Vertex[4];
			vertices[0] = new Vertex(6/16f, 2/16f * level,-6/16f);
			vertices[1] = new Vertex(-6/16f,2/16f * level,-6/16f);
			vertices[2] = new Vertex(-6/16f,2/16f * level,6/16f);
			vertices[3] = new Vertex(6/16f,2/16f * level,6/16f);
			obj.addFace(vertices, uvCompost, move, mtls[1]);
		}
		// If the compost level is exactly 8, it uses the compost ready material instead and is in the same position as level 7.
		else if (level == 8)
		{
			Transform move = new Transform();
			move = Transform.translation(x, y+6/16f, z);
			
			uvCompost = new UV[] { new UV(2/16f, 2/16f), new UV(14/16f, 2/16f), new UV(14/16f, 14/16f), new UV(2/16f, 14/16f) };
			
			Vertex[] vertices = new Vertex[4];
			vertices[0] = new Vertex(6/16f, 0,-6/16f);
			vertices[1] = new Vertex(-6/16f,0,-6/16f);
			vertices[2] = new Vertex(-6/16f,0,6/16f);
			vertices[3] = new Vertex(6/16f,0,6/16f);
			obj.addFace(vertices, uvCompost, move, mtls[2]);
		}	
	}
}
