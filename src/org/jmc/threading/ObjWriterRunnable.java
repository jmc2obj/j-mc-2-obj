package org.jmc.threading;

import java.awt.Point;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmc.geom.FaceUtils.Face;
import org.jmc.geom.FaceUtils.OBJFace;
import org.jmc.Options;
import org.jmc.ProgressCallback;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.registry.Registries;
import org.jmc.registry.TextureEntry;
import org.jmc.threading.ThreadObjOutputQueue.ChunkOutput;
import org.jmc.util.Log;

public class ObjWriterRunnable implements Runnable {

	/**
	 * List of vertices currently being exported.
	 */
	private List<Vertex> exportVertices;
	/**
	 * List of texture coordinates in the file
	 */
	private List<UV> exportTexCoords;
	/**
	 * List of normals
	 */
	private List<Vertex> exportNormals;
	/**
	 * List of faces
	 */
	private List<OBJFace> exportFaces;
	
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

	private int vertex_counter, tex_counter, norm_counter;
	
	private long obj_idx_count;

	/**
	 * Decides whether to print "usemtl" lines in OBJ file
	 */
	private boolean print_usemtl;

	/**
	 * Offsets of the file. Used to position the chunk in its right location.
	 */
	private double x_offset, y_offset, z_offset;

	private float file_scale;
	
	private ThreadObjOutputQueue outputQueue;
	
	private PrintWriter obj_writer;
	
	private ProgressCallback progress;
	private int chunksToDo;
	
	public ObjWriterRunnable(ThreadObjOutputQueue queue, PrintWriter writer, ProgressCallback progress, int chunksToDo) {
		super();
		
		outputQueue = queue;
		obj_writer = writer;
		this.progress = progress;
		this.chunksToDo = chunksToDo;
		
		x_offset = 0;
		y_offset = 0;
		z_offset = 0;
		file_scale = 1.0f;
		print_usemtl=true;
		
		obj_idx_count = -1;
		vertexMap = new HashMap<Vertex, Integer>();
		vertex_counter = 1;
		texCoordMap = new HashMap<UV, Integer>();
		tex_counter = 1;
		normalsMap = new HashMap<Vertex, Integer>();
		norm_counter = 1;
		
		exportVertices = new ArrayList<Vertex>();
		exportTexCoords = new ArrayList<UV>();
		exportNormals = new ArrayList<Vertex>();
		exportFaces = new ArrayList<OBJFace>();
	}

	@Override
	public void run() {
		ChunkOutput chunkOut;
		int chunksDone = 0;
		while (!Thread.interrupted()) {
			//Check for chunks in queue
			try {
				chunkOut = outputQueue.take();
			} catch (InterruptedException e) {
				Log.debug(String.format("Writer %s interrupted!", Thread.currentThread().getName()));
				break;
			}
			
			Point chunkCoord = chunkOut.getChunkCoord();
			ArrayList<Face> chunkFaces = chunkOut.getFaces();
			
			addOBJFaces(chunkFaces);
			
			// export the chunk to the OBJ
			appendTextures(obj_writer);
			appendNormals(obj_writer);
			appendVertices(obj_writer);
			if (Options.objectPerChunk && !Options.objectPerBlock && chunkCoord != null)
				obj_writer.println("o chunk_" + chunkCoord.x + "_" + chunkCoord.y);
			appendFaces(obj_writer);
			clearData();
			
			chunksDone++;
			if (progress != null) {
				float progValue = (float)chunksDone / (float)chunksToDo;
				progress.setProgress(progValue);
			}
		}
	}

	/**
	 * Offset all the vertices by these amounts.
	 * Used to position the chunk in its right location.
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 */
	public void setOffset(double x, double y, double z)
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
	
	/**
	 * Write texture coordinates. These will be shared by all chunks.
	 * @param out writer of the OBJ file
	 */
	private void appendTextures(PrintWriter out) {
		for (UV uv : exportTexCoords) {
			out.println("vt " + formatDouble(uv.u, 9) + " " + formatDouble(uv.v, 9));
		}
	}

	/**
	 * Write normals. These will be shared by all chunks.
	 * @param out writer of the OBJ file
	 */
	private void appendNormals(PrintWriter out) {
		for (Vertex norm : exportNormals) {
			out.println("vn " + formatDouble(norm.x, 3) + " " + formatDouble(norm.y, 3) + " " + formatDouble(norm.z, 3));
		}
	}

	/**
	 * Appends vertices to the file.
	 * @param out
	 */
	private void appendVertices(PrintWriter out) {
		for (Vertex vertex : exportVertices) {
			double x = (vertex.x + x_offset) * file_scale;
			double y = (vertex.y + y_offset) * file_scale;
			double z = (vertex.z + z_offset) * file_scale;
			out.println("v " + formatDouble(x, 3) + " " + formatDouble(y, 3) + " " + formatDouble(z, 3));
		}
	}

	/**
	 * This method prints faces from the current buffer to an OBJ format.
	 * 
	 * @param out file to append the data
	 */
	private void appendFaces(PrintWriter out)
	{		
		Collections.sort(exportFaces);
		NamespaceID last_mtl=null;	
		Long last_obj_idx=Long.valueOf(-1);
		for(OBJFace f:exportFaces)
		{
			if(!f.tex.equals(last_mtl) && print_usemtl)
			{
				TextureEntry te = Registries.getTexture(f.tex);
				Registries.objTextures.add(te);
				out.println();
				out.println("usemtl "+te.getMatName());
				last_mtl=f.tex;
			}
			
			if(!f.obj_idx.equals(last_obj_idx))
			{
				out.println("o o"+f.obj_idx);
				last_obj_idx=f.obj_idx;
			}

			out.print("f");
			for (int i = 0; i < f.vertices.length; i++)
			{
				
				if (f.normals != null && f.uv != null)
					out.print(" " + f.vertices[i] + "/" + f.uv[i] + "/" + f.normals[i]);
					//out.format((Locale)null, " %d/%d/%d", f.vertices[i], f.uv[i], f.normals[i]);
				else if (f.normals == null && f.uv != null)
					out.print(" " + f.vertices[i] + "/" + f.uv[i]);
					//out.format((Locale)null, " %d/%d", f.vertices[i], f.uv[i]);
				else if (f.normals != null && f.uv == null)
					out.print(" " + f.vertices[i] + "//" + f.normals[i]);
					//out.format((Locale)null, " %d//%d", f.vertices[i], f.normals[i]);
				else
					out.print(" " + f.vertices[i]);
					//out.format((Locale)null, " %d", f.vertices[i]);
			}
			out.println();
		}
	}
	
	private void addOBJFaces(ArrayList<Face> chunkFaces)
	{
		int last_chunk_idx=-1;
		for (Face f : chunkFaces) {
			Vertex[] verts = f.vertices;
			Vertex[] norms = f.norms;
			UV[] uv = f.uvs;
			NamespaceID tex = f.texture;
			
			if(f.chunk_idx != last_chunk_idx)
			{
				obj_idx_count++;
				last_chunk_idx=f.chunk_idx;
			}
			
			OBJFace face = new OBJFace(verts.length);
			face.obj_idx=Long.valueOf(obj_idx_count);
			face.tex = tex;
			if (norms == null) face.normals = null;
			if (uv == null) 
			{
				face.uv = null;
			}
			else if(Options.textureMerge)
			{
				//uv=UVRecalculate.recalculate(uv, tex); TODO fix single tex export
			}
		
			for (int i = 0; i < verts.length; i++)
			{
				// add vertices
				Vertex vert;
				vert = verts[i];
				
				Integer vertId = vertexMap.get(vert); 
				if (vertId != null) {
					face.vertices[i] = vertId;
				} else  {
					exportVertices.add(vert);
					vertexMap.put(vert, vertex_counter);
					face.vertices[i] = vertex_counter;
					vertex_counter++;
				}
		
				// add normals
				if (norms != null) {
					Vertex norm;
					norm = norms[i];
					
					Integer normId = normalsMap.get(norm);
					if (normId != null) {
						face.normals[i] = normId;
					} else {
						exportNormals.add(norm);
						normalsMap.put(norm, norm_counter);
						face.normals[i] = norm_counter;
						norm_counter++;
					}
				}
		
				// add texture coords
				if (uv != null) {
					Integer uvId = texCoordMap.get(uv[i]);
					if (uvId != null)	 {
						face.uv[i] = uvId;
					} else {
						exportTexCoords.add(uv[i]);
						texCoordMap.put(uv[i], tex_counter);
						face.uv[i] = tex_counter;
						tex_counter++;
					}
				}
			}
		
			exportFaces.add(face);
		}
	}
	
	private void clearData() {
		if(Options.removeDuplicates)
		{
			//keep edge vertices
			for(Vertex v:exportVertices)
				if((v.x-0.5)%16!=0 && (v.z-0.5)%16!=0 && (v.x+0.5)%16!=0 && (v.z+0.5)%16!=0)
					vertexMap.remove(v);
		}
		else
		{
			vertexMap.clear();
		}
		exportVertices.clear();
		exportTexCoords.clear();
		exportNormals.clear();
		exportFaces.clear();
	}
	
	// fast double format from https://stackoverflow.com/a/10554128/5233018
	private static final int[] POW10 = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};
	
	public static String formatDouble(double val, int precision) {
		StringBuilder sb = new StringBuilder();
		if (val < 0) {
			sb.append('-');
			val = -val;
		}
		int exp = POW10[precision];
		long lval = (long)(val * exp + 0.5);
		sb.append(lval / exp).append('.');
		long fval = lval % exp;
		for (int p = precision - 1; p > 0 && fval < POW10[p]; p--) {
			sb.append('0');
		}
		sb.append(fval);
		return sb.toString();
	}
}
