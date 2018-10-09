package org.jmc.models;

import java.util.HashMap;

import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for vines.
 */
public class Vines extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, HashMap<String, String> data, int biome)
	{
		boolean top = data.get("up").equals("true");
		boolean n,e,s,w;
		n = e = w = s = false;
		
		if (data.get("north").equals("true")) {
			n = true;
		}
		if (data.get("south").equals("true")) {
			s = true;
		}
		if (data.get("east").equals("true")) {
			e = true;
		}
		if (data.get("west").equals("true")) {
			w = true;
		}


		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(-0.5f, -0.5f, -0.47f);
		vertices[1] = new Vertex( 0.5f, -0.5f, -0.47f);
		vertices[2] = new Vertex( 0.5f,  0.5f, -0.47f);			
		vertices[3] = new Vertex(-0.5f,  0.5f, -0.47f);

		Transform rot = new Transform();
		Transform trans = new Transform();
		
		if (n)
		{
			trans.translate(x, y, z);		
			obj.addFace(vertices, null, trans, materials.get(data,biome)[0]);
		}
		if (s)
		{
			rot.rotate(0, 180, 0);
			trans.translate(x, y, z);		
			obj.addFace(vertices, null, trans.multiply(rot), materials.get(data,biome)[0]);
		}
		if (e)
		{
			rot.rotate(0, 90, 0);
			trans.translate(x, y, z);		
			obj.addFace(vertices, null, trans.multiply(rot), materials.get(data,biome)[0]);
		}
		if (w)
		{
			rot.rotate(0, -90, 0);
			trans.translate(x, y, z);		
			obj.addFace(vertices, null, trans.multiply(rot), materials.get(data,biome)[0]);
		}
		if (top)
		{
			rot.rotate(90, 0, 0);
			trans.translate(x, y, z);		
			obj.addFace(vertices, null, trans.multiply(rot), materials.get(data,biome)[0]);
		}
			
		
	}

}
