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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jmc.NBT.TAG_Compound;
import org.jmc.NBT.TAG_Double;
import org.jmc.NBT.TAG_Float;
import org.jmc.NBT.TAG_List;
import org.jmc.NBT.TAG_String;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.models.Cube;


/**
 * OBJ file class.
 * This file contains the geometry of the whole world we are trying to export.
 * It also contains the links to the materials saved in the MTL file.
 * @author danijel
 *
 */
public class OBJOutputFile extends OBJFileBase
{
	/**
	 * Identifier of the file.
	 * Since many OBJ class objects are created by different chunks,
	 * this helps differentiate them and assign them a name. It's
	 * usually just the coordinates of the chunk.
	 */
	private String identifier;

	/**
	 * Map of vertices to their respective IDs used in the faces of the mesh.
	 */
	private Map<Vertex, Integer> vertexMap;
	
	/**
	 * Map of texture coordinates to their respective indexes in the OBJ file.
	 */
	private Map<UV, Integer> texCoordMap;
	
	/**
	 * Map of normals to their respective indexes in the OBJ file.
	 */
	private Map<Vertex, Integer> normalsMap;
	
	/**
	 * Offsets of the file. Used to position the chunk in its right location.
	 */
	private float x_offset, y_offset, z_offset;

	private float file_scale;

	private int vertex_counter, tex_counter, norm_counter;


	/**
	 * Main constructor.
	 * @param ident identifier of the OBJ
	 * @param mtl reference to the MTL
	 */
	public OBJOutputFile(String ident)
	{
		super();
		
		identifier = ident;
		vertexMap = new HashMap<Vertex, Integer>();
		vertex_counter = 1;
		texCoordMap = new HashMap<UV, Integer>();
		tex_counter = 1;
		normalsMap = new HashMap<Vertex, Integer>();
		norm_counter = 1;
		
		x_offset = 0;
		y_offset = 0;
		z_offset = 0;
		file_scale = 1.0f;
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
	 * Scales the map by a float value.
	 * @param scale
	 */
	public void setScale(float scale)
	{
		file_scale=scale;
	}


	public void clearData(boolean remove_duplicates)
	{
		if(remove_duplicates)
		{
			//keep edge vertices
			for(Vertex v:vertices)
				if((v.x-0.5)%16!=0 && (v.z-0.5)%16!=0 && (v.x+0.5)%16!=0 && (v.z+0.5)%16!=0)
					vertexMap.remove(v);
		}
		else
		{
			vertexMap.clear();
		}
		vertices.clear();
		texCoords.clear();
		normals.clear();
		faces.clear();
	}

	/**
	 * Appends a header linking to the MTL file.
	 * @param out
	 * @param mtlFile
	 */
	public void appendMtl(PrintWriter out, String mtlFile)
	{
		out.println("mtllib "+mtlFile);
		out.println();
	}
	
	/**
	 * Appends an object name line to the file.
	 * @param out
	 */
	public void appendObjectname(PrintWriter out)
	{
		out.println("g "+identifier);
		out.println();
	}

	/**
	 * Write texture coordinates. These will be shared by all chunks.
	 * @param out writer of the OBJ file
	 */
	public void appendTextures(PrintWriter out)
	{
		for (UV uv : texCoords)
		{
			out.format((Locale)null, "vt %.4f %.4f", uv.u, uv.v);
			out.println();
		}
	}

	/**
	 * Write normals. These will be shared by all chunks.
	 * @param out writer of the OBJ file
	 */
	public void appendNormals(PrintWriter out)
	{
		for (Vertex norm : normals)
		{
			out.format((Locale)null, "vn %.3f %.3f %.3f", norm.x, norm.y, norm.z);
			out.println();
		}
	}

	/**
	 * Appends vertices to the file.
	 * @param out
	 */
	public void appendVertices(PrintWriter out)
	{
		for (Vertex vertex : vertices)
		{
			out.format((Locale)null, "v %.3f %.3f %.3f",
					(vertex.x+x_offset)*file_scale,
					(vertex.y+y_offset)*file_scale,
					(vertex.z+z_offset)*file_scale);
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
		Collections.sort(faces);
		String last_mtl=null;	
		for(Face f:faces)
		{
			if(!f.mtl.equals(last_mtl))
			{
				out.println();
				if(obj_per_mat) out.println("g "+identifier+"_"+f.mtl);
				out.println("usemtl "+f.mtl);
				last_mtl=f.mtl;
			}

			out.format((Locale)null,
					"f %d/%d/%d %d/%d/%d %d/%d/%d %d/%d/%d", 
					f.vertices[0], f.uv[0], f.normals[0],
					f.vertices[1], f.uv[1], f.normals[1],
					f.vertices[2], f.uv[2], f.normals[2],
					f.vertices[3], f.uv[3], f.normals[3]);
			out.println();
		}
	}
	

	/**
	 * Add a face with the given vertices to the OBJ file.
	 * 
	 * @param verts vertices of the face (length must be 4)
	 * @param uv texture coordinates for the vertices (length must be 4). If null, the default coordinates will be used.
	 * @param trans Transform to apply to the vertex coordinates. If null, no transform is applied 
	 * @param mtl Name of the material for the face
	 */
	public void addFace(Vertex[] verts, UV[] uv, Transform trans, String mtl)
	{
		addFace(verts, null, uv, trans, mtl);
	}
	
	/**
	 * Add a face with the given vertices to the OBJ file.
	 * TODO accept faces with any number of vertices 
	 * TODO ignoring normals for now (must also apply transform to normals!)
	 * 
	 * @param verts vertices of the face (length must be 4)
	 * @param norms normals for the vertices (length must be 4). If null, no normals will be written to file.
	 * @param uv texture coordinates for the vertices (length must be 4). If null, the default coordinates will be used.
	 * @param trans Transform to apply to the vertex coordinates. If null, no transform is applied 
	 * @param mtl Name of the material for the face
	 */
	public void addFace(Vertex[] verts, Vertex[] norms, UV[] uv, Transform trans, String mtl)
	{
		if (uv == null)
		{
			uv = new UV[] { 
				new UV(0,0),
				new UV(1,0), 
				new UV(1,1), 
				new UV(0,1) 
			};
		}
			
		Face face = new Face(4);
		face.mtl = mtl;		
		for (int i=0; i<4; i++)
		{
			Vertex vert;
			if (trans != null)
				vert = trans.multiply(verts[i]);
			else
				vert = verts[i];

			if (vertexMap.containsKey(vert))				
			{
				face.vertices[i] = vertexMap.get(vert);
			}
			else 
			{
				vertices.add(vert);
				vertexMap.put(vert, vertex_counter);
				face.vertices[i] = vertex_counter;
				vertex_counter++;
			}
		}
		for (int i=0; i<4; i++)
		{
			if (texCoordMap.containsKey(uv[i]))				
			{
				face.uv[i] = texCoordMap.get(uv[i]);
			}
			else
			{
				texCoords.add(uv[i]);
				texCoordMap.put(uv[i], tex_counter);
				face.uv[i] = tex_counter;
				tex_counter++;
			}
		}


		// XXX crude normals test
		
		Vertex norm = new Vertex(0,0,0);
		int normIndex;
		norm.x = (verts[1].y - verts[0].y) * (verts[2].z - verts[0].z) - (verts[1].z - verts[0].z) * (verts[2].y - verts[0].y);
		norm.y = (verts[1].z - verts[0].z) * (verts[2].x - verts[0].x) - (verts[1].x - verts[0].x) * (verts[2].z - verts[0].z);
		norm.z = (verts[1].x - verts[0].x) * (verts[2].y - verts[0].y) - (verts[1].y - verts[0].y) * (verts[2].x - verts[0].x);
		
		if (normalsMap.containsKey(norm))				
		{
			normIndex = normalsMap.get(norm);
		}
		else
		{
			normals.add(norm);
			normalsMap.put(norm, norm_counter);
			normIndex = norm_counter;
			norm_counter++;
		}
		
		for (int i=0; i<4; i++)
		{
			face.normals[i] = normIndex;
		}		
		
		
		faces.add(face);
	}
	
	/**
	 * Adds all blocks from the given chunk buffer into the file.
	 * @param chunk
	 * @param chunk_x
	 * @param chunk_z
	 */
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
		
		for(TAG_Compound entity:chunk.getEntities(chunk_x, chunk_z))
		{
			TAG_String id = (TAG_String) entity.getElement("id");
			if(id==null) continue;
			
			if(id.value.equals("Minecart"))
			{
				TAG_List pos = (TAG_List) entity.getElement("Pos");
				int ex=(int)((TAG_Double)pos.getElement(0)).value;				
				int ey=(int)((TAG_Double)pos.getElement(1)).value;
				int ez=(int)((TAG_Double)pos.getElement(2)).value;
				BlockTypes.get((short)5).model.addModel(this, chunk, ex, ey, ez, (byte)0);
			}
		}
	}

}