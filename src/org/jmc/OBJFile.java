/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.jmc.MTLFile.Side;

/**
 * OBJ file class.
 * This file contains the geometry of the whole world we are trying to export.
 * It also contains the links to the materials saved in the MTL file.
 * @author danijel
 *
 */
public class OBJFile {
	
	/**
	 * Small enum describing the UV location of the texture for simple rectangular
	 * textures.
	 * @author danijel
	 *
	 */
	public enum TexCoordinate
	{
		TOPLEFT,
		TOPRIGHT,
		BOTTOMRIGHT,
		BOTTOMLEFT
	}
	
	/**
	 * Small enum describing the mesh types.
	 * Currently only blocks are supported.
	 * @author danijel
	 *
	 */
	public enum MeshType
	{
		BLOCK,
		IMAGE,
		TORCH,
		FENCE
		//TODO: complete this
	}
	
	/**
	 * Small internal class for describing Vertices in a sortable fashion.
	 * @author danijel
	 *
	 */
	private class Vertex implements Comparable<Vertex>
	{
		float x,y,z;
		
		/**
		 * Vertex constructor.
		 * @param x x coordinate
		 * @param y y coordinate
		 * @param z z coordinate
		 */
		Vertex(float x, float y, float z)
		{
			this.x=x;
			this.y=y;
			this.z=z;
		}

		/**
		 * Comparator that sorts vertices first along the X, then Y and finally Z axis.
		 */
		@Override
		public int compareTo(Vertex o) {
			if(this.x>o.x) return 1;
			if(this.x<o.x) return -1;
			if(this.y>o.y) return 1;
			if(this.y<o.y) return -1;
			if(this.z>o.z) return 1;
			if(this.z<o.z) return -1;
			return 0;
		}
		
	}
	
	/**
	 * Small internal class for describing a simple rectangular face of an object.
	 * @author danijel
	 *
	 */
	private class Face implements Comparable<Face>
	{
		int [] vertices;
		Side side;
		int mtl_id;
		@Override
		public int compareTo(Face o) {
			return this.mtl_id-o.mtl_id;
		}
	}
	
	/**
	 * Identifier of the file.
	 * Since many OBJ class objects are created by different chunks,
	 * this helps differentiate them and assign them a name. It's
	 * usually just the coordinates of the chunk.
	 */
	String identifier;
	/**
	 * Reference to the MTL file used in this OBJ.
	 */
	MTLFile material;
	/**
	 * List of vertices in the file.
	 */
	List<Vertex> vertices;
	/**
	 * Map of vertices to their respective IDs used in the faces of the mesh.
	 */
	Map<Vertex, Integer> vertex_map;
	/**
	 * List of faces in the file.
	 */
	List<Face> faces;
	
	/**
	 * Offsets of the file. Used to position the chunk in its right location.
	 */
	float x_offset, y_offset, z_offset;
	
	/**
	 * Main constructor.
	 * @param ident identifier of the OBJ
	 * @param mtl reference to the MTL
	 */
	public OBJFile(String ident, MTLFile mtl)
	{
		identifier=ident;
		material=mtl;
		vertices=new LinkedList<Vertex>();
		vertex_map=new TreeMap<Vertex, Integer>();
		faces=new LinkedList<OBJFile.Face>();
		x_offset=0;
		y_offset=0;
		z_offset=0;
	}
	
	/**
	 * Offset all the vertices by these amounts.
	 * Used to position the chunk in its right location.
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 */
	public void setOffset(int x, int y, int z)
	{
		x_offset=x;
		y_offset=y;
		z_offset=z;
	}
	
	/**
	 * Add a cube at the given location. 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @param id block id
	 * @param drawside 6-element array describing which sides are to be drawn
	 */
	public void addCube(float x, float y, float z, int id, boolean [] drawside)
	{
		Vertex vertices[]=new Vertex[4];
		
		if(drawside[0])
		{
			vertices[0]=new Vertex(x-0.5f,y+0.5f,z-0.5f);
			vertices[1]=new Vertex(x-0.5f,y+0.5f,z+0.5f);
			vertices[2]=new Vertex(x+0.5f,y+0.5f,z+0.5f);
			vertices[3]=new Vertex(x+0.5f,y+0.5f,z-0.5f);
			addFace(vertices,Side.TOP,id);
		}
		if(drawside[1])
		{
			vertices[0]=new Vertex(x+0.5f,y-0.5f,z-0.5f);
			vertices[1]=new Vertex(x+0.5f,y-0.5f,z+0.5f);
			vertices[2]=new Vertex(x-0.5f,y-0.5f,z+0.5f);
			vertices[3]=new Vertex(x-0.5f,y-0.5f,z-0.5f);
			addFace(vertices,Side.BOTTOM,id);
		}
		if(drawside[2])
		{
			vertices[0]=new Vertex(x-0.5f,y-0.5f,z+0.5f);
			vertices[1]=new Vertex(x-0.5f,y+0.5f,z+0.5f);
			vertices[2]=new Vertex(x-0.5f,y+0.5f,z-0.5f);
			vertices[3]=new Vertex(x-0.5f,y-0.5f,z-0.5f);
			addFace(vertices,Side.LEFT,id);
		}
		if(drawside[3])
		{
			vertices[0]=new Vertex(x+0.5f,y-0.5f,z-0.5f);
			vertices[1]=new Vertex(x+0.5f,y+0.5f,z-0.5f);
			vertices[2]=new Vertex(x+0.5f,y+0.5f,z+0.5f);
			vertices[3]=new Vertex(x+0.5f,y-0.5f,z+0.5f);
			addFace(vertices,Side.RIGHT,id);
		}
		if(drawside[4])
		{
			vertices[0]=new Vertex(x-0.5f,y-0.5f,z-0.5f);
			vertices[1]=new Vertex(x-0.5f,y+0.5f,z-0.5f);
			vertices[2]=new Vertex(x+0.5f,y+0.5f,z-0.5f);
			vertices[3]=new Vertex(x+0.5f,y-0.5f,z-0.5f);
			addFace(vertices,Side.FRONT,id);
		}
		if(drawside[5])
		{
			vertices[0]=new Vertex(x+0.5f,y-0.5f,z+0.5f);
			vertices[1]=new Vertex(x+0.5f,y+0.5f,z+0.5f);
			vertices[2]=new Vertex(x-0.5f,y+0.5f,z+0.5f);
			vertices[3]=new Vertex(x-0.5f,y-0.5f,z+0.5f);
			addFace(vertices,Side.BACK,id);
		}
	}

	/**
	 * Append this object to the OBJ file.
	 * @param out writer of the OBJ file
	 */
	public void append(PrintWriter out)
	{
		Locale l=null;
		out.println("g "+identifier);
		out.println();
		
		for(Vertex vertex:vertices)
		{
			out.format(l,"v %2.2f %2.2f %2.2f",vertex.x+x_offset,vertex.y+y_offset,vertex.z+z_offset);
			out.println();
		}
		
		printTexturesAndNormals(out);
		
		Collections.sort(faces);
		int last_id=-2;	
		int normal_idx;
		int vertices_num=vertices.size();
		for(Face f:faces)
		{
			if(f.mtl_id<0) continue; //TODO: temporary modification - skip unknown materials  
			
			if(f.mtl_id!=last_id)
			{
				out.println();
				out.println("usemtl "+material.getMaterial(f.mtl_id));
				last_id=f.mtl_id;
			}
			
			normal_idx=sideToNormalIndex(f.side);
			
			out.print("f ");
			out.format(l,"%d/-4/%d ",(-vertices_num+f.vertices[0]),normal_idx);
			out.format(l,"%d/-3/%d ",(-vertices_num+f.vertices[1]),normal_idx);
			out.format(l,"%d/-2/%d ",(-vertices_num+f.vertices[2]),normal_idx);
			out.format(l,"%d/-1/%d ",(-vertices_num+f.vertices[3]),normal_idx);
			out.println();
		}				
	}
	
	/**
	 * Write texture coordinates and normals. These are usually the same for all chunks.
	 * @param out writer of the OBJ file
	 */
	private void printTexturesAndNormals(PrintWriter out)
	{
		out.println("vt 0 0");
		out.println("vt 1 0");
		out.println("vt 1 1");
		out.println("vt 0 1");
		out.println("vn 0 -1 0");
		out.println("vn 0 1 0");
		out.println("vn 1 0 0");
		out.println("vn -1 0 0");
		out.println("vn 0 0 1");
		out.println("vn 0 1 0");
		//TODO: finish printing normals
		
	}
	
	/**
	 * Converts the side to the normal index as printed by the printTexturesAndNormals method
	 * @param side side of the block
	 * @return index of the normal
	 */
	private int sideToNormalIndex(Side side)
	{
		switch(side)
		{
		case BOTTOM: return -6;
		case TOP: return -5;
		case RIGHT: return -4;
		case LEFT: return -3;
		case BACK: return -2;
		case FRONT: return -1;
		default: return -1;
		}
	}
	
	/**
	 * Add a face with the given vertices to the appropriate lists.
	 * Also create vertices if necessary.
	 * @param verts vertices of the face
	 * @param side side of the object
	 * @param id block id
	 */
	private void addFace(Vertex [] verts, Side side, int id)
	{
		Face face=new Face();
		face.side=side;
		face.mtl_id=material.getMaterialId(id, side);
		face.vertices=new int[4];
		for(int i=0; i<4; i++)
		{
			if(!vertex_map.containsKey(verts[i]))				
			{
				vertices.add(verts[i]);
				vertex_map.put(verts[i], vertices.size()-1);				
			}
			face.vertices[i]=vertex_map.get(verts[i]);
		}
		
		faces.add(face);
	}

}
