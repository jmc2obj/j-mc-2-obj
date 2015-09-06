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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jmc.NBT.TAG_Compound;
import org.jmc.entities.Entity;
import org.jmc.geom.FaceUtils.Face;
import org.jmc.geom.FaceUtils;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
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
	
	private ArrayList<Face> faces;
	
	public static long timeOptimising;

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

		faces = new ArrayList<Face>();
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
		objFaces.clear();
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
		Collections.sort(objFaces);
		String last_mtl=null;	
		Long last_obj_idx=Long.valueOf(-1);
		for(OBJFace f:objFaces)
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
		if (Options.optimiseGeometry){
			Face face = new Face();
			face.uvs = uv.clone();
			face.material = mtl;
			face.vertices = verts.clone();
			if (trans != null){
				face = trans.multiply(face);
			}
			faces.add(face);
		}
		else{
			addOBJFace(verts, null, uv, trans, mtl);
		}
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
	public void addOBJFace(Vertex[] verts, Vertex[] norms, UV[] uv, Transform trans, String mtl)
	{
		OBJFace face = new OBJFace(verts.length);
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
	
		objFaces.add(face);
	}
	
	public void addOBJFace(Face face){
		addOBJFace(face.vertices, null, face.uvs, null, face.material);
	}

	/**
	 * Adds all blocks from the given chunk buffer into the file.
	 * @param chunk
	 * @param chunk_x
	 * @param chunk_z
	 */
	public void addChunkBuffer(ChunkDataBuffer chunk, int chunk_x, int chunk_z)
	{
		faces = new ArrayList<Face>();
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

		for(int z = zs; z < ze; z++)
		{
			for(int x = xs; x < xe; x++)
			{
				for(int y = ymin; y < ymax; y++)
				{
					short blockID=chunk.getBlockID(x, y, z);
					byte blockData=chunk.getBlockData(x, y, z);
					byte blockBiome=chunk.getBlockBiome(x, z);
					
					if(blockID==0)
						continue;
					
					if(Options.excludeBlocks.contains(blockID))
						continue;

					if(Options.convertOres){
						if(blockID == 14 || blockID == 15 || blockID == 16 || blockID == 21 || blockID == 56 || blockID == 73 || blockID == 74 || blockID == 129){
							blockID = 1;
						}
					}
					
					if(Options.objectPerBlock) obj_idx_count++;
					
					try {
						BlockTypes.get(blockID).getModel().addModel(this, chunk, x, y, z, blockData, blockBiome);
					}
					catch (Exception ex) {
						Log.error("Error rendering block, skipping.", ex);
					}
				}
			}
		}
		
		if (Options.optimiseGeometry) {
			long startTime = System.nanoTime();
			HashMap<String, ArrayList<Face>> faceAxisArray = new HashMap<String, ArrayList<Face>>();
			for (Face f : faces){
				int planar = f.isPlanar();
				if (planar == 3){
					addOBJFace(f);
					continue;
				}
				String key = "";
				switch(planar){
					case 0: key += "X "; break;
					case 1: key += "Y "; break;
					case 2: key += "Z "; break;
					default: Log.debug("isPlanar returned an unknown value!"); break;
				}
				//Sort faces into planar groups so merging can be efficient
				key += Float.toString(f.vertices[0].getByInt(planar));
				ArrayList<Face> faceList = getOrDefault(faceAxisArray, key, new ArrayList<Face>());
				faceList.add(f);
				faceAxisArray.put(key, faceList);
			}
			for (ArrayList<Face> faceList : faceAxisArray.values()){
				//Merge faces per axis
				//X loop
				faceList = mergeAxisFaces(faceList, 0);
				//Y loop
				faceList = mergeAxisFaces(faceList, 1);
				//Z loop
				faceList = mergeAxisFaces(faceList, 2);
				for (Face face : faceList) {
					if (!face.remove){
						addOBJFace(face);
					}
				}
			}
			faces = new ArrayList<Face>();//Clear out faces list because they have all been added so far
			long endTime = System.nanoTime();
			timeOptimising += (endTime - startTime);
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
		
		for (Face face : faces){
			addOBJFace(face); //Add any left over faces from not optimising and entities.
		}
		faces = new ArrayList<Face>();
	}
	
	/**
	 * Attempts to join all faces in faces along axis
	 * @param faceList The faces to combine
	 * @param axis The axis to combine across 
	 * @return The combined faces
	 */
	private static ArrayList<Face> mergeAxisFaces(ArrayList<Face> faceList, int axis) {
		ArrayList<Face> faceList2 = new ArrayList<Face>();
		for (Face face : faceList){
			if (!faceList2.isEmpty()){
				boolean merged = false;
				for (Face face2 : faceList2) {
					merged = mergeFaces(face, face2, axis);
					if (merged) break;
				}
				if (!merged) {
					faceList2.add(face);
				}
			}
			else {
				//mmdanggg2: if first face, just add it
				faceList2.add(face);
			}
		}
		return faceList2;
	}
	
	/**
	 * Attempts to join 2 faces along axis and store the joined face in face2
	 * @param face1 Input face 1
	 * @param face2 Input face 2, joined face will replace face2
	 * @param axis The axis to join across 0, 1 or 2
	 * @return Whether the join was successful
	 */
	private static boolean mergeFaces(Face face1, Face face2, int axis){
		if (face1.remove || face2.remove){
			return false;
		}
		boolean isFacingEqual = true;
		if (face1.isAnticlockwise() != face2.isAnticlockwise()){
			isFacingEqual = false;
		}
		if (face1.material == face2.material) {
			Vertex[] verts1 = face1.vertices;
			Vertex[] verts2 = face2.vertices;
			ArrayList<Vertex> matches = new ArrayList<Vertex>();
			//mmdanggg2: check for shared verts
			for (Vertex vert1 : verts1){
				for (Vertex vert2 : verts2){
					if (vert1.similar(vert2)){
						matches.add(vert1);
					}
				}
			}
			if (matches.size() == 2 && isFacingEqual){
				//mmdanggg2: if the face extends in the axis we are looking at
				int extendingIn;
				float xMin1, xMax1, yMin1, yMax1, zMin1, zMax1;
				float xMin2, xMax2, yMin2, yMax2, zMin2, zMax2;
				xMin1 = xMax1 = verts1[0].x;
				yMin1 = yMax1 = verts1[0].y;
				zMin1 = zMax1 = verts1[0].z;
				xMin2 = xMax2 = verts2[0].x;
				yMin2 = yMax2 = verts2[0].y;
				zMin2 = zMax2 = verts2[0].z;
				for (Vertex v : verts1){
					if (v.x > xMax1) xMax1 = v.x;
					if (v.x < xMin1) xMin1 = v.x;
					if (v.y > yMax1) yMax1 = v.y;
					if (v.y < yMin1) yMin1 = v.y;
					if (v.z > zMax1) zMax1 = v.z;
					if (v.z < zMin1) zMin1 = v.z;
				}
				for (Vertex v : verts2){
					if (v.x > xMax2) xMax2 = v.x;
					if (v.x < xMin2) xMin2 = v.x;
					if (v.y > yMax2) yMax2 = v.y;
					if (v.y < yMin2) yMin2 = v.y;
					if (v.z > zMax2) zMax2 = v.z;
					if (v.z < zMin2) zMin2 = v.z;
				}
				if (xMax1 > xMax2 || xMin1 < xMin2) extendingIn = 0;
				else if (yMax1 > yMax2 || yMin1 < yMin2) extendingIn = 1;
				else if (zMax1 > zMax2 || zMin1 < zMin2) extendingIn = 2;
				else extendingIn = 3;
				if (extendingIn == axis){
					if (face1.isUVAnticlockwise() != face2.isUVAnticlockwise()){
						return false;
					}
					//Get the indexes of the matching verts in each face array
					ArrayList<Integer> verts1MatchIndex = new ArrayList<Integer>();
					ArrayList<Integer> verts2MatchIndex = new ArrayList<Integer>();
					verts1MatchIndex.add(Arrays.asList(verts1).indexOf(matches.get(0)));
					verts1MatchIndex.add(Arrays.asList(verts1).indexOf(matches.get(1)));
					verts2MatchIndex.add(Arrays.asList(verts2).indexOf(matches.get(0)));
					verts2MatchIndex.add(Arrays.asList(verts2).indexOf(matches.get(1)));
					//Check the matching point UV vectors
					UV uv1 = face1.uvs[verts1MatchIndex.get(0)];
					UV uv2 = face2.uvs[verts2MatchIndex.get(0)];
					UV uvVec1 = UV.subtract(face1.uvs[verts1MatchIndex.get(1)], uv1);
					UV uvVec2 = UV.subtract(face2.uvs[verts2MatchIndex.get(1)], uv2);
					if (uvVec1.similar(uvVec2)){
						//Check the matching point UV Starting points
						if (FaceUtils.similar(uv1.u%1.0f, uv2.u%1.0f) && FaceUtils.similar(uv1.v%1.0f, uv2.v%1.0f)){
							//Extend the faces
							Vertex[] newVerts = verts2.clone();
							UV[] newUVs = face2.uvs.clone();
							int changeCount = 0;
							//Replace the common verts in face 2 with the uncommon from face 1
							for (int i = 0; i < 2; i++){//For the 2 matches, get their indexes
								int v1Index = verts1MatchIndex.get(i);
								int v2Index = verts2MatchIndex.get(i);
								for (int j = 0; j < 4; j++){//for the 4 verts in face1
									Vertex v = verts1[j];
									boolean[] axisEqual = new boolean[3];
									for (int ax = 0; ax < 3; ax++){//for each axis
										//if pos is equal in the ax axis
										axisEqual[ax] = verts2[v2Index].getByInt(ax) == v.getByInt(ax);
									}
									//flip the t/f of the axis we're merging in because it would not be equal
									axisEqual[axis] = !axisEqual[axis];
									if (axisEqual[0] && axisEqual[1] && axisEqual[2]){
										newVerts[v2Index] = v;
										UV uvA = face1.uvs[v1Index];
										UV uvB = face1.uvs[j];
										//add the uv difference to the end of the old uv
										newUVs[v2Index] = UV.add(newUVs[v2Index], UV.subtract(uvB, uvA));
										changeCount++;
										break;
									}
								}
							}
							if (changeCount == 2){
								//Replace face 2 with new verts and UVs
								face2.vertices = newVerts;
								face2.uvs = newUVs;
								return true;
							}
						}
					}
				}
			} else if(matches.size() == 4 && !isFacingEqual){
				face1.remove = true;
				face2.remove = true;
				return false;
			}
		}
		return false;
	}
	
	private static <K,V> V getOrDefault(Map<K,V> map, K key, V deflt) {
		if (map.containsKey(key))
			return map.get(key);
		else
			return deflt;
	}
}
