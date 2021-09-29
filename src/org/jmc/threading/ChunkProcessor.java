/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.threading;

import java.awt.Rectangle;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import org.jmc.BlockData;
import org.jmc.BlockTypes;
import org.jmc.EntityTypes;
import org.jmc.Options;
import org.jmc.NBT.TAG_Compound;
import org.jmc.entities.Entity;
import org.jmc.geom.FaceUtils;
import org.jmc.geom.FaceUtils.Face;
import org.jmc.geom.Transform;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.util.Log;


/**
 * ChunkProcessor reads through the given chunk and outputs faces
 */
public class ChunkProcessor
{
	private int chunk_idx_count=-1;
	
	private ArrayList<Face> optimisedFaces = new ArrayList<Face>();
	private ArrayList<Face> faces = new ArrayList<Face>();

	/**
	 * See: {@link #addFace(Vertex[], Vertex[], UV[], Transform, NamespaceID) addFace}
	 */
	public void addFace(Vertex[] verts, UV[] uv, Transform trans, NamespaceID tex) {
		addFace(verts, null, uv, trans, tex);
	}
	
	/**
	 * See: {@link #addFace(Vertex[], Vertex[], UV[], Transform, String, boolean) addFace}
	 */
	public void addFace(Vertex[] verts, Vertex[] norms, UV[] uv, Transform trans, NamespaceID tex)
	{
		addFace(verts, norms, uv, trans, tex, true);
	}

	/**
	 * See: {@link #addFace(Vertex[], Vertex[], UV[], Transform, String) addFace}
	 */
	public void addDoubleSidedFace(Vertex[] verts, UV[] uv, Transform trans, NamespaceID tex) {
		addDoubleSidedFace(verts, null, uv, trans, tex);
	}

	public void addDoubleSidedFace(Vertex[] verts, Vertex[] norms, UV[] uv, Transform trans, NamespaceID tex) {
		addFace(verts, norms, uv, trans, tex);
		if (Options.doubleSidedFaces) {
			if (uv == null) {
				uv = defaultUVs();
			}
			addFace(reversed(verts), norms, reversed(uv), trans, tex);
		}
	}

	private static <T> T[] reversed(T[] in) {
		if (in == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		T[] out = (T[]) Array.newInstance(in.getClass().getComponentType(), in.length);
		for (int i = 0; i < in.length; i++) {
			out[in.length - 1 - i] = in[i];
		}
		return out;
	}

	private static UV[] defaultUVs() {
		return new UV[] {
				new UV(0,0),
				new UV(1,0),
				new UV(1,1),
				new UV(0,1)
		};
	}

	/**
	 * Add a face with the given vertices to the chunk output.
	 * 
	 * @param verts vertices of the face
	 * @param norms normals of the face
	 * @param uv texture coordinates for the vertices. If null, the default coordinates will be used
	 * (only accepted if face is a quad!).
	 * @param trans Transform to apply to the vertex coordinates. If null, no transform is applied 
	 * @param tex Name of the material for the face
	 * @param canOptimise Allows this face to be optimised
	 */
	public void addFace(Vertex[] verts, Vertex[] norms, UV[] uv, Transform trans, NamespaceID tex, boolean canOptimise)
	{
		if (uv == null)
		{
			if (verts.length != 4)
				throw new IllegalArgumentException("Default texture coordinates are only defined for quads.");

			uv = defaultUVs();
		}
		Face face = new Face();
		face.uvs = uv.clone();
		face.texture = tex;
		face.vertices = verts.clone();
		if (norms != null) {
			face.norms = norms.clone();
		}
		if (trans != null){
			face = trans.multiply(face);
		}
		face.chunk_idx = chunk_idx_count;
		if (Options.optimiseGeometry && canOptimise) {
			optimisedFaces.add(face);
		} else {
			faces.add(face);
		}
	}

	/**
	 * Returns all blocks from the given chunk buffer into the output.
	 * @param chunk
	 * @param chunk_x
	 * @param chunk_z
	 */
	public ArrayList<Face> process(ThreadChunkDeligate chunk, int chunk_x, int chunk_z)
	{
		optimisedFaces = new ArrayList<Face>();
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
					BlockData block=chunk.getBlockData(x, y, z);
					int blockBiome=chunk.getBlockBiome(x, y, z);
					
					if(block == null || block.id == NamespaceID.NULL)
						continue;
					
					if(Options.excludeBlocks.contains(block.id))
						continue;
					
					if(Options.convertOres){
						if(block.id.path.endsWith("ore")){
							block.id = new NamespaceID("minecraft", "stone");
						}
					}
					
					if(Options.objectPerBlock)
						chunk_idx_count++;
					
					try {
						BlockTypes.get(block).getModel().addModel(this, chunk, x, y, z, block, blockBiome);
						if (Boolean.parseBoolean(block.state.get("waterlogged"))) {
							BlockTypes.get(new BlockData(new NamespaceID("minecraft", "water"))).getModel().addModel(this, chunk, x, y, z, block, blockBiome);
						}
					}
					catch (Exception ex) {
						Log.error(String.format("Error rendering block '%s', skipping.", block.id), ex);
					}
				}
			}
		}
		
		if (Options.optimiseGeometry ) {
			HashMap<String, ArrayList<Face>> faceAxisArray = new HashMap<String, ArrayList<Face>>();
			for (Face f : optimisedFaces){
				int planar = f.isPlanar();
				if (planar == 3){
					faces.add(f);
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
				ArrayList<Face> faceList = faceAxisArray.getOrDefault(key, new ArrayList<Face>());
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
						faces.add(face);
					}
				}
			}
			optimisedFaces.clear();//Clear out faces list because they have all been added so far
		}
		
		if(Options.renderEntities)
		{
			for(TAG_Compound entity:chunk.getEntities(chunk_x, chunk_z))
			{
				Entity handler=EntityTypes.getEntity(entity);
				if (handler!=null) {
					try {
						handler.addEntity(this, entity);
					} catch (Exception ex) {
						Log.error(String.format("Error rendering entity %s, skipping.", handler.id), ex);
					}
				}
			}
	
			for(TAG_Compound entity:chunk.getTileEntities(chunk_x, chunk_z))
			{
				Entity handler=EntityTypes.getEntity(entity);
				try {
					if(handler!=null) handler.addEntity(this, entity);
				}
				catch (Exception ex) {
					Log.error("Error rendering tile entity, skipping.", ex);
				}
			}
		}
		
		faces.addAll(optimisedFaces);//Add any left over faces from not optimising and entities.
		return faces;
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
	 * Attempts to join two faces along axis and store the joined face in face2
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
		if (!face1.texture.equals(face2.texture)) {
			return false;
		}
		Vertex[] verts1 = face1.vertices;
		Vertex[] verts2 = face2.vertices;
		ArrayList<Vertex> matches = new ArrayList<Vertex>();
		ArrayList<Integer> verts1MatchIndex = new ArrayList<Integer>();
		ArrayList<Integer> verts2MatchIndex = new ArrayList<Integer>();
		//mmdanggg2: check for shared verts
		for (int i = 0; i < 4; i++){
			Vertex vert1 = verts1[i];
			for (int j = 0; j < 4; j++){
				Vertex vert2 = verts2[j];
				if (vert1.similar(vert2)){
					matches.add(vert1);
					//Get the indexes of the matching verts in each face array
					verts1MatchIndex.add(i);
					verts2MatchIndex.add(j);
				}
			}
		}
		if (matches.size() == 4 && !isFacingEqual){
			//face1.remove = true;
			//face2.remove = true;
			return false;
		}
		if (matches.size() == 2 && isFacingEqual){
			
			if ((verts1MatchIndex.get(0)+2)%4 == verts1MatchIndex.get(1) || (verts2MatchIndex.get(0)+2)%4 == verts2MatchIndex.get(1)) {
				return false;//Don't allow matching diagonal vertices
			}
			
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
			if (extendingIn != axis){
				return false;
			}
			if (face1.isUVAnticlockwise() != face2.isUVAnticlockwise()){
				return false;
			}
			//Check the matching point UV vectors
			UV uv1 = face1.uvs[verts1MatchIndex.get(0)];
			UV uv2 = face2.uvs[verts2MatchIndex.get(0)];
			UV uvVec1 = UV.subtract(face1.uvs[verts1MatchIndex.get(1)], uv1);
			UV uvVec2 = UV.subtract(face2.uvs[verts2MatchIndex.get(1)], uv2);
			if (!uvVec1.similar(uvVec2)){
				return false;
			}
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
		return false;
	}
}
