package org.jmc.threading;

import com.google.gson.JsonObject;
import org.jmc.BlockDataPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadJsonOutputQueue {
	private final BlockingQueue<ChunkOutput> outputQueue;
	
	public static class ChunkOutput {
		private final Point chunkCoord;
		private final ArrayList<BlockDataPos> jsonBlocks;
		
		public ChunkOutput(Point chunkCoord, ArrayList<BlockDataPos> jsonBlocks) {
			this.chunkCoord = chunkCoord;
			this.jsonBlocks = jsonBlocks;
		}
		
		public Point getChunkCoord() {
			return chunkCoord;
		}

		public ArrayList<BlockDataPos> getJsonBlocks() {
			return jsonBlocks;
		}
	}
	
	public ThreadJsonOutputQueue(int queueSize) {
		outputQueue = new LinkedBlockingQueue<ChunkOutput>(queueSize);
	}
	
	/**
	 * Calls {@link BlockingQueue#put()}
	 * @param outChunk the {@link ChunkOutput chunk} to put in the queue
	 * @throws InterruptedException
	 */
	public void put(ChunkOutput outChunk) throws InterruptedException {
		outputQueue.put(outChunk);
	}
	
	/**
	 * Calls {@link BlockingQueue#take()} and notifies {@link #waitUntilEmpty()}
	 * @throws InterruptedException
	 */
	public ChunkOutput take() throws InterruptedException {
		ChunkOutput outChunk = outputQueue.take();
		synchronized (this) {
			notifyAll();
		}
		return outChunk;
	}
	
	
	/**
	 * Waits on this until take is called and queue is emptied
	 * @throws InterruptedException
	 */
	public synchronized void waitUntilEmpty() throws InterruptedException {
		while (!outputQueue.isEmpty()) {
			wait();
		}
	}
}
