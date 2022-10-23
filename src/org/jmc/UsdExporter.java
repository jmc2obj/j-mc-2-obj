package org.jmc;

import org.jmc.Options.OffsetType;
import org.jmc.registry.Registries;
import org.jmc.registry.TextureEntry;
import org.jmc.threading.*;
import org.jmc.util.Hilbert.HilbertComparator;
import org.jmc.util.Log;
import org.jmc.util.Messages;

import javax.annotation.CheckForNull;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/**
 * Handles the export of Minecraft world geometry to an .OBJ file (with matching
 * .MTL file)
 */
public class UsdExporter extends Exporter {
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
	 */
	@Override
	public void export(@CheckForNull ProgressCallback progress) {
		Log.debug("Exporting world "+Options.worldDir);
		
		File usdfile = new File(Options.outputDir, Options.objFileName + ".usd");

		try {
			usdfile.createNewFile();
		} catch (IOException e) {
			Log.error("Cannot write to the chosen location!", e);
			return;
		}
		
		ArrayList<Thread> threads = new ArrayList<>(Options.exportThreads);
		Thread writeThread = null;
		
		long exportTimer = System.nanoTime();

		try {
			resetErrors();
			
			if (Options.maxX - Options.minX == 0 || Options.maxY - Options.minY == 0
					|| Options.maxZ - Options.minZ == 0) {
				Log.error(Messages.getString("MainPanel.SEL_ERR"), null, true);
				return;
			}

			PrintWriter usdWriter = new PrintWriter(usdfile, StandardCharsets.UTF_8.name());
			
			if (progress != null)
				progress.setMessage(Messages.getString("Progress.OBJ"));

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
			
			int chunksToDo = (ce.x - cs.x + 1) * (ce.y - cs.y + 1);

			ChunkDataBuffer chunk_buffer = new ChunkDataBuffer(Options.minX, Options.maxX, Options.minY,
					Options.maxY, Options.minZ, Options.maxZ);
			
			ThreadInputQueue inputQueue = new ThreadInputQueue();
			ThreadUsdOutputQueue outputQueue = new ThreadUsdOutputQueue(Options.exportThreads);

			UsdWriterRunnable writeRunner = new UsdWriterRunnable(outputQueue, usdWriter, progress, chunksToDo);
			writeRunner.setOffset(oxs, oys, ozs);
			writeRunner.setScale(Options.scale);
			
			usdWriter.println("#usda 1.0\n" +
					"(\n" +
					"    doc = \"jmc2obj\"\n" +
					"    metersPerUnit = 1\n" +
					"    upAxis = \"Y\"\n" +
					")\n");
			
			Log.info("Processing chunks...");
			
			for (int i = 0; i < Options.exportThreads; i++) {
				Thread thread = new Thread(new UsdReaderRunnable(chunk_buffer, cs, ce, inputQueue, outputQueue));
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
			
			Log.debug("Writing Models:" + (System.nanoTime() - objTimer2)/1000000000d);
			
			chunk_buffer.removeAllChunks();
			
			usdWriter.print("def \"_materials\"\n" +
							"{\n");
			if (progress != null)
				progress.setMessage(Messages.getString("Progress.MTL"));
			int count = 0;
			synchronized (Registries.objTextures) {
				for (TextureEntry textureEntry : Registries.objTextures) {
					try {
						String alphaTex = null;
						if (textureEntry.hasAlpha()) {
							if (Options.textureAlpha) {
								alphaTex = textureEntry.getExportFilePathAlpha();
							} else {
								alphaTex = textureEntry.getExportFilePath();
							}
						}
						writeMaterial(usdWriter, textureEntry.getMatName().replace('-', '_'), textureEntry.getAverageColour(), null, textureEntry.getExportFilePath(), alphaTex);
					} catch (IOException e) {
						Log.error("Error writing material definition " + textureEntry.id, e);
					}
					if (progress != null)
						progress.setProgress((float)++count / Registries.objTextures.size());
				}
			}
			usdWriter.print("}\n");

			usdWriter.close();
			Log.info("USD Export Time:" + (System.nanoTime() - objTimer)/1000000000d);
			Log.info("Saved model to " + usdfile.getAbsolutePath());
			
			if (Thread.interrupted())
				return;
			
			if (progress != null)
				progress.setProgress(1);
			
			if (Options.exportTex) {
				Log.info("Exporting textures...");
				synchronized (Registries.objTextures) {
					TextureExporter.exportTextures(Registries.objTextures, progress);
				}
			}

			Log.info("Saved blocks to " + usdfile.getAbsolutePath());
			Log.info("Export Time:" + (System.nanoTime() - exportTimer)/1000000000d);
			Log.info("Done!");
		} catch (InterruptedException e) {
			Log.debug("Export interrupted!");
		} catch (Exception e) {
			Log.error("Error while exporting usd:", e);
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
	
	private static void writeMaterial(PrintWriter out, String matName, Color color, Color spec, @CheckForNull String diffTex, @CheckForNull String alphaTex) {
		if (color == null)
			color = Color.WHITE;
		if (spec == null)
			spec = Color.BLACK;
		
		float[] colorComps = color.getRGBComponents(null);
		float[] specComps = spec.getRGBComponents(null);
		
		out.print(		"\n" +
			String.format("\tdef Material \"%s\"\n", matName) +
						"\t{\n" +
			String.format("\t\ttoken outputs:surface.connect = </_materials/%s/preview/Principled_BSDF.outputs:surface>\n", matName) +
						"\n" +
						"\t\tdef Scope \"preview\"\n" +
						"\t\t{\n" +
						"\t\t\tdef Shader \"Principled_BSDF\"\n" +
						"\t\t\t{\n" +
						"\t\t\t\tuniform token info:id = \"UsdPreviewSurface\"\n");
		if (diffTex != null) {
			out.printf(	"\t\t\t\tfloat3 inputs:diffuseColor.connect = </_materials/%s/preview/Diffuse_Texture.outputs:rgb>\n", matName);
		} else {
			out.printf(	"\t\t\t\tfloat3 inputs:diffuseColor = (%f, %f, %f)\n", colorComps[0], colorComps[1], colorComps[2]);
		}
		out.print(		"\t\t\t\tfloat inputs:metallic = 0\n");
		if (alphaTex == null) {
			out.print(	"\t\t\t\tfloat inputs:opacity = 1\n");
		} else if (alphaTex.equals(diffTex)) {
			out.printf(	"\t\t\t\tfloat inputs:opacity.connect = </_materials/%s/preview/Diffuse_Texture.outputs:r>\n", matName);
		} else {
			out.printf(	"\t\t\t\tfloat inputs:opacity.connect = </_materials/%s/preview/Alpha_Texture.outputs:r>\n", matName);
		}
		out.print(		"\t\t\t\tfloat inputs:roughness = 0.5\n" +
						"\t\t\t\tfloat inputs:specular = 0.5\n" +
						"\t\t\t\ttoken outputs:surface\n" +
						"\t\t\t}\n");
		if (diffTex != null) {
			out.print(	"\n" +
						"\t\t\tdef Shader \"Diffuse_Texture\"\n" +
						"\t\t\t{\n" +
						"\t\t\t\tuniform token info:id = \"UsdUVTexture\"\n");
			out.printf(	"\t\t\t\tasset inputs:file = @%s@\n", diffTex);
			out.printf(	"\t\t\t\tfloat2 inputs:st.connect = </_materials/%s/preview/uvmap.outputs:result>\n", matName);
			if (diffTex.equals(alphaTex))
				out.print("\t\t\t\tfloat outputs:r\n");
			out.print(	"\t\t\t\tfloat3 outputs:rgb\n" +
						"\t\t\t}\n");
		}
		if (diffTex != null || alphaTex != null) {
			out.print(	"\t\t\tdef Shader \"uvmap\"\n" +
						"\t\t\t{\n" +
						"\t\t\t\tuniform token info:id = \"UsdPrimvarReader_float2\"\n" +
						"\t\t\t\ttoken inputs:varname = \"UVMap\"\n" +
						"\t\t\t\tfloat2 outputs:result\n" +
						"\t\t\t}\n");
		}
		if (alphaTex != null && !alphaTex.equals(diffTex)) {
			out.print(	"\n" +
						"\t\t\tdef Shader \"Alpha_Texture\"\n" +
						"\t\t\t{\n" +
						"\t\t\t\tuniform token info:id = \"UsdUVTexture\"\n");
			out.printf(	"\t\t\t\tasset inputs:file = @%s@\n", diffTex);
			out.printf(	"\t\t\t\tfloat2 inputs:st.connect = </_materials/%s/preview/uvmap.outputs:result>\n", matName);
			out.print(	"\t\t\t\tfloat outputs:r" +
						"\t\t\t}\n");
		}
		out.print(		"\t\t}\n" +
						"\t}\n");
	}
}
