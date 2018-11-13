package org.jmc.models;

import java.util.HashMap;
import java.util.Random;

import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for Buttons.
 */
public class SeaPickle extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		// The amount of eggs (1-4)
		int pickles = Integer.parseInt(data.get("pickles"));
				
		Transform rotate = new Transform();
		Transform translate = new Transform();
		Transform rt;

        // Generates a random number between 0 and 3 inclusively. 
        Random r = new Random();
        int randomRotation = r.nextInt(4);		
		
        // Creates a rotation such that we will use to randomly rotate the pickle(s) with. 
		switch (randomRotation)
		{
			case 0:
				rotate.rotate(0, 0, 0);
				break;
			case 1:
				rotate.rotate(0, 90, 0);
				break;
			case 2:
				rotate.rotate(0, 180, 0);
				break;
			case 3:
				rotate.rotate(0, 270, 0);
				break;
		}
		
		
		translate.translate(x, y, z);			
		rt = translate.multiply(rotate);
		

		
		if (pickles == 1)
		{
			newPickle(obj, chunks, 0, 0, 6, rt, data, biome);
		} 
		else if (pickles == 2) 
		{
			newPickle(obj, chunks, 3, 3, 6, rt, data, biome);
			newPickle(obj, chunks, -2, -2, 4, rt, data, biome);
		} 
		else if (pickles == 3) 
		{
			newPickle(obj, chunks, -2, -2, 6, rt, data, biome);
			newPickle(obj, chunks, 4, -4, 4, rt, data, biome);
			newPickle(obj, chunks, 0, 3, 6, rt, data, biome);			
		} 
		else 
		{
			newPickle(obj, chunks, -4, 4, 6, rt, data, biome);
			newPickle(obj, chunks, 3, -4, 4, rt, data, biome);
			newPickle(obj, chunks, 3, 4, 6, rt, data, biome);
			newPickle(obj, chunks, -4, -2, 7, rt, data, biome);
		}
	}
	
	private void newPickle(ChunkProcessor obj, ThreadChunkDeligate chunks, float x, float z, float height, Transform rt, HashMap<String, String> data, int biome)
	{
		boolean[] drawSides = new boolean[] {true,true,true,true,true,false};
		UV[] uvTop, uvSide, uvTopInner;
		UV[][] uvSides;
		Vertex[] vertices = new Vertex[4];		
		
		uvTop = new UV[] { new UV(4/16f,11/16f), new UV(4/16f,15/16f), new UV(8/16f,15/16f), new UV(8/16f,11/16f) }; // The very top of the pickle
		uvTopInner = new UV[] { new UV(8/16f,11/16f), new UV(8/16f,15/16f), new UV(12/16f,15/16f), new UV(12/16f,11/16f) }; // The inside of the top of the pickle
		uvSide = new UV[] { new UV(4/16f,(11-height)/16f) , new UV(0,(11-height)/16f), new UV(0,11/16f), new UV(4/16f,11/16f)};
		uvSides = new UV[][] { uvTop, uvSide, uvSide, uvSide, uvSide, uvSide };
		addBox(obj, (x/16f)-(2/16f),-8/16f, (z/16f)-(2/16f), (x/16f)+(2/16f), (height-8)/16f, (z/16f)+(2/16f), rt, getMtlSides(data, biome), uvSides, drawSides);
		
		vertices[0] = new Vertex((x/16f)-(2/16f), ((height-8)/16f)-.01f, (z/16f)+(2/16f));
		vertices[1] = new Vertex((x/16f)+(2/16f), ((height-8)/16f)-.01f, (z/16f)+(2/16f));
		vertices[2] = new Vertex((x/16f)+(2/16f), ((height-8)/16f)-.01f, (z/16f)-(2/16f));
		vertices[3] = new Vertex((x/16f)-(2/16f), ((height-8)/16f)-.01f, (z/16f)-(2/16f));
		obj.addFace(vertices, uvTopInner, rt, materials.get(data,biome)[0]);	
		
		// If underwater, add the stem
		if(data.get("waterlogged").equals("true")) 
		{
			UV[] uvStem1, uvStem2; // UV's for the two cross-shaped stem planes.
			uvStem1 = new UV[] { new UV(0,13/16f), new UV(0,1), new UV(4/16f,1), new UV(4/16f,13/16f) };
			uvStem2 = new UV[] { new UV(12/16f,13/16f), new UV(12/16f,1), new UV(1,1), new UV(1,13/16f) };
			
			vertices[0] = new Vertex((x/16f)+(1/16f), ((height-8)/16f)-.01f, (z/16f)+(1/16f));
			vertices[1] = new Vertex((x/16f)+(1/16f), ((height-8)/16f)+2/16f, (z/16f)+(1/16f));
			vertices[2] = new Vertex((x/16f)-(1/16f), ((height-8)/16f)+2/16f, (z/16f)-(1/16f));
			vertices[3] = new Vertex((x/16f)-(1/16f), ((height-8)/16f)-.01f, (z/16f)-(1/16f));
			obj.addFace(vertices, uvStem1, rt, materials.get(data,biome)[0]);
			
			vertices[0] = new Vertex((x/16f)-(1/16f), ((height-8)/16f)-.01f, (z/16f)+(1/16f));
			vertices[1] = new Vertex((x/16f)-(1/16f), ((height-8)/16f)+2/16f, (z/16f)+(1/16f));
			vertices[2] = new Vertex((x/16f)+(1/16f), ((height-8)/16f)+2/16f, (z/16f)-(1/16f));
			vertices[3] = new Vertex((x/16f)+(1/16f), ((height-8)/16f)-.01f, (z/16f)-(1/16f));
			obj.addFace(vertices, uvStem2, rt, materials.get(data,biome)[0]);
		}
	}

}
