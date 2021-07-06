package org.jmc.models;

import org.jmc.BlockData;
import org.jmc.geom.Transform;
import org.jmc.geom.Vertex;
import org.jmc.threading.ChunkProcessor;
import org.jmc.threading.ThreadChunkDeligate;


/**
 * Model for fire
 */
public class Fire extends BlockModel
{

	@Override
	public void addModel(ChunkProcessor obj, ThreadChunkDeligate chunks, int x, int y, int z, BlockData data, int biome)
	{
		String[] mtlSides = getMtlSides(data,biome);
		
		Transform t = Transform.translation(x, y, z);
		
		boolean north = data.state.getBool("north", false);
		boolean south = data.state.getBool("south", false);
		boolean east = data.state.getBool("east", false);
		boolean west = data.state.getBool("west", false);
		boolean up = data.state.getBool("up", false);
		boolean none = !(north || south || east || west || up);
		
		if (none) {
			addFloor(obj, t, mtlSides[0]);
		}
		if (north || none) {
			addSide(obj, t, mtlSides[0], 0);
		}
		if (east || none) {
			addSide(obj, t, mtlSides[0], 90);
		}
		if (south || none) {
			addSide(obj, t, mtlSides[0], 180);
		}
		if (west || none) {
			addSide(obj, t, mtlSides[0], -90);
		}
		if (up) {
			addUp(obj, t, mtlSides[0]);
		}
	}
	
	private void addSide(ChunkProcessor obj, Transform baseT, String material, float yaw) {
		Transform rot = Transform.rotation(0, yaw, 0);
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(-0.5f, -0.5f, -0.5f);
		vertices[1] = new Vertex( 0.5f, -0.5f, -0.5f);
		vertices[2] = new Vertex( 0.45f,  0.6f, -0.45f);
		vertices[3] = new Vertex(-0.45f,  0.6f, -0.45f);
		obj.addFace(vertices, null, baseT.multiply(rot), material);
	}
	
	private void addFloor(ChunkProcessor obj, Transform baseT, String material) {
		Transform rot = new Transform();
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex( 0.49f, -0.5f, -0.25f);
		vertices[1] = new Vertex(-0.49f, -0.5f, -0.25f);
		vertices[2] = new Vertex(-0.44f,  0.75f, 0.25f);
		vertices[3] = new Vertex( 0.44f,  0.75f, 0.25f);
		obj.addFace(vertices, null, baseT.multiply(rot), material);
		rot = Transform.rotation(0, 90, 0);
		obj.addFace(vertices, null, baseT.multiply(rot), material);
		rot = Transform.rotation(0, 180, 0);
		obj.addFace(vertices, null, baseT.multiply(rot), material);
		rot = Transform.rotation(0, -90, 0);
		obj.addFace(vertices, null, baseT.multiply(rot), material);
	}

	private void addUp(ChunkProcessor obj, Transform baseT, String material) {
		Transform rot = new Transform();
		Vertex[] vertices = new Vertex[4];
		vertices[0] = new Vertex(-0.5f, 0.5f,  0.49f);
		vertices[1] = new Vertex(-0.5f, 0.5f, -0.49f);
		vertices[2] = new Vertex( 0.5f, 0.1f, -0.44f);
		vertices[3] = new Vertex( 0.5f, 0.1f,  0.44f);
		obj.addFace(vertices, null, baseT.multiply(rot), material);
		rot = Transform.rotation(0, 180, 0);
		obj.addFace(vertices, null, baseT.multiply(rot), material);
	}

	/*/ front
	vertices[0] = new Vertex( 0.5f, -0.5f, -0.5f);
	vertices[1] = new Vertex(-0.5f, -0.5f, -0.5f);
	vertices[2] = new Vertex(-0.45f,  0.6f, -0.45f);
	vertices[3] = new Vertex( 0.45f,  0.6f, -0.45f);
	obj.addFace(vertices, null, t, mtlSides[1]);

	vertices[0] = new Vertex( 0.49f, -0.5f, -0.25f);
	vertices[1] = new Vertex(-0.49f, -0.5f, -0.25f);
	vertices[2] = new Vertex(-0.44f,  0.75f, 0.25f);
	vertices[3] = new Vertex( 0.44f,  0.75f, 0.25f);
	obj.addFace(vertices, null, t, mtlSides[3]);

	// back
	vertices[0] = new Vertex(-0.5f, -0.5f,  0.5f);
	vertices[1] = new Vertex( 0.5f, -0.5f,  0.5f);
	vertices[2] = new Vertex( 0.45f,  0.6f,  0.45f);
	vertices[3] = new Vertex(-0.45f,  0.6f,  0.45f);
	obj.addFace(vertices, null, t, mtlSides[2]);

	vertices[0] = new Vertex(-0.49f, -0.5f,  0.25f);
	vertices[1] = new Vertex( 0.49f, -0.5f,  0.25f);
	vertices[2] = new Vertex( 0.44f,  0.75f, -0.25f);
	vertices[3] = new Vertex(-0.44f,  0.75f, -0.25f);
	obj.addFace(vertices, null, t, mtlSides[4]);

	// left
	vertices[0] = new Vertex(-0.5f, -0.5f, -0.5f);
	vertices[1] = new Vertex(-0.5f, -0.5f,  0.5f);
	vertices[2] = new Vertex(-0.45f,  0.6f,  0.45f);
	vertices[3] = new Vertex(-0.45f,  0.6f, -0.45f);
	obj.addFace(vertices, null, t, mtlSides[3]);

	vertices[0] = new Vertex(-0.25f, -0.5f, -0.49f);
	vertices[1] = new Vertex(-0.25f, -0.5f,  0.49f);
	vertices[2] = new Vertex( 0.25f,  0.75f,  0.44f);
	vertices[3] = new Vertex( 0.25f,  0.75f, -0.44f);
	obj.addFace(vertices, null, t, mtlSides[1]);

	// right
	vertices[0] = new Vertex( 0.5f, -0.5f,  0.5f);
	vertices[1] = new Vertex( 0.5f, -0.5f, -0.5f);
	vertices[2] = new Vertex( 0.45f,  0.6f, -0.45f);
	vertices[3] = new Vertex( 0.45f,  0.6f,  0.45f);
	obj.addFace(vertices, null, t, mtlSides[4]);

	vertices[0] = new Vertex( 0.25f, -0.5f,  0.49f);
	vertices[1] = new Vertex( 0.25f, -0.5f, -0.49f);
	vertices[2] = new Vertex(-0.25f,  0.75f, -0.44f);
	vertices[3] = new Vertex(-0.25f,  0.75f,  0.44f);
	obj.addFace(vertices, null, t, mtlSides[2]);
	*/
}
