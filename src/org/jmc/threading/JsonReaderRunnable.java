package org.jmc.threading;

import com.google.gson.JsonObject;
import org.jmc.BlockDataPos;
import org.jmc.ChunkDataBuffer;
import org.jmc.util.Log;

import java.awt.*;
import java.util.ArrayList;

public class JsonReaderRunnable implements Runnable {
	private ChunkDataBuffer chunkBuffer;
	private ThreadChunkDeligate chunkDeligate;
	private Point chunkStart;
	private Point chunkEnd;
	private ThreadInputQueue inputQueue;
	private ThreadJsonOutputQueue outputQueue;
	
	public JsonReaderRunnable(ChunkDataBuffer chunk_buffer, Point chunkStart, Point chunkEnd, ThreadInputQueue inQueue, ThreadJsonOutputQueue outQueue) {
		super();
		this.chunkBuffer = chunk_buffer;
		this.chunkDeligate = new ThreadChunkDeligate(chunk_buffer);
		this.chunkStart = chunkStart;
		this.chunkEnd = chunkEnd;
		this.inputQueue = inQueue;
		this.outputQueue = outQueue;
	}

	@Override
	public void run() {
		Point chunkCoord;
		while (!Thread.interrupted()) {
			try {
				chunkCoord = inputQueue.getNext();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			if (chunkCoord == null) {
				break;
			}
			ThreadJsonOutputQueue.ChunkOutput output = exportChunk(chunkCoord);
			if (output == null){
				continue;
			}
			try {
				outputQueue.put(output);
			} catch (InterruptedException e) {
				Log.debug(String.format("Reader %s interrupted!", Thread.currentThread().getName()));
				break;
			}
		}
	}
	
	private ThreadJsonOutputQueue.ChunkOutput exportChunk(Point chunkCoord){
		int chunkX = chunkCoord.x;
		int chunkZ = chunkCoord.y;
		
		chunkDeligate.setCurrentChunk(chunkCoord);

		// export the chunk to the OBJ
		JsonChunkProcessor proc = new JsonChunkProcessor();
		ArrayList<BlockDataPos> objects = proc.process(chunkDeligate, chunkX, chunkZ);
		
		ThreadJsonOutputQueue.ChunkOutput output = new ThreadJsonOutputQueue.ChunkOutput(chunkCoord, objects);
		return output;
	}
}
