package org.jmc.threading;

import java.awt.Point;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmc.geom.FaceUtils.Face;
import org.jmc.geom.FaceUtils.OBJFace;
import org.jmc.Options;
import org.jmc.ProgressCallback;
import org.jmc.StopCallback;
import org.jmc.UVRecalculate;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.threading.ThreadOutputQueue.ChunkOutput;

public class WriterRunnable implements Runnable {

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
	private float x_offset, y_offset, z_offset;

	private float file_scale;
	
	private ThreadOutputQueue outputQueue;
	
	private PrintWriter obj_writer;
	
	private ProgressCallback progress;
	private StopCallback stop;
	private int chunksToDo;
	
	public WriterRunnable(ThreadOutputQueue queue, PrintWriter writer, ProgressCallback progress, StopCallback stop, int chunksToDo) {
		super();
		
		outputQueue = queue;
		obj_writer = writer;
		this.progress = progress;
		this.stop = stop;
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
		while (true) {
			if (stop != null && stop.stopRequested())
				break;
			//Check for chunks in queue
			try {
				chunkOut = outputQueue.getNext();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			//if none left, kill thread
			if (chunkOut == null) {
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
				obj_writer.println("g chunk_" + chunkCoord.x + "_" + chunkCoord.y);
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
	
	/**
	 * Write texture coordinates. These will be shared by all chunks.
	 * @param out writer of the OBJ file
	 */
	private void appendTextures(PrintWriter out)
	{		
		for (UV uv : exportTexCoords)
		{
			BigDecimal uRound = new BigDecimal(uv.u).setScale(4, RoundingMode.HALF_UP);
			BigDecimal vRound = new BigDecimal(uv.v).setScale(4, RoundingMode.HALF_UP);
			out.print("vt " + uRound.toPlainString() + " " + vRound.toPlainString());
			//out.format((Locale)null, "vt %.4f %.4f", uv.u, uv.v);
			out.println();
		}
	}

	/**
	 * Write normals. These will be shared by all chunks.
	 * @param out writer of the OBJ file
	 */
	private void appendNormals(PrintWriter out)
	{
		for (Vertex norm : exportNormals)
		{
			BigDecimal xRound = new BigDecimal(norm.x).setScale(3, RoundingMode.HALF_UP);
			BigDecimal yRound = new BigDecimal(norm.y).setScale(3, RoundingMode.HALF_UP);
			BigDecimal zRound = new BigDecimal(norm.z).setScale(3, RoundingMode.HALF_UP);
			out.print("vn " + xRound.toPlainString() + " " + yRound.toPlainString() + " " + zRound.toPlainString());
			//out.format((Locale)null, "vn %.3f %.3f %.3f", norm.x, norm.y, norm.z);
			out.println();
		}
	}

	/**
	 * Appends vertices to the file.
	 * @param out
	 */
	private void appendVertices(PrintWriter out)
	{
		for (Vertex vertex : exportVertices)
		{
			float x = (vertex.x + x_offset) * file_scale;
			float y = (vertex.y + y_offset) * file_scale;
			float z = (vertex.z + z_offset) * file_scale;
			BigDecimal xRound = new BigDecimal(x).setScale(3, RoundingMode.HALF_UP);
			BigDecimal yRound = new BigDecimal(y).setScale(3, RoundingMode.HALF_UP);
			BigDecimal zRound = new BigDecimal(z).setScale(3, RoundingMode.HALF_UP);
			out.print("v " + xRound.toPlainString() + " " + yRound.toPlainString() + " " + zRound.toPlainString());
			/*out.format((Locale)null, "v %.3f %.3f %.3f",
					(vertex.x+x_offset)*file_scale,
					(vertex.y+y_offset)*file_scale,
					(vertex.z+z_offset)*file_scale);*/
			out.println();
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
		String last_mtl=null;	
		Long last_obj_idx=Long.valueOf(-1);
		for(OBJFace f:exportFaces)
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
			String mtl = f.material;
			
			if(f.chunk_idx != last_chunk_idx)
			{
				obj_idx_count++;
				last_chunk_idx=f.chunk_idx;
			}
			
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
				vert = verts[i];
		
				if (vertexMap.containsKey(vert))
				{
					face.vertices[i] = vertexMap.get(vert);
				}
				else 
				{
					exportVertices.add(vert);
					vertexMap.put(vert, vertex_counter);
					face.vertices[i] = vertex_counter;
					vertex_counter++;
				}
		
				// add normals
				if (norms != null)
				{
					Vertex norm;
					norm = norms[i];
		
					if (normalsMap.containsKey(norm))
					{
						face.normals[i] = normalsMap.get(norm);
					}
					else
					{
						exportNormals.add(norm);
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
}
