package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.threading.ObjChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for vines.
 */
public class Vines extends BlockModel
{

	@Override
	public void addModel(ObjChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, NamespaceID biome)
	{
		boolean top = data.state.get("up").equals("true");
		boolean n,e,s,w;
		n = e = w = s = false;
		
		if (data.state.get("north").equals("true")) {
			n = true;
		}
		if (data.state.get("south").equals("true")) {
			s = true;
		}
		if (data.state.get("east").equals("true")) {
			e = true;
		}
		if (data.state.get("west").equals("true")) {
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
			trans = Transform.translation(x, y, z);		
			obj.addFace(vertices, null, trans, materials.get(data.state,biome)[0]);
		}
		if (s)
		{
			rot = Transform.rotation(0, 180, 0);
			trans = Transform.translation(x, y, z);		
			obj.addFace(vertices, null, trans.multiply(rot), materials.get(data.state,biome)[0]);
		}
		if (e)
		{
			rot = Transform.rotation(0, 90, 0);
			trans = Transform.translation(x, y, z);		
			obj.addFace(vertices, null, trans.multiply(rot), materials.get(data.state,biome)[0]);
		}
		if (w)
		{
			rot = Transform.rotation(0, -90, 0);
			trans = Transform.translation(x, y, z);		
			obj.addFace(vertices, null, trans.multiply(rot), materials.get(data.state,biome)[0]);
		}
		if (top)
		{
			rot = Transform.rotation(90, 0, 0);
			trans = Transform.translation(x, y, z);		
			obj.addFace(vertices, null, trans.multiply(rot), materials.get(data.state,biome)[0]);
		}
			
		
	}

}
