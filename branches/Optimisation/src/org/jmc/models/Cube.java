package org.jmc.models;

import java.util.ArrayList;
import java.util.HashMap;

import org.jmc.ChunkDataBuffer;
import org.jmc.OBJOutputFile;
import org.jmc.geom.FaceUtils.Face;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;


/**
 * Generic model for cube blocks.
 */
public class Cube extends BlockModel
{

	@Override
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome)
	{
		addBox(obj,
				x - 0.5f, y - 0.5f, z - 0.5f,
				x + 0.5f, y + 0.5f, z + 0.5f, 
				null, 
				getMtlSides(data,biome), 
				null, 
				drawSides(chunks, x, y, z));
	}
	
	public void addModel(OBJOutputFile obj, ChunkDataBuffer chunks, int x, int y, int z, byte data, byte biome, HashMap<String, ArrayList<Face>> faceAxisArray){
		//mmdanggg2: Add the faces to the face array dependent on the direction they face.
		Vertex[] vertices = new Vertex[4];
		float xs = x - 0.5f;
		float ys = y - 0.5f;
		float zs = z - 0.5f;
		float xe = x + 0.5f;
		float ye = y + 0.5f;
		float ze = z + 0.5f;
		String[] mtlSides = getMtlSides(data, biome);
		boolean[] drawSides = drawSides(chunks, x, y, z);
		
		if (drawSides == null || drawSides[0]) { // top
			vertices[0] = new Vertex(xs, ye, ze);
			vertices[1] = new Vertex(xe, ye, ze);
			vertices[2] = new Vertex(xe, ye, zs);
			vertices[3] = new Vertex(xs, ye, zs);
			String key = "Y+ " + y;//mmdanggg2: use a key to separate planar faces into arrays
			ArrayList<Face> faceList = faceAxisArray.getOrDefault(key, new ArrayList<Face>());
			UV[] uv = new UV[] {new UV(0,0), new UV(1,0), new UV(1,1), new UV(0,1)};
			Face face = new Face(vertices, uv, 0, mtlSides[0]);
			faceList.add(face);
			faceAxisArray.put(key, faceList);
		}
		if (drawSides == null || drawSides[1]) { // front
			vertices[0] = new Vertex(xe, ys, zs);
			vertices[1] = new Vertex(xs, ys, zs);
			vertices[2] = new Vertex(xs, ye, zs);
			vertices[3] = new Vertex(xe, ye, zs);
			String key = "Z+ " + z;
			ArrayList<Face> faceList = faceAxisArray.getOrDefault(key, new ArrayList<Face>());
			UV[] uv = new UV[] {new UV(0,0), new UV(1,0), new UV(1,1), new UV(0,1)};
			Face face = new Face(vertices, uv, 0, mtlSides[1]);
			faceList.add(face);
			faceAxisArray.put(key, faceList);
		}
		if (drawSides == null || drawSides[2]) { // back
			vertices[0] = new Vertex(xs, ys, ze);
			vertices[1] = new Vertex(xe, ys, ze);
			vertices[2] = new Vertex(xe, ye, ze);
			vertices[3] = new Vertex(xs, ye, ze);
			String key = "Z- " + z;
			ArrayList<Face> faceList = faceAxisArray.getOrDefault(key, new ArrayList<Face>());
			UV[] uv = new UV[] {new UV(0,0), new UV(1,0), new UV(1,1), new UV(0,1)};
			Face face = new Face(vertices, uv, 0, mtlSides[2]);
			faceList.add(face);
			faceAxisArray.put(key, faceList);
		}
		if (drawSides == null || drawSides[3]) { // left
			vertices[0] = new Vertex(xs, ys, zs);
			vertices[1] = new Vertex(xs, ys, ze);
			vertices[2] = new Vertex(xs, ye, ze);
			vertices[3] = new Vertex(xs, ye, zs);
			String key = "X+ " + x;
			ArrayList<Face> faceList = faceAxisArray.getOrDefault(key, new ArrayList<Face>());
			UV[] uv = new UV[] {new UV(0,0), new UV(1,0), new UV(1,1), new UV(0,1)};
			Face face = new Face(vertices, uv, 0, mtlSides[3]);
			faceList.add(face);
			faceAxisArray.put(key, faceList);
		}
		if (drawSides == null || drawSides[4]) { // right
			vertices[0] = new Vertex(xe, ys, ze);
			vertices[1] = new Vertex(xe, ys, zs);
			vertices[2] = new Vertex(xe, ye, zs);
			vertices[3] = new Vertex(xe, ye, ze);
			String key = "X- " + x;
			ArrayList<Face> faceList = faceAxisArray.getOrDefault(key, new ArrayList<Face>());
			UV[] uv = new UV[] {new UV(0,0), new UV(1,0), new UV(1,1), new UV(0,1)};
			Face face = new Face(vertices, uv, 0, mtlSides[4]);
			faceList.add(face);
			faceAxisArray.put(key, faceList);
		}
		if (drawSides == null || drawSides[5]) { // bottom
			vertices[0] = new Vertex(xe, ys, ze);
			vertices[1] = new Vertex(xs, ys, ze);
			vertices[2] = new Vertex(xs, ys, zs);
			vertices[3] = new Vertex(xe, ys, zs);
			String key = "Y- " + y;
			ArrayList<Face> faceList = faceAxisArray.getOrDefault(key, new ArrayList<Face>());
			UV[] uv = new UV[] {new UV(0,0), new UV(1,0), new UV(1,1), new UV(0,1)};
			Face face = new Face(vertices, uv, 0, mtlSides[5]);
			faceList.add(face);
			faceAxisArray.put(key, faceList);
		}
	}

}
