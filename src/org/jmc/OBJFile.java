/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.Rectangle;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;


/**
 * OBJ file class.
 * This file contains the geometry of the whole world we are trying to export.
 * It also contains the links to the materials saved in the MTL file.
 * @author danijel
 *
 */
public class OBJFile
{

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
	 * A small enum for describing the sides of a cube.
	 * @author danijel
	 *
	 */
	public enum Side
	{
		TOP,
		BOTTOM,
		LEFT,
		RIGHT,
		FRONT,
		BACK,
		BACKRIGHT,
		FRONTRIGHT
	}

	/**
	 * Small internal class for describing a simple rectangular face of an object.
	 * @author danijel
	 *
	 */
	private class Face implements Comparable<Face>
	{
		int[] vertices;
		Side side;
		String mtl;
		@Override
		public int compareTo(Face o) {
			return this.mtl.compareTo(o.mtl);
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
	 * List of vertices in the file.
	 */
	List<Vertex> vertices;

	/**
	 * Map of vertices to their respective IDs used in the faces of the mesh.
	 */
	Map<Vertex, Integer> vertex_map;
	
	private int vertex_counter;
	
	/**
	 * List of faces in the file.
	 */
	List<Face> faces;

	/**
	 * Offsets of the file. Used to position the chunk in its right location.
	 */
	float x_offset, y_offset, z_offset;

	float file_scale;

	/**
	 * Main constructor.
	 * @param ident identifier of the OBJ
	 * @param mtl reference to the MTL
	 */
	public OBJFile(String ident)
	{
		identifier=ident;
		vertices=new LinkedList<Vertex>();
		vertex_map=new TreeMap<Vertex, Integer>();
		vertex_counter=1;
		faces=new LinkedList<OBJFile.Face>();
		x_offset=0;
		y_offset=0;
		z_offset=0;
		file_scale=1.0f;
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

	public void setScale(float scale)
	{
		file_scale=scale;
	}


	public void appendMtl(PrintWriter out, String mtlFile)
	{
		out.println("mtllib "+mtlFile);
		out.println();
	}
	
	public void appendObjectname(PrintWriter out)
	{
		out.println("g "+identifier);
		out.println();
	}

	public void appendVertices(PrintWriter out)
	{
		Locale l=null;

		for(Vertex vertex:vertices)
		{
			out.format(l,"v %2.2f %2.2f %2.2f",(vertex.x+x_offset)*file_scale,(vertex.y+y_offset)*file_scale,(vertex.z+z_offset)*file_scale);
			out.println();
		}
	}

	/**
	 * This method prints faces from the current buffer to an OBJ format.
	 * 
	 * @param out file to append the data
	 * @param obj_per_mat create separate object for each material
	 */
	public void appendFaces(PrintWriter out, boolean obj_per_mat)
	{
		Locale l=null;
		
		Collections.sort(faces);
		String last_mtl=null;	
		int normal_idx;
		for(Face f:faces)
		{
			if(!f.mtl.equals(last_mtl))
			{
				out.println();
				if(obj_per_mat) out.println("g "+identifier+"_"+f.mtl);
				out.println("usemtl "+f.mtl);
				last_mtl=f.mtl;
			}

			normal_idx=sideToNormalIndex(f.side);

			out.print("f ");
			out.format(l,"%d/-4/%d ",f.vertices[0],normal_idx);
			out.format(l,"%d/-3/%d ",f.vertices[1],normal_idx);
			out.format(l,"%d/-2/%d ",f.vertices[2],normal_idx);
			out.format(l,"%d/-1/%d ",f.vertices[3],normal_idx);
			out.println();
		}				
	}
	
	void clearData(boolean remove_duplicates)
	{
		faces.clear();
		
		if(!remove_duplicates)
		{
			vertices.clear();
			vertex_map.clear();
			return;
		}
				
		//keep edge vertices
		for(Vertex v:vertices)
		{			
			if((v.x-0.5)%16!=0 && (v.z-0.5)%16!=0 && (v.x+0.5)%16!=0 && (v.z+0.5)%16!=0)
				vertex_map.remove(v);
		}
		vertices.clear();
	}

	/**
	 * Write texture coordinates and normals. These are usually the same for all chunks.
	 * @param out writer of the OBJ file
	 */
	public void printTexturesAndNormals(PrintWriter out)
	{
		out.println("vt 1 0");
		out.println("vt 1 1");
		out.println("vt 0 1");
		out.println("vt 0 0");
		out.println("vn 0 -1 0");
		out.println("vn 0 1 0");
		out.println("vn 1 0 0");
		out.println("vn -1 0 0");
		out.println("vn 0 0 1");
		out.println("vn 0 0 -1");
		out.println("vn 0.7 0 0.7");
		out.println("vn -0.7 0 0.7");
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
		case BOTTOM: return 1;
		case TOP: return 2;
		case RIGHT: return 3;
		case LEFT: return 4;
		case BACK: return 5;
		case FRONT: return 6;
		case BACKRIGHT: return 7;
		case FRONTRIGHT: return 8;
		default: return 6;
		}
	}

	/**
	 * Add a face with the given vertices to the appropriate lists.
	 * Also create vertices if necessary.
	 * @param verts vertices of the face
	 * @param side side of the object
	 */
	public void addFace(Vertex [] verts, Transform trans, Side side, String mtl)
	{
		Face face=new Face();
		face.side=side;
		face.mtl=mtl;		
		face.vertices=new int[4];
		Vertex vert;
		for(int i=0; i<4; i++)
		{
			if (trans != null)
				vert=trans.multiply(verts[i]);
			else
				vert=verts[i];

			if(!vertex_map.containsKey(vert))				
			{
				vertices.add(vert);
				vertex_map.put(vert, vertex_counter);
				vertex_counter++;
			}
			face.vertices[i]=vertex_map.get(vert);
		}

		faces.add(face);
	}

	public void addChunkBuffer(ChunkDataBuffer chunk, int chunk_x, int chunk_z)
	{
		int x,y,z;
		int xmin,xmax,ymin,ymax,zmin,zmax;
		Rectangle xy,xz;
		xy=chunk.getXYBoundaries();
		xz=chunk.getXZBoundaries();
		xmin=xy.x;
		xmax=xmin+xy.width;
		ymin=xy.y;
		ymax=ymin+xy.height;
		zmin=xz.y;
		zmax=zmin+xz.height;

		int xs=chunk_x*16;
		int zs=chunk_z*16;
		int xe=xs+16;
		int ze=zs+16;

		if(xs<xmin) xs=xmin;
		if(xe>xmax) xe=xmax;
		if(zs<zmin) zs=zmin;
		if(ze>zmax) ze=zmax;

		for(z = zs; z < ze; z++)
		{
			for(x = xs; x < xe; x++)
			{
				for(y = ymin; y < ymax; y++)
				{						
					short blockID=chunk.getBlockID(x, y, z);
					byte blockData=chunk.getBlockData(x, y, z);

					if(blockID==0) continue;

					BlockTypes.get(blockID).model.addModel(this, chunk, x, y, z, blockData);
				}									
			}
		}		
	}

}