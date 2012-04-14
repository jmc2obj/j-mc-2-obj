package org.jmc.geom;

/**
 * Small class for describing a simple rectangular face of an object.
 * Faces can be sorted by material.
 */
public class Face implements Comparable<Face>
{
	public int[] vertices;
	public int[] normals;
	public int[] uv;
	public int mtl;
	
	public Face()
	{
		vertices=new int[4];
		normals=new int[4];
		uv=new int[4];
	}
	
	@Override
	public int compareTo(Face o) {
		return this.mtl-o.mtl;
	}
}
