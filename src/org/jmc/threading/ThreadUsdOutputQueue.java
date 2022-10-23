package org.jmc.threading;

import org.jmc.BlockDataPos;
import org.jmc.geom.FaceUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadUsdOutputQueue {
	private final BlockingQueue<ChunkOutput> outputQueue;
	
	public static class ChunkOutput {
		private final Point chunkCoord;
		private final ArrayList<BlockDataPos> usdBlocks;
		private final Map<String, ArrayList<FaceUtils.Face>> usdModels;
		
		public ChunkOutput(Point chunkCoord, ArrayList<BlockDataPos> usdBlocks, Map<String, ArrayList<FaceUtils.Face>> usdModels) {
			this.chunkCoord = chunkCoord;
			this.usdBlocks = usdBlocks;
			this.usdModels = usdModels;
		}
		
		public Point getChunkCoord() {
			return chunkCoord;
		}

		public ArrayList<BlockDataPos> getUsdBlocks() {
			return usdBlocks;
		}
		public Map<String, ArrayList<FaceUtils.Face>> getUsdModels() {
			return usdModels;
		}
	}
	
	public ThreadUsdOutputQueue(int queueSize) {
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
