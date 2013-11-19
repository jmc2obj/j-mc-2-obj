/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import org.jmc.NBT.TAG_Compound;
import org.jmc.entities.Entity;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;


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
	
	private long obj_idx_count=-1;

	static class Block
	{
		short id;
		byte data;
		int x, y, z;

		Block() {}

		Block(Block other)
		{
			id = other.id;
			data = other.data;

			x = other.x;
			y = other.y;
			z = other.z;
		}

		Block[] getSurroundings()
		{
			Block[] surroundings = new Block[6];

			surroundings[0] = new Block(this);
			surroundings[1] = new Block(this);
			surroundings[2] = new Block(this);
			surroundings[3] = new Block(this);
			surroundings[4] = new Block(this);
			surroundings[5] = new Block(this);

			surroundings[0].x--;
			surroundings[1].x++;
			surroundings[2].y--;
			surroundings[3].y++;
			surroundings[4].z--;
			surroundings[5].z++;

			return surroundings;
		}

		public boolean equals(Object o)
		{
			if (!(o instanceof Block))
				return false;

			Block other = (Block)o;

			return id == other.id && data == other.data &&
				x == other.x && y == other.y && z == other.z;
		}
		public int hashCode()
		{
			return
				((id & 0xF) << 28) |
				((data & 0xF) << 24) |
				((x & 0xFF) << 16) |
				((y & 0xFF) << 8) |
				((z & 0xFF) << 0);
		}
	}

	//Map of chunk coordinates to list of every contiguous mass in that chunk
	private Map<Point, ArrayList<Block>> contiguous_masses;

	//Index that masses for the current chunk starts at
	private int chunk_mass_index = 0;

	//Map of masses in a chunk to neighboring masses in surrounding chunks.
	//Currently it gets populated, but isn't being used yet.
	private Map<Block, HashSet<Block>> neighbors;

	/**
	 * Decides whether to print "usemtl" lines in OBJ file
	 */
	private boolean print_usemtl;

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
		print_usemtl=true;

		contiguous_masses = new HashMap<Point, ArrayList<Block>>();
		neighbors = new HashMap<Block, HashSet<Block>>();
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

	/**
	 * Sets the print usemtl switch.
	 * @param val
	 */
	public void setPrintUseMTL(boolean val)
	{
		print_usemtl=val;
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
	 */
	public void appendFaces(PrintWriter out)
	{		
		Collections.sort(faces);
		String last_mtl=null;	
		Long last_obj_idx=Long.valueOf(-1);
		for(Face f:faces)
		{
			if(!f.mtl.equals(last_mtl) && print_usemtl)
			{
				out.println();
				out.println("usemtl "+f.mtl);
				last_mtl=f.mtl;
			}
			
			if(!f.obj_idx.equals(last_obj_idx))
			{
				out.println("g o"+f.obj_idx);
				last_obj_idx=f.obj_idx;
			}

			out.print("f");
			for (int i = 0; i < f.vertices.length; i++)
			{
				if (f.normals != null && f.uv != null)
					out.format((Locale)null, " %d/%d/%d", f.vertices[i], f.uv[i], f.normals[i]);
				else if (f.normals == null && f.uv != null)
					out.format((Locale)null, " %d/%d", f.vertices[i], f.uv[i]);
				else if (f.normals != null && f.uv == null)
					out.format((Locale)null, " %d//%d", f.vertices[i], f.normals[i]);
				else
					out.format((Locale)null, " %d", f.vertices[i]);
			}
			out.println();
		}
	}


	/**
	 * Add a face with the given vertices to the OBJ file.
	 * 
	 * @param verts vertices of the face
	 * @param uv texture coordinates for the vertices. If null, the default coordinates will be used
	 * (only accepted if face is a quad!).
	 * @param trans Transform to apply to the vertex coordinates. If null, no transform is applied 
	 * @param mtl Name of the material for the face
	 */
	public void addFace(Vertex[] verts, UV[] uv, Transform trans, String mtl)
	{
		if (uv == null)
		{
			if (verts.length != 4)
				throw new IllegalArgumentException("Default texture coordinates are only defined for quads.");

			uv = new UV[] {
					new UV(0,0),
					new UV(1,0), 
					new UV(1,1), 
					new UV(0,1) 
			};
		}

		addFace(verts, null, uv, trans, mtl);
	}

	/**
	 * Add a face with the given vertices to the OBJ file.
	 * 
	 * @param verts vertices of the face
	 * @param norms normals for the vertices. If null, no normals will be written to file.
	 * @param uv texture coordinates for the vertices. If null, no uv coords will be written to file.
	 * @param trans Transform to apply to the vertex coordinates. If null, no transform is applied 
	 * @param mtl Name of the material for the face
	 */
	public void addFace(Vertex[] verts, Vertex[] norms, UV[] uv, Transform trans, String mtl)
	{
		Face face = new Face(verts.length);
		face.obj_idx=Long.valueOf(obj_idx_count);
		face.mtl = mtl;
		if (norms == null) face.normals = null;
		if (uv == null) 
		{
			face.uv = null;
		}
		else if(Options.useUVFile)
		{
			uv=UVRecalculate.recalculate(uv, mtl);
		}

		for (int i = 0; i < verts.length; i++)
		{
			// add vertices
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

			// add normals
			if (norms != null)
			{
				Vertex norm;
				if (trans != null)
					norm = trans.applyToNormal(norms[i]);
				else
					norm = norms[i];

				if (normalsMap.containsKey(norm))				
				{
					face.normals[i] = normalsMap.get(norm);
				}
				else
				{
					normals.add(norm);
					normalsMap.put(norm, norm_counter);
					face.normals[i] = norm_counter;
					norm_counter++;
				}
			}

			// add texture coords
			if (uv != null)
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
		Block block = new Block();
		Point here = new Point(chunk_x, chunk_z);

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

		if(Options.objectPerContiguousMass)
		{
			calculateMasses(chunk, here, ymin, ymax);
			calculateMasses(chunk, new Point(chunk_x-1, chunk_z), ymin, ymax);
			calculateMasses(chunk, new Point(chunk_x, chunk_z-1), ymin, ymax);
			calculateMasses(chunk, new Point(chunk_x+1, chunk_z), ymin, ymax);
			calculateMasses(chunk, new Point(chunk_x, chunk_z+1), ymin, ymax);

			linkMasses(chunk, here, new Point(chunk_x-1, chunk_z), ymin, ymax);
			linkMasses(chunk, here, new Point(chunk_x, chunk_z-1), ymin, ymax);
			linkMasses(chunk, here, new Point(chunk_x+1, chunk_z), ymin, ymax);
			linkMasses(chunk, here, new Point(chunk_x, chunk_z+1), ymin, ymax);
		}

		ArrayList<Block> masses = contiguous_masses.get(here);

		for(block.z = zs; block.z < ze; block.z++)
		{
			for(block.x = xs; block.x < xe; block.x++)
			{
				for(block.y = ymin; block.y < ymax; block.y++)
				{
					block.id=chunk.getBlockID(block.x, block.y, block.z);
					block.data=chunk.getBlockData(block.x, block.y, block.z);
					byte blockBiome=chunk.getBlockBiome(block.x, block.z);

					if(block.id==0) continue;

					if(Options.objectPerBlock) obj_idx_count++;

					if(Options.objectPerContiguousMass)
					{
						int mass_index = 0;
						for (int i = 0; i < masses.size(); i++)
						{
							Block mass = masses.get(i);
							if ((block.id != mass.id) || (block.data != mass.data))
								continue;

							if (pathExists(chunk, ymin, ymax, block, mass))
							{
								mass_index = i;
								break;
							}
						}

						obj_idx_count = chunk_mass_index + mass_index;
					}

					BlockTypes.get(block.id).getModel().addModel(this, chunk, block.x, block.y, block.z, block.data, blockBiome);
				}
			}
		}

		if(Options.objectPerContiguousMass)
			chunk_mass_index += masses.size();

		if(Options.renderEntities)
		{
			for(TAG_Compound entity:chunk.getEntities(chunk_x, chunk_z))
			{
				Entity handler=EntityTypes.getEntity(entity);
				if(handler!=null) handler.addEntity(this, entity);						
			}
	
			for(TAG_Compound entity:chunk.getTileEntities(chunk_x, chunk_z))
			{
				Entity handler=EntityTypes.getEntity(entity);
				if(handler!=null) handler.addEntity(this, entity);						
			}
		}
	}

	private void calculateMasses(ChunkDataBuffer chunk, Point chunkp, int ymin, int ymax)
	{
		if (!chunk.hasChunk(chunkp.x, chunkp.y) || contiguous_masses.containsKey(chunkp))
			return;

		ArrayList<Block> masses = new ArrayList<Block>();
		Block block = new Block();

		for (block.z = chunkp.y*16; block.z < (chunkp.y*16)+16; block.z++)
		{
			for (block.x = chunkp.x*16; block.x < (chunkp.x*16)+16; block.x++)
			{
				for(block.y = ymin; block.y < ymax; block.y++)
				{
					block.id = chunk.getBlockID(block.x, block.y, block.z);
					block.data = chunk.getBlockData(block.x, block.y, block.z);

					if (block.id == 0)
						continue;

					int mass_index = -1;
					for (int i = 0; i < masses.size(); i++)
					{
						Block mass = masses.get(i);
						if ((block.id != mass.id) || (block.data != mass.data))
							continue;

						if (pathExists(chunk, ymin, ymax, block, mass))
						{
							mass_index = i;
							break;
						}
					}

					if (mass_index < 0)
						masses.add(new Block(block));
				}
			}
		}

		contiguous_masses.put(chunkp, masses);
	}
	private void linkMasses(ChunkDataBuffer chunk, Point chunkp_here, Point chunkp_there, int ymin, int ymax)
	{
		if (!chunk.hasChunk(chunkp_there.x, chunkp_there.y) || !contiguous_masses.containsKey(chunkp_there))
			return;

		for (Block mass_here: contiguous_masses.get(chunkp_here))
		{
			HashSet<Block> masses_there = new HashSet<Block>();

			for (Block mass_there: contiguous_masses.get(chunkp_there))
				if (pathExists(chunk, ymin, ymax, mass_here, mass_there))
					masses_there.add(mass_there);

			neighbors.put(mass_here, masses_there);
		}
	}


	private boolean pathExists(ChunkDataBuffer chunk, int ymin, int ymax, Block b1, Block b2)
	{
		Rectangle area = new Rectangle(Chunk.getChunkPos(b1.x, b1.z));
		area.add(Chunk.getChunkPos(b2.x, b2.z));

		area.x *= 16;
		area.y *= 16;
		area.width = (area.width+1)*16;
		area.height = (area.height+1)*16;

		return __pathExists(chunk, area, ymin, ymax, new HashSet<Block>(), new Block(b1), b2);
	}
	private boolean __pathExists(ChunkDataBuffer chunk, Rectangle area, int ymin, int ymax, HashSet<Block> checked, Block block, Block target)
	{
		if (block.equals(target))
			return true;

		if (checked.contains(block))
			return false;

		if (block.y < ymin || block.y >= ymax)
			return false;
		if (block.x < area.x || block.z < area.y)
			return false;
		if (block.x >= (area.x + area.width) || block.z >= (area.y + area.height))
			return false;

		block.id = chunk.getBlockID(block.x, block.y, block.z);
		block.data = chunk.getBlockData(block.x, block.y, block.z);

		if (block.id != target.id || block.data != target.data)
			return false;

		checked.add(block);

		for (Block b: block.getSurroundings())
			if (__pathExists(chunk, area, ymin, ymax, checked, b, target))
				return true;

		return false;
	}
}
