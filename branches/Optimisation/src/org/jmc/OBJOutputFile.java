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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jmc.NBT.TAG_Compound;
import org.jmc.entities.Entity;
import org.jmc.geom.FaceUtils;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.models.BlockModel;
import org.jmc.models.Cube;
import org.jmc.models.DirtGrass;
import org.jmc.util.Log;


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
	
	/**
	 * Decides whether to print "usemtl" lines in OBJ file
	 */
	private boolean print_usemtl;


	/**
	 * Checks if the blockId is in the option list of blocks to render.
	 * 
	 * @param blockID Id do check
	 * @return Whether the id is in the list {@code Options.blockid }
	 */
	private boolean checkBlockIdInOptions(int blockID){
		for(int id : Options.blockid){
			if(blockID == id) return true;
		}
		return false;
	}


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
	}

	/**
	 * Offset all the vertices by these amounts.
	 * Used to position the chunk in its right location.
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 */
	public void setOffset(float x, float y, float z)
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
		
		HashMap<String, ArrayList<FaceUtils.Face>> faceAxisArray = new HashMap<String, ArrayList<FaceUtils.Face>>();

		for(int z = zs; z < ze; z++)
		{
			for(int x = xs; x < xe; x++)
			{
				for(int y = ymin; y < ymax; y++)
				{
					short blockID=chunk.getBlockID(x, y, z);
					byte blockData=chunk.getBlockData(x, y, z);
					byte blockBiome=chunk.getBlockBiome(x, z);
					
					if(Options.convertOres){
						if(blockID == 14 || blockID == 15 || blockID == 16 || blockID == 21 || blockID == 56 || blockID == 73 || blockID == 74 || blockID == 129){
							blockID = 1;
						}
					}

					if(blockID==0)
						continue;
					if(Options.singleBlock && !checkBlockIdInOptions(blockID))						
						continue;

					if(Options.objectPerBlock) obj_idx_count++;
					
					try {
						if (Options.optimiseGeometry){
							//mmdanggg2: If we're optimising, only affect simple cube models
							BlockModel bm = BlockTypes.get(blockID).getModel();
							if (bm instanceof Cube || bm instanceof DirtGrass){
								Cube cubeMdl = (Cube) bm;
								cubeMdl.addModel(this, chunk, x, y, z, blockData, blockBiome, faceAxisArray);
							}
							else {
								bm.addModel(this, chunk, x, y, z, blockData, blockBiome);
							}
						}
						else {
							BlockTypes.get(blockID).getModel().addModel(this, chunk, x, y, z, blockData, blockBiome);
						}
					}
					catch (Exception ex) {
						Log.error("Error rendering block, skipping.", ex);
					}
				}
			}
		}
		
		if (Options.optimiseGeometry) {
			for (ArrayList<FaceUtils.Face> faces : faceAxisArray.values()){
				//X loop
				faces = compileFaces(faces, 0);
				//Y loop
				faces = compileFaces(faces, 1);
				//Z loop
				faces = compileFaces(faces, 2);
				for (FaceUtils.Face face : faces) {
					//mmdanggg2: correct the UVs for the new faces
					face.uvs[1].u = face.uvs[2].u = (float) Vertex.distance(face.vertices[0], face.vertices[1]);
					face.uvs[3].v = face.uvs[2].v = (float) Vertex.distance(face.vertices[0], face.vertices[3]);
					addFace(face.vertices, face.uvs, null, face.material);
				}
			}
		}
		
		if(Options.renderEntities)
		{
			for(TAG_Compound entity:chunk.getEntities(chunk_x, chunk_z))
			{
				Entity handler=EntityTypes.getEntity(entity);
				try {
					if(handler!=null) handler.addEntity(this, entity);
				}
				catch (Exception ex) {
					Log.error("Error rendering entity, skipping.", ex);
				}
			}
	
			for(TAG_Compound entity:chunk.getTileEntities(chunk_x, chunk_z))
			{
				Entity handler=EntityTypes.getEntity(entity);
				try {
					if(handler!=null) handler.addEntity(this, entity);
				}
				catch (Exception ex) {
					Log.error("Error rendering tyle entity, skipping.", ex);
				}
			}
		}
	}
	
	/**
	 * Attempts to join all faces in faces along axis
	 * @param faces The faces to combine
	 * @param axis The axis to combine across 
	 * @return The combined faces
	 */
	private static ArrayList<FaceUtils.Face> compileFaces(ArrayList<FaceUtils.Face> faces, int axis) {
		ArrayList<FaceUtils.Face> faces2 = new ArrayList<FaceUtils.Face>();
		for (FaceUtils.Face face : faces){
			if (!faces2.isEmpty()){
				boolean merged = false;
				for (FaceUtils.Face face2 : faces2) {
					merged = mergeFaces(face, face2, axis);
					if (merged) break;
				}
				if (!merged) {
					faces2.add(face);
				}
			}
			else {
				//mmdanggg2: if first face, just add it
				faces2.add(face);
			}
		}
		return faces2;
	}
	
	/**
	 * Attempts to join 2 faces along axis and store the joined face in face2
	 * @param face1 Input face 1
	 * @param face2 Input face 2, joined face will replace face2
	 * @param axis The axis to join across 0, 1 or 2
	 * @return Whether the join was successful
	 */
	private static boolean mergeFaces(FaceUtils.Face face1, FaceUtils.Face face2, int axis){
		if (face1.material == face2.material) {
			Vertex[] verts1 = face1.vertices;
			Vertex[] verts2 = face2.vertices;
			ArrayList<Vertex> matches = new ArrayList<Vertex>();
			//mmdanggg2: check for shared verts
			for (Vertex vert1 : verts1){
				for (Vertex vert2 : verts2){
					if (vert1.equals(vert2)){
						matches.add(vert1);
					}
				}
			}
			if (matches.size() == 2){
				//mmdanggg2: if the face extends in the axis we are looking at
				if (matches.get(0).getByInt(axis) != matches.get(1).getByInt(axis)){
					ArrayList<Vertex> newVertList = new ArrayList<Vertex>();
					for (int i = 0; i < 4; i++) {
						//mmdanggg2: get the vertices that are not the shared ones.
						if (!verts1[i].equals(matches.get(0)) && !verts1[i].equals(matches.get(1))) {
							newVertList.add(verts1[i]);
						}
						if (!verts2[i].equals(matches.get(0)) && !verts2[i].equals(matches.get(1))) {
							newVertList.add(verts2[i]);
						}
					}
					if (newVertList.size() == 4) {
						Vertex[] newVerts = newVertList.toArray(new Vertex[newVertList.size()]);
						//mmdanggg2: replace the face2's verts with the new ones
						face2.vertices = newVerts;
						return true;
					}
					else {
						return false;
					}
				}
				else {
					return false;
				}
			}
			else{
				return false;
			}
		}
		else {
			return false;
		}
	}
}
