package org.jmc;

import java.util.ArrayList;
import java.util.List;

import org.jmc.geom.UV;
import org.jmc.geom.Vertex;


/**
 * Common base for OBJInputFile and OBJOutputFile.
 */
public abstract class OBJFileBase
{
	/**
	 * Describes a face in the OBJ file.
	 * Faces can be sorted by material.
	 */
	protected static class Face implements Comparable<Face>
	{
		public int[] vertices;
		public int[] normals;
		public int[] uv;
		public String mtl;
		public Long obj_idx;
		
		public Face(int sides)
		{
			obj_idx=Long.valueOf(-1);
			vertices=new int[sides];
			normals=new int[sides];
			uv=new int[sides];
		}
		
		@Override
		public int compareTo(Face o) {
			if(this.obj_idx!=o.obj_idx)
				return this.obj_idx.compareTo(o.obj_idx);
			return this.mtl.compareTo(o.mtl);
		}
	}

	
	/**
	 * List of vertices in the file.
	 */
	protected List<Vertex> vertices;
	/**
	 * List of texture coordinates in the file
	 */
	protected List<UV> texCoords;
	/**
	 * List of normals
	 */
	protected List<Vertex> normals;
	/**
	 * List of faces
	 */
	protected List<Face> faces;

	
	protected OBJFileBase()
	{
		vertices = new ArrayList<Vertex>();
		texCoords = new ArrayList<UV>();
		normals = new ArrayList<Vertex>();
		faces = new ArrayList<Face>();
	}

	
}
