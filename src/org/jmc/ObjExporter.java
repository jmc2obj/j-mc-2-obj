package org.jmc;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.CheckForNull;

import org.jmc.Options.OffsetType;
import org.jmc.geom.Vertex;
import org.jmc.models.Banner;
import org.jmc.registry.Registries;
import org.jmc.threading.ReaderRunnable;
import org.jmc.threading.ThreadInputQueue;
import org.jmc.threading.ThreadOutputQueue;
import org.jmc.threading.WriterRunnable;
import org.jmc.util.Filesystem;
import org.jmc.util.Hilbert.HilbertComparator;
import org.jmc.util.Log;
import org.jmc.util.Messages;

/**
 * Handles the export of Minecraft world geometry to an .OBJ file (with matching
 * .MTL file)
 */
public class ObjExporter {
	/**
	 * Do the export. Export settings are taken from the global Options.
	 * <p>
	 * The overall logic is as follows:
	 * <ul>
	 * <li>Add the geometry to the OBJ, one chunk at a time.
	 * <li>The ChunkDataBuffer holds a collection of chunks in the range of 
	 * x-1..x+1 and z-1..z+1 around the chunk that is being processed. This
	 * is so that neighbouring block information exists for blocks that are at
	 * the edge of the chunk.
	 * <li>By holding only 9 chunks at a time, we can export arbitrarily large
	 * maps in constant memory.
	 * </ul>
	 * 
	 * @param progress
	 *            If not null, the exporter will invoke this callback to inform
	 *            on the operation's progress.
	 * @param writeTex
	 *            Whether to write the textures to the output folder.
	 */
	public static void export(@CheckForNull ProgressCallback progress, boolean writeTex) {
		Log.debug("Exporting world "+Options.worldDir);
		
		File objfile = new File(Options.outputDir, Options.objFileName);
		File mtlfile = new File(Options.outputDir, Options.mtlFileName);
		File tmpdir = new File(Options.outputDir, "temp");

		if (tmpdir.exists()) {
			Log.error("Cannot create directory: " + tmpdir.getAbsolutePath() + "\nSomething is in the way.", null);
			return;
		}

		try {
			objfile.createNewFile();
			mtlfile.createNewFile();
		} catch (IOException e) {
			Log.error("Cannot write to the chosen location!", e);
			return;
		}
		
		ArrayList<Thread> threads = new ArrayList<>(Options.exportThreads);
		Thread writeThread = null;
		
		long exportTimer = System.nanoTime();

		try {
			Registries.objTextures.clear();
			resetErrors();
			
			if (Options.maxX - Options.minX == 0 || Options.maxY - Options.minY == 0
					|| Options.maxZ - Options.minZ == 0) {
				Log.error(Messages.getString("MainPanel.SEL_ERR"), null, true);
				return;
			}

			PrintWriter obj_writer = new PrintWriter(objfile, StandardCharsets.UTF_8.name());
			
			if (progress != null)
				progress.setMessage(Messages.getString("ExportOptions.Progress.OBJ"));

			// Calculate the boundaries of the chunks selected by the user
			Point cs = Chunk.getChunkPos(Options.minX, Options.minZ);
			Point ce = Chunk.getChunkPos(Options.maxX + 15, Options.maxZ + 15);
			int oxs, oys, ozs;

			if (Options.offsetType == OffsetType.CENTER) {
				oxs = -(Options.minX + (Options.maxX - Options.minX) / 2);
				oys = -Options.minY;
				ozs = -(Options.minZ + (Options.maxZ - Options.minZ) / 2);
				Log.info("Center offset: " + oxs + "/" + oys + "/" + ozs);
			} else if (Options.offsetType == OffsetType.CUSTOM) {
				oxs = Options.offsetX;
				oys = 0;
				ozs = Options.offsetZ;
				Log.info("Custom offset: " + oxs + "/" + oys + "/" + ozs);
			} else {
				oxs = 0;
				oys = 0;
				ozs = 0;
			}

			/*if (Options.useUVFile) {
				Log.info("Using file to recalculate UVs: " + Options.UVFile.getAbsolutePath());
				try {
					UVRecalculate.load(Options.UVFile);
				} catch (Exception e) {
					Log.error("Cannot load UV file!", e);
					obj_writer.close();
					return;
				}
			}*/// TODO fix single tex export
			
			int chunksToDo = (ce.x - cs.x + 1) * (ce.y - cs.y + 1);

			ChunkDataBuffer chunk_buffer = new ChunkDataBuffer(Options.minX, Options.maxX, Options.minY,
					Options.maxY, Options.minZ, Options.maxZ);
			
			ThreadInputQueue inputQueue = new ThreadInputQueue();
			ThreadOutputQueue outputQueue = new ThreadOutputQueue(Options.exportThreads);

			WriterRunnable writeRunner = new WriterRunnable(outputQueue, obj_writer, progress, chunksToDo);
			writeRunner.setOffset(oxs, oys, ozs);
			writeRunner.setScale(Options.scale);
			
			writeCommonMcObjHeader(obj_writer, new Vertex(oxs, oys, ozs));

			obj_writer.println("mtllib " + mtlfile.getName());
			obj_writer.println();
			if (!Options.objectPerMaterial && !Options.objectPerBlock && !Options.objectPerChunk){
				obj_writer.println(Options.getObjObject() + " minecraft");
				obj_writer.println();
			}

			/*if (Options.singleMaterial) {
				obj_writer.println("usemtl minecraft_material");
				obj_writer.println();
				writeRunner.setPrintUseMTL(false);
			}*///TODO fix single tex export
			
			Log.info("Processing chunks...");
			
			for (int i = 0; i < Options.exportThreads; i++) {
				Thread thread = new Thread(new ReaderRunnable(chunk_buffer, inputQueue, outputQueue));
				thread.setName("ReadThread-" + i);
				thread.setPriority(Thread.NORM_PRIORITY - 1);
				threads.add(thread);
				thread.start();
			}

			writeThread = new Thread(writeRunner);
			writeThread.setName("WriteThread");
			writeThread.start();
			
			long objTimer = System.nanoTime();
			
			ArrayList<Point> chunkList = new ArrayList<>();
			
			// loop through the chunks selected by the user
			for (int cx = cs.x; cx <= ce.x; cx++) {
				for (int cz = cs.y; cz <= ce.y; cz++) {
					chunkList.add(new Point(cx, cz));
				}
			}
			
			chunkList.sort(new HilbertComparator(Math.max(ce.x - cs.x, ce.y - cs.y)));
			
			for (Point chunk : chunkList) {
				inputQueue.add(chunk);
			}
			
			inputQueue.finish();
			
			long objTimer2 = System.nanoTime();
			
			for (Thread thread : threads){
				thread.join();
			}
			Log.debug("Reading Chunks:" + (System.nanoTime() - objTimer2)/1000000000d);
			objTimer2 = System.nanoTime();
			
			outputQueue.waitUntilEmpty();
			writeThread.interrupt();
			writeThread.join();
			
			Log.debug("Writing File:" + (System.nanoTime() - objTimer2)/1000000000d);
			Log.info("OBJ Export Time:" + (System.nanoTime() - objTimer)/1000000000d);
			
			chunk_buffer.removeAllChunks();

			obj_writer.close();
			
			if (Thread.interrupted())
				return;

			if (progress != null)
				progress.setProgress(1);
			Log.info("Saved model to " + objfile.getAbsolutePath());

			if (!Options.objectPerBlock && (!Options.objectPerChunk || Options.objectPerMaterial)) {
				//mmdanggg2: in maya the obj importer does not recognise the same obj group appearing twice
				//		so if we want to export per chunk, the current sorting will not work in maya.
				Log.info("Sorting OBJ file...");
				if (progress != null)
					progress.setMessage(Messages.getString("ExportOptions.Progress.OBJ_SORT"));

				if (!tmpdir.mkdir()) {
					Log.error("Cannot temp create directory: " + tmpdir.getAbsolutePath(), null);
					return;
				}

				File mainfile = new File(tmpdir, "main");
				PrintWriter main = new PrintWriter(mainfile, StandardCharsets.UTF_8.name());
				File vertexfile = new File(tmpdir, "vertex");
				PrintWriter vertex = new PrintWriter(vertexfile, StandardCharsets.UTF_8.name());
				File normalfile = new File(tmpdir, "normal");
				PrintWriter normal = new PrintWriter(normalfile, StandardCharsets.UTF_8.name());
				File uvfile = new File(tmpdir, "uv");
				PrintWriter uv = new PrintWriter(uvfile, StandardCharsets.UTF_8.name());

				BufferedReader objin = Files.newBufferedReader(objfile.toPath(), StandardCharsets.UTF_8);

				Map<String, FaceFile> faces = new HashMap<>();
				int facefilecount = 1;

				FaceFile current_ff = null;
				String current_o = Options.getObjObject() + " default";

				int maxcount = (int) objfile.length();
				if (maxcount == 0)
					maxcount = 1;
				int count = 0;

				String line;
				while ((line = objin.readLine()) != null) {
					if (line.length() == 0)
						continue;

					count += line.length() + 1;
					if (count > maxcount)
						count = maxcount;

					if (progress != null)
						progress.setProgress(0.5f * (float) count / (float) maxcount);

					if (line.startsWith("usemtl ")) {
						line = line.substring(7).trim();

						if (!faces.containsKey(line)) {
							current_ff = new FaceFile();
							current_ff.name = line;
							current_ff.file = new File(tmpdir, "" + facefilecount);
							facefilecount++;
							current_ff.writer = new PrintWriter(current_ff.file, StandardCharsets.UTF_8.name());
							faces.put(line, current_ff);
						} else
							current_ff = faces.get(line);

						if (Options.objectPerChunk) {
							current_ff.writer.println();
							current_ff.writer.println(current_o);
							current_ff.writer.println();
						}
					} else if (line.startsWith("f ")) {
						if (current_ff != null) {
							current_ff.writer.println(line);
						}
					} else if (line.startsWith("v ")) {
						vertex.println(line);
					} else if (line.startsWith("vn ")) {
						normal.println(line);
					} else if (line.startsWith("vt ")) {
						uv.println(line);
					} else if (line.startsWith(Options.getObjObject() + " ")) {
						current_o = line;
					} else {
						main.println(line);
						if (line.startsWith("mtllib"))
							main.println();
					}
				}

				objin.close();

				vertex.close();
				normal.close();
				uv.close();

				/*if (Options.singleMaterial) {
					main.println("usemtl minecraft_material");
					main.println();
				}*///TODO fix single tex export

				BufferedReader norm_reader = Files.newBufferedReader(normalfile.toPath(), StandardCharsets.UTF_8);
				while ((line = norm_reader.readLine()) != null)
					main.println(line);
				norm_reader.close();
				normalfile.delete();

				BufferedReader uv_reader = Files.newBufferedReader(uvfile.toPath(), StandardCharsets.UTF_8);
				while ((line = uv_reader.readLine()) != null)
					main.println(line);
				uv_reader.close();
				uvfile.delete();

				BufferedReader vertex_reader = Files.newBufferedReader(vertexfile.toPath(), StandardCharsets.UTF_8);
				while ((line = vertex_reader.readLine()) != null)
					main.println(line);
				vertex_reader.close();
				vertexfile.delete();

				count = 0;
				maxcount = faces.size();

				for (FaceFile ff : faces.values()) {
					String current_mat = ff.name;

					ff.writer.close();

					count++;
					if (progress != null)
						progress.setProgress(0.5f + 0.5f * (float) count / (float) maxcount);

					vertex.println();
					if (Options.objectPerMaterial && !Options.objectPerChunk)
						main.println(Options.getObjObject() + " " + ff.name);
					main.println();

					/*if (!Options.singleMaterial)TODO fix single tex export*/ {
						main.println("usemtl " + ff.name);
						main.println();
					}

					BufferedReader reader = Files.newBufferedReader(ff.file.toPath(), StandardCharsets.UTF_8);
					while ((line = reader.readLine()) != null) {
						if (Options.objectPerChunk && line.startsWith(Options.getObjObject() + " ")) {
							if (Options.objectPerMaterial)
								main.println(line + "_" + current_mat);
							else
								main.println(line);
						} else
							main.println(line);
					}
					reader.close();

					ff.file.delete();
				}

				main.close();

				Filesystem.moveFile(mainfile, objfile);

				if (progress != null)
					progress.setProgress(1);

				if (!tmpdir.delete())
					Log.error("Failed to erase temp dir: " + tmpdir.getAbsolutePath()
							+ "\nPlease remove it yourself!", null);
			}
			
			Log.info(String.format("Writing materials to %s...", mtlfile.getAbsolutePath()));
			Materials.writeMTLFile(mtlfile, progress);
			
			if (writeTex) {
				Log.info("Exporting textures...");
				synchronized (Registries.objTextures) {
					/*if (Options.textureMerge) {
						Log.error("Texture merging is not supported!", null);
						TextureExporter.mergeTextures(Registries.objTextures, progress);
					} else {
						TextureExporter.exportTextures(Registries.objTextures, progress);
					//}*/// TODO fix single tex export
					TextureExporter.exportTextures(Registries.objTextures, progress);
				}
			}
			Log.info("Export Time:" + (System.nanoTime() - exportTimer)/1000000000d);
			Log.info("Done!");
		} catch (InterruptedException e) {
			Log.debug("Export interrupted!");
		} catch (Exception e) {
			Log.error("Error while exporting OBJ:", e);
		} finally {
			for (Thread t : threads) {
				t.interrupt();
			}
			if (writeThread != null) {
				writeThread.interrupt();
			}
			System.gc();
		}
	}
	
	/**
	 * Add CommonMCOBJ Header.
	 * <p>
	 * CommonMCOBJ is a common standard for exporting metadata
	 * in OBJs.
	 * @param objWriter
	 *      The writer that's writing the OBJ file
	 */
	private static void writeCommonMcObjHeader(PrintWriter objWriter, Vertex offsetVec) {
		objWriter.println("# COMMON_MC_OBJ_START");
		objWriter.println("# version: 1");
		objWriter.println("# exporter: jmc2obj");  // Name of the exporter, all lowercase, with spaces substituted by underscores
		objWriter.println("# world_name: " + Options.worldDir.getName());  // Name of the source world
		objWriter.println("# world_path: " + Options.worldDir.toString());  // Path of the source world
		objWriter.println("# export_bounds_min: " +  String.format("(%d, %d, %d)", Options.minX, Options.minY, Options.minZ));  // The lowest block coordinate exported in the obj file
		objWriter.println("# export_bounds_max: " + String.format("(%d, %d, %d)", Options.maxX-1, Options.maxY-1, Options.maxZ-1));  // The highest block coordinate exported in the obj file
		objWriter.println("# export_offset: " + String.format(Locale.US, "(%f, %f, %f)", offsetVec.x, offsetVec.y, offsetVec.z)); // The offset vector the model was exported with
		objWriter.println("# block_scale: " + String.format(Locale.US, "%f", Options.scale)); // Scale of each block
		objWriter.println("# block_origin_offset: (-0.5, -0.5, -0.5)"); // The offset vector of the block model origins
		objWriter.println("# z_up: false");  // true if the Z axis is up instead of Y, false is not
		objWriter.println("# texture_type: " + (Options.singleMaterial ? "ATLAS" : "INDIVIDUAL_TILES"));  // ATLAS or INDIVIDUAL_TILES
		objWriter.println("# has_split_blocks: " + (Options.objectPerMaterial ? "true" : "false"));  // true if blocks have been split, false if not
		objWriter.println("# COMMON_MC_OBJ_END");
		objWriter.println();
	}
	
	private static void resetErrors() {
		Banner.resetReadError();
		Log.resetSingles();
	}
}

/**
 * Little helper class for the map used in sorting.
 * 
 * @author danijel
 * 
 */
class FaceFile {
	public String name;
	public File file;
	public PrintWriter writer;
}
