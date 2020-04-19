package org.jmc;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.jmc.Options.OffsetType;
import org.jmc.models.Banner;
import org.jmc.threading.ReaderRunnable;
import org.jmc.threading.ThreadInputQueue;
import org.jmc.threading.ThreadOutputQueue;
import org.jmc.threading.WriterRunnable;
import org.jmc.util.Filesystem;
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
	 * @param stop
	 *            If not null, the exporter will poll this callback to check if
	 *            the operation should be cancelled.
	 * @param writeObj
	 *            Whether to write the .obj file
	 * @param writeMtl
	 *            Whether to write the .mtl file
	 */
	public static void export(ProgressCallback progress, StopCallback stop, boolean writeObj, boolean writeMtl) {
		File objfile = new File(Options.outputDir, Options.objFileName);
		File mtlfile = new File(Options.outputDir, Options.mtlFileName);
		File tmpdir = new File(Options.outputDir, "temp");

		if (tmpdir.exists()) {
			Log.error("Cannot create directory: " + tmpdir.getAbsolutePath() + "\nSomething is in the way.", null);
			return;
		}

		try {
			if (writeObj)
				objfile.createNewFile();

			if (writeMtl)
				mtlfile.createNewFile();
		} catch (IOException e) {
			Log.error("Cannot write to the chosen location!", e);
			return;
		}

		try {
			if (writeObj) {
				if (Options.maxX - Options.minX == 0 || Options.maxY - Options.minY == 0
						|| Options.maxZ - Options.minZ == 0) {
					Log.error(Messages.getString("MainPanel.SEL_ERR"), null, true);
					return;
				}

				PrintWriter obj_writer = new PrintWriter(new FileWriter(objfile));

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

				if (Options.useUVFile) {
					Log.info("Using file to recalculate UVs: " + Options.UVFile.getAbsolutePath());
					try {
						UVRecalculate.load(Options.UVFile);
					} catch (Exception e) {
						Log.error("Cannot load UV file!", e);
						obj_writer.close();
						return;
					}
				}
				
				int chunksToDo = (ce.x - cs.x + 1) * (ce.y - cs.y + 1);

				ChunkDataBuffer chunk_buffer = new ChunkDataBuffer(Options.minX, Options.maxX, Options.minY,
						Options.maxY, Options.minZ, Options.maxZ);
				
				ThreadInputQueue inputQueue = new ThreadInputQueue();
				ThreadOutputQueue outputQueue = new ThreadOutputQueue();

				WriterRunnable writeRunner = new WriterRunnable(outputQueue, obj_writer, progress, stop, chunksToDo);
				writeRunner.setOffset(oxs, oys, ozs);
				writeRunner.setScale(Options.scale);

				obj_writer.println("mtllib " + mtlfile.getName());
				obj_writer.println();
				if (!Options.objectPerMaterial && !Options.objectPerBlock && !Options.objectPerChunk){
					obj_writer.println("g minecraft");
					obj_writer.println();
				}

				if (Options.singleMaterial) {
					obj_writer.println("usemtl minecraft_material");
					obj_writer.println();

					if (Options.objectPerBlock)
						writeRunner.setPrintUseMTL(false);
				}
				
				Banner.resetReadError();
				
				Log.info("Processing chunks...");
				
				Thread[] threads = new Thread[Options.exportThreads];
				for (int i = 0; i < Options.exportThreads; i++){
					threads[i] = new Thread(new ReaderRunnable(chunk_buffer, cs, ce, inputQueue, outputQueue, stop));
					threads[i].setName("ReadThread-" + i);
					threads[i].setPriority(Thread.NORM_PRIORITY - 1);
					threads[i].start();
				}

				Thread writeThread = new Thread(writeRunner);
				writeThread.setName("WriteThread");
				writeThread.start();
				
				long timer = System.nanoTime();
				long timer2 = System.nanoTime();

				// loop through the chunks selected by the user
				for (int cx = cs.x; cx <= ce.x; cx++) {
					for (int cz = cs.y; cz <= ce.y; cz++) {
						inputQueue.add(cx, cz);
					}
				}
				
				inputQueue.finish();
				
				timer = System.nanoTime();
				
				for (Thread thread : threads){
					thread.join();
				}
				Log.debug("Reading Chunks:" + (System.nanoTime() - timer)/1000000000d);
				timer = System.nanoTime();
				
				outputQueue.finish();
				writeThread.join();
				
				Log.debug("Writing File:" + (System.nanoTime() - timer)/1000000000d);
				Log.info("Time:" + (System.nanoTime() - timer2)/1000000000d);
				
				chunk_buffer.removeAllChunks();

				obj_writer.close();
				
				if (stop != null && stop.stopRequested())
					return;

				if (progress != null)
					progress.setProgress(1);
				Log.info("Saved model to " + objfile.getAbsolutePath());

				if (!Options.objectPerBlock && !Options.objectPerChunk) {
					//mmdanggg2: in maya the obj importer does not recognise the same obj group appearing twice
					//		so if we want to export per chunk, the current sorting will not work in maya.
					Log.info("Sorting OBJ file...");

					if (!tmpdir.mkdir()) {
						Log.error("Cannot temp create directory: " + tmpdir.getAbsolutePath(), null);
						return;
					}

					File mainfile = new File(tmpdir, "main");
					PrintWriter main = new PrintWriter(mainfile);
					File vertexfile = new File(tmpdir, "vertex");
					PrintWriter vertex = new PrintWriter(vertexfile);
					File normalfile = new File(tmpdir, "normal");
					PrintWriter normal = new PrintWriter(normalfile);
					File uvfile = new File(tmpdir, "uv");
					PrintWriter uv = new PrintWriter(uvfile);

					BufferedReader objin = new BufferedReader(new FileReader(objfile));

					Map<String, FaceFile> faces = new HashMap<String, FaceFile>();
					int facefilecount = 1;

					FaceFile current_ff = null;
					String current_g = "g default";

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
								current_ff.writer = new PrintWriter(current_ff.file);
								faces.put(line, current_ff);
							} else
								current_ff = faces.get(line);

							if (Options.objectPerChunk) {
								current_ff.writer.println();
								current_ff.writer.println(current_g);
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
						} else if (line.startsWith("g ")) {
							current_g = line;
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

					if (Options.singleMaterial) {
						main.println("usemtl minecraft_material");
						main.println();
					}

					BufferedReader norm_reader = new BufferedReader(new FileReader(normalfile));
					while ((line = norm_reader.readLine()) != null)
						main.println(line);
					norm_reader.close();
					normalfile.delete();

					BufferedReader uv_reader = new BufferedReader(new FileReader(uvfile));
					while ((line = uv_reader.readLine()) != null)
						main.println(line);
					uv_reader.close();
					uvfile.delete();

					BufferedReader vertex_reader = new BufferedReader(new FileReader(vertexfile));
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
							main.println("g " + ff.name);
						main.println();

						if (!Options.singleMaterial) {
							main.println("usemtl " + ff.name);
							main.println();
						}

						BufferedReader reader = new BufferedReader(new FileReader(ff.file));
						while ((line = reader.readLine()) != null) {
							if (Options.objectPerChunk && line.startsWith("g ")) {
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
			}
			
			if (writeMtl) {
				Materials.copyMTLFile(mtlfile);
				Log.info("Saved materials to " + mtlfile.getAbsolutePath());
			}

			Log.info("Done!");

		} catch (Exception e) {
			Log.error("Error while exporting OBJ:", e);
		}
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
};
