package org.jmc.threading;

import com.google.gson.*;
import org.jmc.BlockDataPos;
import org.jmc.Options;
import org.jmc.ProgressCallback;
import org.jmc.geom.FaceUtils;
import org.jmc.geom.UV;
import org.jmc.geom.Vertex;
import org.jmc.registry.NamespaceID;
import org.jmc.registry.Registries;
import org.jmc.registry.TextureEntry;
import org.jmc.util.Log;

import java.io.PrintWriter;
import java.util.*;

public class UsdWriterRunnable implements Runnable {
	/**
	 * Offsets of the file. Used to position the chunk in its right location.
	 */
	private double x_offset, y_offset, z_offset;

	private float file_scale;
	
	private ThreadUsdOutputQueue outputQueue;
	
	private PrintWriter usd_writer;
	
	private ProgressCallback progress;
	private int chunksToDo;
	
	Map<String, Integer> blockCounts = new HashMap<>();
	
	public UsdWriterRunnable(ThreadUsdOutputQueue queue, PrintWriter writer, ProgressCallback progress, int chunksToDo) {
		super();
		
		outputQueue = queue;
		usd_writer = writer;
		this.progress = progress;
		this.chunksToDo = chunksToDo;
		
		x_offset = 0;
		y_offset = 0;
		z_offset = 0;
		file_scale = 1.0f;
	}

	@Override
	public void run() {
		ThreadUsdOutputQueue.ChunkOutput chunkOut;
		int chunksDone = 0;
		Map<String, ArrayList<FaceUtils.Face>> models = new HashMap<>();
		while (!Thread.interrupted()) {
			//Check for chunks in queue
			try {
				chunkOut = outputQueue.take();
			} catch (InterruptedException e) {
				Log.debug(String.format("Writer %s interrupted!", Thread.currentThread().getName()));
				break;
			}
			
			
			for (Map.Entry<String, ArrayList<FaceUtils.Face>> entry : chunkOut.getUsdModels().entrySet()) {
				if (!models.containsKey(entry.getKey())) {
					models.put(entry.getKey(), entry.getValue());
				}
			}
			
			for (BlockDataPos block : chunkOut.getUsdBlocks()) {
				writeInstance(block);
			}
			
			chunksDone++;
			if (progress != null) {
				float progValue = (float)chunksDone / (float)chunksToDo;
				progress.setProgress(progValue);
			}
		}
		usd_writer.print("\n" +
				"over \"_models\"\n" +
				"{\n"
		);
		
		for (Map.Entry<String, ArrayList<FaceUtils.Face>> entry : models.entrySet()) {
			writeModel(entry.getKey(), entry.getValue());
		}
		usd_writer.print("}\n");
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
	
	private static class UsdFace {
		int vertCount;
		int[] vertIndices;
		String mat;
		Vertex[] normals;
		UV[] uvs;
		int faceInd;
		UsdFace(int vertCount) {
			this.vertCount = vertCount;
			vertIndices = new int[vertCount];
			normals = new Vertex[vertCount];
			uvs = new UV[vertCount];
		}
	}
	
	private void writeModel(String id, ArrayList<FaceUtils.Face> jmcFaces) {
		LinkedHashMap<Vertex, Integer> points = new LinkedHashMap<>();
		int nextPointIdx = 0;
		int nextFaceIdx = 0;
		ArrayList<UsdFace> usdFaces = new ArrayList<>();
		
		for (FaceUtils.Face face : jmcFaces) {
			UsdFace usdFace = new UsdFace(face.vertices.length);
			TextureEntry te = Registries.getTexture(face.texture);
			Registries.objTextures.add(te);
			usdFace.mat = te.getMatName().replace('-', '_');
			usdFace.vertCount = face.vertices.length;
			for (int i = 0; i < face.vertices.length; i++) {
				Vertex vert = face.vertices[i];
				Integer ptIdx = points.get(vert);
				if (ptIdx != null) {
					usdFace.vertIndices[i] = ptIdx;
				} else {
					points.put(vert, nextPointIdx);
					usdFace.vertIndices[i] = nextPointIdx;
					nextPointIdx++;
				}
			}
			usdFace.normals = face.norms;
			usdFace.uvs = face.uvs;
			usdFace.faceInd = nextFaceIdx++;
			usdFaces.add(usdFace);
		}
		
		String mainMat = "</_materials/unknown>";
		ArrayList<Integer> faceVertCounts = new ArrayList<>(jmcFaces.size());
		ArrayList<Integer> faceVertIndices = new ArrayList<>();
		ArrayList<Vertex> normals = new ArrayList<>();
		ArrayList<UV> uvs = new ArrayList<>();
		Map<String, ArrayList<Integer>> matFaces = new HashMap<>();
		
		for (UsdFace f : usdFaces) {
			faceVertCounts.add(f.vertCount);
			for (int vertInd : f.vertIndices) {
				faceVertIndices.add(vertInd);
			}
			if (f.normals != null) normals.addAll(Arrays.asList(f.normals));
			if (f.uvs != null) uvs.addAll(Arrays.asList(f.uvs));
			mainMat = f.mat;
			ArrayList<Integer> mats = matFaces.computeIfAbsent(f.mat, k -> new ArrayList<>());
			mats.add(f.faceInd);
		}
		
		StringBuilder def = new StringBuilder();
		def.append(		"\n" +
			String.format("over Xform \"%s\"\n", id) +
						"{\n" +
			String.format("\tdef Mesh \"%s\"\n", id) +
						"\t{\n" +
						"\t\tuniform bool doubleSided = 1\n" +
						"\t\tint[] faceVertexCounts = " + faceVertCounts.toString() + "\n" +
						"\t\tint[] faceVertexIndices = " + faceVertIndices.toString() + "\n" +
						//"\t\trel material:binding = </_materials/" + mainMat + ">\n" +
						//"\t\tnormal3f[] normals = " + normals.toString() + " (\n" +
						//"\t\t\tinterpolation = \"faceVarying\"\n" +
						//"\t\t)\n" +
						"\t\tpoint3f[] points = " + points.keySet().toString() + "\n" +
						"\t\ttexCoord2f[] primvars:UVMap = " + uvs.toString() + " (\n" +
						"\t\t\tinterpolation = \"faceVarying\"\n" +
						"\t\t)\n" +
						"\t\tuniform token subdivisionScheme = \"none\"\n");
		for (Map.Entry<String, ArrayList<Integer>> entry : matFaces.entrySet()) {
			def.append(		"\n" +
				String.format("\t\tdef GeomSubset \"%s\"\n", entry.getKey()) +
							"\t\t{\n" +
							"\t\t\tuniform token elementType = \"face\"\n" +
							"\t\t\tuniform token familyName = \"materialBind\"\n" +
							"\t\t\tint[] indices = " + entry.getValue().toString() + "\n" +
							"\t\t\trel material:binding = </_materials/" + entry.getKey() + ">\n" +
							"\t\t}\n");
		}
		def.append(		"\t}\n" +
						"}\n");
		usd_writer.print(def);
	}
	
	private void writeInstance(BlockDataPos block) {
		String id = block.data.toHashedId();
		String modelPath = "</_models/" + id + ">";
		
		int count = blockCounts.getOrDefault(id, 0);
		blockCounts.put(id, count+1);
		
		String def = "\n" +
			String.format("\tdef Xform \"%s_%d\"(\n", id, count) +
						"\t\tinstanceable = true\n" +
						"\t\tinherits = " + modelPath + "\n" +
						"\t)" +
						"\t{\n" +
			String.format("\t\tmatrix4d xformOp:transform = ( (1, 0, 0, 0), (0, 1, 0, 0), (0, 0, 1, 0), (%d, %d, %d, 1) )\n", block.pos.x, block.pos.y, block.pos.z) +
						"\t\tuniform token[] xformOpOrder = [\"xformOp:transform\"]\n" +
						"\t}\n";
		usd_writer.print(def);
	}
}
