package org.jmc;

import org.jmc.Options.OffsetType;
import org.jmc.models.Banner;
import org.jmc.threading.*;
import org.jmc.util.Hilbert.HilbertComparator;
import org.jmc.util.Log;
import org.jmc.util.Messages;

import javax.annotation.CheckForNull;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Handles the export of Minecraft world geometry to an .OBJ file (with matching
 * .MTL file)
 */
public class JsonExporter extends Exporter {
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
		
		File jsonfile = new File(Options.outputDir, Options.objFileName + ".json");

		try {
			jsonfile.createNewFile();
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

			PrintWriter jsonWriter = new PrintWriter(jsonfile, StandardCharsets.UTF_8.name());
			
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
			ThreadJsonOutputQueue outputQueue = new ThreadJsonOutputQueue(Options.exportThreads);

			JsonWriterRunnable writeRunner = new JsonWriterRunnable(outputQueue, jsonWriter, progress, chunksToDo);
			writeRunner.setOffset(oxs, oys, ozs);
			writeRunner.setScale(Options.scale);
			
			jsonWriter.print("{");
			
			Log.info("Processing chunks...");
			
			for (int i = 0; i < Options.exportThreads; i++) {
				Thread thread = new Thread(new JsonReaderRunnable(chunk_buffer, cs, ce, inputQueue, outputQueue));
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
			
			jsonWriter.print("\n}");
			
			Log.debug("Writing File:" + (System.nanoTime() - objTimer2)/1000000000d);
			Log.info("JSON Export Time:" + (System.nanoTime() - objTimer)/1000000000d);
			
			chunk_buffer.removeAllChunks();

			jsonWriter.close();
			
			if (Thread.interrupted())
				return;

			if (progress != null)
				progress.setProgress(1);
			Log.info("Saved blocks to " + jsonfile.getAbsolutePath());
			Log.info("Export Time:" + (System.nanoTime() - exportTimer)/1000000000d);
			Log.info("Done!");
		} catch (InterruptedException e) {
			Log.debug("Export interrupted!");
		} catch (Exception e) {
			Log.error("Error while exporting json:", e);
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
}
