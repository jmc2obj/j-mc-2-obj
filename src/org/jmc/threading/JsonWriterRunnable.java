package org.jmc.threading;

import com.google.gson.*;
import org.jmc.BlockDataPos;
import org.jmc.ProgressCallback;
import org.jmc.util.Log;

import java.awt.*;
import java.io.PrintWriter;
import java.util.*;

public class JsonWriterRunnable implements Runnable {
	/**
	 * Offsets of the file. Used to position the chunk in its right location.
	 */
	private double x_offset, y_offset, z_offset;

	private float file_scale;
	
	private ThreadJsonOutputQueue outputQueue;
	
	private PrintWriter obj_writer;
	
	private ProgressCallback progress;
	private int chunksToDo;
	
	private final Gson gson;
	private boolean firstLine;
	
	public JsonWriterRunnable(ThreadJsonOutputQueue queue, PrintWriter writer, ProgressCallback progress, int chunksToDo) {
		super();
		
		outputQueue = queue;
		obj_writer = writer;
		this.progress = progress;
		this.chunksToDo = chunksToDo;
		
		x_offset = 0;
		y_offset = 0;
		z_offset = 0;
		file_scale = 1.0f;
		
		gson = new GsonBuilder().disableHtmlEscaping().create();
	}

	@Override
	public void run() {
		ThreadJsonOutputQueue.ChunkOutput chunkOut;
		int chunksDone = 0;
		firstLine = true;
		while (!Thread.interrupted()) {
			//Check for chunks in queue
			try {
				chunkOut = outputQueue.take();
			} catch (InterruptedException e) {
				Log.debug(String.format("Writer %s interrupted!", Thread.currentThread().getName()));
				break;
			}
			
			ArrayList<BlockDataPos> chunkBlocks = chunkOut.getJsonBlocks();
			if (!chunkBlocks.isEmpty()) {
				Point chunkCoords = chunkOut.getChunkCoord();
				JsonElement elems = addObjects(chunkBlocks);
				if (firstLine) {
					firstLine = false;
				} else {
					obj_writer.print(',');
				}
				obj_writer.printf("\n\"%d,%d\": %s", chunkCoords.x, chunkCoords.y, gson.toJson(elems));
			}
			
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
	
	private JsonElement addObjects(ArrayList<BlockDataPos> chunkBlocks) {
		Map<String, JsonArray> blocks = new HashMap<>();
		for (BlockDataPos block : chunkBlocks) {
			String blockId = block.data.toIdString();
			JsonArray blockArr = blocks.get(blockId);
			if (blockArr == null) {
				blockArr = new JsonArray();
				blocks.put(blockId, blockArr);
			}
			JsonObject elem = new JsonObject();
			elem.addProperty("x", block.pos.x);
			elem.addProperty("y", block.pos.y);
			elem.addProperty("z", block.pos.z);
			elem.addProperty("biome", block.biome.toString());
			blockArr.add(elem);
		}
		return gson.toJsonTree(blocks);
	}
}
